package com.example.Paws_Backend.service;

import com.example.Paws_Backend.dto.ItemsNeedingApprovalDTO;
import com.example.Paws_Backend.model.*;
import com.example.Paws_Backend.repository.PurchaseOrderRepository;
import com.example.Paws_Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PetService petService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public PurchaseOrder createOrder(PurchaseOrder order, User user) {
        if (!"consumer".equalsIgnoreCase(user.getUserRole())) {
            throw new IllegalArgumentException("Only users with the role 'consumer' can create orders.");
        }

        if (order.getProduct() == null && order.getPet() == null) {
            throw new IllegalArgumentException("Order must contain either a product or a pet.");
        }

        order.setUser(user);
        order.setSellerApprovalStatuses(Map.of(
                order.getProduct() != null ? order.getProduct().getSeller().getId() : order.getPet().getSeller().getId(), false
        ));

        return purchaseOrderRepository.save(order);
    }

    @Override
    public List<PurchaseOrder> getApprovedOrdersByUser(Long userId) {
        return purchaseOrderRepository.findAll().stream()
                .filter(order -> order.getUser().getId().equals(userId))
                .filter(order -> !order.isOrderCanceled())
                .filter(order -> order.isOrderConfirmed())
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrder> getOrdersNotYetApprovedBySeller(Long userId) {
        return purchaseOrderRepository.findAll().stream()
                .filter(order -> !order.isOrderCanceled()
                        && !order.getSellerApprovalStatuses().getOrDefault(userId, false))
                .collect(Collectors.toList());
    }

    @Override
    public boolean confirmOrder(Long orderId) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with ID " + orderId + " does not exist."));

        if (order.isOrderConfirmed()) {
            return false;
        }

        if (order.getSellerApprovalStatuses().values().stream().allMatch(Boolean::booleanValue)) {
            order.setOrderedTime(LocalDateTime.now());
            order.setOrderConfirmed(true);
            order.generateOtp();
            purchaseOrderRepository.save(order);
            return true;
        }
        return false;
    }

    @Override
    public ItemsNeedingApprovalDTO getProductsNeedingApproval(Long sellerId) {
        List<PurchaseOrder> orders = purchaseOrderRepository.findAll();

        List<Product> productsNeedingApproval = orders.stream()
                .filter(order -> !order.isOrderCanceled()
                        && order.getProduct() != null
                        && order.getProduct().getSeller().getId().equals(sellerId)
                        && !order.getSellerApprovalStatuses().getOrDefault(sellerId, false))
                .map(PurchaseOrder::getProduct)
                .distinct()
                .collect(Collectors.toList());

        List<Pet> petsNeedingApproval = orders.stream()
                .filter(order -> !order.isOrderCanceled()
                        && order.getPet() != null
                        && order.getPet().getSeller().getId().equals(sellerId)
                        && !order.getSellerApprovalStatuses().getOrDefault(sellerId, false))
                .map(PurchaseOrder::getPet)
                .distinct()
                .collect(Collectors.toList());

        return new ItemsNeedingApprovalDTO(productsNeedingApproval, petsNeedingApproval);
    }

    @Override
    public void rejectOrder(Long orderId, Long userId) throws AccessDeniedException {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with ID " + orderId + " does not exist."));

        if (order.isOrderCanceled()) {
            throw new IllegalStateException("Order has already been canceled and cannot be rejected.");
        }

        if (order.getSellerApprovalStatuses().containsKey(userId)) {
            order.getSellerApprovalStatuses().put(userId, false);
            purchaseOrderRepository.save(order);
        } else {
            throw new AccessDeniedException("User does not have permission to reject this order.");
        }
    }
//
    @Override
    public List<PurchaseOrder> getApprovedOrdersBySeller(Long sellerId) {
        return purchaseOrderRepository.findAll().stream()
                .filter(order -> order.getSellerApprovalStatuses().containsKey(sellerId)
                        && order.getSellerApprovalStatuses().get(sellerId)
                        && order.isOrderConfirmed()
                        && !order.isOrderCanceled())
                .collect(Collectors.toList());
    }

    @Override
    public void handleItemApproval(Long orderId, Long itemId, Long userId, boolean approve,
                                   LocalDateTime shipmentTime, LocalDateTime approxDeliveryTime,
                                   LocalDateTime maxDeliveryTime, Double deliveryCost)
            throws AccessDeniedException {

        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with ID " + orderId + " does not exist."));

        boolean itemMatchesOrder = (order.getProduct() != null && order.getProduct().getId().equals(itemId)) ||
                (order.getPet() != null && order.getPet().getId().equals(itemId));

        if (!itemMatchesOrder) {
            throw new IllegalArgumentException("Item with ID " + itemId + " is not associated with this order.");
        }

        boolean isSeller = (order.getProduct() != null && order.getProduct().getSeller().getId().equals(userId)) ||
                (order.getPet() != null && order.getPet().getSeller().getId().equals(userId));

        if (!isSeller) {
            throw new AccessDeniedException("User does not have permission to handle this item.");
        }

        order.getSellerApprovalStatuses().put(userId, approve);

        if (approve) {
            order.setShipmentTime(shipmentTime);
            order.setApproxDeliveryTime(approxDeliveryTime);
            order.setMaxDeliveryTime(maxDeliveryTime);
            order.setDeliveryCost(deliveryCost);
        }

        purchaseOrderRepository.save(order);
    }

    @Override
    public void cancelOrder(Long orderId, Long userId) throws AccessDeniedException {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with ID " + orderId + " does not exist."));

        boolean isSeller = order.getSellerApprovalStatuses().containsKey(userId);

        if (!order.getUser().getId().equals(userId) && !isSeller) {
            throw new AccessDeniedException("User does not have permission to cancel this order.");
        }

        LocalDateTime now = LocalDateTime.now();
        boolean isPastMaxDeliveryTime = order.getMaxDeliveryTime() != null && now.isAfter(order.getMaxDeliveryTime());

        if (isPastMaxDeliveryTime) {
            order.setCancellationFee(0.0);
        } else if (order.getShipmentTime() != null && now.isAfter(order.getShipmentTime())) {
            double deduction = order.getTotalOrderValue() * 0.30;
            order.setCancellationFee(deduction);
        } else {
            order.setCancellationFee(0.0);
        }

        order.setOrderCanceled(true);
        purchaseOrderRepository.save(order);
    }

    @Override
    public List<PurchaseOrder> getConfirmedOrdersByUser(Long userId) {
        return purchaseOrderRepository.findAll().stream()
                .filter(order -> order.getUser().getId().equals(userId)
                        && order.isOrderConfirmed()
                        && !order.isOrderCanceled())
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrder> getCanceledOrdersByUser(Long userId) {
        return purchaseOrderRepository.findAll().stream()
                .filter(order -> order.getUser().getId().equals(userId)
                        && order.isOrderCanceled())
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrder> getConfirmedOrdersBySeller(Long sellerId) {
        return purchaseOrderRepository.findAll().stream()
                .filter(order -> order.getSellerApprovalStatuses().containsKey(sellerId)
                        && order.getSellerApprovalStatuses().get(sellerId)
                        && order.isOrderConfirmed()
                        && !order.isOrderCanceled())
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrder> getCanceledOrdersBySeller(Long sellerId) {
        return purchaseOrderRepository.findAll().stream()
                .filter(order -> order.getSellerApprovalStatuses().containsKey(sellerId)
                        && order.getSellerApprovalStatuses().get(sellerId)
                        && order.isOrderCanceled())
                .collect(Collectors.toList());
    }

    @Override
    public void completeOrder(Long orderId, Long sellerId, String otp) throws AccessDeniedException {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with ID " + orderId + " does not exist."));

        if (!order.getSellerApprovalStatuses().containsKey(sellerId)) {
            throw new AccessDeniedException("User does not have permission to complete this order.");
        }

        if (order.isOrderCompleted()) {
            throw new IllegalStateException("Order has already been completed.");
        }

        if (!order.getOtp().equals(otp)) {
            throw new IllegalArgumentException("Invalid OTP provided.");
        }

        order.setOrderCompleted(true);
        purchaseOrderRepository.save(order);
    }
}
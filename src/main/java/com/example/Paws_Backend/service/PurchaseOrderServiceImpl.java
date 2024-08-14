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
        order.setOrderStatus(OrderStatus.PENDING);

        return purchaseOrderRepository.save(order);
    }

    @Override
    public List<PurchaseOrder> getApprovedOrdersByUser(Long userId) {
        return purchaseOrderRepository.findAll().stream()
                .filter(order -> order.getUser().getId().equals(userId))
                .filter(order -> !order.isOrderCanceled())
                .filter(order -> order.getOrderStatus() == OrderStatus.APPROVED)
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrder> getOrdersNotYetApprovedBySeller(Long userId) {
        return purchaseOrderRepository.findAll().stream()
                .filter(order -> !order.isOrderCanceled())
                .filter(order -> order.getOrderStatus() == OrderStatus.PENDING)
                .filter(order -> order.getSeller() != null && order.getSeller().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean confirmOrder(Long orderId) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with ID " + orderId + " does not exist."));

        if (order.getOrderStatus() != OrderStatus.APPROVED) {
            return false;
        }

        order.setOrderedTime(LocalDateTime.now());
        order.setOrderStatus(OrderStatus.CONFIRMED);
        order.generateOtp();
        purchaseOrderRepository.save(order);
        return true;
    }

    @Override
    public ItemsNeedingApprovalDTO getProductsNeedingApproval(Long sellerId) {
        List<PurchaseOrder> orders = purchaseOrderRepository.findAll();

        List<Product> productsNeedingApproval = orders.stream()
                .filter(order -> !order.isOrderCanceled())
                .filter(order -> order.getProduct() != null)
                .filter(order -> order.getSeller() != null && order.getSeller().getId().equals(sellerId))
                .filter(order -> order.getOrderStatus() == OrderStatus.PENDING)
                .map(PurchaseOrder::getProduct)
                .distinct()
                .collect(Collectors.toList());

        List<Pet> petsNeedingApproval = orders.stream()
                .filter(order -> !order.isOrderCanceled())
                .filter(order -> order.getPet() != null)
                .filter(order -> order.getSeller() != null && order.getSeller().getId().equals(sellerId))
                .filter(order -> order.getOrderStatus() == OrderStatus.PENDING)
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

        if (!order.getOrderStatus().equals(OrderStatus.PENDING)) {
            throw new AccessDeniedException("Order cannot be rejected as it is not in PENDING status.");
        }

        if (order.getSeller() != null && !order.getSeller().getId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to reject this order.");
        }

        order.setOrderStatus(OrderStatus.REJECTED);
        order.setOrderCanceled(true);
        purchaseOrderRepository.save(order);
    }

    @Override
    public List<PurchaseOrder> getApprovedOrdersBySeller(Long sellerId) {
        return purchaseOrderRepository.findAll().stream()
                .filter(order -> order.getSeller() != null && order.getSeller().getId().equals(sellerId))
                .filter(order -> order.getOrderStatus() == OrderStatus.APPROVED)
                .filter(order -> !order.isOrderCanceled())
                .collect(Collectors.toList());
    }

    @Override
    public void handleItemApproval(Long orderId, Long itemId, Long userId, boolean approve,
                                   LocalDateTime shipmentTime, LocalDateTime approxDeliveryTime,
                                   LocalDateTime maxDeliveryTime, Double deliveryCost)
            throws AccessDeniedException {

        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with ID " + orderId + " does not exist."));

        if (order.isOrderCanceled()) {
            throw new IllegalStateException("Order has already been canceled and cannot be modified.");
        }

        if (order.getSeller() == null || !order.getSeller().getId().equals(userId)) {
            throw new AccessDeniedException("You are not authorized to approve this order.");
        }

        if (approve) {
            order.setOrderStatus(OrderStatus.APPROVED);
            order.setShipmentTime(shipmentTime);
            order.setApproxDeliveryTime(approxDeliveryTime);
            order.setMaxDeliveryTime(maxDeliveryTime);
            order.setDeliveryCost(deliveryCost);
        } else {
            order.setOrderStatus(OrderStatus.REJECTED);
            order.setOrderCanceled(true);
        }

        purchaseOrderRepository.save(order);
    }

    @Override
    public void cancelOrder(Long orderId, Long userId) throws AccessDeniedException {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with ID " + orderId + " does not exist."));

        if (order.isOrderCanceled()) {
            throw new IllegalStateException("Order has already been canceled.");
        }

        if (!order.getUser().getId().equals(userId) && (order.getSeller() == null || !order.getSeller().getId().equals(userId))) {
            throw new AccessDeniedException("You are not authorized to cancel this order.");
        }

        if (order.getOrderStatus() == OrderStatus.CONFIRMED && order.getShipmentTime() != null) {
            order.setCancellationFee(order.getTotalOrderValue() * 0.30);
        }

        order.setOrderStatus(OrderStatus.CANCELED);
        order.setOrderCanceled(true);
        purchaseOrderRepository.save(order);
    }

    @Override
    public List<PurchaseOrder> getConfirmedOrdersByUser(Long userId) {
        return purchaseOrderRepository.findAll().stream()
                .filter(order -> order.getUser().getId().equals(userId))
                .filter(order -> order.getOrderStatus() == OrderStatus.CONFIRMED)
                .filter(order -> !order.isOrderCanceled())
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrder> getCanceledOrdersByUser(Long userId) {
        return purchaseOrderRepository.findAll().stream()
                .filter(order -> order.getUser().getId().equals(userId))
                .filter(order -> order.getOrderStatus() == OrderStatus.CANCELED)
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrder> getConfirmedOrdersBySeller(Long sellerId) {
        return purchaseOrderRepository.findAll().stream()
                .filter(order -> order.getSeller() != null && order.getSeller().getId().equals(sellerId))
                .filter(order -> order.getOrderStatus() == OrderStatus.CONFIRMED)
                .filter(order -> !order.isOrderCanceled())
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrder> getCanceledOrdersBySeller(Long sellerId) {
        return purchaseOrderRepository.findAll().stream()
                .filter(order -> order.getSeller() != null && order.getSeller().getId().equals(sellerId))
                .filter(order -> order.getOrderStatus() == OrderStatus.CANCELED)
                .collect(Collectors.toList());
    }

    @Override
    public void completeOrder(Long orderId, Long sellerId, String otp) throws AccessDeniedException {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with ID " + orderId + " does not exist."));

        if (order.getOrderStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Order has not been confirmed and cannot be completed.");
        }

        if (order.getSeller() == null || !order.getSeller().getId().equals(sellerId)) {
            throw new AccessDeniedException("You are not authorized to complete this order.");
        }

        if (order.getOtp().equals(otp)) {
            order.setOrderCompleted(true);
            order.setOrderStatus(OrderStatus.COMPLETED);
            purchaseOrderRepository.save(order);
        } else {
            throw new IllegalArgumentException("Invalid OTP.");
        }
    }
}

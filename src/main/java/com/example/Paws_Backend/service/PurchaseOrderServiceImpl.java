package com.example.Paws_Backend.service;

import com.example.Paws_Backend.dto.ItemsNeedingApprovalDTO;
import com.example.Paws_Backend.model.*;
import com.example.Paws_Backend.repository.PurchaseOrderRepository;
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

    @Override
    public PurchaseOrder createOrder(PurchaseOrder order) {
        User user = order.getUser();
        if (!"consumer".equalsIgnoreCase(user.getUserRole())) {
            throw new IllegalArgumentException("Only users with the role 'consumer' can create orders.");
        }

        // Initialize the seller approval statuses map
        Map<Long, Boolean> sellerApprovalStatuses;

        if (order.getProduct() != null) {
            sellerApprovalStatuses = Map.of(order.getProduct().getSeller().getId(), false);
        } else if (order.getPet() != null) {
            sellerApprovalStatuses = Map.of(order.getPet().getSeller().getId(), false);
        } else {
            throw new IllegalArgumentException("Order must contain either a product or a pet.");
        }

        order.setSellerApprovalStatuses(sellerApprovalStatuses);
        order.setUser(user);

        return purchaseOrderRepository.save(order);
    }

    @Override
    public List<PurchaseOrder> getApprovedOrdersByUser(Long userId) {
        return purchaseOrderRepository.findById(userId).stream()
                .filter(order -> order.getSellerApprovalStatuses().values().stream()
                        .allMatch(Boolean::booleanValue))
                .collect(Collectors.toList());
    }

    @Override
    public boolean confirmOrder(Long orderId) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with ID " + orderId + " does not exist."));

        if (order.getSellerApprovalStatuses().values().stream().allMatch(Boolean::booleanValue)) {
            order.setOrderConfirmed(true);
            purchaseOrderRepository.save(order);
            return true;
        }
        return false;
    }

    @Override
    public ItemsNeedingApprovalDTO getProductsNeedingApproval(Long sellerId) {
        List<PurchaseOrder> orders = purchaseOrderRepository.findAll();

        // Filter products needing approval
        List<Product> productsNeedingApproval = orders.stream()
                .filter(order -> order.getProduct() != null &&
                        order.getProduct().getSeller().getId().equals(sellerId) &&
                        !order.getSellerApprovalStatuses().getOrDefault(sellerId, false))
                .map(PurchaseOrder::getProduct)
                .distinct()
                .collect(Collectors.toList());

        // Filter pets needing approval
        List<Pet> petsNeedingApproval = orders.stream()
                .filter(order -> order.getPet() != null &&
                        order.getPet().getSeller().getId().equals(sellerId) &&
                        !order.getSellerApprovalStatuses().getOrDefault(sellerId, false))
                .map(PurchaseOrder::getPet)
                .distinct()
                .collect(Collectors.toList());

        return new ItemsNeedingApprovalDTO(productsNeedingApproval, petsNeedingApproval);
    }




    @Override
    public void rejectOrder(Long orderId, Long userId) throws AccessDeniedException {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with ID " + orderId + " does not exist."));

        Map<Long, Boolean> statuses = order.getSellerApprovalStatuses();

        if (statuses.containsKey(userId)) {
            statuses.put(userId, false);
            order.setSellerApprovalStatuses(statuses);
            purchaseOrderRepository.save(order);
        } else {
            throw new AccessDeniedException("User does not have permission to reject this order.");
        }
    }

    @Override
    public List<PurchaseOrder> getApprovedOrdersBySeller(Long sellerId) {
        return purchaseOrderRepository.findAll().stream()
                .filter(order -> order.getSellerApprovalStatuses().containsKey(sellerId) &&
                        order.getSellerApprovalStatuses().get(sellerId))
                .collect(Collectors.toList());
    }
    @Override
    public void handleItemApproval(Long orderId, Long itemId, Long userId, boolean approve, LocalDateTime shipmentTime) throws AccessDeniedException {
        // Retrieve the order
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with ID " + orderId + " does not exist."));

        // Check if the seller is approving the item
        Optional<Product> productOpt = productService.getProductById(itemId);
        Optional<Pet> petOpt = petService.getPetById(itemId);

        boolean itemFound = false;
        if (productOpt.isPresent() && productOpt.get().getSeller().getId().equals(userId)) {
            itemFound = true;
        } else if (petOpt.isPresent() && petOpt.get().getSeller().getId().equals(userId)) {
            itemFound = true;
        }

        if (!itemFound) {
            throw new AccessDeniedException("User does not have permission to handle this item.");
        }

        // Update the approval status and set shipment time if approved
        Map<Long, Boolean> statuses = order.getSellerApprovalStatuses();
        statuses.put(userId, approve);
        order.setSellerApprovalStatuses(statuses);

        if (approve) {
            order.setShipmentTime(shipmentTime);
        }

        // Save the updated order
        purchaseOrderRepository.save(order);
    }

    @Override
    public void cancelOrder(Long orderId, Long userId) throws AccessDeniedException {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with ID " + orderId + " does not exist."));

        if (!order.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("User does not have permission to cancel this order.");
        }

        if (LocalDateTime.now().isAfter(order.getShipmentTime())) {
            double deduction = order.getTotalOrderValue() * 0.30;
            order.setCancellationFee(deduction);
        } else {
            order.setCancellationFee(0.0);
        }

        purchaseOrderRepository.save(order);
    }
    @Override
    public List<PurchaseOrder> getConfirmedOrdersByUser(Long userId) {
        return purchaseOrderRepository.findAll().stream()
                .filter(order -> order.getUser().getId().equals(userId) && order.isOrderConfirmed())
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrder> getCanceledOrdersByUser(Long userId) {
        return purchaseOrderRepository.findAll().stream()
                .filter(order -> order.getUser().getId().equals(userId) && order.getCancellationFee() != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrder> getConfirmedOrdersBySeller(Long sellerId) {
        return purchaseOrderRepository.findAll().stream()
                .filter(order -> order.getSellerApprovalStatuses().containsKey(sellerId)
                        && order.getSellerApprovalStatuses().get(sellerId)
                        && order.isOrderConfirmed())
                .collect(Collectors.toList());
    }

    @Override
    public List<PurchaseOrder> getCanceledOrdersBySeller(Long sellerId) {
        return purchaseOrderRepository.findAll().stream()
                .filter(order -> order.getSellerApprovalStatuses().containsKey(sellerId)
                        && order.getSellerApprovalStatuses().get(sellerId)
                        && order.getCancellationFee() != null)
                .collect(Collectors.toList());
    }


}

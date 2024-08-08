package com.example.Paws_Backend.service;

import com.example.Paws_Backend.model.Pet;
import com.example.Paws_Backend.model.Product;
import com.example.Paws_Backend.model.PurchaseOrder;
import com.example.Paws_Backend.model.User;
import com.example.Paws_Backend.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
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
    public PurchaseOrder createOrder(PurchaseOrder order, User user) {
        if (!"consumer".equalsIgnoreCase(user.getUserRole())) {
            throw new IllegalArgumentException("Only users with the role 'consumer' can create orders.");
        }

        // Initialize the seller approval statuses map
        Map<Long, Boolean> sellerApprovalStatuses = new HashMap<>();

        if (order.getProducts() != null) {
            for (Product product : order.getProducts()) {
                sellerApprovalStatuses.put(product.getSeller().getId(), false);
            }
        }
        if (order.getPets() != null) {
            for (Pet pet : order.getPets()) {
                sellerApprovalStatuses.put(pet.getSeller().getId(), false);
            }
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
    public List<Product> getProductsNeedingApproval(Long sellerId) {
        List<PurchaseOrder> orders = purchaseOrderRepository.findAll();

        return orders.stream()
                .flatMap(order -> order.getProducts().stream()
                        .filter(product -> product.getSeller().getId().equals(sellerId) &&
                                !order.getSellerApprovalStatuses().getOrDefault(sellerId, false)))
                .distinct()
                .collect(Collectors.toList());
    }
    @Override
    public void handleItemApproval(Long orderId, Long itemId, Long userId, boolean approve) throws AccessDeniedException {
        // Retrieve the order
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order with ID " + orderId + " does not exist."));

        // Try to find the item in products or pets
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

        // Update the approval status
        Map<Long, Boolean> statuses = order.getSellerApprovalStatuses();
        statuses.put(userId, approve);
        order.setSellerApprovalStatuses(statuses);

        // Save the updated order
        purchaseOrderRepository.save(order);
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
}

package com.example.Paws_Backend.controller;

import com.example.Paws_Backend.model.PurchaseOrder;
import com.example.Paws_Backend.model.Product;
import com.example.Paws_Backend.model.User;
import com.example.Paws_Backend.service.PurchaseOrderService;
import com.example.Paws_Backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<PurchaseOrder> createOrder(@RequestHeader("Authorization") String jwt, @RequestBody PurchaseOrder order) {
        User user = userService.findUserByJwt(jwt);
        if (user == null) {
            return ResponseEntity.status(401).body(null);
        }
        PurchaseOrder createdOrder = purchaseOrderService.createOrder(order, user);
        return ResponseEntity.ok(createdOrder);
    }

    @PutMapping("/{orderId}/handleItem/{itemId}/user/{userId}")
    public ResponseEntity<String> handleItemApproval(@PathVariable Long orderId,
                                                     @PathVariable Long itemId,
                                                     @PathVariable Long userId,
                                                     @RequestParam boolean approve) {
        try {
            purchaseOrderService.handleItemApproval(orderId, itemId, userId, approve);
            return ResponseEntity.ok(approve ? "Item approved successfully." : "Item rejected successfully.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{orderId}/confirm")
    public ResponseEntity<String> confirmOrder(@PathVariable Long orderId) {
        boolean isConfirmed = purchaseOrderService.confirmOrder(orderId);
        if (isConfirmed) {
            return ResponseEntity.ok("Order confirmed successfully.");
        } else {
            return ResponseEntity.status(400).body("Order could not be confirmed. Not all items are approved.");
        }
    }

    @GetMapping("/{userId}/approved-orders")
    public ResponseEntity<?> getApprovedOrdersByUser(@PathVariable Long userId) {
        try {
            List<PurchaseOrder> approvedOrders = purchaseOrderService.getApprovedOrdersByUser(userId);
            return ResponseEntity.ok(approvedOrders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/products-needing-approval/{sellerId}")
    public ResponseEntity<List<Product>> getProductsNeedingApproval(@PathVariable Long sellerId) {
        try {
            List<Product> products = purchaseOrderService.getProductsNeedingApproval(sellerId);
            return ResponseEntity.ok(products);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}

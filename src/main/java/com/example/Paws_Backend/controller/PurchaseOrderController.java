package com.example.Paws_Backend.controller;

import com.example.Paws_Backend.dto.ItemsNeedingApprovalDTO;
import com.example.Paws_Backend.model.PurchaseOrder;
import com.example.Paws_Backend.model.User;
import com.example.Paws_Backend.service.PurchaseOrderService;
import com.example.Paws_Backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
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
        PurchaseOrder createdOrder = purchaseOrderService.createOrder(order);
        return ResponseEntity.ok(createdOrder);
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

    @GetMapping("/items-needing-approval/{sellerId}")
    public ResponseEntity<ItemsNeedingApprovalDTO> getItemsNeedingApproval(@PathVariable Long sellerId) {
        try {
            ItemsNeedingApprovalDTO itemsNeedingApproval = purchaseOrderService.getProductsNeedingApproval(sellerId);
            return ResponseEntity.ok(itemsNeedingApproval);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @PutMapping("/{orderId}/handleItem/{itemId}/user/{userId}")
    public ResponseEntity<String> handleItemApproval(@PathVariable Long orderId,
                                                     @PathVariable Long itemId,
                                                     @PathVariable Long userId,
                                                     @RequestParam boolean approve,
                                                     @RequestParam(required = false) LocalDateTime shipmentTime) {
        try {
            if (approve && shipmentTime == null) {
                return ResponseEntity.badRequest().body("Shipment time is required when approving the item.");
            }
            purchaseOrderService.handleItemApproval(orderId, itemId, userId, approve, shipmentTime);
            return ResponseEntity.ok(approve ? "Item approved successfully." : "Item rejected successfully.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId,
                                              @RequestHeader("Authorization") String jwt) {
        User user = userService.findUserByJwt(jwt);
        if (user == null) {
            return ResponseEntity.status(401).body("User not authorized");
        }
        try {
            purchaseOrderService.cancelOrder(orderId, user.getId());
            return ResponseEntity.ok("Order canceled successfully. A cancellation fee may be applied.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/user/{userId}/confirmed-orders")
    public ResponseEntity<List<PurchaseOrder>> getConfirmedOrdersByUser(@PathVariable Long userId) {
        try {
            List<PurchaseOrder> orders = purchaseOrderService.getConfirmedOrdersByUser(userId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/user/{userId}/canceled-orders")
    public ResponseEntity<List<PurchaseOrder>> getCanceledOrdersByUser(@PathVariable Long userId) {
        try {
            List<PurchaseOrder> orders = purchaseOrderService.getCanceledOrdersByUser(userId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/seller/{sellerId}/confirmed-orders")
    public ResponseEntity<List<PurchaseOrder>> getConfirmedOrdersBySeller(@PathVariable Long sellerId) {
        try {
            List<PurchaseOrder> orders = purchaseOrderService.getConfirmedOrdersBySeller(sellerId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/seller/{sellerId}/canceled-orders")
    public ResponseEntity<List<PurchaseOrder>> getCanceledOrdersBySeller(@PathVariable Long sellerId) {
        try {
            List<PurchaseOrder> orders = purchaseOrderService.getCanceledOrdersBySeller(sellerId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


}

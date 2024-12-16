package com.example.Paws_Backend.controller;

import com.example.Paws_Backend.dto.ItemsNeedingApprovalDTO;
import com.example.Paws_Backend.model.Pet;
import com.example.Paws_Backend.model.Product;
import com.example.Paws_Backend.model.PurchaseOrder;
import com.example.Paws_Backend.model.User;
import com.example.Paws_Backend.repository.PurchaseOrderRepository;
import com.example.Paws_Backend.response.ErrorResponse;
import com.example.Paws_Backend.service.PetService;
import com.example.Paws_Backend.service.ProductService;
import com.example.Paws_Backend.service.PurchaseOrderService;
import com.example.Paws_Backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private ProductService productService;
    @Autowired
    private PetService petService;
    @Autowired
    private UserService userService;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @PostMapping("/product/{productId}")
    public ResponseEntity<PurchaseOrder> createProductOrder(@RequestHeader("Authorization") String jwt, @RequestBody PurchaseOrder order,@PathVariable Long productId) {
        User user = userService.findUserByJwt(jwt);
        Product product=productService.getProductById(productId).get();
        order.setProduct(product);
        order.setPrice(product.getPrice());
        order.setOrderedTime(LocalDateTime.now());
        if (user == null) {
            return ResponseEntity.status(401).body(null);
        }
        PurchaseOrder createdOrder = purchaseOrderService.createOrder(order, user);
        return ResponseEntity.ok(createdOrder);
    }
    @PostMapping("/pet/{petId}")
    public ResponseEntity<PurchaseOrder> createPetOrder(@RequestHeader("Authorization") String jwt, @RequestBody PurchaseOrder order,@PathVariable Long petId) {
        User user = userService.findUserByJwt(jwt);
        Pet pet=petService.getPetById(petId).get();
        order.setPet(pet);
        order.setPrice(pet.getPrice());
        order.setOrderedTime(LocalDateTime.now());
        if (user == null) {
            return ResponseEntity.status(401).body(null);
        }
        PurchaseOrder createdOrder = purchaseOrderService.createOrder(order, user);
        return ResponseEntity.ok(createdOrder);
    }

    @PutMapping("/{orderId}/confirm")
    public ResponseEntity<String> confirmOrder(@PathVariable Long orderId,@RequestHeader("Authorization") String token) {
        boolean isConfirmed = purchaseOrderService.confirmOrder(orderId,userService.findUserByJwt(token).getId());
        if (isConfirmed) {
            PurchaseOrder order = purchaseOrderRepository.findById(orderId).get();
            return ResponseEntity.ok("Order confirmed successfully. OTP: " + order.getOtp());
        } else {
            return ResponseEntity.status(400).body("Order could not be confirmed. Not all items are approved.");
        }
    }

    @GetMapping("/approved-orders/user")
    public ResponseEntity<?> getApprovedOrdersByUser(@RequestHeader("Authorization") String token) {
        try {
            List<PurchaseOrder> approvedOrders = purchaseOrderService.getApprovedOrdersByUserId(userService.findUserByJwt(token).getId());
            return ResponseEntity.ok(approvedOrders);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/not-approved")
    public ResponseEntity<?> getNotApprovedOrdersByUserId(@RequestHeader("Authorization") String token) {
        try {
            List<PurchaseOrder> notApprovedOrders = purchaseOrderService.getNotApprovedOrdersByUserId(userService.findUserByJwt(token).getId());
            return ResponseEntity.ok(notApprovedOrders);
        } catch (IllegalArgumentException e) {
            // Return a response with a bad request status and an error message
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            // Handle other unexpected exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred."));
        }
    }

    @GetMapping("/items-needing-approval/seller")
    public ResponseEntity<ItemsNeedingApprovalDTO> getItemsNeedingApproval(@RequestHeader("Authorization") String token) {
        try {
            User user = userService.findUserByJwt(token);
            if(user.getUserRole().equals("consumer"))
                return ResponseEntity.status(403).body(null);
            ItemsNeedingApprovalDTO itemsNeedingApproval = purchaseOrderService.getProductsNeedingApproval(user.getId());
            return ResponseEntity.ok(itemsNeedingApproval);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @PutMapping("/{orderId}/handleItem/user")
    public ResponseEntity<String> handleItemApproval(@PathVariable Long orderId,
                                                     @RequestHeader("Authorization") String token,
                                                     @RequestParam boolean approve,
                                                     @RequestParam(required = false) String senderAddress,
                                                     @RequestParam(required = false) LocalDateTime shipmentTime,
                                                     @RequestParam(required = false) LocalDateTime approxDeliveryTime,
                                                     @RequestParam(required = false) LocalDateTime maxDeliveryTime,
                                                     @RequestParam(required = false) Double deliveryCost) {
        try {
            if (approve) {
                if (senderAddress==null||shipmentTime == null || approxDeliveryTime == null || maxDeliveryTime == null || deliveryCost == null) {
                    return ResponseEntity.badRequest().body("Shipment time, approximate delivery time, maximum delivery time, and delivery cost are required when approving the item.");
                }
            }
            purchaseOrderService.handleItemApproval(orderId,  userService.findUserByJwt(token).getId(), approve, shipmentTime, approxDeliveryTime, maxDeliveryTime, deliveryCost,senderAddress);
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
    @GetMapping("/user/confirmed-orders")
    public ResponseEntity<List<PurchaseOrder>> getConfirmedOrdersByUser(@RequestHeader("Authorization") String token) {
        try {
            List<PurchaseOrder> orders = purchaseOrderService.getConfirmedOrdersByUser(userService.findUserByJwt(token).getId());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/user/canceled-orders")
    public ResponseEntity<List<PurchaseOrder>> getCanceledOrdersByUser(@RequestHeader("Authorization") String token) {
        try {
            List<PurchaseOrder> orders = purchaseOrderService.getCanceledOrdersByUser(userService.findUserByJwt(token).getId());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/seller/confirmed-orders")
    public ResponseEntity<List<PurchaseOrder>> getConfirmedOrdersBySeller(@RequestHeader("Authorization") String token) {
        try {
            List<PurchaseOrder> orders = purchaseOrderService.getConfirmedOrdersBySeller(userService.findUserByJwt(token).getId());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/seller/canceled-orders")
    public ResponseEntity<List<PurchaseOrder>> getCanceledOrdersBySeller(@RequestHeader("Authorization") String token) {
        try {
            List<PurchaseOrder> orders = purchaseOrderService.getCanceledOrdersBySeller(userService.findUserByJwt(token).getId());
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
    @PostMapping("/{orderId}/complete")
    public ResponseEntity<String> completeOrder(@PathVariable Long orderId,
                                                @RequestHeader("Authorization") String token,
                                                @RequestParam String otp) {
        try {
            purchaseOrderService.completeOrder(orderId, userService.findUserByJwt(token).getId(), otp);
            return ResponseEntity.ok("Order completed successfully.");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}

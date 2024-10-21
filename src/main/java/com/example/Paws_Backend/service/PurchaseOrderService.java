package com.example.Paws_Backend.service;


import com.example.Paws_Backend.dto.ItemsNeedingApprovalDTO;
import com.example.Paws_Backend.model.PurchaseOrder;
import com.example.Paws_Backend.model.User;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

public interface PurchaseOrderService {


    PurchaseOrder createOrder(PurchaseOrder order, User user);

   List<PurchaseOrder> getApprovedOrdersByUserId(Long userId);

    List<PurchaseOrder> getNotApprovedOrdersByUserId(Long userId);

    //
    List<PurchaseOrder> getOrdersNotYetApprovedBySeller(Long userId);
//
    boolean confirmOrder(Long orderId,Long userId);
//
  ItemsNeedingApprovalDTO getProductsNeedingApproval(Long sellerId);

    void rejectOrder(Long orderId, Long userId) throws AccessDeniedException;

    List<PurchaseOrder> getApprovedOrdersBySeller(Long sellerId);


    void handleItemApproval(Long orderId, Long itemId, Long userId, boolean approve,
                            LocalDateTime shipmentTime, LocalDateTime approxDeliveryTime,
                            LocalDateTime maxDeliveryTime, Double deliveryCost)
            throws AccessDeniedException;

    void cancelOrder(Long orderId, Long userId) throws AccessDeniedException;
    List<PurchaseOrder> getConfirmedOrdersByUser(Long userId);
    List<PurchaseOrder> getCanceledOrdersByUser(Long userId);

    List<PurchaseOrder> getConfirmedOrdersBySeller(Long sellerId);
    List<PurchaseOrder> getCanceledOrdersBySeller(Long sellerId);

    void completeOrder(Long orderId, Long sellerId, String otp) throws AccessDeniedException;
}

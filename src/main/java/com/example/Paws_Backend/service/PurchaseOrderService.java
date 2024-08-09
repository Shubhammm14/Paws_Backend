package com.example.Paws_Backend.service;


import com.example.Paws_Backend.dto.ItemsNeedingApprovalDTO;
import com.example.Paws_Backend.model.PurchaseOrder;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

public interface PurchaseOrderService {


    PurchaseOrder createOrder(PurchaseOrder order);

    List<PurchaseOrder> getApprovedOrdersByUser(Long userId);

    boolean confirmOrder(Long orderId);

    ItemsNeedingApprovalDTO getProductsNeedingApproval(Long sellerId);

    void rejectOrder(Long orderId, Long userId) throws AccessDeniedException;

    List<PurchaseOrder> getApprovedOrdersBySeller(Long sellerId);

    void handleItemApproval(Long orderId, Long itemId, Long userId, boolean approve, LocalDateTime shipmentTime) throws AccessDeniedException;

    void cancelOrder(Long orderId, Long userId) throws AccessDeniedException;
    List<PurchaseOrder> getConfirmedOrdersByUser(Long userId);
    List<PurchaseOrder> getCanceledOrdersByUser(Long userId);

    List<PurchaseOrder> getConfirmedOrdersBySeller(Long sellerId);
    List<PurchaseOrder> getCanceledOrdersBySeller(Long sellerId);

    void completeOrder(Long orderId, Long sellerId, String otp) throws AccessDeniedException;
}

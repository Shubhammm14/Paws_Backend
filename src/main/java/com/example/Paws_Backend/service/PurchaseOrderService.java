package com.example.Paws_Backend.service;


import com.example.Paws_Backend.model.Product;
import com.example.Paws_Backend.model.PurchaseOrder;
import com.example.Paws_Backend.model.User;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface PurchaseOrderService {




    PurchaseOrder createOrder(PurchaseOrder order, User user);

    List<PurchaseOrder> getApprovedOrdersByUser(Long userId);

    boolean confirmOrder(Long orderId);

    List<Product> getProductsNeedingApproval(Long sellerId);

    //void approveItem(Long orderId, Long itemId, Long userId) throws AccessDeniedException;

    void handleItemApproval(Long orderId, Long itemId, Long userId, boolean approve) throws AccessDeniedException;

    void rejectOrder(Long orderId, Long userId) throws AccessDeniedException;

    List<PurchaseOrder> getApprovedOrdersBySeller(Long sellerId);
}

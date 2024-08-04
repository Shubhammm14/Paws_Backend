package com.example.Paws_Backend.service;

import com.example.Paws_Backend.model.Order;
import com.example.Paws_Backend.model.User;

public interface OrderService {
    /**
     * Creates a new order.
     * @param order The order to be created.
     * @param userRole The role of the user attempting to create the order.
     * @throws IllegalArgumentException if the user does not have the role of "seller".
     */
    void createOrder(Order order, User userRole);
}

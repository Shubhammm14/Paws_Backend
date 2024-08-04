package com.example.Paws_Backend.service;

import com.example.Paws_Backend.model.Order;
import com.example.Paws_Backend.model.User;
import com.example.Paws_Backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;


    @Override
    public void createOrder(Order order, User user) {
        if (!"consumer".equalsIgnoreCase(user.getUserRole())) {
            throw new IllegalArgumentException("Only users with the role 'consumer' can create orders.");
        }
        orderRepository.save(order);
    }
}

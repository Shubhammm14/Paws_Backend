package com.example.Paws_Backend.repository;

import com.example.Paws_Backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}

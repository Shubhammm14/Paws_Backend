package com.example.Paws_Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

import java.time.LocalDateTime;

@Entity
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private Double originalPrice;
    private Double deliveryCost;
    private Double totalOrderValue;
    private LocalDateTime orderedTime;
    private LocalDateTime deliveryTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Default constructor
    public Order() {
    }

    // Parameterized constructor
    public Order(Product product, Double deliveryCost, LocalDateTime orderedTime, LocalDateTime deliveryTime, User user) {
        this.product = product;
        this.originalPrice = product != null ? product.getPrice() : 0.0;
        this.deliveryCost = deliveryCost;
        this.totalOrderValue = calculateTotalOrderValue();
        this.orderedTime = orderedTime;
        this.deliveryTime = deliveryTime;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.originalPrice = product != null ? product.getPrice() : 0.0;
        calculateTotalOrderValue();  // Update total order value if product changes
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
        calculateTotalOrderValue();  // Update total order value if original price changes
    }

    public Double getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(Double deliveryCost) {
        this.deliveryCost = deliveryCost;
        calculateTotalOrderValue();  // Update total order value if delivery cost changes
    }

    public Double getTotalOrderValue() {
        return totalOrderValue;
    }

    private Double calculateTotalOrderValue() {
        return (this.originalPrice != null ? this.originalPrice : 0.0) +
                (this.deliveryCost != null ? this.deliveryCost : 0.0);
    }

    public LocalDateTime getOrderedTime() {
        return orderedTime;
    }

    public void setOrderedTime(LocalDateTime orderedTime) {
        this.orderedTime = orderedTime;
    }

    public LocalDateTime getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(LocalDateTime deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", product=" + product +
                ", originalPrice=" + originalPrice +
                ", deliveryCost=" + deliveryCost +
                ", totalOrderValue=" + totalOrderValue +
                ", orderedTime=" + orderedTime +
                ", deliveryTime=" + deliveryTime +
                ", user=" + user +
                '}';
    }
}

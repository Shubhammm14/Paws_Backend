package com.example.Paws_Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class PetOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Pet pet;

    private LocalDateTime orderedTime;
    private LocalDateTime deliveryTime;

    @ManyToOne
    private User user;

    private double deliveryCost;
    private double totalValue;

    private String senderAddress;
    private String receiverAddress;

    public PetOrder() {
    }

    public PetOrder(Pet pet, LocalDateTime orderedTime, LocalDateTime deliveryTime, User user, double deliveryCost, String senderAddress, String receiverAddress) {
        this.pet = pet;
        this.orderedTime = orderedTime;
        this.deliveryTime = deliveryTime;
        this.user = user;
        this.deliveryCost = deliveryCost;
        this.senderAddress = senderAddress;
        this.receiverAddress = receiverAddress;
        this.totalValue = calculateTotalValue();
    }

    private double calculateTotalValue() {
        // Assuming Pet has a getPrice() method
        return (pet != null ? pet.getPrice() : 0) + deliveryCost;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
        this.totalValue = calculateTotalValue(); // Recalculate total value if pet changes
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

    public double getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(double deliveryCost) {
        this.deliveryCost = deliveryCost;
        this.totalValue = calculateTotalValue(); // Recalculate total value if delivery cost changes
    }

    public double getTotalValue() {
        return totalValue;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    // Optional: Override toString() for easier debugging
    @Override
    public String toString() {
        return "PetOrder{" +
                "id=" + id +
                ", pet=" + pet +
                ", orderedTime=" + orderedTime +
                ", deliveryTime=" + deliveryTime +
                ", user=" + user +
                ", deliveryCost=" + deliveryCost +
                ", totalValue=" + totalValue +
                ", senderAddress='" + senderAddress + '\'' +
                ", receiverAddress='" + receiverAddress + '\'' +
                '}';
    }
}

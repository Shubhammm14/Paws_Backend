package com.example.Paws_Backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "pet_id")
    private Pet pet;

    private Double price;
    private Double deliveryCost;
    private Double totalOrderValue;
    private LocalDateTime orderedTime;
    private LocalDateTime deliveryTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    private String senderAddress;
    private String receiverAddress;

    @ElementCollection
    @MapKeyColumn(name = "seller_id")
    @Column(name = "approval_status")
    private Map<Long, Boolean> sellerApprovalStatuses;

    private boolean orderConfirmed;
    private LocalDateTime shipmentTime;
    private Double cancellationFee;

    // Getters and Setters...

    public LocalDateTime getShipmentTime() {
        return shipmentTime;
    }

    public void setShipmentTime(LocalDateTime shipmentTime) {
        this.shipmentTime = shipmentTime;
    }

    public Double getCancellationFee() {
        return cancellationFee;
    }

    public void setCancellationFee(Double cancellationFee) {
        this.cancellationFee = cancellationFee;
    }
    public PurchaseOrder() {
    }

    public PurchaseOrder(Product product, Pet pet, User user, Double deliveryCost, Double totalOrderValue, String senderAddress, String receiverAddress, boolean orderConfirmed) {
        this.product = product;
        this.pet = pet;
        this.price = calculatePrice();
        this.deliveryCost = deliveryCost;
        this.totalOrderValue = totalOrderValue;
        this.orderedTime = LocalDateTime.now();
        this.user = user;
        this.senderAddress = senderAddress;
        this.receiverAddress = receiverAddress;
        this.orderConfirmed = orderConfirmed;
    }

    private Double calculatePrice() {
        if (product != null) {
            return Double.valueOf(product.getPrice());
        } else if (pet != null) {
            return Double.valueOf(pet.getPrice());
        }
        return 0.0;
    }

    private Double calculateTotalOrderValue() {
        return calculatePrice() + (this.deliveryCost != null ? this.deliveryCost : 0.0);
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
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(Double deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public Double getTotalOrderValue() {
        return totalOrderValue;
    }

    public void setTotalOrderValue(Double totalOrderValue) {
        this.totalOrderValue = totalOrderValue;
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

    public Map<Long, Boolean> getSellerApprovalStatuses() {
        return sellerApprovalStatuses;
    }

    public void setSellerApprovalStatuses(Map<Long, Boolean> sellerApprovalStatuses) {
        this.sellerApprovalStatuses = sellerApprovalStatuses;
        this.orderConfirmed = checkOrderConfirmation();
    }

    public boolean isOrderConfirmed() {
        return orderConfirmed;
    }

    public void setOrderConfirmed(boolean orderConfirmed) {
        this.orderConfirmed = orderConfirmed;
    }

    private boolean checkOrderConfirmation() {
        return sellerApprovalStatuses.values().stream().allMatch(Boolean::booleanValue);
    }

    @Override
    public String toString() {
        return "PurchaseOrder{" +
                "id=" + id +
                ", product=" + product +
                ", pet=" + pet +
                ", price=" + price +
                ", deliveryCost=" + deliveryCost +
                ", totalOrderValue=" + totalOrderValue +
                ", orderedTime=" + orderedTime +
                ", deliveryTime=" + deliveryTime +
                ", user=" + user +
                ", senderAddress='" + senderAddress + '\'' +
                ", receiverAddress='" + receiverAddress + '\'' +
                ", sellerApprovalStatuses=" + sellerApprovalStatuses +
                ", orderConfirmed=" + orderConfirmed +
                '}';
    }
}

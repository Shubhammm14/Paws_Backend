package com.example.Paws_Backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.security.SecureRandom;
import java.time.LocalDateTime;

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
    private User user; // Consumer

    @ManyToOne
    @JoinColumn(name = "seller_id")
    @JsonIgnore
    private User seller; // Seller

    private String senderAddress;
    private String receiverAddress;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private boolean orderConfirmed;
    private LocalDateTime shipmentTime;
    private Double cancellationFee;
    private String otp;
    private boolean orderCompleted;
    private LocalDateTime approxDeliveryTime;
    private LocalDateTime maxDeliveryTime;
    private boolean orderCanceled;

    public PurchaseOrder() {
        this.orderedTime = LocalDateTime.now();
        this.price = calculatePrice();
        this.totalOrderValue = calculateTotalOrderValue();
        this.orderStatus = OrderStatus.PENDING; // Set initial status to PENDING
    }

    public PurchaseOrder(Product product, Pet pet, User user, User seller, Double deliveryCost, String senderAddress, String receiverAddress) {
        this();
        if (product != null) {
            this.product = product;
        } else if (pet != null) {
            this.pet = pet;
        }
        this.user = user;
        this.seller = seller;
        this.deliveryCost = deliveryCost;
        this.senderAddress = senderAddress;
        this.receiverAddress = receiverAddress;
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

    public void generateOtp() {
        SecureRandom random = new SecureRandom();
        int number = random.nextInt(999999);
        this.otp = String.format("%06d", number);
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
        this.price = calculatePrice();
        this.totalOrderValue = calculateTotalOrderValue();
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
        this.price = calculatePrice();
        this.totalOrderValue = calculateTotalOrderValue();
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
        this.totalOrderValue = calculateTotalOrderValue();
    }

    public Double getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(Double deliveryCost) {
        this.deliveryCost = deliveryCost;
        this.totalOrderValue = calculateTotalOrderValue();
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

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
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

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public boolean isOrderConfirmed() {
        return orderConfirmed;
    }

    public void setOrderConfirmed(boolean orderConfirmed) {
        this.orderConfirmed = orderConfirmed;
    }

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

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public boolean isOrderCompleted() {
        return orderCompleted;
    }

    public void setOrderCompleted(boolean orderCompleted) {
        this.orderCompleted = orderCompleted;
    }

    public LocalDateTime getApproxDeliveryTime() {
        return approxDeliveryTime;
    }

    public void setApproxDeliveryTime(LocalDateTime approxDeliveryTime) {
        this.approxDeliveryTime = approxDeliveryTime;
    }

    public LocalDateTime getMaxDeliveryTime() {
        return maxDeliveryTime;
    }

    public void setMaxDeliveryTime(LocalDateTime maxDeliveryTime) {
        this.maxDeliveryTime = maxDeliveryTime;
    }

    public boolean isOrderCanceled() {
        return orderCanceled;
    }

    public void setOrderCanceled(boolean orderCanceled) {
        this.orderCanceled = orderCanceled;
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
                ", seller=" + seller +
                ", senderAddress='" + senderAddress + '\'' +
                ", receiverAddress='" + receiverAddress + '\'' +
                ", orderStatus=" + orderStatus +
                ", orderConfirmed=" + orderConfirmed +
                ", shipmentTime=" + shipmentTime +
                ", cancellationFee=" + cancellationFee +
                ", otp='" + otp + '\'' +
                ", orderCompleted=" + orderCompleted +
                ", approxDeliveryTime=" + approxDeliveryTime +
                ", maxDeliveryTime=" + maxDeliveryTime +
                ", orderCanceled=" + orderCanceled +
                '}';
    }
}

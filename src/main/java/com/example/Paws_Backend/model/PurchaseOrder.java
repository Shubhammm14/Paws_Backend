package com.example.Paws_Backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany
    @JoinColumn(name = "product_id")
    private List<Product> products;

    @OneToMany
    @JoinColumn(name = "pet_id")
    private List<Pet> pets;

    private Double originalPrice;
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

//    @Transient
//    private GeocodingService geocodingService;

    // Default constructor
    public PurchaseOrder() {
    }

    // Parameterized constructor
    public PurchaseOrder(List<Product> products, List<Pet> pets,  User user,Double deliveryCost,Double totalOrderValue, String senderAddress, String receiverAddress, boolean orderConfirmed) {
        this.products = products;
        this.pets = pets;
        this.originalPrice = calculateOriginalPrice();
        //this.deliveryCost = calculateDeliveryCost(senderAddress, receiverAddress, geocodingService);
        //this.totalOrderValue = calculateTotalOrderValue();
        this.deliveryCost=deliveryCost;
        this.totalOrderValue=totalOrderValue;
        this.orderedTime = LocalDateTime.now();
        this.user = user;
        this.senderAddress = senderAddress;
        this.receiverAddress = receiverAddress;
        this.orderConfirmed = orderConfirmed;
       // this.geocodingService = geocodingService;
    }

    private Double calculateOriginalPrice() {
        double price = 0.0;
        if (products != null) {
            for (Product product : products) {
                price += product.getPrice();
            }
        }
        if (pets != null) {
            for (Pet pet : pets) {
                price += pet.getPrice();
            }
        }
        return price;
    }





    private Double calculateTotalOrderValue() {
        return calculateOriginalPrice() + (this.deliveryCost != null ? this.deliveryCost : 0.0);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Pet> getPets() {
        return pets;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
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
                ", products=" + products +
                ", pets=" + pets +
                ", originalPrice=" + originalPrice +
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

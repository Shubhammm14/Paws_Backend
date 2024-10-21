package com.example.Paws_Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;
    private String name;
    private String profileImg;
    private String password;
    private String userRole; // Added userRole property
    private String vetType; // Added vetType property
    private String vetDescription; // Added vetDescription property
    private Integer numberOfAppointments; // Added numberOfAppointments property

    @OneToMany(mappedBy = "user")
    private List<PurchaseOrder> orders; // Changed to list of Order

    // Default constructor
    public User() {
    }

    // Parameterized constructor
    public User(String email, String name, String password, String userRole) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.userRole = userRole;
    }
    public User(String email, String name, String password, String userRole,String vetType, String vetDescription) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.userRole = userRole;
        this.vetType = vetType;
        this.vetDescription = vetDescription;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getVetType() {
        return vetType;
    }

    public void setVetType(String vetType) {
        this.vetType = vetType;
    }

    public String getVetDescription() {
        return vetDescription;
    }

    public void setVetDescription(String vetDescription) {
        this.vetDescription = vetDescription;
    }

    public Integer getNumberOfAppointments() {
        return numberOfAppointments;
    }

    public void setNumberOfAppointments(Integer numberOfAppointments) {
        this.numberOfAppointments = numberOfAppointments;
    }

    public List<PurchaseOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<PurchaseOrder> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", profileImg='" + profileImg + '\'' +
                ", password='" + password + '\'' +
                ", userRole='" + userRole + '\'' +
                ", vetType='" + vetType + '\'' +
                ", vetDescription='" + vetDescription + '\'' +
                ", numberOfAppointments=" + numberOfAppointments +
                ", orders=" + orders +
                '}';
    }
}

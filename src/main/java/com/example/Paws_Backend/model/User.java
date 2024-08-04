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

    @OneToMany(mappedBy = "user")
    private List<PetOrder> petOrders;

    @OneToMany(mappedBy = "user")
    private List<Order> accessoriesOrders;

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

    public List<PetOrder> getPetOrders() {
        return petOrders;
    }

    public void setPetOrders(List<PetOrder> petOrders) {
        this.petOrders = petOrders;
    }

    public List<Order> getAccessoriesOrders() {
        return accessoriesOrders;
    }

    public void setAccessoriesOrders(List<Order> accessoriesOrders) {
        this.accessoriesOrders = accessoriesOrders;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", profileImg='" + profileImg + '\'' +
                ", password='" + password + '\'' +
                ", userRole='" + userRole + '\'' + // Updated toString method
                ", petOrders=" + petOrders +
                ", accessoriesOrders=" + accessoriesOrders +
                '}';
    }
}

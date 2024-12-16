package com.example.Paws_Backend.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class CareHouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;  // e.g., Dog, Cat, Fish, etc.

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String pincode;

    @ElementCollection
    private List<String> availableServices;  // e.g., grooming, feeding, medical care

    @OneToMany(mappedBy = "careHouse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CareHouseAppointment> appointments;  // Updated to CareHouseAppointment instead of Appointment

    // Default constructor
    public CareHouse() {}

    // Constructor with all fields
    public CareHouse(String name, String type, String address, String pincode, List<String> availableServices) {
        this.name = name;
        this.type = type;
        this.address = address;
        this.pincode = pincode;
        this.availableServices = availableServices;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public List<String> getAvailableServices() {
        return availableServices;
    }

    public void setAvailableServices(List<String> availableServices) {
        this.availableServices = availableServices;
    }

    public List<CareHouseAppointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<CareHouseAppointment> appointments) {
        this.appointments = appointments;
    }
}

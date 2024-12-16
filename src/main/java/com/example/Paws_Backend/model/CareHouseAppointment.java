package com.example.Paws_Backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class CareHouseAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "care_house_id", nullable = false)
    private CareHouse careHouse;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User customer;

    private String petDescription;
    private int petAge;
    private String contactDetails;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus appointmentStatus; // Status of the appointment

    private double charges; // Charges for the appointment

    // Default constructor
    public CareHouseAppointment() {}

    // Constructor
    public CareHouseAppointment(CareHouse careHouse, User customer, String petDescription, int petAge,
                                String contactDetails, LocalDateTime startDate, LocalDateTime endDate,
                                AppointmentStatus appointmentStatus, double charges) {
        this.careHouse = careHouse;
        this.customer = customer;
        this.petDescription = petDescription;
        this.petAge = petAge;
        this.contactDetails = contactDetails;
        this.startDate = startDate;
        this.endDate = endDate;
        this.appointmentStatus = appointmentStatus;
        this.charges = charges;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public CareHouse getCareHouse() {
        return careHouse;
    }

    public void setCareHouse(CareHouse careHouse) {
        this.careHouse = careHouse;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public String getPetDescription() {
        return petDescription;
    }

    public void setPetDescription(String petDescription) {
        this.petDescription = petDescription;
    }

    public int getPetAge() {
        return petAge;
    }

    public void setPetAge(int petAge) {
        this.petAge = petAge;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public AppointmentStatus getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(AppointmentStatus appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    public double getCharges() {
        return charges;
    }

    public void setCharges(double charges) {
        this.charges = charges;
    }

    // Optional: toString for debugging/logging
    @Override
    public String toString() {
        return "CareHouseAppointment{" +
                "id=" + id +
                ", careHouse=" + careHouse.getName() +
                ", customer=" + customer.getName() +
                ", petDescription='" + petDescription + '\'' +
                ", petAge=" + petAge +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", appointmentStatus=" + appointmentStatus +
                ", charges=" + charges +
                '}';
    }
}

package com.example.Paws_Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.time.LocalDateTime;

@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime appointmentDate; // Date and time of the appointment
    private String description; // Description of the appointment
    private String status; // Status of the appointment (e.g., Scheduled, Completed, Canceled)

    @ManyToOne
    private User vet; // The vet associated with this appointment

    @ManyToOne
    private User client; // The client associated with this appointment

    // Default constructor
    public Appointment() {
    }

    // Parameterized constructor
    public Appointment(LocalDateTime appointmentDate, String description, String status, User vet, User client) {
        this.appointmentDate = appointmentDate;
        this.description = description;
        this.status = status;
        this.vet = vet;
        this.client = client;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getVet() {
        return vet;
    }

    public void setVet(User vet) {
        this.vet = vet;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", appointmentDate=" + appointmentDate +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", vet=" + vet +
                ", client=" + client +
                '}';
    }
}

package com.example.Paws_Backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDate appointmentDate; // Date and time of the appointment
    private LocalTime appointmentTime;
    private String description; // Description of the appointment


    @ManyToOne
    private User vet; // The vet associated with this appointment

    @ManyToOne
    private User client; // The client associated with this appointment

    // Default constructor
    public Appointment() {
    }

    // Parameterized constructor
    public Appointment(LocalDate appointmentDate, String description, AppointmentStatus status, User vet, User client) {
        this.appointmentDate = appointmentDate;
        this.description = description;
        this.status = status;
        this.vet = vet;
        this.client = client;
    }
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    // Other fields and methods...

    // Getters and Setters...

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

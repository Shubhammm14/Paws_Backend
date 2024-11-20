package com.example.Paws_Backend.service;

import com.example.Paws_Backend.model.Appointment;
import com.example.Paws_Backend.model.User;

import java.time.LocalTime;
import java.util.List;

public interface AppointmentService {
    Appointment createAppointment(Appointment appointment);

    void approveAppointment(Long appointmentId, Long vetId, LocalTime time);

    void rejectAppointment(Long appointmentId, Long vetId);

    void completeAppointment(Long appointmentId, Long vetId);

    List<Appointment> getAppointmentsByVet(Long vetId);

    List<Appointment> getAppointmentsByClient(Long clientId);

    List<User> searchVets(String keyword);
}

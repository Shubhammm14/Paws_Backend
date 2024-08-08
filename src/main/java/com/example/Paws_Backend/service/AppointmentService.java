package com.example.Paws_Backend.service;

import com.example.Paws_Backend.model.Appointment;
import com.example.Paws_Backend.model.User;

import java.util.List;

public interface AppointmentService {
    Appointment createAppointment(Appointment appointment);

    List<Appointment> getAppointmentsByVet(Long vetId);

    List<Appointment> getAppointmentsByClient(Long clientId);

    List<User> searchVets(String keyword);
}

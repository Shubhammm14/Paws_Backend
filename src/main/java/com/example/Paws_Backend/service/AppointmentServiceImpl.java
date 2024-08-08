package com.example.Paws_Backend.service;

import com.example.Paws_Backend.model.Appointment;
import com.example.Paws_Backend.model.User;
import com.example.Paws_Backend.repository.AppointmentRepository;
import com.example.Paws_Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Appointment createAppointment(Appointment appointment) {
        User client = userRepository.findById(appointment.getClient().getId())
                .orElseThrow(() -> new IllegalArgumentException("Client does not exist."));
        User vet = userRepository.findById(appointment.getVet().getId())
                .orElseThrow(() -> new IllegalArgumentException("Vet does not exist."));

        // Validate that client is a consumer and vet is a vet
        if (!"consumer".equalsIgnoreCase(client.getUserRole())) {
            throw new IllegalArgumentException("Only consumers can book appointments.");
        }
        if (!"vet".equalsIgnoreCase(vet.getUserRole())) {
            throw new IllegalArgumentException("The selected user is not a vet.");
        }

        return appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> getAppointmentsByVet(Long vetId) {
        return appointmentRepository.findAll().stream()
                .filter(appointment -> appointment.getVet().getId().equals(vetId))
                .toList();
    }

    @Override
    public List<Appointment> getAppointmentsByClient(Long clientId) {
        return appointmentRepository.findAll().stream()
                .filter(appointment -> appointment.getClient().getId().equals(clientId))
                .toList();
    }
    @Override
    public List<User> searchVets(String keyword) {
        return userRepository.searchVets(keyword);
    }
}

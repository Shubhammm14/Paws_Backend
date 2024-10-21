package com.example.Paws_Backend.service;

import com.example.Paws_Backend.model.Appointment;
import com.example.Paws_Backend.model.AppointmentStatus;
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
        appointment.setStatus(AppointmentStatus.PENDING);
        return appointmentRepository.save(appointment);
    }
    @Override
    public void approveAppointment(Long appointmentId, Long vetId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment does not exist."));

        if (!appointment.getVet().getId().equals(vetId)) {
            throw new IllegalArgumentException("Vet does not have permission to approve this appointment.");
        }

        appointment.setStatus(AppointmentStatus.SCHEDULED);
        appointmentRepository.save(appointment);
    }

    @Override
    public void rejectAppointment(Long appointmentId, Long vetId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment does not exist."));

        if (!appointment.getVet().getId().equals(vetId)) {
            throw new IllegalArgumentException("Vet does not have permission to reject this appointment.");
        }

        appointment.setStatus(AppointmentStatus.CANCELED);
        appointmentRepository.save(appointment);
    }

    @Override
    public void completeAppointment(Long appointmentId, Long vetId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment does not exist."));

        if (!appointment.getVet().getId().equals(vetId)) {
            throw new IllegalArgumentException("Vet does not have permission to complete this appointment.");
        }

        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);
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

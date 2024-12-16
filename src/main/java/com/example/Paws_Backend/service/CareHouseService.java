package com.example.Paws_Backend.service;

import com.example.Paws_Backend.model.CareHouse;
import com.example.Paws_Backend.model.CareHouseAppointment;
import com.example.Paws_Backend.model.AppointmentStatus;
import com.example.Paws_Backend.model.User;
import com.example.Paws_Backend.repository.CareHouseAppointmentRepository;
import com.example.Paws_Backend.repository.CareHouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CareHouseService {

    @Autowired
    private CareHouseRepository careHouseRepository;

    @Autowired
    private CareHouseAppointmentRepository careHouseAppointmentRepository;

    @Autowired
    private UserService userService;  // Assuming UserService exists to fetch users by JWT or ID

    // Method to create a care house appointment
    public CareHouseAppointment createCareHouseAppointment(Long careHouseId, Long userId, String petDescription,
                                                           int petAge, String contactDetails, LocalDateTime startDate,
                                                           LocalDateTime endDate, double charges) {
        CareHouse careHouse = careHouseRepository.findById(careHouseId)
                .orElseThrow(() -> new IllegalArgumentException("CareHouse not found with id: " + careHouseId));

        User customer = userService.findUserById(userId); // Method to fetch user by ID

        // Create new appointment with status as PENDING and the given charges
        CareHouseAppointment appointment = new CareHouseAppointment(careHouse, customer, petDescription, petAge,
                contactDetails, startDate, endDate, AppointmentStatus.PENDING, charges);

        return careHouseAppointmentRepository.save(appointment);
    }

    // Method to approve an appointment (status: PENDING -> APPROVED -> SCHEDULED)
    public CareHouseAppointment approveAppointment(Long appointmentId) {
        CareHouseAppointment appointment = careHouseAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + appointmentId));

        if (appointment.getAppointmentStatus() == AppointmentStatus.PENDING) {
            appointment.setAppointmentStatus(AppointmentStatus.SCHEDULED); // Automatically set to SCHEDULED after approval
            return careHouseAppointmentRepository.save(appointment);
        } else {
            throw new IllegalStateException("Appointment cannot be approved as it is not in PENDING status.");
        }
    }

    // Method to complete an appointment (status: SCHEDULED -> COMPLETED)
    public CareHouseAppointment completeAppointment(Long appointmentId) {
        CareHouseAppointment appointment = careHouseAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + appointmentId));

        if (appointment.getAppointmentStatus() == AppointmentStatus.SCHEDULED) {
            appointment.setAppointmentStatus(AppointmentStatus.COMPLETED);
            return careHouseAppointmentRepository.save(appointment);
        } else {
            throw new IllegalStateException("Appointment cannot be completed as it is not in SCHEDULED status.");
        }
    }

    // Method to cancel an appointment (status: PENDING or APPROVED -> CANCELED)
    public CareHouseAppointment cancelAppointment(Long appointmentId) {
        CareHouseAppointment appointment = careHouseAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found with id: " + appointmentId));

        if (appointment.getAppointmentStatus() == AppointmentStatus.PENDING || appointment.getAppointmentStatus() == AppointmentStatus.SCHEDULED) {
            appointment.setAppointmentStatus(AppointmentStatus.CANCELED);
            return careHouseAppointmentRepository.save(appointment);
        } else {
            throw new IllegalStateException("Appointment cannot be canceled as it is already completed or canceled.");
        }
    }

    // Method to get appointments for a specific care house
    public List<CareHouseAppointment> getAppointmentsForCareHouse(Long careHouseId) {
        return careHouseAppointmentRepository.findByCareHouseId(careHouseId);
    }

    // Method to get appointments by customer (user)
    public List<CareHouseAppointment> getAppointmentsByUser(Long userId) {
        return careHouseAppointmentRepository.findByCustomerId(userId);
    }
}

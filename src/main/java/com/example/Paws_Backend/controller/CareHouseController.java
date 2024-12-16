package com.example.Paws_Backend.controller;

import com.example.Paws_Backend.model.CareHouseAppointment;
import com.example.Paws_Backend.service.CareHouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/carehouses")
public class CareHouseController {

    @Autowired
    private CareHouseService careHouseService;

    // Create an appointment for a care house
    @PostMapping("/{careHouseId}/appointments")
    public ResponseEntity<CareHouseAppointment> createCareHouseAppointment(
            @PathVariable Long careHouseId,
            @RequestParam Long userId,
            @RequestParam String petDescription,
            @RequestParam int petAge,
            @RequestParam String contactDetails,
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate,
            @RequestParam double charges) {

        CareHouseAppointment appointment = careHouseService.createCareHouseAppointment(careHouseId, userId, petDescription,
                petAge, contactDetails, startDate, endDate, charges);
        return ResponseEntity.ok(appointment);
    }

    // Get all appointments for a care house
    @GetMapping("/{careHouseId}/appointments")
    public List<CareHouseAppointment> getAppointmentsForCareHouse(@PathVariable Long careHouseId) {
        return careHouseService.getAppointmentsForCareHouse(careHouseId);
    }

    // Get all appointments for a user
    @GetMapping("/appointments")
    public List<CareHouseAppointment> getAppointmentsForUser(@RequestParam Long userId) {
        return careHouseService.getAppointmentsByUser(userId);
    }

    // Approve an appointment
    @PutMapping("/appointments/{appointmentId}/approve")
    public ResponseEntity<CareHouseAppointment> approveAppointment(@PathVariable Long appointmentId) {
        CareHouseAppointment appointment = careHouseService.approveAppointment(appointmentId);
        return ResponseEntity.ok(appointment);
    }



    // Complete an appointment
    @PutMapping("/appointments/{appointmentId}/complete")
    public ResponseEntity<CareHouseAppointment> completeAppointment(@PathVariable Long appointmentId) {
        CareHouseAppointment appointment = careHouseService.completeAppointment(appointmentId);
        return ResponseEntity.ok(appointment);
    }
}

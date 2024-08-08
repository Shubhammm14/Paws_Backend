package com.example.Paws_Backend.controller;

import com.example.Paws_Backend.model.Appointment;
import com.example.Paws_Backend.model.User;
import com.example.Paws_Backend.service.AppointmentService;
import com.example.Paws_Backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private UserService userService;

    @GetMapping("/search-vets")
    public ResponseEntity<List<User>> searchVets(@RequestParam(required = false, defaultValue = "") String keyword) {
        List<User> vets = appointmentService.searchVets(keyword);
        return ResponseEntity.ok(vets);
    }
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(@RequestHeader("Authorization") String jwt, @RequestBody Appointment appointment) {
        User user = userService.findUserByJwt(jwt);
        if (user == null) {
            return ResponseEntity.status(401).body(null);
        }

        if (!"consumer".equalsIgnoreCase(user.getUserRole())) {
            return ResponseEntity.status(403).body(null); // Forbidden
        }

        User vet = userService.findUserById(appointment.getVet().getId());
        if (vet == null || !"vet".equalsIgnoreCase(vet.getUserRole())) {
            return ResponseEntity.badRequest().body(null); // Invalid vet
        }

        appointment.setClient(user);
        Appointment createdAppointment = appointmentService.createAppointment(appointment);
        return ResponseEntity.ok(createdAppointment);
    }

    @GetMapping("/vet/{vetId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByVet(@PathVariable Long vetId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByVet(vetId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Appointment>> getAppointmentsByClient(@PathVariable Long clientId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByClient(clientId);
        return ResponseEntity.ok(appointments);
    }
}

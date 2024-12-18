package com.example.Paws_Backend.controller;

import com.example.Paws_Backend.model.Appointment;
import com.example.Paws_Backend.model.User;
import com.example.Paws_Backend.service.AppointmentService;
import com.example.Paws_Backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
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

    @PostMapping("/create/{id}")
    public ResponseEntity<Appointment> createAppointment(@RequestHeader("Authorization") String jwt, @RequestBody Appointment appointment,@PathVariable long id) {
        User user = userService.findUserByJwt(jwt);
        if (user == null) {
            return ResponseEntity.status(401).body(null);
        }

        if (!"consumer".equalsIgnoreCase(user.getUserRole())) {
            return ResponseEntity.status(403).body(null); // Forbidden
        }

        User vet = userService.findUserById(id);
        //ySystem.out.println(vet);
        if (vet == null || !"vet".equalsIgnoreCase(vet.getUserRole())) {
            return ResponseEntity.badRequest().body(null); // Invalid vet
        }
        appointment.setClient(user);
        appointment.setVet(vet);
        Appointment createdAppointment = appointmentService.createAppointment(appointment);
        return ResponseEntity.ok(createdAppointment);
    }

    @GetMapping("/vet")
    public ResponseEntity<List<Appointment>> getAppointmentsByVet(@RequestHeader("Authorization") String jwt) {
        List<Appointment> appointments = appointmentService.getAppointmentsByVet(userService.findUserByJwt(jwt).getId());
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/client")
    public ResponseEntity<List<Appointment>> getAppointmentsByClient(@RequestHeader("Authorization") String jwt) {
        List<Appointment> appointments = appointmentService.getAppointmentsByClient(userService.findUserByJwt(jwt).getId());
        return ResponseEntity.ok(appointments);
    }

    // New endpoints for appointment approval, rejection, and completion

    @PutMapping("/{appointmentId}/approve")
    public ResponseEntity<String> approveAppointment(@PathVariable Long appointmentId, @RequestParam LocalTime time, @RequestHeader("Authorization") String jwt) {
        User vet = userService.findUserByJwt(jwt);
        if (vet == null || !"vet".equalsIgnoreCase(vet.getUserRole())) {
            return ResponseEntity.status(403).body("Only vets can approve appointments.");
        }

        appointmentService.approveAppointment(appointmentId, vet.getId(),time);
        return ResponseEntity.ok("Appointment approved and scheduled.");
    }

    @PutMapping("/{appointmentId}/reject")
    public ResponseEntity<String> rejectAppointment(@PathVariable Long appointmentId, @RequestHeader("Authorization") String jwt) {
        User vet = userService.findUserByJwt(jwt);
        if (vet == null || !"vet".equalsIgnoreCase(vet.getUserRole())) {
            return ResponseEntity.status(403).body("Only vets can reject appointments.");
        }

        appointmentService.rejectAppointment(appointmentId, vet.getId());
        return ResponseEntity.ok("Appointment rejected.");
    }

    @PutMapping("/{appointmentId}/complete")
    public ResponseEntity<String> completeAppointment(@PathVariable Long appointmentId, @RequestHeader("Authorization") String jwt) {
        User vet = userService.findUserByJwt(jwt);
        if (vet == null || !"vet".equalsIgnoreCase(vet.getUserRole())) {
            return ResponseEntity.status(403).body("Only vets can complete appointments.");
        }

        appointmentService.completeAppointment(appointmentId, vet.getId());
        return ResponseEntity.ok("Appointment completed.");
    }
}

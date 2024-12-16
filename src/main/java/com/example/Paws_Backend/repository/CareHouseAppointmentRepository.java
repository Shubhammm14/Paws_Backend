package com.example.Paws_Backend.repository;

import com.example.Paws_Backend.model.CareHouseAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareHouseAppointmentRepository extends JpaRepository<CareHouseAppointment, Long> {

    // Custom method to find appointments by care house ID
    List<CareHouseAppointment> findByCareHouseId(Long careHouseId);

    // Custom method to find appointments by customer (user) ID
    List<CareHouseAppointment> findByCustomerId(Long customerId);

    // Additional custom query methods as required
}

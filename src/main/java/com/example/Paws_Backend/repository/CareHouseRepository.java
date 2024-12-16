package com.example.Paws_Backend.repository;

import com.example.Paws_Backend.model.CareHouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareHouseRepository extends JpaRepository<CareHouse, Long> {

    // Find care houses by type and pincode
    List<CareHouse> findByTypeAndPincode(String type, String pincode);

    // Find care houses by type
    List<CareHouse> findByType(String type);

    // Find care houses by pincode
    List<CareHouse> findByPincode(String pincode);

    // Find care houses by type and available services (you may want to filter by services)
    List<CareHouse> findByTypeAndAvailableServicesContaining(String type, String service);
}

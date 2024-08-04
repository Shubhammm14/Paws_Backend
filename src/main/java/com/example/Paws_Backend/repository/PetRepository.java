package com.example.Paws_Backend.repository;

import com.example.Paws_Backend.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
}

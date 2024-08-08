package com.example.Paws_Backend.controller;

import com.example.Paws_Backend.model.Pet;
import com.example.Paws_Backend.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    @Autowired
    private PetService petService;

    // Create a new pet
    @PostMapping
    public ResponseEntity<Pet> createPet(@RequestBody Pet pet, @RequestParam Long userId) {
        try {
            Pet createdPet = petService.createPet(pet, userId);
            return new ResponseEntity<>(createdPet, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }

    // Update an existing pet
    @PutMapping("/{id}")
    public ResponseEntity<Pet> updatePet(@PathVariable Long id, @RequestBody Pet pet, @RequestParam Long userId) {
        try {
            Pet updatedPet = petService.updatePet(id, pet, userId);
            return new ResponseEntity<>(updatedPet, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }

    // Delete a pet
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePet(@PathVariable Long id, @RequestParam Long userId) {
        try {
            petService.deletePet(id, userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // Get a pet by ID
    @GetMapping("/{id}")
    public ResponseEntity<Pet> getPetById(@PathVariable Long id) {
        Optional<Pet> pet = petService.getPetById(id);
        return pet.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Get all pets
    @GetMapping
    public ResponseEntity<List<Pet>> getAllPets() {
        List<Pet> pets = petService.getAllPets();
        return new ResponseEntity<>(pets, HttpStatus.OK);
    }

    // Search pets by keyword
    @GetMapping("/search")
    public ResponseEntity<List<Pet>> searchPets(@RequestParam String keyword) {
        List<Pet> pets = petService.searchPets(keyword);
        return new ResponseEntity<>(pets, HttpStatus.OK);
    }

    // Search pets by seller ID
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<Pet>> getPetsBySellerId(@PathVariable Long sellerId) {
        List<Pet> pets = petService.searchPetsBySellerId(sellerId);
        return new ResponseEntity<>(pets, HttpStatus.OK);
    }
}

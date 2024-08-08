package com.example.Paws_Backend.service;

import com.example.Paws_Backend.model.Pet;
import com.example.Paws_Backend.model.User;

import java.util.List;
import java.util.Optional;

public interface PetService {


    Pet createPet(Pet pet, Long userId);

    Pet updatePet(Long id, Pet pet, Long userId);

    void deletePet(Long id, Long userId);

    Optional<Pet> getPetById(Long id);

    List<Pet> getAllPets();

    List<Pet> searchPets(String keyword);

    List<Pet> searchPetsBySellerId(Long sellerId);

    List<Pet> searchPetsBySellerName(String sellerName);
}

package com.example.Paws_Backend.service;

import com.example.Paws_Backend.model.Pet;
import com.example.Paws_Backend.model.User;

public interface PetService {
    /**
     * Creates a new pet for sale.
     * @param pet The pet to be created.
     * @param user The role of the user attempting to create the pet.
     * @throws IllegalArgumentException if the user does not have the role of "seller".
     */
    void createPetForSale(Pet pet, User user);
}

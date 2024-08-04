package com.example.Paws_Backend.service;

import com.example.Paws_Backend.model.Pet;
import com.example.Paws_Backend.model.User;
import com.example.Paws_Backend.repository.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PetServiceImpl implements PetService {
    @Autowired
    private PetRepository petRepository;



    @Override
    public void createPetForSale(Pet pet, User user) {
        if (!"seller".equalsIgnoreCase(user.getUserRole())) {
            throw new IllegalArgumentException("Only users with the role 'seller' can create pets for sale.");
        }
        petRepository.save(pet);
    }
}

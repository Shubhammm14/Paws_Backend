package com.example.Paws_Backend.service;

import com.example.Paws_Backend.model.Pet;
import com.example.Paws_Backend.model.User;
import com.example.Paws_Backend.repository.PetRepository;
import com.example.Paws_Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PetServiceImpl implements PetService {
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Pet createPet(Pet pet, Long userId) {
        verifySellerRole(userId);
        pet.setSeller(userRepository.findById(userId).get());
        return petRepository.save(pet);
    }

    @Override
    public Pet updatePet(Long id, Pet pet, Long userId) {
        verifySellerRole(userId);

        // Check if the pet exists
        Pet existingPet = petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pet with ID " + id + " does not exist."));

        // Ensure the user is the seller of the pet
        if (!existingPet.getSeller().getId().equals(userId)) {
            throw new AccessDeniedException("User with ID " + userId + " is not the seller of the pet.");
        }

        // Update pet details
        pet.setId(id);
        pet.setSeller(existingPet.getSeller()); // Preserve the existing seller information
        return petRepository.save(pet);
    }


    @Override
    public void deletePet(Long id, Long userId) {
        verifySellerRole(userId);

        // Retrieve the existing pet
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pet with ID " + id + " does not exist."));

        // Ensure the user is the seller of the pet
        if (!pet.getSeller().getId().equals(userId)) {
            throw new AccessDeniedException("User with ID " + userId + " is not the seller of the pet.");
        }

        // Delete the pet
        petRepository.deleteById(id);
    }


    @Override
    public Optional<Pet> getPetById(Long id) {
        return petRepository.findById(id);
    }

    @Override
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    @Override
    public List<Pet> searchPets(String keyword) {
        return petRepository.searchPets(keyword);
    }
    @Override
    public List<Pet> searchPetsBySellerId(Long sellerId) {
        return petRepository.findPetsBySellerId(sellerId);
    }

    @Override
    public List<Pet> searchPetsBySellerName(String sellerName) {
        return petRepository.findPetsBySellerName(sellerName);
    }

    private void verifySellerRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " does not exist."));
        if (!"seller".equalsIgnoreCase(user.getUserRole())) {
            throw new AccessDeniedException("User with ID " + userId + " does not have the seller role.");
        }
    }
}

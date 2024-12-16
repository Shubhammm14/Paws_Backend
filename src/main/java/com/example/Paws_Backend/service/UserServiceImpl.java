package com.example.Paws_Backend.service;

import com.example.Paws_Backend.config.JwtProvider;
import com.example.Paws_Backend.model.PurchaseOrder;
import com.example.Paws_Backend.model.User;
import com.example.Paws_Backend.repository.PurchaseOrderRepository;
import com.example.Paws_Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    @Override
    public List<PurchaseOrder> getOrdersNeedingApproval(String token) {
        long sellerId=findUserByJwt(token).getId();
        return purchaseOrderRepository.findAll().stream()
                .filter(order -> order.getSeller() != null &&
                        order.getSeller().getId().equals(sellerId) &&
                        !order.isOrderConfirmed())
                .collect(Collectors.toList());
    }


    @Override
    public User updateUser(User user, String token) {
        User existingUser = findUserByJwt(token);
        if (existingUser==null) {
            return null; // Or handle it as you see fit
        }

        if (user.getName() != null && !user.getName().trim().isEmpty()) {
            existingUser.setName(user.getName());
        }
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }
        if (user.getProfileImg() != null && !user.getProfileImg().trim().isEmpty()) {
            existingUser.setProfileImg(user.getProfileImg());
        }

        // Add null checks for userRole before accessing its value
        if (user.getVetType() != null && !user.getVetType().trim().isEmpty() &&
                existingUser.getUserRole() != null && existingUser.getUserRole().equalsIgnoreCase("vet")) {
            existingUser.setVetType(user.getVetType());
        }

        if (user.getVetDescription() != null && !user.getVetDescription().trim().isEmpty() &&
                existingUser.getUserRole() != null && existingUser.getUserRole().equalsIgnoreCase("vet")) {
            existingUser.setVetDescription(user.getVetDescription());
        }

        return userRepository.save(existingUser);
    }


    @Override
    public User findUserByJwt(String token) {
        String email = JwtProvider.getEmailFromJwtToken(token);
        return userRepository.findByEmail(email);
    }

}

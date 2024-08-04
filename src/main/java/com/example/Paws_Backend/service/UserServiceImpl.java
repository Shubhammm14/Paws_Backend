package com.example.Paws_Backend.service;

import com.example.Paws_Backend.config.JwtProvider;
import com.example.Paws_Backend.model.User;
import com.example.Paws_Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User updateUser(User user, Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return null; // Or handle it as you see fit
        }
        User existingUser = userOptional.get();
        if (user.getName() != null && !user.getName().trim().isEmpty())
            existingUser.setName(user.getName());
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty())
            existingUser.setEmail(user.getEmail());
        if (user.getPassword() != null && !user.getPassword().isEmpty())
            existingUser.setPassword(user.getPassword());
        if (user.getProfileImg() != null && !user.getProfileImg().trim().isEmpty())
            existingUser.setProfileImg(user.getProfileImg());

        return userRepository.save(existingUser);
    }

    @Override
    public User findUserByJwt(String token) {
        String email = JwtProvider.getEmailFromJwtToken(token);
        return userRepository.findByEmail(email);
    }

}

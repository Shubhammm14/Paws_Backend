package com.example.Paws_Backend.service;



import com.example.Paws_Backend.model.User;
import com.example.Paws_Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        //System.out.print(user.getPassword());
        if (user == null) {
            throw new UsernameNotFoundException("User not found with the email: " + username);
        }
      // System.out.println(user);
        if (user.getEmail() == null || user.getPassword() == null) {
            throw new IllegalArgumentException("User email or password is null");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        // Add user authorities/roles to the authorities list if needed

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), authorities);
    }

}

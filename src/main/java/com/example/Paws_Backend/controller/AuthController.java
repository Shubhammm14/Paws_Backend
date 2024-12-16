package com.example.Paws_Backend.controller;

import com.example.Paws_Backend.config.JwtProvider;
import com.example.Paws_Backend.exception.UserExcepition;
import com.example.Paws_Backend.service.CustomUserDetailService;
import com.example.Paws_Backend.model.User;
import com.example.Paws_Backend.repository.UserRepository;
import com.example.Paws_Backend.request.LoginRequest;
import com.example.Paws_Backend.response.AuthResponse;
import com.example.Paws_Backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CustomUserDetailService customUserDetailService;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    @PostMapping("/signup")
    public AuthResponse createUser(@RequestBody User user)throws UserExcepition{
        if(user!=null&&user.getEmail()!=null&&isValidEmail(user.getEmail()))
        {
            throw new UserExcepition("invalid mail-id");
        }
        User isExist=userRepository.findByEmail((user.getEmail()));
        if(isExist!=null)
        {
            throw new UserExcepition("this email already used with another account");
        }
        if(user.getEmail().trim().isEmpty()||user.getUserRole().trim().isEmpty()||user.getName().trim().isEmpty()||user.getPassword().trim().isEmpty())
        {
            throw new UserExcepition("all fields are required");
        }
        // Validate user role
        List<String> validRoles = Arrays.asList("consumer", "seller", "vet");
        if (!validRoles.contains(user.getUserRole().trim().toLowerCase())) {
            throw new UserExcepition("Invalid user role. Role must be one of: consumer, seller, or vet.");
        }
        User newUser=new User(user.getEmail(),user.getName(),passwordEncoder.encode(user.getPassword()),user.getUserRole(),user.getVetType(),user.getVetDescription());
        User  savedUser=userRepository.save(newUser);
        Authentication authentication=new UsernamePasswordAuthenticationToken(savedUser.getEmail(),savedUser.getPassword());
        String token= JwtProvider.generateToken(authentication);
        return new AuthResponse(token,"validation done");
    }
    @PostMapping("/sign_in")
    public AuthResponse signIn(@RequestBody LoginRequest loginRequest) throws Exception {
        Authentication authentication= authenticate(loginRequest.getEmail(),loginRequest.getPassword());
        String token=JwtProvider.generateToken(authentication);
        return new AuthResponse(token,"validation done");
    }
    private Authentication authenticate(String email,String password) throws Exception {
        UserDetails userDetails=customUserDetailService.loadUserByUsername(email);
        if(userDetails==null)
            throw new BadCredentialsException("Invalid email");
        if (!passwordEncoder.matches(password,userDetails.getPassword()))
            throw new BadCredentialsException(("Invalid password"));
        return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
    }
}

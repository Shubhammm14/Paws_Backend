package com.example.Paws_Backend.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtProvider {

    // Define the secret key used for signing the JWT
    private static final SecretKey KEY = Keys.hmacShaKeyFor(JwtConstants.SECRET_KEY.getBytes());

    // Method to generate JWT based on authentication information
    public static String generateToken(Authentication authentication) {
        // Extract the principal (in this case, email) from the authentication object
        String email = authentication.getName();
        // Set the token's issuer (you can customize this as needed)
        String issuer = "Shubham";

        // Calculate token expiration time (in this case, 24 hours from now)
        Date now = new Date();
        Date expiryTime = new Date(now.getTime() + 24 * 60 * 60 * 1000); // 24 hours in milliseconds

        // Build the JWT
        String jwt = Jwts.builder()
                .setIssuer(issuer)
               // .setSubject(email) // Set subject as email
                .setIssuedAt(now) // Set issue time
                .setExpiration(expiryTime) // Set expiration time
                .claim("email",authentication.getName())
                .signWith(KEY) // Sign the JWT with the secret key
                .compact();

        return jwt;
    }

    // Method to extract email from JWT token
    public static String getEmailFromJwtToken(String jwt) {
        if (jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
        }
        Claims claims = Jwts.parserBuilder().setSigningKey(KEY).build().parseClaimsJws(jwt).getBody();
        return (String) claims.get("email");
    }
}

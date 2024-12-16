package com.example.Paws_Backend.repository;

import com.example.Paws_Backend.model.Cart;
import com.example.Paws_Backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Finds a cart by the associated user.
     *
     * @param user the user associated with the cart
     * @return an Optional containing the cart if it exists
     */
    Optional<Cart> findByUser(User user);
}

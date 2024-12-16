package com.example.Paws_Backend.controller;

import com.example.Paws_Backend.model.Cart;
import com.example.Paws_Backend.model.Pet;
import com.example.Paws_Backend.model.Product;
import com.example.Paws_Backend.model.User;
import com.example.Paws_Backend.service.CartService;
import com.example.Paws_Backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    private Long getUserIdFromToken(String token) {
        return userService.findUserByJwt(token).getId();
    }

    @GetMapping
    public ResponseEntity<?> getUserCart(@RequestHeader("Authorization") String token) {
        try {
           // Long userId = getUserIdFromToken(token);
            User user=userService.findUserByJwt(token);
            Cart cart = cartService.getCartByUser(user);
            return new ResponseEntity<>(cart, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Cart not found for user.", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/addProduct/{productId}")
    public ResponseEntity<?> addProductToCart(@PathVariable long productId, @RequestHeader("Authorization") String token) {
        try {
            //Long userId = getUserIdFromToken(token);
            User user=userService.findUserByJwt(token);
            Cart updatedCart = cartService.addProductToCart(user, productId);
            return new ResponseEntity<>(updatedCart, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to add product to cart.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/addPet/{petId}")
    public ResponseEntity<?> addPetToCart(@PathVariable long petId, @RequestHeader("Authorization") String token) {
        try {
            //Long userId = getUserIdFromToken(token);
            User user=userService.findUserByJwt(token);
            Cart updatedCart = cartService.addPetToCart(user, petId);
            return new ResponseEntity<>(updatedCart, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to add pet to cart.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/removeProduct/{productId}")
    public ResponseEntity<?> removeProductFromCart(@PathVariable long productId, @RequestHeader("Authorization") String token) {
        try {
            //Long userId = getUserIdFromToken(token);
            User user=userService.findUserByJwt(token);
            Cart updatedCart = cartService.removeProductFromCart(user, productId);
            if (updatedCart == null) {
                return new ResponseEntity<>("Cart is empty and has been deleted.", HttpStatus.OK);
            }
            return new ResponseEntity<>(updatedCart, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to remove product from cart.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/removePet/{petId}")
    public ResponseEntity<?> removePetFromCart(@PathVariable long petId, @RequestHeader("Authorization") String token) {
        try {
           // Long userId = getUserIdFromToken(token);
            User user=userService.findUserByJwt(token);
            Cart updatedCart = cartService.removePetFromCart(user, petId);
            if (updatedCart == null) {
                return new ResponseEntity<>("Cart is empty and has been deleted.", HttpStatus.OK);
            }
            return new ResponseEntity<>(updatedCart, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to remove pet from cart.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

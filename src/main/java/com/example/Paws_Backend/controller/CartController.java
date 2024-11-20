package com.example.Paws_Backend.controller;

import com.example.Paws_Backend.model.Cart;
import com.example.Paws_Backend.model.Pet;
import com.example.Paws_Backend.model.Product;
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

    private void validateCartOwnership(long cartId, String token) {
        Long userIdFromToken = userService.findUserByJwt(token).getId();
        Cart cart = cartService.getCartById(cartId);
        if (!cart.getUser().getId().equals(userIdFromToken)) {
            throw new IllegalArgumentException("You are not authorized to modify this cart.");
        }
    }

    @PostMapping
    public ResponseEntity<?> createCart(@RequestBody Cart cart, @RequestHeader("Authorization") String token) {
        try {
            Long userId = userService.findUserByJwt(token).getId();
            cart.setUser(userService.findUserById(userId));
            Cart createdCart = cartService.createCart(cart);
            return new ResponseEntity<>(createdCart, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to create cart.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCart(@PathVariable long id, @RequestHeader("Authorization") String token) {
        try {
            validateCartOwnership(id, token);
            Cart cart = cartService.getCartById(id);
            return new ResponseEntity<>(cart, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>("Cart not found.", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/{id}/addProduct")
    public ResponseEntity<?> addProduct(@PathVariable long id, @RequestBody Product product, @RequestHeader("Authorization") String token) {
        try {
            validateCartOwnership(id, token);
            Cart updatedCart = cartService.addProductToCart(id, product.getId());
            return new ResponseEntity<>(updatedCart, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to add product to cart.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{id}/addPet")
    public ResponseEntity<?> addPet(@PathVariable long id, @RequestBody Pet pet, @RequestHeader("Authorization") String token) {
        try {
            validateCartOwnership(id, token);
            Cart updatedCart = cartService.addPetToCart(id, pet.getId());
            return new ResponseEntity<>(updatedCart, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to add pet to cart.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}/removeProduct/{productId}")
    public ResponseEntity<?> removeProduct(@PathVariable long id, @PathVariable long productId, @RequestHeader("Authorization") String token) {
        try {
            validateCartOwnership(id, token);
            Cart updatedCart = cartService.removeProductFromCart(id, productId);
            return new ResponseEntity<>(updatedCart, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to remove product from cart.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}/removePet/{petId}")
    public ResponseEntity<?> removePet(@PathVariable long id, @PathVariable long petId, @RequestHeader("Authorization") String token) {
        try {
            validateCartOwnership(id, token);
            Cart updatedCart = cartService.removePetFromCart(id, petId);
            return new ResponseEntity<>(updatedCart, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to remove pet from cart.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCart(@PathVariable long id, @RequestHeader("Authorization") String token) {
        try {
            validateCartOwnership(id, token);
            cartService.deleteCart(id);
            return new ResponseEntity<>("Cart deleted successfully.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete cart.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

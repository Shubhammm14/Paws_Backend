package com.example.Paws_Backend.service;

import com.example.Paws_Backend.model.Cart;
import com.example.Paws_Backend.model.Pet;
import com.example.Paws_Backend.model.Product;
import com.example.Paws_Backend.model.User;
import com.example.Paws_Backend.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PetService petService;

    @Override
    public Cart getCartByUser(User user) {


        // Find the cart associated with the user
        Optional<Cart> cartOptional = cartRepository.findByUser(user);
        if (cartOptional.isEmpty()) {
            // If no cart exists, create a new one
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        }

        return cartOptional.get();
    }
    @Override
    public Cart addProductToCart(User user, long productId) {
        // Fetch or create cart for the user
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> createCartForUser(user));

        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        if (!cart.getProducts().contains(product)) {
            cart.addProduct(product);
        } else {
            throw new IllegalArgumentException("Product already exists in the cart.");
        }

        return cartRepository.save(cart);
    }

    @Override
    public Cart addPetToCart(User user, long petId) {
        // Fetch or create cart for the user
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> createCartForUser(user));

        Pet pet = petService.getPetById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found with id: " + petId));

        if (!cart.getPets().contains(pet)) {
            cart.addPet(pet);
        } else {
            throw new IllegalArgumentException("Pet already exists in the cart.");
        }

        return cartRepository.save(cart);
    }

    @Override
    public Cart removeProductFromCart(User user, long productId) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found for the user."));

        boolean removed = cart.getProducts().removeIf(product -> product.getId() == productId);
        if (!removed) {
            throw new RuntimeException("Product not found in the cart with id: " + productId);
        }

        // Delete the cart if it's empty
        if (cart.getProducts().isEmpty() && cart.getPets().isEmpty()) {
            cartRepository.delete(cart);
            return null; // Indicate that the cart was deleted
        }

        return cartRepository.save(cart);
    }

    @Override
    public Cart removePetFromCart(User user, long petId) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found for the user."));

        boolean removed = cart.getPets().removeIf(pet -> pet.getId() == petId);
        if (!removed) {
            throw new RuntimeException("Pet not found in the cart with id: " + petId);
        }

        // Delete the cart if it's empty
        if (cart.getProducts().isEmpty() && cart.getPets().isEmpty()) {
            cartRepository.delete(cart);
            return null; // Indicate that the cart was deleted
        }

        return cartRepository.save(cart);
    }

    // Helper method to create a cart for a user
    private Cart createCartForUser(User user) {
        Cart cart = new Cart();
        cart.setUser(user);
        return cartRepository.save(cart);
    }
}

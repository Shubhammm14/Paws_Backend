package com.example.Paws_Backend.service;

import com.example.Paws_Backend.model.Cart;
import com.example.Paws_Backend.model.Pet;
import com.example.Paws_Backend.model.Product;
import com.example.Paws_Backend.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PetService petService;

    @Override
    public Cart createCart(Cart cart) {
        if (cart.getUser() == null) {
            throw new IllegalArgumentException("Cart must be associated with a user.");
        }
        return cartRepository.save(cart);
    }

    @Override
    public Cart getCartById(long cartId) {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + cartId));
    }

    @Override
    public Cart addProductToCart(long cartId, long productId) {
        Cart cart = getCartById(cartId);
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
    public Cart addPetToCart(long cartId, long petId) {
        Cart cart = getCartById(cartId);
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
    public Cart removeProductFromCart(long cartId, long productId) {
        Cart cart = getCartById(cartId);

        boolean removed = cart.getProducts().removeIf(product -> product.getId() == productId);
        if (!removed) {
            throw new RuntimeException("Product not found in the cart with id: " + productId);
        }

        return cartRepository.save(cart);
    }

    @Override
    public Cart removePetFromCart(long cartId, long petId) {
        Cart cart = getCartById(cartId);

        boolean removed = cart.getPets().removeIf(pet -> pet.getId() == petId);
        if (!removed) {
            throw new RuntimeException("Pet not found in the cart with id: " + petId);
        }

        return cartRepository.save(cart);
    }

    @Override
    public void deleteCart(long cartId) {
        if (!cartRepository.existsById(cartId)) {
            throw new RuntimeException("Cart not found with id: " + cartId);
        }
        cartRepository.deleteById(cartId);
    }
}

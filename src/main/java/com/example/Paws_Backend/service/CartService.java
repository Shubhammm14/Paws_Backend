package com.example.Paws_Backend.service;

import com.example.Paws_Backend.model.Cart;
import com.example.Paws_Backend.model.Pet;
import com.example.Paws_Backend.model.Product;

public interface CartService {
    Cart createCart(Cart cart);
    Cart getCartById(long cartId);
    Cart addProductToCart(long cartId, long productId);
    Cart addPetToCart(long cartId, long petId);
    Cart removeProductFromCart(long cartId, long productId);
    Cart removePetFromCart(long cartId, long petId);
    void deleteCart(long cartId);
}

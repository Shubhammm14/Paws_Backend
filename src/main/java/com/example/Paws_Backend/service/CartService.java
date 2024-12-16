package com.example.Paws_Backend.service;

import com.example.Paws_Backend.model.Cart;
import com.example.Paws_Backend.model.User;

public interface CartService {

    Cart getCartByUser(User user);

    Cart addProductToCart(User user, long productId);


    Cart addPetToCart(User user, long petId);


    Cart removeProductFromCart(User user, long productId);


    Cart removePetFromCart(User user, long petId);
}

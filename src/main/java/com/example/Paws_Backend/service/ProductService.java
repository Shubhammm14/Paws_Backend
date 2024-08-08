package com.example.Paws_Backend.service;

import com.example.Paws_Backend.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product createProduct(Product product, Long userId);

    Product updateProduct(Long id, Product product, Long userId);

    void deleteProduct(Long id, Long userId);

    Optional<Product> getProductById(Long id);

    List<Product> getAllProducts();

    List<Product> searchProducts(String keyword);

    List<Product> findProductsBySellerName(String sellerName);

    List<Product> findProductsBySellerId(Long sellerId);
}

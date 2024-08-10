package com.example.Paws_Backend.service;

import com.example.Paws_Backend.model.Product;
import com.example.Paws_Backend.model.User;
import com.example.Paws_Backend.repository.ProductRepository;
import com.example.Paws_Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Product createProduct(Product product, Long userId) {
        verifySellerRole(userId);
        User seller = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " does not exist."));
        product.setSeller(seller);
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, Product updatedProduct, Long userId) {
        verifySellerRole(userId);
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product with ID " + id + " does not exist."));

        if (!existingProduct.getSeller().getId().equals(userId)) {
            throw new AccessDeniedException("User with ID " + userId + " does not own the product.");
        }

        // Update only non-null fields in the existing product
        if (updatedProduct.getName() != null) {
            existingProduct.setName(updatedProduct.getName());
        }
        if (updatedProduct.getDescription() != null) {
            existingProduct.setDescription(updatedProduct.getDescription());
        }
        if (updatedProduct.getPrice() != null) {
            existingProduct.setPrice(updatedProduct.getPrice());
        }
        if (updatedProduct.getCategory() != null) {
            existingProduct.setCategory(updatedProduct.getCategory());
        }
        if (updatedProduct.getImageUrl() != null) {
            existingProduct.setImageUrl(updatedProduct.getImageUrl());
        }

        return productRepository.save(existingProduct);
    }


    @Override
    public void deleteProduct(Long id, Long userId) {
        verifySellerRole(userId);
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product with ID " + id + " does not exist."));
        if (!existingProduct.getSeller().getId().equals(userId)) {
            throw new AccessDeniedException("User with ID " + userId + " does not own the product.");
        }
        productRepository.deleteById(id);
    }

    @Override
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword);
    }

    @Override
    public List<Product> findProductsBySellerName(String sellerName) {
        return productRepository.findProductsBySellerName(sellerName);
    }

    @Override
    public List<Product> findProductsBySellerId(Long sellerId) {
        return productRepository.findProductsBySellerId(sellerId);
    }

    private void verifySellerRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " does not exist."));
        if (!"seller".equalsIgnoreCase(user.getUserRole())) {
            throw new AccessDeniedException("User with ID " + userId + " does not have the seller role.");
        }
    }
}

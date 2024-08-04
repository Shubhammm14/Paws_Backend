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

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Product createProduct(Product product, Long userId) {
        verifySellerRole(userId);
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, Product product, Long userId) {
        verifySellerRole(userId);
        if (productRepository.existsById(id)) {
            product.setId(id);
            return productRepository.save(product);
        }
        throw new IllegalArgumentException("Product with ID " + id + " does not exist.");
    }

    @Override
    public void deleteProduct(Long id, Long userId) {
        verifySellerRole(userId);
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Product with ID " + id + " does not exist.");
        }
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
        return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
    }

    private void verifySellerRole(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " does not exist."));
        if (!"seller".equalsIgnoreCase(user.getUserRole())) {
            throw new AccessDeniedException("User with ID " + userId + " does not have the seller role.");
        }
    }
}

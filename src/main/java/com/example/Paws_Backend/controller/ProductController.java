package com.example.Paws_Backend.controller;

import com.example.Paws_Backend.model.Product;
import com.example.Paws_Backend.service.ProductService;
import com.example.Paws_Backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    // Create a new product
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product, @RequestHeader("Authorization") String token) {
        try {

            Product createdProduct = productService.createProduct(product, userService.findUserByJwt(token).getId());
            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }

    // Update an existing product
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product, @RequestHeader("Authorization") String token) {
        try {
            Product updatedProduct = productService.updateProduct(id, product, userService.findUserByJwt(token).getId());
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }

    // Delete a product
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            productService.deleteProduct(id, userService.findUserByJwt(token).getId());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    // Get a product by ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Get all products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Search products by keyword
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Search products by seller name
    @GetMapping("/seller/name")
    public ResponseEntity<List<Product>> getProductsBySellerName(@RequestHeader("Authorization") String token) {
        List<Product> products = productService.findProductsBySellerName(userService.findUserByJwt(token).getName());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Search products by seller ID
    @GetMapping("/seller/id")
    public ResponseEntity<List<Product>> getProductsBySellerId(@RequestHeader("Authorization") String token) {
        List<Product> products = productService.findProductsBySellerId(userService.findUserByJwt(token).getId());
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}

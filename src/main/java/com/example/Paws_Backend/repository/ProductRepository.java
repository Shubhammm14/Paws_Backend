package com.example.Paws_Backend.repository;

import com.example.Paws_Backend.model.Product;
import com.example.Paws_Backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

//    @Query("SELECT p FROM Product p WHERE " +
//            "SIMILARITY(LOWER(p.name), LOWER(:keyword)) > 0.6 OR " +
//            "SIMILARITY(LOWER(p.description), LOWER(:keyword)) > 0.6 OR " +
//            "SIMILARITY(CAST(p.price AS string), :keyword) > 0.6 OR " +
//            "SIMILARITY(LOWER(p.category), LOWER(:keyword)) > 0.6 " +
//            "ORDER BY SIMILARITY(LOWER(p.name), LOWER(:keyword)) DESC")
//    List<Product> searchProducts(@Param("keyword") String keyword);

//    @Query("SELECT p FROM Product p WHERE " +
//            "MATCH(p.name, p.description, p.category) AGAINST(:keyword IN BOOLEAN MODE) " +
//            "OR CONCAT(p.price) LIKE %:keyword%")
//    List<Product> searchProducts(@Param("keyword") String keyword);

    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "CAST(p.price AS string) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.category) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProducts(@Param("keyword") String keyword);

    @Query("SELECT p FROM Product p WHERE LOWER(p.seller.name) LIKE LOWER(CONCAT('%', :sellerName, '%'))")
    List<Product> findProductsBySellerName(@Param("sellerName") String sellerName);

    @Query("SELECT p FROM Product p WHERE p.seller.id = :sellerId")
    List<Product> findProductsBySellerId(@Param("sellerId") Long sellerId);
}

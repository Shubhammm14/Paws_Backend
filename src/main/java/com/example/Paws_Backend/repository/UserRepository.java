package com.example.Paws_Backend.repository;


import com.example.Paws_Backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.vetType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.vetDescription) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> searchVets(@Param("keyword") String keyword);
}

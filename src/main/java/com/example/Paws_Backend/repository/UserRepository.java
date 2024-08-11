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
    @Query(value = "SELECT * FROM User u WHERE " +
            "REGEXP_LIKE(u.vetType, :keyword, 'i') OR " +
            "REGEXP_LIKE(u.vetDescription, :keyword, 'i')",
            nativeQuery = true)
    List<User> searchVets(@Param("keyword") String keyword);



}

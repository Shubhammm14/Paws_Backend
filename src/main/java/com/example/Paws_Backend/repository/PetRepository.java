package com.example.Paws_Backend.repository;

import com.example.Paws_Backend.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {

//    @Query(value = "SELECT * FROM pet WHERE MATCH(breed_name, pet_type, pet_description) AGAINST (?1 IN BOOLEAN MODE) " +
//            "OR CAST(price AS CHAR) LIKE %?1% " +
//            "OR CAST(age AS CHAR) LIKE %?1% " +
//            "ORDER BY MATCH(breed_name, pet_type, pet_description) AGAINST (?1 IN BOOLEAN MODE) DESC",
//            nativeQuery = true)
//    List<Pet> searchPets(String keyword);


    @Query("SELECT p FROM Pet p WHERE " +
            "LOWER(p.breedName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.petType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.petDescription) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "CAST(p.price AS string) LIKE CONCAT('%', :keyword, '%') OR " +
            "CAST(p.age AS string) LIKE CONCAT('%', :keyword, '%')")
    List<Pet> searchPets(@Param("keyword") String keyword);
    @Query("SELECT p FROM Pet p WHERE p.seller.name LIKE %:sellerName%")
    List<Pet> findPetsBySellerName(@Param("sellerName") String sellerName);

    @Query("SELECT p FROM Pet p WHERE p.seller.id = :sellerId")
    List<Pet> findPetsBySellerId(@Param("sellerId") Long sellerId);
}

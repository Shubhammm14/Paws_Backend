package com.example.Paws_Backend.repository;

import com.example.Paws_Backend.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {

//    @Query("SELECT p FROM Pet p WHERE " +
//            "SIMILARITY(LOWER(p.breedName), LOWER(:keyword)) > 0.6 OR " +
//            "SIMILARITY(LOWER(p.petType), LOWER(:keyword)) > 0.6 OR " +
//            "SIMILARITY(LOWER(p.petDescription), LOWER(:keyword)) > 0.6 OR " +
//            "SIMILARITY(CAST(p.price AS string), :keyword) > 0.6 OR " +
//            "SIMILARITY(CAST(p.age AS string), :keyword) > 0.6 " +
//            "ORDER BY SIMILARITY(LOWER(p.breedName), LOWER(:keyword)) DESC")
//    List<Pet> searchPets(@Param("keyword") String keyword);
@Query("SELECT p FROM Pet p WHERE " +
        "MATCH(p.breedName, p.petType, p.petDescription) AGAINST(:keyword IN BOOLEAN MODE) " +
        "OR CAST(p.price AS string) LIKE CONCAT('%', :keyword, '%') " +
        "OR CAST(p.age AS string) LIKE CONCAT('%', :keyword, '%')")
List<Pet> searchPets(@Param("keyword") String keyword);


    @Query("SELECT p FROM Pet p WHERE p.seller.name LIKE %:sellerName%")
    List<Pet> findPetsBySellerName(@Param("sellerName") String sellerName);

    @Query("SELECT p FROM Pet p WHERE p.seller.id = :sellerId")
    List<Pet> findPetsBySellerId(@Param("sellerId") Long sellerId);
}

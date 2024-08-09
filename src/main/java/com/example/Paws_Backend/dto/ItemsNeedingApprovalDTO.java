package com.example.Paws_Backend.dto;

import com.example.Paws_Backend.model.Pet;
import com.example.Paws_Backend.model.Product;

import java.util.List;

public class ItemsNeedingApprovalDTO {

    private List<Product> productsNeedingApproval;
    private List<Pet> petsNeedingApproval;

    // Constructors, Getters, and Setters

    public ItemsNeedingApprovalDTO(List<Product> productsNeedingApproval, List<Pet> petsNeedingApproval) {
        this.productsNeedingApproval = productsNeedingApproval;
        this.petsNeedingApproval = petsNeedingApproval;
    }

    public List<Product> getProductsNeedingApproval() {
        return productsNeedingApproval;
    }

    public void setProductsNeedingApproval(List<Product> productsNeedingApproval) {
        this.productsNeedingApproval = productsNeedingApproval;
    }

    public List<Pet> getPetsNeedingApproval() {
        return petsNeedingApproval;
    }

    public void setPetsNeedingApproval(List<Pet> petsNeedingApproval) {
        this.petsNeedingApproval = petsNeedingApproval;
    }
}

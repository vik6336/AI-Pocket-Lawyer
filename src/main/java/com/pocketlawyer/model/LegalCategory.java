package com.pocketlawyer.model;

import java.sql.Timestamp;

/**
 * Model class for legal categories
 */
public class LegalCategory {
    private int categoryId;
    private String categoryName;
    private String description;
    private Timestamp createdAt;
    
    public LegalCategory() {}
    
    public LegalCategory(int categoryId, String categoryName, String description) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
    }
    
    // Getters and Setters
    public int getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return categoryName;
    }
}

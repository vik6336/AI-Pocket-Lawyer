package com.pocketlawyer.model;

import java.sql.Timestamp;

/**
 * Model class for legal rights information
 */
public class LegalRight {
    private int rightId;
    private int categoryId;
    private String title;
    private String description;
    private String details;
    private String source;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    public LegalRight() {}
    
    public LegalRight(String title, String description, String details) {
        this.title = title;
        this.description = description;
        this.details = details;
    }
    
    // Getters and Setters
    public int getRightId() {
        return rightId;
    }
    
    public void setRightId(int rightId) {
        this.rightId = rightId;
    }
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDetails() {
        return details;
    }
    
    public void setDetails(String details) {
        this.details = details;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}

package com.pocketlawyer.model;

import java.sql.Timestamp;

/**
 * Model class for legal Q&A pairs
 */
public class LegalQA {
    private int qaId;
    private int categoryId;
    private String question;
    private String answer;
    private String keywords;
    private int priority;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    public LegalQA() {}
    
    public LegalQA(String question, String answer, String keywords) {
        this.question = question;
        this.answer = answer;
        this.keywords = keywords;
    }
    
    // Getters and Setters
    public int getQaId() {
        return qaId;
    }
    
    public void setQaId(int qaId) {
        this.qaId = qaId;
    }
    
    public int getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public void setQuestion(String question) {
        this.question = question;
    }
    
    public String getAnswer() {
        return answer;
    }
    
    public void setAnswer(String answer) {
        this.answer = answer;
    }
    
    public String getKeywords() {
        return keywords;
    }
    
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
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

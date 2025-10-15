package com.pocketlawyer.model;

import java.sql.Timestamp;

/**
 * Model class for document templates
 */
public class DocumentTemplate {
    private int templateId;
    private String templateName;
    private String templateType;
    private String templateContent;
    private String fieldsJson;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    public DocumentTemplate() {}
    
    public DocumentTemplate(String templateName, String templateType, String templateContent) {
        this.templateName = templateName;
        this.templateType = templateType;
        this.templateContent = templateContent;
    }
    
    // Getters and Setters
    public int getTemplateId() {
        return templateId;
    }
    
    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }
    
    public String getTemplateName() {
        return templateName;
    }
    
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }
    
    public String getTemplateType() {
        return templateType;
    }
    
    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }
    
    public String getTemplateContent() {
        return templateContent;
    }
    
    public void setTemplateContent(String templateContent) {
        this.templateContent = templateContent;
    }
    
    public String getFieldsJson() {
        return fieldsJson;
    }
    
    public void setFieldsJson(String fieldsJson) {
        this.fieldsJson = fieldsJson;
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
    
    @Override
    public String toString() {
        return templateName;
    }
}

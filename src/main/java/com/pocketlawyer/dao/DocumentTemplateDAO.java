package com.pocketlawyer.dao;

import com.pocketlawyer.database.DatabaseManager;
import com.pocketlawyer.model.DocumentTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Document Template operations
 */
public class DocumentTemplateDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentTemplateDAO.class);
    private final DatabaseManager dbManager;
    
    public DocumentTemplateDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Get all document templates
     */
    public List<DocumentTemplate> getAllTemplates() {
        List<DocumentTemplate> templates = new ArrayList<>();
        String query = "SELECT * FROM document_templates ORDER BY template_name";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                templates.add(mapResultSetToTemplate(rs));
            }
            logger.info("Retrieved {} document templates", templates.size());
            
        } catch (SQLException e) {
            logger.error("Error retrieving document templates", e);
        }
        
        return templates;
    }
    
    /**
     * Get template by ID
     */
    public DocumentTemplate getTemplateById(int templateId) {
        String query = "SELECT * FROM document_templates WHERE template_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, templateId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToTemplate(rs);
            }
            
        } catch (SQLException e) {
            logger.error("Error retrieving template by ID", e);
        }
        
        return null;
    }
    
    /**
     * Get template by name
     */
    public DocumentTemplate getTemplateByName(String templateName) {
        String query = "SELECT * FROM document_templates WHERE template_name = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, templateName);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToTemplate(rs);
            }
            
        } catch (SQLException e) {
            logger.error("Error retrieving template by name", e);
        }
        
        return null;
    }
    
    /**
     * Save generated document record
     */
    public boolean saveGeneratedDocument(int templateId, String fileName, String filePath, String format) {
        String query = "INSERT INTO generated_documents (template_id, file_name, file_path, format) " +
                      "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, templateId);
            pstmt.setString(2, fileName);
            pstmt.setString(3, filePath);
            pstmt.setString(4, format);
            
            int rowsAffected = pstmt.executeUpdate();
            logger.info("Saved generated document record: {}", fileName);
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.error("Error saving generated document record", e);
            return false;
        }
    }
    
    /**
     * Map ResultSet to DocumentTemplate object
     */
    private DocumentTemplate mapResultSetToTemplate(ResultSet rs) throws SQLException {
        DocumentTemplate template = new DocumentTemplate();
        template.setTemplateId(rs.getInt("template_id"));
        template.setTemplateName(rs.getString("template_name"));
        template.setTemplateType(rs.getString("template_type"));
        template.setTemplateContent(rs.getString("template_content"));
        template.setFieldsJson(rs.getString("fields_json"));
        template.setCreatedAt(rs.getTimestamp("created_at"));
        template.setUpdatedAt(rs.getTimestamp("updated_at"));
        return template;
    }
}

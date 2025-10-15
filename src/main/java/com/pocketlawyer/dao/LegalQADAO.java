package com.pocketlawyer.dao;

import com.pocketlawyer.database.DatabaseManager;
import com.pocketlawyer.model.LegalQA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Legal Q&A operations
 */
public class LegalQADAO {
    
    private static final Logger logger = LoggerFactory.getLogger(LegalQADAO.class);
    private final DatabaseManager dbManager;
    
    public LegalQADAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Get all Q&A pairs from database
     */
    public List<LegalQA> getAllQA() {
        List<LegalQA> qaList = new ArrayList<>();
        String query = "SELECT * FROM legal_qa ORDER BY priority DESC, qa_id ASC";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                qaList.add(mapResultSetToQA(rs));
            }
            logger.info("Retrieved {} Q&A pairs from database", qaList.size());
            
        } catch (SQLException e) {
            logger.error("Error retrieving Q&A pairs", e);
        }
        
        return qaList;
    }
    
    /**
     * Search Q&A by keywords
     */
    public List<LegalQA> searchByKeywords(String searchTerm) {
        List<LegalQA> qaList = new ArrayList<>();
        String query = "SELECT * FROM legal_qa WHERE " +
                      "LOWER(question) LIKE ? OR " +
                      "LOWER(answer) LIKE ? OR " +
                      "LOWER(keywords) LIKE ? " +
                      "ORDER BY priority DESC";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                qaList.add(mapResultSetToQA(rs));
            }
            logger.info("Found {} Q&A pairs matching '{}'", qaList.size(), searchTerm);
            
        } catch (SQLException e) {
            logger.error("Error searching Q&A pairs", e);
        }
        
        return qaList;
    }
    
    /**
     * Get Q&A by category
     */
    public List<LegalQA> getQAByCategory(int categoryId) {
        List<LegalQA> qaList = new ArrayList<>();
        String query = "SELECT * FROM legal_qa WHERE category_id = ? ORDER BY priority DESC";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, categoryId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                qaList.add(mapResultSetToQA(rs));
            }
            logger.info("Retrieved {} Q&A pairs for category {}", qaList.size(), categoryId);
            
        } catch (SQLException e) {
            logger.error("Error retrieving Q&A by category", e);
        }
        
        return qaList;
    }
    
    /**
     * Add new Q&A pair
     */
    public boolean addQA(LegalQA qa) {
        String query = "INSERT INTO legal_qa (category_id, question, answer, keywords, priority) " +
                      "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, qa.getCategoryId());
            pstmt.setString(2, qa.getQuestion());
            pstmt.setString(3, qa.getAnswer());
            pstmt.setString(4, qa.getKeywords());
            pstmt.setInt(5, qa.getPriority());
            
            int rowsAffected = pstmt.executeUpdate();
            logger.info("Added new Q&A pair: {}", qa.getQuestion());
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.error("Error adding Q&A pair", e);
            return false;
        }
    }
    
    /**
     * Map ResultSet to LegalQA object
     */
    private LegalQA mapResultSetToQA(ResultSet rs) throws SQLException {
        LegalQA qa = new LegalQA();
        qa.setQaId(rs.getInt("qa_id"));
        qa.setCategoryId(rs.getInt("category_id"));
        qa.setQuestion(rs.getString("question"));
        qa.setAnswer(rs.getString("answer"));
        qa.setKeywords(rs.getString("keywords"));
        qa.setPriority(rs.getInt("priority"));
        qa.setCreatedAt(rs.getTimestamp("created_at"));
        qa.setUpdatedAt(rs.getTimestamp("updated_at"));
        return qa;
    }
}

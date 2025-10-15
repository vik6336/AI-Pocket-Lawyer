package com.pocketlawyer.dao;

import com.pocketlawyer.database.DatabaseManager;
import com.pocketlawyer.model.LegalRight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Legal Rights operations
 */
public class LegalRightsDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(LegalRightsDAO.class);
    private final DatabaseManager dbManager;
    
    public LegalRightsDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Get all legal rights
     */
    public List<LegalRight> getAllRights() {
        List<LegalRight> rightsList = new ArrayList<>();
        String query = "SELECT * FROM legal_rights ORDER BY category_id, right_id";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                rightsList.add(mapResultSetToRight(rs));
            }
            logger.info("Retrieved {} legal rights from database", rightsList.size());
            
        } catch (SQLException e) {
            logger.error("Error retrieving legal rights", e);
        }
        
        return rightsList;
    }
    
    /**
     * Get rights by category
     */
    public List<LegalRight> getRightsByCategory(int categoryId) {
        List<LegalRight> rightsList = new ArrayList<>();
        String query = "SELECT * FROM legal_rights WHERE category_id = ? ORDER BY right_id";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, categoryId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                rightsList.add(mapResultSetToRight(rs));
            }
            logger.info("Retrieved {} rights for category {}", rightsList.size(), categoryId);
            
        } catch (SQLException e) {
            logger.error("Error retrieving rights by category", e);
        }
        
        return rightsList;
    }
    
    /**
     * Search rights by keyword
     */
    public List<LegalRight> searchRights(String searchTerm) {
        List<LegalRight> rightsList = new ArrayList<>();
        String query = "SELECT * FROM legal_rights WHERE " +
                      "LOWER(title) LIKE ? OR " +
                      "LOWER(description) LIKE ? OR " +
                      "LOWER(details) LIKE ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + searchTerm.toLowerCase() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                rightsList.add(mapResultSetToRight(rs));
            }
            logger.info("Found {} rights matching '{}'", rightsList.size(), searchTerm);
            
        } catch (SQLException e) {
            logger.error("Error searching legal rights", e);
        }
        
        return rightsList;
    }
    
    /**
     * Get right by ID
     */
    public LegalRight getRightById(int rightId) {
        String query = "SELECT * FROM legal_rights WHERE right_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, rightId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToRight(rs);
            }
            
        } catch (SQLException e) {
            logger.error("Error retrieving right by ID", e);
        }
        
        return null;
    }
    
    /**
     * Map ResultSet to LegalRight object
     */
    private LegalRight mapResultSetToRight(ResultSet rs) throws SQLException {
        LegalRight right = new LegalRight();
        right.setRightId(rs.getInt("right_id"));
        right.setCategoryId(rs.getInt("category_id"));
        right.setTitle(rs.getString("title"));
        right.setDescription(rs.getString("description"));
        right.setDetails(rs.getString("details"));
        right.setSource(rs.getString("source"));
        right.setCreatedAt(rs.getTimestamp("created_at"));
        right.setUpdatedAt(rs.getTimestamp("updated_at"));
        return right;
    }
}

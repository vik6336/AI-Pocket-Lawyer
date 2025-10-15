package com.pocketlawyer.dao;

import com.pocketlawyer.database.DatabaseManager;
import com.pocketlawyer.model.LegalCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Legal Category operations
 */
public class CategoryDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(CategoryDAO.class);
    private final DatabaseManager dbManager;
    
    public CategoryDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }
    
    /**
     * Get all categories
     */
    public List<LegalCategory> getAllCategories() {
        List<LegalCategory> categories = new ArrayList<>();
        String query = "SELECT * FROM legal_categories ORDER BY category_name";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
            logger.info("Retrieved {} categories", categories.size());
            
        } catch (SQLException e) {
            logger.error("Error retrieving categories", e);
        }
        
        return categories;
    }
    
    /**
     * Get category by ID
     */
    public LegalCategory getCategoryById(int categoryId) {
        String query = "SELECT * FROM legal_categories WHERE category_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, categoryId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCategory(rs);
            }
            
        } catch (SQLException e) {
            logger.error("Error retrieving category by ID", e);
        }
        
        return null;
    }
    
    /**
     * Map ResultSet to LegalCategory object
     */
    private LegalCategory mapResultSetToCategory(ResultSet rs) throws SQLException {
        LegalCategory category = new LegalCategory();
        category.setCategoryId(rs.getInt("category_id"));
        category.setCategoryName(rs.getString("category_name"));
        category.setDescription(rs.getString("description"));
        category.setCreatedAt(rs.getTimestamp("created_at"));
        return category;
    }
}

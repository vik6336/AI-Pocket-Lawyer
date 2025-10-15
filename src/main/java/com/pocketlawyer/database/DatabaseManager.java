package com.pocketlawyer.database;

import com.pocketlawyer.config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database connection manager using JDBC
 * Handles database connections and initialization
 */
public class DatabaseManager {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private static DatabaseManager instance;
    private Connection connection;
    
    private DatabaseManager() {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            logger.info("MySQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            logger.error("MySQL JDBC Driver not found", e);
            throw new RuntimeException("Failed to load MySQL JDBC Driver", e);
        }
    }
    
    /**
     * Get singleton instance of DatabaseManager
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Get database connection
     * Creates new connection if not exists or closed
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(
                DatabaseConfig.DB_URL,
                DatabaseConfig.DB_USER,
                DatabaseConfig.DB_PASSWORD
            );
            logger.info("Database connection established");
        }
        return connection;
    }
    
    /**
     * Test database connection
     */
    public boolean testConnection() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SELECT 1");
            logger.info("Database connection test successful");
            return true;
        } catch (SQLException e) {
            logger.error("Database connection test failed", e);
            return false;
        }
    }
    
    /**
     * Close database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                logger.info("Database connection closed");
            }
        } catch (SQLException e) {
            logger.error("Error closing database connection", e);
        }
    }
    
    /**
     * Initialize database with schema
     * This should be run once during first setup
     */
    public void initializeDatabase() {
        logger.info("Database initialization should be done by running schema.sql manually");
        logger.info("Please execute: mysql -u root -p < database/schema.sql");
    }
}

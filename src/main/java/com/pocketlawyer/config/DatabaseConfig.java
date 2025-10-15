package com.pocketlawyer.config;

/**
 * Database configuration class
 * Stores database connection parameters
 */
public class DatabaseConfig {
    
    // Database connection parameters
    public static final String DB_URL = "jdbc:mysql://localhost:3306/pocket_lawyer";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = ""; // Update with your MySQL password
    
    // Connection pool settings
    public static final int MAX_CONNECTIONS = 10;
    public static final int CONNECTION_TIMEOUT = 30000; // 30 seconds
    
    // Application settings
    public static final String APP_NAME = "AI Pocket Lawyer";
    public static final String APP_VERSION = "1.0.0";
    
    private DatabaseConfig() {
        // Private constructor to prevent instantiation
    }
}

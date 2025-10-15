package com.pocketlawyer;

import com.pocketlawyer.ui.MainFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Main entry point for AI Pocket Lawyer application
 */
public class Main {
    
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {
        logger.info("Starting AI Pocket Lawyer application...");
        
        // Set system properties for better UI rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        // Launch application on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
                logger.info("Application started successfully");
            } catch (Exception e) {
                logger.error("Error starting application", e);
                JOptionPane.showMessageDialog(
                    null,
                    "Error starting application: " + e.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE
                );
                System.exit(1);
            }
        });
    }
}

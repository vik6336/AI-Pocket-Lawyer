package com.pocketlawyer.ui;

import com.pocketlawyer.config.DatabaseConfig;
import com.pocketlawyer.database.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Main application frame with tabbed interface
 */
public class MainFrame extends JFrame {
    
    private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);
    
    private JTabbedPane tabbedPane;
    private ChatbotPanel chatbotPanel;
    private DocumentGeneratorPanel documentPanel;
    private LegalRightsPanel rightsPanel;
    
    public MainFrame() {
        initializeUI();
        checkDatabaseConnection();
    }
    
    /**
     * Initialize the user interface
     */
    private void initializeUI() {
        setTitle(DatabaseConfig.APP_NAME + " - v" + DatabaseConfig.APP_VERSION);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            logger.warn("Could not set system look and feel", e);
        }
        
        // Create main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Create and add panels
        chatbotPanel = new ChatbotPanel();
        documentPanel = new DocumentGeneratorPanel();
        rightsPanel = new LegalRightsPanel();
        
        tabbedPane.addTab("🤖 Legal Chatbot", chatbotPanel);
        tabbedPane.addTab("📄 Document Generator", documentPanel);
        tabbedPane.addTab("⚖️ Legal Rights Hub", rightsPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Create footer
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        logger.info("Main frame initialized");
    }
    
    /**
     * Create header panel
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        headerPanel.setLayout(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel(DatabaseConfig.APP_NAME);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Your AI-Powered Legal Assistant");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(236, 240, 241));
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.CENTER);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        return headerPanel;
    }
    
    /**
     * Create footer panel
     */
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(new Color(52, 73, 94));
        footerPanel.setPreferredSize(new Dimension(0, 30));
        
        JLabel footerLabel = new JLabel("© 2024 AI Pocket Lawyer - Empowering Legal Awareness");
        footerLabel.setForeground(Color.WHITE);
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        
        footerPanel.add(footerLabel);
        
        return footerPanel;
    }
    
    /**
     * Check database connection on startup
     */
    private void checkDatabaseConnection() {
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                DatabaseManager dbManager = DatabaseManager.getInstance();
                return dbManager.testConnection();
            }
            
            @Override
            protected void done() {
                try {
                    boolean connected = get();
                    if (!connected) {
                        showDatabaseError();
                    } else {
                        logger.info("Database connection successful");
                    }
                } catch (Exception e) {
                    logger.error("Error checking database connection", e);
                    showDatabaseError();
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Show database connection error dialog
     */
    private void showDatabaseError() {
        String message = "Could not connect to the database.\n\n" +
                        "Please ensure:\n" +
                        "1. MySQL is running\n" +
                        "2. Database 'pocket_lawyer' exists\n" +
                        "3. Credentials in DatabaseConfig are correct\n\n" +
                        "Run: mysql -u root -p < database/schema.sql";
        
        JOptionPane.showMessageDialog(
            this,
            message,
            "Database Connection Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
    
    /**
     * Main method to launch the application
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}

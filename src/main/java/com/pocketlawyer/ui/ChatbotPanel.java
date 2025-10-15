package com.pocketlawyer.ui;

import com.pocketlawyer.service.ChatbotEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Chatbot panel for legal Q&A
 */
public class ChatbotPanel extends JPanel {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatbotPanel.class);
    
    private JTextArea chatArea;
    private JTextField inputField;
    private JButton sendButton;
    private JButton clearButton;
    private ChatbotEngine chatbotEngine;
    private SimpleDateFormat timeFormat;
    
    public ChatbotPanel() {
        this.chatbotEngine = new ChatbotEngine();
        this.timeFormat = new SimpleDateFormat("HH:mm");
        initializeUI();
        showWelcomeMessage();
    }
    
    /**
     * Initialize the UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Create chat display area
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
        chatArea.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        // Create input panel
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        inputField = new JTextField();
        inputField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        // Add enter key listener
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMessage();
                }
            }
        });
        
        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.BOLD, 14));
        sendButton.setBackground(new Color(46, 204, 113));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.setPreferredSize(new Dimension(100, 40));
        sendButton.addActionListener(e -> sendMessage());
        
        clearButton = new JButton("Clear");
        clearButton.setFont(new Font("Arial", Font.BOLD, 14));
        clearButton.setBackground(new Color(231, 76, 60));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.setBorderPainted(false);
        clearButton.setPreferredSize(new Dimension(100, 40));
        clearButton.addActionListener(e -> clearChat());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonPanel.add(clearButton);
        buttonPanel.add(sendButton);
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Create suggestions panel
        JPanel suggestionsPanel = createSuggestionsPanel();
        
        // Add components to main panel
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        add(suggestionsPanel, BorderLayout.NORTH);
    }
    
    /**
     * Create suggestions panel with quick question buttons
     */
    private JPanel createSuggestionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Quick Questions"));
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        List<String> suggestions = chatbotEngine.getRandomSuggestions(4);
        
        for (String suggestion : suggestions) {
            // Truncate long suggestions
            String displayText = suggestion.length() > 50 ? 
                                suggestion.substring(0, 47) + "..." : suggestion;
            
            JButton btn = new JButton(displayText);
            btn.setFont(new Font("Arial", Font.PLAIN, 11));
            btn.setToolTipText(suggestion);
            btn.addActionListener(e -> {
                inputField.setText(suggestion);
                sendMessage();
            });
            
            buttonsPanel.add(btn);
        }
        
        panel.add(buttonsPanel, BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Show welcome message
     */
    private void showWelcomeMessage() {
        appendBotMessage("Hello! I'm your AI Pocket Lawyer assistant. 👋\n\n" +
                        "I can help you with:\n" +
                        "• Consumer rights and product issues\n" +
                        "• Tenant and landlord disputes\n" +
                        "• Employee rights and workplace matters\n" +
                        "• Women's rights and safety\n" +
                        "• Cyber crimes and online privacy\n" +
                        "• General legal procedures\n\n" +
                        "Ask me any legal question, and I'll provide you with clear, " +
                        "easy-to-understand guidance!");
    }
    
    /**
     * Send user message and get bot response
     */
    private void sendMessage() {
        String userMessage = inputField.getText().trim();
        
        if (userMessage.isEmpty()) {
            return;
        }
        
        // Display user message
        appendUserMessage(userMessage);
        inputField.setText("");
        
        // Get bot response in background
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                return chatbotEngine.getResponse(userMessage);
            }
            
            @Override
            protected void done() {
                try {
                    String response = get();
                    appendBotMessage(response);
                } catch (Exception e) {
                    logger.error("Error getting chatbot response", e);
                    appendBotMessage("I'm sorry, I encountered an error processing your question. " +
                                   "Please try again.");
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Append user message to chat
     */
    private void appendUserMessage(String message) {
        String timestamp = timeFormat.format(new Date());
        chatArea.append(String.format("\n[%s] You:\n%s\n", timestamp, message));
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
    
    /**
     * Append bot message to chat
     */
    private void appendBotMessage(String message) {
        String timestamp = timeFormat.format(new Date());
        chatArea.append(String.format("\n[%s] AI Lawyer:\n%s\n", timestamp, message));
        chatArea.append("\n" + "─".repeat(80) + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
    
    /**
     * Clear chat history
     */
    private void clearChat() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to clear the chat history?",
            "Clear Chat",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            chatArea.setText("");
            showWelcomeMessage();
        }
    }
}

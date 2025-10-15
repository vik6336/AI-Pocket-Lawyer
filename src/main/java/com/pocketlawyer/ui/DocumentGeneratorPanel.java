package com.pocketlawyer.ui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pocketlawyer.dao.DocumentTemplateDAO;
import com.pocketlawyer.model.DocumentTemplate;
import com.pocketlawyer.service.DocumentGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Document generator panel for creating legal documents
 */
public class DocumentGeneratorPanel extends JPanel {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentGeneratorPanel.class);
    
    private JComboBox<DocumentTemplate> templateComboBox;
    private JPanel fieldsPanel;
    private JComboBox<String> formatComboBox;
    private JButton generateButton;
    private JTextArea previewArea;
    
    private DocumentTemplateDAO templateDAO;
    private DocumentGenerator documentGenerator;
    private Map<String, JTextField> fieldInputs;
    
    public DocumentGeneratorPanel() {
        this.templateDAO = new DocumentTemplateDAO();
        this.documentGenerator = new DocumentGenerator();
        this.fieldInputs = new HashMap<>();
        initializeUI();
        loadTemplates();
    }
    
    /**
     * Initialize the UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Create top panel for template selection
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        selectionPanel.add(new JLabel("Select Template:"));
        
        templateComboBox = new JComboBox<>();
        templateComboBox.setPreferredSize(new Dimension(250, 30));
        templateComboBox.addActionListener(e -> onTemplateSelected());
        selectionPanel.add(templateComboBox);
        
        selectionPanel.add(new JLabel("Format:"));
        formatComboBox = new JComboBox<>(new String[]{"PDF", "DOCX"});
        formatComboBox.setPreferredSize(new Dimension(100, 30));
        selectionPanel.add(formatComboBox);
        
        topPanel.add(selectionPanel, BorderLayout.NORTH);
        
        // Create fields panel with scroll
        fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        JScrollPane fieldsScroll = new JScrollPane(fieldsPanel);
        fieldsScroll.setBorder(BorderFactory.createTitledBorder("Document Fields"));
        fieldsScroll.setPreferredSize(new Dimension(0, 300));
        
        topPanel.add(fieldsScroll, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        
        JButton clearButton = new JButton("Clear Fields");
        clearButton.addActionListener(e -> clearFields());
        buttonPanel.add(clearButton);
        
        generateButton = new JButton("Generate Document");
        generateButton.setFont(new Font("Arial", Font.BOLD, 14));
        generateButton.setBackground(new Color(52, 152, 219));
        generateButton.setForeground(Color.WHITE);
        generateButton.setFocusPainted(false);
        generateButton.addActionListener(e -> generateDocument());
        buttonPanel.add(generateButton);
        
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Create preview panel
        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
        
        previewArea = new JTextArea();
        previewArea.setEditable(false);
        previewArea.setLineWrap(true);
        previewArea.setWrapStyleWord(true);
        previewArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        JScrollPane previewScroll = new JScrollPane(previewArea);
        previewPanel.add(previewScroll, BorderLayout.CENTER);
        
        // Add components to main panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, previewPanel);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.5);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Add info panel
        JPanel infoPanel = createInfoPanel();
        add(infoPanel, BorderLayout.NORTH);
    }
    
    /**
     * Create info panel
     */
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(241, 196, 15));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JLabel infoLabel = new JLabel("📄 Generate legal documents by filling in the required fields below");
        infoLabel.setFont(new Font("Arial", Font.BOLD, 13));
        panel.add(infoLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    /**
     * Load templates from database
     */
    private void loadTemplates() {
        SwingWorker<List<DocumentTemplate>, Void> worker = new SwingWorker<List<DocumentTemplate>, Void>() {
            @Override
            protected List<DocumentTemplate> doInBackground() {
                return templateDAO.getAllTemplates();
            }
            
            @Override
            protected void done() {
                try {
                    List<DocumentTemplate> templates = get();
                    templateComboBox.removeAllItems();
                    
                    for (DocumentTemplate template : templates) {
                        templateComboBox.addItem(template);
                    }
                    
                    if (templates.size() > 0) {
                        templateComboBox.setSelectedIndex(0);
                    }
                    
                    logger.info("Loaded {} templates", templates.size());
                } catch (Exception e) {
                    logger.error("Error loading templates", e);
                    JOptionPane.showMessageDialog(
                        DocumentGeneratorPanel.this,
                        "Error loading templates: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Handle template selection
     */
    private void onTemplateSelected() {
        DocumentTemplate template = (DocumentTemplate) templateComboBox.getSelectedItem();
        
        if (template == null) {
            return;
        }
        
        // Clear previous fields
        fieldsPanel.removeAll();
        fieldInputs.clear();
        
        // Parse fields from JSON
        try {
            Gson gson = new Gson();
            List<String> fields = gson.fromJson(
                template.getFieldsJson(),
                new TypeToken<List<String>>(){}.getType()
            );
            
            // Create input fields
            for (String fieldName : fields) {
                JPanel fieldPanel = new JPanel(new BorderLayout(5, 5));
                fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
                fieldPanel.setBorder(new EmptyBorder(2, 5, 2, 5));
                
                JLabel label = new JLabel(formatFieldName(fieldName) + ":");
                label.setPreferredSize(new Dimension(200, 25));
                
                JTextField textField = new JTextField();
                textField.setPreferredSize(new Dimension(300, 25));
                
                fieldPanel.add(label, BorderLayout.WEST);
                fieldPanel.add(textField, BorderLayout.CENTER);
                
                fieldsPanel.add(fieldPanel);
                fieldInputs.put(fieldName, textField);
            }
            
            fieldsPanel.revalidate();
            fieldsPanel.repaint();
            
            // Update preview
            updatePreview();
            
        } catch (Exception e) {
            logger.error("Error parsing template fields", e);
        }
    }
    
    /**
     * Format field name for display
     */
    private String formatFieldName(String fieldName) {
        String formatted = fieldName.replace("_", " ").toLowerCase();
        // Capitalize first letter of each word
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        
        for (char c : formatted.toCharArray()) {
            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
                result.append(c);
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
    
    /**
     * Update preview area
     */
    private void updatePreview() {
        DocumentTemplate template = (DocumentTemplate) templateComboBox.getSelectedItem();
        
        if (template == null) {
            return;
        }
        
        String content = template.getTemplateContent();
        
        // Replace placeholders with field values or [FIELD_NAME]
        for (Map.Entry<String, JTextField> entry : fieldInputs.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue().getText().trim();
            
            if (value.isEmpty()) {
                value = "[" + entry.getKey() + "]";
            }
            
            content = content.replace(placeholder, value);
        }
        
        previewArea.setText(content);
        previewArea.setCaretPosition(0);
    }
    
    /**
     * Clear all input fields
     */
    private void clearFields() {
        for (JTextField field : fieldInputs.values()) {
            field.setText("");
        }
        updatePreview();
    }
    
    /**
     * Generate document
     */
    private void generateDocument() {
        DocumentTemplate template = (DocumentTemplate) templateComboBox.getSelectedItem();
        
        if (template == null) {
            JOptionPane.showMessageDialog(
                this,
                "Please select a template",
                "No Template Selected",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        // Collect field values
        Map<String, String> fieldValues = new HashMap<>();
        boolean hasEmptyFields = false;
        
        for (Map.Entry<String, JTextField> entry : fieldInputs.entrySet()) {
            String value = entry.getValue().getText().trim();
            fieldValues.put(entry.getKey(), value);
            
            if (value.isEmpty()) {
                hasEmptyFields = true;
            }
        }
        
        // Warn about empty fields
        if (hasEmptyFields) {
            int result = JOptionPane.showConfirmDialog(
                this,
                "Some fields are empty. Do you want to continue?",
                "Empty Fields",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        // Generate document in background
        String format = (String) formatComboBox.getSelectedItem();
        
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                return documentGenerator.generateDocument(
                    template.getTemplateName(),
                    fieldValues,
                    format
                );
            }
            
            @Override
            protected void done() {
                try {
                    String filePath = get();
                    
                    if (filePath != null) {
                        int result = JOptionPane.showConfirmDialog(
                            DocumentGeneratorPanel.this,
                            "Document generated successfully!\n\nLocation: " + filePath + 
                            "\n\nDo you want to open the folder?",
                            "Success",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        
                        if (result == JOptionPane.YES_OPTION) {
                            openFileLocation(filePath);
                        }
                    } else {
                        JOptionPane.showMessageDialog(
                            DocumentGeneratorPanel.this,
                            "Error generating document. Please check the logs.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                } catch (Exception e) {
                    logger.error("Error in document generation", e);
                    JOptionPane.showMessageDialog(
                        DocumentGeneratorPanel.this,
                        "Error: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Open file location in system file browser
     */
    private void openFileLocation(String filePath) {
        try {
            File file = new File(filePath);
            Desktop.getDesktop().open(file.getParentFile());
        } catch (Exception e) {
            logger.error("Error opening file location", e);
        }
    }
}

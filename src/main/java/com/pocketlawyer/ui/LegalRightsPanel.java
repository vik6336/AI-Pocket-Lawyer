package com.pocketlawyer.ui;

import com.pocketlawyer.dao.CategoryDAO;
import com.pocketlawyer.dao.LegalRightsDAO;
import com.pocketlawyer.model.LegalCategory;
import com.pocketlawyer.model.LegalRight;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Legal Rights Hub panel for browsing legal rights information
 */
public class LegalRightsPanel extends JPanel {
    
    private static final Logger logger = LoggerFactory.getLogger(LegalRightsPanel.class);
    
    private JComboBox<LegalCategory> categoryComboBox;
    private JList<LegalRight> rightsList;
    private DefaultListModel<LegalRight> rightsListModel;
    private JTextArea detailsArea;
    private JTextField searchField;
    
    private CategoryDAO categoryDAO;
    private LegalRightsDAO rightsDAO;
    
    public LegalRightsPanel() {
        this.categoryDAO = new CategoryDAO();
        this.rightsDAO = new LegalRightsDAO();
        initializeUI();
        loadCategories();
    }
    
    /**
     * Initialize the UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Create top panel
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        
        // Info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(155, 89, 182));
        infoPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        JLabel infoLabel = new JLabel("⚖️ Browse legal rights by category or search for specific information");
        infoLabel.setFont(new Font("Arial", Font.BOLD, 13));
        infoLabel.setForeground(Color.WHITE);
        infoPanel.add(infoLabel, BorderLayout.WEST);
        
        topPanel.add(infoPanel, BorderLayout.NORTH);
        
        // Search and filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        filterPanel.add(new JLabel("Category:"));
        categoryComboBox = new JComboBox<>();
        categoryComboBox.setPreferredSize(new Dimension(200, 30));
        categoryComboBox.addActionListener(e -> onCategorySelected());
        filterPanel.add(categoryComboBox);
        
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(250, 30));
        filterPanel.add(searchField);
        
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());
        filterPanel.add(searchButton);
        
        JButton showAllButton = new JButton("Show All");
        showAllButton.addActionListener(e -> showAllRights());
        filterPanel.add(showAllButton);
        
        topPanel.add(filterPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Create split pane for list and details
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.4);
        
        // Create rights list panel
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder("Legal Rights"));
        
        rightsListModel = new DefaultListModel<>();
        rightsList = new JList<>(rightsListModel);
        rightsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rightsList.setCellRenderer(new RightsListCellRenderer());
        rightsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onRightSelected();
            }
        });
        
        JScrollPane listScroll = new JScrollPane(rightsList);
        listPanel.add(listScroll, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(listPanel);
        
        // Create details panel
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Details"));
        
        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(new Font("Arial", Font.PLAIN, 14));
        detailsArea.setMargin(new Insets(15, 15, 15, 15));
        
        JScrollPane detailsScroll = new JScrollPane(detailsArea);
        detailsPanel.add(detailsScroll, BorderLayout.CENTER);
        
        splitPane.setRightComponent(detailsPanel);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    /**
     * Load categories from database
     */
    private void loadCategories() {
        SwingWorker<List<LegalCategory>, Void> worker = new SwingWorker<List<LegalCategory>, Void>() {
            @Override
            protected List<LegalCategory> doInBackground() {
                return categoryDAO.getAllCategories();
            }
            
            @Override
            protected void done() {
                try {
                    List<LegalCategory> categories = get();
                    categoryComboBox.removeAllItems();
                    
                    // Add "All Categories" option
                    LegalCategory allCategories = new LegalCategory(0, "All Categories", "");
                    categoryComboBox.addItem(allCategories);
                    
                    for (LegalCategory category : categories) {
                        categoryComboBox.addItem(category);
                    }
                    
                    categoryComboBox.setSelectedIndex(0);
                    showAllRights();
                    
                    logger.info("Loaded {} categories", categories.size());
                } catch (Exception e) {
                    logger.error("Error loading categories", e);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Handle category selection
     */
    private void onCategorySelected() {
        LegalCategory category = (LegalCategory) categoryComboBox.getSelectedItem();
        
        if (category == null) {
            return;
        }
        
        if (category.getCategoryId() == 0) {
            showAllRights();
        } else {
            loadRightsByCategory(category.getCategoryId());
        }
    }
    
    /**
     * Load rights by category
     */
    private void loadRightsByCategory(int categoryId) {
        SwingWorker<List<LegalRight>, Void> worker = new SwingWorker<List<LegalRight>, Void>() {
            @Override
            protected List<LegalRight> doInBackground() {
                return rightsDAO.getRightsByCategory(categoryId);
            }
            
            @Override
            protected void done() {
                try {
                    List<LegalRight> rights = get();
                    updateRightsList(rights);
                } catch (Exception e) {
                    logger.error("Error loading rights by category", e);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Show all rights
     */
    private void showAllRights() {
        SwingWorker<List<LegalRight>, Void> worker = new SwingWorker<List<LegalRight>, Void>() {
            @Override
            protected List<LegalRight> doInBackground() {
                return rightsDAO.getAllRights();
            }
            
            @Override
            protected void done() {
                try {
                    List<LegalRight> rights = get();
                    updateRightsList(rights);
                } catch (Exception e) {
                    logger.error("Error loading all rights", e);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Perform search
     */
    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        
        if (searchTerm.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter a search term",
                "Empty Search",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        SwingWorker<List<LegalRight>, Void> worker = new SwingWorker<List<LegalRight>, Void>() {
            @Override
            protected List<LegalRight> doInBackground() {
                return rightsDAO.searchRights(searchTerm);
            }
            
            @Override
            protected void done() {
                try {
                    List<LegalRight> rights = get();
                    updateRightsList(rights);
                    
                    if (rights.isEmpty()) {
                        JOptionPane.showMessageDialog(
                            LegalRightsPanel.this,
                            "No results found for: " + searchTerm,
                            "No Results",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                } catch (Exception e) {
                    logger.error("Error searching rights", e);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Update rights list
     */
    private void updateRightsList(List<LegalRight> rights) {
        rightsListModel.clear();
        
        for (LegalRight right : rights) {
            rightsListModel.addElement(right);
        }
        
        if (!rights.isEmpty()) {
            rightsList.setSelectedIndex(0);
        } else {
            detailsArea.setText("");
        }
    }
    
    /**
     * Handle right selection
     */
    private void onRightSelected() {
        LegalRight selectedRight = rightsList.getSelectedValue();
        
        if (selectedRight == null) {
            return;
        }
        
        // Display details
        StringBuilder details = new StringBuilder();
        details.append(selectedRight.getTitle()).append("\n");
        details.append("=".repeat(selectedRight.getTitle().length())).append("\n\n");
        
        details.append("OVERVIEW\n");
        details.append("-".repeat(50)).append("\n");
        details.append(selectedRight.getDescription()).append("\n\n");
        
        if (selectedRight.getDetails() != null && !selectedRight.getDetails().isEmpty()) {
            details.append("DETAILED INFORMATION\n");
            details.append("-".repeat(50)).append("\n");
            details.append(selectedRight.getDetails()).append("\n\n");
        }
        
        if (selectedRight.getSource() != null && !selectedRight.getSource().isEmpty()) {
            details.append("LEGAL SOURCE\n");
            details.append("-".repeat(50)).append("\n");
            details.append(selectedRight.getSource()).append("\n");
        }
        
        detailsArea.setText(details.toString());
        detailsArea.setCaretPosition(0);
    }
    
    /**
     * Custom cell renderer for rights list
     */
    private class RightsListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                     int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof LegalRight) {
                LegalRight right = (LegalRight) value;
                setText("<html><b>" + right.getTitle() + "</b><br>" +
                       "<small>" + truncate(right.getDescription(), 80) + "</small></html>");
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            }
            
            return this;
        }
        
        private String truncate(String text, int length) {
            if (text == null) return "";
            return text.length() > length ? text.substring(0, length) + "..." : text;
        }
    }
}

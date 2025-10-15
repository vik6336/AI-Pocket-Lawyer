package com.pocketlawyer.service;

import com.pocketlawyer.dao.DocumentTemplateDAO;
import com.pocketlawyer.model.DocumentTemplate;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Document generation service
 * Generates legal documents in DOCX and PDF formats
 */
public class DocumentGenerator {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentGenerator.class);
    private final DocumentTemplateDAO templateDAO;
    
    // Default output directory
    private static final String OUTPUT_DIR = System.getProperty("user.home") + "/PocketLawyer/Documents/";
    
    public DocumentGenerator() {
        this.templateDAO = new DocumentTemplateDAO();
        ensureOutputDirectory();
    }
    
    /**
     * Ensure output directory exists
     */
    private void ensureOutputDirectory() {
        File dir = new File(OUTPUT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
            logger.info("Created output directory: {}", OUTPUT_DIR);
        }
    }
    
    /**
     * Generate document from template with field values
     */
    public String generateDocument(String templateName, Map<String, String> fieldValues, String format) {
        DocumentTemplate template = templateDAO.getTemplateByName(templateName);
        
        if (template == null) {
            logger.error("Template not found: {}", templateName);
            return null;
        }
        
        // Replace placeholders with actual values
        String content = replacePlaceholders(template.getTemplateContent(), fieldValues);
        
        // Generate file based on format
        String filePath = null;
        try {
            if ("PDF".equalsIgnoreCase(format)) {
                filePath = generatePDF(templateName, content);
            } else if ("DOCX".equalsIgnoreCase(format)) {
                filePath = generateDOCX(templateName, content);
            } else {
                logger.error("Unsupported format: {}", format);
                return null;
            }
            
            // Save record to database
            if (filePath != null) {
                String fileName = new File(filePath).getName();
                templateDAO.saveGeneratedDocument(template.getTemplateId(), fileName, filePath, format);
                logger.info("Generated document: {}", filePath);
            }
            
        } catch (IOException e) {
            logger.error("Error generating document", e);
            return null;
        }
        
        return filePath;
    }
    
    /**
     * Replace placeholders in template with actual values
     */
    private String replacePlaceholders(String template, Map<String, String> values) {
        String result = template;
        
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue() : "";
            result = result.replace(placeholder, value);
        }
        
        return result;
    }
    
    /**
     * Generate DOCX document
     */
    private String generateDOCX(String templateName, String content) throws IOException {
        String fileName = generateFileName(templateName, "docx");
        String filePath = OUTPUT_DIR + fileName;
        
        try (XWPFDocument document = new XWPFDocument();
             FileOutputStream out = new FileOutputStream(filePath)) {
            
            // Split content into paragraphs
            String[] paragraphs = content.split("\n");
            
            for (String para : paragraphs) {
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText(para);
                run.setFontSize(12);
                run.setFontFamily("Arial");
            }
            
            document.write(out);
            logger.info("Generated DOCX: {}", filePath);
        }
        
        return filePath;
    }
    
    /**
     * Generate PDF document
     */
    private String generatePDF(String templateName, String content) throws IOException {
        String fileName = generateFileName(templateName, "pdf");
        String filePath = OUTPUT_DIR + fileName;
        
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 750);
                
                // Split content into lines and handle page breaks
                String[] lines = content.split("\n");
                float yPosition = 750;
                float leading = 14.5f;
                
                for (String line : lines) {
                    // Check if we need a new page
                    if (yPosition < 50) {
                        contentStream.endText();
                        contentStream.close();
                        
                        page = new PDPage();
                        document.addPage(page);
                        PDPageContentStream newStream = new PDPageContentStream(document, page);
                        newStream.setFont(PDType1Font.HELVETICA, 12);
                        newStream.beginText();
                        newStream.newLineAtOffset(50, 750);
                        yPosition = 750;
                    }
                    
                    // Handle long lines (wrap text)
                    if (line.length() > 80) {
                        String[] wrappedLines = wrapText(line, 80);
                        for (String wrappedLine : wrappedLines) {
                            contentStream.showText(wrappedLine);
                            contentStream.newLineAtOffset(0, -leading);
                            yPosition -= leading;
                        }
                    } else {
                        contentStream.showText(line);
                        contentStream.newLineAtOffset(0, -leading);
                        yPosition -= leading;
                    }
                }
                
                contentStream.endText();
            }
            
            document.save(filePath);
            logger.info("Generated PDF: {}", filePath);
        }
        
        return filePath;
    }
    
    /**
     * Wrap text to specified width
     */
    private String[] wrapText(String text, int width) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        java.util.List<String> lines = new java.util.ArrayList<>();
        
        for (String word : words) {
            if (line.length() + word.length() + 1 > width) {
                lines.add(line.toString());
                line = new StringBuilder(word);
            } else {
                if (line.length() > 0) {
                    line.append(" ");
                }
                line.append(word);
            }
        }
        
        if (line.length() > 0) {
            lines.add(line.toString());
        }
        
        return lines.toArray(new String[0]);
    }
    
    /**
     * Generate unique file name
     */
    private String generateFileName(String templateName, String extension) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String safeName = templateName.replaceAll("[^a-zA-Z0-9]", "_");
        return safeName + "_" + timestamp + "." + extension;
    }
    
    /**
     * Get output directory path
     */
    public String getOutputDirectory() {
        return OUTPUT_DIR;
    }
}

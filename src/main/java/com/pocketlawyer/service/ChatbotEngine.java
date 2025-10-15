package com.pocketlawyer.service;

import com.pocketlawyer.dao.LegalQADAO;
import com.pocketlawyer.model.LegalQA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AI Chatbot Engine with intelligent keyword matching and intent recognition
 * Uses hybrid approach: rule-based + keyword similarity matching
 */
public class ChatbotEngine {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatbotEngine.class);
    private final LegalQADAO qaDAO;
    private List<LegalQA> knowledgeBase;
    
    // Common stop words to ignore during matching
    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        "a", "an", "the", "is", "are", "was", "were", "be", "been", "being",
        "have", "has", "had", "do", "does", "did", "will", "would", "should",
        "could", "may", "might", "can", "i", "you", "he", "she", "it", "we",
        "they", "what", "which", "who", "when", "where", "why", "how", "my",
        "your", "his", "her", "its", "our", "their", "this", "that", "these",
        "those", "am", "to", "of", "in", "for", "on", "with", "as", "by", "at"
    ));
    
    public ChatbotEngine() {
        this.qaDAO = new LegalQADAO();
        loadKnowledgeBase();
    }
    
    /**
     * Load knowledge base from database
     */
    private void loadKnowledgeBase() {
        knowledgeBase = qaDAO.getAllQA();
        logger.info("Loaded {} Q&A pairs into knowledge base", knowledgeBase.size());
    }
    
    /**
     * Reload knowledge base (useful after updates)
     */
    public void reloadKnowledgeBase() {
        loadKnowledgeBase();
    }
    
    /**
     * Get response for user query using AI matching
     */
    public String getResponse(String userQuery) {
        if (userQuery == null || userQuery.trim().isEmpty()) {
            return "Please ask me a legal question, and I'll do my best to help you.";
        }
        
        // Normalize query
        String normalizedQuery = normalizeText(userQuery);
        
        // Extract keywords from query
        Set<String> queryKeywords = extractKeywords(normalizedQuery);
        
        if (queryKeywords.isEmpty()) {
            return "I didn't quite understand that. Could you please rephrase your question?";
        }
        
        // Find best matching Q&A
        LegalQA bestMatch = findBestMatch(normalizedQuery, queryKeywords);
        
        if (bestMatch != null) {
            logger.info("Matched query '{}' to Q&A: {}", userQuery, bestMatch.getQuestion());
            return bestMatch.getAnswer();
        }
        
        // No match found - provide helpful fallback
        return getFallbackResponse(queryKeywords);
    }
    
    /**
     * Find best matching Q&A using hybrid scoring
     */
    private LegalQA findBestMatch(String query, Set<String> queryKeywords) {
        double bestScore = 0.0;
        LegalQA bestMatch = null;
        final double THRESHOLD = 0.3; // Minimum similarity threshold
        
        for (LegalQA qa : knowledgeBase) {
            double score = calculateSimilarityScore(query, queryKeywords, qa);
            
            if (score > bestScore && score >= THRESHOLD) {
                bestScore = score;
                bestMatch = qa;
            }
        }
        
        logger.debug("Best match score: {}", bestScore);
        return bestMatch;
    }
    
    /**
     * Calculate similarity score between query and Q&A
     * Uses multiple factors: keyword overlap, question similarity, priority
     */
    private double calculateSimilarityScore(String query, Set<String> queryKeywords, LegalQA qa) {
        // Factor 1: Keyword overlap with stored keywords
        Set<String> qaKeywords = extractKeywords(qa.getKeywords());
        double keywordScore = calculateJaccardSimilarity(queryKeywords, qaKeywords);
        
        // Factor 2: Question text similarity
        Set<String> questionKeywords = extractKeywords(normalizeText(qa.getQuestion()));
        double questionScore = calculateJaccardSimilarity(queryKeywords, questionKeywords);
        
        // Factor 3: Direct substring matching (bonus for exact phrase matches)
        double substringScore = 0.0;
        String normalizedQuestion = normalizeText(qa.getQuestion());
        if (normalizedQuestion.contains(query) || query.contains(normalizedQuestion)) {
            substringScore = 0.3;
        }
        
        // Factor 4: Priority boost (higher priority = slight boost)
        double priorityBoost = qa.getPriority() * 0.01;
        
        // Weighted combination
        double finalScore = (keywordScore * 0.4) + 
                           (questionScore * 0.4) + 
                           (substringScore * 0.15) + 
                           (priorityBoost * 0.05);
        
        return finalScore;
    }
    
    /**
     * Calculate Jaccard similarity between two sets
     */
    private double calculateJaccardSimilarity(Set<String> set1, Set<String> set2) {
        if (set1.isEmpty() && set2.isEmpty()) {
            return 0.0;
        }
        
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        
        return union.isEmpty() ? 0.0 : (double) intersection.size() / union.size();
    }
    
    /**
     * Extract meaningful keywords from text
     */
    private Set<String> extractKeywords(String text) {
        if (text == null || text.isEmpty()) {
            return new HashSet<>();
        }
        
        return Arrays.stream(text.toLowerCase().split("[\\s,;.!?]+"))
                .filter(word -> word.length() > 2)
                .filter(word -> !STOP_WORDS.contains(word))
                .collect(Collectors.toSet());
    }
    
    /**
     * Normalize text for comparison
     */
    private String normalizeText(String text) {
        if (text == null) {
            return "";
        }
        return text.toLowerCase()
                   .replaceAll("[^a-z0-9\\s]", " ")
                   .replaceAll("\\s+", " ")
                   .trim();
    }
    
    /**
     * Provide fallback response when no match found
     */
    private String getFallbackResponse(Set<String> keywords) {
        // Try to identify category from keywords
        String suggestedCategory = identifyCategory(keywords);
        
        StringBuilder response = new StringBuilder();
        response.append("I don't have a specific answer for that question in my knowledge base. ");
        
        if (suggestedCategory != null) {
            response.append("However, this seems related to ").append(suggestedCategory).append(". ");
        }
        
        response.append("\n\nHere are some things I can help you with:\n");
        response.append("• Consumer rights and product issues\n");
        response.append("• Tenant and landlord disputes\n");
        response.append("• Employee rights and workplace issues\n");
        response.append("• Women's rights and safety\n");
        response.append("• Cyber crimes and online privacy\n");
        response.append("• General legal procedures\n\n");
        response.append("Try asking a more specific question, or explore the Legal Rights Hub for detailed information.");
        
        return response.toString();
    }
    
    /**
     * Identify likely category from keywords
     */
    private String identifyCategory(Set<String> keywords) {
        Map<String, Set<String>> categoryKeywords = new HashMap<>();
        
        categoryKeywords.put("Consumer Rights", new HashSet<>(Arrays.asList(
            "product", "defective", "refund", "warranty", "shop", "purchase", "consumer", "seller"
        )));
        
        categoryKeywords.put("Tenant Rights", new HashSet<>(Arrays.asList(
            "rent", "landlord", "tenant", "lease", "deposit", "eviction", "property", "housing"
        )));
        
        categoryKeywords.put("Employee Rights", new HashSet<>(Arrays.asList(
            "employee", "employer", "salary", "wage", "job", "work", "office", "termination", "leave"
        )));
        
        categoryKeywords.put("Women's Rights", new HashSet<>(Arrays.asList(
            "women", "harassment", "domestic", "violence", "safety", "maternity", "discrimination"
        )));
        
        categoryKeywords.put("Cyber Rights", new HashSet<>(Arrays.asList(
            "cyber", "online", "hack", "password", "social", "media", "internet", "digital", "privacy"
        )));
        
        // Find category with most keyword matches
        String bestCategory = null;
        int maxMatches = 0;
        
        for (Map.Entry<String, Set<String>> entry : categoryKeywords.entrySet()) {
            Set<String> intersection = new HashSet<>(keywords);
            intersection.retainAll(entry.getValue());
            
            if (intersection.size() > maxMatches) {
                maxMatches = intersection.size();
                bestCategory = entry.getKey();
            }
        }
        
        return maxMatches > 0 ? bestCategory : null;
    }
    
    /**
     * Get suggested questions for a category
     */
    public List<String> getSuggestedQuestions(int categoryId, int limit) {
        List<LegalQA> categoryQA = qaDAO.getQAByCategory(categoryId);
        return categoryQA.stream()
                .limit(limit)
                .map(LegalQA::getQuestion)
                .collect(Collectors.toList());
    }
    
    /**
     * Get random suggested questions
     */
    public List<String> getRandomSuggestions(int count) {
        if (knowledgeBase.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<LegalQA> shuffled = new ArrayList<>(knowledgeBase);
        Collections.shuffle(shuffled);
        
        return shuffled.stream()
                .limit(count)
                .map(LegalQA::getQuestion)
                .collect(Collectors.toList());
    }
}

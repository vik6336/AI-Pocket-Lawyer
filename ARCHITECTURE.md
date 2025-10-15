# AI Pocket Lawyer - System Architecture

## 🏗️ Architecture Overview

The application follows a **layered architecture** pattern with clear separation of concerns.

```
┌─────────────────────────────────────────────────────────────┐
│                     USER INTERFACE LAYER                     │
│                      (Java Swing)                            │
├─────────────────────────────────────────────────────────────┤
│  MainFrame.java                                             │
│  ├── ChatbotPanel.java         (Legal Q&A Interface)        │
│  ├── DocumentGeneratorPanel.java (Document Creation UI)     │
│  └── LegalRightsPanel.java     (Rights Browser)            │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                   BUSINESS LOGIC LAYER                       │
│                      (Services)                              │
├─────────────────────────────────────────────────────────────┤
│  ChatbotEngine.java                                         │
│  ├── Keyword Extraction                                     │
│  ├── Similarity Matching (Jaccard Algorithm)               │
│  ├── Intent Recognition                                     │
│  └── Response Generation                                    │
│                                                             │
│  DocumentGenerator.java                                     │
│  ├── Template Processing                                    │
│  ├── PDF Generation (Apache PDFBox)                        │
│  └── DOCX Generation (Apache POI)                          │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                  DATA ACCESS LAYER (DAO)                     │
│                      (JDBC)                                  │
├─────────────────────────────────────────────────────────────┤
│  CategoryDAO.java          - Category operations            │
│  LegalQADAO.java           - Q&A CRUD operations           │
│  LegalRightsDAO.java       - Rights CRUD operations        │
│  DocumentTemplateDAO.java  - Template operations           │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                   DATABASE LAYER                             │
│                  (MySQL + JDBC)                              │
├─────────────────────────────────────────────────────────────┤
│  DatabaseManager.java                                       │
│  ├── Connection Management                                  │
│  ├── Connection Pooling                                     │
│  └── Transaction Handling                                   │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                      MySQL DATABASE                          │
├─────────────────────────────────────────────────────────────┤
│  Tables:                                                    │
│  • legal_categories                                         │
│  • legal_qa                                                 │
│  • legal_rights                                             │
│  • document_templates                                       │
│  • chat_history                                             │
│  • generated_documents                                      │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔄 Data Flow Diagrams

### 1. Chatbot Query Flow

```
User Types Question
        ↓
ChatbotPanel captures input
        ↓
ChatbotEngine.getResponse()
        ↓
┌───────────────────────────────┐
│ 1. Normalize query text       │
│ 2. Extract keywords            │
│ 3. Remove stop words           │
└───────────────────────────────┘
        ↓
LegalQADAO.getAllQA()
        ↓
DatabaseManager.getConnection()
        ↓
MySQL: SELECT * FROM legal_qa
        ↓
Return List<LegalQA>
        ↓
┌───────────────────────────────┐
│ For each Q&A pair:            │
│ 1. Calculate keyword match    │
│ 2. Calculate question match   │
│ 3. Calculate substring match  │
│ 4. Apply priority boost       │
│ 5. Compute final score        │
└───────────────────────────────┘
        ↓
Find best match (score > 0.3)
        ↓
Return answer OR fallback
        ↓
ChatbotPanel displays response
        ↓
User sees answer
```

### 2. Document Generation Flow

```
User Selects Template
        ↓
DocumentGeneratorPanel loads template
        ↓
DocumentTemplateDAO.getTemplateByName()
        ↓
MySQL: SELECT * FROM document_templates
        ↓
Parse fields JSON
        ↓
Display input fields dynamically
        ↓
User fills in fields
        ↓
User clicks "Generate"
        ↓
DocumentGenerator.generateDocument()
        ↓
┌───────────────────────────────┐
│ 1. Replace placeholders       │
│ 2. Choose format (PDF/DOCX)   │
│ 3. Generate file              │
└───────────────────────────────┘
        ↓
IF PDF:
  Apache PDFBox
  ├── Create PDDocument
  ├── Add pages
  ├── Write text with wrapping
  └── Save to file
        ↓
IF DOCX:
  Apache POI
  ├── Create XWPFDocument
  ├── Add paragraphs
  ├── Format text
  └── Save to file
        ↓
DocumentTemplateDAO.saveGeneratedDocument()
        ↓
MySQL: INSERT INTO generated_documents
        ↓
Return file path
        ↓
Show success dialog
        ↓
Open file location (optional)
```

### 3. Legal Rights Search Flow

```
User enters search term
        ↓
LegalRightsPanel.performSearch()
        ↓
LegalRightsDAO.searchRights(term)
        ↓
MySQL: SELECT * FROM legal_rights 
       WHERE title LIKE '%term%' 
       OR description LIKE '%term%'
       OR details LIKE '%term%'
        ↓
Return List<LegalRight>
        ↓
Update UI list
        ↓
User selects a right
        ↓
Display detailed information
        ↓
User reads content
```

---

## 🧩 Component Interaction

### Chatbot Component

```
┌─────────────────────────────────────────┐
│         ChatbotPanel (UI)               │
│  • Input field                          │
│  • Send button                          │
│  • Chat display area                    │
│  • Quick question buttons               │
└────────────┬────────────────────────────┘
             │
             ↓
┌─────────────────────────────────────────┐
│      ChatbotEngine (Service)            │
│  • getResponse(query)                   │
│  • extractKeywords()                    │
│  • calculateSimilarity()                │
│  • findBestMatch()                      │
│  • identifyCategory()                   │
└────────────┬────────────────────────────┘
             │
             ↓
┌─────────────────────────────────────────┐
│       LegalQADAO (Data Access)          │
│  • getAllQA()                           │
│  • searchByKeywords()                   │
│  • getQAByCategory()                    │
└────────────┬────────────────────────────┘
             │
             ↓
┌─────────────────────────────────────────┐
│      MySQL Database                     │
│  Table: legal_qa                        │
│  • qa_id                                │
│  • question                             │
│  • answer                               │
│  • keywords                             │
│  • priority                             │
└─────────────────────────────────────────┘
```

### Document Generator Component

```
┌─────────────────────────────────────────┐
│   DocumentGeneratorPanel (UI)           │
│  • Template dropdown                    │
│  • Dynamic field inputs                 │
│  • Format selector                      │
│  • Preview area                         │
│  • Generate button                      │
└────────────┬────────────────────────────┘
             │
             ↓
┌─────────────────────────────────────────┐
│   DocumentGenerator (Service)           │
│  • generateDocument()                   │
│  • replacePlaceholders()                │
│  • generatePDF()                        │
│  • generateDOCX()                       │
└────────────┬────────────────────────────┘
             │
             ↓
┌─────────────────────────────────────────┐
│  DocumentTemplateDAO (Data Access)      │
│  • getAllTemplates()                    │
│  • getTemplateByName()                  │
│  • saveGeneratedDocument()              │
└────────────┬────────────────────────────┘
             │
             ↓
┌─────────────────────────────────────────┐
│      MySQL Database                     │
│  Table: document_templates              │
│  • template_id                          │
│  • template_name                        │
│  • template_content                     │
│  • fields_json                          │
└─────────────────────────────────────────┘
```

---

## 🎯 Design Patterns

### 1. Singleton Pattern
**Used in**: `DatabaseManager`

```java
public class DatabaseManager {
    private static DatabaseManager instance;
    
    private DatabaseManager() { }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
}
```

**Purpose**: Ensure only one database connection manager exists.

### 2. Data Access Object (DAO) Pattern
**Used in**: All DAO classes

```
Interface/Abstract: DAO operations
    ↓
Concrete Implementation: LegalQADAO, LegalRightsDAO, etc.
    ↓
Database: MySQL tables
```

**Purpose**: Separate data access logic from business logic.

### 3. Model-View-Controller (MVC)
**Structure**:
- **Model**: `LegalQA`, `LegalRight`, `DocumentTemplate`, etc.
- **View**: `ChatbotPanel`, `DocumentGeneratorPanel`, `LegalRightsPanel`
- **Controller**: `ChatbotEngine`, `DocumentGenerator`

**Purpose**: Separation of concerns, maintainability.

### 4. Factory Pattern
**Used in**: Document generation

```java
if ("PDF".equals(format)) {
    return generatePDF(content);
} else if ("DOCX".equals(format)) {
    return generateDOCX(content);
}
```

**Purpose**: Create different document types based on format.

### 5. Template Method Pattern
**Used in**: Document templates

```
Template with placeholders: {{FIELD_NAME}}
    ↓
Fill in values
    ↓
Generate final document
```

**Purpose**: Define document structure, fill in specifics.

---

## 🔐 Security Architecture

### Input Validation Layer

```
User Input
    ↓
┌─────────────────────────────┐
│ Sanitization                │
│ • Trim whitespace           │
│ • Remove special chars      │
│ • Validate length           │
└─────────────────────────────┘
    ↓
Business Logic
```

### Database Security

```
Application
    ↓
┌─────────────────────────────┐
│ Prepared Statements         │
│ • Parameterized queries     │
│ • No string concatenation   │
│ • SQL injection prevention  │
└─────────────────────────────┘
    ↓
MySQL Database
```

### Data Privacy

```
User Data
    ↓
┌─────────────────────────────┐
│ Local Storage Only          │
│ • No cloud transmission     │
│ • No external APIs          │
│ • No telemetry              │
└─────────────────────────────┘
    ↓
User's Machine
```

---

## ⚡ Performance Optimization

### Database Optimization

```
Query Request
    ↓
┌─────────────────────────────┐
│ Indexed Columns             │
│ • keywords (legal_qa)       │
│ • title (legal_rights)      │
│ • category_id (all tables)  │
└─────────────────────────────┘
    ↓
Fast Retrieval
```

### UI Responsiveness

```
User Action
    ↓
┌─────────────────────────────┐
│ SwingWorker                 │
│ • Background thread         │
│ • Non-blocking UI           │
│ • Progress indication       │
└─────────────────────────────┘
    ↓
Smooth Experience
```

### Caching Strategy

```
Application Start
    ↓
Load knowledge base into memory
    ↓
┌─────────────────────────────┐
│ In-Memory Cache             │
│ • All Q&A pairs             │
│ • Fast lookups              │
│ • Reload on demand          │
└─────────────────────────────┘
    ↓
Instant Responses
```

---

## 🔄 State Management

### Application State

```
┌─────────────────────────────────────────┐
│         Application State               │
├─────────────────────────────────────────┤
│ • Current tab                           │
│ • Selected template                     │
│ • Selected category                     │
│ • Chat history (session)                │
│ • Database connection status            │
└─────────────────────────────────────────┘
```

### Session Management

```
User Opens App
    ↓
Generate Session ID
    ↓
Store in chat_history table
    ↓
Track conversation
    ↓
User Closes App
    ↓
Session ends (data persists)
```

---

## 📦 Dependency Management

### Maven Dependency Tree

```
ai-pocket-lawyer
├── mysql-connector-java (8.0.33)
│   └── protobuf-java
├── poi-ooxml (5.2.3)
│   ├── poi
│   ├── poi-ooxml-schemas
│   ├── xmlbeans
│   └── commons-compress
├── pdfbox (2.0.29)
│   ├── fontbox
│   └── commons-logging
├── gson (2.10.1)
└── slf4j-simple (2.0.7)
    └── slf4j-api
```

---

## 🚀 Deployment Architecture

### Standalone Desktop Application

```
┌─────────────────────────────────────────┐
│         User's Machine                  │
├─────────────────────────────────────────┤
│                                         │
│  ┌───────────────────────────────┐     │
│  │   Java Runtime (JRE 11+)      │     │
│  └───────────────────────────────┘     │
│                                         │
│  ┌───────────────────────────────┐     │
│  │   AI Pocket Lawyer JAR        │     │
│  │   (with dependencies)         │     │
│  └───────────────────────────────┘     │
│                                         │
│  ┌───────────────────────────────┐     │
│  │   MySQL Server                │     │
│  │   (localhost:3306)            │     │
│  └───────────────────────────────┘     │
│                                         │
│  ┌───────────────────────────────┐     │
│  │   Generated Documents         │     │
│  │   ~/PocketLawyer/Documents/   │     │
│  └───────────────────────────────┘     │
│                                         │
└─────────────────────────────────────────┘
```

---

## 🧪 Testing Architecture

### Recommended Testing Strategy

```
┌─────────────────────────────────────────┐
│         Unit Tests                      │
│  • DAO methods                          │
│  • Service logic                        │
│  • Utility functions                    │
└─────────────────────────────────────────┘
            ↓
┌─────────────────────────────────────────┐
│      Integration Tests                  │
│  • Database operations                  │
│  • Service + DAO interaction            │
│  • Document generation                  │
└─────────────────────────────────────────┘
            ↓
┌─────────────────────────────────────────┐
│         UI Tests                        │
│  • Panel rendering                      │
│  • User interactions                    │
│  • Event handling                       │
└─────────────────────────────────────────┘
            ↓
┌─────────────────────────────────────────┐
│      End-to-End Tests                   │
│  • Complete user workflows              │
│  • Database to UI                       │
│  • Document generation flow             │
└─────────────────────────────────────────┘
```

---

## 📊 Scalability Considerations

### Current Architecture
- **Users**: Single user per instance
- **Data**: Hundreds of Q&A pairs
- **Performance**: Sub-second responses

### Future Scalability
- **Multi-user**: Add user authentication
- **Cloud Database**: MySQL → Cloud SQL
- **API Layer**: REST API for mobile apps
- **Microservices**: Separate chatbot, doc gen services

---

## 🎯 Architecture Benefits

### Maintainability
✅ Clear separation of concerns  
✅ Modular components  
✅ Easy to locate and fix bugs  
✅ Well-documented code  

### Extensibility
✅ Easy to add new features  
✅ Plugin-ready architecture  
✅ Database-driven content  
✅ Configurable components  

### Testability
✅ Independent layers  
✅ Mock-friendly design  
✅ Isolated business logic  
✅ DAO abstraction  

### Performance
✅ Efficient database queries  
✅ In-memory caching  
✅ Async UI operations  
✅ Optimized algorithms  

---

**This architecture ensures the AI Pocket Lawyer is robust, maintainable, and ready for future enhancements.**

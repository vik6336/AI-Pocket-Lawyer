# AI Pocket Lawyer 🤖⚖️

An AI-powered desktop application that provides accessible legal assistance to common users. Built with Java Swing, JDBC, and MySQL.

## 📋 Overview

AI Pocket Lawyer is a comprehensive legal assistance platform designed to bridge the gap between everyday users and professional legal help. It offers:

- **🤖 AI Legal Chatbot**: Intelligent Q&A system for instant legal guidance
- **📄 Document Generator**: Create legal documents (rental agreements, wills, complaint letters)
- **⚖️ Legal Rights Hub**: Searchable knowledge base of legal rights by category

## ✨ Features

### 1. Legal Q&A Chatbot
- AI-powered responses using hybrid matching algorithm
- Keyword-based intent recognition
- Covers multiple legal categories:
  - Consumer Rights
  - Tenant Rights
  - Employee Rights
  - Women's Rights
  - Cyber Rights
  - General Legal Matters

### 2. Document Generator
- Pre-built templates for common legal documents
- Form-based input for easy document creation
- Export to PDF and DOCX formats
- Templates included:
  - Rental Agreement
  - Complaint Letter
  - Simple Will

### 3. Legal Rights Hub
- Browse rights by category
- Search functionality
- Detailed explanations with legal sources
- Easy-to-understand language

## 🛠️ Technology Stack

- **Language**: Java 11+
- **UI Framework**: Java Swing
- **Database**: MySQL 8.0+
- **Database Connectivity**: JDBC
- **Build Tool**: Maven
- **Libraries**:
  - Apache POI (DOCX generation)
  - Apache PDFBox (PDF generation)
  - Gson (JSON processing)
  - SLF4J (Logging)

## 📦 Prerequisites

Before running the application, ensure you have:

1. **Java Development Kit (JDK) 11 or higher**
   ```bash
   java -version
   ```

2. **Apache Maven**
   ```bash
   mvn -version
   ```

3. **MySQL Server 8.0+**
   ```bash
   mysql --version
   ```

## 🚀 Installation & Setup

### Step 1: Clone or Download the Project

```bash
cd /Users/vikramkhanna/Documents/vikram/Programming/PocketLawyer
```

### Step 2: Set Up MySQL Database

1. **Start MySQL Server**
   ```bash
   # On macOS
   brew services start mysql
   
   # Or manually
   mysql.server start
   ```

2. **Create Database and Import Schema**
   ```bash
   mysql -u root -p < database/schema.sql
   ```
   
   This will:
   - Create the `pocket_lawyer` database
   - Create all required tables
   - Insert sample legal data (Q&A, rights, templates)

3. **Update Database Credentials** (if needed)
   
   Edit `src/main/java/com/pocketlawyer/config/DatabaseConfig.java`:
   ```java
   public static final String DB_USER = "root";
   public static final String DB_PASSWORD = "your_password";
   ```

### Step 3: Build the Project

```bash
mvn clean install
```

This will:
- Download all dependencies
- Compile the source code
- Run tests (if any)
- Package the application

### Step 4: Run the Application

**Option 1: Using Maven**
```bash
mvn exec:java -Dexec.mainClass="com.pocketlawyer.Main"
```

**Option 2: Using Java directly**
```bash
java -cp target/ai-pocket-lawyer-1.0.0.jar com.pocketlawyer.Main
```

**Option 3: From IDE**
- Open the project in IntelliJ IDEA or Eclipse
- Run `com.pocketlawyer.Main` class

## 📁 Project Structure

```
PocketLawyer/
├── database/
│   └── schema.sql                 # Database schema and sample data
├── src/main/java/com/pocketlawyer/
│   ├── Main.java                  # Application entry point
│   ├── config/
│   │   └── DatabaseConfig.java    # Database configuration
│   ├── database/
│   │   └── DatabaseManager.java   # JDBC connection manager
│   ├── dao/                       # Data Access Objects
│   │   ├── CategoryDAO.java
│   │   ├── LegalQADAO.java
│   │   ├── LegalRightsDAO.java
│   │   └── DocumentTemplateDAO.java
│   ├── model/                     # Data models
│   │   ├── LegalCategory.java
│   │   ├── LegalQA.java
│   │   ├── LegalRight.java
│   │   └── DocumentTemplate.java
│   ├── service/                   # Business logic
│   │   ├── ChatbotEngine.java     # AI chatbot logic
│   │   └── DocumentGenerator.java # Document generation
│   └── ui/                        # User interface
│       ├── MainFrame.java
│       ├── ChatbotPanel.java
│       ├── DocumentGeneratorPanel.java
│       └── LegalRightsPanel.java
├── pom.xml                        # Maven configuration
└── README.md                      # This file
```

## 🤖 AI Implementation Details

### Hybrid AI Approach

The chatbot uses a **3-tier intelligent matching system**:

1. **Keyword Extraction**
   - Removes stop words
   - Extracts meaningful keywords from user queries

2. **Similarity Scoring**
   - Jaccard similarity for keyword overlap
   - Question text similarity matching
   - Substring matching for exact phrases
   - Priority-based boosting

3. **Intent Recognition**
   - Category identification from keywords
   - Fallback responses with suggestions
   - Context-aware recommendations

### Advantages
- ✅ **No internet required** - Works completely offline
- ✅ **No API costs** - No external AI service fees
- ✅ **Fast responses** - Instant answers from local database
- ✅ **Privacy-focused** - All data stays local
- ✅ **Customizable** - Easy to add more Q&A pairs
- ✅ **Scalable** - Can be extended with external AI APIs later

## 📊 Database Schema

### Tables

1. **legal_categories** - Legal topic categories
2. **legal_qa** - Question-answer pairs for chatbot
3. **legal_rights** - Legal rights information
4. **document_templates** - Document templates
5. **chat_history** - User conversation history
6. **generated_documents** - Generated document records

## 🎯 Usage Guide

### Using the Chatbot

1. Navigate to the "Legal Chatbot" tab
2. Type your legal question in the input field
3. Press Enter or click "Send"
4. The AI will provide an instant response
5. Use "Quick Questions" for common queries

**Example Questions:**
- "What are my rights if I receive a defective product?"
- "Can my landlord increase rent without notice?"
- "Am I entitled to paid leave?"

### Generating Documents

1. Go to "Document Generator" tab
2. Select a template from the dropdown
3. Fill in all required fields
4. Choose output format (PDF or DOCX)
5. Click "Generate Document"
6. Document will be saved to `~/PocketLawyer/Documents/`

### Browsing Legal Rights

1. Open "Legal Rights Hub" tab
2. Select a category or use "Search"
3. Click on any right to view details
4. Read comprehensive information with legal sources

## 🔧 Configuration

### Database Configuration

Edit `src/main/java/com/pocketlawyer/config/DatabaseConfig.java`:

```java
public static final String DB_URL = "jdbc:mysql://localhost:3306/pocket_lawyer";
public static final String DB_USER = "root";
public static final String DB_PASSWORD = "your_password";
```

### Output Directory

Generated documents are saved to:
```
~/PocketLawyer/Documents/
```

To change this, edit `DocumentGenerator.java`:
```java
private static final String OUTPUT_DIR = "your/custom/path/";
```

## 🐛 Troubleshooting

### Database Connection Error

**Problem**: "Could not connect to the database"

**Solutions**:
1. Ensure MySQL is running: `mysql.server status`
2. Check credentials in `DatabaseConfig.java`
3. Verify database exists: `mysql -u root -p -e "SHOW DATABASES;"`
4. Re-run schema: `mysql -u root -p < database/schema.sql`

### Maven Build Errors

**Problem**: Dependencies not downloading

**Solution**:
```bash
mvn clean install -U
```

### PDF Generation Issues

**Problem**: PDF not generating properly

**Solution**: Ensure Apache PDFBox dependency is correctly loaded. Check `pom.xml`.

### UI Not Displaying Correctly

**Problem**: Blank or malformed UI

**Solution**:
1. Update Java to latest version
2. Try different look and feel in `MainFrame.java`

## 📈 Future Enhancements

Potential improvements for future versions:

- [ ] Integration with OpenAI/Gemini API for advanced queries
- [ ] Multi-language support
- [ ] Voice input/output
- [ ] More document templates
- [ ] Case law search
- [ ] Lawyer directory integration
- [ ] Mobile app version
- [ ] Cloud sync for chat history

## 🤝 Contributing

To add more legal content:

### Adding Q&A Pairs

```sql
INSERT INTO legal_qa (category_id, question, answer, keywords, priority) 
VALUES (1, 'Your question?', 'Your answer', 'keyword1,keyword2', 8);
```

### Adding Legal Rights

```sql
INSERT INTO legal_rights (category_id, title, description, details, source) 
VALUES (1, 'Right Title', 'Brief description', 'Detailed info', 'Legal Act');
```

### Adding Document Templates

1. Create template content with placeholders: `{{FIELD_NAME}}`
2. Insert into database with fields JSON array
3. Restart application to load new template

## 📄 License

This project is created for educational purposes. Legal information provided is general guidance and should not replace professional legal advice.

## ⚠️ Disclaimer

**IMPORTANT**: This application provides general legal information only. It is NOT a substitute for professional legal advice. For specific legal issues, please consult a qualified lawyer.

## 👨‍💻 Developer

Created by Vikram Khanna

## 📞 Support

For issues or questions:
1. Check the troubleshooting section
2. Review application logs
3. Verify database connectivity
4. Ensure all dependencies are installed

---

**Made with ❤️ to make legal help accessible to everyone**

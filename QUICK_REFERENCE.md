# AI Pocket Lawyer - Quick Reference Card

## 🚀 Quick Commands

### Setup & Run
```bash
# Setup database
mysql -u root -p < database/schema.sql

# Build project
mvn clean install

# Run application
mvn exec:java -Dexec.mainClass="com.pocketlawyer.Main"
```

### Database Operations
```bash
# Start MySQL
brew services start mysql

# Access database
mysql -u root -p pocket_lawyer

# Backup database
mysqldump -u root -p pocket_lawyer > backup.sql

# Restore database
mysql -u root -p pocket_lawyer < backup.sql
```

---

## 📝 Common SQL Queries

### Add New Q&A
```sql
INSERT INTO legal_qa (category_id, question, answer, keywords, priority) 
VALUES (
    1,  -- Category: 1=Consumer, 2=Tenant, 3=Employee, 4=Women, 5=Cyber, 6=General
    'Your question here?',
    'Your detailed answer here',
    'keyword1,keyword2,keyword3',
    8  -- Priority: 1-10 (higher = more important)
);
```

### Add New Legal Right
```sql
INSERT INTO legal_rights (category_id, title, description, details, source) 
VALUES (
    1,
    'Right Title',
    'Brief description',
    'Detailed information with steps and examples',
    'Legal Act Name, Year'
);
```

### View All Q&A
```sql
SELECT q.question, q.answer, c.category_name 
FROM legal_qa q 
JOIN legal_categories c ON q.category_id = c.category_id
ORDER BY q.priority DESC;
```

### Search Q&A by Keyword
```sql
SELECT * FROM legal_qa 
WHERE keywords LIKE '%refund%' 
   OR question LIKE '%refund%'
ORDER BY priority DESC;
```

### View All Rights by Category
```sql
SELECT r.title, r.description, c.category_name 
FROM legal_rights r 
JOIN legal_categories c ON r.category_id = c.category_id
WHERE c.category_name = 'Consumer Rights';
```

---

## 🎯 File Locations

### Configuration
```
Database Config: src/main/java/com/pocketlawyer/config/DatabaseConfig.java
```

### Generated Documents
```
Default Location: ~/PocketLawyer/Documents/
```

### Logs
```
Console output (can be redirected to file)
```

### Database Schema
```
Schema File: database/schema.sql
```

---

## 🔧 Configuration Changes

### Change Database Password
**File**: `src/main/java/com/pocketlawyer/config/DatabaseConfig.java`
```java
public static final String DB_PASSWORD = "your_new_password";
```

### Change Database Name
```java
public static final String DB_URL = "jdbc:mysql://localhost:3306/your_db_name";
```

### Change Output Directory
**File**: `src/main/java/com/pocketlawyer/service/DocumentGenerator.java`
```java
private static final String OUTPUT_DIR = "/your/custom/path/";
```

---

## 🐛 Troubleshooting Quick Fixes

### Database Connection Failed
```bash
# Check MySQL status
mysql.server status

# Start MySQL
mysql.server start

# Test connection
mysql -u root -p -e "SHOW DATABASES;"
```

### Database Not Found
```bash
# Recreate database
mysql -u root -p < database/schema.sql

# Verify
mysql -u root -p -e "USE pocket_lawyer; SHOW TABLES;"
```

### Maven Build Failed
```bash
# Clean and rebuild
mvn clean install -U

# Skip tests (if needed)
mvn clean install -DskipTests
```

### Port 3306 In Use
```bash
# Find process using port
lsof -i :3306

# Kill process (replace PID)
kill -9 <PID>
```

---

## 📊 Database Schema Quick Reference

### Tables
| Table | Purpose | Key Columns |
|-------|---------|-------------|
| legal_categories | Legal domains | category_id, category_name |
| legal_qa | Q&A pairs | qa_id, question, answer, keywords |
| legal_rights | Rights info | right_id, title, description, details |
| document_templates | Doc templates | template_id, template_name, template_content |
| chat_history | Conversation log | chat_id, user_query, bot_response |
| generated_documents | Doc records | doc_id, file_name, file_path |

### Categories
| ID | Category Name |
|----|---------------|
| 1 | Consumer Rights |
| 2 | Tenant Rights |
| 3 | Employee Rights |
| 4 | Women Rights |
| 5 | Cyber Rights |
| 6 | General Legal |

---

## 🎨 UI Components

### Main Tabs
1. **Legal Chatbot** - AI Q&A interface
2. **Document Generator** - Create legal documents
3. **Legal Rights Hub** - Browse rights information

### Keyboard Shortcuts
- **Enter** - Send message in chatbot
- **Cmd/Ctrl + Q** - Quit application (standard)

---

## 📦 Maven Commands

```bash
# Clean build
mvn clean

# Compile only
mvn compile

# Package JAR
mvn package

# Install to local repo
mvn install

# Run application
mvn exec:java -Dexec.mainClass="com.pocketlawyer.Main"

# Generate project info
mvn site

# Show dependency tree
mvn dependency:tree
```

---

## 🔍 Useful MySQL Queries

### Count Records
```sql
SELECT 
    (SELECT COUNT(*) FROM legal_qa) as qa_count,
    (SELECT COUNT(*) FROM legal_rights) as rights_count,
    (SELECT COUNT(*) FROM document_templates) as template_count;
```

### View Recent Chat History
```sql
SELECT user_query, bot_response, created_at 
FROM chat_history 
ORDER BY created_at DESC 
LIMIT 10;
```

### View Generated Documents
```sql
SELECT file_name, format, created_at 
FROM generated_documents 
ORDER BY created_at DESC;
```

### Clear Chat History
```sql
DELETE FROM chat_history;
```

### Reset Database (Caution!)
```sql
DROP DATABASE pocket_lawyer;
SOURCE database/schema.sql;
```

---

## 🎯 Testing Checklist

### After Installation
- [ ] MySQL running
- [ ] Database created
- [ ] Application launches
- [ ] No error dialogs
- [ ] All tabs visible

### Feature Testing
- [ ] Chatbot responds to questions
- [ ] Quick questions work
- [ ] Templates load in dropdown
- [ ] Document fields appear
- [ ] PDF generation works
- [ ] DOCX generation works
- [ ] Rights categories load
- [ ] Search finds results
- [ ] Details display correctly

---

## 📞 Quick Help

### Error Messages

**"Could not connect to database"**
→ Check MySQL is running, verify credentials

**"Template not found"**
→ Ensure database has templates, re-run schema.sql

**"Error generating document"**
→ Check output directory exists and is writable

**"No results found"**
→ Database may be empty, import sample data

### Log Locations
Console output shows all errors and info messages

### Getting More Help
1. Check README.md
2. Review SETUP_GUIDE.md
3. See FEATURES.md for feature details
4. Check ARCHITECTURE.md for technical info

---

## 🔐 Security Notes

### Database Security
- Use strong MySQL password
- Don't commit credentials to Git
- Keep database local (no remote access)

### Application Security
- All data stays on your machine
- No external API calls
- No telemetry or tracking

---

## 📈 Performance Tips

### Database
- Keep Q&A count under 10,000 for best performance
- Use specific keywords for better matching
- Regularly clean old chat history

### Application
- Close unused tabs to save memory
- Restart app if it becomes slow
- Clear chat history periodically

---

## 🎓 Learning Resources

### Java Swing
- Oracle Java Swing Tutorial
- Java Swing Documentation

### JDBC
- JDBC Tutorial - Oracle
- MySQL Connector/J Documentation

### Maven
- Maven Getting Started Guide
- Maven POM Reference

### MySQL
- MySQL Documentation
- SQL Tutorial - W3Schools

---

## 💡 Pro Tips

### For Better Chatbot Results
1. Use specific legal terms in questions
2. Ask one question at a time
3. Include context (e.g., "tenant", "consumer")
4. Try rephrasing if no match found

### For Document Generation
1. Fill all fields for best results
2. Use preview before generating
3. Keep backup of important documents
4. Customize templates in database

### For Database Management
1. Backup before major changes
2. Use transactions for bulk inserts
3. Index frequently searched columns
4. Regular maintenance and cleanup

---

## 🚀 Quick Start Workflow

```
1. Start MySQL
   ↓
2. Import schema
   ↓
3. Build with Maven
   ↓
4. Run application
   ↓
5. Test chatbot
   ↓
6. Generate a document
   ↓
7. Browse legal rights
   ↓
8. Success! ✅
```

---

## 📋 Maintenance Schedule

### Daily
- Check application logs for errors

### Weekly
- Backup database
- Review generated documents

### Monthly
- Update legal content
- Add new Q&A pairs
- Clean old chat history

### Quarterly
- Update dependencies
- Review and improve AI responses
- Add new document templates

---

**Keep this reference handy for quick access to common operations!**

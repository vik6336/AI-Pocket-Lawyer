# AI Pocket Lawyer - Quick Setup Guide

## 🚀 Quick Start (5 Minutes)

Follow these steps to get the application running:

### 1️⃣ Prerequisites Check

Open Terminal and verify installations:

```bash
# Check Java (need 11+)
java -version

# Check Maven
mvn -version

# Check MySQL
mysql --version
```

**Don't have them?** Install using Homebrew:

```bash
# Install Homebrew (if not installed)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install Java
brew install openjdk@11

# Install Maven
brew install maven

# Install MySQL
brew install mysql
```

### 2️⃣ Start MySQL

```bash
# Start MySQL service
brew services start mysql

# Or start manually
mysql.server start
```

### 3️⃣ Set Up Database

```bash
# Navigate to project directory
cd /Users/vikramkhanna/Documents/vikram/Programming/PocketLawyer

# Import database schema (you'll be prompted for MySQL password)
mysql -u root -p < database/schema.sql
```

**First time using MySQL?** Set root password:

```bash
mysql -u root
```

Then in MySQL prompt:
```sql
ALTER USER 'root'@'localhost' IDENTIFIED BY 'your_password';
FLUSH PRIVILEGES;
EXIT;
```

### 4️⃣ Configure Database Credentials

Edit `src/main/java/com/pocketlawyer/config/DatabaseConfig.java`:

```java
public static final String DB_PASSWORD = "your_password"; // Update this line
```

### 5️⃣ Build the Project

```bash
mvn clean install
```

This will download dependencies and compile the code. First time may take 2-3 minutes.

### 6️⃣ Run the Application

```bash
mvn exec:java -Dexec.mainClass="com.pocketlawyer.Main"
```

**Or** open in IntelliJ IDEA and run `Main.java`

---

## ✅ Verify Installation

Once the application starts, you should see:

1. **Main Window** with three tabs
2. **Legal Chatbot** tab with welcome message
3. **Document Generator** with template dropdown
4. **Legal Rights Hub** with categories

### Test the Chatbot

Try asking:
- "What are my consumer rights?"
- "Can my landlord increase rent?"
- "How do I file an FIR?"

### Test Document Generator

1. Select "Rental Agreement"
2. Fill in the fields
3. Click "Generate Document"
4. Check `~/PocketLawyer/Documents/` folder

---

## 🐛 Common Issues & Fixes

### Issue 1: Database Connection Failed

**Error**: "Could not connect to the database"

**Fix**:
```bash
# Check if MySQL is running
mysql.server status

# If not running, start it
mysql.server start

# Test connection
mysql -u root -p -e "SHOW DATABASES;"
```

### Issue 2: Database 'pocket_lawyer' doesn't exist

**Fix**:
```bash
# Re-import schema
mysql -u root -p < database/schema.sql

# Verify
mysql -u root -p -e "USE pocket_lawyer; SHOW TABLES;"
```

### Issue 3: Maven build fails

**Fix**:
```bash
# Clean and rebuild
mvn clean install -U

# If still fails, delete .m2 cache
rm -rf ~/.m2/repository
mvn clean install
```

### Issue 4: Java version mismatch

**Error**: "Unsupported class file major version"

**Fix**:
```bash
# Check Java version
java -version

# Should be 11 or higher. If not:
brew install openjdk@11

# Set JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home -v 11)
```

### Issue 5: Port 3306 already in use

**Error**: MySQL can't start

**Fix**:
```bash
# Check what's using port 3306
lsof -i :3306

# Kill the process or change MySQL port in DatabaseConfig.java
```

---

## 📊 Database Verification

To verify the database is set up correctly:

```bash
mysql -u root -p
```

Then run:
```sql
USE pocket_lawyer;

-- Check tables
SHOW TABLES;

-- Check sample data
SELECT COUNT(*) FROM legal_qa;
SELECT COUNT(*) FROM legal_rights;
SELECT COUNT(*) FROM document_templates;

-- Should show:
-- legal_qa: 12 rows
-- legal_rights: 10 rows
-- document_templates: 3 rows

EXIT;
```

---

## 🎯 IDE Setup (Optional)

### IntelliJ IDEA

1. **Open Project**
   - File → Open → Select `PocketLawyer` folder
   - Wait for Maven import to complete

2. **Configure JDK**
   - File → Project Structure → Project
   - Set SDK to Java 11 or higher

3. **Run Application**
   - Navigate to `src/main/java/com/pocketlawyer/Main.java`
   - Right-click → Run 'Main.main()'

### Eclipse

1. **Import Project**
   - File → Import → Existing Maven Projects
   - Select `PocketLawyer` folder

2. **Update Project**
   - Right-click project → Maven → Update Project

3. **Run Application**
   - Right-click `Main.java` → Run As → Java Application

---

## 📝 Testing Checklist

After setup, test these features:

- [ ] Application launches without errors
- [ ] Database connection successful (no error dialog)
- [ ] Chatbot responds to questions
- [ ] Quick question buttons work
- [ ] Document templates load in dropdown
- [ ] Can fill document fields
- [ ] Can generate PDF document
- [ ] Can generate DOCX document
- [ ] Legal rights categories load
- [ ] Can view right details
- [ ] Search functionality works

---

## 🔄 Updating the Application

To add more legal content:

### Add Q&A Pairs

```bash
mysql -u root -p pocket_lawyer
```

```sql
INSERT INTO legal_qa (category_id, question, answer, keywords, priority) 
VALUES (
    1, 
    'Your legal question here?',
    'Detailed answer here',
    'keyword1,keyword2,keyword3',
    8
);
```

### Add Legal Rights

```sql
INSERT INTO legal_rights (category_id, title, description, details, source) 
VALUES (
    1,
    'Right to XYZ',
    'Brief description',
    'Detailed explanation with steps and examples',
    'Legal Act Name, Year'
);
```

After adding content, restart the application to load new data.

---

## 📞 Need Help?

1. **Check logs**: Look for error messages in the terminal
2. **Verify MySQL**: Ensure database is running and accessible
3. **Check credentials**: Verify username/password in `DatabaseConfig.java`
4. **Review README**: See `README.md` for detailed documentation

---

## 🎉 You're All Set!

The AI Pocket Lawyer is now ready to use. Enjoy exploring legal rights and generating documents!

**Remember**: This tool provides general legal information. Always consult a qualified lawyer for specific legal advice.

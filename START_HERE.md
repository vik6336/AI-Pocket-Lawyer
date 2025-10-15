# 🎯 START HERE - Quick Launch Guide

## ⚡ Fastest Way to Run the Application

### **Option 1: Automated Script (Recommended)**

Simply run this command in your terminal:

```bash
cd /Users/vikramkhanna/Documents/vikram/Programming/PocketLawyer
./run.sh
```

This will automatically:
1. ✅ Check all prerequisites
2. ✅ Install missing components
3. ✅ Start MySQL
4. ✅ Set up the database
5. ✅ Build the project
6. ✅ Launch the application

---

### **Option 2: Manual Step-by-Step**

If you prefer to do it manually or the script fails, follow these steps:

#### **Step 1: Install Prerequisites**

```bash
# Install Maven
brew install maven

# Install MySQL
brew install mysql
```

#### **Step 2: Start MySQL**

```bash
# Start MySQL service
brew services start mysql

# Verify it's running
brew services list | grep mysql
```

#### **Step 3: Set Up Database**

```bash
# Navigate to project directory
cd /Users/vikramkhanna/Documents/vikram/Programming/PocketLawyer

# Import database schema
mysql -u root -p < database/schema.sql
```

**Note**: If this is your first time using MySQL, just press Enter when prompted for password (no password set by default).

If you want to set a password:
```bash
mysql -u root
```
Then in MySQL:
```sql
ALTER USER 'root'@'localhost' IDENTIFIED BY 'your_password';
FLUSH PRIVILEGES;
EXIT;
```

Don't forget to update the password in:
`src/main/java/com/pocketlawyer/config/DatabaseConfig.java`

#### **Step 4: Build the Project**

```bash
mvn clean install
```

This will:
- Download all dependencies (first time may take 2-3 minutes)
- Compile all Java files
- Package the application

#### **Step 5: Run the Application**

```bash
mvn exec:java -Dexec.mainClass="com.pocketlawyer.Main"
```

**OR** if you prefer using the JAR file:

```bash
java -cp target/ai-pocket-lawyer-1.0.0.jar:target/lib/* com.pocketlawyer.Main
```

---

## 🎨 What You Should See

### **1. Application Window**

When the application starts, you'll see:

```
┌─────────────────────────────────────────────────────┐
│  AI Pocket Lawyer                                   │
│  Your AI-Powered Legal Assistant                   │
├─────────────────────────────────────────────────────┤
│  [🤖 Legal Chatbot] [📄 Document Generator]        │
│  [⚖️ Legal Rights Hub]                             │
│                                                     │
│  (Main content area with tabs)                     │
│                                                     │
└─────────────────────────────────────────────────────┘
```

### **2. Legal Chatbot Tab (Default)**

You'll see:
- Welcome message from the AI
- Text input field at the bottom
- "Quick Questions" buttons at the top
- Chat history area in the middle

**Try asking**: "What are my consumer rights?"

### **3. Document Generator Tab**

You'll see:
- Template dropdown (Rental Agreement, Complaint Letter, Simple Will)
- Dynamic form fields
- Preview area
- Generate button

**Try**: Select "Rental Agreement" and fill in the fields

### **4. Legal Rights Hub Tab**

You'll see:
- Category dropdown
- Search field
- List of rights on the left
- Details panel on the right

**Try**: Select "Consumer Rights" category

---

## ✅ Verification Checklist

After launching, verify these:

### **Backend (Database) is Running:**

```bash
# Check MySQL is running
brew services list | grep mysql
# Should show: mysql started

# Verify database exists
mysql -u root -p -e "SHOW DATABASES LIKE 'pocket_lawyer';"
# Should show: pocket_lawyer

# Check tables
mysql -u root -p -e "USE pocket_lawyer; SHOW TABLES;"
# Should show 6 tables
```

### **Frontend (UI) is Running:**

- [ ] Application window opens
- [ ] No error dialogs appear
- [ ] All 3 tabs are visible
- [ ] Chatbot shows welcome message
- [ ] Document templates load in dropdown
- [ ] Legal rights categories appear

### **Test Each Feature:**

**Chatbot:**
```
1. Type: "Can my landlord increase rent?"
2. Press Enter
3. You should get a detailed response
```

**Document Generator:**
```
1. Select "Complaint Letter"
2. Fill in a few fields
3. Click "Generate Document"
4. Check ~/PocketLawyer/Documents/ folder
```

**Legal Rights:**
```
1. Select "Employee Rights"
2. Click on any right
3. Details should appear on the right
```

---

## 🐛 Troubleshooting

### **Problem: "Command not found: mvn"**

**Solution:**
```bash
brew install maven
# Then verify
mvn -version
```

### **Problem: "Could not connect to database"**

**Solution:**
```bash
# Start MySQL
brew services start mysql

# Wait a few seconds, then retry
```

### **Problem: "Database 'pocket_lawyer' doesn't exist"**

**Solution:**
```bash
mysql -u root -p < database/schema.sql
```

### **Problem: "Access denied for user 'root'"**

**Solution:**

If you have a MySQL password, update it in:
`src/main/java/com/pocketlawyer/config/DatabaseConfig.java`

Change line:
```java
public static final String DB_PASSWORD = ""; // Change to your password
```

Then rebuild:
```bash
mvn clean install
```

### **Problem: Maven build fails**

**Solution:**
```bash
# Clean everything and rebuild
mvn clean install -U

# If still fails, delete Maven cache
rm -rf ~/.m2/repository
mvn clean install
```

### **Problem: Application window doesn't appear**

**Solution:**
```bash
# Check for errors in terminal
# Look for stack traces

# Common fix: Ensure JAVA_HOME is set
export JAVA_HOME=$(/usr/libexec/java_home)
```

---

## 📊 Verify Database Content

To see what's in your database:

```bash
mysql -u root -p pocket_lawyer
```

Then run:
```sql
-- Check Q&A count
SELECT COUNT(*) FROM legal_qa;
-- Should show: 12

-- Check rights count
SELECT COUNT(*) FROM legal_rights;
-- Should show: 10

-- Check templates count
SELECT COUNT(*) FROM document_templates;
-- Should show: 3

-- View a sample Q&A
SELECT question, answer FROM legal_qa LIMIT 1;

-- Exit
EXIT;
```

---

## 🎯 Quick Test Scenarios

### **Test 1: Chatbot Intelligence**

Ask these questions and verify you get relevant answers:
1. "What if I get a defective product?"
2. "My landlord won't return my deposit"
3. "Can I file an FIR online?"
4. "What are my rights as an employee?"

### **Test 2: Document Generation**

1. Go to Document Generator tab
2. Select "Rental Agreement"
3. Fill in these sample values:
   - Date: 2024-10-14
   - Landlord Name: John Doe
   - Tenant Name: Jane Smith
   - Rent Amount: $1500
4. Click "Generate Document" (PDF)
5. Check `~/PocketLawyer/Documents/` folder
6. Open the PDF and verify content

### **Test 3: Legal Rights Search**

1. Go to Legal Rights Hub
2. Type "privacy" in search box
3. Click Search
4. Should find "Right to Privacy" entries
5. Click on one to see details

---

## 🚀 Running from IDE (Alternative)

If you prefer using an IDE:

### **IntelliJ IDEA:**

1. Open IntelliJ IDEA
2. File → Open → Select `PocketLawyer` folder
3. Wait for Maven import to complete
4. Navigate to `src/main/java/com/pocketlawyer/Main.java`
5. Right-click → Run 'Main.main()'

### **Eclipse:**

1. Open Eclipse
2. File → Import → Existing Maven Projects
3. Select `PocketLawyer` folder
4. Right-click project → Maven → Update Project
5. Right-click `Main.java` → Run As → Java Application

### **VS Code:**

1. Open VS Code
2. Install "Extension Pack for Java"
3. Open `PocketLawyer` folder
4. Open `Main.java`
5. Click "Run" above the main method

---

## 📸 Expected Screenshots

### **Chatbot in Action:**
```
[You]: What are my consumer rights?

[AI Lawyer]: Under the Consumer Protection Act, you have the 
right to: 1) Return defective products, 2) Get refunds or 
replacements, 3) File complaints with consumer forums...
```

### **Document Generated:**
```
✓ Document generated successfully!

Location: /Users/vikramkhanna/PocketLawyer/Documents/
Rental_Agreement_1234567890.pdf

[Open Folder] [OK]
```

### **Legal Rights Display:**
```
Right to Information
═══════════════════════════════════════

OVERVIEW
─────────────────────────────────────
Consumers have the right to know about the quality, 
quantity, potency, purity, standard, and price of goods.

DETAILED INFORMATION
─────────────────────────────────────
The Consumer Protection Act ensures that consumers are 
protected from unfair trade practices...
```

---

## 🎉 Success Indicators

You'll know everything is working when:

✅ Application window opens without errors  
✅ Chatbot responds to your questions  
✅ Documents generate successfully  
✅ Legal rights load and display  
✅ Search functionality works  
✅ No error dialogs appear  

---

## 📞 Need Help?

If you encounter issues:

1. **Check Terminal Output**: Look for error messages
2. **Verify MySQL**: `brew services list | grep mysql`
3. **Check Logs**: Application prints logs to console
4. **Review Documentation**: See `README.md` and `SETUP_GUIDE.md`

---

## 🎓 Next Steps After Launch

Once the application is running:

1. **Explore the Chatbot**: Ask various legal questions
2. **Generate Documents**: Try all 3 templates
3. **Browse Rights**: Explore all 6 categories
4. **Add Custom Content**: Add your own Q&A pairs to the database
5. **Customize**: Modify templates or add new ones

---

**Ready to launch? Run `./run.sh` and let's get started!** 🚀

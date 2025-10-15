#!/bin/bash

# AI Pocket Lawyer - Run Script
# This script sets up and runs the application

echo "🚀 AI Pocket Lawyer - Setup and Run Script"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Step 1: Check Java
echo "📌 Step 1: Checking Java..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1)
    echo -e "${GREEN}✓ Java found: $JAVA_VERSION${NC}"
else
    echo -e "${RED}✗ Java not found. Please install Java 11 or higher.${NC}"
    exit 1
fi

# Step 2: Check Maven
echo ""
echo "📌 Step 2: Checking Maven..."
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version | head -n 1)
    echo -e "${GREEN}✓ Maven found: $MVN_VERSION${NC}"
else
    echo -e "${RED}✗ Maven not found. Installing...${NC}"
    brew install maven
fi

# Step 3: Check MySQL
echo ""
echo "📌 Step 3: Checking MySQL..."
if command -v mysql &> /dev/null; then
    MYSQL_VERSION=$(mysql --version)
    echo -e "${GREEN}✓ MySQL found: $MYSQL_VERSION${NC}"
else
    echo -e "${RED}✗ MySQL not found. Installing...${NC}"
    brew install mysql
fi

# Step 4: Start MySQL
echo ""
echo "📌 Step 4: Starting MySQL..."
brew services start mysql
sleep 3
echo -e "${GREEN}✓ MySQL service started${NC}"

# Step 5: Setup Database
echo ""
echo "📌 Step 5: Setting up database..."
echo -e "${YELLOW}You will be prompted for MySQL root password (press Enter if no password set)${NC}"
mysql -u root -p < database/schema.sql
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Database setup complete${NC}"
else
    echo -e "${RED}✗ Database setup failed. Please run manually:${NC}"
    echo "  mysql -u root -p < database/schema.sql"
fi

# Step 6: Build Project
echo ""
echo "📌 Step 6: Building project with Maven..."
mvn clean install
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Build successful${NC}"
else
    echo -e "${RED}✗ Build failed${NC}"
    exit 1
fi

# Step 7: Run Application
echo ""
echo "📌 Step 7: Launching AI Pocket Lawyer..."
echo -e "${GREEN}✓ Application starting...${NC}"
echo ""
mvn exec:java -Dexec.mainClass="com.pocketlawyer.Main"

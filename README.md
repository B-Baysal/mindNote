# MindNote - Note Application Backend

Production-ready REST API for a Note Application built with Spring Boot 4.0.1, Java 25, and PostgreSQL.

## 📋 Table of Contents

- [Technology Stack](#-technology-stack)
- [Features](#-features)
- [Prerequisites](#-prerequisites)
- [Installation & Setup](#-installation--setup)
- [Running the Application](#-running-the-application)
- [Testing](#-testing)
- [Troubleshooting](#-troubleshooting)

## 🛠 Technology Stack

- **Java:** 25
- **Framework:** Spring Boot 4.0.1
- **Database:** PostgreSQL 15+
- **Logging:** Log4j2 (with Async Loggers)
- **Build Tool:** Maven 3.9+
- **Testing:** JUnit 5 + Mockito
- **Concurrency:** Virtual Threads enabled

## ✨ Features

- ✅ **Full CRUD operations** for Notes
- ✅ **Categorization:** Organize notes into distinct Categories (Folders)
- ✅ **Tagging System:** Add multiple dynamic Tags to notes (Many-to-Many)
- ✅ **Advanced Search:** Filter notes by Category and/or Tags
- ✅ **Pagination & Sorting:** Efficiently handle large datasets with server-side pagination
- ✅ **Java 25 Records:** Immutable DTOs (no Lombok)
- ✅ **JPA Entity Lifecycle:** Automated timestamps (@PrePersist, @PreUpdate)
- ✅ **Bean Validation:** Strict input validation (@NotBlank)
- ✅ **Global Exception Handling:** Standardized error responses (@RestControllerAdvice)
- ✅ **Production Logging:** Async Log4j2 with JSON layout (ELK ready)

## 📦 Prerequisites

- Java Development Kit (JDK) 25
- PostgreSQL 15 or higher
- Apache Maven 3.9+
- Git (optional)

**Verify installations:**
```bash
java -version    # Should show version 25
psql --version   # Should show PostgreSQL 15+
mvn -version     # Should show Maven 3.9+
```

## 🚀 Installation & Setup

### Step 1: Clone the Repository

```bash
git clone https://github.com/B-Baysal/mindNote.git
cd mindNote
```

### Step 2: Set Up PostgreSQL Database

#### Option A: Using psql Command Line

1. **Connect to PostgreSQL:**
   ```bash
   # Windows (run as postgres user)
   psql -U postgres

   # macOS/Linux
   sudo -u postgres psql
   ```

2. **Create Database:**
   ```sql
   CREATE DATABASE mindnote;

   -- Optional: Create a dedicated user
   CREATE USER mindnote_user WITH PASSWORD 'your_secure_password';
   GRANT ALL PRIVILEGES ON DATABASE mindnote TO mindnote_user;

   -- Exit psql
   \q
   ```

### Step 3: Configure Application Properties

⚠️ **IMPORTANT:** The `application.properties` file is NOT included in the repository for security reasons.

**Create your configuration file:**

```bash
# Copy from the example template
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

**Edit `src/main/resources/application.properties` with your database credentials:**

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/mindnote
spring.datasource.username=postgres
spring.datasource.password=your_password_here

# Keep other settings as default for local development
```

⚠️ **Never commit** `application.properties` to version control - it's already in `.gitignore`

**Important Configuration Options:**

| Property | Default | Description |
|----------|---------|-------------|
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/mindnote` | Database connection URL |
| `spring.datasource.username` | `postgres` | Database username |
| `spring.datasource.password` | `postgres` | Database password |
| `spring.jpa.hibernate.ddl-auto` | `update` | Auto-create/update tables |
| `spring.threads.virtual.enabled` | `true` | Enable virtual threads |

### Step 4: Build the Project

```bash
# Clean and build the project
mvn clean install

# This will:
# - Download all dependencies
# - Compile the source code
# - Run unit tests
# - Package the application as a JAR file
```

**Expected Output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX s
```

## ▶️ Running the Application

### Option 1: Using Maven (Recommended for Development)

```bash
mvn spring-boot:run
```

### Option 2: Using Java JAR

```bash
# Build the JAR first
mvn clean package -DskipTests

# Run the JAR
java -jar target/mindNote-0.0.1-SNAPSHOT.jar
```

### Option 3: Using IDE

1. **IntelliJ IDEA:**
   - Open the project
   - Navigate to `src/main/java/com/bbay/mindnote/MindNoteApplication.java`
   - Right-click → "Run 'MindNoteApplication'"

2. **VS Code:**
   - Install "Spring Boot Extension Pack"
   - Open Command Palette (Ctrl+Shift+P)
   - Type "Spring Boot Dashboard"
   - Click the play button next to "mindNote"

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=NoteServiceTest
```

### Run Tests with Coverage

```bash
mvn clean test jacoco:report
# View report: target/site/jacoco/index.html
```

### Test Output

```
Tests run: 9, Failures: 0, Errors: 0, Skipped: 0
```

## 📊 Logging

The application uses **Log4j2** with production-grade configuration:

### Log Files

| File | Content | Rotation |
|------|---------|----------|
| `logs/mindnote.log` | All application logs | Daily / 10MB |
| `logs/mindnote-error.log` | ERROR and FATAL only | Daily / 10MB |
| `logs/mindnote-json.log` | JSON format (ELK/Splunk) | Daily / 10MB |

### Log Levels

- **INFO:** Business operations (create, update, delete)
- **DEBUG:** Detailed information (request payloads, counts)
- **ERROR:** Exception details with stack traces

### Log Retention

- Maximum 30 archived files
- Automatic compression (`.gz`)
- Files older than 30 days are auto-deleted

## 🔧 Troubleshooting

### Common Issues and Solutions

#### 1. Application Won't Start - Database Connection Error

**Error:**
```
Cannot create PoolableConnectionFactory (Connection refused)
```

**Solution:**
- Verify PostgreSQL is running:
  ```bash
  # Windows
  pg_ctl status

  # macOS/Linux
  sudo systemctl status postgresql
  ```
- Check database exists:
  ```bash
  psql -U postgres -l
  ```
- Verify credentials in `application.properties`

#### 2. Java Version Error

**Error:**
```
Unsupported class file major version
```

**Solution:**
- Verify Java 25 is installed:
  ```bash
  java -version
  ```
- Set JAVA_HOME environment variable:
  ```bash
  # Windows
  set JAVA_HOME=C:\Program Files\Java\jdk-25

  # macOS/Linux
  export JAVA_HOME=/path/to/jdk-25
  ```

#### 3. Maven Build Fails

**Error:**
```
Failed to execute goal ... plugin
```

**Solution:**
- Clean Maven cache:
  ```bash
  mvn clean
  rm -rf ~/.m2/repository
  mvn install
  ```

#### 4. Port Already in Use

**Error:**
```
Port 8080 is already in use
```

**Solution:**
- Change port in `application.properties`:
  ```properties
  server.port=8081
  ```
- Or kill process using port:
  ```bash
  # Windows
  netstat -ano | findstr :8080
  taskkill /PID <PID> /F

  # macOS/Linux
  lsof -i :8080
  kill -9 <PID>
  ```

#### 5. Tests Fail

**Error:**
```
Tests in error: ...
```

**Solution:**
- Ensure H2 is not required (we use PostgreSQL)
- Check test database configuration
- Run tests with verbose output:
  ```bash
  mvn test -X
  ```

#### 6. Log Files Not Created

**Solution:**
- Verify write permissions in project directory
- Check `log4j2-spring.xml` configuration
- Logs directory is auto-created on first run

### Getting Help

If you encounter issues not covered here:

1. Check application logs in `logs/mindnote-error.log`
2. Enable DEBUG logging in `log4j2-spring.xml`
3. Verify all prerequisites are installed correctly
4. Search for error messages in Spring Boot documentation

## 🔒 Security Notes

- **Development:** Default credentials (`postgres`/`postgres`) are safe for local development
- **Production:**
  - Use environment variables for credentials
  - Enable Spring Security (add to `pom.xml`)
  - Use HTTPS with proper SSL certificates
  - Implement authentication/authorization
  - Never commit real passwords to version control

**Environment Variables (Production):**
```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://prod-host:5432/mindnote
export SPRING_DATASOURCE_USERNAME=prod_user
export SPRING_DATASOURCE_PASSWORD=secure_password
```

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📧 Contact

For questions or support, please open an issue on GitHub.

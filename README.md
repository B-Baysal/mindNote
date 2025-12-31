# MindNote - Note Application Backend

Production-ready REST API for a Note Application built with Spring Boot 4.0.1, Java 25, and PostgreSQL.

## 📋 Table of Contents

- [Technology Stack](#technology-stack)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [API Documentation](#api-documentation)
- [Logging](#logging)
- [Troubleshooting](#troubleshooting)

## 🛠 Technology Stack

- **Java:** 25
- **Framework:** Spring Boot 4.0.1
- **Database:** PostgreSQL 15+
- **Logging:** Log4j2 (with Async Loggers)
- **Build Tool:** Maven 3.9+
- **Testing:** JUnit 5 + Mockito
- **Concurrency:** Virtual Threads enabled

## ✨ Features

- ✅ Full CRUD operations for Notes
- ✅ Java 25 Records for DTOs (no Lombok)
- ✅ Constructor Injection throughout
- ✅ JPA Entity with lifecycle hooks (@PrePersist, @PreUpdate)
- ✅ Bean Validation (@NotBlank)
- ✅ Global Exception Handling (@RestControllerAdvice)
- ✅ Production-grade Log4j2 configuration
- ✅ Async Logging with rolling file policies
- ✅ JSON logs for observability (ELK/Splunk)
- ✅ Comprehensive unit tests with 100% service coverage

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

### Required Software

1. **Java Development Kit (JDK) 25**
   - Download from: [Oracle JDK 25](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK 25](https://jdk.java.net/25/)
   - Verify installation:
     ```bash
     java -version
     # Should show: java version "25" or higher
     ```

2. **PostgreSQL 15 or higher**
   - **Windows:** Download from [PostgreSQL Downloads](https://www.postgresql.org/download/windows/)
   - **macOS:**
     ```bash
     brew install postgresql@15
     brew services start postgresql@15
     ```
   - **Linux (Ubuntu/Debian):**
     ```bash
     sudo apt update
     sudo apt install postgresql postgresql-contrib
     sudo systemctl start postgresql
     ```
   - Verify installation:
     ```bash
     psql --version
     # Should show: psql (PostgreSQL) 15.x or higher
     ```

3. **Apache Maven 3.9+**
   - **Windows:** Download from [Maven Downloads](https://maven.apache.org/download.cgi)
   - **macOS:**
     ```bash
     brew install maven
     ```
   - **Linux:**
     ```bash
     sudo apt update
     sudo apt install maven
     ```
   - Verify installation:
     ```bash
     mvn -version
     # Should show: Apache Maven 3.9.x or higher
     ```

4. **Git** (optional, for cloning)
   - Download from: [Git Downloads](https://git-scm.com/downloads)

### Optional Tools

- **Postman** or **cURL** - For testing API endpoints
- **IntelliJ IDEA** or **VS Code** - For development

## 🚀 Installation & Setup

### Step 1: Clone the Repository

```bash
git clone https://github.com/yourusername/mindNote.git
cd mindNote
```

Or download and extract the ZIP file.

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

#### Option B: Using pgAdmin (GUI)

1. Open pgAdmin
2. Right-click on "Databases" → "Create" → "Database"
3. Enter database name: `mindnote`
4. Click "Save"

### Step 3: Configure Application Properties

The application uses `application.properties` for configuration.

**For first-time setup:**

```bash
# The application.properties already exists with default values
# If you need custom configuration, copy from the example:
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

**Edit `src/main/resources/application.properties`:**

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/mindnote
spring.datasource.username=postgres
spring.datasource.password=your_password_here

# Keep other settings as default for local development
```

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

### Verify Application is Running

The application will start on **http://localhost:8080**

**Check health:**
```bash
curl http://localhost:8080/api/notes
# Should return: []
```

**Successful startup logs:**
```
INFO  c.b.m.MindNoteApplication - Started MindNoteApplication in X.XXX seconds
INFO  c.b.m.service.NoteService - NoteService initialized
INFO  c.b.m.controller.NoteController - NoteController initialized
```

## ⚙️ Configuration

### Database Configuration

**Change Database Name:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/your_database_name
```

**Use Different PostgreSQL Port:**
```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/mindnote
```

**Remote Database:**
```properties
spring.datasource.url=jdbc:postgresql://your-host.com:5432/mindnote
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Application Port Configuration

**Change default port (8080):**
```properties
server.port=9090
```

### Logging Configuration

Logs are stored in the `logs/` directory:
- `logs/mindnote.log` - All application logs
- `logs/mindnote-error.log` - Error logs only
- `logs/mindnote-json.log` - JSON format for log aggregation

**Adjust log levels** in `src/main/resources/log4j2-spring.xml`:
```xml
<!-- Change from DEBUG to INFO for less verbose logs -->
<Logger name="com.bbay.mindnote" level="INFO" additivity="false">
```

### Database Schema Auto-Creation

The application automatically creates tables on startup.

**To disable auto-creation:**
```properties
spring.jpa.hibernate.ddl-auto=none
```

Options:
- `update` - Update schema (safe for development)
- `create` - Drop and recreate (data loss!)
- `create-drop` - Drop on shutdown
- `validate` - Validate schema only
- `none` - Disable

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

## 📚 API Documentation

Base URL: `http://localhost:8080`

### Endpoints Overview

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/notes` | Get all notes | No |
| GET | `/api/notes/{id}` | Get note by ID | No |
| POST | `/api/notes` | Create a new note | No |
| PUT | `/api/notes/{id}` | Update existing note | No |
| DELETE | `/api/notes/{id}` | Delete note | No |

### API Examples

#### 1. Create a New Note

**Request:**
```bash
curl -X POST http://localhost:8080/api/notes \
  -H "Content-Type: application/json" \
  -d '{
    "title": "My First Note",
    "content": "This is the content of my note"
  }'
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "title": "My First Note",
  "content": "This is the content of my note",
  "createdAt": "2025-12-31T10:30:00",
  "updatedAt": "2025-12-31T10:30:00"
}
```

#### 2. Get All Notes

**Request:**
```bash
curl http://localhost:8080/api/notes
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "title": "My First Note",
    "content": "This is the content of my note",
    "createdAt": "2025-12-31T10:30:00",
    "updatedAt": "2025-12-31T10:30:00"
  },
  {
    "id": 2,
    "title": "Another Note",
    "content": "More content here",
    "createdAt": "2025-12-31T11:00:00",
    "updatedAt": "2025-12-31T11:00:00"
  }
]
```

#### 3. Get Note by ID

**Request:**
```bash
curl http://localhost:8080/api/notes/1
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "title": "My First Note",
  "content": "This is the content of my note",
  "createdAt": "2025-12-31T10:30:00",
  "updatedAt": "2025-12-31T10:30:00"
}
```

#### 4. Update Note

**Request:**
```bash
curl -X PUT http://localhost:8080/api/notes/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated Title",
    "content": "Updated content"
  }'
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "title": "Updated Title",
  "content": "Updated content",
  "createdAt": "2025-12-31T10:30:00",
  "updatedAt": "2025-12-31T12:00:00"
}
```

#### 5. Delete Note

**Request:**
```bash
curl -X DELETE http://localhost:8080/api/notes/1
```

**Response:** `204 No Content`

### Error Responses

#### Validation Error (400)

**Request with missing title:**
```bash
curl -X POST http://localhost:8080/api/notes \
  -H "Content-Type: application/json" \
  -d '{"content": "Content without title"}'
```

**Response:** `400 Bad Request`
```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2025-12-31T10:30:00",
  "errors": {
    "title": "Title is required"
  }
}
```

#### Resource Not Found (404)

**Response:** `404 Not Found`
```json
{
  "status": 404,
  "message": "Note not found with id: 999",
  "timestamp": "2025-12-31T10:30:00"
}
```

### Testing with Postman

1. Import the API collection:
   - Create a new Collection in Postman
   - Add requests for each endpoint above
   - Set base URL as variable: `{{baseUrl}}` = `http://localhost:8080`

2. Test sequence:
   - POST → Create a note
   - GET all → Verify note exists
   - GET by ID → Retrieve specific note
   - PUT → Update the note
   - DELETE → Remove the note

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

### Viewing Logs

**Real-time console logs:**
```bash
mvn spring-boot:run
```

**Tail log files:**
```bash
# All logs
tail -f logs/mindnote.log

# Errors only
tail -f logs/mindnote-error.log
```

## 🗂 Project Structure

```
mindNote/
├── src/
│   ├── main/
│   │   ├── java/com/bbay/mindnote/
│   │   │   ├── MindNoteApplication.java          # Main Spring Boot application
│   │   │   ├── controller/
│   │   │   │   └── NoteController.java           # REST endpoints
│   │   │   ├── service/
│   │   │   │   └── NoteService.java              # Business logic
│   │   │   ├── repository/
│   │   │   │   └── NoteRepository.java           # JPA repository
│   │   │   ├── entity/
│   │   │   │   └── Note.java                     # JPA entity with lifecycle hooks
│   │   │   ├── dto/
│   │   │   │   ├── NoteRequest.java              # Request DTO (Record)
│   │   │   │   └── NoteResponse.java             # Response DTO (Record)
│   │   │   └── exception/
│   │   │       ├── ResourceNotFoundException.java # Custom exception
│   │   │       └── GlobalExceptionHandler.java   # @RestControllerAdvice
│   │   └── resources/
│   │       ├── application.properties            # Configuration
│   │       ├── application.properties.example    # Configuration template
│   │       └── log4j2-spring.xml                 # Log4j2 configuration
│   └── test/
│       └── java/com/bbay/mindnote/
│           ├── MindNoteApplicationTests.java     # Context load test
│           └── service/
│               └── NoteServiceTest.java          # Service unit tests
├── logs/                                         # Log files (gitignored)
├── target/                                       # Build output (gitignored)
├── .gitignore                                    # Git ignore rules
├── pom.xml                                       # Maven dependencies
└── README.md                                     # This file
```

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

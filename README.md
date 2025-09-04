# Digital Assistant Service

A comprehensive Spring Boot REST API service that allows users to create digital assistants with predefined responses and interact with them through HTTP endpoints. Includes a modern JavaScript frontend for easy testing and interaction.

## Features

### Core Functionality
- **Create/Update Assistants**: Define assistants with custom names and response texts
- **Message Processing**: Send messages to assistants and receive predefined responses
- **CRUD Operations**: Complete Create, Read, Update, Delete functionality
- **Data Persistence**: H2 in-memory database with JPA (easily configurable for production databases)

### Technical Features
- **Input Validation**: Comprehensive request validation with meaningful error messages
- **Error Handling**: Proper HTTP status codes and structured error responses
- **Health Monitoring**: Built-in health check endpoints with Spring Boot Actuator
- **Frontend Interface**: Modern JavaScript UI for testing and interaction
- **CORS Support**: Cross-origin resource sharing enabled for frontend integration
- **Detailed Logging**: Comprehensive logging for debugging and monitoring

## Technology Stack

### Backend
- **Java 17**: Modern Java with latest features
- **Spring Boot 3.2.0**: Latest Spring Boot framework
- **Spring Data JPA**: Database abstraction and ORM
- **Spring Boot Actuator**: Monitoring and health checks
- **H2 Database**: In-memory database for development
- **Bean Validation**: Input validation with annotations
- **Maven**: Build and dependency management

### Frontend
- **HTML5**: Modern semantic markup
- **CSS3**: Responsive design with Grid and Flexbox
- **Vanilla JavaScript**: No external dependencies, modern ES6+ features
- **Fetch API**: Modern HTTP client for API communication

## Quick Start

### Prerequisites
- **Java 17** or higher ([Download here](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html))
- **Maven 3.6** or higher ([Download here](https://maven.apache.org/download.cgi))
- **Git** (optional, for version control)

### Installation and Setup

1. **Create the Project Structure**
   ```bash
   # Create main directory
   mkdir digital-assistant-service
   cd digital-assistant-service
   
   # Create Spring Boot directory structure
   mkdir -p src/main/java/com/example/digitalassistant/{model,repository,service,controller}
   mkdir -p src/main/resources/static
   mkdir -p src/test/java
   ```

2. **Add All Project Files**
   - Copy all the provided Java files to their respective directories
   - Copy the `pom.xml` to the project root
   - Copy the `application.properties` to `src/main/resources/`
   - Copy the HTML and JavaScript files to `src/main/resources/static/`

3. **Build and Run**
   ```bash
   # Build the project
   mvn clean install
   
   # Run the application
   mvn spring-boot:run
   ```

4. **Verify Installation**
   - API Health Check: http://localhost:8080/api/assistants/health
   - Frontend Interface: http://localhost:8080
   - H2 Database Console: http://localhost:8080/h2-console

## API Documentation

### Base URL
```
http://localhost:8080/api/assistants
```

### Endpoints Overview

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/assistants` | Create or update an assistant |
| POST | `/api/assistants/{name}/message` | Send message to assistant |
| GET | `/api/assistants` | Get all assistants |
| GET | `/api/assistants/{name}` | Get specific assistant |
| DELETE | `/api/assistants/{name}` | Delete assistant |
| GET | `/api/assistants/health` | Health check |

### Detailed API Examples

#### 1. Create/Update Assistant
**Request:**
```bash
curl -X POST http://localhost:8080/api/assistants \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Yash-SmartBot",
    "responseText": "Hello! I am Yash-SmartBot, your intelligent digital assistant. How can I help you today?"
  }'
```

**Response (201 Created):**
```json
{
  "success": true,
  "message": "Assistant 'Yash-SmartBot' created successfully",
  "operation": "created",
  "assistant": {
    "id": 1,
    "name": "Yash-SmartBot",
    "responseText": "Hello! I am Yash-SmartBot, your intelligent digital assistant. How can I help you today?",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
}
```

#### 2. Send Message to Assistant
**Request:**
```bash
curl -X POST http://localhost:8080/api/assistants/Yash-SmartBot/message \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Hello there!"
  }'
```

**Response (200 OK):**
```json
{
  "assistantName": "Yash-SmartBot",
  "response": "Hello! I am Yash-SmartBot, your intelligent digital assistant. How can I help you today?",
  "originalMessage": "Hello there!",
  "timestamp": "2024-01-15T10:35:00"
}
```

#### 3. Get All Assistants
**Request:**
```bash
curl http://localhost:8080/api/assistants
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "Yash-SmartBot",
    "responseText": "Hello! I am Yash-SmartBot, your intelligent digital assistant. How can I help you today?",
    "createdAt": "2024-01-15T10:30:00",
    "updatedAt": "2024-01-15T10:30:00"
  }
]
```

## Frontend Usage

### Accessing the Web Interface
1. Start the Spring Boot application
2. Open your browser and go to: http://localhost:8080
3. Use the web interface to:
   - Create new assistants
   - Send messages to existing assistants
   - View all available assistants
   - Delete assistants when no longer needed

### Frontend Features
- **Responsive Design**: Works on desktop and mobile devices
- **Real-time Updates**: Automatically refreshes data after operations
- **Error Handling**: User-friendly error messages and validation
- **Modern UI**: Clean, professional interface with intuitive navigation

##  Configuration

### Development Configuration
The application comes pre-configured for development with:
- H2 in-memory database
- Debug logging enabled
- H2 console accessible
- CORS enabled for frontend development

### Production Configuration
For production deployment, create `application-prod.properties`:

```properties
# Production Database (PostgreSQL example)
spring.datasource.url=jdbc:postgresql://localhost:5432/assistants_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate

# Security
spring.h2.console.enabled=false
management.endpoints.web.exposure.include=health

# Logging
logging.level.com.example.digitalassistant=INFO
```

### Environment Variables
```bash
# Database Configuration
export DB_USERNAME=your_db_user
export DB_PASSWORD=your_db_password

# Server Configuration
export SERVER_PORT=8080

# Run with production profile
java -jar target/digital-assistant-service-1.0.0.jar --spring.profiles.active=prod
```

##  Testing

### Manual Testing with cURL

```bash
# 1. Health Check
curl http://localhost:8080/api/assistants/health

# 2. Create Assistant
curl -X POST http://localhost:8080/api/assistants \
  -H "Content-Type: application/json" \
  -d '{"name": "Yash-SmartBot", "responseText": "I am Yash-SmartBot, your intelligent assistant!"}'

# 3. Send Message
curl -X POST http://localhost:8080/api/assistants/Yash-SmartBot/message \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello Yash-SmartBot!"}'

# 4. List Assistants
curl http://localhost:8080/api/assistants

# 5. Get Specific Assistant
curl http://localhost:8080/api/assistants/Yash-SmartBot

# 6. Delete Assistant
curl -X DELETE http://localhost:8080/api/assistants/Yash-SmartBot
```

### Automated Testing
```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify

# Generate test coverage report
mvn jacoco:report
```

##  Deployment Options

### 1. Local Development
```bash
mvn spring-boot:run
```

### 2. Standalone JAR
```bash
mvn clean package
java -jar target/digital-assistant-service-1.0.0.jar
```

### 3. Docker Deployment
```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/digital-assistant-service-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
# Build and run with Docker
docker build -t digital-assistant-service .
docker run -p 8080:8080 digital-assistant-service
```

### 4. Cloud Platform Deployment

#### Heroku
```bash
# Create Procfile
echo "web: java -jar target/digital-assistant-service-1.0.0.jar" > Procfile

# Deploy to Heroku
heroku create your-assistant-service
git push heroku main
```

#### Google Cloud Run
```bash
# Build and deploy
gcloud run deploy digital-assistant-service \
  --source . \
  --platform managed \
  --region us-central1 \
  --allow-unauthenticated
```

#### Railway
```bash
# Connect Railway to your Git repository
railway login
railway link
railway up
```

##  Architecture Overview

### Layered Architecture
```
┌─────────────────────────────────────┐
│           Frontend (HTML/JS)         │
├─────────────────────────────────────┤
│        REST Controller Layer        │  ← HTTP endpoints, request/response handling
├─────────────────────────────────────┤
│         Service Layer               │  ← Business logic, validation, transactions
├─────────────────────────────────────┤
│        Repository Layer             │  ← Data access abstraction
├─────────────────────────────────────┤
│         Database Layer              │  ← H2/PostgreSQL data storage
└─────────────────────────────────────┘
```

### Key Design Patterns
- **MVC Pattern**: Model-View-Controller separation
- **Repository Pattern**: Data access abstraction
- **Service Layer Pattern**: Business logic encapsulation
- **DTO Pattern**: Data transfer objects for API communication
- **Dependency Injection**: Spring IoC container management

##  Monitoring and Debugging

### Health Endpoints
- **Application Health**: http://localhost:8080/actuator/health
- **Custom Health**: http://localhost:8080/api/assistants/health
- **Application Info**: http://localhost:8080/actuator/info

### Database Console
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:assistantdb`
  - Username: `sa`
  - Password: (empty)

### Logging
- Application logs show detailed information about requests and operations
- SQL queries are logged for debugging database operations
- Error logs include stack traces for troubleshooting

### Production Recommendations
- Add authentication (JWT tokens)
- Implement rate limiting
- Use HTTPS in production
- Restrict CORS origins
- Add request/response logging
- Implement API versioning

**Issue: Application won't start**
- Check Java 17 is installed: `java -version`
- Verify Maven is working: `mvn -version`
- Check port 8080 is available: `netstat -an | findstr 8080`

**Issue: Frontend can't connect to API**
- Verify backend is running on port 8080
- Check browser console for CORS errors
- Verify API health: http://localhost:8080/api/assistants/health

**Issue: Database errors**
- Check H2 console for data state
- Verify application.properties configuration
- Check logs for SQL errors

### Getting Help
- Check application logs for detailed error information
- Use H2 console to inspect database state
- Enable debug logging for more detailed information
- Use browser developer tools for frontend debugging



##  Project Summary

This Digital Assistant Service demonstrates:

1. **Clean Architecture**: Well-structured, maintainable code
2. **Modern Technologies**: Latest Spring Boot and Java features
3. **Comprehensive Testing**: Multiple testing approaches
4. **Production Ready**: Monitoring, health checks, and deployment options
5. **User-Friendly**: Both API and web interface for interaction
6. **Extensible Design**: Easy to add new features and integrations

The implementation showcases best practices in Spring Boot development while providing a functional, deployable service that meets all specified requirements.

**Total Development Time**: ~3-4 hours (It took around 7 hours, I agree little more than expected)
**Code Quality**: Production-ready with comprehensive documentation
**Deployment**: Ready for local or cloud deployment
=======
# digital-assistant-service
Java Spring Boot REST API for Digital Assistant Service 
>>>>>>> 4b2875ec81ccea23292bf4f67e45c4f9dcf71d01

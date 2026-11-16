# Help Desk Ticket API

## Project Overview

The Help Desk Ticket API is a RESTful backend application developed using Spring Boot. It provides a centralized system for managing support tickets, assigning agents, tracking ticket progress, and maintaining activity records.

The application uses Microsoft SQL Server for relational data management and MongoDB for storing comments and ticket activity events.

It follows a layered architecture with separate responsibilities for controllers, services, repositories, security, and data persistence.

## Key Features

- User registration and authentication using JWT.
- Role-based access control for Requesters, Agents, and Administrators.
- Ticket creation, retrieval, assignment, and status management.
- Ticket priority management with due-time targets.
- Ticket comments and activity tracking.
- Automatic detection of overdue tickets through scheduled tasks.
- Duplicate prevention for overdue warning events.
- REST API documentation using Swagger UI.
- Application health monitoring using Spring Boot Actuator.
- Aspect-oriented logging for important service operations.
- Automated unit and integration testing.

## Technology Stack

- **Programming Language:** Java 21
- **Backend Framework:** Spring Boot 4.1.2
- **Build Tool:** Maven
- **Relational Database:** Microsoft SQL Server
- **NoSQL Database:** MongoDB
- **ORM:** Spring Data JPA / Hibernate
- **NoSQL Integration:** Spring Data MongoDB
- **Authentication:** Spring Security and JWT
- **API Documentation:** Springdoc OpenAPI / Swagger UI
- **Monitoring:** Spring Boot Actuator
- **Testing:** JUnit 5, Mockito, Spring Boot Test
- **Version Control:** Git and GitHub

## Prerequisites

Ensure the following software and services are installed before running the application.

- **Java Development Kit (JDK):** Java 21
- **Microsoft SQL Server:** SQL Server 2022 or a compatible version.
- **SQL Server Management Studio (SSMS):** For database management and SQL queries.
- **MongoDB:** MongoDB 8.0 or a compatible version.
- **MongoDB Compass:** For viewing and managing MongoDB databases.
- **Git:** For cloning and managing the project repository.

### Build Tool

The project includes the Maven Wrapper (`mvnw` and `mvnw.cmd`). A separate Maven installation is not required. The wrapper downloads and uses the Maven version configured by the project.

## Database Setup

The application uses two databases:

- **Microsoft SQL Server:** Stores user information and ticket records.
- **MongoDB:** Stores ticket comments and activity events.

### 1. Microsoft SQL Server Setup

1. Start the SQL Server service.
2. Open SQL Server Management Studio (SSMS).
3. Connect to your SQL Server instance.
4. Create a database named `HelpDeskDB`.

```sql
CREATE DATABASE HelpDeskDB;
```

5. Configure SQL Server authentication and create an application login.
6. Create a database user for that login and grant the permissions required by the application.

The application uses the following database configuration:

- Database: `HelpDeskDB`
- Default server: `localhost`
- Default port: `1433`
- Authentication: SQL Server Authentication

The application connects using environment variables rather than hardcoded credentials.

### 2. MongoDB Setup

1. Start the MongoDB service.
2. Ensure MongoDB is accessible at `localhost:27017`.
3. The application uses a database named `helpdesk`.
4. The application stores comments in the `comments` collection and activity events in the `activities` collection.

Default MongoDB connection URI:

```text
mongodb://localhost:27017/helpdesk
```

#### Required Activity Index

The `activities` collection uses a unique partial index to prevent duplicate overdue warning events for the same ticket.

If the index has not already been created, execute the following command in MongoDB Compass or the MongoDB shell:

```javascript
db.activities.createIndex(
    { ticketId: 1, action: 1 },
    {
        name: "unique_ticket_warning_idx",
        unique: true,
        partialFilterExpression: { action: "WARNING" }
    }
);
```

This index ensures that a ticket cannot have multiple WARNING activity records.

## Environment Configuration

The application uses environment variables to manage database credentials, JWT security settings, and environment-specific configuration.

Do not hardcode passwords, database credentials, or JWT secrets in the application configuration files or commit them to Git.

### Required Environment Variables

SPRING_PROFILES_ACTIVE: Activates the application profile.
DB_USERNAME: SQL Server application username.
DB_PASSWORD: SQL Server application password.
JWT_SECRET: Base64-encoded secret used for JWT signing.

### Optional Environment Variables

DB_URL: SQL Server JDBC connection URL.
Default: Local SQL Server connection.
MONGODB_URI: MongoDB connection URI.
Default: mongodb://localhost:27017/helpdesk

### Windows PowerShell Configuration

Open PowerShell in the `helpdesk-api` project directory.

Set the local profile:

```powershell
$env:SPRING_PROFILES_ACTIVE = "local"
```

Set the SQL Server credentials:

```powershell
$env:DB_USERNAME = "helpdesk_app"
$env:DB_PASSWORD = Read-Host "Enter SQL Server password"
```

Generate a secure Base64-encoded JWT secret:

```powershell
$bytes = New-Object byte[] 32
[System.Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
```

The application validates the required configuration during startup. It will fail safely if the required database password or JWT secret is missing or invalid.

**Note:** These environment variables apply only to the current PowerShell session. If a new terminal is opened, configure them again.

## Build and Run

Make sure SQL Server and MongoDB services are running and the required environment variables are configured before starting the application.

### 1. Compile the Application

From the project root directory, execute:

```powershell
.\mvnw.cmd clean compile
```

This command cleans previous build files and compiles the application source code.

### 2. Run Automated Tests

Execute:

```powershell
.\mvnw.cmd clean verify
```

This command compiles the application, runs the automated tests, and verifies the build.

### 3. Start the Application

To run the application using the Maven Wrapper:

```powershell
.\mvnw.cmd spring-boot:run
```

Once the application starts successfully, it will be available at:

```text
http://localhost:8081
```

### 4. Create an Executable JAR

To package the application:

```powershell
.\mvnw.cmd clean package
```

The executable JAR file will be generated inside the `target` directory.

### 5. Run the Executable JAR

After packaging, identify the generated executable JAR inside the `target` directory and run it:

```powershell
java -jar target/<generated-jar-name>.jar
```

Replace `<generated-jar-name>` with the actual JAR filename.

### 6. Stop the Application

Press `Ctrl + C` in the terminal to stop the application.

The application uses graceful shutdown to allow active requests to complete and close its database resources cleanly.

## API Documentation and Health Monitoring

### 1. Swagger UI

The application provides interactive API documentation using Swagger UI.

After starting the application, open the following URL in your browser:

```text
http://localhost:8081/swagger-ui/index.html
```

Swagger UI allows users to:

- View available REST API endpoints.
- Inspect HTTP methods and request parameters.
- Understand request and response payloads.
- Execute API requests directly from the browser.
- Authorize protected endpoints using JWT authentication.

#### JWT Authentication

1. Register a user or log in using the appropriate API endpoint.
2. Obtain the JWT access token from the login response.
3. Click the **Authorize** button in Swagger UI.
4. Enter the access token in the Bearer authentication field.
5. Click **Authorize** to access protected endpoints.

### 2. Actuator Health Monitoring

Spring Boot Actuator provides application health information.

Open the following URL:

```text
http://localhost:8081/actuator/health
```

The health endpoint provides information about the application's operational status and configured database connections.

The application exposes the following Actuator endpoints:

- `/actuator/health` – Checks application and dependency health.
- `/actuator/info` – Provides application information.

These endpoints can be used to verify whether the application and its database connections are operational.


## Business Workflow and Application Functionality

### 1. User Roles

The application supports three user roles:

- **REQUESTER**
  - Creates support tickets.
  - Views their own tickets.
  - Follows ticket progress.
  - Adds comments and reviews ticket activity.
  - Closes tickets after resolution.

- **AGENT**
  - Views available tickets.
  - Claims unassigned tickets.
  - Works on assigned tickets.
  - Updates ticket status.
  - Adds comments and records progress.

- **ADMIN**
  - Performs administrative ticket operations.
  - Assigns tickets to agents.
  - Views tickets across the system.
  - Manages privileged user creation.

### 2. Ticket Lifecycle

Tickets follow a controlled workflow:

`OPEN → IN_PROGRESS → RESOLVED → CLOSED`

The application enforces the following rules:

- A newly created ticket starts with `OPEN` status.
- An eligible agent can claim an unassigned ticket.
- Only the assigned agent can move a ticket from `OPEN` to `IN_PROGRESS`.
- Only the assigned agent can move a ticket from `IN_PROGRESS` to `RESOLVED`.
- The requester can move a resolved ticket to `CLOSED`.
- Invalid, skipped, or reversed status transitions are not permitted.

### 3. Ticket Priority and Due-Time Targets

Each ticket is assigned a due-time target based on its priority.

- **HIGH:** 4 hours
- **MEDIUM:** 24 hours
- **LOW:** 72 hours

The due time is calculated when the ticket is created.

### 4. Comments and Activity Tracking

- Ticket comments are stored in MongoDB.
- Ticket activity events are maintained in MongoDB.
- Important actions, such as ticket creation, claiming, assignment, and status changes, are recorded as activity events.
- Activity records provide a timeline of important ticket operations.

### 5. Overdue Ticket Monitoring

The application includes a scheduled task that periodically checks unresolved tickets.

- Identifies overdue tickets with `OPEN` or `IN_PROGRESS` status.
- Records a `WARNING` activity event for an overdue ticket.
- Prevents duplicate warning events for the same ticket.
- Does not automatically change the ticket status.
- Handles MongoDB write failures without terminating the scheduled task, allowing a later scan to retry.

## Testing and Verification

The application includes automated tests to verify important business logic and application startup.

### 1. Execute Automated Tests

Run the following command from the project root directory:

```powershell
.\mvnw.cmd clean verify
```

This command executes the test suite and verifies the Maven build.

### 2. Test Coverage

The current automated test suite includes:

**Application Integration Test**
- Verifies that the Spring Boot application context loads successfully.

**Overdue Ticket Scheduler Unit Tests**
- Verifies that a warning event is saved for an overdue ticket.
- Verifies warning generation for multiple overdue tickets.
- Verifies that duplicate warning events are not created.
- Verifies that MongoDB write failures are handled without crashing the scheduler.
- Verifies that duplicate-key exceptions are handled safely.

### 3. Manual Application Verification

After starting the application, use Swagger UI to verify the REST API functionality.

The following areas should be checked:

- User registration and login.
- JWT authentication and authorization.
- Ticket creation and retrieval.
- Ticket claiming and administrative assignment.
- Ticket status transitions.
- Ticket comments and activity records.
- Ticket filtering and pagination.
- Overdue ticket warning generation.
- Application and database health through Actuator.

### 4. Test Results

Maven displays the test execution summary in the terminal.

Detailed test reports are generated under:

```text
target/surefire-reports/
```

A successful verification should complete with:

```text
BUILD SUCCESS
```


## Project Structure

The application follows a layered package structure to separate business logic, API handling, data access, security, and supporting components.

helpdesk-api/
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── helpdesk/
│   │               ├── advice/
│   │               ├── aspect/
│   │               ├── config/
│   │               ├── controller/
│   │               ├── document/
│   │               ├── dto/
│   │               ├── entity/
│   │               ├── enums/
│   │               ├── event/
│   │               ├── exception/
│   │               ├── mapper/
│   │               ├── repository/
│   │               ├── scheduler/
│   │               ├── service/
│   │               └── validation/
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── helpdesk/
│                   ├── HelpdeskApiApplicationTests.java
│                   └── scheduler/
│                       └── OverdueTicketSchedulerTest.java
│
├── .mvn/
├── mvnw
├── mvnw.cmd
├── pom.xml
├── HELP.md
└── README.md

### Package Responsibilities

- **advice:** Handles global API exception responses.
- **aspect:** Implements aspect-oriented logging for service operations.
- **config:** Contains application and security configuration.
- **controller:** Exposes REST API endpoints.
- **document:** Contains MongoDB document models.
- **dto:** Defines request and response data transfer objects.
- **entity:** Contains relational database entities.
- **enums:** Defines application-specific enumerations.
- **event:** Handles application events and event listeners.
- **exception:** Contains custom application exceptions.
- **mapper:** Handles object and DTO mapping.
- **repository:** Provides database access operations.
- **scheduler:** Contains scheduled background tasks.
- **service:** Implements application business logic.
- **validation:** Handles application configuration validation.

### Test Structure

- `HelpdeskApiApplicationTests.java` – Application context integration test.
- `OverdueTicketSchedulerTest.java` – Unit tests for overdue ticket scheduling behavior.


## API Endpoint Reference

All API endpoints use the `/api/v1` base path.

Protected endpoints require JWT Bearer authentication unless otherwise specified.

### 1. User APIs

**Base Path:** `/api/v1/users`

- **Register User**
  - Method: `POST`
  - Endpoint: `/api/v1/users/register`
  - Access: Public
  - Purpose: Registers a new requester account.

- **Login User**
  - Method: `POST`
  - Endpoint: `/api/v1/users/login`
  - Access: Public
  - Purpose: Authenticates a user and returns a JWT access token.

- **Create Privileged User**
  - Method: `POST`
  - Endpoint: `/api/v1/users/admin/create-user`
  - Access: ADMIN
  - Purpose: Allows an administrator to create privileged user accounts.

### 2. Ticket APIs

**Base Path:** `/api/v1/tickets`

- **Create Ticket**
  - Method: `POST`
  - Endpoint: `/api/v1/tickets`
  - Access: REQUESTER
  - Purpose: Creates a new support ticket.

- **Get Ticket by ID**
  - Method: `GET`
  - Endpoint: `/api/v1/tickets/{ticketId}`
  - Access: REQUESTER, AGENT, ADMIN
  - Purpose: Retrieves ticket details based on the ticket ID.

- **Get All Tickets**
  - Method: `GET`
  - Endpoint: `/api/v1/tickets`
  - Access: REQUESTER, AGENT, ADMIN
  - Purpose: Retrieves tickets with filtering and pagination support.

- **Update Ticket Status**
  - Method: `PATCH`
  - Endpoint: `/api/v1/tickets/{ticketId}/status`
  - Access: REQUESTER, AGENT
  - Purpose: Updates the status of a ticket according to the permitted workflow.

- **Claim Ticket**
  - Method: `PATCH`
  - Endpoint: `/api/v1/tickets/{ticketId}/claim`
  - Access: AGENT
  - Purpose: Allows an agent to claim an unassigned ticket.

- **Assign Agent**
  - Method: `PATCH`
  - Endpoint: `/api/v1/tickets/{ticketId}/assign`
  - Access: ADMIN
  - Purpose: Allows an administrator to assign an agent to a ticket.

### 3. Comment APIs

**Base Path:** `/api/v1/tickets/{ticketId}/comments`

- **Add Comment**
  - Method: `POST`
  - Endpoint: `/api/v1/tickets/{ticketId}/comments`
  - Access: Authenticated user
  - Purpose: Adds a comment to a ticket.

- **Get Ticket Comments**
  - Method: `GET`
  - Endpoint: `/api/v1/tickets/{ticketId}/comments`
  - Access: Authenticated user
  - Purpose: Retrieves comments associated with a ticket.

### Authentication Header

For protected endpoints, include the following HTTP header:

```http
Authorization: Bearer <JWT_ACCESS_TOKEN>
```

## Ticket Filtering and Pagination

The ticket retrieval endpoint supports filtering and pagination to make ticket management easier.

**Endpoint:** `GET /api/v1/tickets`

### Available Query Parameters

- **status**
  - Filters tickets by their current status.
  - Optional parameter.
  - Supported values: `OPEN`, `IN_PROGRESS`, `RESOLVED`, `CLOSED`.

- **priority**
  - Filters tickets by priority.
  - Optional parameter.
  - Supported values: `HIGH`, `MEDIUM`, `LOW`.

- **page**
  - Specifies the page number.
  - Default value: `0`.
  - Minimum value: `0`.

- **size**
  - Specifies the number of records per page.
  - Default value: `10`.
  - Allowed range: `1` to `100`.

### Example Requests

Retrieve all tickets using default pagination:

```http
GET /api/v1/tickets
```

Retrieve OPEN tickets:

```http
GET /api/v1/tickets?status=OPEN
```

Retrieve HIGH priority tickets:

```http
GET /api/v1/tickets?priority=HIGH
```

Retrieve OPEN and HIGH priority tickets:

```http
GET /api/v1/tickets?status=OPEN&priority=HIGH
```

Retrieve the second page with 5 records:

```http
GET /api/v1/tickets?page=1&size=5
```

Combine filtering and pagination:

```http
GET /api/v1/tickets?status=IN_PROGRESS&priority=MEDIUM&page=0&size=10
```

### Pagination Validation

- Page numbers cannot be negative.
- Page size must be between 1 and 100.
- Invalid pagination values are rejected by the application.

## Error Handling

The application uses centralized exception handling through Spring's `@RestControllerAdvice` to provide consistent error responses.

### HTTP Error Responses

- **400 Bad Request**
  - Occurs when request validation fails.
  - Occurs when invalid ticket status transitions are attempted.
  - Occurs when request parameters or request bodies contain invalid data.

- **401 Unauthorized**
  - Returned when authentication is missing or a JWT token is invalid or expired.
  - Handled by the Spring Security authentication configuration.

- **403 Forbidden**
  - Returned when an authenticated user attempts an operation without the required permissions.

- **500 Internal Server Error**
  - Returned when an unexpected application exception occurs.

### Error Response Structure

Error responses use the `ErrorResponse` DTO and contain the following fields:

- `timestamp` – Date and time when the error occurred.
- `status` – HTTP status code.
- `error` – Description of the error.
- `message` – Details about the error.

### Example Error Response

```json
{
  "timestamp": "2026-11-13T14:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid ticket status transition."
}
```

The example illustrates the response structure. Actual messages depend on the exception and validation failure.

## Database Schema and Index Management

### 1. SQL Server Schema Management

The application uses Spring Data JPA and Hibernate to manage the relational database schema.

The local profile uses:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
```

- Missing database tables are created automatically.
- Schema updates are applied based on the JPA entity definitions.
- Existing database records are generally preserved.

Before starting the application, create the `HelpDeskDB` database in SQL Server. Hibernate will manage the application tables during startup.

### 2. Test Database Schema

The test profile uses:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop
```

This configuration creates the database schema for testing and drops it when the test context is closed.

### 3. MongoDB Index Management

MongoDB indexes improve query performance and help maintain data integrity.

The application uses indexes on activity records, including:

- `ticket_createdAt_idx` – Supports ticket activity retrieval ordered by creation time.
- `unique_ticket_warning_idx` – A unique partial index on `ticketId` and `action`, restricted to `WARNING` events.

The unique partial index prevents duplicate overdue warning events for the same ticket.

If the unique partial index is not automatically created, execute the following command in MongoDB Compass or the MongoDB shell:

```javascript
db.activities.createIndex(
    { ticketId: 1, action: 1 },
    {
        name: "unique_ticket_warning_idx",
        unique: true,
        partialFilterExpression: { action: "WARNING" }
    }
);
```

## Dependencies and Build Tools

The project uses Maven for dependency management and build automation. The Maven Wrapper is included, so a separate Maven installation is not required.

### Core Dependencies

- **Spring Boot WebMVC:** Provides REST API development and HTTP request handling.
- **Spring Data JPA:** Handles relational database operations using Hibernate.
- **Microsoft SQL Server JDBC Driver:** Connects the application to SQL Server.
- **Spring Data MongoDB:** Handles document storage and retrieval in MongoDB.
- **Spring Boot Validation:** Validates incoming request data.
- **Spring Security:** Provides authentication and authorization.
- **JJWT:** Handles JWT creation and token validation.
- **Spring Boot Actuator:** Provides application health and monitoring endpoints.
- **Spring Boot AspectJ:** Supports aspect-oriented programming and operation logging.
- **Springdoc OpenAPI:** Generates API documentation and Swagger UI.

### Testing Dependencies

- **Spring Boot Test:** Supports application and integration testing.
- **JUnit 5:** Provides the testing framework.
- **H2 Database:** Provides an in-memory relational database for tests.
- **Testcontainers:** Provides container-based MongoDB testing.
- **Spring Boot Test Integration:** Supports testing Spring Boot components and application integrations.

### Build Tool

- Maven Wrapper (`mvnw.cmd`) is used to build, test, and package the application.
- Java 21 is configured as the project's required Java version.
- The Spring Boot Maven Plugin is used to package the application as an executable JAR.
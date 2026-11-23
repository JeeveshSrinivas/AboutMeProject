Help Desk Ticket API
Project Overview
The Help Desk Ticket API is a versioned RESTful backend application
built with Spring Boot. It provides a centralized system for registering
users, creating and managing support tickets, assigning agents, tracking
ticket progress, and maintaining ticket comments and activity records.

The application uses:
- Microsoft SQL Server for user information and current ticket
  records.
- MongoDB for ticket comments and activity timeline events.
The application follows a layered architecture that separates
controllers, services, repositories, security, configuration, and data
persistence.
Key Features
- User registration and login using JWT authentication.
- BCrypt password hashing.
- Role-based access control for REQUESTER, AGENT, and ADMIN.
- JWT token revocation on logout.
- Ticket creation, retrieval, claiming, assignment, and status
  management.
- Ticket priority management with due-time targets.
- Ticket comments and activity tracking.
- Activity records that capture the authenticated employee ID and
  role.
- Automatic detection of overdue tickets through scheduled tasks.
- Duplicate prevention for overdue warning events.
- Centralized exception handling.
- Aspect-oriented logging for important service operations.
- REST API documentation using Swagger UI.
- Application health monitoring using Spring Boot Actuator.
- Automated unit and integration tests.
Technology Stack
  Area                               Technology
  Programming language               Java 21
Backend framework                  Spring Boot 4.1.2
Build tool                         Maven Wrapper
Relational database                Microsoft SQL Server
NoSQL database                     MongoDB
ORM                                Spring Data JPA / Hibernate
NoSQL integration                  Spring Data MongoDB
Authentication and authorization   Spring Security, JWT, JJWT
Password hashing                   BCrypt
API documentation                  Springdoc OpenAPI / Swagger UI
Monitoring                         Spring Boot Actuator
Testing                            JUnit 5, Mockito, Spring Boot Test, Testcontainers
Version control                    Git and GitHub
Prerequisites
Install or prepare the following before running the application:
- JDK 21.
- Microsoft SQL Server 2022 or a compatible version.
- SQL Server Management Studio (SSMS) for database management.
- MongoDB 8.0 or a compatible version.
- MongoDB Compass (optional, for viewing MongoDB data).
- Docker Desktop (required for integration tests that use
  Testcontainers).
- Git.
The project includes the Maven Wrapper (mvnw and mvnw.cmd), so a
separate Maven installation is not required.
Database Setup
The application uses two databases.
  Database                            Purpose
  Microsoft SQL Server                Stores user information and current
ticket records.
MongoDB                             Stores ticket comments and activity
events.
1. Microsoft SQL Server Setup
1. Start the SQL Server service.
2. Open SQL Server Management Studio (SSMS).
3. Connect to your SQL Server instance.
4. Create a database named HelpDeskDB.
CREATE DATABASE HelpDeskDB;
5. Configure SQL Server authentication and create an application login.
6. Create a database user for that login and grant the permissions
   required by the application.
The default local SQL Server connection uses:
- Database: HelpDeskDB
- Server: localhost
- Port: 1433
- Authentication: SQL Server Authentication
The application reads database credentials from environment variables
rather than hardcoding them.
2. MongoDB Setup
1. Start the MongoDB service.
2. Ensure MongoDB is accessible at localhost:27017.
3. The application uses a database named helpdesk.
4. The application stores comments in the comments collection and
   activity events in the activities collection.
Default MongoDB connection URI:
mongodb://localhost:27017/helpdesk
Required Activity Index
A unique partial index on the activities collection prevents duplicate
WARNING activity events for the same ticket.

If the index has not already been created, execute this command in
MongoDB Compass or the MongoDB shell:
db.activities.createIndex(
    { ticketId: 1, action: 1 },
    {
        name: "unique_ticket_warning_idx",
        unique: true,
        partialFilterExpression: { action: "WARNING" }
    }
);
This index applies only to documents whose action is WARNING.
Environment Configuration
The application uses environment variables for database credentials, JWT
security settings, and profile-specific configuration.

Do not hardcode database passwords or JWT secrets in application
configuration files or commit them to Git.
Required Environment Variables
  Variable                            Description
  SPRING_PROFILES_ACTIVE            Activates the application profile.
Use local for local development.

  DB_USERNAME                       SQL Server application username.

  DB_PASSWORD                       SQL Server application password.
JWT_SECRET                        Base64-encoded secret used to sign
JWTs. It must decode to at least 32
bytes.
Optional Environment Variables
  Variable                Description             Default
  DB_URL                SQL Server JDBC         Local SQL Server connection configured
connection URL.         by the application

  MONGODB_URI           MongoDB connection URI. mongodb://localhost:27017/helpdesk
JWT_EXPIRATION        JWT access-token        86400000 (24 hours)
lifetime in
milliseconds.
Windows PowerShell Configuration
Open PowerShell in the helpdesk-api project directory.

Activate the local profile:
$env:SPRING_PROFILES_ACTIVE = "local"
Set the SQL Server credentials:
$env:DB_USERNAME = "helpdesk_app"
$env:DB_PASSWORD = Read-Host "Enter SQL Server password"
Generate a secure Base64-encoded JWT secret:
$bytes = New-Object byte[] 32
[System.Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
Set the JWT token lifetime:
$env:JWT_EXPIRATION = "86400000"
The default token lifetime is 86,400,000 milliseconds (24 hours).

The application validates required configuration during startup. It will
fail to start if required configuration, such as the database password
or JWT secret, is missing or invalid.
Note: These environment variables apply only to the current
PowerShell session. Configure them again when opening a new terminal.
Use the same JWT secret while running the application if you need
tokens issued by that running instance to remain valid.

Build and Run
Before starting the application:
- Ensure SQL Server and MongoDB services are running.
- Configure the required environment variables in the current
  PowerShell session.
- Ensure the HelpDeskDB database exists.
Run the commands below from the helpdesk-api project directory.
1. Compile the Application
.\mvnw.cmd clean compile
This removes previous build output and compiles the application source
code.
2. Run Automated Tests
Docker Desktop must be running because the integration tests use
Testcontainers to start MongoDB.
.\mvnw.cmd clean verify
This command builds the application, executes the automated tests, and
verifies the Maven build.
3. Start the Application
.\mvnw.cmd spring-boot:run
The application uses the profile selected through
SPRING_PROFILES_ACTIVE. With the local profile configured, the API
runs at:
http://localhost:8081
4. Create an Executable JAR
.\mvnw.cmd clean package
The executable JAR is generated inside the target directory.
5. Run the Executable JAR
Identify the generated executable JAR inside target and run:
java -jar target/<generated-jar-name>.jar
Replace <generated-jar-name> with the actual JAR filename.

Make sure the required environment variables are configured in the
terminal before starting the JAR.
6. Stop the Application
Press Ctrl + C in the terminal running the application.

The application is configured for graceful shutdown so it can finish
active requests and close resources cleanly.
API Documentation and Health Monitoring
1. Swagger UI
After starting the application, open:
http://localhost:8081/swagger-ui/index.html
Swagger UI allows users to:
- View available REST API endpoints.
- Inspect HTTP methods, request parameters, and request bodies.
- Review documented response codes.
- Execute API requests from the browser.
- Authorize protected endpoints using JWT Bearer authentication.
Using JWT Authentication in Swagger
1. Register a user or log in using the appropriate authentication
   endpoint.
2. Copy the access token returned by the login response.
3. Click Authorize in Swagger UI.
4. Enter the access token in the Bearer authentication field.
5. Click Authorize.
6. Execute protected endpoints.
Use the access token itself in the Bearer field unless the Swagger UI
explicitly asks for the complete Bearer prefix.
2. Actuator Health Monitoring
The application exposes the following Actuator endpoints:

  Endpoint             Purpose
  /actuator/health   Checks application health.
/actuator/info     Provides application information.

Open the health endpoint at:
http://localhost:8081/actuator/health
The exact details returned by the health endpoint depend on the
application's Actuator configuration and health contributors.
Business Workflow and Application Functionality
1. User Roles
The application supports three roles.
REQUESTER
- Registers an account and receives the REQUESTER role.
- Creates support tickets.
- Views tickets subject to ownership rules.
- Follows ticket progress.
- Adds comments and reviews ticket activity.
- Closes tickets after resolution.
AGENT
- Views tickets available to agents.
- Claims eligible unassigned tickets.
- Works on assigned tickets.
- Updates ticket status according to the workflow.
- Adds comments and records progress.
ADMIN
- Performs administrative ticket operations.
- Assigns tickets to agents.
- Views tickets across the system, subject to implemented access
  rules.
- Creates privileged AGENT or ADMIN users.
Self-registration does not allow a user to choose a privileged role. New
self-registered users are assigned REQUESTER. Privileged user creation
is restricted to an authenticated administrator.
2. Ticket Lifecycle
Tickets follow this workflow:
OPEN → IN_PROGRESS → RESOLVED → CLOSED
The application enforces the following rules:
- A newly created ticket starts with OPEN status.
- An eligible agent can claim an unassigned ticket.
- Only the assigned agent can move a ticket from OPEN to
  IN_PROGRESS.
- Only the assigned agent can move a ticket from IN_PROGRESS to
  RESOLVED.
- The requester can move a resolved ticket to CLOSED.
- Invalid, skipped, or reversed status transitions are rejected.
A valid JWT authenticates the caller; it does not bypass the existing
role, ownership, assignment, or workflow checks.
3. Ticket Priority and Due-Time Targets
Each ticket receives a due-time target based on its priority.

  Priority     Due-time target
  HIGH               4 hours
MEDIUM            24 hours
LOW               72 hours

The due time is calculated when the ticket is created.
4. Comments and Activity Tracking
- Ticket comments are stored in MongoDB.
- Ticket activity events are stored in MongoDB.
- Important ticket operations are recorded in the activity timeline.
- User-originated activity records capture the authenticated employee
  ID and role from the JWT.
- System-generated events use the system origin and do not represent
  an authenticated employee.
The SQL Server database remains the source for user and current ticket
records. MongoDB stores the comments and activity history associated
with tickets.
5. Overdue Ticket Monitoring
A scheduled task periodically checks unresolved tickets.
- It identifies overdue tickets that remain unresolved.
- It records a WARNING activity event for an overdue ticket.
- It prevents duplicate warning events for the same ticket.
- It does not automatically change the ticket status.
- MongoDB write failures are handled so that the scheduled task can
  continue and a later scan can retry.
Testing and Verification
1. Execute Automated Tests
Start Docker Desktop, then run the following command from the project
root:
.\mvnw.cmd clean verify
The test profile uses an H2 in-memory database for relational database
tests and Testcontainers to start MongoDB for integration testing.
2. Test Coverage
The automated test suite includes the following test classes:
- HelpdeskApiApplicationTests.java -- Verifies that the Spring
  Boot application context loads successfully.
- OverdueTicketSchedulerTest.java -- Tests overdue warning
  creation, multiple overdue tickets, duplicate prevention, and safe
  handling of MongoDB write failures and duplicate-key exceptions.
- JwtAuthenticationFilterTest.java -- Tests JWT authentication
  filter behavior, including token validation and rejected
  authentication cases.
- UserServiceTest.java -- Tests user service behavior, including
  password hashing and authentication-related operations.
3. Manual Application Verification
Use Swagger UI to verify the API functionality.

Recommended manual checks:
- Register a new requester account.
- Log in with valid credentials and receive a JWT.
- Attempt login with invalid credentials.
- Access a protected endpoint without a token and verify that the
  request is rejected.
- Access a protected endpoint with a valid token.
- Verify role, ownership, assignment, and ticket workflow
  restrictions.
- Claim and assign tickets using the appropriate roles.
- Move a ticket through valid status transitions.
- Add and retrieve ticket comments.
- Verify that the corresponding activity events are stored in MongoDB.
- Log out and verify that the revoked token can no longer access
  protected endpoints.
- Verify ticket filtering and pagination.
- Verify overdue warning generation.
- Check application health through Actuator.
4. Test Results
Maven displays the test execution summary in the terminal. Detailed
reports are generated under:
target/surefire-reports/
A successful verification ends with:
BUILD SUCCESS
Project Structure
The application follows a layered package structure.
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
│                   ├── config/
│                   │   └── JwtAuthenticationFilterTest.java
│                   ├── service/
│                   │   └── UserServiceTest.java
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
Package Responsibilities
  Package                             Responsibility
  advice                            Handles global API exception
responses.

  aspect                            Implements aspect-oriented logging
for service operations.

  config                            Contains application, security, and
API documentation configuration.

  controller                        Exposes REST API endpoints.

  document                          Contains MongoDB document models.

  dto                               Defines request and response data
transfer objects.

  entity                            Contains relational database
entities.

  enums                             Defines application-specific
enumerations.

  event                             Handles application events and
event listeners.

  exception                         Contains custom application
exceptions.

  mapper                            Handles object and DTO mapping.

  repository                        Provides database access
operations.

  scheduler                         Contains scheduled background
tasks.

  service                           Implements application business
logic.
validation                        Handles application configuration
validation.
API Endpoint Reference
All application business APIs use the /api/v2 base path.

Protected endpoints require a valid JWT Bearer token in the
Authorization header. Registration and login are public. Logout
requires authentication.
1. Authentication APIs
Base path: /api/v2/auth
  Method            Endpoint                  Access            Purpose
  POST              /api/v2/auth/register   Public            Registers a new
REQUESTER
account.

  POST              /api/v2/auth/login      Public            Validates
credentials and
returns a signed
JWT access token.
POST              /api/v2/auth/logout     Authenticated     Revokes the current
token and returns
204 No Content.
Registration Request Example
{
  "fullName": "Example Requester",
  "email": "requester@example.com",
  "password": "Choose-A-Strong-Password"
}
The registration API does not allow users to select their own role.
Every self-registered account receives the REQUESTER role.

The login response contains a signed JWT access token. Use that token to
access protected endpoints.
2. User API
  Method            Endpoint                            Access            Purpose
  POST              /api/v2/users/admin/create-user   ADMIN           Allows an
administrator to
create privileged
AGENT or
ADMIN accounts.
3. Ticket APIs
Base path: /api/v2/tickets
  Method            Endpoint                              Access            Purpose
  POST              /api/v2/tickets                     REQUESTER       Creates a support
ticket.

  GET               /api/v2/tickets/{ticketId}          Authenticated;    Retrieves ticket
ownership and     details.
role rules apply

  GET               /api/v2/tickets                     Authenticated;    Retrieves tickets
role-based        with filtering
filtering applies and pagination.

  PATCH             /api/v2/tickets/{ticketId}/status   Assigned AGENT  Updates ticket
or owning         status according
REQUESTER,      to the workflow.
depending on the
transition

  PATCH             /api/v2/tickets/{ticketId}/claim    AGENT           Claims an
eligible
unassigned
ticket.
PATCH             /api/v2/tickets/{ticketId}/assign   ADMIN           Assigns an agent
to a ticket.
4. Comment APIs
Base path: /api/v2/tickets/{ticketId}/comments
  Method            Endpoint                                Access            Purpose
  POST              /api/v2/tickets/{ticketId}/comments   Authenticated     Adds a comment to
user; ticket      a ticket.
access rules
apply
GET               /api/v2/tickets/{ticketId}/comments   Authenticated     Retrieves
user; ticket      comments
access rules      associated with a
apply             ticket.
Authentication Header
For protected endpoints, include the following HTTP header:
Authorization: Bearer <JWT_ACCESS_TOKEN>
Authentication and Authorization Responses
- 401 Unauthorized -- Returned when the token is missing,
  malformed, expired, invalid, or revoked.
- 403 Forbidden -- Returned when an authenticated user fails an
  existing role, ownership, assignment, or workflow rule.
Ticket Filtering and Pagination
The ticket retrieval endpoint supports filtering and pagination.

Endpoint: GET /api/v2/tickets
Available Query Parameters
  Parameter               Description             Values / Default
  status                Filters tickets by      OPEN, IN_PROGRESS,
current status.         RESOLVED, CLOSED

  priority              Filters tickets by      HIGH, MEDIUM, LOW
priority.

  page                  Specifies the page      Default 0; minimum
number.                 0
size                  Specifies the number of Default 10; allowed
records per page.       range 1 to 100
All query parameters are optional.
Example Requests
Retrieve all tickets using default pagination:
GET /api/v2/tickets
Retrieve open tickets:
GET /api/v2/tickets?status=OPEN
Retrieve high-priority tickets:
GET /api/v2/tickets?priority=HIGH
Retrieve open and high-priority tickets:
GET /api/v2/tickets?status=OPEN&priority=HIGH
Retrieve the second page with five records:
GET /api/v2/tickets?page=1&size=5
Combine filtering and pagination:
GET /api/v2/tickets?status=IN_PROGRESS&priority=MEDIUM&page=0&size=10
Pagination Validation
- Page numbers cannot be negative.
- Page size must be between 1 and 100.
- Invalid pagination values are rejected by the application.
Error Handling
The application uses centralized exception handling through Spring's
@RestControllerAdvice to provide consistent error responses.
HTTP Error Responses
- 400 Bad Request
  - Returned when request validation fails.
  - Returned when invalid ticket status transitions are attempted.
  - Returned when request parameters or request bodies contain
    invalid data.
- 401 Unauthorized
  - Returned when authentication is missing or a JWT is malformed,
    invalid, expired, or revoked.
  - Handled by the Spring Security authentication configuration.
- 403 Forbidden
  - Returned when an authenticated user attempts an operation
    without the required permissions.
- 500 Internal Server Error
  - Returned when an unexpected application exception occurs.
Error Response Structure
Error responses use the ErrorResponse DTO and contain:
- timestamp -- Date and time when the error occurred.
- status -- HTTP status code.
- error -- Description of the error.
- message -- Details about the error.
Example:
{
  "timestamp": "2026-11-13T14:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid ticket status transition."
}
The example illustrates the response structure. Actual messages depend
on the exception and validation failure.
Database Schema and Index Management
1. SQL Server Schema Management
The application uses Spring Data JPA and Hibernate to manage the
relational database schema.

The local profile uses:
spring:
  jpa:
    hibernate:
      ddl-auto: update
- Missing database tables are created automatically.
- Schema updates are applied based on the JPA entity definitions.
- Existing database records are generally preserved.
Create the HelpDeskDB database in SQL Server before starting the
application. Hibernate manages the application tables during startup.
2. Test Database Schema
The test profile uses:
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop
This configuration creates the database schema for testing and drops it
when the test context is closed.
3. MongoDB Index Management
MongoDB indexes improve query performance and help maintain data
integrity.

The application uses activity indexes, including:
- ticket_createdAt_idx -- Supports ticket activity retrieval ordered
  by creation time.
- unique_ticket_warning_idx -- A unique partial index on ticketId
  and action, restricted to WARNING events.
If the unique partial index is not automatically created, execute:
db.activities.createIndex(
    { ticketId: 1, action: 1 },
    {
        name: "unique_ticket_warning_idx",
        unique: true,
        partialFilterExpression: { action: "WARNING" }
    }
);
Dependencies and Build Tools
The project uses Maven for dependency management and build automation.
The Maven Wrapper is included, so a separate Maven installation is not
required.
Core Dependencies
- Spring Boot WebMVC -- Provides REST API development and HTTP
  request handling.
- Spring Data JPA -- Handles relational database operations using
  Hibernate.
- Microsoft SQL Server JDBC Driver -- Connects the application to
  SQL Server.
- Spring Data MongoDB -- Handles document storage and retrieval in
  MongoDB.
- Spring Boot Validation -- Validates incoming request data.
- Spring Security -- Provides authentication and authorization.
- JJWT -- Handles JWT creation and token validation.
- BCrypt -- Hashes user passwords.
- Spring Boot Actuator -- Provides application health and
  monitoring endpoints.
- Spring Boot AspectJ -- Supports aspect-oriented programming and
  operation logging.
- Springdoc OpenAPI -- Generates API documentation and Swagger UI.
Testing Dependencies
- Spring Boot Test -- Supports application and integration
  testing.
- JUnit 5 -- Provides the testing framework.
- Mockito -- Supports unit testing with mocks.
- H2 Database -- Provides an in-memory relational database for
  tests.
- Testcontainers -- Provides container-based MongoDB testing.
Build Tool
- Maven Wrapper (mvnw and mvnw.cmd) is used to build, test, and
  package the application.
- Java 21 is the project's required Java version.
- The Spring Boot Maven Plugin packages the application as an
  executable JAR.
Security Notes
- Never commit real database passwords, JWT secrets, or access tokens.
- Use a strong, randomly generated JWT secret that decodes to at least
  32 bytes.
- Keep the JWT secret consistent for the lifetime of the running
  application.
- Self-registration creates only REQUESTER accounts.
- Privileged user creation is restricted to administrators.
- Logging out revokes the presented token; subsequent requests using
  that token should be rejected.
- A valid JWT does not override ticket ownership, assignment, role, or
  workflow authorization rules.
Version Information
- API version: v2
- Java version: 21
- Spring Boot version: 4.1.2
- Default JWT access-token lifetime: 24 hours (86400000
  milliseconds)
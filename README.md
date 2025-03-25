# Java Service Template

This is a template for creating new Java services. It is built using **Java 17** and **Spring Boot**, with added support for:
- Multi-tenant **MySQL** databases
- **Redis** for caching
- **Flyway** for database migration
- **kafka** Integration as producer

## Maintainers
| Manager   | Developer   |
|-----------|------------|
| `Manager` | `Dev Names` |

## Built With
- [Java 17](https://docs.oracle.com/en/java/javase/17/)
- [Spring Boot 3.1.4](https://spring.io/projects/spring-boot)
- [Maven](https://maven.apache.org/)

---

## Clean Architecture Overview

Below is the **Clean Architecture Diagram** showcasing **Dependency Injection** and **Layered Responsibility**:

![](CleanArchitecture.jpg)

- **Controller Layer**: Handles HTTP requests.
- **Use Case Layer**: Implements business logic.
- **Domain Layer**: Contains core domain models.
- **Repository Layer**: Interface to data persistence.
- **Database Layer**: Stores application data.

Dependency Injection is applied so that higher layers **only depend on abstractions, not concrete implementations**.

---

## Folder Structure
```
project-root/
├── src/main/java/com/olx/boilerplate/
│   ├── controller/    # Handles HTTP requests
│   ├── usercase/      # Business logic (Use Cases)
│   ├── domain/        # Core business models
│   ├── repository/    # Data access layer
│   ├── config/        # Configuration files
│   ├── infrastructure/ # External system integrations
│
├── src/test/java/com/olx/boilerplate/
│   ├── unit/          # Unit tests (mock dependencies)
│   ├── integration/   # Integration tests (end-to-end flows)
│
├── docker-compose-local.yml  # Local environment setup
├── migrate.sh                # DB migration script
├── README.md                 # Project documentation
```

---

## Local Setup

1. **Start Docker**.
2. **Clone the repository** and start the required containers:
   ```sh
   git clone {project-repo}
   cd {project-path}
   docker-compose -f docker-compose-local.yml up -d
   ```
3. **Run Flyway migrations** to initialize the database:
   ```sh
   ./migrate.sh
   ```
4. **Start the Spring Boot Application** with the `local` profile:
   ```sh
   mvn spring-boot:run -Dspring.profiles.active=local
   ```

---

## Features & Plugins
1. **SpotBugs** (Static code analysis)
2. **Maven Formatter** (Ensure code consistency)
   ```sh
   mvn formatter:format
   ```
3. **Dependency Check** (Find vulnerable dependencies)
4. **Jarvis (Integration Tests)**

---

## Integration Testing (IT) - Example `.feature` Files

### Create User API Test
```gherkin
Scenario: Create a new user successfully
  Given I have /user API
  And I have following headers
    | Content-Type |
    | application/json |
  And I have a request body in {"name":"Test User", "email": "test@email.com"}
  When Execute POST request using REST
  Then Validate status code is: 201
  And Validate user response
```

### Delete User API Test
```gherkin
Scenario: Delete an existing user
  Given I have /user/{id} API
  When Execute DELETE request using REST
  Then Validate status code is: 200
  And Validate user deletion response
```

---

## Testing Strategy

### Unit Tests (UTs)
- Test individual components in **isolation** using **mocked dependencies**.
- Tools: **JUnit, Mockito**.

### Integration Tests (ITs)
- Test **end-to-end flows** with actual **database and services**.
- Uses **Docker containers** for **MySQL & Redis**.
- Runs inside the **`integration-test` profile**.

---

## FAQs

### 1. Java Version Issues
```sh
Execution spotbugs of goal com.github.spotbugs:spotbugs-maven-plugin failed: Unsupported class file major version
```
- Ensure you are running **Java 17**:
  ```sh
  java -version
  ```
- Set Java 17 as default:
  ```sh
  export JAVA_HOME=$(/usr/libexec/java_home -v 17)
  ```
---

---

🚀 **You're all set! Happy coding!**


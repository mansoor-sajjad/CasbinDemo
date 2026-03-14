# Production Readiness Guide

This project is currently a demonstration. To make it production-ready, you must implement the following:

## Security & Authentication (Critical)
*   **Implement Real Authentication:** Replace the mocked `AuthController` login with a true user store, implement Spring Security's `UserDetailsService`, and verify passwords using `BCryptPasswordEncoder`.
*   **Secure the JWT Secret:** The `jwt.secret` is currently hardcoded in `application.properties`. Extract this and inject it via environment variables (e.g., `jwt.secret=${JWT_SECRET}`) or a secure vault.
*   **Enforce HTTPS/TLS:** Serve the API over HTTPS to prevent JWT interception. Terminate TLS at a load balancer or configure Tomcat to require SSL.
*   **Configure CORS:** Implement a strict Cross-Origin Resource Sharing (CORS) policy if a web frontend will consume the API.

## Observability & Monitoring
*   **Add Spring Boot Actuator:** Include `spring-boot-starter-actuator` to expose health and metrics endpoints (`/actuator/health`).
*   **Global Exception Handling:** Implement a `@ControllerAdvice` class to catch exceptions globally and return standardized JSON error responses (e.g., RFC 7807 Problem Details) without leaking stack traces.
*   **Structured Logging:** Configure Logback to output logs in a structured format (like JSON) for centralized logging systems.

## Database & Connection Management
*   **Externalize Database Properties:** Use environment variables for `spring.datasource.username` and `spring.datasource.password`.
*   **Tune Connection Pooling:** Explicitly configure HikariCP pool settings (e.g., `maximum-pool-size`) based on load and capacity.
*   **Casbin Policy Management:** Build a secure administrative mechanism (UI or API) to manage Casbin rules in the database at runtime.

## Deployment & Infrastructure
*   **Containerization:** Create a `Dockerfile` using a lean base image (like Eclipse Temurin JRE or Alpine).
*   **JVM Tuning:** Set sensible memory limits using JVM flags (e.g., `-XX:MaxRAMPercentage=75`) for container environments.
*   **Automated Testing & CI/CD:** Establish pipelines to run unit/integration tests and static code analysis automatically on every commit.

# Technical and Architectural Overview

The **CasbinDemo** application is a demo project built with Java 25 and Spring Boot. It demonstrates a secure, multi-tenant RESTful API utilizing JWT for authentication, Casbin for fine-grained authorization, and Hibernate for database interaction.

## 1. Technology Stack

*   **Java Version:** 25 (utilizing preview features like pattern matching in switch statements)
*   **Framework:** Spring Boot 3.x (indicated by `jakarta.*` imports and Spring Security 6)
*   **Database:** PostgreSQL
*   **Database Migrations:** Flyway
*   **ORM:** Hibernate / Spring Data JPA
*   **Security & Authentication:** Spring Security with JSON Web Tokens (JJWT)
*   **Authorization:** Casbin (jCasbin) with JDBC Adapter

## 2. Core Architecture

The architecture follows a standard layered Spring Boot structure (Controller, Service/Security Security Guards, Repository) enhanced with filters and contexts for multi-tenancy and advanced authorization.

### 2.1. Authentication Flow (JWT)
1.  **Login ([AuthController](file:///home/mansoor-sajjad/Projects/CasbinDemo/src/main/java/com/eldmatix/CasbinDemo/controller/AuthController.java#11-34)):** A client sends a POST request with `username` and `tenantId`. A JWT is generated containing the `subject` (username) and a custom claim `tenantId`. (Note: Password validation is omitted for demonstration purposes).
2.  **Request Interception ([JwtAuthenticationFilter](file:///home/mansoor-sajjad/Projects/CasbinDemo/src/main/java/com/eldmatix/CasbinDemo/security/JwtAuthenticationFilter.java#17-75)):** Every incoming request passing through the `SecurityFilterChain` is intercepted by this filter.
3.  **Token Processing:** The filter extracts the JWT, validates it, and retrieves the `username` and `tenantId`.
4.  **Context Population:** It populates the standard `SecurityContextHolder` with an `UsernamePasswordAuthenticationToken` and leverages Java's `ScopedValue` to set the tenant context securely for the current thread/record via `TenantContext`.

### 2.2. Multi-Tenancy (Hibernate mapping)
1.  **Tenant Context ([TenantContext.java](file:///home/mansoor-sajjad/Projects/CasbinDemo/src/main/java/com/eldmatix/CasbinDemo/security/TenantContext.java)):** Holds the current `tenantId` (defaulting to "DEFAULT" if unspecified) resolved during the JWT filtering phase.
2.  **Hibernate Integration ([HibernateConfig.java](file:///home/mansoor-sajjad/Projects/CasbinDemo/src/main/java/com/eldmatix/CasbinDemo/config/HibernateConfig.java)):** 
    *   Implements `CurrentTenantIdentifierResolver` to return the value from `TenantContext.currentTenant()`.
    *   Uses a `BeanPostProcessor` to register this resolver, enforcing that all database queries automatically scope to the correct tenant dynamically.

### 2.3. Authorization Flow (Casbin)
1.  **Configuration ([CasbinConfig](file:///home/mansoor-sajjad/Projects/CasbinDemo/src/main/java/com/eldmatix/CasbinDemo/config/CasbinConfig.java#15-35)):** Initializes the Casbin `Enforcer`. It uses a `JDBCAdapter` connected to the active DataSource to load policies directly from the PostgreSQL database (table usually named `casbin_rule`).
2.  **Model ([casbin/model.conf](file:///home/mansoor-sajjad/Projects/CasbinDemo/src/main/resources/casbin/model.conf)):** Defines an Access Control List (ACL) model structured as `sub, dom, obj, act` (Subject, Domain/Tenant, Object/Resource, Action).
3.  **Authorization Guard ([CasbinGuard](file:///home/mansoor-sajjad/Projects/CasbinDemo/src/main/java/com/eldmatix/CasbinDemo/security/CasbinGuard.java#8-26)):** Implements an `AuthorizationGuard` interface. It retrieves the current authenticated user (`sub`) and the active tenant context (`dom`). 
4.  **Enforcement ([ShipmentController](file:///home/mansoor-sajjad/Projects/CasbinDemo/src/main/java/com/eldmatix/CasbinDemo/controller/ShipmentController.java#13-57)):** Controller methods explicitly invoke the guard (e.g., `guard.allow("shipment", "read")`). The guard asks the `Enforcer` to evaluate the request against the database policies. If denied, an `AccessDeniedException` is thrown.

## 3. Directory Structure Summary

*   **`config/`**: Configuration classes for Casbin, Hibernate (Multi-tenancy), and Spring Security.
*   **`controller/`**: REST API endpoints mapping ([AuthController](file:///home/mansoor-sajjad/Projects/CasbinDemo/src/main/java/com/eldmatix/CasbinDemo/controller/AuthController.java#11-34) for JWT generation, [ShipmentController](file:///home/mansoor-sajjad/Projects/CasbinDemo/src/main/java/com/eldmatix/CasbinDemo/controller/ShipmentController.java#13-57) for protected resource access).
*   **`dto/`**: Data Transfer Objects (e.g., `ShipmentDto`).
*   **`model/` & `repository/`**: JPA Entities ([Shipment](file:///home/mansoor-sajjad/Projects/CasbinDemo/src/main/java/com/eldmatix/CasbinDemo/controller/ShipmentController.java#13-57)) and Spring Data Repositories for data access.
*   **[security/](file:///home/mansoor-sajjad/Projects/CasbinDemo/src/main/java/com/eldmatix/CasbinDemo/config/SecurityConfig.java#22-34)**: Core security logic, including JWT generation/validation (`JwtTokenProvider`), thread-local context (`TenantContext`), HTTP structural filtering ([JwtAuthenticationFilter](file:///home/mansoor-sajjad/Projects/CasbinDemo/src/main/java/com/eldmatix/CasbinDemo/security/JwtAuthenticationFilter.java#17-75)), and the Casbin abstraction layer ([CasbinGuard](file:///home/mansoor-sajjad/Projects/CasbinDemo/src/main/java/com/eldmatix/CasbinDemo/security/CasbinGuard.java#8-26)).
*   **`resources/casbin/`**: Contains the [model.conf](file:///home/mansoor-sajjad/Projects/CasbinDemo/src/main/resources/casbin/model.conf) for the Casbin policy engine definition.

## 4. Key Takeaways and Highlights

*   **Stateless Scaling:** Leveraging stateless JWTs ensures horizontal scalability without session replication.
*   **Tenant Isolation:** The robust implementation of `ScopedValue` and Hibernate's `CurrentTenantIdentifierResolver` ensures strict data isolation at the ORM query level between tenants.
*   **Dynamic Policies:** By using Casbin's JDBC adapter, authorization policies can be added, updated, or removed in the database at runtime without requiring application restarts or code changes.

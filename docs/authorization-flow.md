# Authentication & Authorization Technical Flow

This document outlines how authentication (verifying *who* a user is) and authorization (verifying *what* they can do) work within the EldMatix application, particularly focusing on our multi-tenant architecture and Casbin integration.

---

## 1. Authentication Flow

When a user logs into the system:

1. **Login Request**: The client sends a `POST` request to `/api/auth/login` containing the user's `email` (treated as the username) and `password`.
2. **Verification**: The `AuthController` delegates the credentials to Spring Security's `AuthenticationManager` to verify them against the database (`UserRepository`).
3. **JWT Generation**: If valid, the system retrieves the user's details, including their associated `tenantId`. A JWT (JSON Web Token) is generated, embedding the `email` as the subject and the `tenantId` as a custom claim.
4. **Token Usage**: The client must include this JWT in the `Authorization: Bearer <token>` header for all subsequent API requests.

---

## 2. Authorization Flow

For every protected API call, the system must verify if the user is allowed to perform the requested action.

1. **Token Interception**: A Spring Security filter intercepts the request, validates the JWT signature, and checks its expiration.
2. **Security Context**: The filter extracts the `email` and the `tenantId` from the token. It stores the `email` in the `SecurityContext` (so Spring knows who the user is) and stores the `tenantId` in a `ThreadLocal` context (often named `TenantContext`).
3. **Endpoint Protection**: Controllers use the `AuthorizationGuard` (injected as `CasbinGuard`) to enforce access. For example, `guard.allow("driver", "write")` is called before saving a driver.
4. **Casbin Evaluation**: The `CasbinGuard` retrieves the current user's email from the `SecurityContext` and the `tenantId` from the `TenantContext`. It passes these to the Casbin `Enforcer`, which evaluates the request against rules defined in the database.

---

## 3. Casbin Authorization Details

We use [Casbin](https://casbin.org/), a powerful and efficient open-source access control library.

### The Model Configuration (`casbin/model.conf`)

The `model.conf` file defines the abstract structure and evaluation logic of our rules. It uses an RBAC (Role-Based Access Control) with Domains (Tenants) model:

```ini
[request_definition]
r = sub, dom, obj, act

[policy_definition]
p = sub, dom, obj, act

[role_definition]
g = _, _, _

[policy_effect]
e = some(where (p.eft == allow))

[matchers]
m = g(r.sub, p.sub, r.dom) && r.dom == p.dom && r.obj == p.obj && r.act == p.act
```

- **sub**: Subject (the User or Role performing the action).
- **dom**: Domain (the `tenantId` the user belongs to).
- **obj**: Object (the resource being accessed, e.g., `driver`, `shipment`).
- **act**: Action (the operation, e.g., `read`, `write`).

*Key Takeaway: The `.conf` file is completely generic. It never hardcodes "admin" or "driver". It simply tells Casbin how to read the policies stored in the database.*

### The Database Rules (`casbin_rule` table)

All actual permissions are stored inside your PostgreSQL `casbin_rule` table. The `Enforcer` automatically loads these rules to evaluate requests.

#### The `ptype` Column
The `ptype` (Policy Type) determines whether a row acts as a direct permission (`p`) or a role assignment (`g`).

#### 1. Role Permission Policies (`p`)
Assigns an action on a resource to a role within a specific tenant.
- **Mapping:** `v0` (Role), `v1` (Tenant), `v2` (Resource), `v3` (Action)
- **Database Example:** 
  `ptype='p', v0='ADMIN', v1='DEFAULT', v2='driver', v3='read'`
  *(Result: the ADMIN role can read drivers in the DEFAULT tenant)*

#### 2. Grouping / Role Assignments (`g`)
Assigns a user to a specific role within a tenant domain.
- **Mapping:** `v0` (User), `v1` (Role), `v2` (Tenant)
- **Database Example:**
  `ptype='g', v0='alice@example.com', v1='ADMIN', v2='DEFAULT'`
  *(Result: alice@example.com receives the ADMIN role in the DEFAULT tenant)*

### Recommended RBAC Strategy

To keep your policies manageable as the application grows:

1. **Define base permissions for roles (using `p`)**:
   - `p, ADMIN, DEFAULT, driver, read`
   - `p, ADMIN, DEFAULT, driver, write`
   - `p, DRIVER, DEFAULT, driver, read`

2. **Assign users directly to roles (using `g`)**:
   - `g, driver@example.com, DRIVER, DEFAULT`
   - `g, alice@example.com, ADMIN, DEFAULT`

Casbin's matcher evaluates the `g` mappings and automatically grants `alice@example.com` the right to `write` drivers through the ADMIN role.

# Role & Permission Management Implementation

## Overview
This document describes the Role & Permission Management system implemented in Phase 1 of the production readiness plan.

## Database Schema

### Tables Created
- **roles**: Stores role definitions (ADMIN, OPERATOR, DRIVER)
- **permissions**: Stores permission definitions (resource:action pairs)
- **role_permissions**: Many-to-many mapping between roles and permissions
- **user_roles**: Many-to-many mapping between users and roles

### Default Roles
- **ADMIN**: Full access to all resources
- **OPERATOR**: Access to shipment and driver management
- **DRIVER**: Read-only access to shipments and drivers

### Sample Permissions
- `shipment:read`, `shipment:create`, `shipment:update`, `shipment:delete`
- `driver:read`, `driver:create`, `driver:update`, `driver:delete`
- `user:read`, `user:create`, `user:update`, `user:delete`
- `role:read`, `role:create`, `role:update`, `role:delete`
- `permission:read`, `permission:create`, `permission:update`, `permission:delete`

## Entities

### User
```java
@Entity
public class User {
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles")
    private Set<Role> roles;
}
```

### Role
```java
@Entity
public class Role {
    private String name;
    private String description;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "role_permissions")
    private Set<Permission> permissions;
}
```

### Permission
```java
@Entity
public class Permission {
    private String name;          // e.g., "shipment:read"
    private String description;
    private String resource;      // e.g., "shipment"
    private String action;        // e.g., "read"
    
    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles;
}
```

## JWT Token Changes

### Token Claims
The JWT token now includes:
- `sub`: username
- `tenantId`: tenant identifier
- **`roles`**: Array of role names
- **`permissions`**: Array of permission names

Example JWT payload:
```json
{
  "sub": "user@example.com",
  "tenantId": "DEFAULT",
  "roles": ["ADMIN", "OPERATOR"],
  "permissions": ["shipment:read", "shipment:create", "driver:read"],
  "iat": 1704067200,
  "exp": 1704153600
}
```

## API Endpoints

### Role Management
- `GET /api/roles` - Get all roles
- `GET /api/roles/{id}` - Get role by ID
- `POST /api/roles` - Create new role
- `PUT /api/roles/{id}` - Update role
- `DELETE /api/roles/{id}` - Delete role
- `POST /api/roles/{roleId}/permissions/{permissionId}` - Assign permission to role
- `DELETE /api/roles/{roleId}/permissions/{permissionId}` - Remove permission from role

### Permission Management
- `GET /api/permissions` - Get all permissions
- `GET /api/permissions/{id}` - Get permission by ID
- `GET /api/permissions/resource/{resource}` - Get permissions by resource
- `POST /api/permissions` - Create new permission
- `PUT /api/permissions/{id}` - Update permission
- `DELETE /api/permissions/{id}` - Delete permission

### User Role Management
- `GET /api/users` - Get all users
- `GET /api/users/{username}` - Get user info
- `GET /api/users/{username}/roles` - Get user's roles
- `POST /api/users/{username}/roles/{roleId}` - Assign role to user
- `DELETE /api/users/{username}/roles/{roleId}` - Remove role from user

## Usage Examples

### Login Response
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "email": "admin@example.com",
    "tenantId": "DEFAULT",
    "roles": ["ADMIN"]
  }
}
```

### Create Role
```bash
POST /api/roles
{
  "name": "SUPERVISOR",
  "description": "Supervisor with limited management access",
  "permissionIds": [1, 2, 5, 6]
}
```

### Assign Role to User
```bash
POST /api/users/driver@example.com/roles/3
```

## Frontend Integration

### React Web App
1. Extract roles and permissions from JWT token
2. Use for route-level access control
3. Show/hide UI elements based on user roles
4. Implement role-based API calls

```javascript
// Example: Check if user has role
const hasRole = (token, role) => {
  const decoded = jwtDecode(token);
  return decoded.roles.includes(role);
};

// Example: Check if user has permission
const hasPermission = (token, permission) => {
  const decoded = jwtDecode(token);
  return decoded.permissions.includes(permission);
};
```

### React Native Driver App
1. Validate login response includes DRIVER role
2. Show error if user doesn't have driver role
3. Display driver-specific permissions on dashboard

```javascript
// Block login if role is not DRIVER
if (!loginResponse.user.roles.includes('DRIVER')) {
  throw new Error('Only drivers can access this app');
}
```

## Security Notes

1. **Multi-tenancy**: All roles and permissions are tenant-scoped
2. **Role-based Access Control (RBAC)**: Implemented at entity level
3. **Permission Format**: `resource:action` (e.g., "shipment:create")
4. **Eager Loading**: Roles and permissions loaded eagerly for JWT token generation
5. **TenantContext**: Ensures all operations respect tenant isolation

## Next Steps (Phase 2)

1. Implement multi-app support (web, driver-android, driver-ios)
2. Add client type validation per application
3. Implement role validation on client apps
4. Add app-specific authorization checks

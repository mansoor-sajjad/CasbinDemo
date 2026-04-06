# API Endpoints & cURL Commands

This document contains example `curl` commands to interact with the EldMatix REST API.

---

## 1. Authentication (Login)
To access protected endpoints, you must first authenticate and obtain a JWT token. The token now includes your roles and permissions for offline validation.

```bash
curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{
           "username": "your_email@example.com",
           "password": "your_password"
         }'
```

**Response Details:**
On a successful login, the server will return a JSON payload containing the JWT `token` and user details including roles. You will need this token for the subsequent requests.

**Example Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwi...",
  "user": {
    "email": "user@example.com",
    "tenantId": "DEFAULT",
    "roles": ["ADMIN", "OPERATOR"]
  }
}
```

**JWT Token Claims:**
- `sub` - Username/email
- `tenantId` - Tenant identifier
- `roles` - Array of role names (e.g., ["ADMIN", "DRIVER"])
- `permissions` - Array of permission names (e.g., ["shipment:read", "driver:create"])
- `iat` - Issued at timestamp
- `exp` - Expiration timestamp

*Tip: Save the token into a variable for easier use in your terminal.*
```bash
TOKEN="Paste-Your-JWT-Token-Here"
```

---

## 2. Roles Management

### Get all Roles
Retrieve all roles available in your tenant.

```bash
curl -X GET http://localhost:8080/api/roles \
     -H "Authorization: Bearer $TOKEN"
```

### Get Role by ID
Retrieve a specific role by its ID.

```bash
curl -X GET http://localhost:8080/api/roles/1 \
     -H "Authorization: Bearer $TOKEN"
```

### Create a Role
Create a new role with assigned permissions.

```bash
curl -X POST http://localhost:8080/api/roles \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer $TOKEN" \
     -d '{
           "name": "SUPERVISOR",
           "description": "Supervisor with limited management access",
           "permissionIds": [1, 2, 5, 6]
         }'
```

### Update a Role
Update an existing role's name, description, and permissions.

```bash
curl -X PUT http://localhost:8080/api/roles/1 \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer $TOKEN" \
     -d '{
           "name": "SUPERVISOR_V2",
           "description": "Updated supervisor role",
           "permissionIds": [1, 2, 3, 5, 6]
         }'
```

### Delete a Role
Delete a role from the system.

```bash
curl -X DELETE http://localhost:8080/api/roles/1 \
     -H "Authorization: Bearer $TOKEN"
```

### Assign Permission to Role
Add a permission to a role.

```bash
curl -X POST http://localhost:8080/api/roles/1/permissions/5 \
     -H "Authorization: Bearer $TOKEN"
```

### Remove Permission from Role
Remove a permission from a role.

```bash
curl -X DELETE http://localhost:8080/api/roles/1/permissions/5 \
     -H "Authorization: Bearer $TOKEN"
```

---

## 3. Permissions Management

### Get all Permissions
Retrieve all permissions available in your tenant.

```bash
curl -X GET http://localhost:8080/api/permissions \
     -H "Authorization: Bearer $TOKEN"
```

### Get Permission by ID
Retrieve a specific permission by its ID.

```bash
curl -X GET http://localhost:8080/api/permissions/1 \
     -H "Authorization: Bearer $TOKEN"
```

### Get Permissions by Resource
Filter permissions by resource type.

```bash
curl -X GET http://localhost:8080/api/permissions/resource/shipment \
     -H "Authorization: Bearer $TOKEN"
```

### Create a Permission
Create a new permission.

```bash
curl -X POST http://localhost:8080/api/permissions \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer $TOKEN" \
     -d '{
           "name": "shipment:download",
           "description": "Permission to download shipment documents",
           "resource": "shipment",
           "action": "download"
         }'
```

### Update a Permission
Update an existing permission.

```bash
curl -X PUT http://localhost:8080/api/permissions/1 \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer $TOKEN" \
     -d '{
           "name": "shipment:download",
           "description": "Updated permission to download shipment documents",
           "resource": "shipment",
           "action": "download"
         }'
```

### Delete a Permission
Delete a permission from the system.

```bash
curl -X DELETE http://localhost:8080/api/permissions/1 \
     -H "Authorization: Bearer $TOKEN"
```

---

## 4. User Management

### Get all Users
Retrieve all users in your tenant.

```bash
curl -X GET http://localhost:8080/api/users \
     -H "Authorization: Bearer $TOKEN"
```

### Get User by Username
Retrieve a specific user's information.

```bash
curl -X GET http://localhost:8080/api/users/user@example.com \
     -H "Authorization: Bearer $TOKEN"
```

### Get User Roles
Retrieve all roles assigned to a user.

```bash
curl -X GET http://localhost:8080/api/users/user@example.com/roles \
     -H "Authorization: Bearer $TOKEN"
```

**Example Response:**
```json
[
  {
    "id": 1,
    "name": "ADMIN",
    "description": "Administrator with full access"
  },
  {
    "id": 2,
    "name": "OPERATOR",
    "description": "Operator with shipment and driver management"
  }
]
```

### Assign Role to User
Add a role to a user.

```bash
curl -X POST http://localhost:8080/api/users/user@example.com/roles/3 \
     -H "Authorization: Bearer $TOKEN"
```

### Remove Role from User
Remove a role from a user.

```bash
curl -X DELETE http://localhost:8080/api/users/user@example.com/roles/3 \
     -H "Authorization: Bearer $TOKEN"
```

---

## 5. Drivers

### Add a Driver
Create a new driver record in the database. The driver will be automatically associated with the authenticated user's tenant context.

```bash
curl -X POST http://localhost:8080/api/drivers \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer $TOKEN" \
     -d '{
           "firstName": "Mansoor",
           "lastName": "Sajjad",
           "email": "mansoor@parallelogram.com",
           "phone": "+47 481 33 963",
           "dateOfBirth": "1985-11-27",
           "licenseNumber": "D473-345-987",
           "licenseState": "CA",
           "licenseClass": "B",
           "licenseExpiry": "2033-11-16"
         }'
```

### Get all Drivers
Retrieve the list of all drivers associated with your tenant.

```bash
curl -X GET http://localhost:8080/api/drivers \
     -H "Authorization: Bearer $TOKEN"
```

---

## 6. Shipments

### Add a Shipment
Create a new shipment record.

```bash
curl -X POST http://localhost:8080/api/shipments \
     -H "Content-Type: application/json" \
     -H "Authorization: Bearer $TOKEN" \
     -d '{
           "description": "Electronics delivery to Warehouse 4"
         }'
```

### Get all Shipments
Retrieve the list of all shipments associated with your tenant.

```bash
curl -X GET http://localhost:8080/api/shipments \
     -H "Authorization: Bearer $TOKEN"
```

# API Endpoints & cURL Commands

This document contains example `curl` commands to interact with the EldMatix REST API.

---

## 1. Authentication (Login)
To access protected endpoints, you must first authenticate and obtain a JWT token.

```bash
curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{
           "email": "your_username",
           "password": "your_password"
         }'
```

**Response Details:**
On a successful login, the server will return a JSON payload containing the JWT `token` and user details. You will need this token for the subsequent requests.

*Tip: Save the token into a variable for easier use in your terminal.*
```bash
TOKEN="Paste-Your-JWT-Token-Here"
```

---

## 2. Drivers

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

## 3. Shipments

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

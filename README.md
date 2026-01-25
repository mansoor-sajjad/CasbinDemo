# Manual Verification Walkthrough

The application is running and Casbin is configured. You can manually verify access to the `/shipments` endpoint using `curl`.

## 1. Login to get a JWT
Authenticate as `alice` in `tenant1` to receive a Bearer token.

```bash
curl -X POST "http://localhost:8080/auth/login?username=alice&tenantId=tenant1"
```

> [!NOTE]
> For this demonstration, the `AuthController` issues a JWT without password validation. In a production system, you would include a `password` parameter and validate it against a user database.

**Response:** You will receive a long JWT string (e.g., `eyJhbGciOi...`). Copy this token.

## 2. Access Protected Endpoint
Use the token from step 1 in the `Authorization` header.

```bash
curl -X GET http://localhost:8080/shipments \
  -H "Authorization: Bearer <YOUR_TOKEN_HERE>"
```

**Response:**
- **Success (200 OK):** Returns `[]` (empty list of shipments).
- **Failure (403 Forbidden):** Returns `{"error": "Forbidden", ...}`.

## Current State
A policy has been inserted into the database:
- **User:** `alice`
- **Tenant:** `tenant1`
- **Resource:** `shipment`
- **Action:** `read`

This allows `alice` to successfully access the GET `/shipments` endpoint.

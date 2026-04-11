# Manual Verification Walkthrough

The application is running and Casbin is configured. You can manually verify access to the `/shipments` endpoint using `curl`.

## 1. Login to get a JWT

Send a `POST` request with a JSON body containing the user's email address and password. The `tenantId` is resolved automatically from the user's record in the database.

```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"username": "alice@example.com", "password": "yourpassword"}'
```

> [!NOTE]
> The `username` field must be a valid email address. Authentication is performed against the database — both the email and password must match a stored user record.

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
- **Failure (401 Unauthorized):** Returned when credentials are invalid.

## Current State
The database is seeded with role-based Casbin policies:
- **User-role mapping:** `g, alice@example.com, ADMIN, DEFAULT`
- **Role permissions:** `p, ADMIN, DEFAULT, shipment, read` and related ADMIN rules

This allows `alice@example.com` to successfully access protected shipment and driver endpoints in the `DEFAULT` tenant.

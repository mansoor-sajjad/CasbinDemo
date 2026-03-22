# CORS Configuration

The backend uses a configurable list of allowed origins for CORS, controlled via an environment variable.

## Environment Variable

| Variable | Required | Default |
|---|---|---|
| `CORS_ALLOWED_ORIGINS` | No | `https://eldmatix-web.onrender.com` |

### Format

A single origin or a comma-separated list of origins (no spaces):

```
CORS_ALLOWED_ORIGINS=https://eldmatix-web.onrender.com
```

```
CORS_ALLOWED_ORIGINS=https://eldmatix-web.onrender.com,http://localhost:3000
```

## Local Development

Add the following to `src/main/resources/application-local.properties`:

```properties
cors.allowed-origins=http://localhost:3000
```

## Production (Render)

Set `CORS_ALLOWED_ORIGINS` in your backend service's **Environment** settings on the Render dashboard.

> **Note:** Spring Boot automatically maps `CORS_ALLOWED_ORIGINS` → `cors.allowed-origins` via its relaxed binding rules. No additional configuration is needed.

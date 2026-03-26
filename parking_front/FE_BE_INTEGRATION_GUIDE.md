# Frontend-Backend Integration Guide

## 🎯 Overview

This guide documents the complete FE-BE integration fixes for the Parking Management System.

## ✅ Fixed Integration Issues

### 1. **JWT Authentication**
- ✅ Backend returns JWT token from `/api/users/login` and `/api/users/register`
- ✅ Frontend stores JWT in HTTP-only cookie: `jwt_token`
- ✅ All API requests include `Authorization: Bearer {token}` header
- ✅ Server-side hooks validate JWT for protected routes

### 2. **Environment Configuration**
- ✅ Created `src/lib/config.ts` for centralized configuration
- ✅ All server-side files use `process.env.BACKEND_HOST` and `process.env.BACKEND_PORT`
- ✅ Defaults: `BACKEND_HOST=backend`, `BACKEND_PORT=8080` (Docker)
- ✅ Supports localhost for development

### 3. **API Client (`src/lib/api/client.ts`)**
- ✅ Added JWT token management with localStorage
- ✅ Implemented all HTTP methods: GET, POST, PUT, DELETE
- ✅ Added `credentials: 'include'` for CORS cookie handling
- ✅ Proper error handling with JSON/text response parsing
- ✅ Automatic token extraction from API responses

### 4. **Type Definitions (`src/lib/types.ts`)**
- ✅ Fixed all type mismatches with backend API
  - `ChargingPoint` enum: `SLOW_CHARGER | FAST_CHARGER | FALSE`
  - `SpaceStatus` includes `BLOCKED` state
  - `InvoiceStatus`: `CREATED | PENDING | PAID | FAILED`
  - IDs as strings (UUIDs for spaces/zones, numbers for users/reservations)
  - Proper timestamp format (ISO 8601)
- ✅ Added `AuthResponse` for login/register responses
- ✅ Added `OccupationRecord` for parking sessions
- ✅ Added denormalized fields (`userEmail`) for notifications

### 5. **Authentication Flow**
- ✅ **Login** (`/auth/login`):
  - Sends email + password to `/api/users/login`
  - Backend returns JWT token + user object
  - Token stored in HTTP-only cookie
  - Redirects to home page on success

- ✅ **Signup** (`/auth/signup`):
  - Sends email + password + numberplate to `/api/users/register`
  - Backend returns JWT token + user object
  - Token stored in HTTP-only cookie
  - Redirects to login page

- ✅ **Session Validation** (`hooks.server.ts`):
  - Checks JWT token in cookies
  - Validates with `/api/users/me` endpoint
  - Protects routes: redirects to /auth/login if unauthenticated
  - Clears cookies on invalid token

- ✅ **Logout** (`/auth/logout`):
  - Deletes JWT and session cookies
  - Redirects to login

### 6. **Server-Side Actions**
Updated all `+page.server.ts` files to:
- ✅ Use environment variables for backend URL
- ✅ Include JWT token in Authorization headers
- ✅ Include `credentials: 'include'` for session cookies
- ✅ Proper error handling with fallback redirect
- ✅ English comments (no Dutch)

**Files Updated:**
- `src/routes/auth/login/+page.server.ts` - JWT login
- `src/routes/auth/signup/+page.server.ts` - JWT signup with numberplate
- `src/routes/auth/logout/+page.server.ts` - Clear auth cookies
- `src/routes/admin/zones/create/+page.server.ts` - Create zone with JWT
- `src/routes/admin/zones/[id]/edit/+page.server.ts` - Update/delete zone with JWT
- `src/routes/zone/+page.server.ts` - Fetch zones with JWT

### 7. **CORS & Security Headers**
- ✅ CSP configured in `hooks.server.ts` to allow backend URLs
- ✅ `credentials: 'include'` for cookie-based session fallback
- ✅ HTTP-only cookies prevent XSS attacks
- ✅ SameSite=Lax for CSRF protection
- ✅ Secure flag enabled in production (NODE_ENV=production)

## 📝 Configuration

### Environment Variables (Docker Compose)
```dockerfile
# .env or docker-compose.yml
BACKEND_HOST=backend          # Docker service name
BACKEND_PORT=8080
NODE_ENV=development
```

### Development (Local)
```bash
# Automatic localhost resolution
http://localhost:8080
```

## 🔗 API Endpoints Usage

### Frontend Client (`apiGet`, `apiPost`, `apiPut`, `apiDelete`)
```typescript
import { apiGet, apiPost } from '$lib/api/client';

// GET with optional token
const { data, error } = await apiGet<ParkingZone>('/zones/123-abc', token);

// POST with body
const { data, error } = await apiPost<Reservation>('/reservations', {
  spaceId: '456-def',
  from: '2026-03-26T10:00:00Z',
  until: '2026-03-26T12:00:00Z'
}, token);

// PUT (update)
const { data, error } = await apiPut<ParkingZone>(`/zones/${zoneId}`, updatePayload, token);

// DELETE
const { error } = await apiDelete(`/zones/${zoneId}`, token);
```

### Server-Side Actions
```typescript
// Get JWT token from cookies
const jwtToken = cookies.get('jwt_token');

// Build headers
const headers: HeadersInit = {
  'Content-Type': 'application/json'
};

if (jwtToken) {
  headers['Authorization'] = `Bearer ${jwtToken}`;
}

// Make request
const response = await fetch(`${BACKEND_URL}/zones`, {
  method: 'POST',
  headers,
  credentials: 'include',
  body: JSON.stringify(payload)
});
```

## 🚀 Deployment

### Docker Compose
Frontend and backend communicate via internal Docker network:
- Backend service name: `backend`
- Backend internal URL: `http://backend:8080`
- Frontend port: `5173`
- Backend port: `8080`

### Production
1. Set environment variables:
   ```bash
   export BACKEND_HOST=your-api-domain.com
   export BACKEND_PORT=8080
   export NODE_ENV=production
   ```

2. Update CORS in backend:
   ```bash
   export CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com
   ```

## ✅ Testing Checklist

- [ ] Login with valid credentials
- [ ] Signup creates new user
- [ ] JWT token stored in cookies
- [ ] Protected routes redirect to login if not authenticated
- [ ] Logout clears cookies
- [ ] Zone creation sends JWT token
- [ ] Zone Update/delete sends JWT token
- [ ] API returns proper error messages
- [ ] CORS headers correct
- [ ] No console errors for CORS or auth

## 📚 Files Structure

```
src/
├── lib/
│   ├── api/
│   │   └── client.ts          # API HTTP client with JWT support
│   ├── server/
│   │   └── api.ts             # Server-side API utilities
│   ├── config.ts              # Environment & config
│   └── types.ts               # Backend type definitions
├── routes/
│   ├── auth/
│   │   ├── login/+page.server.ts
│   │   ├── signup/+page.server.ts
│   │   └── logout/+page.server.ts
│   ├── admin/zones/
│   │   ├── create/+page.server.ts
│   │   └── [id]/edit/+page.server.ts
│   └── zone/+page.server.ts
└── hooks.server.ts            # JWT validation & security headers
```

## 🔐 Security Notes

1. **Token Storage**: Stored in HTTP-only cookie (prevents JavaScript access)
2. **CORS**: Configured to accept credentials
3. **CSP**: Allows connections to backend API
4. **Session Fallback**: Both JWT and session cookies supported
5. **No Token Exposure**: Token never appears in localStorage or URL
6. **HTTPS Required**: Secure flag enabled in production

## 🐛 Troubleshooting

### "JWT token not provided"
- Check if login was successful
- Verify cookie is set: `cookies.get('jwt_token')`
- Check browser DevTools > Application > Cookies

### "Unauthorized 401"
- Token may be expired (1 week TTL)
- Logout and login again
- Check backend JWT_SECRET matches frontend expectations

### "CORS error"
- Verify CORS_ALLOWED_ORIGINS in backend
- Check CSP headers in (`hooks.server.ts`)
- Ensure credentials: 'include' in fetch requests

### "Backend unreachable"
- Check Docker network: `docker network ls`
- Verify backend service is running: `docker ps`
- Check backend port: `docker port {container}`

## 📖 Related Documentation

- [Backend API Documentation](../../parking/API_SPEC.md)
- [Deployment Guide](../../DEPLOYMENT_GUIDE.md)
- [Architecture Guidelines](../../parking/.junie/guidelines.md)

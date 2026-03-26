/**
 * SvelteKit hooks for server-side request handling
 * - Authentication check using JWT token
 * - Security headers (CSP, X-Frame-Options, etc.)
 * - CORS cookie forwarding
 */

import { redirect } from '@sveltejs/kit';
import type { Handle, HandleFetch } from '@sveltejs/kit';

// Backend URL for server-side requests (Docker internal network)
const BACKEND_URL = process.env.BACKEND_HOST
  ? `http://${process.env.BACKEND_HOST}:${process.env.BACKEND_PORT || 8080}`
  : 'http://backend:8080';

/**
 * Clear authentication cookies when session is invalid
 */
function clearAuthCookies(cookies: any): void {
  const cookieOptions = {
    path: '/',
    secure: process.env.NODE_ENV === 'production',
    sameSite: 'lax' as const
  };

  cookies.delete('jwt_token', cookieOptions);
  cookies.delete('JSESSIONID', cookieOptions);
}

/**
 * Main request handler: check authentication and set up security headers
 */
export const handle: Handle = async ({ event, resolve }) => {
  // Get JWT token or session ID
  const jwtToken = event.cookies.get('jwt_token');
  const sessionId = event.cookies.get('JSESSIONID');

  // Try to authenticate user
  if (jwtToken || sessionId) {
    try {
      const headers: HeadersInit = { 'Content-Type': 'application/json' };

      if (jwtToken) {
        headers['Authorization'] = `Bearer ${jwtToken}`;
      }

      const response = await event.fetch(`${BACKEND_URL}/api/users/me`, {
        method: 'GET',
        headers,
        credentials: 'include'
      });

      if (response.ok) {
        const user = await response.json();
        event.locals.user = user;
      } else {
        console.warn(`Authentication failed: ${response.status}`);
        event.locals.user = null;
        clearAuthCookies(event.cookies);
      }
    } catch (error) {
      console.error('Failed to verify authentication:', error);
      event.locals.user = null;
      clearAuthCookies(event.cookies);
    }
  } else {
    event.locals.user = null;
  }

  // Check route protection
  const unprotectedRoutes = ['/auth/login', '/auth/signup', '/'];
  const isProtected = !unprotectedRoutes.some((route) =>
    event.url.pathname.startsWith(route)
  );

  if (isProtected && !event.locals.user) {
    throw redirect(302, '/auth/login');
  }

  // Get response from resolve
  const response = await resolve(event);

  // Add security headers
  response.headers.set('X-Content-Type-Options', 'nosniff');
  response.headers.set('X-Frame-Options', 'SAMEORIGIN');

  // Content Security Policy
  const csp = [
    "default-src 'none'",
    "script-src 'self' 'unsafe-inline' 'unsafe-eval'",
    "font-src 'self' https://fonts.gstatic.com",
    `connect-src 'self' http://localhost:8080 ${BACKEND_URL}`,
    "frame-ancestors 'self'",
    "img-src 'self' data: https://*.googleusercontent.com http://*.googleusercontent.com",
    "form-action 'self'",
    "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com",
    "object-src 'none'",
    "media-src 'none'",
    "worker-src 'self' blob:",
    "base-uri 'self'"
  ].join('; ');

  response.headers.set('Content-Security-Policy', csp);

  return response;
};

/**
 * Handle fetch requests to backend: forward authentication headers and cookies
 */
export const handleFetch: HandleFetch = async ({ event, request, fetch }) => {
  // If request goes to backend, add authentication headers
  if (request.url.startsWith(BACKEND_URL) || request.url.startsWith('/api')) {
    const jwtToken = event.cookies.get('jwt_token');

    if (jwtToken) {
      request.headers.set('Authorization', `Bearer ${jwtToken}`);
    }

    // Forward cookies for session-based auth
    const cookieHeader = event.request.headers.get('cookie');
    if (cookieHeader) {
      request.headers.set('cookie', cookieHeader);
    }
  }

  return fetch(request);
};

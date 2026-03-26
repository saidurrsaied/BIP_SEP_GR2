/**
 * Centralized configuration for API endpoints
 * Supports both browser and server-side execution
 */

// Determine if we're in a browser environment
const isBrowser = typeof window !== 'undefined';

/**
 * Get the API base URL based on environment
 * In Docker: uses 'backend:8080' for server-side, 'localhost:8080' for client-side
 * In dev: uses 'localhost:8080'
 */
export function getApiBaseUrl(): string {
  // Server-side: use internal Docker network
  if (!isBrowser) {
    const backendHost = process.env.BACKEND_HOST || 'backend';
    const backendPort = process.env.BACKEND_PORT || '8080';
    return `http://${backendHost}:${backendPort}`;
  }

  // Client-side: use localhost or environment variable
  const clientBackendUrl = typeof window !== 'undefined'
    ? (window as any).__BACKEND_URL__ || localStorage.getItem('backendUrl') || 'http://localhost:8080'
    : 'http://localhost:8080';

  return clientBackendUrl;
}

export const API_BASE_URL = getApiBaseUrl();
export const API_ENDPOINT = `${API_BASE_URL}/api`;

// Token storage key
export const TOKEN_STORAGE_KEY = 'parking_jwt_token';

/**
 * Get stored JWT token from localStorage
 */
export function getStoredToken(): string | null {
  if (!isBrowser) return null;
  return localStorage.getItem(TOKEN_STORAGE_KEY);
}

/**
 * Store JWT token in localStorage
 */
export function storeToken(token: string): void {
  if (!isBrowser) return;
  localStorage.setItem(TOKEN_STORAGE_KEY, token);
}

/**
 * Clear stored JWT token
 */
export function clearToken(): void {
  if (!isBrowser) return;
  localStorage.removeItem(TOKEN_STORAGE_KEY);
}

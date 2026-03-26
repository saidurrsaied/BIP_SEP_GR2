/**
 * API client for communicating with the Parking Management backend
 * Handles JWT token management and API calls
 */

import { API_ENDPOINT, getStoredToken, storeToken } from '../config';

export interface ApiResponse<T> {
  data?: T;
  error?: string;
  status: number;
}

/**
 * Helper to extract and store token from API response
 */
function extractAndStoreToken(data: any): void {
  if (data?.token) {
    storeToken(data.token);
  }
}

/**
 * Build headers with authentication
 */
function buildHeaders(token?: string): HeadersInit {
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
  };

  // Use provided token or stored token
  const authToken = token || getStoredToken();
  if (authToken) {
    headers['Authorization'] = `Bearer ${authToken}`;
  }

  return headers;
}

/**
 * Handle API response and extract data
 */
async function handleResponse<T>(response: Response): Promise<ApiResponse<T>> {
  try {
    if (response.ok) {
      const contentType = response.headers.get('content-type');
      let data: T | undefined;

      if (contentType?.includes('application/json')) {
        data = await response.json();
        // Store token if present in response
        extractAndStoreToken(data);
      }

      return { data, status: response.status };
    } else {
      const contentType = response.headers.get('content-type');
      let error: string;

      if (contentType?.includes('application/json')) {
        const errorData = await response.json();
        error = errorData.message || errorData.error || `HTTP ${response.status}`;
      } else {
        error = await response.text();
      }

      return { error, status: response.status };
    }
  } catch (e) {
    const errorMessage = e instanceof Error ? e.message : 'Unknown error';
    return { error: errorMessage, status: 500 };
  }
}

/**
 * GET request to API
 */
export async function apiGet<T>(
  endpoint: string,
  token?: string,
  options?: RequestInit
): Promise<ApiResponse<T>> {
  try {
    const response = await fetch(`${API_ENDPOINT}${endpoint}`, {
      method: 'GET',
      headers: buildHeaders(token),
      credentials: 'include', // Include cookies for session-based auth
      ...options,
    });

    return handleResponse<T>(response);
  } catch (e) {
    const errorMessage = e instanceof Error ? e.message : 'Network request failed';
    return { error: errorMessage, status: 0 };
  }
}

/**
 * POST request to API
 */
export async function apiPost<T>(
  endpoint: string,
  body?: unknown,
  token?: string,
  options?: RequestInit
): Promise<ApiResponse<T>> {
  try {
    const response = await fetch(`${API_ENDPOINT}${endpoint}`, {
      method: 'POST',
      headers: buildHeaders(token),
      credentials: 'include',
      body: body ? JSON.stringify(body) : undefined,
      ...options,
    });

    return handleResponse<T>(response);
  } catch (e) {
    const errorMessage = e instanceof Error ? e.message : 'Network request failed';
    return { error: errorMessage, status: 0 };
  }
}

/**
 * PUT request to API
 */
export async function apiPut<T>(
  endpoint: string,
  body?: unknown,
  token?: string,
  options?: RequestInit
): Promise<ApiResponse<T>> {
  try {
    const response = await fetch(`${API_ENDPOINT}${endpoint}`, {
      method: 'PUT',
      headers: buildHeaders(token),
      credentials: 'include',
      body: body ? JSON.stringify(body) : undefined,
      ...options,
    });

    return handleResponse<T>(response);
  } catch (e) {
    const errorMessage = e instanceof Error ? e.message : 'Network request failed';
    return { error: errorMessage, status: 0 };
  }
}

/**
 * DELETE request to API
 */
export async function apiDelete(
  endpoint: string,
  token?: string,
  options?: RequestInit
): Promise<ApiResponse<void>> {
  try {
    const response = await fetch(`${API_ENDPOINT}${endpoint}`, {
      method: 'DELETE',
      headers: buildHeaders(token),
      credentials: 'include',
      ...options,
    });

    return handleResponse<void>(response);
  } catch (e) {
    const errorMessage = e instanceof Error ? e.message : 'Network request failed';
    return { error: errorMessage, status: 0 };
  }
}

// API client configuration
const API_BASE_URL = 'http://localhost:8080/api';

export interface ApiResponse<T> {
  data?: T;
  error?: string;
  status: number;
}

async function handleResponse<T>(response: Response): Promise<ApiResponse<T>> {
  if (response.ok) {
    const data = await response.json();
    return { data, status: response.status };
  } else {
    const error = await response.text();
    return { error, status: response.status };
  }
}

export async function apiGet<T>(endpoint: string, token?: string): Promise<ApiResponse<T>> {
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    method: 'GET',
    headers,
  });

  return handleResponse<T>(response);
}

export async function apiPost<T>(
  endpoint: string,
  body?: unknown,
  token?: string
): Promise<ApiResponse<T>> {
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    method: 'POST',
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });

  return handleResponse<T>(response);
}

export async function apiDelete(endpoint: string, token?: string): Promise<ApiResponse<void>> {
  const headers: HeadersInit = {};

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    method: 'DELETE',
    headers,
  });

  return handleResponse<void>(response);
}

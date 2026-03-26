/**
 * Server-side utilities for backend communication
 * Used by all +page.server.ts files for consistent API calls
 */

export function getBackendUrl(): string {
  const host = process.env.BACKEND_HOST || 'backend';
  const port = process.env.BACKEND_PORT || '8080';
  return `http://${host}:${port}`;
}

export function getApiUrl(): string {
  return `${getBackendUrl()}/api`;
}

/**
 * Build headers for API requests with authentication
 */
export function buildApiHeaders(token?: string, additionalHeaders?: Record<string, string>): HeadersInit {
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...additionalHeaders
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  return headers;
}

/**
 * Make authenticated API request
 */
export async function makeApiRequest<T>(
  url: string,
  method: string = 'GET',
  token?: string,
  body?: unknown
): Promise<{ ok: boolean; data?: T; error?: string; status: number }> {
  try {
    const response = await fetch(url, {
      method,
      headers: buildApiHeaders(token),
      credentials: 'include',
      body: body ? JSON.stringify(body) : undefined
    });

    if (response.ok) {
      const contentType = response.headers.get('content-type');
      const data = contentType?.includes('application/json') ? await response.json() : null;
      return { ok: true, data, status: response.status };
    } else {
      const error = await response.text();
      return { ok: false, error, status: response.status };
    }
  } catch (err) {
    const errorMsg = err instanceof Error ? err.message : 'Unknown error';
    return { ok: false, error: errorMsg, status: 0 };
  }
}

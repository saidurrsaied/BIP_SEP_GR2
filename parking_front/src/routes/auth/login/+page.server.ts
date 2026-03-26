import { fail, redirect } from '@sveltejs/kit';
import type { Actions } from './$types';

// Backend URL for server-side requests (Docker internal network)
const BACKEND_URL = process.env.BACKEND_HOST ? `http://${process.env.BACKEND_HOST}:${process.env.BACKEND_PORT || 8080}` : 'http://backend:8080';

export const actions: Actions = {
  default: async ({ request, cookies}) => {
    const data = await request.formData();
    const email = data.get('email')?.toString();
    const password = data.get('password')?.toString();

    if (!email || !password) {
      return fail(400, { error: 'Please provide both email and password.' });
    }

    try {
      const response = await fetch(`${BACKEND_URL}/api/users/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
        credentials: 'include'
      });

      if (!response.ok) {
        const errorText = await response.text();
        console.error('Login failed:', errorText);
        return fail(response.status, { error: 'Login failed. Please check your credentials.' });
      }

      const result = await response.json();

      // Store JWT token in HTTP-only cookie for security
      if (result.token) {
        cookies.set('jwt_token', result.token, {
          path: '/',
          httpOnly: true, // Prevent JavaScript access (security)
          sameSite: 'lax',
          secure: process.env.NODE_ENV === 'production', // Only over HTTPS in production
          maxAge: 60 * 60 * 24 * 7 // 1 week
        });
      }

      // Also set session cookie if backend sends one
      const setCookieHeader = response.headers.get('set-cookie');
      if (setCookieHeader) {
        const match = setCookieHeader.match(/JSESSIONID=([^;]+)/);
        if (match) {
          cookies.set('JSESSIONID', match[1], {
            path: '/',
            httpOnly: true,
            sameSite: 'lax',
            secure: process.env.NODE_ENV === 'production',
            maxAge: 60 * 60 * 24 * 7
          });
        }
      }

      return { success: true, user: result.user };
    } catch (error) {
      console.error('Login fetch error:', error);
      return fail(500, { error: 'Cannot connect to the server. Please try again.' });
    }
  }
} satisfies Actions;

import { fail, redirect } from '@sveltejs/kit';
import type { Actions } from './$types';

// Backend URL for server-side requests (Docker internal network)
const BACKEND_URL = process.env.BACKEND_HOST ? `http://${process.env.BACKEND_HOST}:${process.env.BACKEND_PORT || 8080}` : 'http://backend:8080';

export const actions: Actions = {
  default: async ({ request, cookies }) => {
    const data = await request.formData();
    const email = data.get('email')?.toString();
    const password = data.get('password')?.toString();
    const numberplate = data.get('numberplate')?.toString();

    // Validate all required fields
    if (!email || !password || !numberplate) {
      return fail(400, { error: 'Please fill in all fields.' });
    }

    if (password.length < 8) {
      return fail(400, { error: 'Password must be at least 8 characters.' });
    }

    try {
      const response = await fetch(`${BACKEND_URL}/api/users/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password, numberplate, role: 'CITIZEN' }),
        credentials: 'include'
      });

      if (!response.ok) {
        const errorText = await response.text();
        console.error('Signup failed:', errorText);
        return fail(response.status, {
          error: errorText || 'Signup registration failed.'
        });
      }

      const result = await response.json();

      // Store JWT token if provided
      if (result.token) {
        cookies.set('jwt_token', result.token, {
          path: '/',
          httpOnly: true,
          sameSite: 'lax',
          secure: process.env.NODE_ENV === 'production',
          maxAge: 60 * 60 * 24 * 7
        });
      }

      // Redirect to login page after successful signup
      throw redirect(302, '/auth/login');
    } catch (error) {
      if (error instanceof Error && error.message.includes('redirect')) {
        throw error;
      }
      console.error('Signup fetch error:', error);
      return fail(500, { error: 'Cannot connect to the server. Please try again.' });
    }
  }
} satisfies Actions;
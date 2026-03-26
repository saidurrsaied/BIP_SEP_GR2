import { fail, redirect } from '@sveltejs/kit';
import type { Actions } from './$types';

const BACKEND_URL = "http://backend:8080";

export const actions = {
	default: async ({ request, fetch }) => {
		const data = await request.formData();
		const email = data.get('email')?.toString();
		const password = data.get('password')?.toString();

		if (!email || !password || password.length < 8) {
			return fail(400, { error: 'Fill in a valid email address and a password of at least 8 characters.' });
		}

		try {
			const res = await fetch(`${BACKEND_URL}/api/users/register`, {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({ email, password })
			});

			if (!res.ok) {
				const errorMessage = await res.text();
				return fail(res.status, {
					error: errorMessage || 'Signup failed. This email address might already be in use.'
				});
			}

		} catch (error) {
			console.error('Signup fetch error:', error);
			return fail(500, { error: 'Cannot connect to the server.' });
		}

		throw redirect(303, '/auth/login');
	}
} satisfies Actions;

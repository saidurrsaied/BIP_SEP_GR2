import { fail, redirect } from '@sveltejs/kit';
import type { Actions } from './$types';

const BACKEND_URL = "http://backend:8080";

export const actions = {
    default: async ({ request, fetch }) => {
        const data = await request.formData();
        const email = data.get('email')?.toString();
        const password = data.get('password')?.toString();
        const numberplate = data.get('numberplate')?.toString(); // 1. Get the value

        // 2. Add validation for the numberplate
        if (!email || !password || !numberplate || password.length < 8) {
            return fail(400, { error: 'Please fill in all fields. Password must be at least 8 characters.' });
        }

        try {
            const res = await fetch(`${BACKEND_URL}/api/users/register`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                // 3. Include numberplate in the body
                body: JSON.stringify({ email, password, numberplate }) 
            });

            if (!res.ok) {
                const errorMessage = await res.text();
                // Tip: If your backend returns a JSON error, you might need JSON.parse(errorMessage).message
                return fail(res.status, {
                    error: errorMessage || 'Signup failed.'
                });
            }

        } catch (error) {
            console.error('Signup fetch error:', error);
            return fail(500, { error: 'Cannot connect to the server.' });
        }

        throw redirect(303, '/auth/login');
    }
} satisfies Actions;
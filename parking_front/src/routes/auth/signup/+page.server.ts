import { fail, redirect } from '@sveltejs/kit';
import type { Actions } from './$types';

// Let op de poort: 8080 (of 3000 als je dat in Spring Boot geforceerd had)
const BACKEND_URL = "http://backend:8080";

export const actions = {
	default: async ({ request, fetch }) => {
		const data = await request.formData();
		const email = data.get('email')?.toString();
		const password = data.get('password')?.toString();

		if (!email || !password || password.length < 8) {
			return fail(400, { error: 'Vul een geldig e-mailadres in en een wachtwoord van min. 8 tekens.' });
		}

		try {
			// Stuur de data naar je Spring Boot backend
			const res = await fetch(`${BACKEND_URL}/api/users/register`, {
				method: 'POST',
				headers: { 'Content-Type': 'application/json' },
				body: JSON.stringify({ email, password })
			});

			if (!res.ok) {
				// Als Spring Boot een error gooit (bijv. 409 Conflict of 400 Bad Request)
				// Probeer de error message van je backend op te vangen, anders een standaard melding.
				const errorMessage = await res.text();
				return fail(res.status, {
					error: errorMessage || 'Registreren mislukt. Mogelijk bestaat dit e-mailadres al.'
				});
			}

		} catch (error) {
			console.error('Signup fetch error:', error);
			return fail(500, { error: 'Kan geen verbinding maken met de server.' });
		}

		// Als Spring Boot een 200/201 OK terugstuurt, sturen we de bezoeker naar de login!
		throw redirect(303, '/auth/login');
	}
} satisfies Actions;

import { fail, redirect } from '@sveltejs/kit';

const BACKEND_URL = "http://backend:8080"; // Je Docker URL

export const actions = {
	default: async ({ request, cookies, fetch }) => {
		const data = await request.formData();
		const email = data.get('email')?.toString();
		const password = data.get('password')?.toString();

		if (!email || !password) {
			return fail(400, { error: 'Vul e-mail en wachtwoord in.' });
		}

		// Stuur login naar Spring Boot
		const res = await fetch(`${BACKEND_URL}/api/users/login`, {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ email, password })
		});

		if (!res.ok) {
			return fail(401, { error: 'Inloggen mislukt. Check je gegevens.' });
		}

		// VANG DE JSESSIONID UIT DE SPRING BOOT RESPONSE
		const setCookieHeader = res.headers.get('set-cookie');

		if (setCookieHeader) {
			// Zoek de JSESSIONID in de header string
			const match = setCookieHeader.match(/JSESSIONID=([^;]+)/);
			if (match) {
				// Sla hem op in de SvelteKit cookies
				cookies.set('JSESSIONID', match[1], {
					path: '/',
					httpOnly: true,
					sameSite: 'lax',
					secure: false, // Zet op true in productie
					maxAge: 60 * 60 * 24 * 7 // 1 week
				});
			}
		}

		throw redirect(303, '/');
	}
};

import { fail, redirect } from '@sveltejs/kit';

const BACKEND_URL = "http://backend:8080"; // Je Docker URL

export const actions = {
	default: async ({ request, cookies, fetch }) => {
		const data = await request.formData();
		const email = data.get('email')?.toString();
		const password = data.get('password')?.toString();

		if (!email || !password) {
			return fail(400, { error: 'Fill in a email and password.' });
		}

		const res = await fetch(`${BACKEND_URL}/api/users/login`, {
			method: 'POST',
			headers: { 'Content-Type': 'application/json' },
			body: JSON.stringify({ email, password })
		});


		if (!res.ok) {
			return fail(401, { error: 'Login failed. Check your credentials.' });
		}

		const setCookieHeader = res.headers.get('set-cookie');
		console.log("=== LOGIN DEBUG ===");
		console.log("Do we get a cookie from Spring Boot?:", setCookieHeader);

		if (setCookieHeader) {
			const match = setCookieHeader.match(/JSESSIONID=([^;]+)/);
			if (match) {
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

// src/hooks.server.ts
import { redirect } from '@sveltejs/kit';
import type { Handle, HandleFetch } from '@sveltejs/kit';

// Gebruik de Docker interne netwerk URL voor server-side fetches
const BACKEND_URL = "http://backend:8080";

// Helper functie om code schoon te houden
function clearSessionCookies(cookies: any) {
	const cookieOptions = {
		path: '/',
		secure: false, // Zet op true zodra je HTTPS/productie gebruikt
		sameSite: 'lax' as const
	};

	cookies.delete('JSESSIONID', cookieOptions);
	cookies.delete('XSRF-TOKEN', cookieOptions);
}

export const handle: Handle = async ({ event, resolve }) => {
	const sessionId = event.cookies.get('JSESSIONID');
	console.log("SessionID", sessionId)

	// --- AUTHENTICATIE CHECK ---
	if (!sessionId) {
		event.locals.user = null;
	} else {
		try {
			// HIER ZAT DE FOUT: Het moest /api/users/me zijn (met een 's')!
			const res = await event.fetch(`${BACKEND_URL}/api/users/me`);
			console.log(res);
			if (res.ok) {
				event.locals.user = await res.json();
			} else {
				// Nu vertelt hij je in de terminal PRECIES waarom hij faalt
				const errorBody = await res.text();
				console.warn(`Hooks: Ongeldige sessie (Status: ${res.status}). Reden: ${errorBody}`);
				event.locals.user = null;
				clearSessionCookies(event.cookies);
			}
		} catch (error) {
			console.error('Hooks: kon backend niet bereiken:', error);
			event.locals.user = null;
			clearSessionCookies(event.cookies);
		}
	}

	// --- ROUTE BEVEILIGING ---
	const nonProtectedRoutes = ['/auth/login', '/auth/signup'];
	const isProtectedRoute = !nonProtectedRoutes.some((route) => event.url.pathname.startsWith(route));

	if (isProtectedRoute && !event.locals.user) {
		console.error(`Toegang geweigerd tot ${event.url.pathname}. Redirect naar /auth/login`);
		throw redirect(302, '/auth/login');
	}

	// --- BEVEILIGING HEADERS & CSP ---
	const response = await resolve(event);

	response.headers.set('X-Content-Type-Options', 'nosniff');
	response.headers.set('X-Frame-Options', 'SAMEORIGIN');

	const csp = [
		"default-src 'none'",

		// Tailwind CDN verwijderd, je gebruikt app.css via Vite nu!
		"script-src 'self' 'unsafe-inline' 'unsafe-eval'",

		// Sta Google Fonts bestanden toe
		"font-src 'self' https://fonts.gstatic.com",

		// Sta de frontend toe om API calls te doen
		`connect-src 'self' http://localhost:8080 ${BACKEND_URL}`,

		"frame-ancestors 'self'",

		// Sta plaatjes van Google (voor profielfoto's) toe
		"img-src 'self' data: https://*.googleusercontent.com http://*.googleusercontent.com",

		"form-action 'self'",

		// Sta Google Fonts CSS toe
		"style-src 'self' 'unsafe-inline' https://fonts.googleapis.com",

		"object-src 'none'",
		"media-src 'none'",
		"worker-src 'self' blob:",
		"base-uri 'self'"
	].join('; ');

	response.headers.set('Content-Security-Policy', csp);

	return response;
};

// --- FETCH INTERCEPTOR ---
export const handleFetch: HandleFetch = async ({ event, request, fetch }) => {
	// Zorg dat elke fetch naar de Spring Boot backend automatisch de cookies (JSESSIONID) meekrijgt
	if (request.url.startsWith(BACKEND_URL)) {
		const cookieHeader = event.request.headers.get('cookie');
		if (cookieHeader) {
			request.headers.set('cookie', cookieHeader);
		}
	}

	return fetch(request);
};

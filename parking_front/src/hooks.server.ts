// src/hooks.server.ts
import { redirect } from '@sveltejs/kit';
import type { Handle, HandleFetch } from '@sveltejs/kit';

// 1. Gebruik de Docker interne netwerk URL voor server-side fetches!
const BACKEND_URL = "http://backend:8080";

// Helper functie om code schoon te houden
function clearSessionCookies(cookies: any) {
	const cookieOptions = {
		path: '/',
		secure: false, // Zet op true zodra je HTTPS/productie gebruikt
		sameSite: 'lax' as const
		// We laten 'domain' bewust weg, zodat het feilloos werkt op zowel localhost als .localtest.me
	};

	cookies.delete('JSESSIONID', cookieOptions);
	cookies.delete('XSRF-TOKEN', cookieOptions);
}

export const handle: Handle = async ({ event, resolve }) => {
	const sessionId = event.cookies.get('JSESSIONID');

	// --- AUTHENTICATIE CHECK ---
	if (!sessionId) {
		event.locals.user = null;
	} else {
		try {
			// Let op: check of je Spring Boot endpoint inderdaad /api/user is (of /api/users/me)
			const res = await event.fetch(`${BACKEND_URL}/api/user`);

			if (res.ok) {
				event.locals.user = await res.json();
			} else {
				console.warn('Hooks: Ongeldige sessie, gebruiker niet gevonden op backend.');
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
		throw redirect(302, '/auth/login');
	}

	// --- BEVEILIGING HEADERS & CSP ---
	const response = await resolve(event);

	response.headers.set('X-Content-Type-Options', 'nosniff');
	response.headers.set('X-Frame-Options', 'SAMEORIGIN');

// src/hooks.server.ts
const csp = [
    "default-src * 'unsafe-inline' 'unsafe-eval' data: blob:",
    "script-src * 'unsafe-inline' 'unsafe-eval'",
    "connect-src * 'unsafe-inline'",
    "img-src * data: blob: 'unsafe-inline'",
    "frame-src *",
    "style-src * 'unsafe-inline'"
].join('; ');

response.headers.set('Content-Security-Policy', csp);

	response.headers.set('Content-Security-Policy', csp);

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

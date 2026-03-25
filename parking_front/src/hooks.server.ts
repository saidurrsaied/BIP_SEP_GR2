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

	const csp = [
        "default-src 'none'",
        
        // Sta de Tailwind CDN toe
        `script-src 'self' 'unsafe-inline' https://cdn.tailwindcss.com`,
        
        // Sta Google Fonts bestanden toe
        "font-src 'self' https://fonts.gstatic.com",
        
        // Sta de frontend toe om API calls te doen naar localhost:8080 (vanuit je browser) en backend:8080 (vanuit Docker)
        `connect-src 'self' http://localhost:8080 ${BACKEND_URL}`,
        
        "frame-ancestors 'self'",
        
        // Sta plaatjes van Google (voor profielfoto's) toe
        "img-src 'self' data: https://*.googleusercontent.com http://*.googleusercontent.com",
        
        "form-action 'self'",
        
        // Sta Google Fonts CSS toe
        "style-src 'self' 'unsafe-inline' https://fonts.googleapis.com",
        
        "object-src 'none'",
        "media-src 'none'",
        "worker-src 'none'",
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

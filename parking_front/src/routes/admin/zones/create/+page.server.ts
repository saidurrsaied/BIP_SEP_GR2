// src/routes/admin/zones/create/+page.server.ts
import { fail } from '@sveltejs/kit';
import type { Actions } from './$types';

// Let op: We gebruiken hier 'backend' omdat de SvelteKit server met de Java server praat via Docker!
const BACKEND_URL = "http://backend:8080/api";

export const actions = {
	default: async ({ request, fetch }) => {
		const data = await request.formData();

		// Haal de waardes uit het formulier
		const name = data.get('name')?.toString();
		const address = data.get('address')?.toString();
		const city = data.get('city')?.toString();
		const latitude = parseFloat(data.get('latitude')?.toString() || '0');
		const longitude = parseFloat(data.get('longitude')?.toString() || '0');
		const hourlyRate = parseFloat(data.get('hourlyRate')?.toString() || '0');
		const chargingRate = parseFloat(data.get('chargingRate')?.toString() || '0');

		if (!name || !address || !city) {
			return fail(400, { error: 'Vul alle verplichte velden in.' });
		}

		// Bouw de payload (prijzen omzetten naar centen)
		const payload = {
			name,
			address,
			city,
			latitude,
			longitude,
			pricingPolicy: {
				hourlyRateCents: Math.round(hourlyRate * 100),
				chargingRatePerKwhCents: Math.round(chargingRate * 100),
				currency: 'EUR'
			}
		};

		try {
			const res = await fetch(`${BACKEND_URL}/zones`, {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json'
					// Voeg hier eventueel cookies of authorization headers toe
				},
				body: JSON.stringify(payload)
			});

			if (!res.ok) {
				const errorText = await res.text();
				return fail(res.status, { error: `Backend error: ${errorText}` });
			}

			// Alles is goed gegaan! We sturen een succes-bericht terug naar de frontend.
			return { success: true, message: 'Zone is succesvol aangemaakt!' };

		} catch (error) {
			console.error('Zone aanmaken mislukt:', error);
			return fail(500, { error: 'Kan de backend niet bereiken.' });
		}
	}
} satisfies Actions;

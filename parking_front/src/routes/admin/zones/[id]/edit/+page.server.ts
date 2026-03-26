// src/routes/admin/zones/[id]/edit/+page.server.ts
import { fail, redirect } from '@sveltejs/kit';
import type { PageServerLoad, Actions } from './$types';

const BACKEND_URL = "http://backend:8080/api";

export const load: PageServerLoad = async ({ params, fetch }) => {
    const { id } = params;

    // Haal de data van deze specifieke zone op om het formulier te vullen
    const res = await fetch(`${BACKEND_URL}/zones/${id}`);
		console.log("HIEEEEEEEER", res)

    if (!res.ok) {
        throw redirect(303, '/admin/zones'); // Terug naar overzicht als hij niet bestaat
    }

    const zone = await res.json();
    return { zone };
};

export const actions = {
    // Action 1: Updaten
    update: async ({ request, params, fetch }) => {
        const data = await request.formData();
        const { id } = params;

        const name = data.get('name')?.toString();
        const address = data.get('address')?.toString();
        const city = data.get('city')?.toString();
        const latitude = parseFloat(data.get('latitude')?.toString() || '0');
        const longitude = parseFloat(data.get('longitude')?.toString() || '0');
        const hourlyRate = parseFloat(data.get('hourlyRate')?.toString() || '0');
        const chargingRate = parseFloat(data.get('chargingRate')?.toString() || '0');

        if (!name || !address || !city) {
            return fail(400, { error: 'Please fill in all required fields.' });
        }

        const payload = {
            name, address, city, latitude, longitude,
            pricingPolicy: {
                hourlyRateCents: Math.round(hourlyRate * 100),
                chargingRatePerKwhCents: Math.round(chargingRate * 100),
                currency: 'EUR'
            }
        };

        try {
            // Let op: we doen een PUT request naar Spring Boot
            const res = await fetch(`${BACKEND_URL}/zones/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!res.ok) {
                const errorText = await res.text();
                return fail(res.status, { error: `Backend error: ${errorText}` });
            }

            return { success: true, message: 'Zone successfully updated!' };
        } catch (error) {
            return fail(500, { error: 'Could not reach the server.' });
        }
    },

    // Action 2: Verwijderen
    delete: async ({ params, fetch }) => {
        const { id } = params;

        try {
            // Let op: we doen een DELETE request naar Spring Boot
            const res = await fetch(`${BACKEND_URL}/zones/${id}`, {
                method: 'DELETE'
            });

            if (!res.ok) {
                return fail(res.status, { error: 'Failed to delete zone. It might still contain spaces.' });
            }
        } catch (error) {
            return fail(500, { error: 'Could not reach the server.' });
        }

        // Als verwijderen gelukt is, direct terugsturen naar het overzicht!
        throw redirect(303, '/admin/zones');
    },


    addSpace: async ({ request, params, fetch }) => {
        const data = await request.formData();
        const { id } = params;

        const spaceNumber = data.get('spaceNumber')?.toString();
        const level = data.get('level')?.toString();
        const chargingPoint = data.get('chargingPoint')?.toString();

        if (!spaceNumber || !level) {
            return fail(400, { error: 'Please provide a space number and level.' });
        }

        try {
            // Let op de URL: we gebruiken het endpoint uit je ZoneController
            const res = await fetch(`${BACKEND_URL}/zones/${id}/spaces`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                // Jouw record: AddSpaceRequest(ChargingPoint chargingPoint, String level, String spaceNumber)
                body: JSON.stringify({ 
                    spaceNumber, 
                    level, 
                    chargingPoint 
                })
            });

            console.error("WHUUUUT", res)

            if (!res.ok) {
                const errorText = await res.text();
                return fail(res.status, { error: `Failed to add space: ${errorText}` });
            }

            return { success: true, message: `Space ${spaceNumber} added successfully!` };
        } catch (error) {
            return fail(500, { error: 'Could not reach the server.' });
        }
    },
} satisfies Actions;
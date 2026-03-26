import { fail, type Actions, type RequestEvent } from '@sveltejs/kit';

// URL van de Java Backend (Docker service naam)
const BACKEND_URL = "http://backend:8080/api";

export const actions = {
    default: async ({ request, fetch, params }: RequestEvent) => {
        const data = await request.formData();
        const zoneId = params.zoneId;

        // Data ophalen uit het formulier
        const spaceNumber = data.get('spaceNumber')?.toString();
        const level = data.get('level')?.toString();
        const status = data.get('status')?.toString();
        const chargingPoint = data.get('chargingPoint')?.toString();

        // Validatie
        if (!spaceNumber || !status || !chargingPoint) {
            return fail(400, { error: 'Vul alle verplichte velden in.' });
        }

        const payload = {
            spaceNumber,
            level: level || "0",
            status,
            chargingPoint
        };

        try {
            const res = await fetch(`${BACKEND_URL}/zones/${zoneId}/spaces`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!res.ok) {
                const errorText = await res.text();
                return fail(res.status, { error: `Backend fout: ${errorText}` });
            }

            return { success: true, message: 'Parkeerplek succesvol toegevoegd!' };
        } catch (error) {
            console.error('Fout bij aanmaken space:', error);
            return fail(500, { error: 'Kan de backend server niet bereiken.' });
        }
    }
} satisfies Actions;
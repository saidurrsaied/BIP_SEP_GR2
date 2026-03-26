// src/routes/zones/[id]/reserve/+page.server.ts
import { fail, redirect } from '@sveltejs/kit';
import type { PageServerLoad, Actions } from './$types';

const BACKEND_URL = "http://backend:8080/api";

export const load: PageServerLoad = async ({ fetch, params }) => {
    const res = await fetch(`${BACKEND_URL}/zones/${params.id}`);

    if (!res.ok) {
        throw redirect(307, '/');
    }

    const zone = await res.json();
    // Alleen plekken tonen die echt vrij zijn
    const freeSpaces = (zone.spaces || []).filter((s: any) => s.status === 'FREE');

    return { zone, freeSpaces };
};

export const actions: Actions = {
    confirm: async ({ request, fetch }) => {
        const data = await request.formData();
        const spaceId = data.get('spaceId');

        if (!spaceId) {
            return fail(400, { error: 'Selecteer een parkeerplaats.' });
        }

        // Tijden voorbereiden (nu tot over 2 uur) voor de Java Instant
        const from = new Date().toISOString(); 
        const until = new Date(Date.now() + 2 * 60 * 60 * 1000).toISOString();

        try {
            // We roepen jouw ReservationController aan: POST /api/reservations
            const res = await fetch(`${BACKEND_URL}/reservations`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    userId: 1, // Tijdelijk hardcoded tot je Auth hebt
                    spaceId: spaceId,
                    from: from,
                    until: until
                })
            });

            if (!res.ok) {
                const errorData = await res.text();
                console.error("Backend Error:", errorData);
                return fail(res.status, { error: 'Reservering mislukt. Is de plek al bezet?' });
            }

            // Als het lukt (204 No Content), stuurt de controller niets terug
            return { success: true };
        } catch (e) {
            return fail(500, { error: 'Kan de reserveringsserver niet bereiken.' });
        }
    }
};
import { fail, redirect } from '@sveltejs/kit';
import type { PageServerLoad, Actions } from './$types';

const BACKEND_URL = "http://backend:8080/api";

export const load: PageServerLoad = async ({ fetch, params, locals }) => {
    // Check ook in de load of de user wel mag reserveren
    if (!locals.user) {
        throw redirect(302, '/login?callback=/zones/' + params.id + '/reserve');
    }

    const res = await fetch(`${BACKEND_URL}/zones/${params.id}`);

    if (!res.ok) {
        throw redirect(307, '/');
    }

    const zone = await res.json();
    const freeSpaces = (zone.spaces || []).filter((s: any) => s.status === 'FREE');

    return { zone, freeSpaces };
};

export const actions: Actions = {
    confirm: async ({ request, fetch, locals }) => {
        // De user ophalen uit locals
        const user = locals.user;

        if (!user) {
            return fail(401, { error: 'Je moet ingelogd zijn om te reserveren.' });
        }

        const data = await request.formData();
        const spaceId = data.get('spaceId');

        if (!spaceId) {
            return fail(400, { error: 'Selecteer een parkeerplaats.' });
        }

        const from = new Date().toISOString(); 
        const until = new Date(Date.now() + 2 * 60 * 60 * 1000).toISOString();

const payload = {
            userId: user.id,
            spaceId: spaceId,
            from: from,
            until: until
        };

        // --- LOG DE REQUEST ---
        console.log("🚀 [POST] /reservations - Request:", JSON.stringify(payload, null, 2));

        try {
            const res = await fetch(`${BACKEND_URL}/reservations`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            // --- LOG DE RESPONSE ---
            // Belangrijk: gebruik .clone() zodat je de body kan loggen én daarna nog kan gebruiken
            const clonedRes = res.clone();
            const responseText = await clonedRes.text();
            
            console.log(`✅ [POST] /reservations - Status: ${res.status} ${res.statusText}`);
            try {
                // Probeer het als JSON te loggen voor leesbaarheid, anders als tekst
                console.log("� Response Body:", JSON.parse(responseText));
            } catch {
                console.log("📄 Response Body (text):", responseText);
            }

            if (!res.ok) {
                // ... je error handling ...
            }

            return { success: true };
        } catch (e) {
            console.error("❌ Fetch Error:", e);
            return fail(500, { error: 'Kan de server niet bereiken.' });
        }
    }
};
import { error } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async ({ fetch, params }) => {
    const BACKEND_URL = "http://backend:8080/api";

    try {
        // We halen de specifieke zone op via de ID in de URL
        const res = await fetch(`${BACKEND_URL}/zones/${params.id}`);

        if (!res.ok) {
            // Als de zone niet bestaat in de DB (404)
            throw error(res.status, 'Parking zone not found');
        }

        const zone = await res.json();

        // Dit object wordt 'data' in je +page.svelte
        return {
            zone
        };
    } catch (err: any) {
        console.error("Fout bij ophalen zone details:", err);
        throw error(500, 'Could not load zone details');
    }
};
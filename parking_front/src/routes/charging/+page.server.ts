import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async ({ fetch }) => {
    // Zorg dat deze URL exact matcht met je backend container naam en poort
    const BACKEND_URL = "http://backend:8080/api";

    try {
        const res = await fetch(`${BACKEND_URL}/zones`);

        if (!res.ok) {
            console.error("Backend response not OK:", res.status);
            return { zones: [] };
        }

        const zones = await res.json();
        return { zones };
    } catch (error) {
        console.error("Could not connect to backend:", error);
        return { zones: [] };
    }
};
import { redirect } from '@sveltejs/kit';

export const actions = {
    default: async ({ cookies }) => {
        // Gooi de sessie cookie weg
        cookies.delete('session', { path: '/' });
        
        // Stuur terug naar login
        throw redirect(303, '/auth/login');
    }
};
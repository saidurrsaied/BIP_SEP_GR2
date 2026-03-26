import { redirect } from '@sveltejs/kit';

export const actions = {
    default: async ({ cookies }) => {
        cookies.delete('session', { path: '/' });
        
        throw redirect(303, '/auth/login');
    }
};
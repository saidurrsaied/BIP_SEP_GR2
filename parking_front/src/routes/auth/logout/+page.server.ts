import { redirect } from '@sveltejs/kit';
import type { Actions } from './$types';

export const actions: Actions = {
  default: async ({ cookies }) => {
    // Clear authentication cookies
    cookies.delete('jwt_token', { path: '/' });
    cookies.delete('JSESSIONID', { path: '/' });

    // Redirect to login
    throw redirect(303, '/auth/login');
  }
};
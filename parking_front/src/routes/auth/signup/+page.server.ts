import { fail, redirect } from '@sveltejs/kit';
import type { Actions } from '@sveltejs/kit';

export const actions = {
    default: async ({ request }: Parameters<Actions['default']>[0]) => {
        const data = await request.formData();
        const email = data.get('email')?.toString();
        const password = data.get('password')?.toString();

        if (!email || !password || password.length < 8) {
            return fail(400, { error: 'Vul een geldig e-mailadres en wachtwoord in (min. 8 tekens).' });
        }

        // TODO: 1. Check in je database of deze email al bestaat.
        // TODO: 2. Hash het wachtwoord (bijv. met bcrypt).
        // TODO: 3. Sla de nieuwe user op in je database.

        // Als het is gelukt, stuur de gebruiker naar de login pagina:
        throw redirect(303, '/auth/login');
    }
};
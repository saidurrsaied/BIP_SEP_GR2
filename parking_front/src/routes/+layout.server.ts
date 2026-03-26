import type { LayoutServerLoad } from './$types';



export const load: LayoutServerLoad = async ({ locals }) => {

	return {

		user: locals.user // ← komt uit hooks.server.ts

	};

};

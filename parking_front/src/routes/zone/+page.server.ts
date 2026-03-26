// src/routes/zone/+page.server.ts
import { fail } from '@sveltejs/kit';
import type { PageServerLoad } from './$types';

// Backend URL for server-side requests (Docker internal network)
const BACKEND_URL = process.env.BACKEND_HOST
  ? `http://${process.env.BACKEND_HOST}:${process.env.BACKEND_PORT || 8080}/api`
  : 'http://backend:8080/api';

export const load: PageServerLoad = async ({ fetch, cookies }) => {
  try {
    // Get JWT token from cookies
    const jwtToken = cookies.get('jwt_token');
    const headers: HeadersInit = {};

    if (jwtToken) {
      headers['Authorization'] = `Bearer ${jwtToken}`;
    }

    // Fetch all zones with spaces
    const zonesResponse = await fetch(`${BACKEND_URL}/zones`, {
      headers,
      credentials: 'include'
    });

    if (!zonesResponse.ok) {
      console.error('Failed to fetch zones:', zonesResponse.status);
      return { zones: [], error: 'Failed to load zones' };
    }

    const zones = await zonesResponse.json();
    return { zones };
  } catch (error) {
    console.error('Zone fetch error:', error);
    return { zones: [], error: 'Cannot connect to the server' };
  }
};
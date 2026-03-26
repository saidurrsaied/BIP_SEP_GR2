import { fail, redirect } from '@sveltejs/kit';
import type { PageServerLoad, Actions } from './$types';

// Backend URL for server-side requests (Docker internal network)
const BACKEND_URL = process.env.BACKEND_HOST
  ? `http://${process.env.BACKEND_HOST}:${process.env.BACKEND_PORT || 8080}/api`
  : 'http://backend:8080/api';

export const load: PageServerLoad = async ({ params, fetch, cookies }) => {
  const { id } = params;

  // Fetch zone data to populate form
  const jwtToken = cookies.get('jwt_token');
  const headers: HeadersInit = {};

  if (jwtToken) {
    headers['Authorization'] = `Bearer ${jwtToken}`;
  }

  try {
    const response = await fetch(`${BACKEND_URL}/zones/${id}`, {
      headers,
      credentials: 'include'
    });

    if (!response.ok) {
      throw redirect(302, '/admin/zones');
    }

    const zone = await response.json();
    return { zone };
  } catch (error) {
    console.error('Failed to load zone:', error);
    throw redirect(302, '/admin/zones');
  }
};

export const actions = {
  // Update zone
  update: async ({ request, params, cookies }) => {
    const data = await request.formData();
    const { id } = params;

    const name = data.get('name')?.toString();
    const address = data.get('address')?.toString();
    const city = data.get('city')?.toString();
    const latitude = parseFloat(data.get('latitude')?.toString() || '0');
    const longitude = parseFloat(data.get('longitude')?.toString() || '0');
    const hourlyRate = parseFloat(data.get('hourlyRate')?.toString() || '0');
    const chargingRate = parseFloat(data.get('chargingRate')?.toString() || '0');

    // Validate required fields
    if (!name || !address || !city) {
      return fail(400, { error: 'Please fill in all required fields.' });
    }

    const payload = {
      name,
      address,
      city,
      latitude,
      longitude,
      pricingPolicy: {
        hourlyRateCents: Math.round(hourlyRate * 100),
        chargingRatePerKwhCents: Math.round(chargingRate * 100),
        currency: 'EUR'
      }
    };

    try {
      const jwtToken = cookies.get('jwt_token');
      const headers: HeadersInit = {
        'Content-Type': 'application/json'
      };

      if (jwtToken) {
        headers['Authorization'] = `Bearer ${jwtToken}`;
      }

      const response = await fetch(`${BACKEND_URL}/zones/${id}`, {
        method: 'PUT',
        headers,
        credentials: 'include',
        body: JSON.stringify(payload)
      });

      if (!response.ok) {
        const errorText = await response.text();
        console.error('Zone update failed:', errorText);
        return fail(response.status, { error: `Backend error: ${errorText}` });
      }

      // Success - redirect to admin page
      throw redirect(302, '/admin');
    } catch (error) {
      if (error instanceof Error && error.message.includes('redirect')) {
        throw error;
      }
      console.error('Zone update fetch error:', error);
      return fail(500, { error: 'Cannot connect to the server.' });
    }
  },

  // Delete zone
  delete: async ({ params, cookies }) => {
    const { id } = params;

    try {
      const jwtToken = cookies.get('jwt_token');
      const headers: HeadersInit = {};

      if (jwtToken) {
        headers['Authorization'] = `Bearer ${jwtToken}`;
      }

      const response = await fetch(`${BACKEND_URL}/zones/${id}`, {
        method: 'DELETE',
        headers,
        credentials: 'include'
      });

      if (!response.ok) {
        return fail(response.status, {
          error: 'Failed to delete zone. It might still contain spaces.'
        });
      }

      // Success - redirect to admin page
      throw redirect(302, '/admin/zones');
    } catch (error) {
      if (error instanceof Error && error.message.includes('redirect')) {
        throw error;
      }
      console.error('Zone deletion fetch error:', error);
      return fail(500, { error: 'Cannot connect to the server.' });
    }
  }
} satisfies Actions;

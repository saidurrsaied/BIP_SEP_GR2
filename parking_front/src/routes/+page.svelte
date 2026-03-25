<script lang="ts">
  import { onMount } from 'svelte';
  import TopNavBar from '$lib/components/TopNavBar.svelte';
  import Sidebar from '$lib/components/Sidebar.svelte';
  import MapSection from '$lib/components/MapSection.svelte';
  import BottomNavBar from '$lib/components/BottomNavBar.svelte';
  import { apiGet } from '$lib/api/client';
  import type { MapData } from '$lib/types';

  let zones: MapData[] = $state([]);
  let isLoading: boolean = $state(true);
  let error: string | null = $state(null);

  onMount(async () => {
    try {
      isLoading = true;
      const response = await apiGet<MapData[]>('/zones/map');

      if (response.data) {
        zones = response.data;
      } else if (response.error) {
        error = response.error;
        // Use mock data as fallback for development
        zones = getMockData();
      }
    } catch (err) {
      console.error('Failed to load zones:', err);
      error = 'Failed to load parking zones';
      // Use mock data as fallback
      zones = getMockData();
    } finally {
      isLoading = false;
    }
  });

  function getMockData(): MapData[] {
    return [
      {
        zone: {
          id: 1,
          name: 'Central Plaza Hub',
          address: '123 Main St',
          city: 'Dortmund',
          latitude: 51.514,
          longitude: 7.4652,
          pricingPolicy: {
            hourlyRateCents: 450,
            chargingRatePerKwhCents: 2500,
            currency: 'EUR',
          },
        },
        spaces: [
          {
            id: 1,
            zoneId: 1,
            status: 'FREE',
            hasChargingPoint: 'TRUE',
            level: 'Ground',
            spaceNumber: 'A-001',
          },
          {
            id: 2,
            zoneId: 1,
            status: 'RESERVED',
            hasChargingPoint: 'FALSE',
            level: 'Ground',
            spaceNumber: 'A-002',
          },
          {
            id: 3,
            zoneId: 1,
            status: 'OCCUPIED',
            hasChargingPoint: 'TRUE',
            level: '1st',
            spaceNumber: 'B-001',
          },
        ],
      },
      {
        zone: {
          id: 2,
          name: 'Green Street Garage',
          address: '456 Green Ave',
          city: 'Dortmund',
          latitude: 51.515,
          longitude: 7.465,
          pricingPolicy: {
            hourlyRateCents: 300,
            chargingRatePerKwhCents: 2000,
            currency: 'EUR',
          },
        },
        spaces: [
          {
            id: 4,
            zoneId: 2,
            status: 'FREE',
            hasChargingPoint: 'TRUE',
            level: 'Ground',
            spaceNumber: 'A-001',
          },
          {
            id: 5,
            zoneId: 2,
            status: 'FREE',
            hasChargingPoint: 'FALSE',
            level: 'Ground',
            spaceNumber: 'A-002',
          },
        ],
      },
      {
        zone: {
          id: 3,
          name: 'Riverfront Parking',
          address: '789 River Rd',
          city: 'Dortmund',
          latitude: 51.513,
          longitude: 7.466,
          pricingPolicy: {
            hourlyRateCents: 250,
            chargingRatePerKwhCents: 0,
            currency: 'EUR',
          },
        },
        spaces: [
          {
            id: 6,
            zoneId: 3,
            status: 'FREE',
            hasChargingPoint: 'FALSE',
            level: 'Ground',
            spaceNumber: 'A-001',
          },
        ],
      },
    ];
  }
</script>

<!DOCTYPE html>
<html class="light" lang="en">
  <head>
    <meta charset="utf-8" />
    <meta content="width=device-width, initial-scale=1.0" name="viewport" />
    <title>UrbanFlow | Citizen Map Search</title>
    <script src="https://cdn.tailwindcss.com?plugins=forms,container-queries"></script>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@100..900&display=swap" rel="stylesheet" />
    <link
      href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap"
      rel="stylesheet"
    />
    <script id="tailwind-config">
      tailwind.config = {
        darkMode: 'class',
        theme: {
          extend: {
            colors: {
              'on-background': '#191c1f',
              'on-tertiary-fixed-variant': '#005228',
              'outline-variant': '#c4c6cd',
              error: '#ba1a1a',
              'secondary-fixed': '#6bfe9c',
              'surface-container-highest': '#e1e2e6',
              'on-tertiary-fixed': '#00210c',
              'surface-container': '#eceef1',
              'surface-container-low': '#f2f4f7',
              'secondary-fixed-dim': '#4ae183',
              'on-primary-fixed-variant': '#38485a',
              'error-container': '#ffdad6',
              'surface-tint': '#4f6073',
              'primary-container': '#1a2b3c',
              'on-primary-container': '#8192a7',
              'inverse-surface': '#2e3134',
              'on-secondary-container': '#00743a',
              'on-surface-variant': '#44474c',
              primary: '#041627',
              'surface-dim': '#d8dade',
              'tertiary-fixed': '#6bfe9c',
              tertiary: '#001a08',
              'surface-variant': '#e1e2e6',
              'secondary-container': '#6bfe9c',
              secondary: '#006d37',
              'inverse-on-surface': '#eff1f4',
              'primary-fixed-dim': '#b7c8de',
              'on-error-container': '#93000a',
              'tertiary-fixed-dim': '#4ae183',
              'on-primary': '#ffffff',
              'surface-container-lowest': '#ffffff',
              'on-tertiary': '#ffffff',
              background: '#f8f9fd',
              'on-error': '#ffffff',
              outline: '#74777d',
              'on-primary-fixed': '#0b1d2d',
              'on-tertiary-container': '#00a656',
              'on-secondary-fixed-variant': '#005228',
              'inverse-primary': '#b7c8de',
              'primary-fixed': '#d2e4fb',
              'on-surface': '#191c1f',
              'tertiary-container': '#003115',
              surface: '#f8f9fd',
              'on-secondary-fixed': '#00210c',
              'on-secondary': '#ffffff',
              'surface-container-high': '#e6e8ec',
              'surface-bright': '#f8f9fd',
            },
            fontFamily: {
              headline: ['Inter'],
              body: ['Inter'],
              label: ['Inter'],
            },
            borderRadius: { DEFAULT: '0.125rem', lg: '0.25rem', xl: '0.5rem', full: '0.75rem' },
          },
        },
      };
    </script>
    <style>
      :global(.material-symbols-outlined) {
        font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
      }
      :global(body) {
        font-family: 'Inter', sans-serif;
      }
      :global(.glass-panel) {
        background: rgba(225, 226, 230, 0.7);
        backdrop-filter: blur(20px);
      }
    </style>
  </head>
  <body class="bg-surface text-on-surface antialiased overflow-hidden">
    <TopNavBar />

    <main class="pt-16 h-screen w-full flex relative">
      <Sidebar {zones} {isLoading} />
      <MapSection {zones} />
    </main>

    <BottomNavBar />

    {#if error}
      <div class="fixed bottom-24 md:bottom-4 right-4 bg-error text-on-error px-4 py-2 rounded-lg">
        {error}
      </div>
    {/if}
  </body>
</html>

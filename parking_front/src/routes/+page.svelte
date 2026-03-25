<script lang="ts">
  import { onMount } from 'svelte';
  import TopNavBar from '$lib/components/TopNavBar.svelte';
  import Sidebar from '$lib/components/Sidebar.svelte';
  import MapSection from '$lib/components/MapSection.svelte';
  import BottomNavBar from '$lib/components/BottomNavBar.svelte';
  import { apiGet } from '$lib/api/client';
  import type { MapData } from '$lib/types';

  let zones = $state<MapData[]>([]);
  let isLoading = $state<boolean>(true);
  let error = $state<string | null>(null);

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
          { id: 1, zoneId: 1, status: 'FREE', hasChargingPoint: 'TRUE', level: 'Ground', spaceNumber: 'A-001' },
          { id: 2, zoneId: 1, status: 'RESERVED', hasChargingPoint: 'FALSE', level: 'Ground', spaceNumber: 'A-002' },
          { id: 3, zoneId: 1, status: 'OCCUPIED', hasChargingPoint: 'TRUE', level: '1st', spaceNumber: 'B-001' },
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
          { id: 4, zoneId: 2, status: 'FREE', hasChargingPoint: 'TRUE', level: 'Ground', spaceNumber: 'A-001' },
          { id: 5, zoneId: 2, status: 'FREE', hasChargingPoint: 'FALSE', level: 'Ground', spaceNumber: 'A-002' },
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
          { id: 6, zoneId: 3, status: 'FREE', hasChargingPoint: 'FALSE', level: 'Ground', spaceNumber: 'A-001' },
        ],
      },
    ];
  }
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

<div class="bg-surface text-on-surface antialiased overflow-hidden min-h-screen">
  <TopNavBar />

  <main class="pt-16 h-screen w-full flex relative">
    <Sidebar {zones} {isLoading} />
    <MapSection {zones} />
  </main>

  <BottomNavBar />

  {#if error}
    <div class="fixed bottom-24 md:bottom-4 right-4 bg-error text-on-error px-4 py-2 rounded-lg z-50">
      {error}
    </div>
  {/if}
</div>
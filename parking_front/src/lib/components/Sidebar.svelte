<script lang="ts">
  import type { ParkingSpace, ParkingZone, MapData } from '$lib/types';

  interface Props {
    zones: MapData[];
    isLoading: boolean;
  }

  let { zones = [], isLoading = false }: Props = $props();

  // Format price for display
  function formatPrice(cents: number): string {
    return (cents / 100).toFixed(2);
  }

  // Count available spaces
  function countAvailable(zone: MapData): number {
    return zone.spaces.filter((s) => s.status === 'FREE').length;
  }

  // Get space status badge color
  function getStatusBadge(status: string): { bg: string; text: string; label: string } {
    switch (status) {
      case 'FREE':
        return {
          bg: 'bg-secondary-container',
          text: 'text-on-secondary-container',
          label: 'FREE',
        };
      case 'RESERVED':
        return {
          bg: 'bg-primary-fixed',
          text: 'text-on-primary-fixed-variant',
          label: 'RESERVED',
        };
      case 'OCCUPIED':
        return {
          bg: 'bg-error-container',
          text: 'text-on-error-container',
          label: 'OCCUPIED',
        };
      default:
        return {
          bg: 'bg-surface-container',
          text: 'text-on-surface',
          label: status,
        };
    }
  }
</script>

<!-- Sidebar: Discovery List -->
<aside class="w-96 bg-surface-container-low h-full flex flex-col z-20 shadow-xl overflow-hidden">
  <div class="p-6 pb-4">
    <h1 class="text-headline-sm font-bold tracking-tight mb-2">Nearby Spaces</h1>
    <p class="text-on-surface-variant text-sm body-md">
      Found {zones.reduce((acc, z) => acc + countAvailable(z), 0)} available spots in your area
    </p>

    <!-- Quick Filters -->
    <div class="flex flex-wrap gap-2 mt-4">
      <button
        class="px-3 py-1.5 rounded-full bg-secondary-container text-on-secondary-container text-xs font-bold label-sm tracking-wide flex items-center gap-1"
      >
        <span class="material-symbols-outlined text-sm">bolt</span>
        CHARGING
      </button>
      <button
        class="px-3 py-1.5 rounded-full bg-surface-container-lowest border border-outline-variant text-on-surface-variant text-xs font-bold label-sm tracking-wide"
      >
        DISTANCE
      </button>
      <button
        class="px-3 py-1.5 rounded-full bg-surface-container-lowest border border-outline-variant text-on-surface-variant text-xs font-bold label-sm tracking-wide"
      >
        PRICE
      </button>
    </div>
  </div>

  <!-- Scrollable List -->
  <div class="flex-1 overflow-y-auto px-6 pb-24 space-y-4">
    {#if isLoading}
      <div class="flex justify-center items-center h-full">
        <div class="text-on-surface-variant text-sm">Loading spaces...</div>
      </div>
    {:else if zones.length === 0}
      <div class="flex justify-center items-center h-full">
        <div class="text-on-surface-variant text-sm">No zones available</div>
      </div>
    {:else}
      {#each zones as mapData (mapData.zone.id)}
        <div class="space-y-3">
          <!-- Zone Header -->
          <div class="px-2">
            <h2 class="font-bold text-primary text-sm">{mapData.zone.name}</h2>
            <p class="text-on-surface-variant text-xs">{mapData.zone.address}</p>
          </div>

          <!-- Spaces in Zone -->
          {#each mapData.spaces as space (space.id)}
            <div
              class="bg-surface-container-lowest p-4 rounded-xl shadow-sm hover:bg-surface-container-highest transition-all cursor-pointer group"
            >
              <div class="flex justify-between items-start mb-2">
                <h3 class="font-bold text-primary group-hover:text-secondary transition-colors">
                  {mapData.zone.name} - {space.spaceNumber}
                </h3>
                <span class="{getStatusBadge(space.status).bg} {getStatusBadge(space.status).text} px-2.5 py-1 rounded-full text-[10px] font-bold label-sm">
                  {getStatusBadge(space.status).label}
                </span>
              </div>
              <div class="space-y-2">
                <div class="flex items-center gap-2 text-on-surface-variant text-xs">
                  <span class="material-symbols-outlined text-sm">location_on</span>
                  <span>Level {space.level} • Zone {mapData.zone.id}</span>
                </div>
                <div class="flex items-center gap-2 text-on-surface-variant text-xs">
                  <span class="material-symbols-outlined text-sm"
                    >{space.hasChargingPoint === 'TRUE' ? 'bolt' : 'remove'}</span
                  >
                  <span class={space.hasChargingPoint === 'TRUE' ? 'text-secondary font-semibold' : ''}>
                    {space.hasChargingPoint === 'TRUE'
                      ? `${(mapData.zone.pricingPolicy.chargingRatePerKwhCents / 100).toFixed(2)}/kWh Charging`
                      : 'No Charging Available'}
                  </span>
                </div>
                <div class="flex justify-between items-center pt-2 mt-2 border-t border-surface-container">
                  <span class="text-sm font-black text-primary"
                    >${formatPrice(mapData.zone.pricingPolicy.hourlyRateCents)}<span
                      class="font-normal text-xs text-on-surface-variant">/hr</span
                    ></span
                  >
                  <button class="text-xs font-bold text-secondary uppercase tracking-widest flex items-center gap-1">
                    DETAILS
                    <span class="material-symbols-outlined text-xs">arrow_forward</span>
                  </button>
                </div>
              </div>
            </div>
          {/each}
        </div>
      {/each}
    {/if}
  </div>
</aside>

<style>
  :global(.material-symbols-outlined) {
    font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  }
</style>

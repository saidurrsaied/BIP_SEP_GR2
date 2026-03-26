<script lang="ts">
  import type { ParkingSpace, ParkingZone, MapData } from '$lib/types';

  interface Props {
    zones: any[]; // Gebruikt de platte data structuur van de backend
    isLoading: boolean;
  }

  let { zones = [], isLoading = false }: Props = $props();

  // Formatteer centen naar Euro weergave
  function formatPrice(cents: number): string {
    return (cents / 100).toFixed(2);
  }

  // Tel beschikbare plaatsen in een zone
  function countAvailable(zone: any): number {
    return (zone.spaces || []).filter((s: any) => s.status === 'FREE').length;
  }

  // Kleuren voor de status badges
  function getStatusBadge(status: string): { bg: string; text: string; label: string } {
    switch (status) {
      case 'FREE':
        return { bg: 'bg-secondary-container', text: 'text-on-secondary-container', label: 'FREE' };
      case 'RESERVED':
        return { bg: 'bg-primary-fixed', text: 'text-on-primary-fixed-variant', label: 'RESERVED' };
      case 'OCCUPIED':
        return { bg: 'bg-error-container', text: 'text-on-error-container', label: 'OCCUPIED' };
      default:
        return { bg: 'bg-surface-container', text: 'text-on-surface', label: status };
    }
  }
</script>

<aside class="w-96 bg-surface-container-low h-full flex flex-col z-20 shadow-xl overflow-hidden">
  <div class="p-6 pb-4">
    <h1 class="text-headline-sm font-bold tracking-tight mb-2">Nearby Zones</h1>
    <p class="text-on-surface-variant text-sm body-md">
      Found {zones.reduce((acc, z) => acc + countAvailable(z), 0)} available spots in your area
    </p>

    <div class="flex flex-wrap gap-2 mt-4">
      <button class="px-3 py-1.5 rounded-full bg-secondary-container text-on-secondary-container text-xs font-bold label-sm tracking-wide flex items-center gap-1">
        <span class="material-symbols-outlined text-sm">bolt</span>
        CHARGING
      </button>
      <button class="px-3 py-1.5 rounded-full bg-surface-container-lowest border border-outline-variant text-on-surface-variant text-xs font-bold label-sm tracking-wide">
        DISTANCE
      </button>
      <button class="px-3 py-1.5 rounded-full bg-surface-container-lowest border border-outline-variant text-on-surface-variant text-xs font-bold label-sm tracking-wide">
        PRICE
      </button>
    </div>
  </div>

  <div class="flex-1 overflow-y-auto px-6 pb-24 space-y-4">
    {#if isLoading}
      <div class="flex justify-center items-center h-full">
        <div class="text-on-surface-variant text-sm">Loading parking data...</div>
      </div>
    {:else if !zones || zones.length === 0}
      <div class="flex justify-center items-center h-full">
        <div class="text-on-surface-variant text-sm">No zones found.</div>
      </div>
    {:else}
      {#each zones as mapData (mapData.zoneId)}
        <div class="bg-surface-container-lowest p-5 rounded-2xl shadow-sm border border-outline-variant/30 hover:shadow-md transition-all group">
          
          <div class="flex justify-between items-start mb-3">
            <div>
              <h2 class="font-bold text-primary text-lg leading-tight group-hover:text-secondary transition-colors">
                {mapData.name}
              </h2>
              <p class="text-on-surface-variant text-xs flex items-center gap-1 mt-1">
                <span class="material-symbols-outlined text-sm">location_on</span>
                {mapData.address || 'Address unknown'}
              </p>
            </div>
            
            <div class="flex flex-col items-end gap-2">
              {#if mapData.spaces && mapData.spaces.length > 0}
                <span class="bg-secondary-container text-on-secondary-container px-2.5 py-1 rounded-full text-[10px] font-black">
                  {countAvailable(mapData)} / {mapData.spaces.length} FREE
                </span>
              {:else}
                <span class="bg-surface-container-high text-on-surface-variant px-2.5 py-1 rounded-full text-[10px] font-bold">
                  NO SPACES
                </span>
              {/if}
            </div>
          </div>

          <div class="space-y-1.5 mb-4">
            <div class="flex items-center gap-2 text-xs">
              <span class="material-symbols-outlined text-sm text-on-surface-variant">payments</span>
              <span class="font-bold text-primary">€{formatPrice(mapData.pricingPolicy?.hourlyRateCents || 0)}</span>
              <span class="text-on-surface-variant">/ hour</span>
            </div>
            
            {#if mapData.spaces && mapData.spaces.some(s => s.hasChargingPoint === 'TRUE' || s.chargingPoint === 'TRUE')}
              <div class="flex items-center gap-2 text-xs">
                <span class="material-symbols-outlined text-sm text-secondary">bolt</span>
                <span class="text-secondary font-bold">EV Charging Available</span>
              </div>
            {/if}
          </div>

          <div class="flex gap-2 pt-3 border-t border-surface-container-highest">
            <a 
              href="/zones/{mapData.zoneId}" 
              class="flex-1 flex items-center justify-center gap-1.5 py-2 bg-surface-container hover:bg-surface-container-high text-on-surface text-[11px] font-bold uppercase tracking-wider rounded-xl transition-colors"
            >
              <span class="material-symbols-outlined text-sm">visibility</span>
              Details
            </a>
            
            <a 
              href="/admin/zones/{mapData.zoneId}/edit" 
              class="flex-1 flex items-center justify-center gap-1.5 py-2 bg-secondary text-on-secondary hover:bg-secondary-container hover:text-on-secondary-container text-[11px] font-bold uppercase tracking-wider rounded-xl transition-colors shadow-sm"
            >
              <span class="material-symbols-outlined text-sm">edit</span>
              Edit Zone
            </a>
          </div>

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
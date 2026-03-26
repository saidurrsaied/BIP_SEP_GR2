<script lang="ts">
  import type { MapData } from '$lib/types';

  interface Props {
    // Gebruik tijdelijk any[] als je TypeScript klaagt over de platte structuur
    zones: any[]; 
  }

  let { zones = [] }: Props = $props();

  // Calculate average coordinates for map center
  function getMapCenter(): { lat: number; lng: number } {
    if (!zones || zones.length === 0) {
      return { lat: 51.514, lng: 7.4652 }; // Default to Dortmund
    }

    // GEFIXED: we gebruiken z.latitude en z.longitude direct
    const avgLat = zones.reduce((sum, z) => sum + (z.latitude || 0), 0) / zones.length;
    const avgLng = zones.reduce((sum, z) => sum + (z.longitude || 0), 0) / zones.length;

    return { lat: avgLat, lng: avgLng };
  }
</script>

<section class="flex-1 relative bg-surface-container">
  <div
    class="absolute inset-0 bg-cover bg-center"
    style="background-image: url('/img/Dortmund.jpeg')"
  >
    <div class="absolute inset-0 bg-primary/5"></div>
  </div>

  {#each zones as mapData, idx (mapData.zoneId)}
    {#each (mapData.spaces || []).slice(0, 1) as space (space.id || space.spaceId)}
      <div
        class="absolute"
        style="left: {30 + idx * 20}%; top: {40 + idx * 15}%;"
      >
        <div class="relative group cursor-pointer hover:z-10">
          
          <div
            class="absolute bottom-full mb-3 left-1/2 -translate-x-1/2 w-48 glass-panel p-3 rounded-xl text-xs font-bold shadow-xl opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none group-hover:pointer-events-auto"
          >
            <p class="text-primary truncate text-sm mb-1">{mapData.name}</p>
            <p class="text-secondary mb-3">
              €{((mapData.pricingPolicy?.hourlyRateCents || 0) / 100).toFixed(2)} • {space.status}
            </p>
            
            <div class="flex justify-between items-center pt-2 border-t border-surface-container-highest/50 gap-2">
              <a 
                href="/zones/{mapData.zoneId}" 
                class="flex-1 text-center py-1.5 bg-surface-container-low hover:bg-surface-container rounded text-on-surface uppercase tracking-wider text-[10px] transition-colors"
              >
                Details
              </a>
              <a 
                href="/admin/zones/{mapData.zoneId}/edit" 
                class="flex-1 text-center py-1.5 bg-secondary hover:bg-secondary-container text-on-secondary hover:text-on-secondary-container rounded uppercase tracking-wider text-[10px] transition-colors"
              >
                Edit
              </a>
            </div>

          </div>
          
          <div
            class="w-10 h-10 bg-primary rounded-full flex items-center justify-center shadow-lg border-2 border-white"
          >
            <div
              class="w-3 h-3 {space.status === 'FREE' ? 'bg-secondary' : 'bg-error'} rounded-full animate-pulse"
            ></div>
          </div>
          <div class="absolute top-1/2 left-1/2 -translate-x-1/2 w-0.5 h-4 bg-primary"></div>
        </div>
      </div>
    {/each}
  {/each}

  <div class="absolute left-1/2 -translate-x-1/2 bottom-10 w-full max-w-lg px-6 md:hidden">
    <div class="bg-surface-container-lowest shadow-2xl rounded-2xl p-4 flex items-center gap-4">
      <span class="material-symbols-outlined text-secondary">search</span>
      <input
        class="flex-1 bg-transparent border-none focus:ring-0 text-sm"
        placeholder="Where to?"
        type="text"
      />
      <span class="material-symbols-outlined text-on-surface-variant">tune</span>
    </div>
  </div>
</section>

<style>
  :global(.material-symbols-outlined) {
    font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  }

  :global(.glass-panel) {
    background: rgba(225, 226, 230, 0.7);
    backdrop-filter: blur(20px);
  }
</style>
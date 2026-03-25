<script lang="ts">
  import type { MapData } from '$lib/types';

  interface Props {
    zones: MapData[];
  }

  let { zones = [] }: Props = $props();

  // Calculate average coordinates for map center
  function getMapCenter(): { lat: number; lng: number } {
    if (zones.length === 0) {
      return { lat: 51.514, lng: 7.4652 }; // Default to Dortmund
    }

    const avgLat = zones.reduce((sum, z) => sum + z.zone.latitude, 0) / zones.length;
    const avgLng = zones.reduce((sum, z) => sum + z.zone.longitude, 0) / zones.length;

    return { lat: avgLat, lng: avgLng };
  }
</script>

<!-- Main Map Area -->
<section class="flex-1 relative bg-surface-container">
  <!-- Mock Map Background -->
  <div
    class="absolute inset-0 bg-cover bg-center"
    style="background-image: url('https://lh3.googleusercontent.com/aida-public/AB6AXuDCclsHXePmbkf7BAQor8IhQqLytrRPMw7bn-bqjKy4J3CKb5mxOx_LvmORfjzG4x4rsNoCABBrHaAk_d1ZeWoOdu5Wp2RR0cWjt4SVeDO7N4Tqm3skA_D1jpOON8qIt7cBXSMh6V8Oil1JP0l5Geb8d5UT2iQcEmEv29Ah1Am7IKja9PoxM8jhZd5XNFlOj6dRhqm01HETgf7aOc4QFOO0eF8HeL-oDqMeMuqX4Aa_SljEwfu-omiKKo_Ok0hj1Aqm00nNTAbdMwxp')"
  >
    <!-- Overlay for depth -->
    <div class="absolute inset-0 bg-primary/5"></div>
  </div>

  <!-- Floating Map Controls -->
  <div class="absolute right-6 top-6 flex flex-col gap-2">
    <div class="bg-surface-container-lowest rounded-lg shadow-xl flex flex-col overflow-hidden">
      <button class="p-3 hover:bg-surface-container transition-colors text-primary border-b border-surface-container">
        <span class="material-symbols-outlined">add</span>
      </button>
      <button class="p-3 hover:bg-surface-container transition-colors text-primary">
        <span class="material-symbols-outlined">remove</span>
      </button>
    </div>
    <button class="p-3 bg-surface-container-lowest rounded-lg shadow-xl hover:bg-surface-container transition-colors text-primary">
      <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">my_location</span>
    </button>
    <button class="p-3 bg-surface-container-lowest rounded-lg shadow-xl hover:bg-surface-container transition-colors text-primary">
      <span class="material-symbols-outlined">layers</span>
    </button>
  </div>

  <!-- Map Pins -->
  {#each zones as mapData, idx (mapData.zone.id)}
    {#each mapData.spaces.slice(0, 1) as space (space.id)}
      <div
        class="absolute"
        style="left: {30 + idx * 20}%; top: {40 + idx * 15}%;"
      >
        <div class="relative group cursor-pointer">
          <div
            class="absolute -top-12 -left-1/2 w-32 glass-panel p-2 rounded-xl text-xs font-bold shadow-lg opacity-0 group-hover:opacity-100 transition-opacity duration-300"
          >
            <p class="text-primary truncate">{mapData.zone.name}</p>
            <p class="text-secondary">
              ${(mapData.zone.pricingPolicy.hourlyRateCents / 100).toFixed(2)} • {space.status}
            </p>
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

  <!-- Floating Search Bar for Mobile -->
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

<script lang="ts">
  import TopNavBar from '$lib/components/TopNavBar.svelte';
  import BottomNavBar from '$lib/components/BottomNavBar.svelte';
</script>

<TopNavBar />

<main class="pt-16 pb-20 min-h-screen bg-surface">
  <div class="max-w-2xl mx-auto px-4 py-8">
    <h1 class="text-3xl font-bold text-on-surface mb-6">EV Charging Stations</h1>

    <!-- Charging Stations Grid -->
    <div class="grid gap-4 md:grid-cols-2">
      <!-- Charging Station Card 1 -->
      <div class="bg-surface-container-lowest p-6 rounded-2xl shadow-sm hover:shadow-md transition-shadow">
        <div class="flex items-start justify-between mb-4">
          <div>
            <h3 class="text-xl font-bold text-primary">Central Hub — 50kW</h3>
            <p class="text-on-surface-variant text-sm">Fast DC Charging</p>
          </div>
          <span class="px-3 py-1 rounded-full bg-secondary-container text-on-secondary-container text-xs font-bold">
            AVAILABLE
          </span>
        </div>

        <div class="space-y-3 mb-4">
          <div class="flex items-center gap-2 text-on-surface-variant">
            <span class="material-symbols-outlined text-lg">location_on</span>
            <span class="text-sm">123 Main St, Dortmund</span>
          </div>
          <div class="flex items-center gap-2 text-on-surface-variant">
            <span class="material-symbols-outlined text-lg">flash_on</span>
            <span class="text-sm">50kW DC • 3 available</span>
          </div>
          <div class="flex items-center gap-2 text-on-surface-variant">
            <span class="material-symbols-outlined text-lg">access_time</span>
            <span class="text-sm">~30 min for full charge</span>
          </div>
        </div>

        <div class="border-t border-surface-container pt-4">
          <p class="text-lg font-bold text-primary mb-2">
            €0.45<span class="text-sm font-normal text-on-surface-variant">/min</span>
          </p>
          <button
            class="w-full bg-secondary text-on-secondary py-2 rounded-lg font-semibold hover:opacity-90 transition-opacity"
          >
            Reserve Charger
          </button>
        </div>
      </div>

      <!-- Charging Station Card 2 -->
      <div class="bg-surface-container-lowest p-6 rounded-2xl shadow-sm hover:shadow-md transition-shadow">
        <div class="flex items-start justify-between mb-4">
          <div>
            <h3 class="text-xl font-bold text-primary">Green Street — 22kW</h3>
            <p class="text-on-surface-variant text-sm">Level 2 Charging</p>
          </div>
          <span class="px-3 py-1 rounded-full bg-secondary-container text-on-secondary-container text-xs font-bold">
            AVAILABLE
          </span>
        </div>

        <div class="space-y-3 mb-4">
          <div class="flex items-center gap-2 text-on-surface-variant">
            <span class="material-symbols-outlined text-lg">location_on</span>
            <span class="text-sm">456 Green Ave, Dortmund</span>
          </div>
          <div class="flex items-center gap-2 text-on-surface-variant">
            <span class="material-symbols-outlined text-lg">flash_on</span>
            <span class="text-sm">22kW AC • 5 available</span>
          </div>
          <div class="flex items-center gap-2 text-on-surface-variant">
            <span class="material-symbols-outlined text-lg">access_time</span>
            <span class="text-sm">~2 hours for full charge</span>
          </div>
        </div>

        <div class="border-t border-surface-container pt-4">
          <p class="text-lg font-bold text-primary mb-2">
            €0.25<span class="text-sm font-normal text-on-surface-variant">/min</span>
          </p>
          <button
            class="w-full bg-secondary text-on-secondary py-2 rounded-lg font-semibold hover:opacity-90 transition-opacity"
          >
            Reserve Charger
          </button>
        </div>
      </div>
    </div>
  </div>
</main>

<BottomNavBar />

<style>
  :global(.material-symbols-outlined) {
    font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  }
</style>

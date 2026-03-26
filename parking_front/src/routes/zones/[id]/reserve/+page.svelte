<script lang="ts">
  import { enhance } from '$app/forms';
  import TopNavBar from '$lib/components/TopNavBar.svelte';
  import type { ActionData, PageData } from './$types';

  let { data, form } = $props<{ data: PageData; form: ActionData }>();
  
  let selectedSpaceId = $state("");
  let isSubmitting = $state(false);

  function formatPrice(cents: number): string {
    return (cents / 100).toFixed(2);
  }
</script>

<TopNavBar />

<main class="pt-20 pb-24 min-h-screen bg-surface">
  <div class="max-w-xl mx-auto px-6">
    
    <div class="bg-surface-container-high p-6 rounded-3xl mb-8 border border-outline-variant">
      <h1 class="text-2xl font-bold text-primary">{data.zone.name}</h1>
      <p class="text-sm text-on-surface-variant mb-4">{data.zone.address}, {data.zone.city}</p>
      <div class="flex gap-4">
        <span class="px-3 py-1 bg-secondary-container text-on-secondary-container rounded-full text-[10px] font-black uppercase">
          €{formatPrice(data.zone.pricingPolicy?.hourlyRateCents)}/HR
        </span>
      </div>
    </div>

    {#if form?.success}
      <div class="bg-secondary-container text-on-secondary-container p-10 rounded-[2rem] text-center shadow-xl border border-secondary/20">
        <div class="w-20 h-20 bg-secondary text-on-secondary rounded-full flex items-center justify-center mx-auto mb-6">
          <span class="material-symbols-outlined text-4xl">done_all</span>
        </div>
        <h2 class="text-3xl font-black mb-2">Gelukt!</h2>
        <p class="opacity-80 mb-8 font-medium">Je plek is gereserveerd voor de komende 2 uur.</p>
        <a href="/" class="block w-full bg-on-secondary-container text-secondary-container py-4 rounded-2xl font-bold uppercase tracking-widest transition-transform hover:scale-[1.02]">
          Terug naar overzicht
        </a>
      </div>
    {:else}
      <div class="flex items-center justify-between mb-6 px-2">
        <h2 class="text-xs font-black text-on-surface-variant uppercase tracking-widest">Kies je plek</h2>
        <span class="text-[10px] font-bold text-secondary">{data.freeSpaces.length} beschikbaar</span>
      </div>

      <form method="POST" action="?/confirm" use:enhance={() => {
        isSubmitting = true;
        return async ({ update }) => {
          await update();
          isSubmitting = false;
        };
      }} class="space-y-8">
        <input type="hidden" name="spaceId" value={selectedSpaceId} />

        <div class="grid grid-cols-2 gap-4">
          {#each data.freeSpaces as space (space.spaceId)}
            <button
              type="button"
              onclick={() => selectedSpaceId = space.spaceId}
              class="relative p-6 rounded-3xl border-2 transition-all text-left flex flex-col justify-between h-32 {selectedSpaceId === space.spaceId 
                ? 'border-secondary bg-secondary/5 ring-2 ring-secondary/20' 
                : 'border-surface-container-highest bg-surface-container-lowest hover:border-outline-variant'}"
            >
              <div class="flex justify-between items-start w-full">
                <span class="font-black text-xl {selectedSpaceId === space.spaceId ? 'text-secondary' : 'text-primary'}">
                  {space.spaceNumber}
                </span>
                {#if space.chargingPoint !== 'FALSE'}
                  <span class="material-symbols-outlined text-secondary text-sm">bolt</span>
                {/if}
              </div>
              
              <div>
                <p class="text-[10px] font-black text-on-surface-variant uppercase tracking-tighter">Verdieping {space.level}</p>
                {#if space.chargingPoint !== 'FALSE'}
                  <p class="text-[9px] font-bold text-secondary uppercase">EV READY</p>
                {/if}
              </div>

              {#if selectedSpaceId === space.spaceId}
                <div class="absolute -top-2 -right-2 bg-secondary text-on-secondary rounded-full w-7 h-7 flex items-center justify-center shadow-lg border-2 border-white">
                  <span class="material-symbols-outlined text-sm font-bold">check</span>
                </div>
              {/if}
            </button>
          {:else}
            <div class="col-span-2 py-12 text-center bg-surface-container-lowest rounded-3xl border border-dashed border-outline-variant">
              <span class="material-symbols-outlined text-4xl text-on-surface-variant mb-2">block</span>
              <p class="text-sm font-bold text-on-surface-variant italic">Geen plekken vrij...</p>
            </div>
          {/each}
        </div>

        {#if form?.error}
          <div class="p-4 bg-error-container text-on-error-container rounded-2xl text-xs font-bold flex items-center gap-2">
            <span class="material-symbols-outlined text-sm">error</span>
            {form.error}
          </div>
        {/if}

        <button
          type="submit"
          disabled={!selectedSpaceId || isSubmitting}
          class="w-full bg-primary text-on-primary py-5 rounded-[1.5rem] font-black text-lg shadow-xl transition-all active:scale-95 disabled:opacity-20 disabled:grayscale flex items-center justify-center gap-3"
        >
          {#if isSubmitting}
            <span class="material-symbols-outlined animate-spin">progress_activity</span>
            Even geduld...
          {:else}
            <span class="material-symbols-outlined">calendar_today</span>
            BEVESTIG RESERVERING
          {/if}
        </button>
      </form>
    {/if}
  </div>
</main>

<style>
  :global(.material-symbols-outlined) {
    font-variation-settings: 'FILL' 1, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  }
</style>
<script lang="ts">
  import { enhance } from '$app/forms';
  import type { ActionData, PageData } from './$types';

  let { data, form } = $props<{ data: PageData; form: ActionData }>();
  
  // We maken zone reactief zodat de UI update na een succesvolle actie
  let zone = $derived(data.zone);

  let isUpdating = $state(false);
  let isDeleting = $state(false);
  let isAddingSpace = $state(false);

  // Helper voor prijzen
  function formatPrice(cents: number): string {
    return (cents / 100).toFixed(2);
  }

  // De centen uit de database omzetten naar euro's voor de input velden
  let currentHourlyRate = (zone.pricingPolicy?.hourlyRateCents || 0) / 100;
  let currentChargingRate = (zone.pricingPolicy?.chargingRatePerKwhCents || 0) / 100;
</script>

<div class="min-h-screen bg-surface flex flex-col items-center p-6 space-y-6">
  
  <div class="bg-surface-container-lowest p-8 rounded-2xl shadow-xl w-full max-w-2xl border border-surface-container-highest">
    <div class="mb-8 flex justify-between items-start">
      <div>
        <h1 class="text-2xl font-bold text-primary mb-2">Edit Zone Settings</h1>
        <p class="text-on-surface-variant text-sm">Update the general configuration for <strong>{zone.name}</strong>.</p>
      </div>
      <span class="bg-surface-container-high px-3 py-1 rounded-lg text-[10px] font-black text-on-surface uppercase tracking-widest">Zone ID: {zone.zoneId?.slice(0, 8)}</span>
    </div>

    {#if form?.error}
      <div class="bg-error-container text-on-error-container p-4 rounded-lg mb-6 text-sm font-semibold flex items-center gap-2 animate-pulse">
        <span class="material-symbols-outlined">error</span> {form.error}
      </div>
    {/if}

    {#if form?.success}
      <div class="bg-secondary-container text-on-secondary-container p-4 rounded-lg mb-6 text-sm font-semibold flex items-center gap-2">
        <span class="material-symbols-outlined">check_circle</span> {form.message}
      </div>
    {/if}

    <form method="POST" action="?/update" use:enhance={() => {
      isUpdating = true;
      return async ({ update }) => {
        await update({ reset: false });
        isUpdating = false;
      };
    }} class="space-y-6">

      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label for="name" class="block text-xs font-bold text-on-surface-variant mb-1 uppercase tracking-wide">Zone Name</label>
          <input id="name" name="name" type="text" value={zone.name} required
            class="w-full bg-surface-container-low border border-outline-variant text-on-surface rounded-lg px-4 py-2 focus:ring-2 focus:ring-primary outline-none" />
        </div>
        <div>
          <label for="city" class="block text-xs font-bold text-on-surface-variant mb-1 uppercase tracking-wide">City</label>
          <input id="city" name="city" type="text" value={zone.city} required
            class="w-full bg-surface-container-low border border-outline-variant text-on-surface rounded-lg px-4 py-2 focus:ring-2 focus:ring-primary outline-none" />
        </div>
        <div class="md:col-span-2">
          <label for="address" class="block text-xs font-bold text-on-surface-variant mb-1 uppercase tracking-wide">Address</label>
          <input id="address" name="address" type="text" value={zone.address} required
            class="w-full bg-surface-container-low border border-outline-variant text-on-surface rounded-lg px-4 py-2 focus:ring-2 focus:ring-primary outline-none" />
        </div>
      </div>

      <hr class="border-surface-container-highest my-6" />

      <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
        <div>
          <label for="latitude" class="block text-xs font-bold text-on-surface-variant mb-1 uppercase tracking-wide">Latitude</label>
          <input id="latitude" name="latitude" type="number" step="0.000001" value={zone.latitude} required
            class="w-full bg-surface-container-low border border-outline-variant text-on-surface rounded-lg px-4 py-2 focus:ring-2 focus:ring-primary outline-none" />
        </div>
        <div>
          <label for="longitude" class="block text-xs font-bold text-on-surface-variant mb-1 uppercase tracking-wide">Longitude</label>
          <input id="longitude" name="longitude" type="number" step="0.000001" value={zone.longitude} required
            class="w-full bg-surface-container-low border border-outline-variant text-on-surface rounded-lg px-4 py-2 focus:ring-2 focus:ring-primary outline-none" />
        </div>
        <div>
          <label for="hourlyRate" class="block text-xs font-bold text-on-surface-variant mb-1 uppercase tracking-wide">Hourly Rate (€)</label>
          <input id="hourlyRate" name="hourlyRate" type="number" step="0.10" value={currentHourlyRate} required
            class="w-full bg-surface-container-low border border-outline-variant text-on-surface rounded-lg px-4 py-2 focus:ring-2 focus:ring-primary outline-none" />
        </div>
        <div>
          <label for="chargingRate" class="block text-xs font-bold text-on-surface-variant mb-1 uppercase tracking-wide">Charging Rate/kWh (€)</label>
          <input id="chargingRate" name="chargingRate" type="number" step="0.05" value={currentChargingRate} required
            class="w-full bg-surface-container-low border border-outline-variant text-on-surface rounded-lg px-4 py-2 focus:ring-2 focus:ring-primary outline-none" />
        </div>
      </div>

      <div class="pt-4 flex justify-end gap-3">
        <a href="/admin/zones" class="px-6 py-2 rounded-full border border-outline-variant text-on-surface-variant font-bold text-sm hover:bg-surface-container-high transition-colors text-center inline-block">
          Cancel
        </a>
        <button type="submit" disabled={isUpdating} class="px-6 py-2 rounded-full bg-primary text-on-primary font-bold text-sm hover:bg-on-primary-fixed transition-colors disabled:opacity-50 flex items-center gap-2 shadow-lg">
          {#if isUpdating}
            <span class="material-symbols-outlined animate-spin text-sm">progress_activity</span>
          {:else}
            <span class="material-symbols-outlined text-sm">save</span>
          {/if}
          Save Changes
        </button>
      </div>
    </form>
  </div>

  <div class="bg-surface-container-lowest p-8 rounded-2xl shadow-xl w-full max-w-2xl border border-surface-container-highest">
    <div class="flex justify-between items-center mb-6">
      <h2 class="text-xl font-bold text-primary">Parking Spaces</h2>
      <span class="text-xs font-bold text-on-surface-variant bg-surface-container px-3 py-1 rounded-full uppercase">
        Total: {zone.spaces?.length || 0}
      </span>
    </div>
    
    <div class="mb-8">
      {#if zone.spaces && zone.spaces.length > 0}
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 max-h-80 overflow-y-auto pr-2">
          {#each zone.spaces as space (space.spaceId)}
            <div class="bg-surface-container-low p-4 rounded-xl border border-outline-variant flex justify-between items-center hover:border-primary/50 transition-colors">
              <div>
                <p class="font-black text-base text-primary">{space.spaceNumber}</p>
                <p class="text-[10px] font-bold text-on-surface-variant uppercase tracking-tighter">Level {space.level}</p>
              </div>
              <div class="flex flex-col items-end gap-1.5">
                <span class="text-[9px] font-black px-2 py-0.5 rounded-md uppercase {space.status === 'FREE' ? 'bg-secondary-container text-on-secondary-container' : 'bg-surface-container-highest text-on-surface-variant'}">
                  {space.status}
                </span>
                
                {#if space.chargingPoint === 'SLOW_CHARGER'}
                  <div class="flex items-center gap-1 text-secondary" title="Slow Charging (AC)">
                    <span class="text-[9px] font-black uppercase">Slow AC</span>
                    <span class="material-symbols-outlined text-[16px]">bolt</span>
                  </div>
                {:else if space.chargingPoint === 'FAST_CHARGER'}
                  <div class="flex items-center gap-1 text-amber-500" title="Fast Charging (DC)">
                    <span class="text-[9px] font-black uppercase">Fast DC</span>
                    <span class="material-symbols-outlined text-[16px]">bolt_speed</span>
                  </div>
                {/if}
              </div>
            </div>
          {/each}
        </div>
      {:else}
        <div class="py-10 text-center bg-surface-container-low rounded-2xl border border-dashed border-outline-variant">
          <span class="material-symbols-outlined text-3xl text-on-surface-variant mb-2">directions_car</span>
          <p class="text-sm text-on-surface-variant italic font-medium">No spaces added to this zone yet.</p>
        </div>
      {/if}
    </div>

    <div class="bg-surface-container p-6 rounded-2xl border border-outline-variant/30">
      <h3 class="text-xs font-black text-primary mb-4 uppercase tracking-widest flex items-center gap-2">
        <span class="material-symbols-outlined text-base">add_circle</span>
        Register New Space
      </h3>
      
      <form method="POST" action="?/addSpace" use:enhance={() => {
        isAddingSpace = true;
        return async ({ update }) => {
          await update();
          isAddingSpace = false;
        };
      }} class="flex flex-col gap-4">
        
        <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
          <div class="flex-1">
            <label for="spaceNumber" class="block text-[10px] font-black text-on-surface-variant mb-1 uppercase">Spot Number</label>
            <input id="spaceNumber" name="spaceNumber" type="text" placeholder="e.g. A-101" required
              class="w-full bg-surface-container-lowest border border-outline-variant text-on-surface rounded-xl px-4 py-2 text-sm focus:ring-2 focus:ring-primary outline-none" />
          </div>
          
          <div>
            <label for="level" class="block text-[10px] font-black text-on-surface-variant mb-1 uppercase">Floor/Level</label>
            <input id="level" name="level" type="text" value="0" required
              class="w-full bg-surface-container-lowest border border-outline-variant text-on-surface rounded-xl px-4 py-2 text-sm focus:ring-2 focus:ring-primary outline-none" />
          </div>

          <div>
            <label for="chargingPoint" class="block text-[10px] font-black text-on-surface-variant mb-1 uppercase">Charger Type</label>
            <select id="chargingPoint" name="chargingPoint"
              class="w-full bg-surface-container-lowest border border-outline-variant text-on-surface rounded-xl px-4 py-2 text-sm focus:ring-2 focus:ring-primary outline-none">
              <option value="FALSE">None</option>
              <option value="SLOW_CHARGER">Slow (AC)</option>
              <option value="FAST_CHARGER">Fast (DC)</option>
            </select>
          </div>
        </div>

        <button type="submit" disabled={isAddingSpace} class="w-full bg-primary text-on-primary font-bold py-3 rounded-xl hover:bg-on-primary-fixed transition-all disabled:opacity-50 flex items-center justify-center gap-2 shadow-md">
          {#if isAddingSpace}
            <span class="material-symbols-outlined animate-spin text-[20px]">progress_activity</span>
          {:else}
            <span class="material-symbols-outlined">add_box</span>
            Register Space
          {/if}
        </button>
      </form>
    </div>
  </div>

  <div class="bg-surface-container-lowest p-8 rounded-2xl shadow-xl w-full max-w-2xl border border-error/20">
    <h3 class="text-error font-black text-xs mb-4 uppercase tracking-widest flex items-center gap-2">
      <span class="material-symbols-outlined text-base">warning</span>
      Danger Zone
    </h3>
    <div class="flex flex-col sm:flex-row items-center justify-between bg-error-container/10 p-5 rounded-2xl border border-error/20 gap-4">
      <div class="text-center sm:text-left">
        <p class="font-bold text-on-surface">Delete Zone</p>
        <p class="text-xs text-on-surface-variant mt-1">Removing this zone will also delete all registered spaces and associated data.</p>
      </div>

      <form method="POST" action="?/delete" use:enhance={() => {
        const confirmed = confirm(`PERMANENT DELETE: Are you sure you want to delete ${zone.name}?`);
        if (!confirmed) return ({ update }) => update();
        isDeleting = true;
        return async ({ update }) => {
          await update();
          isDeleting = false;
        };
      }}>
        <button type="submit" disabled={isDeleting} class="px-6 py-2.5 rounded-xl bg-error text-on-error font-bold text-sm hover:bg-error/80 transition-colors disabled:opacity-50 flex items-center gap-2">
          {#if isDeleting}
            <span class="material-symbols-outlined animate-spin text-sm">progress_activity</span>
          {:else}
            <span class="material-symbols-outlined text-sm">delete_forever</span>
          {/if}
          Delete
        </button>
      </form>
    </div>
  </div>

</div>

<style>
  :global(.material-symbols-outlined) {
    font-variation-settings: 'FILL' 0, 'wght' 600, 'GRAD' 0, 'opsz' 24;
  }

  /* Custom Scrollbar voor de spaces lijst */
  .max-h-80::-webkit-scrollbar {
    width: 6px;
  }
  .max-h-80::-webkit-scrollbar-track {
    background: transparent;
  }
  .max-h-80::-webkit-scrollbar-thumb {
    background: #e1e2e6;
    border-radius: 10px;
  }
</style>
<script lang="ts">
  import { enhance } from '$app/forms';
  import type { ActionData, PageData } from './$types';

  let { data, form } = $props<{ data: PageData; form: ActionData }>();
  let zone = data.zone;

  let isUpdating = $state(false);
  let isDeleting = $state(false);
  let isAddingSpace = $state(false);

  let currentHourlyRate = (zone.pricingPolicy?.hourlyRateCents || 0) / 100;
  let currentChargingRate = (zone.pricingPolicy?.chargingRatePerKwhCents || 0) / 100;
</script>

<div class="min-h-screen bg-surface flex flex-col items-center p-6 space-y-6">
  
  <div class="bg-surface-container-lowest p-8 rounded-2xl shadow-xl w-full max-w-2xl border border-surface-container-highest">
    <div class="mb-8 flex justify-between items-start">
      <div>
        <h1 class="text-2xl font-bold text-primary mb-2">Edit Zone</h1>
        <p class="text-on-surface-variant text-sm">Update the details for <strong>{zone.name}</strong>.</p>
      </div>
      <span class="bg-surface-container-high px-3 py-1 rounded-lg text-xs font-bold text-on-surface">ID: {zone.id}</span>
    </div>

    {#if form?.error}
      <div class="bg-error-container text-on-error-container p-4 rounded-lg mb-6 text-sm font-semibold flex items-center gap-2">
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
        <button type="submit" disabled={isUpdating} class="px-6 py-2 rounded-full bg-primary text-on-primary font-bold text-sm hover:bg-on-primary-fixed transition-colors disabled:opacity-50 flex items-center gap-2">
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
    <h2 class="text-xl font-bold text-primary mb-4">Parking Spaces</h2>
    
    <div class="mb-6">
      {#if zone.spaces && zone.spaces.length > 0}
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-3 max-h-64 overflow-y-auto pr-2">
          {#each zone.spaces as space}
            <div class="bg-surface-container-low p-3 rounded-xl border border-outline-variant flex justify-between items-center">
              <div>
                <p class="font-bold text-sm text-on-surface">{space.spaceNumber}</p>
                <p class="text-xs text-on-surface-variant">Level {space.level}</p>
              </div>
              <div class="flex flex-col items-end gap-1">
                <span class="text-[10px] font-bold px-2 py-0.5 rounded-full {space.status === 'FREE' ? 'bg-secondary-container text-on-secondary-container' : 'bg-surface-container-highest text-on-surface'}">
                  {space.status}
                </span>
                {#if space.chargingPoint === 'TRUE' || space.hasChargingPoint === 'TRUE'}
                  <span class="material-symbols-outlined text-[14px] text-secondary">bolt</span>
                {/if}
              </div>
            </div>
          {/each}
        </div>
      {:else}
        <p class="text-sm text-on-surface-variant italic">No spaces added to this zone yet.</p>
      {/if}
    </div>

    <div class="bg-surface-container p-5 rounded-xl border border-outline-variant/50">
      <h3 class="text-sm font-bold text-on-surface mb-3 uppercase tracking-wide">Add New Space</h3>
      
      <form method="POST" action="?/addSpace" use:enhance={() => {
        isAddingSpace = true;
        return async ({ update }) => {
          await update(); // Reset formulier na toevoegen
          isAddingSpace = false;
        };
      }} class="flex flex-col sm:flex-row gap-3 items-end">
        
        <div class="flex-1 w-full">
          <label for="spaceNumber" class="block text-xs font-bold text-on-surface-variant mb-1">Number (e.g. A1)</label>
          <input id="spaceNumber" name="spaceNumber" type="text" required
            class="w-full bg-surface-container-lowest border border-outline-variant text-on-surface rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-primary outline-none" />
        </div>
        
        <div class="w-full sm:w-24">
          <label for="level" class="block text-xs font-bold text-on-surface-variant mb-1">Level</label>
          <input id="level" name="level" type="text" value="0" required
            class="w-full bg-surface-container-lowest border border-outline-variant text-on-surface rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-primary outline-none" />
        </div>

        <div class="w-full sm:w-32">
          <label for="chargingPoint" class="block text-xs font-bold text-on-surface-variant mb-1">Charging</label>
          <select id="chargingPoint" name="chargingPoint"
            class="w-full bg-surface-container-lowest border border-outline-variant text-on-surface rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-primary outline-none">
            <option value="FALSE">No</option>
            <option value="TRUE">Yes</option>
          </select>
        </div>

        <button type="submit" disabled={isAddingSpace} class="w-full sm:w-auto px-4 py-2 rounded-lg bg-secondary text-on-secondary font-bold text-sm hover:bg-secondary-container hover:text-on-secondary-container transition-colors disabled:opacity-50 h-[38px] flex items-center justify-center">
          {#if isAddingSpace}
            <span class="material-symbols-outlined animate-spin text-[18px]">progress_activity</span>
          {:else}
            Add
          {/if}
        </button>
      </form>
    </div>
  </div>

  <div class="bg-surface-container-lowest p-8 rounded-2xl shadow-xl w-full max-w-2xl border border-error/30">
    <h3 class="text-error font-bold text-sm mb-2 uppercase tracking-wide">Danger Zone</h3>
    <div class="flex items-center justify-between bg-error-container/20 p-4 rounded-xl border border-error/30">
      <div class="text-sm text-on-surface-variant">
        <p class="font-bold text-on-surface">Delete this zone</p>
        <p class="text-xs">Once you delete a zone, there is no going back. Please be certain.</p>
      </div>

      <form method="POST" action="?/delete" use:enhance={() => {
        const confirmed = confirm(`Are you sure you want to delete ${zone.name}? This cannot be undone.`);
        if (!confirmed) return ({ update }) => update();

        isDeleting = true;
        return async ({ update }) => {
          await update();
          isDeleting = false;
        };
      }}>
        <button type="submit" disabled={isDeleting} class="px-4 py-2 rounded-lg bg-error text-on-error font-bold text-sm hover:bg-error/80 transition-colors disabled:opacity-50 flex items-center gap-2">
          {#if isDeleting}
            <span class="material-symbols-outlined animate-spin text-sm">progress_activity</span>
          {:else}
            <span class="material-symbols-outlined text-sm">delete</span>
          {/if}
          Delete Zone
        </button>
      </form>
    </div>
  </div>

</div>
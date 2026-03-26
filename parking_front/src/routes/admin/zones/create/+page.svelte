<script lang="ts">
	import { enhance } from '$app/forms';
	import type { ActionData } from './$types';

	// Svelte 5 way to capture form statuses from server.ts
	let { form } = $props<{ form: ActionData }>();
	let isLoading = $state(false);
</script>

<div class="min-h-screen bg-surface flex items-center justify-center p-6">
	<div class="bg-surface-container-lowest p-8 rounded-2xl shadow-xl w-full max-w-2xl border border-surface-container-highest">

		<div class="mb-8">
			<h1 class="text-2xl font-bold text-primary mb-2">Create New Zone</h1>
			<p class="text-on-surface-variant text-sm">Add a new parking zone (via SvelteKit Actions).</p>
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

		<form method="POST" use:enhance={() => {
      isLoading = true;
      return async ({ update }) => {
        await update({ reset: form?.success }); // Reset form only if successful
        isLoading = false;
      };
    }} class="space-y-6">

			<div class="grid grid-cols-1 md:grid-cols-2 gap-4">
				<div>
					<label for="name" class="block text-xs font-bold text-on-surface-variant mb-1 uppercase tracking-wide">Zone Name</label>
					<input id="name" name="name" type="text" required
					       class="w-full bg-surface-container-low border border-outline-variant text-on-surface rounded-lg px-4 py-2 focus:ring-2 focus:ring-primary outline-none" />
				</div>
				<div>
					<label for="city" class="block text-xs font-bold text-on-surface-variant mb-1 uppercase tracking-wide">City</label>
					<input id="city" name="city" type="text" value="Dortmund" required
					       class="w-full bg-surface-container-low border border-outline-variant text-on-surface rounded-lg px-4 py-2 focus:ring-2 focus:ring-primary outline-none" />
				</div>
				<div class="md:col-span-2">
					<label for="address" class="block text-xs font-bold text-on-surface-variant mb-1 uppercase tracking-wide">Address</label>
					<input id="address" name="address" type="text" required
					       class="w-full bg-surface-container-low border border-outline-variant text-on-surface rounded-lg px-4 py-2 focus:ring-2 focus:ring-primary outline-none" />
				</div>
			</div>

			<hr class="border-surface-container-highest my-6" />

			<div class="grid grid-cols-1 md:grid-cols-2 gap-4">
				<div>
					<label for="latitude" class="block text-xs font-bold text-on-surface-variant mb-1 uppercase tracking-wide">Latitude</label>
					<input id="latitude" name="latitude" type="number" step="0.000001" value="51.5140" required
					       class="w-full bg-surface-container-low border border-outline-variant text-on-surface rounded-lg px-4 py-2 focus:ring-2 focus:ring-primary outline-none" />
				</div>
				<div>
					<label for="longitude" class="block text-xs font-bold text-on-surface-variant mb-1 uppercase tracking-wide">Longitude</label>
					<input id="longitude" name="longitude" type="number" step="0.000001" value="7.4650" required
					       class="w-full bg-surface-container-low border border-outline-variant text-on-surface rounded-lg px-4 py-2 focus:ring-2 focus:ring-primary outline-none" />
				</div>
				<div>
					<label for="hourlyRate" class="block text-xs font-bold text-on-surface-variant mb-1 uppercase tracking-wide">Uurtarief (€)</label>
					<input id="hourlyRate" name="hourlyRate" type="number" step="0.10" value="2.50" required
					       class="w-full bg-surface-container-low border border-outline-variant text-on-surface rounded-lg px-4 py-2 focus:ring-2 focus:ring-primary outline-none" />
				</div>
				<div>
					<label for="chargingRate" class="block text-xs font-bold text-on-surface-variant mb-1 uppercase tracking-wide">Laadtarief per kWh (€)</label>
					<input id="chargingRate" name="chargingRate" type="number" step="0.05" value="0.25" required
					       class="w-full bg-surface-container-low border border-outline-variant text-on-surface rounded-lg px-4 py-2 focus:ring-2 focus:ring-primary outline-none" />
				</div>
			</div>

			<div class="pt-4 flex justify-end gap-3">
				<button type="submit" disabled={isLoading} class="px-6 py-2 rounded-full bg-primary text-on-primary font-bold text-sm hover:bg-on-primary-fixed transition-colors disabled:opacity-50 flex items-center gap-2">
					{#if isLoading}
						<span class="material-symbols-outlined animate-spin text-sm">progress_activity</span>
					{:else}
						<span class="material-symbols-outlined text-sm">save</span>
					{/if}
					Save
				</button>
			</div>

		</form>
	</div>
</div>

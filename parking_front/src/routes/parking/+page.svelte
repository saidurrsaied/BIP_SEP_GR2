<script lang="ts">
  import TopNavBar from '$lib/components/TopNavBar.svelte';
  import BottomNavBar from '$lib/components/BottomNavBar.svelte';
  import type { Reservation } from '$lib/types';

  let activeReservations: Reservation[] = $state([
    {
      id: 1,
      userId: 1,
      spaceId: 1,
      zoneId: 1,
      status: 'CONFIRMED',
      reservedFrom: '2025-03-25T14:00:00Z',
      reservedUntil: '2025-03-25T18:00:00Z',
      createdAt: '2025-03-25T10:30:00Z',
    },
  ]);

  function formatTime(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit',
      hour12: true,
    });
  }

  function formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      weekday: 'short',
      month: 'short',
      day: 'numeric',
    });
  }

  function calculateTimeRemaining(endTime: string): string {
    const now = new Date();
    const end = new Date(endTime);
    const diff = end.getTime() - now.getTime();

    if (diff < 0) return 'Expired';

    const hours = Math.floor(diff / (1000 * 60 * 60));
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));

    if (hours > 0) {
      return `${hours}h ${minutes}m remaining`;
    }
    return `${minutes}m remaining`;
  }
</script>

<TopNavBar />

<main class="pt-16 pb-20 min-h-screen bg-surface">
  <div class="max-w-2xl mx-auto px-4 py-8">
    <h1 class="text-3xl font-bold text-on-surface mb-2">My Parking</h1>
    <p class="text-on-surface-variant mb-8">Manage your active reservations and parking sessions</p>

    {#if activeReservations.length === 0}
      <div class="bg-surface-container-lowest p-12 rounded-2xl shadow-sm text-center">
        <span class="material-symbols-outlined text-6xl text-on-surface-variant mb-4">directions_car</span>
        <h2 class="text-xl font-bold text-on-surface mb-2">No Active Reservations</h2>
        <p class="text-on-surface-variant mb-6">
          You don't have any active parking reservations at the moment
        </p>
        <a
          href="/"
          class="inline-block bg-secondary text-on-secondary px-6 py-2 rounded-lg font-semibold hover:opacity-90 transition-opacity"
        >
          Find a Parking Space
        </a>
      </div>
    {:else}
      <div class="space-y-6">
        {#each activeReservations as reservation (reservation.id)}
          <div class="bg-surface-container-lowest p-6 rounded-2xl shadow-sm">
            <!-- Status Header -->
            <div class="flex items-center justify-between mb-6">
              <div>
                <h2 class="text-2xl font-bold text-on-surface">Zone {reservation.zoneId}</h2>
                <p class="text-on-surface-variant">Space {reservation.spaceId}</p>
              </div>
              <span class="px-4 py-2 rounded-full bg-secondary-container text-on-secondary-container font-bold">
                {reservation.status}
              </span>
            </div>

            <!-- Time Information -->
            <div class="grid grid-cols-3 gap-4 mb-6 p-4 bg-surface-container rounded-lg">
              <div>
                <p class="text-on-surface-variant text-xs uppercase tracking-widest mb-1">From</p>
                <p class="text-lg font-bold text-on-surface">{formatTime(reservation.reservedFrom)}</p>
                <p class="text-xs text-on-surface-variant">{formatDate(reservation.reservedFrom)}</p>
              </div>
              <div class="flex items-end justify-center pb-2">
                <span class="material-symbols-outlined text-on-surface-variant">arrow_right_alt</span>
              </div>
              <div>
                <p class="text-on-surface-variant text-xs uppercase tracking-widest mb-1">To</p>
                <p class="text-lg font-bold text-on-surface">{formatTime(reservation.reservedUntil)}</p>
                <p class="text-xs text-on-surface-variant">{formatDate(reservation.reservedUntil)}</p>
              </div>
            </div>

            <!-- Time Remaining -->
            <div class="mb-6 p-4 bg-secondary-container/20 rounded-lg border border-secondary-container">
              <p class="text-on-surface-variant text-sm mb-1">Time Remaining</p>
              <p class="text-2xl font-bold text-secondary">{calculateTimeRemaining(reservation.reservedUntil)}</p>
            </div>

            <!-- Actions -->
            <div class="flex gap-3">
              <button
                class="flex-1 bg-secondary text-on-secondary py-3 rounded-lg font-semibold hover:opacity-90 transition-opacity flex items-center justify-center gap-2"
              >
                <span class="material-symbols-outlined">directions</span>
                Get Directions
              </button>
              <button
                class="flex-1 bg-surface-container text-on-surface py-3 rounded-lg font-semibold hover:opacity-70 transition-opacity flex items-center justify-center gap-2"
              >
                <span class="material-symbols-outlined">edit</span>
                Extend Time
              </button>
              <button
                class="px-4 bg-error-container text-on-error-container py-3 rounded-lg font-semibold hover:opacity-70 transition-opacity flex items-center justify-center"
              >
                <span class="material-symbols-outlined">close</span>
              </button>
            </div>
          </div>
        {/each}
      </div>
    {/if}

    <!-- Quick Stats -->
    <div class="grid grid-cols-2 gap-4 mt-12">
      <div class="bg-surface-container-lowest p-6 rounded-2xl shadow-sm">
        <p class="text-on-surface-variant text-sm mb-2">Total Sessions</p>
        <p class="text-3xl font-bold text-primary">24</p>
      </div>
      <div class="bg-surface-container-lowest p-6 rounded-2xl shadow-sm">
        <p class="text-on-surface-variant text-sm mb-2">Total Spent</p>
        <p class="text-3xl font-bold text-primary">€145.50</p>
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

<script lang="ts">
  import TopNavBar from '$lib/components/TopNavBar.svelte';
  import BottomNavBar from '$lib/components/BottomNavBar.svelte';
  import type { Reservation, Invoice } from '$lib/types';

  let reservations: Reservation[] = $state([
    {
      id: 1,
      userId: 1,
      spaceId: 1,
      zoneId: 1,
      status: 'COMPLETED',
      reservedFrom: '2025-03-20T10:00:00Z',
      reservedUntil: '2025-03-20T12:30:00Z',
      createdAt: '2025-03-20T09:45:00Z',
    },
    {
      id: 2,
      userId: 1,
      spaceId: 4,
      zoneId: 2,
      status: 'COMPLETED',
      reservedFrom: '2025-03-18T14:00:00Z',
      reservedUntil: '2025-03-18T16:15:00Z',
      createdAt: '2025-03-18T13:30:00Z',
    },
  ]);

  let invoices: Invoice[] = $state([
    {
      id: 1,
      userId: 1,
      reservationId: 1,
      items: [
        {
          id: 101,
          type: 'PARKING',
          description: 'Parking at Central Plaza - 2.5 hours',
          amountCents: 1125,
          durationMinutes: 150,
        },
      ],
      status: 'PAID',
      totalAmountCents: 1125,
      currency: 'EUR',
      createdAt: '2025-03-20T12:30:00Z',
      paidAt: '2025-03-20T12:31:00Z',
    },
    {
      id: 2,
      userId: 1,
      reservationId: 2,
      items: [
        {
          id: 102,
          type: 'PARKING',
          description: 'Parking at Green Street - 2.25 hours',
          amountCents: 675,
          durationMinutes: 135,
        },
      ],
      status: 'PAID',
      totalAmountCents: 675,
      currency: 'EUR',
      createdAt: '2025-03-18T16:15:00Z',
      paidAt: '2025-03-18T16:16:00Z',
    },
  ]);

  function formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  function formatPrice(cents: number): string {
    return (cents / 100).toFixed(2);
  }

  function getStatusColor(status: string): string {
    switch (status) {
      case 'COMPLETED':
        return 'text-secondary bg-secondary-container/20';
      case 'PENDING':
        return 'text-primary bg-primary-fixed/20';
      case 'PAID':
        return 'text-secondary bg-secondary-container/20';
      default:
        return 'text-on-surface-variant';
    }
  }
</script>

<TopNavBar />

<main class="pt-16 pb-20 min-h-screen bg-surface">
  <div class="max-w-4xl mx-auto px-4 py-8">
    <h1 class="text-3xl font-bold text-on-surface mb-2">Parking History</h1>
    <p class="text-on-surface-variant mb-8">Your past and current parking sessions</p>

    <!-- Tabs -->
    <div class="flex gap-4 border-b border-surface-container mb-8">
      <button class="px-4 py-3 font-semibold text-secondary border-b-2 border-secondary">
        Reservations
      </button>
      <button class="px-4 py-3 font-semibold text-on-surface-variant hover:text-on-surface transition-colors">
        Invoices
      </button>
    </div>

    <!-- Reservations List -->
    <div class="space-y-4">
      {#each reservations as reservation (reservation.id)}
        <div class="bg-surface-container-lowest p-6 rounded-2xl shadow-sm hover:shadow-md transition-shadow">
          <div class="flex items-start justify-between mb-4">
            <div>
              <h3 class="text-lg font-bold text-primary">
                {reservation.status === 'COMPLETED' ? 'Parking Session' : 'Active Reservation'}
              </h3>
              <p class="text-on-surface-variant text-sm">Zone {reservation.zoneId} • Space {reservation.spaceId}</p>
            </div>
            <span class="px-3 py-1 rounded-full text-xs font-bold {getStatusColor(reservation.status)}">
              {reservation.status}
            </span>
          </div>

          <div class="space-y-2 mb-4">
            <div class="flex items-center justify-between text-sm">
              <span class="text-on-surface-variant">From:</span>
              <span class="font-medium text-on-surface">{formatDate(reservation.reservedFrom)}</span>
            </div>
            <div class="flex items-center justify-between text-sm">
              <span class="text-on-surface-variant">Until:</span>
              <span class="font-medium text-on-surface">{formatDate(reservation.reservedUntil)}</span>
            </div>
          </div>

          <div class="border-t border-surface-container pt-4">
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm text-on-surface-variant")>Associated Invoice</p>
                <p class="text-lg font-bold text-primary">Invoice #{reservation.id}</p>
              </div>
              <button class="text-secondary font-semibold hover:opacity-70 transition-opacity flex items-center gap-1">
                View Details
                <span class="material-symbols-outlined text-lg">arrow_forward</span>
              </button>
            </div>
          </div>
        </div>
      {/each}
    </div>

    <!-- Divider -->
    <div class="border-t border-surface-container my-12"></div>

    <!-- Invoices Section -->
    <h2 class="text-2xl font-bold text-on-surface mb-6">Recent Invoices</h2>
    <div class="space-y-4">
      {#each invoices as invoice (invoice.id)}
        <div class="bg-surface-container-lowest p-6 rounded-2xl shadow-sm hover:shadow-md transition-shadow">
          <div class="flex items-start justify-between mb-4">
            <div>
              <h3 class="text-lg font-bold text-primary">Invoice #{invoice.id}</h3>
              <p class="text-on-surface-variant text-sm">{formatDate(invoice.createdAt)}</p>
            </div>
            <span class="text-2xl font-bold text-primary">
              €{formatPrice(invoice.totalAmountCents)}
            </span>
          </div>

          <div class="space-y-2 mb-4">
            {#each invoice.items as item (item.id)}
              <div class="flex items-center justify-between text-sm">
                <div>
                  <p class="font-medium text-on-surface">{item.description}</p>
                  {#if item.durationMinutes}
                    <p class="text-on-surface-variant text-xs">{item.durationMinutes} minutes</p>
                  {/if}
                </div>
                <span class="font-semibold text-on-surface">€{formatPrice(item.amountCents)}</span>
              </div>
            {/each}
          </div>

          <div class="border-t border-surface-container pt-4">
            <div class="flex items-center justify-between">
              <span class={`px-3 py-1 rounded-full text-xs font-bold ${getStatusColor(invoice.status)}`}>
                {invoice.status}
              </span>
              <button class="text-secondary font-semibold hover:opacity-70 transition-opacity flex items-center gap-1">
                Download Receipt
                <span class="material-symbols-outlined text-lg">download</span>
              </button>
            </div>
          </div>
        </div>
      {/each}
    </div>
  </div>
</main>

<BottomNavBar />

<style>
  :global(.material-symbols-outlined) {
    font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  }
</style>

<script lang="ts">
  import TopNavBar from '$lib/components/TopNavBar.svelte';
  import BottomNavBar from '$lib/components/BottomNavBar.svelte';
  import { fade, fly } from 'svelte/transition';
  import type { PageData } from './$types';

  let { data }: { data: PageData } = $props();
  
  // Gebruik $derived voor een veilige, reactieve referentie naar de zone data
  let zone = $derived(data.zone);

  // Helper: Prijzen netjes formatteren
  function formatPrice(cents: number): string {
    return (cents / 100).toFixed(2);
  }

  // Helper: Status configuratie voor de badges
  function getStatusInfo(status: string) {
    const configs = {
      'FREE': { color: 'text-secondary bg-secondary-container', icon: 'check_circle', label: 'Beschikbaar' },
      'RESERVED': { color: 'text-primary bg-primary-container', icon: 'schedule', label: 'Gereserveerd' },
      'OCCUPIED': { color: 'text-error bg-error-container', icon: 'cancel', label: 'Bezet' }
    };
    return configs[status as keyof typeof configs] || { color: 'bg-surface-container-high', icon: 'help', label: status };
  }

  // Helper: Tel beschikbare plekken reactief
  let availableSpaces = $derived((zone?.spaces || []).filter(s => s.status === 'FREE').length);
</script>

<TopNavBar />

{#if zone}
  <main class="pt-16 pb-32 min-h-screen bg-surface-container-low" in:fade>
    

    <div class="max-w-2xl mx-auto px-6 -mt-6 relative z-10">
      
      <div class="grid grid-cols-3 gap-3 mb-8">
        <div class="bg-surface-container-lowest p-4 rounded-[2rem] shadow-sm border border-outline-variant/30 flex flex-col items-center justify-center text-center h-28">
          <p class="text-[9px] font-black text-on-surface-variant uppercase mb-2 tracking-widest">Spots</p>
          <p class="text-2xl font-black text-secondary">{availableSpaces}</p>
          <p class="text-[8px] font-bold text-on-surface-variant/60 uppercase">Vrij</p>
        </div>
        <div class="bg-surface-container-lowest p-4 rounded-[2rem] shadow-sm border border-outline-variant/30 flex flex-col items-center justify-center text-center h-28">
          <p class="text-[9px] font-black text-on-surface-variant uppercase mb-2 tracking-widest">Tarief</p>
          <p class="text-2xl font-black text-primary">€{formatPrice(zone.pricingPolicy?.hourlyRateCents)}</p>
          <p class="text-[8px] font-bold text-on-surface-variant/60 uppercase">P/Uur</p>
        </div>
        <div class="bg-surface-container-lowest p-4 rounded-[2rem] shadow-sm border border-outline-variant/30 flex flex-col items-center justify-center text-center h-28">
          <p class="text-[9px] font-black text-on-surface-variant uppercase mb-2 tracking-widest">EV</p>
          <div class="w-10 h-10 bg-secondary-container rounded-full flex items-center justify-center">
            <span class="material-symbols-outlined text-on-secondary-container text-xl">bolt</span>
          </div>
        </div>
      </div>

      <section class="mb-10">
        <h2 class="text-xs font-black text-primary uppercase tracking-widest mb-4 px-2">Prijsinformatie</h2>
        <div class="bg-surface-container-lowest rounded-[2.5rem] p-8 border border-outline-variant/30 shadow-sm space-y-6">
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <span class="material-symbols-outlined text-on-surface-variant">payments</span>
              <span class="text-sm font-bold text-on-surface">Parkeertarief</span>
            </div>
            <span class="font-black text-primary">€{formatPrice(zone.pricingPolicy?.hourlyRateCents)} <small class="text-[10px] font-normal opacity-60">/u</small></span>
          </div>
          
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <span class="material-symbols-outlined text-secondary">ev_station</span>
              <span class="text-sm font-bold text-on-surface">Laadtarief</span>
            </div>
            <span class="font-black text-secondary">€{formatPrice(zone.pricingPolicy?.chargingRatePerKwhCents)} <small class="text-[10px] font-normal opacity-60">/kWh</small></span>
          </div>
        </div>
      </section>

      <section class="mb-12">
        <div class="flex justify-between items-center mb-6 px-2">
          <h2 class="text-xs font-black text-primary uppercase tracking-widest">Alle Plekken</h2>
          <span class="text-[10px] font-black bg-surface-container-high px-3 py-1 rounded-full">{zone.spaces?.length || 0} TOTAAL</span>
        </div>

        <div class="space-y-3">
          {#each (zone.spaces || []) as space (space.spaceId)}
            {@const statusInfo = getStatusInfo(space.status)}

            <div class="bg-surface-container-lowest p-5 rounded-3xl border border-outline-variant/20 flex items-center justify-between hover:scale-[1.01] transition-transform">
              <div class="flex items-center gap-5">
                <div class="w-14 h-14 bg-surface-container-high rounded-2xl flex flex-col items-center justify-center border border-outline-variant/30">
                  <span class="text-[8px] font-black text-on-surface-variant uppercase">Spot</span>
                  <span class="text-lg font-black text-primary">{space.spaceNumber}</span>
                </div>
                <div>
                  <p class="font-black text-sm text-on-surface">Verdieping {space.level}</p>
                  <div class="flex gap-2 mt-1">
                    {#if space.chargingPoint === 'FAST_CHARGER'}
                      <span class="flex items-center gap-0.5 text-[9px] font-black text-amber-600 uppercase">
                        <span class="material-symbols-outlined text-[14px]">bolt_speed</span> Fast DC
                      </span>
                    {:else if space.chargingPoint === 'SLOW_CHARGER'}
                      <span class="flex items-center gap-0.5 text-[9px] font-black text-secondary uppercase">
                        <span class="material-symbols-outlined text-[14px]">bolt</span> Slow AC
                      </span>
                    {/if}
                  </div>
                </div>
              </div>

              <div class="flex flex-col items-end gap-2">
                <span class="px-3 py-1.5 rounded-xl text-[9px] font-black uppercase tracking-wider {statusInfo.color} flex items-center gap-1">
                  <span class="material-symbols-outlined text-[12px]">{statusInfo.icon}</span>
                  {statusInfo.label}
                </span>
              </div>
            </div>
          {:else}
            <div class="py-20 text-center opacity-40">
              <span class="material-symbols-outlined text-4xl mb-2">sentiment_dissatisfied</span>
              <p class="text-sm font-bold uppercase tracking-widest">Geen plekken gevonden</p>
            </div>
          {/each}
        </div>
      </section>
    </div>

    {#if availableSpaces > 0}
      <div class="fixed bottom-24 left-1/2 -translate-x-1/2 w-full max-w-md px-6 z-50" in:fly={{ y: 50, delay: 400 }}>
        <a 
          href="/zones/{zone.zoneId}/reserve"
          class="flex items-center justify-center gap-3 w-full bg-primary text-on-primary py-5 rounded-[2rem] font-black text-lg shadow-[0_20px_50px_rgba(0,0,0,0.2)] transition-all hover:-translate-y-1 active:scale-95"
        >
          <span class="material-symbols-outlined">calendar_add_on</span>
          RESERVEREN
        </a>
      </div>
    {/if}

  </main>
{:else}
  <div class="h-screen w-full flex flex-col items-center justify-center bg-surface gap-4">
    <span class="material-symbols-outlined animate-spin text-4xl text-primary">progress_activity</span>
    <p class="text-xs font-black uppercase tracking-widest text-on-surface-variant">Zone laden...</p>
  </div>
{/if}

<BottomNavBar />

<style>
  :global(.material-symbols-outlined) {
    font-variation-settings: 'FILL' 1, 'wght' 500, 'GRAD' 0, 'opsz' 24;
  }
</style>
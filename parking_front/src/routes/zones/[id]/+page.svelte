<script lang="ts">
  import TopNavBar from '$lib/components/TopNavBar.svelte';
  import BottomNavBar from '$lib/components/BottomNavBar.svelte';
  import { fade, fly } from 'svelte/transition';
  import type { PageData } from './$types';

  let { data }: { data: PageData } = $props();
  
  let zone = $derived(data.zone);

  function formatPrice(cents: number): string {
    return (cents / 100).toFixed(2);
  }

  function getStatusInfo(status: string) {
    const configs = {
      'FREE': { color: 'text-secondary bg-secondary-container', icon: 'check_circle', label: 'Beschikbaar' },
      'RESERVED': { color: 'text-primary bg-primary-container', icon: 'schedule', label: 'Gereserveerd' },
      'OCCUPIED': { color: 'text-error bg-error-container', icon: 'cancel', label: 'Bezet' }
    };
    return configs[status as keyof typeof configs] || { color: 'bg-surface-container-high', icon: 'help', label: status };
  }

  let availableSpaces = $derived((zone?.spaces || []).filter(s => s.status === 'FREE').length);

  // NIEUW: Bereken de prijzen op basis van multipliers
  let hourlyRate = $derived(zone?.pricingPolicy?.hourlyRateCents || 0);
  let slowMultiplier = $derived(zone?.pricingPolicy?.slowMultiplier || 2);
  let fastMultiplier = $derived(zone?.pricingPolicy?.fastMultiplier || 4);
</script>

<TopNavBar />

{#if zone}
  <main class="pb-32 min-h-screen bg-surface-container-low" in:fade>
    
    <div class="max-w-2xl mx-auto px-6 pt-20 relative z-10">
      
      <div class="grid grid-cols-3 gap-3 mb-10">
        <div class="bg-surface-container-lowest p-4 rounded-[2.5rem] shadow-xl shadow-black/5 border border-white/20 flex flex-col items-center justify-center text-center h-32 transition-transform hover:scale-[1.02]">
          <span class="material-symbols-outlined text-secondary mb-1">local_parking</span>
          <p class="text-2xl font-black text-on-surface">{availableSpaces}</p>
          <p class="text-[9px] font-bold text-on-surface-variant/60 uppercase tracking-tighter">Spots Vrij</p>
        </div>
        
        <div class="bg-surface-container-lowest p-4 rounded-[2.5rem] shadow-xl shadow-black/5 border border-white/20 flex flex-col items-center justify-center text-center h-32 transition-transform hover:scale-[1.02]">
          <span class="material-symbols-outlined text-primary mb-1">payments</span>
          <p class="text-2xl font-black text-on-surface">€{formatPrice(hourlyRate)}</p>
          <p class="text-[9px] font-bold text-on-surface-variant/60 uppercase tracking-tighter">P/Uur</p>
        </div>

        <div class="bg-surface-container-lowest p-4 rounded-[2.5rem] shadow-xl shadow-black/5 border border-white/20 flex flex-col items-center justify-center text-center h-32 transition-transform hover:scale-[1.02]">
          <span class="material-symbols-outlined text-amber-500 mb-1">bolt</span>
          <p class="text-2xl font-black text-on-surface">€{formatPrice(hourlyRate * slowMultiplier)}</p>
          <p class="text-[9px] font-bold text-on-surface-variant/60 uppercase tracking-tighter">Laden v.a.</p>
        </div>
      </div>

      <section class="mb-10" in:fly={{ y: 20, delay: 200 }}>
        <h2 class="text-[11px] font-black text-primary uppercase tracking-[0.2em] mb-4 px-2 opacity-70">Tarieven & Informatie</h2>
        <div class="bg-surface-container-lowest rounded-[2.5rem] p-8 border border-outline-variant/20 shadow-sm space-y-6">
          
          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <span class="material-symbols-outlined text-on-surface-variant">payments</span>
              <span class="text-sm font-bold text-on-surface">Standaard Parkeren</span>
            </div>
            <span class="font-black text-primary">€{formatPrice(hourlyRate)} <small class="text-[10px] font-normal opacity-60">/u</small></span>
          </div>

          <hr class="border-outline-variant/30" />

          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <span class="material-symbols-outlined text-secondary">bolt</span>
              <span class="text-sm font-bold text-on-surface">Slow Charging (AC)</span>
            </div>
            <div class="flex flex-col items-end">
              <span class="font-black text-secondary">€{formatPrice(hourlyRate * slowMultiplier)} <small class="text-[10px] font-normal opacity-60">/u</small></span>
              <span class="text-[8px] font-bold opacity-40 uppercase">Basistarief x{slowMultiplier}</span>
            </div>
          </div>

          <div class="flex items-center justify-between">
            <div class="flex items-center gap-3">
              <span class="material-symbols-outlined text-amber-600">bolt_speed</span>
              <span class="text-sm font-bold text-on-surface">Fast Charging (DC)</span>
            </div>
            <div class="flex flex-col items-end">
              <span class="font-black text-amber-600">€{formatPrice(hourlyRate * fastMultiplier)} <small class="text-[10px] font-normal opacity-60">/u</small></span>
              <span class="text-[8px] font-bold opacity-40 uppercase">Basistarief x{fastMultiplier}</span>
            </div>
          </div>
        </div>
      </section>

      <section class="mb-12" in:fly={{ y: 20, delay: 400 }}>
        <div class="flex justify-between items-center mb-6 px-4">
          <h2 class="text-[11px] font-black text-primary uppercase tracking-[0.2em] opacity-70">Beschikbaarheid</h2>
          <div class="flex items-center gap-2">
            <span class="w-2 h-2 rounded-full bg-secondary animate-pulse"></span>
            <span class="text-[10px] font-black text-on-surface-variant uppercase">Live Status</span>
          </div>
        </div>

        <div class="space-y-4">
          {#each (zone.spaces || []) as space (space.spaceId)}
            {@const statusInfo = getStatusInfo(space.status)}

            <div class="bg-surface-container-lowest p-5 rounded-[2rem] border border-outline-variant/10 flex items-center justify-between shadow-sm hover:shadow-md transition-all active:scale-[0.98]">
              <div class="flex items-center gap-5">
                <div class="w-16 h-16 bg-surface-container rounded-[1.25rem] flex flex-col items-center justify-center border border-outline-variant/20">
                  <span class="text-[8px] font-black text-on-surface-variant/50 uppercase">Spot</span>
                  <span class="text-xl font-black text-primary">{space.spaceNumber}</span>
                </div>
                
                <div>
                  <p class="font-black text-base text-on-surface">Niveau {space.level}</p>
                  <div class="flex gap-3 mt-1.5">
                    {#if space.chargingPoint === 'FAST_CHARGER'}
                      <span class="flex items-center gap-1 text-[10px] font-black text-amber-600 uppercase bg-amber-50 px-2 py-0.5 rounded-md">
                        <span class="material-symbols-outlined text-sm">bolt_speed</span> Fast DC
                      </span>
                    {:else if space.chargingPoint === 'SLOW_CHARGER'}
                      <span class="flex items-center gap-1 text-[10px] font-black text-secondary uppercase bg-secondary-container/30 px-2 py-0.5 rounded-md">
                        <span class="material-symbols-outlined text-sm">bolt</span> Slow AC
                      </span>
                    {/if}
                  </div>
                </div>
              </div>

              <div class="flex flex-col items-end">
                <span class="px-4 py-2 rounded-2xl text-[10px] font-black uppercase tracking-wider {statusInfo.color} flex items-center gap-2 shadow-sm">
                  <span class="material-symbols-outlined text-base">{statusInfo.icon}</span>
                  {statusInfo.label}
                </span>
              </div>
            </div>
          {:else}
            <div class="py-20 text-center bg-surface-container-lowest rounded-[3rem] border border-dashed border-outline-variant/50">
              <span class="material-symbols-outlined text-5xl text-on-surface-variant/20 mb-3">grid_off</span>
              <p class="text-sm font-bold text-on-surface-variant/40 uppercase tracking-widest">Geen plekken gevonden</p>
            </div>
          {/each}
        </div>
      </section>
    </div>

    {#if availableSpaces > 0}
      <div class="fixed bottom-28 left-1/2 -translate-x-1/2 w-full max-w-md px-8 z-50" in:fly={{ y: 50, delay: 800 }}>
        <a 
          href="/zones/{zone.zoneId}/reserve"
          class="group flex items-center justify-center gap-4 w-full bg-primary text-on-primary py-6 rounded-[2.5rem] font-black text-lg shadow-[0_25px_50px_-12px_rgba(var(--m3-primary-rgb),0.5)] transition-all hover:-translate-y-1 hover:shadow-primary/40 active:scale-95 overflow-hidden relative"
        >
          <div class="absolute inset-0 bg-white/10 translate-x-[-100%] group-hover:translate-x-[100%] transition-transform duration-700 skew-x-[-20deg]"></div>
          
          <span class="material-symbols-outlined text-2xl">calendar_add_on</span>
          <span class="tracking-tight">NU RESERVEREN</span>
        </a>
      </div>
    {/if}

  </main>
{:else}
  <div class="h-screen w-full flex flex-col items-center justify-center bg-surface gap-6">
    <div class="relative flex items-center justify-center">
      <div class="absolute w-16 h-16 border-4 border-primary/20 rounded-full"></div>
      <div class="w-16 h-16 border-4 border-primary border-t-transparent rounded-full animate-spin"></div>
    </div>
    <div class="text-center">
      <p class="text-xs font-black uppercase tracking-[0.4em] text-primary animate-pulse">Laden...</p>
      <p class="text-[10px] text-on-surface-variant/60 font-medium mt-2">Dortmund Smart Parking</p>
    </div>
  </div>
{/if}

<BottomNavBar />

<style>
  :global(.material-symbols-outlined) {
    font-variation-settings: 'FILL' 1, 'wght' 600, 'GRAD' 0, 'opsz' 24;
  }

  :global(:root) {
    --m3-primary-rgb: 103, 80, 164;
  }
</style>
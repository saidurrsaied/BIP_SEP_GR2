<script lang="ts">
  import TopNavBar from '$lib/components/TopNavBar.svelte';
  import BottomNavBar from '$lib/components/BottomNavBar.svelte';
  import type { PageData } from './$types';

  let { data }: { data: PageData } = $props();

  const chargingZones = $derived(
    (data.zones || []).filter(zone => 
      zone.spaces && zone.spaces.some((s: any) => s.chargingPoint !== 'FALSE')
    )
  );

  function formatPrice(cents: number): string {
    return (cents / 100).toFixed(2);
  }

  // NIEUW: Bereken de prijs op basis van de multipliers
  function calculateEVPrice(zone: any): number {
    const policy = zone.pricingPolicy;
    if (!policy) return 0;

    const hasFast = zone.spaces.some((s: any) => s.chargingPoint === 'FAST_CHARGER');
    
    // Pak de juiste multiplier (fallback naar 2 of 4 als ze undefined zijn)
    const multiplier = hasFast ? (policy.fastMultiplier || 4) : (policy.slowMultiplier || 2);
    
    return policy.hourlyRateCents * multiplier;
  }

  function getAvailableLaders(zone: any): number {
    return (zone.spaces || []).filter((s: any) => 
      s.chargingPoint !== 'FALSE' && s.status === 'FREE'
    ).length;
  }

  function hasFastCharger(zone: any): boolean {
    return (zone.spaces || []).some((s: any) => s.chargingPoint === 'FAST_CHARGER');
  }
</script>

<TopNavBar />

<main class="pt-16 pb-20 min-h-screen bg-surface">
  <div class="max-w-2xl mx-auto px-4 py-8">
    <div class="flex items-center gap-3 mb-6">
      <div class="w-12 h-12 bg-secondary-container rounded-2xl flex items-center justify-center">
        <span class="material-symbols-outlined text-on-secondary-container text-2xl">bolt</span>
      </div>
      <div>
        <h1 class="text-3xl font-bold text-on-surface">EV Charging</h1>
        <p class="text-on-surface-variant text-sm">Prijzen gebaseerd op uurtarief x multiplier</p>
      </div>
    </div>

    {#if chargingZones.length === 0}
      {:else}
      <div class="grid gap-6 md:grid-cols-2">
        {#each chargingZones as zone (zone.zoneId)}
          {@const availableCount = getAvailableLaders(zone)}
          {@const fast = hasFastCharger(zone)}
          {@const evPrice = calculateEVPrice(zone)}
          
          <div class="bg-surface-container-lowest p-6 rounded-3xl shadow-sm border border-surface-container-high hover:shadow-md transition-all group">
            <div class="flex items-start justify-between mb-4">
              <div>
                <h3 class="text-xl font-bold text-primary group-hover:text-secondary transition-colors">{zone.name}</h3>
                <p class="text-[10px] font-black text-on-surface-variant uppercase mt-1">ID: {zone.zoneId.slice(0, 8)}</p>
              </div>
              <span class="px-3 py-1 rounded-full text-[10px] font-black tracking-wider {availableCount > 0 ? 'bg-secondary-container text-on-secondary-container' : 'bg-error-container text-on-error-container'}">
                {availableCount > 0 ? 'AVAILABLE' : 'FULL'}
              </span>
            </div>

            <div class="space-y-3 mb-6">
              <div class="flex items-center gap-3 text-on-surface-variant">
                <span class="material-symbols-outlined text-lg">location_on</span>
                <span class="text-sm font-medium">{zone.address}, {zone.city}</span>
              </div>
              
              <div class="flex items-center gap-3 text-on-surface-variant">
                <span class="material-symbols-outlined text-lg {fast ? 'text-amber-500' : 'text-secondary'}">
                  {fast ? 'bolt_speed' : 'bolt'}
                </span>
                <span class="text-sm">
                  <strong class="text-on-surface">{availableCount}</strong> chargers free 
                  <span class="text-xs opacity-60">({fast ? 'Fast DC' : 'Slow AC'})</span>
                </span>
              </div>

              <div class="flex items-center gap-3 text-on-surface-variant">
                <span class="material-symbols-outlined text-lg">payments</span>
                <span class="text-sm">
                  <strong class="text-primary">€{formatPrice(evPrice)}</strong> per uur
                  <span class="text-[10px] opacity-60 ml-1">
                    ({fast ? 'x' + (zone.pricingPolicy.fastMultiplier || 4) : 'x' + (zone.pricingPolicy.slowMultiplier || 2)})
                  </span>
                </span>
              </div>
            </div>

            <div class="border-t border-surface-container-high pt-5">
              <a href="/zones/{zone.zoneId}/reserve" class="block w-full text-center bg-primary text-on-primary py-3 rounded-xl font-bold text-sm hover:opacity-90 shadow-sm">
                Reserveer Nu
              </a>
            </div>
          </div>
        {/each}
      </div>
    {/if}
  </div>
</main>

<BottomNavBar />
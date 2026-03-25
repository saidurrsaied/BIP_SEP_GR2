<script lang="ts">
  import { onMount } from 'svelte';

  let searchQuery = $state('');

  onMount(() => {
    // Initialize any necessary state
  });
</script>

<!-- TopNavBar -->
<header
  class="fixed top-0 w-full z-50 bg-slate-50/70 dark:bg-slate-950/70 backdrop-blur-xl shadow-sm h-16 flex items-center justify-between px-6 font-sans text-sm tracking-tight"
>
  <div class="flex items-center gap-8">
    <span class="text-xl font-black tracking-tighter text-blue-900 dark:text-blue-50">D-Parking</span>
    <nav class="hidden md:flex items-center gap-6">
      <a
        class="text-slate-500 dark:text-slate-400 font-medium hover:text-blue-700 transition-colors"
        href="/"
      >
        Map
      </a>
      <a
        class="text-slate-500 dark:text-slate-400 font-medium hover:text-blue-700 transition-colors"
        href="/charging"
      >
        Charging
      </a>
      <a
        class="text-slate-500 dark:text-slate-400 font-medium hover:text-blue-700 transition-colors"
        href="/history"
      >
        History
      </a>
    </nav>
  </div>
  <div class="flex items-center gap-4">
    <button class="text-on-surface-variant hover:text-primary transition-colors">
      <span class="material-symbols-outlined">notifications</span>
    </button>
    <div
      class="h-8 w-8 rounded-full overflow-hidden bg-surface-container ml-2 cursor-pointer active:opacity-80 transition-all border border-outline-variant"
    >
      <img
        alt="User profile and settings"
        src="https://lh3.googleusercontent.com/aida-public/AB6AXuCcBDQC_AJ3U4R4549fBbiwNPGTBx8idzhv3V17Ty3cezQ_eADI_iiy60hFMZ7UO4LG5RthyXgmOEOXI87ggmtVOcXG6q9PZBUVAXUBOp8YEz_cTdK1_OS2hTqDbTkvjJRf8Qbty2ImjCOxAa1ugE1GcVsIEO1494yaDZ0bh7Zyi4-oPbGzoVvj5jBsk4A2jEGxuBYYlkwnWDFrlaLAm0Mz_9I7_R_jtmdBn8RL6m3Xbx6zk3HJxMPzoDnd26iacRaL2gJA_2NNkQZB"
      />
    </div>
  </div>
</header>

<style>
  :global(.material-symbols-outlined) {
    font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  }
</style>

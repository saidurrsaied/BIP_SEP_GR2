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
  <form action="/auth/logout" method="POST" class="inline">
      <button type="submit" 
          class="rounded-md bg-red-100 px-3 py-2 text-sm font-medium text-red-700 hover:bg-red-200 transition-colors">
          Uitloggen
      </button>
  </form>
</header>

<style>
  :global(.material-symbols-outlined) {
    font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  }
</style>

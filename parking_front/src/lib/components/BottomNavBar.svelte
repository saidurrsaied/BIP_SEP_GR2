<script lang="ts">
  import { page } from '$app/stores';

  function isActive(path: string): boolean {
    return $page.url.pathname === path;
  }
</script>

<!-- BottomNavBar (Visible on Mobile) -->
<footer class="md:hidden fixed bottom-0 left-0 w-full h-20 flex justify-around items-center px-6 pb-safe bg-white/80 dark:bg-slate-950/80 backdrop-blur-lg border-t border-slate-100 dark:border-slate-800 shadow-[0_-4px_20px_rgba(0,0,0,0.05)] z-50">
  <a
    class="flex flex-col items-center justify-center transition-transform duration-300"
    class:active={isActive('/')}
    href="/"
  >
    <span class="material-symbols-outlined" class:fill={isActive('/')}>map</span>
    <span class="font-sans text-[10px] font-bold uppercase tracking-widest mt-0.5">Explore</span>
  </a>
  <a
    class="flex flex-col items-center justify-center text-slate-400 dark:text-slate-500 active:bg-slate-100 transition-transform duration-300"
    href="/charging"
  >
    <span class="material-symbols-outlined">bolt</span>
    <span class="font-sans text-[10px] font-bold uppercase tracking-widest mt-0.5">Charging</span>
  </a>
  <a
    class="flex flex-col items-center justify-center text-slate-400 dark:text-slate-500 active:bg-slate-100 transition-transform duration-300"
    href="/parking"
  >
    <span class="material-symbols-outlined">directions_car</span>
    <span class="font-sans text-[10px] font-bold uppercase tracking-widest mt-0.5">My Park</span>
  </a>
  <a
    class="flex flex-col items-center justify-center text-slate-400 dark:text-slate-500 active:bg-slate-100 transition-transform duration-300"
    href="/profile"
  >
    <span class="material-symbols-outlined">person</span>
    <span class="font-sans text-[10px] font-bold uppercase tracking-widest mt-0.5">Profile</span>
  </a>
</footer>

<style>
  :global(.material-symbols-outlined) {
    font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  }

  a.active {
    @apply text-green-600 dark:text-green-400 bg-green-50 dark:bg-green-900/30 rounded-2xl px-4 py-1;
  }

  a.active :global(.fill) {
    font-variation-settings: 'FILL' 1, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  }
</style>

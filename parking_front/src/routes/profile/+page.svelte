<script lang="ts">
  import TopNavBar from '$lib/components/TopNavBar.svelte';
  import BottomNavBar from '$lib/components/BottomNavBar.svelte';
  import type { User } from '$lib/types';

  let user: User = $state({
    userId: 'user-001',
    email: 'john.doe@example.com',
    role: 'CITIZEN',
    createdAt: '2025-01-15T10:30:00Z',
  });

  let showEditMode = $state(false);
  let formData = $state({
    email: user.email,
  });

  function toggleEditMode() {
    showEditMode = !showEditMode;
    if (!showEditMode) {
      formData.email = user.email;
    }
  }

  function handleSave() {
    user.email = formData.email;
    showEditMode = false;
  }

  function formatDate(dateString: string): string {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  }
</script>

<TopNavBar />

<main class="pt-16 pb-20 min-h-screen bg-surface">
  <div class="max-w-2xl mx-auto px-4 py-8">
    <!-- Profile Header -->
    <div class="bg-surface-container-lowest p-8 rounded-2xl shadow-sm mb-8">
      <div class="flex items-start justify-between mb-6">
        <div class="flex items-center gap-4">
          <div class="w-16 h-16 rounded-full overflow-hidden bg-primary-fixed border-2 border-primary">
            <img
              alt="User profile avatar"
              class="w-full h-full object-cover"
              src="https://lh3.googleusercontent.com/aida-public/AB6AXuCcBDQC_AJ3U4R4549fBbiwNPGTBx8idzhv3V17Ty3cezQ_eADI_iiy60hFMZ7UO4LG5RthyXgmOEOXI87ggmtVOcXG6q9PZBUVAXUBOp8YEz_cTdK1_OS2hTqDbTkvjJRf8Qbty2ImjCOxAa1ugE1GcVsIEO1494yaDZ0bh7Zyi4-oPbGzoVvj5jBsk4A2jEGxuBYYlkwnWDFrlaLAm0Mz_9I7_R_jtmdBn8RL6m3Xbx6zk3HJxMPzoDnd26iacRaL2gJA_2NNkQZB"
            />
          </div>
          <div>
            <h1 class="text-2xl font-bold text-on-surface">John Doe</h1>
            <p class="text-on-surface-variant text-sm">{user.role}</p>
          </div>
        </div>
        <button
          onclick={toggleEditMode}
          class="text-secondary font-semibold hover:opacity-70 transition-opacity flex items-center gap-2"
        >
          <span class="material-symbols-outlined">edit</span>
          Edit Profile
        </button>
      </div>

      {#if !showEditMode}
        <!-- View Mode -->
        <div class="space-y-4">
          <div>
            <p class="text-on-surface-variant text-sm">Email Address</p>
            <p class="text-on-surface font-medium">{user.email}</p>
          </div>
          <div>
            <p class="text-on-surface-variant text-sm">User ID</p>
            <p class="text-on-surface font-medium">{user.userId}</p>
          </div>
          <div>
            <p class="text-on-surface-variant text-sm">Member Since</p>
            <p class="text-on-surface font-medium">{formatDate(user.createdAt)}</p>
          </div>
        </div>
      {:else}
        <!-- Edit Mode -->
        <div class="space-y-4">
          <div>
            <label for="email" class="block text-on-surface-variant text-sm mb-2">Email Address</label>
            <input
              id="email"
              bind:value={formData.email}
              class="w-full px-4 py-2 rounded-lg border border-outline-variant bg-surface focus:outline-none focus:ring-2 focus:ring-secondary"
              type="email"
            />
          </div>
          <div class="flex gap-3 pt-4">
            <button
              onclick={handleSave}
              class="flex-1 bg-secondary text-on-secondary py-2 rounded-lg font-semibold hover:opacity-90 transition-opacity"
            >
              Save Changes
            </button>
            <button
              onclick={toggleEditMode}
              class="flex-1 bg-surface-container text-on-surface py-2 rounded-lg font-semibold hover:opacity-70 transition-opacity"
            >
              Cancel
            </button>
          </div>
        </div>
      {/if}
    </div>

    <!-- Account Settings -->
    <div class="bg-surface-container-lowest p-6 rounded-2xl shadow-sm mb-8">
      <h2 class="text-xl font-bold text-on-surface mb-6">Account Settings</h2>

      <div class="space-y-4">
        <div class="flex items-center justify-between pb-4 border-b border-surface-container">
          <div>
            <p class="font-semibold text-on-surface">Notifications</p>
            <p class="text-on-surface-variant text-sm">Receive parking alerts and updates</p>
          </div>
          <button
            class="relative inline-flex h-6 w-11 items-center rounded-full bg-primary-fixed"
            role="switch"
          >
            <span class="h-5 w-5 transform rounded-full bg-on-primary absolute left-0.5 transition-transform"></span>
          </button>
        </div>

        <div class="flex items-center justify-between pb-4 border-b border-surface-container">
          <div>
            <p class="font-semibold text-on-surface">Email Notifications</p>
            <p class="text-on-surface-variant text-sm">Receive updates via email</p>
          </div>
          <button
            class="relative inline-flex h-6 w-11 items-center rounded-full bg-surface-container"
            role="switch"
          >
            <span class="h-5 w-5 transform rounded-full bg-on-surface absolute left-0.5 transition-transform"></span>
          </button>
        </div>

        <div class="flex items-center justify-between">
          <div>
            <p class="font-semibold text-on-surface">Two-Factor Authentication</p>
            <p class="text-on-surface-variant text-sm">Enhanced security for your account</p>
          </div>
          <button class="text-secondary font-semibold hover:opacity-70 transition-opacity">Enable</button>
        </div>
      </div>
    </div>

    <!-- Payment Methods -->
    <div class="bg-surface-container-lowest p-6 rounded-2xl shadow-sm mb-8">
      <div class="flex items-center justify-between mb-6">
        <h2 class="text-xl font-bold text-on-surface">Payment Methods</h2>
        <button class="text-secondary font-semibold hover:opacity-70 transition-opacity flex items-center gap-1">
          <span class="material-symbols-outlined">add</span>
          Add Method
        </button>
      </div>

      <div class="space-y-4">
        <div class="p-4 rounded-lg bg-surface-container border border-outline-variant">
          <div class="flex items-center justify-between">
            <div>
              <p class="font-semibold text-on-surface">Visa Card</p>
              <p class="text-on-surface-variant text-sm">**** **** **** 4242</p>
            </div>
            <span class="text-secondary font-semibold">Default</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Danger Zone -->
    <div class="bg-error-container/20 border border-error rounded-2xl p-6">
      <h2 class="text-xl font-bold text-error mb-4">Danger Zone</h2>
      <button class="text-error font-semibold hover:opacity-70 transition-opacity">Delete Account</button>
    </div>
  </div>
</main>

<BottomNavBar />

<style>
  :global(.material-symbols-outlined) {
    font-variation-settings: 'FILL' 0, 'wght' 400, 'GRAD' 0, 'opsz' 24;
  }
</style>

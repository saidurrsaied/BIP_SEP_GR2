<script lang="ts">
    import { enhance } from '$app/forms';
    
    // Svelte 5 Runes - reactive component props:
    let { form } = $props();
</script>

<div class="flex min-h-screen items-center justify-center bg-gray-50 px-4 py-12 sm:px-6 lg:px-8">
    <div class="w-full max-w-md space-y-8 rounded-xl bg-white p-8 shadow-lg">
        
        <div>
            <h2 class="mt-6 text-center text-3xl font-bold tracking-tight text-gray-900">
                Welcome back
            </h2>
            <p class="mt-2 text-center text-sm text-gray-600">
                No account yet? <a href="/auth/signup" class="font-medium text-blue-600 hover:text-blue-500 transition-colors">Click here to create your account</a>
            </p>
        </div>

        <form class="mt-8 space-y-6" method="POST" use:enhance>
            <div class="space-y-4">
                <div>
                    <label for="email" class="block text-sm font-medium text-gray-700">Email</label>
                    <input id="email" name="email" type="email" required 
                        class="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 text-gray-900 placeholder-gray-400 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-500 sm:text-sm transition-shadow" 
                        placeholder="name@gmail.com" />
                </div>

                <div>
                    <label for="password" class="block text-sm font-medium text-gray-700">Password</label>
                    <input id="password" name="password" type="password" required 
                        class="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 text-gray-900 placeholder-gray-400 focus:border-blue-500 focus:outline-none focus:ring-2 focus:ring-blue-500 sm:text-sm transition-shadow" 
                        placeholder="••••••••" />
                </div>
            </div>

            {#if form?.error}
                <div class="rounded-md bg-red-50 p-4 border border-red-200">
                    <p class="text-sm font-medium text-red-800">{form.error}</p>
                </div>
            {/if}

            <div>
                <button type="submit" 
                    class="flex w-full justify-center rounded-md border border-transparent bg-gray-900 py-2.5 px-4 text-sm font-bold text-white shadow-sm hover:bg-gray-800 focus:outline-none focus:ring-2 focus:ring-gray-900 focus:ring-offset-2 transition-all">
                    Log in
                </button>
            </div>
        </form>
    </div>
</div>
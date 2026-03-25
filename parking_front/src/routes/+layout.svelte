<script lang="ts">
	// Verander ./layout.css naar ./app.css (of waar je die grote lijst hebt staan)
	import '../app.css';
	import favicon from '$lib/assets/favicon.svg';

	let { children } = $props();
</script>

<svelte:head><link rel="icon" href={favicon} /></svelte:head>
{@render children()}

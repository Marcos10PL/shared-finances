<script setup lang="ts">
defineProps<{
  title: string
}>()
</script>

<template>
  <UCard class="w-full max-w-100 shadow-lg" :ui="{ body: 'flex flex-col gap-6 p-5 sm:p-8' }">
    <div class="flex flex-col gap-1 text-center">
      <h1 class="text-xl font-semibold text-highlighted">{{ title }}</h1>
      <p class="text-sm text-muted">
        <slot name="subtitle" />
      </p>
    </div>

    <slot />
  </UCard>
</template>

<script setup lang="ts">
const { nameClass = 'w-28' } = defineProps<{
  nameClass?: string
}>()
</script>

<template>
  <div class="flex min-w-0 flex-1 items-center gap-3">
    <USkeleton class="size-8 shrink-0 rounded-full" />

    <div class="flex min-w-0 flex-1 flex-col gap-0.5">
      <USkeleton class="h-4.5" :class="nameClass" />
      <USkeleton class="h-4 w-16" />
    </div>
  </div>
</template>

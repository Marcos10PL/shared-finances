<script setup lang="ts">
defineProps<{
  iconOnly?: boolean
}>()
</script>

<template>
  <span class="inline-flex items-center gap-2">
    <span
      class="inline-flex size-6 shrink-0 items-center justify-center rounded-md bg-primary text-inverted"
    >
      <UIcon name="i-lucide-wallet" class="size-4" />
    </span>
    <span v-if="!iconOnly" class="font-brand text-lg font-bold whitespace-nowrap text-primary">
      Wspólne finanse
    </span>
  </span>
</template>

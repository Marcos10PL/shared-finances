<script setup lang="ts">
const { variant = 'settings' } = defineProps<{
  variant?: 'settings' | 'danger'
}>()
</script>

<template>
  <UCard :variant>
    <div class="flex flex-col gap-4">
      <slot />
    </div>

    <template v-if="$slots.footer" #footer>
      <slot name="footer" />
    </template>
  </UCard>
</template>

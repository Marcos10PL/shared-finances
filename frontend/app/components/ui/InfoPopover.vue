<script setup lang="ts">
const { text } = defineProps<{
  text: string
}>()
</script>

<template>
  <UPopover :content="{ side: 'top', align: 'start' }">
    <UButton
      color="neutral"
      variant="ghost"
      size="xs"
      icon="i-lucide-info"
      class="shrink-0 px-1 text-muted"
      aria-label="Pokaż opis"
    />

    <template #content>
      <p class="max-w-72 px-3 py-2 text-sm wrap-break-word whitespace-pre-line text-default">
        {{ text }}
      </p>
    </template>
  </UPopover>
</template>

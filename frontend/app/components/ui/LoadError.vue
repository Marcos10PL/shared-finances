<script setup lang="ts">
defineProps<{
  title: string
}>()

const emit = defineEmits<{
  retry: []
}>()
</script>

<template>
  <UEmpty
    icon="i-lucide-triangle-alert"
    :title
    description="Sprawdź połączenie i spróbuj ponownie."
    :actions="[
      { label: 'Spróbuj ponownie', icon: 'i-lucide-refresh-cw', onClick: () => emit('retry') }
    ]"
    class="m-auto"
  />
</template>

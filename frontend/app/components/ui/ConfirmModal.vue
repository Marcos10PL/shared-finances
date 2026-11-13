<script setup lang="ts">
const {
  description = undefined,
  confirmLabel = 'Usuń',
  color = 'error'
} = defineProps<{
  title: string
  description?: string
  confirmLabel?: string
  color?: 'error' | 'primary'
}>()

const emit = defineEmits<{
  close: [confirmed: boolean]
}>()
</script>

<template>
  <UModal
    :title
    :description
    :close="{ onClick: () => emit('close', false) }"
    @update:open="(open) => !open && emit('close', false)"
  >
    <template #footer>
      <div class="flex w-full justify-end gap-2">
        <UButton color="neutral" variant="outline" label="Anuluj" @click="emit('close', false)" />
        <UButton :color :label="confirmLabel" @click="emit('close', true)" />
      </div>
    </template>
  </UModal>
</template>

<script setup lang="ts">
const {
  description = undefined,
  confirmLabel = 'Usuń',
  color = 'error',
  onConfirm
} = defineProps<{
  title: string
  description?: string
  confirmLabel?: string
  color?: 'error' | 'primary'
  onConfirm: () => Promise<void>
}>()

const emit = defineEmits<{
  close: []
}>()

const loading = ref(false)

const onSubmit = async () => {
  loading.value = true

  try {
    await onConfirm()
  } finally {
    emit('close')
  }
}
</script>

<template>
  <UModal
    :title
    :description
    :dismissible="!loading"
    :close="loading ? false : { onClick: () => emit('close') }"
    @update:open="(open) => !open && !loading && emit('close')"
  >
    <template #footer>
      <UiModalFooter>
        <UButton
          color="neutral"
          variant="outline"
          label="Anuluj"
          :disabled="loading"
          @click="emit('close')"
        />
        <UButton :color :label="confirmLabel" :loading @click="onSubmit" />
      </UiModalFooter>
    </template>
  </UModal>
</template>

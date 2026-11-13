<script setup lang="ts">
const { loading = false } = defineProps<{
  formId: string
  submitLabel: string
  canSave: boolean
  canCancel: boolean
  loading?: boolean
}>()

defineEmits<{
  cancel: []
}>()
</script>

<template>
  <UButton
    color="neutral"
    variant="outline"
    label="Anuluj"
    :disabled="loading || !canCancel"
    @click="$emit('cancel')"
  />
  <UButton type="submit" :form="formId" :label="submitLabel" :loading :disabled="!canSave" />
</template>

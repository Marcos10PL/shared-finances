<script setup lang="ts">
import type { FormSubmitEvent } from '@nuxt/ui'
import { categoryNameSchema, type CategoryNameInput } from '~/schemas/category'
import type { Category } from '~/types'

const { category } = defineProps<{
  category: Category
}>()

const emit = defineEmits<{
  close: []
}>()

const { renameCategory } = useCategories()
const { showSuccess, showError } = useToasts()

const state = reactive<CategoryNameInput>({ name: category.name })
const loading = ref(false)

const canSave = computed(() => {
  const name = state.name.trim()
  return name !== '' && name !== category.name
})

async function onSubmit(event: FormSubmitEvent<CategoryNameInput>) {
  loading.value = true

  try {
    await renameCategory(category.id, event.data.name)
    showSuccess('Zapisano zmiany.')
    emit('close')
  } catch {
    showError('Nie udało się zapisać zmian.')
    loading.value = false
  }
}
</script>

<template>
  <UModal
    title="Edytuj kategorię"
    :close="{ onClick: () => emit('close') }"
    @update:open="(open) => !open && emit('close')"
  >
    <template #body>
      <UForm
        id="edit-category-form"
        :schema="categoryNameSchema"
        :state
        :disabled="loading"
        @submit="onSubmit"
      >
        <UiInput
          v-model="state.name"
          name="name"
          label="Nazwa"
          :max-length="VALIDATION.CATEGORY_NAME_MAX_LENGTH"
          size="lg"
        />
      </UForm>
    </template>

    <template #footer>
      <div class="flex w-full justify-end gap-2">
        <UButton color="neutral" variant="outline" label="Anuluj" @click="emit('close')" />
        <UButton
          type="submit"
          form="edit-category-form"
          label="Zapisz"
          :loading
          :disabled="!canSave"
        />
      </div>
    </template>
  </UModal>
</template>

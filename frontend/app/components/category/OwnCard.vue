<script setup lang="ts">
import type { FormSubmitEvent } from '@nuxt/ui'
import { categorySchema, type CategoryInput } from '~/schemas/category'
import type { Category } from '~/types'

const { categories, loaded, addCategory, removeCategory } = useCategories()
const { showSuccess, showError } = useToasts()
const { confirm, openEditCategory } = useModals()

const state = reactive<CategoryInput>({ name: '', type: CATEGORY_TYPES.EXPENSE })
const adding = ref(false)
const query = ref('')

const own = computed(() =>
  categories.value
    .filter((category) => category.userId !== null)
    .sort(
      (a, b) =>
        Number(b.type === CATEGORY_TYPES.EXPENSE) - Number(a.type === CATEGORY_TYPES.EXPENSE) ||
        a.name.localeCompare(b.name, 'pl')
    )
)

const normalizedQuery = computed(() => query.value.trim().toLowerCase())
const visible = computed(() =>
  own.value.filter((category) => category.name.toLowerCase().includes(normalizedQuery.value))
)

const canAdd = computed(() => state.name.trim() !== '')

async function onAdd(event: FormSubmitEvent<CategoryInput>) {
  adding.value = true

  try {
    await addCategory(event.data)
    state.name = ''
    showSuccess('Dodano kategorię.')
  } catch (error) {
    showError(getErrorMessage(error, CATEGORY_ERROR_MESSAGES, 'Nie udało się dodać kategorii.'))
  } finally {
    adding.value = false
  }
}

const onRemove = (category: Category) =>
  confirm({
    title: `Usunąć kategorię „${category.name}”?`,
    description: 'Tej operacji nie można cofnąć.',
    confirmLabel: 'Usuń kategorię',
    onConfirm: async () => {
      try {
        await removeCategory(category.id)
        showSuccess('Usunięto kategorię.')
      } catch (error) {
        showError(
          getErrorMessage(error, CATEGORY_ERROR_MESSAGES, 'Nie udało się usunąć kategorii.')
        )
      }
    }
  })
</script>

<template>
  <UCard variant="flush">
    <div class="flex items-center justify-between gap-3 p-4">
      <div class="flex flex-col gap-0.5">
        <h2 class="text-base font-semibold text-highlighted">Moje kategorie</h2>
        <p class="text-sm text-muted">Kategorii użytej w transakcjach nie można usunąć.</p>
      </div>

      <UBadge v-if="loaded" :label="own.length" color="neutral" variant="soft" />
    </div>

    <UForm
      :schema="categorySchema"
      :state
      :disabled="adding"
      class="border-t border-default bg-muted p-4"
      @submit="onAdd"
    >
      <div class="flex flex-wrap items-stretch gap-2">
        <UiInput
          v-model="state.name"
          name="name"
          placeholder="Nazwa nowej kategorii, np. Zwierzęta"
          aria-label="Nazwa nowej kategorii"
          :max-length="VALIDATION.CATEGORY_NAME_MAX_LENGTH"
          size="lg"
          class="min-w-0 flex-1 basis-full"
        />

        <TypeTabs v-model="state.type" class="min-w-0 flex-1" aria-label="Typ kategorii" />

        <UButton
          type="submit"
          icon="i-lucide-plus"
          label="Dodaj"
          size="lg"
          :loading="adding"
          :disabled="!canAdd"
        />
      </div>
    </UForm>

    <div class="border-t border-default p-4">
      <UInput
        v-model="query"
        icon="i-lucide-search"
        placeholder="Szukaj kategorii"
        aria-label="Szukaj kategorii"
        class="w-full"
      />
    </div>

    <ul v-if="!loaded" class="divide-y divide-default border-t border-default" aria-busy="true">
      <li v-for="index in 4" :key="index" class="flex items-center gap-3 px-4 py-3">
        <USkeleton class="size-8 shrink-0 rounded-md" />

        <div class="flex flex-1 flex-col gap-0.5">
          <USkeleton class="h-4.5 w-32" />
          <USkeleton class="h-4 w-16" />
        </div>
      </li>
    </ul>

    <ul v-else class="divide-y divide-default border-t border-default">
      <li v-for="category in visible" :key="category.id" class="flex items-center gap-3 px-4 py-3">
        <CategoryTypeIcon :type="category.type" />

        <div class="flex min-w-0 flex-1 flex-col">
          <span class="truncate text-sm font-medium text-highlighted">{{ category.name }}</span>
          <span class="text-xs text-muted">{{ CATEGORY_TYPE_LABELS[category.type] }}</span>
        </div>

        <UTooltip text="Zmień nazwę">
          <UButton
            icon="i-lucide-pencil"
            color="neutral"
            variant="ghost"
            size="sm"
            class="text-muted"
            :aria-label="`Zmień nazwę: ${category.name}`"
            @click="openEditCategory(category)"
          />
        </UTooltip>
        <UTooltip text="Usuń">
          <UButton
            icon="i-lucide-trash-2"
            color="error"
            variant="ghost"
            size="sm"
            :aria-label="`Usuń kategorię: ${category.name}`"
            @click="onRemove(category)"
          />
        </UTooltip>
      </li>

      <li v-if="visible.length === 0" class="px-4 py-6 text-center text-sm text-muted">
        {{
          own.length === 0 ? 'Nie masz jeszcze własnych kategorii.' : 'Nie znaleziono kategorii.'
        }}
      </li>
    </ul>
  </UCard>
</template>

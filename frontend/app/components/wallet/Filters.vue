<script setup lang="ts">
import type { Category, TransactionFilters, WalletMember } from '~/types'

const { categories, members } = defineProps<{
  categories: Category[]
  members: WalletMember[]
}>()

const open = defineModel<boolean>('open', { required: true })
const filters = defineModel<TransactionFilters>({ required: true })

const draft = ref<TransactionFilters>(createEmptyFilters())
const categorySearch = ref('')
const memberSearch = ref('')

watch(open, (isOpen) => {
  if (!isOpen) return

  draft.value = {
    type: filters.value.type,
    categoryIds: [...filters.value.categoryIds],
    authorIds: [...filters.value.authorIds]
  }
  categorySearch.value = ''
  memberSearch.value = ''
})

const visibleCategories = computed(() =>
  categories.filter((category) => draft.value.type === null || category.type === draft.value.type)
)

const typeTab = computed({
  get: () => draft.value.type,
  set: (value: TransactionFilters['type']) => {
    draft.value.type = value

    const visibleIds = new Set(visibleCategories.value.map((category) => category.id))
    draft.value.categoryIds = draft.value.categoryIds.filter((id) => visibleIds.has(id))
  }
})

const matches = (text: string, search: string) =>
  text.toLowerCase().includes(search.trim().toLowerCase())

const shownCategories = computed(() =>
  visibleCategories.value.filter((category) => matches(category.name, categorySearch.value))
)

interface Option {
  label: string
  value: string
}
const categoryItems = computed<Option[]>(() =>
  shownCategories.value.map((category) => ({ label: category.name, value: String(category.id) }))
)
const memberItems = computed<Option[]>(() =>
  shownMembers.value.map((member) => ({ label: member.displayName, value: String(member.id) }))
)

const toIdModel = (key: 'categoryIds' | 'authorIds') =>
  computed({
    get: () => draft.value[key].map(String),
    set: (values: string[]) => (draft.value[key] = values.map(Number))
  })
const selectedCategories = toIdModel('categoryIds')
const selectedAuthors = toIdModel('authorIds')

const shownMembers = computed(() =>
  members.filter((member) => matches(member.displayName, memberSearch.value))
)

const filterCount = computed(() => countFilters(draft.value))

const onClear = () => {
  draft.value = createEmptyFilters()
  filters.value = createEmptyFilters()
  open.value = false
}

const onApply = () => {
  filters.value = draft.value
  open.value = false
}
</script>

<template>
  <USlideover v-model:open="open" :ui="{ content: 'sm:max-w-lg' }">
    <template #title>
      <span class="flex items-center gap-2">
        Filtry transakcji
        <UBadge v-if="filterCount > 0" :label="filterCount" color="primary" variant="soft" />
      </span>
    </template>

    <template #body>
      <div class="flex flex-col gap-6">
        <section class="flex flex-col gap-3">
          <h3 class="text-sm font-medium text-highlighted">Typ</h3>
          <TypeTabs v-model="typeTab" all />
        </section>

        <section class="flex flex-col gap-3 border-t border-default pt-6">
          <div class="flex items-center justify-between">
            <h3 class="text-sm font-medium text-highlighted">Kategoria</h3>
            <span v-if="draft.categoryIds.length > 0" class="text-xs text-muted">
              {{ draft.categoryIds.length }} z {{ visibleCategories.length }}
            </span>
          </div>

          <UInput
            v-model="categorySearch"
            icon="i-lucide-search"
            placeholder="Szukaj kategorii"
            aria-label="Szukaj kategorii"
            class="w-full"
          />

          <UCheckboxGroup
            v-model="selectedCategories"
            :items="categoryItems"
            variant="card"
            orientation="horizontal"
            legend="Kategorie"
            :ui="{ legend: 'sr-only', fieldset: 'gap-1.5', item: 'px-3 py-1.5' }"
            class="max-h-96 overflow-y-auto"
          />
        </section>

        <section class="flex flex-col gap-3 border-t border-default pt-6">
          <div class="flex items-center justify-between">
            <h3 class="text-sm font-medium text-highlighted">Autor</h3>
            <span v-if="draft.authorIds.length > 0" class="text-xs text-muted">
              {{ draft.authorIds.length }} z {{ members.length }}
            </span>
          </div>

          <UInput
            v-model="memberSearch"
            icon="i-lucide-search"
            placeholder="Szukaj członka"
            aria-label="Szukaj członka"
            class="w-full"
          />

          <UCheckboxGroup
            v-model="selectedAuthors"
            :items="memberItems"
            variant="card"
            legend="Autorzy"
            :ui="{ legend: 'sr-only', fieldset: 'gap-1.5', item: 'px-3 py-1.5' }"
            class="max-h-96 overflow-y-auto"
          >
            <template #label="{ item }">
              <span class="flex items-center gap-2">
                <UiUserAvatar
                  :user-id="Number((item as Option).value)"
                  :name="(item as Option).label"
                  size="xs"
                />
                {{ (item as Option).label }}
              </span>
            </template>
          </UCheckboxGroup>
        </section>
      </div>
    </template>

    <template #footer>
      <UiModalFooter>
        <UButton color="neutral" variant="outline" label="Anuluj" @click="open = false" />
        <UButton
          color="neutral"
          variant="outline"
          label="Wyczyść"
          :disabled="filterCount === 0 && countFilters(filters) === 0"
          @click="onClear"
        />
        <UButton label="Zastosuj" @click="onApply" />
      </UiModalFooter>
    </template>
  </USlideover>
</template>

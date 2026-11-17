<script setup lang="ts">
import type { CategoryType } from '~/types'

const { all = false } = defineProps<{
  all?: boolean
}>()

const model = defineModel<CategoryType | null>({ required: true })

const ALL = 'all'

const items = computed(() => [
  ...(all ? [{ label: 'Wszystkie', value: ALL, icon: 'i-lucide-list' }] : []),
  {
    label: CATEGORY_TYPE_LABELS[CATEGORY_TYPES.EXPENSE],
    value: CATEGORY_TYPES.EXPENSE,
    icon: 'i-lucide-arrow-up-right',
    ui: { trigger: 'data-[state=active]:text-expense' }
  },
  {
    label: CATEGORY_TYPE_LABELS[CATEGORY_TYPES.INCOME],
    value: CATEGORY_TYPES.INCOME,
    icon: 'i-lucide-arrow-down-left',
    ui: { trigger: 'data-[state=active]:text-income' }
  }
])

const value = computed({
  get: () => model.value ?? ALL,
  set: (next: string) => (model.value = next === ALL ? null : (next as CategoryType))
})
</script>

<template>
  <UTabs v-model="value" :items :content="false" variant="pill" class="w-full" />
</template>

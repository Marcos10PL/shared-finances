<script setup lang="ts">
import type { TransactionSort } from '~/types'
defineProps<{
  total: number
  filterCount: number
  locked: boolean
}>()

const emit = defineEmits<{
  openFilters: []
}>()

const sort = defineModel<TransactionSort>('sort', { required: true })

const sortItems = Object.values(TRANSACTION_SORTS).map((value) => ({
  label: TRANSACTION_SORT_LABELS[value],
  value
}))
</script>

<template>
  <div class="@xl:flex @xl:items-center @xl:gap-3 @xl:px-4">
    <div class="flex items-center gap-2 p-4 @xl:px-0">
      <h2 class="text-base font-semibold text-highlighted">Transakcje</h2>
      <UBadge :label="total" color="neutral" variant="soft" />
    </div>

    <div class="hidden h-5 w-px bg-border @xl:block" aria-hidden="true" />

    <div class="flex gap-2 border-t border-default p-3 @xl:flex-1 @xl:border-t-0 @xl:p-0">
      <UButton
        color="neutral"
        variant="outline"
        icon="i-lucide-funnel"
        label="Filtry"
        :disabled="locked"
        class="justify-center @max-xl:shrink-0"
        @click="emit('openFilters')"
      >
        <template v-if="filterCount > 0" #trailing>
          <UBadge :label="filterCount" color="primary" variant="soft" size="sm" />
        </template>
      </UButton>

      <USelect
        v-model="sort"
        :items="sortItems"
        icon="i-lucide-arrow-up-down"
        aria-label="Sortowanie"
        :disabled="locked"
        class="@max-xl:min-w-0 @max-xl:flex-1 @xl:ml-auto @xl:w-48"
        :ui="{ content: 'min-w-0', item: 'py-1.5' }"
      />
    </div>
  </div>
</template>

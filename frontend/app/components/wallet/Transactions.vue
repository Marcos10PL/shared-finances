<script setup lang="ts">
import type {
  Category,
  Transaction,
  TransactionFilters,
  TransactionSort,
  WalletMember
} from '~/types'

const { items, total, categories, members, loading, canManage } = defineProps<{
  items: Transaction[]
  total: number
  categories: Category[]
  members: WalletMember[]
  loading: boolean
  canManage: (transaction: Transaction) => boolean
}>()

const emit = defineEmits<{
  edit: [transaction: Transaction]
  remove: [transaction: Transaction]
}>()

const filters = defineModel<TransactionFilters>('filters', { required: true })
const sort = defineModel<TransactionSort>('sort', { required: true })
const page = defineModel<number>('page', { required: true })

const filtersOpen = ref(false)

const filterCount = computed(() => countFilters(filters.value))
const pageCount = computed(() => getPageCount(total))

const isLocked = computed(() => loading || (total === 0 && filterCount.value === 0))

const emptyText = computed(() =>
  filterCount.value > 0
    ? 'Brak transakcji pasujących do filtrów.'
    : 'Brak transakcji w tym miesiącu.'
)
</script>

<template>
  <UCard variant="flush" :ui="{ root: '@container' }">
    <WalletTransactionsHeader
      v-model:sort="sort"
      :total
      :filter-count
      :locked="isLocked"
      @open-filters="filtersOpen = true"
    />

    <WalletFilters v-model="filters" v-model:open="filtersOpen" :categories :members />

    <WalletTransactionsTable
      :items
      :categories
      :members
      :loading
      :can-manage
      :empty-text
      @edit="emit('edit', $event)"
      @remove="emit('remove', $event)"
    />

    <div
      v-if="total > 0"
      class="flex items-center justify-between gap-3 border-t border-default p-4"
    >
      <span class="text-xs text-muted">Strona {{ page }} z {{ pageCount }}</span>

      <UFieldGroup>
        <UButton
          color="neutral"
          variant="outline"
          label="Poprzednie"
          :disabled="loading || page <= 1"
          @click="page--"
        />
        <UButton
          color="neutral"
          variant="outline"
          label="Następne"
          :disabled="loading || page >= pageCount"
          @click="page++"
        />
      </UFieldGroup>
    </div>
  </UCard>
</template>

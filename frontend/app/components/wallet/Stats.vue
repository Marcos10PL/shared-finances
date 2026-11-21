<script setup lang="ts">
import type { MonthTotals, WalletLimit } from '~/types'

const {
  totals,
  limit = undefined,
  loading = false
} = defineProps<{
  totals: MonthTotals
  limit?: WalletLimit
  loading?: boolean
}>()

const status = computed(() => (limit ? getLimitStatus(limit) : undefined))

const tiles = computed(() => [
  {
    label: 'Przychody',
    value: formatSignedCurrency(totals.income),
    icon: 'i-lucide-arrow-down-left',
    iconClass: 'bg-income/10 text-income',
    valueClass: 'text-income'
  },
  {
    label: 'Wydatki',
    value: formatSignedCurrency(-totals.expenses),
    icon: 'i-lucide-arrow-up-right',
    iconClass: 'bg-expense/10 text-expense',
    valueClass: 'text-expense'
  },
  {
    label: 'Saldo',
    value: formatCurrency(totals.balance),
    icon: 'i-lucide-wallet',
    iconClass: 'bg-primary/10 text-primary',
    valueClass: 'text-highlighted'
  }
])
</script>

<template>
  <div class="@container">
    <div class="flex flex-wrap gap-4">
      <UCard
        v-for="tile in tiles"
        :key="tile.label"
        class="min-w-44 flex-auto"
        :ui="{ body: 'p-4 sm:p-4' }"
      >
        <div class="flex items-center justify-between gap-2">
          <span class="text-sm text-muted">{{ tile.label }}</span>
          <span
            class="inline-flex size-7 items-center justify-center rounded-md"
            :class="tile.iconClass"
          >
            <UIcon :name="tile.icon" class="size-4" />
          </span>
        </div>
        <USkeleton v-if="loading" class="mt-1 h-8 w-32" />
        <p v-else class="mt-1 text-2xl font-semibold tabular-nums" :class="tile.valueClass">
          {{ tile.value }}
        </p>
      </UCard>

      <UCard
        v-if="limit && status"
        class="min-w-52 flex-auto"
        :ui="{ root: 'relative overflow-hidden', body: 'p-4 pb-5 sm:p-4 sm:pb-5' }"
      >
        <div class="flex items-center justify-between gap-2">
          <span class="text-sm text-muted">Limit miesięczny</span>
          <span class="text-sm font-medium" :class="LIMIT_STYLES[status.state].label">
            {{ formatPercent(status.percent) }}
          </span>
        </div>
        <p class="mt-1 text-2xl font-semibold tabular-nums text-highlighted">
          {{ formatCurrency(limit.monthlyExpenses) }}
          <span class="text-xs font-normal text-muted">
            z {{ formatCurrency(limit.monthlyLimit!) }}
          </span>
        </p>

        <!-- A bar glued to the bottom edge of the tile, with a mark of the warning threshold -->
        <div class="absolute inset-x-0 bottom-0">
          <UProgress
            :model-value="Math.min(status.percent, 100)"
            :color="LIMIT_STYLES[status.state].bar"
            :ui="{ base: 'h-1.25 rounded-none bg-elevated' }"
          />
          <span
            v-if="limit.warningThreshold !== null"
            class="absolute inset-y-0 w-0.5 bg-inverted"
            :style="{ left: `${limit.warningThreshold}%` }"
          />
        </div>
      </UCard>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { CategoryShare } from '~/types'

const { rows, loading } = defineProps<{
  rows: CategoryShare[]
  loading: boolean
}>()

const getBarClass = (id: number | null) =>
  id === null ? 'bg-chart-other' : getChartColorById(id).bg
</script>

<template>
  <UCard variant="flush">
    <h2 class="p-4 text-base font-semibold text-highlighted">Wydatki wg kategorii</h2>

    <ul v-if="loading" class="flex flex-col gap-5 border-t border-default p-4" aria-busy="true">
      <li v-for="index in 5" :key="index" class="flex flex-col gap-3">
        <USkeleton class="h-5 w-full" />
        <USkeleton class="h-1.5 w-full" />
      </li>
    </ul>

    <ul v-else-if="rows.length > 0" class="flex flex-col gap-5 border-t border-default p-4">
      <li v-for="row in rows" :key="row.id ?? 'rest'" class="flex flex-col gap-3">
        <div class="flex items-center justify-between gap-3 text-sm">
          <span class="truncate font-semibold text-highlighted">{{ row.name }}</span>
          <span class="flex shrink-0 items-baseline gap-5 tabular-nums">
            <span class="text-xs text-muted">{{ formatPercent(row.percent) }}</span>
            <span class="font-medium text-highlighted">{{ formatCurrency(row.amount) }}</span>
          </span>
        </div>

        <div class="h-1.5 w-full rounded-full bg-elevated">
          <div
            class="h-full rounded-full"
            :class="getBarClass(row.id)"
            :style="{ width: `${row.percent}%` }"
          />
        </div>
      </li>
    </ul>

    <p v-else class="border-t border-default px-4 py-8 text-center text-sm text-muted">
      Brak wydatków w tym miesiącu.
    </p>
  </UCard>
</template>

<script setup lang="ts">
import type { WalletLimit } from '~/types'

const { limit } = defineProps<{
  limit: WalletLimit
}>()

const status = computed(() => getLimitStatus(limit))

const label = computed(() =>
  status.value.state === LIMIT_STATES.OVER
    ? `Przekroczono o ${formatCurrency(status.value.overBy)}`
    : formatPercent(status.value.percent)
)
</script>

<template>
  <div class="flex flex-col gap-1.5">
    <div class="flex justify-between gap-2 text-xs">
      <span class="text-muted tabular-nums">
        <template v-if="status.state === LIMIT_STATES.NONE">Brak limitu</template>
        <template v-else>
          {{ formatCurrency(limit.monthlyExpenses) }} z {{ formatCurrency(limit.monthlyLimit!) }}
        </template>
      </span>
      <span
        v-if="status.state !== LIMIT_STATES.NONE"
        class="font-medium"
        :class="LIMIT_STYLES[status.state].label"
      >
        {{ label }}
      </span>
    </div>

    <div class="relative">
      <UProgress
        :model-value="Math.min(status.percent, 100)"
        :color="LIMIT_STYLES[status.state].bar"
        :ui="{ base: 'h-1.5 bg-elevated' }"
      />
      <span
        v-if="status.state !== LIMIT_STATES.NONE && limit.warningThreshold !== null"
        class="absolute -top-0.75 -bottom-0.75 w-0.5 rounded-xs bg-inverted"
        :style="{ left: `${limit.warningThreshold}%` }"
      />
    </div>
  </div>
</template>

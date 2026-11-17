<script setup lang="ts">
import type { WalletLimit } from '~/types'

const { limit, walletId, isOwner } = defineProps<{
  limit: WalletLimit
  walletId: number
  isOwner: boolean
}>()

const status = computed(() => getLimitStatus(limit))

const isOver = computed(() => status.value.state === LIMIT_STATES.OVER)
const isVisible = computed(() => isOver.value || status.value.state === LIMIT_STATES.WARNING)

const text = computed(() =>
  isOver.value
    ? `Przekroczono limit o ${formatCurrency(status.value.overBy)}.`
    : `Wykorzystano ${formatPercent(status.value.percent)} limitu – do końca miesiąca zostało ${formatCurrency((limit.monthlyLimit ?? 0) - limit.monthlyExpenses)}.`
)
</script>

<template>
  <UAlert
    v-if="isVisible"
    :color="isOver ? 'error' : 'warning'"
    variant="soft"
    icon="i-lucide-triangle-alert"
    :description="text"
    orientation="horizontal"
    :actions="
      isOwner
        ? [
            {
              label: 'Zmień limit',
              color: isOver ? 'error' : 'warning',
              class: 'text-sm',
              variant: 'link',
              to: ROUTES.WALLET_SETTINGS(walletId)
            }
          ]
        : []
    "
  />
</template>

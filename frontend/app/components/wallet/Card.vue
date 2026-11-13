<script setup lang="ts">
import type { Wallet } from '~/types'

const { wallet, index } = defineProps<{
  wallet: Wallet
  index: number
}>()

const { leaveWallet } = useWallets()
const { showSuccess, showError } = useToasts()
const { confirm } = useModals()

const limit = computed(() => getLimitStatus(wallet))

const isOwner = computed(() => wallet.role === WALLET_ROLES.OWNER)

const LIMIT_STYLES = {
  [LIMIT_STATES.NONE]: { bar: 'primary', label: '' },
  [LIMIT_STATES.OK]: { bar: 'primary', label: 'text-muted' },
  [LIMIT_STATES.WARNING]: { bar: 'warning', label: 'text-warning' },
  [LIMIT_STATES.OVER]: { bar: 'error', label: 'text-error' }
} as const

const limitLabel = computed(() =>
  limit.value.state === LIMIT_STATES.OVER
    ? `Przekroczono o ${formatCurrency(limit.value.overBy)}`
    : formatPercent(limit.value.percent)
)

const onLeave = async () => {
  const confirmed = await confirm({
    title: `Opuścić portfel „${wallet.name}”?`,
    description:
      'Stracisz dostęp do jego transakcji i podsumowań. Właściciel będzie mógł zaprosić Cię ponownie.',
    confirmLabel: 'Opuść portfel',
    color: 'primary'
  })

  if (!confirmed) return

  try {
    await leaveWallet(wallet.id)
    showSuccess('Opuszczono portfel')
  } catch {
    showError('Nie udało się opuścić portfela')
  }
}
</script>

<template>
  <UCard
    variant="interactive"
    :ui="{
      body: 'flex flex-col gap-4 p-5 sm:p-5',
      footer: 'flex items-center justify-between py-2 pr-3 pl-5 sm:py-2 sm:pr-3 sm:pl-5'
    }"
  >
    <div class="flex flex-col gap-1.5">
      <div class="flex items-center gap-2">
        <span class="size-2 shrink-0 rounded-xs" :class="getChartColor(index).bg" />
        <NuxtLink
          :to="ROUTES.WALLET(wallet.id)"
          class="truncate font-semibold text-highlighted after:absolute after:inset-0"
        >
          {{ wallet.name }}
        </NuxtLink>
      </div>
      <div>
        <UBadge
          :label="WALLET_ROLE_LABELS[wallet.role]"
          :color="isOwner ? 'primary' : 'secondary'"
          :variant="isOwner ? 'soft' : 'subtle'"
          class="rounded-full px-2.5 py-0.5"
        />
      </div>
    </div>

    <div class="flex flex-col">
      <span class="text-xs text-muted">Saldo</span>
      <span class="text-2xl/8 font-semibold tracking-tight text-highlighted tabular-nums">
        {{ formatCurrency(wallet.balance) }}
      </span>
    </div>

    <div class="flex flex-col gap-1.5">
      <div class="flex justify-between gap-2 text-xs">
        <span class="text-muted tabular-nums">
          <template v-if="limit.state === LIMIT_STATES.NONE">Brak limitu</template>
          <template v-else>
            {{ formatCurrency(wallet.monthlyExpenses) }} z
            {{ formatCurrency(wallet.monthlyLimit!) }}
          </template>
        </span>
        <span
          v-if="limit.state !== LIMIT_STATES.NONE"
          class="font-medium"
          :class="LIMIT_STYLES[limit.state].label"
        >
          {{ limitLabel }}
        </span>
      </div>

      <div class="relative">
        <UProgress
          :model-value="Math.min(limit.percent, 100)"
          :color="LIMIT_STYLES[limit.state].bar"
          :ui="{ base: 'h-1.5 bg-elevated' }"
        />
        <span
          v-if="limit.state !== LIMIT_STATES.NONE && wallet.warningThreshold !== null"
          class="absolute -top-0.75 -bottom-0.75 w-0.5 rounded-xs bg-inverted"
          :style="{ left: `${wallet.warningThreshold}%` }"
        />
      </div>
    </div>

    <template #footer>
      <UAvatarGroup :max="3" size="xs">
        <UAvatar
          v-for="member in wallet.members"
          :key="member.id"
          :text="getInitials(member.displayName)"
          :alt="member.displayName"
          :class="getChartColorById(member.id).bg"
          :ui="{
            fallback: `text-[0.625rem] font-semibold ${getChartColorById(member.id).initials}`
          }"
        />
      </UAvatarGroup>

      <UButton
        v-if="isOwner"
        :to="ROUTES.WALLET_SETTINGS(wallet.id)"
        icon="i-lucide-settings"
        label="Ustawienia"
        color="neutral"
        variant="ghost"
        size="sm"
        class="relative text-muted"
      />
      <UButton
        v-else
        icon="i-lucide-log-out"
        label="Opuść portfel"
        color="neutral"
        variant="ghost"
        size="sm"
        class="relative text-muted"
        @click="onLeave"
      />
    </template>
  </UCard>
</template>

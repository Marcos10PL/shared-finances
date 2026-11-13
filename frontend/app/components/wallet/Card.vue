<script setup lang="ts">
import type { Wallet } from '~/types'

const { wallet, index } = defineProps<{
  wallet: Wallet
  index: number
}>()

const { leaveWallet } = useWallets()
const { showSuccess, showError } = useToasts()
const { confirm } = useModals()

const isOwner = computed(() => wallet.role === WALLET_ROLES.OWNER)

const onLeave = () =>
  confirm({
    title: `Opuścić portfel „${wallet.name}”?`,
    description:
      'Stracisz dostęp do jego transakcji i podsumowań. Właściciel będzie mógł zaprosić Cię ponownie.',
    confirmLabel: 'Opuść portfel',
    color: 'primary',
    onConfirm: async () => {
      try {
        await leaveWallet(wallet.id)
        showSuccess('Opuszczono portfel.')
      } catch {
        showError('Nie udało się opuścić portfela.')
      }
    }
  })
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
        <WalletRoleBadge :role="wallet.role" />
      </div>
    </div>

    <div class="flex flex-col">
      <span class="text-xs text-muted">Saldo</span>
      <span class="text-2xl/8 font-semibold tracking-tight text-highlighted tabular-nums">
        {{ formatCurrency(wallet.balance) }}
      </span>
    </div>

    <WalletLimitBar :limit="wallet" />

    <template #footer>
      <UAvatarGroup :max="3" size="xs">
        <UiUserAvatar
          v-for="member in wallet.members"
          :key="member.id"
          :user-id="member.id"
          :name="member.displayName"
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

<script setup lang="ts">
import type { WalletRole } from '~/types'

const { role, size = 'md' } = defineProps<{
  role: WalletRole
  size?: 'sm' | 'md'
}>()

const STYLES = {
  [WALLET_ROLES.OWNER]: { color: 'primary' },
  [WALLET_ROLES.MEMBER]: { color: 'secondary' },
  [WALLET_ROLES.GUEST]: { color: 'info' }
} as const
</script>

<template>
  <UBadge :label="WALLET_ROLE_LABELS[role]" :color="STYLES[role].color" variant="subtle" :size />
</template>

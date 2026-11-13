<script setup lang="ts">
import type { WalletRole } from '~/types'

const { isYou = false } = defineProps<{
  title: string
  subtitle: string
  role: WalletRole
  isYou?: boolean
}>()
</script>

<template>
  <li class="flex items-center gap-3 px-4 py-3">
    <slot name="leading" />

    <div class="flex min-w-0 flex-1 flex-col">
      <div class="flex flex-wrap items-center gap-2">
        <span class="truncate font-medium text-highlighted">{{ title }}</span>
        <span v-if="isYou" class="text-sm text-muted">(Ty)</span>
        <WalletRoleBadge size="sm" class="-mt-0.5" :role />
      </div>
      <span class="truncate text-xs text-muted">{{ subtitle }}</span>
    </div>

    <slot name="actions" />
  </li>
</template>

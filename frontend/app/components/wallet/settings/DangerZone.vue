<script setup lang="ts">
import type { Wallet } from '~/types'

const { wallet } = defineProps<{
  wallet: Wallet
}>()

const { deleteWallet } = useWallets()
const { showSuccess, showError } = useToasts()
const { confirm } = useModals()

const onDelete = () =>
  confirm({
    title: `Usunąć portfel „${wallet.name}”?`,
    description:
      'Usunięte zostaną wszystkie transakcje, członkostwa i zaproszenia tego portfela. Tej operacji nie można cofnąć.',
    confirmLabel: 'Usuń portfel',
    onConfirm: async () => {
      try {
        await deleteWallet(wallet.id)
        // the settings page has navigation guard
        showSuccess('Usunięto portfel.')
      } catch {
        showError('Nie udało się usunąć portfela.')
      }
    }
  })
</script>

<template>
  <WalletSettingsCard variant="danger">
    <div class="@container">
      <div class="flex flex-col gap-4 @md:flex-row @md:items-center @md:justify-between">
        <div class="flex flex-col gap-1">
          <span class="font-medium text-highlighted">Usuń portfel</span>
          <span class="text-sm text-muted">
            Usuwa wszystkie transakcje, członkostwa i zaproszenia tego portfela.
          </span>
        </div>

        <UButton
          color="error"
          variant="soft"
          icon="i-lucide-trash-2"
          label="Usuń portfel"
          @click="onDelete"
        />
      </div>
    </div>
  </WalletSettingsCard>
</template>

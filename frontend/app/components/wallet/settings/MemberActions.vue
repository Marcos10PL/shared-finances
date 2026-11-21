<script setup lang="ts">
import type { DropdownMenuItem } from '@nuxt/ui'
import type { AssignableRole, Wallet, WalletMember } from '~/types'

const { wallet, member } = defineProps<{
  wallet: Wallet
  member: WalletMember
}>()

const { changeMemberRole, removeMember } = useWallets()
const { showSuccess, showError } = useToasts()
const { confirm } = useModals()

const onChangeRole = async (role: AssignableRole) => {
  if (member.role === role) return

  try {
    await changeMemberRole(wallet.id, member.id, role)
    showSuccess('Zmieniono rolę.')
  } catch (error) {
    showError(getErrorMessage(error, MEMBER_ERROR_MESSAGES, 'Nie udało się zmienić roli.'))
  }
}

const onRemove = () =>
  confirm({
    title: 'Usunąć tę osobę z portfela?',
    description: `${member.displayName} straci dostęp do transakcji i podsumowań tego portfela.`,
    confirmLabel: 'Usuń z portfela',
    onConfirm: async () => {
      try {
        await removeMember(wallet.id, member.id)
        showSuccess('Usunięto osobę z portfela.')
      } catch (error) {
        showError(
          getErrorMessage(error, MEMBER_ERROR_MESSAGES, 'Nie udało się usunąć osoby z portfela.')
        )
      }
    }
  })

const items = computed<DropdownMenuItem[][]>(() => [
  [
    { type: 'label', label: 'Rola' },
    ...ASSIGNABLE_ROLES.map((role) => ({
      label: WALLET_ROLE_LABELS[role],
      icon: member.role === role ? 'i-lucide-check' : undefined,
      onSelect: () => onChangeRole(role)
    }))
  ],
  [
    {
      label: 'Usuń z portfela',
      icon: 'i-lucide-user-minus',
      color: 'error',
      onSelect: onRemove
    }
  ]
])
</script>

<template>
  <UDropdownMenu :items :content="{ align: 'end' }">
    <UButton
      icon="i-lucide-ellipsis"
      color="neutral"
      variant="ghost"
      size="md"
      :aria-label="`Akcje: ${member.displayName}`"
    />
  </UDropdownMenu>
</template>

<script setup lang="ts">
import type { Invitation, InvitationAnswer } from '~/types'

const { invitation } = defineProps<{
  invitation: Invitation
}>()

const { acceptInvitation, declineInvitation } = useInvitations()
const { showSuccess, showError } = useToasts()

const pending = ref<InvitationAnswer | null>(null)

const answer = async (
  kind: InvitationAnswer,
  send: (id: number) => Promise<void>,
  messages: { success: string; failure: string }
) => {
  pending.value = kind

  try {
    await send(invitation.id)
    showSuccess(messages.success)
  } catch {
    showError(messages.failure)
    pending.value = null
  }
}

const onAccept = () =>
  answer(INVITATION_ANSWERS.ACCEPT, acceptInvitation, {
    success: 'Przyjęto zaproszenie.',
    failure: 'Nie udało się przyjąć zaproszenia.'
  })

const onDecline = () =>
  answer(INVITATION_ANSWERS.DECLINE, declineInvitation, {
    success: 'Odrzucono zaproszenie.',
    failure: 'Nie udało się odrzucić zaproszenia.'
  })
</script>

<template>
  <li class="flex flex-col gap-3 p-4 @xl:flex-row @xl:items-center @xl:gap-4">
    <div class="flex min-w-0 flex-1 items-center gap-3">
      <UiUser :user-id="invitation.ownerId" :name="invitation.ownerName" :truncate="false">
        <template #name>
          <span class="font-normal text-default">
            <span class="font-medium text-highlighted">{{ invitation.ownerName }}</span>
            zaprasza Cię do portfela „<span class="font-medium text-highlighted">{{
              invitation.walletName
            }}</span
            >”
          </span>
        </template>

        <template #description>
          {{ formatDate(invitation.createdAt) }} · Rola: {{ WALLET_ROLE_LABELS[invitation.role] }}
        </template>
      </UiUser>
    </div>

    <div class="grid grid-cols-2 gap-2 @xl:flex">
      <UButton
        color="neutral"
        variant="outline"
        label="Odrzuć"
        block
        :loading="pending === INVITATION_ANSWERS.DECLINE"
        :disabled="pending !== null"
        @click="onDecline"
      />
      <UButton
        label="Przyjmij"
        block
        :loading="pending === INVITATION_ANSWERS.ACCEPT"
        :disabled="pending !== null"
        @click="onAccept"
      />
    </div>
  </li>
</template>

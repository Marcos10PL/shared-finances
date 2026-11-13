<script setup lang="ts">
import type { FormSubmitEvent } from '@nuxt/ui'
import { inviteSchema, type InviteInput } from '~/schemas/wallet'
import type { Wallet } from '~/types'

const { wallet } = defineProps<{
  wallet: Wallet
}>()

const { user } = useAuth()
const { inviteMember } = useWallets()
const { showSuccess, showError } = useToasts()

const ROLE_OPTIONS = ASSIGNABLE_ROLES.map((role) => ({
  label: WALLET_ROLE_LABELS[role],
  value: role
}))

const form = useTemplateRef('form')
const state = reactive<InviteInput>({ email: '', role: WALLET_ROLES.MEMBER })
const inviting = ref(false)
const query = ref('')

const canInvite = computed(() => state.email.trim() !== '')
const isChanged = computed(() => canInvite.value || state.role !== WALLET_ROLES.MEMBER)

const normalizedQuery = computed(() => query.value.trim().toLowerCase())

const matches = (...values: string[]) =>
  values.some((value) => value.toLowerCase().includes(normalizedQuery.value))

const members = computed(() =>
  wallet.members.filter((member) => matches(member.displayName, member.email))
)
const invitations = computed(() => wallet.invitations.filter((item) => matches(item.email)))

async function onInvite(event: FormSubmitEvent<InviteInput>) {
  inviting.value = true

  try {
    await inviteMember(wallet.id, event.data.email, event.data.role)
    state.email = ''
    showSuccess('Wysłano zaproszenie.')
  } catch (error) {
    const code = getInviteErrorCode(error)

    if (code) form.value?.setErrors([{ name: 'email', message: INVITE_ERROR_MESSAGES[code] }])
    else showError('Nie udało się wysłać zaproszenia.')
  } finally {
    inviting.value = false
  }
}

const onCancelInvite = () => {
  state.email = ''
  state.role = WALLET_ROLES.MEMBER
  form.value?.clear()
}
</script>

<template>
  <div class="flex flex-col gap-4">
    <WalletSettingsCard>
      <UForm
        id="invite-form"
        ref="form"
        :schema="inviteSchema"
        :state
        :disabled="inviting"
        class="@container"
        @submit="onInvite"
      >
        <div class="flex flex-col gap-4 @md:flex-row">
          <UiInput
            v-model="state.email"
            name="email"
            label="Adres e-mail"
            type="email"
            placeholder="np. anna@example.com"
            autocomplete="off"
            :max-length="VALIDATION.EMAIL_MAX_LENGTH"
            size="lg"
            class="flex-1"
          />

          <UFormField name="role" label="Rola" size="lg" class="@md:w-40">
            <USelect v-model="state.role" :items="ROLE_OPTIONS" class="w-full" />
          </UFormField>
        </div>
      </UForm>

      <template #footer>
        <WalletSettingsFormActions
          form-id="invite-form"
          submit-label="Wyślij zaproszenie"
          :loading="inviting"
          :can-save="canInvite"
          :can-cancel="isChanged"
          @cancel="onCancelInvite"
        />
      </template>
    </WalletSettingsCard>

    <UCard variant="flush">
      <div class="p-4">
        <UInput
          v-model="query"
          icon="i-lucide-search"
          placeholder="Szukaj po imieniu lub e-mailu..."
          aria-label="Szukaj osoby"
          class="w-full"
        />
      </div>

      <ul class="divide-y divide-default border-t border-default">
        <WalletSettingsPersonRow
          v-for="member in members"
          :key="member.id"
          :title="member.displayName"
          :subtitle="member.email"
          :role="member.role"
          :is-you="member.id === user?.id"
        >
          <template #leading>
            <UiUserAvatar :user-id="member.id" :name="member.displayName" />
          </template>

          <template v-if="member.role !== WALLET_ROLES.OWNER" #actions>
            <WalletSettingsMemberActions :wallet :member />
          </template>
        </WalletSettingsPersonRow>

        <WalletSettingsPersonRow
          v-for="invitation in invitations"
          :key="invitation.id"
          :title="invitation.email"
          :subtitle="`Zaproszona ${formatDate(invitation.createdAt)}`"
          :role="invitation.role"
          class="bg-muted"
        >
          <template #leading>
            <span
              class="inline-flex size-8 shrink-0 items-center justify-center rounded-full bg-elevated"
            >
              <UIcon name="i-lucide-mail" class="size-4 text-muted" />
            </span>
          </template>
        </WalletSettingsPersonRow>

        <li
          v-if="members.length === 0 && invitations.length === 0"
          class="px-4 py-6 text-center text-sm text-muted"
        >
          Nikogo nie znaleziono.
        </li>
      </ul>
    </UCard>
  </div>
</template>

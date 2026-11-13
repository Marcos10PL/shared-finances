<script setup lang="ts">
import type { FormSubmitEvent } from '@nuxt/ui'
import { walletSchema, type WalletInput } from '~/schemas/wallet'
import type { Wallet } from '~/types'

const { wallet } = defineProps<{
  wallet: Wallet
}>()

const { updateWallet } = useWallets()
const { showSuccess, showError } = useToasts()

const form = useTemplateRef('form')
const state = reactive<WalletInput>({ name: wallet.name })
const loading = ref(false)

watch(
  () => wallet.name,
  (name) => {
    state.name = name
  }
)

const isChanged = computed(() => state.name !== wallet.name)

const canSave = computed(() => {
  const name = state.name.trim()
  return name !== '' && name !== wallet.name
})

const onCancel = () => {
  state.name = wallet.name
  form.value?.clear()
}

async function onSubmit(event: FormSubmitEvent<WalletInput>) {
  loading.value = true

  try {
    await updateWallet(wallet.id, event.data)
    showSuccess('Zapisano zmiany.')
  } catch {
    showError('Nie udało się zapisać zmian.')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <WalletSettingsCard>
    <UForm
      id="name-form"
      ref="form"
      :schema="walletSchema"
      :state
      :disabled="loading"
      class="flex flex-col gap-4"
      @submit="onSubmit"
    >
      <UiInput
        v-model="state.name"
        name="name"
        label="Nazwa portfela"
        :max-length="VALIDATION.WALLET_NAME_MAX_LENGTH"
        size="lg"
        required
      />
    </UForm>

    <template #footer>
      <WalletSettingsFormActions
        form-id="name-form"
        submit-label="Zapisz zmiany"
        :loading
        :can-save
        :can-cancel="isChanged"
        @cancel="onCancel"
      />
    </template>
  </WalletSettingsCard>
</template>

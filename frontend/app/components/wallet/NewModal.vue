<script setup lang="ts">
import type { FormSubmitEvent } from '@nuxt/ui'
import { walletSchema, type WalletInput } from '~/schemas/wallet'

const emit = defineEmits<{
  close: [created: boolean]
}>()

const { createWallet } = useWallets()
const { showSuccess, showError } = useToasts()

const state = reactive<WalletInput>({ name: '' })
const loading = ref(false)

async function onSubmit(event: FormSubmitEvent<WalletInput>) {
  loading.value = true

  try {
    await createWallet(event.data.name)
    showSuccess('Utworzono portfel')
    emit('close', true)
  } catch {
    showError('Nie udało się utworzyć portfela')
    loading.value = false
  }
}
</script>

<template>
  <UModal
    title="Nowy portfel"
    :close="{ onClick: () => emit('close', false) }"
    @update:open="(open) => !open && emit('close', false)"
  >
    <template #body>
      <UForm
        id="new-wallet-form"
        :schema="walletSchema"
        :state
        :disabled="loading"
        @submit="onSubmit"
      >
        <UiInput
          v-model="state.name"
          name="name"
          label="Nazwa portfela"
          :max-length="VALIDATION.WALLET_NAME_MAX_LENGTH"
          size="lg"
        />
      </UForm>
    </template>

    <template #footer>
      <div class="flex w-full justify-end gap-2">
        <UButton color="neutral" variant="outline" label="Anuluj" @click="emit('close', false)" />
        <UButton type="submit" form="new-wallet-form" label="Utwórz portfel" :loading />
      </div>
    </template>
  </UModal>
</template>

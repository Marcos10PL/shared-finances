<script setup lang="ts">
import type { FormSubmitEvent } from '@nuxt/ui'
import { limitSchema, type LimitInput } from '~/schemas/wallet'
import type { LimitSettings, Wallet, WalletLimit } from '~/types'

const { wallet } = defineProps<{
  wallet: Wallet
}>()

const { setLimit } = useWallets()
const { showSuccess, showError } = useToasts()
const { confirm } = useModals()

const hasLimit = computed(() => wallet.monthlyLimit !== null)

const state = reactive<Partial<LimitInput>>({})
const form = useTemplateRef('form')
const loading = ref(false)

const reset = () => {
  state.monthlyLimit = wallet.monthlyLimit ?? undefined
  state.warningThreshold = wallet.warningThreshold
}

watch(() => [wallet.monthlyLimit, wallet.warningThreshold], reset, { immediate: true })

const canSave = computed(
  () =>
    (state.monthlyLimit ?? null) !== wallet.monthlyLimit ||
    (state.warningThreshold ?? null) !== wallet.warningThreshold
)

const preview = computed<WalletLimit>(() => ({
  monthlyLimit: state.monthlyLimit ?? null,
  monthlyExpenses: wallet.monthlyExpenses,
  warningThreshold: state.warningThreshold ?? null
}))

const thresholdHelp = computed(() => {
  if (!state.warningThreshold) return 'Brak ustawionego ostrzeżenia.'

  if (!state.monthlyLimit) return `Ostrzeżenie pojawi się przy ${state.warningThreshold}% limitu.`

  return `Ostrzeżenie pojawi się przy ${formatCurrency((state.monthlyLimit * state.warningThreshold) / 100)}.`
})

// actions
async function onSubmit(event: FormSubmitEvent<LimitInput>) {
  await save({
    monthlyLimit: event.data.monthlyLimit ?? null,
    warningThreshold: event.data.warningThreshold ?? null
  })
}

const save = async (limit: LimitSettings) => {
  loading.value = true

  try {
    await setLimit(wallet.id, limit)
    showSuccess(limit.monthlyLimit === null ? 'Usunięto limit.' : 'Zapisano limit.')
  } catch {
    showError('Nie udało się zapisać limitu.')
  } finally {
    loading.value = false
  }
}

const onRemove = () =>
  confirm({
    title: 'Usunąć limit?',
    description: 'Portfel nie będzie miał miesięcznego limitu wydatków.',
    confirmLabel: 'Usuń limit',
    onConfirm: () => save({ monthlyLimit: null, warningThreshold: null })
  })

const onCancel = () => {
  reset()
  form.value?.clear()
}
</script>

<template>
  <WalletSettingsCard>
    <UForm
      id="limit-form"
      ref="form"
      :schema="limitSchema"
      :state
      :disabled="loading"
      class="@container flex flex-col gap-4"
      @submit="onSubmit"
    >
      <div class="grid gap-4 @md:grid-cols-2">
        <UFormField name="monthlyLimit" label="Kwota limitu (zł)" size="lg">
          <UInputNumber
            v-model="state.monthlyLimit"
            :min="0"
            :max="VALIDATION.AMOUNT_MAX"
            :increment="false"
            :decrement="false"
            locale="pl-PL"
            :format-options="{ minimumFractionDigits: 2, useGrouping: 'always' }"
            class="w-full"
          />
        </UFormField>

        <UFormField
          name="warningThreshold"
          label="Próg ostrzeżenia (%, opcjonalnie)"
          :help="thresholdHelp"
          size="lg"
        >
          <UInputNumber
            v-model="state.warningThreshold"
            :min="VALIDATION.WARNING_THRESHOLD_MIN"
            :max="VALIDATION.WARNING_THRESHOLD_MAX"
            :increment="false"
            :decrement="false"
            locale="pl-PL"
            class="w-full"
          />
        </UFormField>
      </div>

      <WalletLimitBar :limit="preview" />
    </UForm>

    <template #footer>
      <UButton
        color="error"
        variant="ghost"
        label="Usuń limit"
        :disabled="loading || !hasLimit"
        class="mr-auto"
        @click="onRemove"
      />
      <WalletSettingsFormActions
        form-id="limit-form"
        submit-label="Zapisz limit"
        :loading
        :can-save
        :can-cancel="canSave"
        @cancel="onCancel"
      />
    </template>
  </WalletSettingsCard>
</template>

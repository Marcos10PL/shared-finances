<script setup lang="ts">
import type { FormSubmitEvent, SelectMenuItem } from '@nuxt/ui'
import { transactionSchema, type TransactionInput } from '~/schemas/transaction'
import type { Transaction } from '~/types'

const {
  walletName,
  transaction = undefined,
  authorName = undefined
} = defineProps<{
  walletName: string
  transaction?: Transaction
  authorName?: string
}>()

const emit = defineEmits<{
  close: []
}>()

const { user } = useAuth()
const { categories } = useCategories()
const { addTransaction, updateTransaction } = useTransactions()
const { showSuccess, showError } = useToasts()

const isEdit = computed(() => transaction !== undefined)

const state = reactive<Partial<TransactionInput>>({
  type: transaction?.type ?? CATEGORY_TYPES.EXPENSE,
  name: transaction?.name ?? '',
  amount: transaction?.amount,
  categoryId: transaction?.categoryId,
  date: transaction?.date ?? toDateKey(new Date()),
  description: transaction?.description ?? ''
})

const loading = ref(false)
const form = useTemplateRef('form')

const type = computed({
  get: () => state.type ?? CATEGORY_TYPES.EXPENSE,
  set: (value: TransactionInput['type'] | null) => {
    if (!value || state.type === value) return
    state.type = value
    state.categoryId = undefined
  }
})

const categoryItems = computed<SelectMenuItem[][]>(() => {
  const ofType = categories.value.filter((category) => category.type === state.type)
  const toItem = (category: (typeof ofType)[number]): SelectMenuItem => ({
    label: category.name,
    value: category.id
  })
  const defaults = ofType.filter((category) => category.userId === null).map(toItem)
  const own = ofType.filter((category) => category.userId !== null).map(toItem)

  return [
    ...(own.length ? [[{ type: 'label' as const, label: 'Moje kategorie' }, ...own]] : []),
    ...(defaults.length ? [[{ type: 'label' as const, label: 'Domyślne' }, ...defaults]] : [])
  ]
})

const save = async (input: TransactionInput) => {
  if (transaction) return updateTransaction(transaction.id, input)

  if (user.value) return addTransaction(input, user.value.id)
}

const addAnother = ref(false)

async function onSubmit(event: FormSubmitEvent<TransactionInput>) {
  const keepOpen = addAnother.value
  addAnother.value = false
  loading.value = true

  try {
    await save(event.data)
    showSuccess(isEdit.value ? 'Zapisano zmiany.' : 'Dodano transakcję.')

    if (keepOpen) {
      state.name = ''
      state.amount = undefined
      state.description = ''
      form.value?.clear()
      loading.value = false
      nextTick(() => document.querySelector<HTMLInputElement>('#transaction-form input')?.focus())
    } else emit('close')
  } catch {
    showError('Nie udało się zapisać transakcji.')
    loading.value = false
  }
}
</script>

<template>
  <UModal
    :title="isEdit ? 'Edytuj transakcję' : 'Nowa transakcja'"
    :description="walletName"
    :dismissible="!loading"
    :close="loading ? false : { onClick: () => emit('close') }"
    @update:open="(open) => !open && !loading && emit('close')"
  >
    <template #body>
      <UForm
        id="transaction-form"
        ref="form"
        :schema="transactionSchema"
        :state
        :disabled="loading"
        class="flex flex-col gap-4"
        @submit="onSubmit"
        @error="addAnother = false"
      >
        <UiInput
          v-model="state.name"
          name="name"
          label="Nazwa"
          required
          size="lg"
          :max-length="VALIDATION.TRANSACTION_NAME_MAX_LENGTH"
        />

        <TypeTabs v-model="type" />

        <UFormField name="amount" label="Kwota" required size="lg">
          <div class="relative">
            <UInputNumber
              v-model="state.amount"
              :min="0"
              :max="VALIDATION.AMOUNT_MAX"
              :increment="false"
              :decrement="false"
              locale="pl-PL"
              :format-options="{ minimumFractionDigits: 2, useGrouping: 'always' }"
              size="xl"
              class="w-full"
              :ui="{ base: 'py-3 text-2xl font-semibold' }"
            />
            <span
              class="pointer-events-none absolute inset-y-0 right-3 flex items-center text-muted"
            >
              zł
            </span>
          </div>
        </UFormField>

        <UFormField name="description" label="Opis (opcjonalnie)" size="lg">
          <UTextarea
            v-model="state.description"
            placeholder="Opcjonalnie, np. chleb, mleko, warzywa"
            :rows="3"
            autoresize
            :maxrows="6"
            :maxlength="VALIDATION.TRANSACTION_DESCRIPTION_MAX_LENGTH"
            class="w-full"
          />
        </UFormField>

        <div class="grid gap-4 sm:grid-cols-2">
          <UFormField name="categoryId" label="Kategoria" required size="lg">
            <USelectMenu
              v-model="state.categoryId"
              :items="categoryItems"
              value-key="value"
              placeholder="Wybierz kategorię"
              :search-input="{ placeholder: 'Szukaj kategorii' }"
              class="w-full"
            >
              <template #empty>Brak kategorii</template>
            </USelectMenu>
          </UFormField>

          <UFormField name="date" label="Data" required size="lg">
            <UiDatePicker v-model="state.date" />
          </UFormField>
        </div>

        <p class="text-xs text-muted">
          <template v-if="transaction">
            Autor: <template v-if="authorName">{{ authorName }}</template><em v-else
              >Członek usunięty</em
            >, data utworzenia:
            {{ formatDate(transaction.createdAt) }}
          </template>
          <template v-else>Transakcja zostanie przypisana do Ciebie.</template>
        </p>
      </UForm>
    </template>

    <template #footer>
      <div class="grid w-full grid-cols-2 gap-2 *:justify-center sm:flex sm:justify-end">
        <UButton
          color="neutral"
          variant="outline"
          label="Anuluj"
          :disabled="loading"
          @click="emit('close')"
        />
        <UButton
          v-if="!isEdit"
          type="submit"
          form="transaction-form"
          color="neutral"
          variant="outline"
          label="Zapisz i dodaj kolejną"
          class="max-sm:order-first max-sm:col-span-2 max-sm:justify-center"
          :disabled="loading"
          @click="addAnother = true"
        />
        <UButton
          type="submit"
          form="transaction-form"
          :label="isEdit ? 'Zapisz zmiany' : 'Zapisz'"
          :loading
          @click="addAnother = false"
        />
      </div>
    </template>
  </UModal>
</template>

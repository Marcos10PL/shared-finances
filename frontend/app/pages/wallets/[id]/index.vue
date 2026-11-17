<script setup lang="ts">
import type { Transaction } from '~/types'

const { loaded: walletsLoaded } = useWallets()
const wallet = useCurrentWallet()
const { categories, loaded: categoriesLoaded, fetchCategories } = useCategories()
const {
  page: transactionPage,
  summary,
  pageLoaded,
  summaryLoaded,
  init,
  removeTransaction
} = useTransactions()
const { user } = useAuth()
const { openTransaction, confirm } = useModals()
const { showSuccess, showError } = useToasts()

const title = computed(() => wallet.value?.name ?? 'Portfel')
const breadcrumb = computed(() => [{ label: 'Portfele', to: ROUTES.HOME }, { label: title.value }])

useHead({ title })

const TABS = [
  { label: 'Transakcje', value: 'transactions' },
  { label: 'Statystyki', value: 'stats' }
]
const tab = ref('transactions')

const currentMonth = getMonthKey(new Date())

const isOwner = computed(() => wallet.value?.role === WALLET_ROLES.OWNER)
const canAdd = computed(() => wallet.value && wallet.value.role !== WALLET_ROLES.GUEST)

watch(
  () => wallet.value?.id,
  (id) => {
    if (id === undefined) return
    init(wallet.value!.members.map((member) => member.id))
    if (!categoriesLoaded.value) fetchCategories()
  },
  { immediate: true }
)

watchEffect(() => {
  if (walletsLoaded.value && !wallet.value) navigateTo(ROUTES.HOME)
})

const ready = computed(() => walletsLoaded.value && categoriesLoaded.value && !!wallet.value)

const { month, filters, sort, page } = useTransactionQuery(
  ready,
  computed(() => wallet.value?.id)
)

const totals = computed(() => ({
  balance: summary.value?.balance ?? 0,
  income: summary.value?.income ?? 0,
  expenses: summary.value?.expenses ?? 0
}))

const limit = computed(() =>
  wallet.value &&
  summary.value &&
  wallet.value.monthlyLimit !== null &&
  month.value === currentMonth
    ? {
        monthlyLimit: wallet.value.monthlyLimit,
        warningThreshold: wallet.value.warningThreshold,
        monthlyExpenses: totals.value.expenses
      }
    : undefined
)

const canManage = (transaction: Transaction) =>
  isOwner.value ||
  (wallet.value?.role === WALLET_ROLES.MEMBER && transaction.authorId === user.value?.id)

const onEdit = (transaction: Transaction) =>
  openTransaction({
    transaction,
    walletName: wallet.value?.name ?? '',
    authorName: wallet.value?.members.find((member) => member.id === transaction.authorId)
      ?.displayName
  })

const onRemove = (transaction: Transaction) =>
  confirm({
    title: `Usunąć transakcję „${transaction.name}”?`,
    description: 'Tej operacji nie można cofnąć.',
    confirmLabel: 'Usuń transakcję',
    onConfirm: async () => {
      try {
        await removeTransaction(transaction.id)
        showSuccess('Usunięto transakcję.')
      } catch {
        showError('Nie udało się usunąć transakcji.')
      }
    }
  })
</script>

<template>
  <AppPage :title :breadcrumb>
    <template #title-suffix>
      <WalletRoleBadge v-if="wallet" :role="wallet.role" />
      <USkeleton v-else class="h-5 w-20 rounded-full" />
    </template>

    <template #right>
      <div class="hidden xl:block">
        <WalletMonthSwitcher v-model="month" />
      </div>

      <template v-if="wallet">
        <UTooltip v-if="isOwner" text="Ustawienia portfela">
          <UButton
            color="neutral"
            variant="outline"
            icon="i-lucide-settings"
            aria-label="Ustawienia portfela"
            :to="ROUTES.WALLET_SETTINGS(wallet.id)"
          />
        </UTooltip>

        <UButton
          v-if="canAdd"
          icon="i-lucide-plus"
          label="Dodaj transakcję"
          class="hidden lg:inline-flex"
          @click="openTransaction({ walletName: wallet.name })"
        />
      </template>

      <template v-else>
        <USkeleton class="size-8 rounded-md" />
        <USkeleton class="hidden h-8 w-40 rounded-md lg:block" />
      </template>
    </template>

    <div v-if="wallet" class="flex w-full flex-col gap-4">
      <div class="xl:hidden">
        <WalletMonthSwitcher v-model="month" class="w-full" />
      </div>

      <WalletLimitAlert v-if="limit" :limit :wallet-id="wallet.id" :is-owner="isOwner" />

      <WalletStats :totals :limit :loading="!summaryLoaded && !summary" />

      <UTabs v-model="tab" :items="TABS" :content="false" variant="pill" class="2xl:hidden" />

      <div class="grid items-start gap-4 2xl:grid-cols-3">
        <div class="2xl:col-span-2" :class="tab !== 'transactions' && 'max-2xl:hidden'">
          <WalletTransactions
            v-model:filters="filters"
            v-model:sort="sort"
            v-model:page="page"
            :items="transactionPage.items"
            :total="transactionPage.total"
            :categories
            :members="wallet.members"
            :loading="!pageLoaded"
            :can-manage
            @edit="onEdit"
            @remove="onRemove"
          />
        </div>

        <div class="flex flex-col gap-4" :class="tab !== 'stats' && 'max-2xl:hidden'">
          <WalletContributions
            :contributions="summary?.contributions ?? []"
            :members="wallet.members"
            :loading="!summary"
          />
          <WalletCategoryBreakdown :rows="summary?.categories ?? []" :loading="!summary" />
        </div>
      </div>
    </div>

    <template v-if="canAdd && wallet" #footer>
      <div class="border-t border-default bg-default p-4 lg:hidden">
        <UButton
          icon="i-lucide-plus"
          label="Dodaj transakcję"
          size="lg"
          block
          @click="openTransaction({ walletName: wallet.name })"
        />
      </div>
    </template>
  </AppPage>
</template>

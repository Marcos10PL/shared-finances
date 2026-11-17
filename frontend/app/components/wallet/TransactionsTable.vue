<script setup lang="ts">
import type { DropdownMenuItem } from '@nuxt/ui'
import type { Category, Transaction, WalletMember } from '~/types'
const { items, categories, members, loading, canManage, emptyText } = defineProps<{
  items: Transaction[]
  categories: Category[]
  members: WalletMember[]
  loading: boolean
  canManage: (transaction: Transaction) => boolean
  emptyText: string
}>()

const emit = defineEmits<{
  edit: [transaction: Transaction]
  remove: [transaction: Transaction]
}>()

const categoryById = computed(() => new Map(categories.map((category) => [category.id, category])))
const memberById = computed(() => new Map(members.map((member) => [member.id, member])))

const rows = computed<
  {
    transaction: Transaction
    category: Category | undefined
    author: WalletMember | undefined
    amount: string
    amountClass: string
    actions: DropdownMenuItem[] | null
  }[]
>(() =>
  items.map((transaction) => {
    const isIncome = transaction.type === CATEGORY_TYPES.INCOME

    return {
      transaction,
      category: categoryById.value.get(transaction.categoryId),
      author: memberById.value.get(transaction.authorId),
      amount: formatSignedCurrency(isIncome ? transaction.amount : -transaction.amount),
      amountClass: isIncome ? 'text-income' : 'text-expense',
      actions: canManage(transaction)
        ? [
            {
              label: 'Edytuj',
              icon: 'i-lucide-pencil',
              onSelect: () => emit('edit', transaction)
            },
            {
              label: 'Usuń',
              icon: 'i-lucide-trash-2',
              color: 'error',
              onSelect: () => emit('remove', transaction)
            }
          ]
        : null
    }
  })
)
const hasActions = computed(() => rows.value.some((row) => row.actions))

const isEmpty = computed(() => !loading && items.length === 0)

const ROW_CLASS = 'h-16 *:px-4 *:py-3 *:shadow-[inset_0_1px_0_var(--ui-border)]'
</script>

<template>
  <div class="overflow-x-auto border-t border-default">
    <table class="w-full min-w-196 table-fixed text-sm">
      <colgroup>
        <col />
        <col class="w-40" />
        <col class="w-20" />
        <col class="w-36" />
        <col class="w-36" />
        <col v-if="hasActions" class="w-14" />
      </colgroup>

      <thead class="bg-muted text-xs text-muted">
        <tr class="*:px-4 *:py-2 *:text-left *:font-normal *:whitespace-nowrap">
          <th>Nazwa</th>
          <th>Kategoria</th>
          <th>Autor</th>
          <th>Data</th>
          <th class="text-right!">Kwota</th>
          <th v-if="hasActions"><span class="sr-only">Akcje</span></th>
        </tr>
      </thead>

      <tbody v-if="loading">
        <tr v-for="index in TRANSACTIONS_PAGE_SIZE" :key="index" :class="ROW_CLASS">
          <td>
            <div class="flex items-center gap-3">
              <USkeleton class="size-8 shrink-0 rounded-md" />
              <USkeleton class="h-5 w-40" />
            </div>
          </td>
          <td><USkeleton class="h-5 w-20 rounded-full" /></td>
          <td><USkeleton class="size-6 rounded-full" /></td>
          <td><USkeleton class="h-5 w-20" /></td>
          <td><USkeleton class="ml-auto h-5 w-20" /></td>
          <td v-if="hasActions" />
        </tr>
      </tbody>

      <tbody v-else>
        <tr v-for="row in rows" :key="row.transaction.id" :class="ROW_CLASS">
          <td>
            <div class="flex min-w-0 items-center gap-3">
              <CategoryTypeIcon :type="row.transaction.type" />
              <div class="flex min-w-0 items-center gap-1">
                <span
                  class="line-clamp-2 min-w-0 font-medium wrap-break-word text-highlighted"
                  :title="row.transaction.name"
                >
                  {{ row.transaction.name }}
                </span>
                <UiInfoPopover
                  v-if="row.transaction.description"
                  :text="row.transaction.description"
                />
              </div>
            </div>
          </td>

          <td>
            <CategoryBadge v-if="row.category" :category="row.category" />
          </td>

          <td>
            <UPopover :content="{ side: 'top' }">
              <button
                type="button"
                class="cursor-pointer rounded-full"
                :aria-label="`Autor: ${row.author?.displayName ?? 'Członek usunięty'}`"
              >
                <UiUserAvatar
                  v-if="row.author"
                  :user-id="row.author.id"
                  :name="row.author.displayName"
                  size="xs"
                />
                <UAvatar v-else icon="i-lucide-user-x" size="xs" />
              </button>

              <template #content>
                <p class="px-3 py-2 text-sm text-default">
                  <template v-if="row.author">{{ row.author.displayName }}</template>
                  <em v-else class="text-muted">Członek usunięty</em>
                </p>
              </template>
            </UPopover>
          </td>

          <td class="whitespace-nowrap text-muted">{{ formatDate(row.transaction.date) }}</td>

          <td
            class="text-right font-medium whitespace-nowrap tabular-nums"
            :class="row.amountClass"
          >
            {{ row.amount }}
          </td>

          <td v-if="hasActions" class="text-right">
            <UDropdownMenu v-if="row.actions" :items="row.actions" :content="{ align: 'end' }">
              <UButton
                color="neutral"
                variant="ghost"
                icon="i-lucide-ellipsis"
                :aria-label="`Akcje: ${row.transaction.name}`"
              />
            </UDropdownMenu>
          </td>
        </tr>
      </tbody>
    </table>
  </div>

  <div
    v-if="isEmpty"
    class="flex flex-col items-center justify-center gap-2 border-t border-default px-4 py-12 text-center"
  >
    <UIcon name="i-lucide-receipt-text" class="size-8 text-dimmed" />
    <p class="text-sm text-muted">{{ emptyText }}</p>
  </div>
</template>

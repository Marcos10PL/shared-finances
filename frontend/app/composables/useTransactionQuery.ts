import type { Ref } from 'vue'
import type { TransactionFilters, TransactionSort } from '~/types'

export const useTransactionQuery = (ready: Ref<boolean>, walletId: Ref<number | undefined>) => {
  const { page: result, fetchPage, fetchSummary } = useTransactions()

  const month = ref(getMonthKey(new Date()))
  const filters = ref<TransactionFilters>(createEmptyFilters())
  const sort = ref<TransactionSort>(TRANSACTION_SORTS.NEWEST)
  const page = ref(1)

  watch(walletId, () => (filters.value = createEmptyFilters()), { flush: 'sync' })

  watch([month, filters, sort], () => (page.value = 1), { flush: 'sync' })

  watch(
    [ready, walletId, month, filters, sort, page],
    ([isReady]) => {
      if (isReady)
        fetchPage({
          month: month.value,
          filters: filters.value,
          sort: sort.value,
          page: page.value
        })
    },
    { immediate: true }
  )

  watch(
    [ready, walletId, month],
    ([isReady]) => {
      if (isReady) fetchSummary(month.value)
    },
    { immediate: true }
  )

  watch(
    () => result.value.total,
    (total) => {
      const lastPage = getPageCount(total)
      if (page.value > lastPage) page.value = lastPage
    }
  )

  return { month, filters, sort, page }
}

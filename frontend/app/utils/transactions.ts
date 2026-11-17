import type { Transaction, TransactionFilters } from '~/types'

export const createEmptyFilters = (): TransactionFilters => ({
  type: null,
  categoryIds: [],
  authorIds: []
})

// TODO: MOCK
export const filterTransactions = (transactions: Transaction[], filters: TransactionFilters) =>
  transactions.filter(
    (item) =>
      (filters.type === null || item.type === filters.type) &&
      (filters.categoryIds.length === 0 || filters.categoryIds.includes(item.categoryId)) &&
      (filters.authorIds.length === 0 || filters.authorIds.includes(item.authorId))
  )

export const countFilters = (filters: TransactionFilters) =>
  (filters.type === null ? 0 : 1) +
  (filters.categoryIds.length > 0 ? 1 : 0) +
  (filters.authorIds.length > 0 ? 1 : 0)

export const getPageCount = (total: number) =>
  Math.max(Math.ceil(total / TRANSACTIONS_PAGE_SIZE), 1)

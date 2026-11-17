import type {
  Category,
  CategoryShare,
  MonthSummary,
  Transaction,
  TransactionPage,
  TransactionQuery,
  TransactionSort
} from '~/types'
import type { TransactionInput } from '~/schemas/transaction'

// TODO: MOCK
const wait = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms))

// TODO: MOCK
const COMPARATORS: Record<TransactionSort, (a: Transaction, b: Transaction) => number> = {
  [TRANSACTION_SORTS.NEWEST]: (a, b) => b.date.localeCompare(a.date) || b.id - a.id,
  [TRANSACTION_SORTS.OLDEST]: (a, b) => a.date.localeCompare(b.date) || a.id - b.id,
  [TRANSACTION_SORTS.AMOUNT_DESC]: (a, b) => b.amount - a.amount,
  [TRANSACTION_SORTS.AMOUNT_ASC]: (a, b) => a.amount - b.amount
}

// TODO: MOCK
const round = (value: number) => Math.round(value * 100) / 100

// TODO: MOCK
const getTotals = (transactions: Transaction[]) => {
  let income = 0
  let expenses = 0

  for (const transaction of transactions) {
    if (transaction.type === CATEGORY_TYPES.INCOME) income += transaction.amount
    else expenses += transaction.amount
  }

  return { income: round(income), expenses: round(expenses), balance: round(income - expenses) }
}

// TODO: MOCK
const getCategoryBreakdown = (
  transactions: Transaction[],
  categories: Category[],
  limit = 6
): CategoryShare[] => {
  const amounts = new Map<number, number>()

  for (const transaction of transactions) {
    if (transaction.type !== CATEGORY_TYPES.EXPENSE) continue
    amounts.set(
      transaction.categoryId,
      (amounts.get(transaction.categoryId) ?? 0) + transaction.amount
    )
  }

  const total = [...amounts.values()].reduce((sum, amount) => sum + amount, 0)
  const share = (amount: number) => (total === 0 ? 0 : (amount / total) * 100)

  const sorted = [...amounts]
    .map(([id, amount]) => ({
      id,
      name: categories.find((category) => category.id === id)?.name ?? 'Inne',
      amount: round(amount),
      percent: share(amount)
    }))
    .sort((a, b) => b.amount - a.amount)

  const rest = sorted.slice(limit)
  if (rest.length === 0) return sorted

  const restAmount = rest.reduce((sum, item) => sum + item.amount, 0)
  return [
    ...sorted.slice(0, limit),
    { id: null, name: 'Pozostałe', amount: round(restAmount), percent: share(restAmount) }
  ]
}

export const useTransactions = () => {
  const { categories } = useCategories()

  // TODO: MOCK
  const database = useState<Transaction[]>('transactions:database', () => [])
  const memberIds = useState<number[]>('transactions:members', () => [])
  const page = useState<TransactionPage>('transactions:page', () => ({ items: [], total: 0 }))
  const summary = useState<MonthSummary | null>('transactions:summary', () => null)
  const pageLoaded = useState('transactions:page-loaded', () => false)
  const summaryLoaded = useState('transactions:summary-loaded', () => false)
  const lastQuery = useState<TransactionQuery | null>('transactions:query', () => null)

  let pageRequest = 0
  let summaryRequest = 0

  // TODO: MOCK
  const init = (ids: number[]) => {
    memberIds.value = ids
    database.value = []
    page.value = { items: [], total: 0 }
    summary.value = null
    pageLoaded.value = false
    summaryLoaded.value = false
  }

  // TODO: MOCK
  const inMonth = (month: string) => database.value.filter((item) => item.date.startsWith(month))

  // TODO: MOCK
  const fetchPage = async (query: TransactionQuery) => {
    const request = ++pageRequest
    lastQuery.value = query
    pageLoaded.value = false

    await wait(300)
    if (request !== pageRequest) return

    const matching = filterTransactions(inMonth(query.month), query.filters).sort(
      COMPARATORS[query.sort]
    )
    const start = (query.page - 1) * TRANSACTIONS_PAGE_SIZE
    page.value = {
      items: matching.slice(start, start + TRANSACTIONS_PAGE_SIZE),
      total: matching.length
    }
    pageLoaded.value = true
  }

  // TODO: MOCK
  const fetchSummary = async (month: string) => {
    const request = ++summaryRequest
    summaryLoaded.value = false

    await wait(300)
    if (request !== summaryRequest) return

    const transactions = inMonth(month)
    summary.value = {
      ...getTotals(transactions),
      contributions: memberIds.value
        .map((memberId) => ({
          memberId,
          income: getTotals(transactions.filter((item) => item.authorId === memberId)).income,
          expenses: getTotals(transactions.filter((item) => item.authorId === memberId)).expenses
        }))
        .sort((a, b) => b.expenses - a.expenses),
      categories: getCategoryBreakdown(transactions, categories.value)
    }
    summaryLoaded.value = true
  }

  // TODO: MOCK
  const toTransaction = (input: TransactionInput) => ({
    ...input,
    description: input.description || null
  })

  const refresh = async () => {
    if (!lastQuery.value) return
    await Promise.all([fetchPage(lastQuery.value), fetchSummary(lastQuery.value.month)])
  }

  // TODO: MOCK
  const addTransaction = async (input: TransactionInput, authorId: number) => {
    await wait(300)
    database.value.push({
      id: Date.now(),
      ...toTransaction(input),
      authorId,
      createdAt: new Date().toISOString()
    })
    await refresh()
  }

  // TODO: MOCK
  const updateTransaction = async (id: number, input: TransactionInput) => {
    await wait(300)
    const index = database.value.findIndex((item) => item.id === id)
    const current = database.value[index]
    if (current) database.value[index] = { ...current, ...toTransaction(input) }
    await refresh()
  }

  // TODO: MOCK
  const removeTransaction = async (id: number) => {
    await wait(300)
    database.value = database.value.filter((item) => item.id !== id)
    await refresh()
  }

  const reset = () => {
    database.value = []
    memberIds.value = []
    page.value = { items: [], total: 0 }
    summary.value = null
    pageLoaded.value = false
    summaryLoaded.value = false
    lastQuery.value = null
  }

  return {
    page,
    summary,
    pageLoaded,
    summaryLoaded,
    init,
    fetchPage,
    fetchSummary,
    addTransaction,
    updateTransaction,
    removeTransaction,
    reset
  }
}

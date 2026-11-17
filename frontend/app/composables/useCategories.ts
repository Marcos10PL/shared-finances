import type { Category, CategoryType, ErrorCode } from '~/types'
import type { CategoryInput } from '~/schemas/category'

// TODO: MOCK
const wait = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms))

// TODO: MOCK
const mockApiError = (code: ErrorCode) =>
  Object.assign(new Error(code), { data: { message: code } })

// TODO: MOCK
const defaultCategory = (id: number, name: string, type: CategoryType): Category => ({
  id,
  name,
  type,
  userId: null
})

// TODO: MOCK
const mockCategories = (userId: number): Category[] => [
  defaultCategory(1, 'Jedzenie', CATEGORY_TYPES.EXPENSE),
  defaultCategory(2, 'Rachunki', CATEGORY_TYPES.EXPENSE),
  defaultCategory(3, 'Transport', CATEGORY_TYPES.EXPENSE),
  defaultCategory(4, 'Zdrowie', CATEGORY_TYPES.EXPENSE),
  defaultCategory(5, 'Ubrania', CATEGORY_TYPES.EXPENSE),
  defaultCategory(6, 'Edukacja', CATEGORY_TYPES.EXPENSE),
  defaultCategory(7, 'Inne wydatki', CATEGORY_TYPES.EXPENSE),
  defaultCategory(8, 'Wynagrodzenie', CATEGORY_TYPES.INCOME),
  defaultCategory(9, 'Zwrot', CATEGORY_TYPES.INCOME),
  defaultCategory(10, 'Inne przychody', CATEGORY_TYPES.INCOME),
  { id: 11, name: 'Chemia i dom', type: CATEGORY_TYPES.EXPENSE, userId },
  { id: 12, name: 'Rozrywka', type: CATEGORY_TYPES.EXPENSE, userId },
  { id: 13, name: 'Prezenty', type: CATEGORY_TYPES.EXPENSE, userId },
  { id: 14, name: 'Wpłata', type: CATEGORY_TYPES.INCOME, userId }
]

// TODO: MOCK
const USED_CATEGORY_IDS = [12]

export const useCategories = () => {
  const { user } = useAuth()
  const categories = useState<Category[]>('categories', () => [])
  const loaded = useState('categories:loaded', () => false)

  // TODO: MOCK
  const fetchCategories = async () => {
    await wait(300)
    categories.value = mockCategories(user.value?.id ?? 0)
    loaded.value = true
  }

  // TODO: MOCK
  const addCategory = async (input: CategoryInput) => {
    await wait(300)
    categories.value.push({ id: Date.now(), ...input, userId: user.value?.id ?? 0 })
  }

  // TODO: MOCK
  const renameCategory = async (id: number, name: string) => {
    await wait(300)
    const category = categories.value.find((item) => item.id === id)
    if (category) category.name = name
  }

  // TODO: MOCK
  const removeCategory = async (id: number) => {
    await wait(300)
    if (USED_CATEGORY_IDS.includes(id)) throw mockApiError(ERROR_CODES.CATEGORY_IN_USE)

    categories.value = categories.value.filter((category) => category.id !== id)
  }

  const reset = () => {
    categories.value = []
    loaded.value = false
  }

  return {
    categories,
    loaded,

    fetchCategories,
    addCategory,
    renameCategory,
    removeCategory,
    reset
  }
}

import type { Category } from '~/types'
import type { CategoryInput } from '~/schemas/category'

export const useCategories = () => {
  const api = useApi()
  const categories = useState<Category[]>('categories', () => [])
  const loaded = useState('categories:loaded', () => false)
  const error = useState('categories:error', () => false)

  const fetchCategories = async () => {
    error.value = false

    try {
      categories.value = await api<Category[]>('/api/categories')
      loaded.value = true
    } catch {
      error.value = true
    }
  }

  const addCategory = async (input: CategoryInput) => {
    categories.value.push(await api<Category>('/api/categories', { method: 'POST', body: input }))
  }

  const renameCategory = async (id: number, name: string) => {
    const updated = await api<Category>(`/api/categories/${id}`, {
      method: 'PUT',
      body: { name }
    })
    const category = categories.value.find((item) => item.id === id)
    if (category) Object.assign(category, updated)
  }

  const removeCategory = async (id: number) => {
    await api(`/api/categories/${id}`, { method: 'DELETE' })
    categories.value = categories.value.filter((category) => category.id !== id)
  }

  const reset = () => {
    categories.value = []
    loaded.value = false
    error.value = false
  }

  return {
    categories,
    loaded,
    error,

    fetchCategories,
    addCategory,
    renameCategory,
    removeCategory,
    reset
  }
}

const currencyFormatter = new Intl.NumberFormat('pl-PL', {
  style: 'currency',
  currency: 'PLN',
  useGrouping: 'always'
})
const percentFormatter = new Intl.NumberFormat('pl-PL', { maximumFractionDigits: 1 })
const dateFormatter = new Intl.DateTimeFormat('pl-PL', {
  day: 'numeric',
  month: 'short',
  year: 'numeric'
})

export const formatCurrency = (value: number) => currencyFormatter.format(value)

export const formatPercent = (value: number) => `${percentFormatter.format(value)}%`

export const formatDate = (value: string | Date) => dateFormatter.format(new Date(value))

export const getInitials = (name: string) =>
  name
    .split(/[\s@.]+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part.charAt(0).toUpperCase())
    .join('')

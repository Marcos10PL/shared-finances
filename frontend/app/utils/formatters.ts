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

const monthFormatter = new Intl.DateTimeFormat('pl-PL', { month: 'long', year: 'numeric' })

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

const capitalize = (text: string) => text.charAt(0).toUpperCase() + text.slice(1)

export const formatMonth = (month: string) =>
  capitalize(monthFormatter.format(new Date(`${month}-01T12:00:00`)))

const shortMonthFormatter = new Intl.DateTimeFormat('pl-PL', { month: 'short' })
export const SHORT_MONTH_LABELS = Array.from({ length: 12 }, (_, index) =>
  capitalize(shortMonthFormatter.format(new Date(2026, index, 1)).replace('.', ''))
)

export const formatSignedCurrency = (value: number) =>
  value === 0 ? formatCurrency(0) : `${value < 0 ? '−' : '+'}${formatCurrency(Math.abs(value))}`

export const getMonthKey = (date: Date) =>
  `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`

export const shiftMonth = (month: string, delta: number) => {
  const [year, number] = month.split('-').map(Number)
  return getMonthKey(new Date(year!, number! - 1 + delta, 1))
}

export const toDateKey = (date: Date) =>
  `${getMonthKey(date)}-${String(date.getDate()).padStart(2, '0')}`

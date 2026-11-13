const currencyFormatter = new Intl.NumberFormat('pl-PL', {
  style: 'currency',
  currency: 'PLN',
  useGrouping: 'always'
})
const percentFormatter = new Intl.NumberFormat('pl-PL', { maximumFractionDigits: 1 })

export const formatCurrency = (value: number) => currencyFormatter.format(value)

export const formatPercent = (value: number) => `${percentFormatter.format(value)}%`

export const getInitials = (name: string) =>
  name
    .split(/[\s@.]+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part.charAt(0).toUpperCase())
    .join('')

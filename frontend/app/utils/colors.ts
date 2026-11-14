const CHART_COLORS = [
  { bg: 'bg-chart-1', initials: 'text-white dark:text-zinc-900' },
  { bg: 'bg-chart-2', initials: 'text-zinc-900' },
  { bg: 'bg-chart-3', initials: 'text-white dark:text-zinc-900' },
  { bg: 'bg-chart-4', initials: 'text-white dark:text-zinc-900' },
  { bg: 'bg-chart-5', initials: 'text-white dark:text-zinc-900' }
] as const

export const getChartColor = (index: number) => CHART_COLORS[index % CHART_COLORS.length]!

export const getChartColorById = (id: number) => getChartColor(id)

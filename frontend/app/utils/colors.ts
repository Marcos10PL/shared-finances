const CHART_COLORS = [
  {
    bg: 'bg-chart-1',
    initials: 'text-white dark:text-zinc-900',
    badge: 'bg-chart-1/10 text-chart-1 ring-chart-1/30'
  },
  {
    bg: 'bg-chart-2',
    initials: 'text-zinc-900',
    badge: 'bg-chart-2/10 text-amber-700 dark:text-chart-2 ring-chart-2/30'
  },
  {
    bg: 'bg-chart-3',
    initials: 'text-white dark:text-zinc-900',
    badge: 'bg-chart-3/10 text-chart-3 ring-chart-3/30'
  },
  {
    bg: 'bg-chart-4',
    initials: 'text-white dark:text-zinc-900',
    badge: 'bg-chart-4/10 text-chart-4 ring-chart-4/30'
  },
  {
    bg: 'bg-chart-5',
    initials: 'text-white dark:text-zinc-900',
    badge: 'bg-chart-5/10 text-chart-5 ring-chart-5/30'
  }
] as const

export const getChartColor = (index: number) => CHART_COLORS[index % CHART_COLORS.length]!

export const getChartColorById = (id: number) => getChartColor(id)

import type { LimitState, LimitStatus, WalletLimit } from '~/types'

export const getLimitStatus = (limit: WalletLimit): LimitStatus => {
  const { monthlyLimit, monthlyExpenses, warningThreshold } = limit

  if (!monthlyLimit) return { state: LIMIT_STATES.NONE, percent: 0, overBy: 0 }

  const percent = (monthlyExpenses / monthlyLimit) * 100
  const overBy = Math.max(monthlyExpenses - monthlyLimit, 0)

  let state: LimitState = LIMIT_STATES.OK
  if (overBy > 0) state = LIMIT_STATES.OVER
  else if (warningThreshold !== null && percent >= warningThreshold) state = LIMIT_STATES.WARNING

  return { state, percent, overBy }
}

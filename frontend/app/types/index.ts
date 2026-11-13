import type { LIMIT_STATES, WALLET_ROLES } from '~/utils/const'

export type WalletRole = (typeof WALLET_ROLES)[keyof typeof WALLET_ROLES]
export type LimitState = (typeof LIMIT_STATES)[keyof typeof LIMIT_STATES]

export interface WalletMember {
  id: string
  displayName: string
}

export interface Wallet {
  id: string
  name: string
  role: WalletRole
  balance: number
  monthlyExpenses: number
  monthlyLimit: number | null
  warningThreshold: number | null
  members: WalletMember[]
}

export type LimitStatus = {
  state: LimitState
  percent: number
  overBy: number
}

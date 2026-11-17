import type { CATEGORY_TYPES, ERROR_CODES, LIMIT_STATES, WALLET_ROLES } from '~/utils/const'

export type WalletRole = (typeof WALLET_ROLES)[keyof typeof WALLET_ROLES]
export type LimitState = (typeof LIMIT_STATES)[keyof typeof LIMIT_STATES]
export type AssignableRole = typeof WALLET_ROLES.MEMBER | typeof WALLET_ROLES.GUEST
export type ErrorCode = (typeof ERROR_CODES)[keyof typeof ERROR_CODES]
export type CategoryType = (typeof CATEGORY_TYPES)[keyof typeof CATEGORY_TYPES]

export type LimitSettings = Pick<Wallet, 'monthlyLimit' | 'warningThreshold'>
export type WalletLimit = Pick<Wallet, 'monthlyLimit' | 'monthlyExpenses' | 'warningThreshold'>

export interface WalletMember {
  id: number
  displayName: string
  email: string
  role: WalletRole
}

export interface WalletInvitation {
  id: number
  email: string
  role: AssignableRole
  createdAt: string
}

export interface Wallet {
  id: number
  name: string
  role: WalletRole
  balance: number
  monthlyExpenses: number
  monthlyLimit: number | null
  warningThreshold: number | null
  members: WalletMember[]
  invitations: WalletInvitation[]
}

export interface LimitStatus {
  state: LimitState
  percent: number
  overBy: number
}

export interface AuthUser {
  id: number
  displayName: string
  email: string
}

export interface AuthResponse {
  token: string
  user: AuthUser
}

export interface Category {
  id: number
  name: string
  type: CategoryType
  // null: a default category
  userId: number | null
}

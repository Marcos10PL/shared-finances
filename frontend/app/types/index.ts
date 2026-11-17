import type {
  CATEGORY_TYPES,
  ERROR_CODES,
  INVITATION_ANSWERS,
  LIMIT_STATES,
  TRANSACTION_SORTS,
  WALLET_ROLES
} from '~/utils/const'

export type WalletRole = (typeof WALLET_ROLES)[keyof typeof WALLET_ROLES]
export type LimitState = (typeof LIMIT_STATES)[keyof typeof LIMIT_STATES]
export type AssignableRole = typeof WALLET_ROLES.MEMBER | typeof WALLET_ROLES.GUEST
export type ErrorCode = (typeof ERROR_CODES)[keyof typeof ERROR_CODES]
export type CategoryType = (typeof CATEGORY_TYPES)[keyof typeof CATEGORY_TYPES]
export type InvitationAnswer = (typeof INVITATION_ANSWERS)[keyof typeof INVITATION_ANSWERS]
export type TransactionSort = (typeof TRANSACTION_SORTS)[keyof typeof TRANSACTION_SORTS]

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

export interface Invitation {
  id: number
  // TODO: BACKEND CONFIRM
  ownerId: number
  ownerName: string
  walletId: number
  walletName: string
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

export interface TransactionFilters {
  type: CategoryType | null
  categoryIds: number[]
  authorIds: number[]
}

export interface Transaction {
  id: number
  name: string
  description: string | null
  type: CategoryType
  amount: number
  date: string
  categoryId: number
  authorId: number
  createdAt: string
}

export interface TransactionQuery {
  month: string
  filters: TransactionFilters
  sort: TransactionSort
  page: number
}

export interface TransactionPage {
  items: Transaction[]
  total: number
}

export interface MemberContribution {
  memberId: number
  income: number
  expenses: number
}

export interface CategoryShare {
  id: number | null
  name: string
  amount: number
  percent: number
}

export interface MonthSummary {
  income: number
  expenses: number
  balance: number
  contributions: MemberContribution[]
  categories: CategoryShare[]
}

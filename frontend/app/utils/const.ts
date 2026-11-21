import type { ErrorMessages } from '~/types'

export const ROUTES = {
  HOME: '/',
  LOGIN: '/login',
  REGISTER: '/register',
  INVITATIONS: '/invitations',
  CATEGORIES: '/categories',
  WALLET: (id: number) => `/wallets/${id}`,
  WALLET_SETTINGS: (id: number) => `/wallets/${id}/settings`
} as const

export const WALLET_ROLES = {
  OWNER: 'OWNER',
  MEMBER: 'MEMBER',
  GUEST: 'GUEST'
} as const

export const ASSIGNABLE_ROLES = [WALLET_ROLES.MEMBER, WALLET_ROLES.GUEST] as const

export const LIMIT_STATES = {
  NONE: 'none',
  OK: 'ok',
  WARNING: 'warning',
  OVER: 'over'
} as const

export const LIMIT_STYLES = {
  [LIMIT_STATES.NONE]: { bar: 'primary', label: '' },
  [LIMIT_STATES.OK]: { bar: 'primary', label: 'text-muted' },
  [LIMIT_STATES.WARNING]: { bar: 'warning', label: 'text-warning' },
  [LIMIT_STATES.OVER]: { bar: 'error', label: 'text-error' }
} as const

export const ERROR_CODES = {
  // auth
  EMAIL_TAKEN: 'EMAIL_TAKEN',
  INVALID_CREDENTIALS: 'INVALID_CREDENTIALS',
  USER_NOT_FOUND: 'USER_NOT_FOUND',

  // wallets
  WALLET_NOT_FOUND: 'WALLET_NOT_FOUND',
  NOT_OWNER: 'NOT_OWNER',
  GUEST_READ_ONLY: 'GUEST_READ_ONLY',
  MEMBER_NOT_FOUND: 'MEMBER_NOT_FOUND',
  OWNER_PROTECTED: 'OWNER_PROTECTED',
  OWNER_CANNOT_LEAVE: 'OWNER_CANNOT_LEAVE',

  // invitations
  NO_ACCOUNT: 'NO_ACCOUNT',
  ALREADY_MEMBER: 'ALREADY_MEMBER',
  ALREADY_INVITED: 'ALREADY_INVITED',
  SELF: 'SELF',
  INVALID_ROLE: 'INVALID_ROLE',
  INVITATION_NOT_FOUND: 'INVITATION_NOT_FOUND',
  INVITATION_ALREADY_ANSWERED: 'INVITATION_ALREADY_ANSWERED',

  // categories
  CATEGORY_EXISTS: 'CATEGORY_EXISTS',
  CATEGORY_NOT_FOUND: 'CATEGORY_NOT_FOUND',
  DEFAULT_CATEGORY_READ_ONLY: 'DEFAULT_CATEGORY_READ_ONLY',
  // TODO: BACKEND CONFIRM (waits for the transactions)
  CATEGORY_IN_USE: 'CATEGORY_IN_USE'
} as const

export const INVITE_ERROR_MESSAGES: ErrorMessages = {
  [ERROR_CODES.NO_ACCOUNT]: 'Nie znaleziono konta z tym adresem e-mail.',
  [ERROR_CODES.ALREADY_MEMBER]: 'Ta osoba jest już w tym portfelu.',
  [ERROR_CODES.ALREADY_INVITED]: 'Ta osoba ma już oczekujące zaproszenie.',
  [ERROR_CODES.SELF]: 'Nie możesz zaprosić samego siebie.'
}

export const INVITATION_ERROR_MESSAGES: ErrorMessages = {
  // should not happen, unless answered in another tab
  [ERROR_CODES.INVITATION_NOT_FOUND]: 'Nie znaleziono tego zaproszenia.',
  [ERROR_CODES.INVITATION_ALREADY_ANSWERED]: 'To zaproszenie zostało już obsłużone.',
  [ERROR_CODES.ALREADY_MEMBER]: 'Jesteś już w tym portfelu.'
}

export const MEMBER_ERROR_MESSAGES: ErrorMessages = {
  // the first two should not happen, the interface hides them
  [ERROR_CODES.OWNER_PROTECTED]: 'Nie można zmienić roli ani usunąć właściciela.',
  [ERROR_CODES.OWNER_CANNOT_LEAVE]: 'Właściciel nie może opuścić portfela. Usuń portfel.',
  [ERROR_CODES.MEMBER_NOT_FOUND]: 'Nie znaleziono tej osoby w portfelu.'
}

export const CATEGORY_ERROR_MESSAGES: ErrorMessages = {
  [ERROR_CODES.CATEGORY_EXISTS]: 'Masz już kategorię o tej nazwie.',
  [ERROR_CODES.CATEGORY_IN_USE]: 'Kategoria ma przypisane transakcje.'
}

export const WALLET_ROLE_LABELS = {
  [WALLET_ROLES.OWNER]: 'Właściciel',
  [WALLET_ROLES.MEMBER]: 'Członek',
  [WALLET_ROLES.GUEST]: 'Gość'
} as const

export const INVITATION_ANSWERS = {
  ACCEPT: 'accept',
  DECLINE: 'decline'
} as const

export const CATEGORY_TYPES = {
  EXPENSE: 'EXPENSE',
  INCOME: 'INCOME'
} as const

export const CATEGORY_TYPE_LABELS = {
  [CATEGORY_TYPES.EXPENSE]: 'Wydatek',
  [CATEGORY_TYPES.INCOME]: 'Przychód'
} as const

export const CATEGORY_TYPE_GROUP_LABELS = {
  [CATEGORY_TYPES.EXPENSE]: 'Wydatki',
  [CATEGORY_TYPES.INCOME]: 'Przychody'
} as const

export const TRANSACTION_SORTS = {
  NEWEST: 'newest',
  OLDEST: 'oldest',
  AMOUNT_DESC: 'amountDesc',
  AMOUNT_ASC: 'amountAsc'
} as const

export const TRANSACTION_SORT_LABELS = {
  [TRANSACTION_SORTS.NEWEST]: 'Najnowsze',
  [TRANSACTION_SORTS.OLDEST]: 'Najstarsze',
  [TRANSACTION_SORTS.AMOUNT_DESC]: 'Kwota malejąco',
  [TRANSACTION_SORTS.AMOUNT_ASC]: 'Kwota rosnąco'
} as const

export const TRANSACTIONS_PAGE_SIZE = 12

// TODO: BACKEND CONFIRM
export const VALIDATION = {
  DISPLAY_NAME_MAX_LENGTH: 50,
  EMAIL_MAX_LENGTH: 254,
  PASSWORD_MIN_LENGTH: 8,
  PASSWORD_MAX_LENGTH: 72,
  WALLET_NAME_MAX_LENGTH: 50,
  CATEGORY_NAME_MAX_LENGTH: 50,
  TRANSACTION_NAME_MAX_LENGTH: 100,
  TRANSACTION_DESCRIPTION_MAX_LENGTH: 500,
  AMOUNT_MAX: 9_999_999.99,
  DATE_MIN: '2000-01-01',
  WARNING_THRESHOLD_MIN: 1,
  WARNING_THRESHOLD_MAX: 100
} as const

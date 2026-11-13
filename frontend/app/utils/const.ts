export const ROUTES = {
  HOME: '/',
  LOGIN: '/login',
  REGISTER: '/register',
  INVITATIONS: '/invitations',
  CATEGORIES: '/categories',
  WALLET: (id: string) => `/wallets/${id}`,
  WALLET_SETTINGS: (id: string) => `/wallets/${id}/settings`
} as const

// TODO: BACKEND CONFIRM
export const WALLET_ROLES = {
  OWNER: 'owner',
  MEMBER: 'member',
  GUEST: 'guest'
} as const

export const LIMIT_STATES = {
  NONE: 'none',
  OK: 'ok',
  WARNING: 'warning',
  OVER: 'over'
} as const

export const WALLET_ROLE_LABELS = {
  [WALLET_ROLES.OWNER]: 'Właściciel',
  [WALLET_ROLES.MEMBER]: 'Członek',
  [WALLET_ROLES.GUEST]: 'Gość'
} as const

// TODO: BACKEND CONFIRM
export const VALIDATION = {
  DISPLAY_NAME_MAX_LENGTH: 50,
  EMAIL_MAX_LENGTH: 254,
  PASSWORD_MIN_LENGTH: 8,
  PASSWORD_MAX_LENGTH: 72,
  WALLET_NAME_MAX_LENGTH: 50
} as const

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

export const ASSIGNABLE_ROLES = [WALLET_ROLES.MEMBER, WALLET_ROLES.GUEST] as const

export const LIMIT_STATES = {
  NONE: 'none',
  OK: 'ok',
  WARNING: 'warning',
  OVER: 'over'
} as const

export const INVITE_ERRORS = {
  NO_ACCOUNT: 'NO_ACCOUNT',
  ALREADY_MEMBER: 'ALREADY_MEMBER',
  ALREADY_INVITED: 'ALREADY_INVITED',
  SELF: 'SELF'
} as const

export const INVITE_ERROR_MESSAGES = {
  [INVITE_ERRORS.NO_ACCOUNT]: 'Nie znaleziono konta z tym adresem e-mail.',
  [INVITE_ERRORS.ALREADY_MEMBER]: 'Ta osoba jest już w tym portfelu.',
  [INVITE_ERRORS.ALREADY_INVITED]: 'Ta osoba ma już oczekujące zaproszenie.',
  [INVITE_ERRORS.SELF]: 'Nie możesz zaprosić samego siebie.'
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
  WALLET_NAME_MAX_LENGTH: 50,
  AMOUNT_MAX: 9_999_999.99,
  WARNING_THRESHOLD_MIN: 1,
  WARNING_THRESHOLD_MAX: 100
} as const

export const ROUTES = {
  HOME: '/',
  LOGIN: '/login',
  REGISTER: '/register'
} as const

// TODO: BACKEND CONFIRM
export const VALIDATION = {
  DISPLAY_NAME_MAX_LENGTH: 50,
  EMAIL_MAX_LENGTH: 254,
  PASSWORD_MIN_LENGTH: 8,
  PASSWORD_MAX_LENGTH: 72
} as const

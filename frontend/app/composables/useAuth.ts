import type { LoginInput, RegisterInput } from '~/schemas/auth'

export type AuthErrorCode = 'INVALID_CREDENTIALS' | 'EMAIL_TAKEN'

export interface AuthUser {
  id: string
  displayName: string
  email: string
}

// TODO: MOCK
export const getAuthErrorCode = (error: unknown) => (error as { code?: AuthErrorCode } | null)?.code

const authError = (code: AuthErrorCode) => Object.assign(new Error(code), { code })

// Simulates the network delay of the mocked API
const wait = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms))

const MOCK_USER_ID = 'u-5'

export const useAuth = () => {
  const user = useState<AuthUser | null>('auth:user', () => null)
  const ready = useState('auth:ready', () => false)
  const session = useCookie<AuthUser | null>('mock-session', { default: () => null })

  const loggedIn = computed(() => user.value !== null)

  const setUser = (value: AuthUser | null) => {
    user.value = value
    session.value = value
  }

  const fetchUser = async () => {
    user.value = session.value?.id ? session.value : null
    ready.value = true
  }

  const login = async (input: LoginInput) => {
    await wait(500)
    if (input.password === 'zle-haslo') throw authError('INVALID_CREDENTIALS')
    setUser({ id: MOCK_USER_ID, displayName: input.email, email: input.email })
  }

  const register = async (input: RegisterInput) => {
    await wait(500)
    if (input.email === 'zajety@example.com') throw authError('EMAIL_TAKEN')
    setUser({ id: MOCK_USER_ID, displayName: input.displayName, email: input.email })
  }

  const logout = () => setUser(null)

  return { user, ready, loggedIn, fetchUser, login, register, logout }
}

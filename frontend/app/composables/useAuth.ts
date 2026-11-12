import type { LoginInput, RegisterInput } from '~/schemas/auth'

export type AuthErrorCode = 'INVALID_CREDENTIALS' | 'EMAIL_TAKEN'

export interface AuthUser {
  displayName: string
  email: string
}

// TODO: MOCK
export const getAuthErrorCode = (error: unknown) => (error as { code?: AuthErrorCode } | null)?.code

const authError = (code: AuthErrorCode) => Object.assign(new Error(code), { code })

const wait = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms))

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
    user.value = session.value
    ready.value = true
  }

  const login = async (input: LoginInput) => {
    await wait(500)
    if (input.password === 'zle-haslo') throw authError('INVALID_CREDENTIALS')
    setUser({ displayName: input.email, email: input.email })
  }

  const register = async (input: RegisterInput) => {
    await wait(500)
    if (input.email === 'zajety@example.com') throw authError('EMAIL_TAKEN')
    setUser({ displayName: input.displayName, email: input.email })
  }

  const logout = () => setUser(null)

  return { user, ready, loggedIn, fetchUser, login, register, logout }
}

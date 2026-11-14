import type { LoginInput, RegisterInput } from '~/schemas/auth'
import type { AuthResponse, AuthUser } from '~/types'

export const useAuth = () => {
  const api = useApi()
  const token = useAuthToken()
  const user = useState<AuthUser | null>('auth:user', () => null)
  const ready = useState('auth:ready', () => false)

  const loggedIn = computed(() => user.value !== null)

  const startSession = (response: AuthResponse) => {
    token.value = response.token
    user.value = response.user
  }

  const fetchUser = async () => {
    if (token.value) {
      try {
        user.value = await api<AuthUser>('/api/auth/me')
      } catch {
        user.value = null
      }
    }

    ready.value = true
  }

  const login = async (input: LoginInput) => {
    startSession(await api<AuthResponse>('/api/auth/login', { method: 'POST', body: input }))
  }

  const register = async (input: RegisterInput) => {
    startSession(
      await api<AuthResponse>('/api/auth/register', {
        method: 'POST',
        body: { displayName: input.displayName, email: input.email, password: input.password }
      })
    )
  }

  const logout = () => {
    token.value = null
    user.value = null
  }

  return { user, ready, loggedIn, fetchUser, login, register, logout }
}

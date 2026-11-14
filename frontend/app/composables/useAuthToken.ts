export const useAuthToken = () =>
  useCookie<string | null>('auth-token', {
    default: () => null,
    maxAge: 60 * 60 * 24,
    sameSite: 'lax'
  })

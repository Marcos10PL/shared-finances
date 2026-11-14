export const useApi = () => {
  const { apiBase } = useRuntimeConfig().public
  const token = useAuthToken()

  return <T>(path: string, options: Parameters<typeof $fetch>[1] = {}) =>
    $fetch<T>(path, {
      baseURL: apiBase,
      ...options,
      headers: {
        ...(token.value ? { Authorization: `Bearer ${token.value}` } : {}),
        ...options.headers
      },
      onResponseError: ({ response }) => {
        if (response.status !== 401 || !token.value) return

        useAuth().logout()
        useToasts().showError('Sesja wygasła. Zaloguj się ponownie.')
      }
    })
}

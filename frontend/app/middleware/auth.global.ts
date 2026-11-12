const PUBLIC_ROUTES: string[] = [ROUTES.LOGIN, ROUTES.REGISTER]

export default defineNuxtRouteMiddleware(async (to) => {
  const { ready, loggedIn, fetchUser } = useAuth()

  if (!ready.value) await fetchUser()

  const isPublic = PUBLIC_ROUTES.includes(to.path)

  if (!loggedIn.value && !isPublic) return navigateTo(ROUTES.LOGIN)
  if (loggedIn.value && isPublic) return navigateTo(ROUTES.HOME)
})

<script setup lang="ts">
const { user } = useAuth()
const { fetchWallets, reset } = useWallets()
const { reset: resetCategories } = useCategories()
const { reset: resetTransactions } = useTransactions()
const { fetchInvitations, reset: resetInvitations } = useInvitations()
const route = useRoute()

const REFRESH_INTERVAL = 30_000 // 30 s
let lastRefresh = Date.now()

const refreshInvitations = () => {
  if (!user.value || Date.now() - lastRefresh < REFRESH_INTERVAL) return

  lastRefresh = Date.now()
  fetchInvitations()
}

watch(() => route.fullPath, refreshInvitations)

const onVisible = () => {
  if (document.visibilityState === 'visible') refreshInvitations()
}

onMounted(() => document.addEventListener('visibilitychange', onVisible))
onBeforeUnmount(() => document.removeEventListener('visibilitychange', onVisible))

watch(
  () => user.value?.id,
  (id) => {
    reset()
    resetCategories()
    resetTransactions()
    resetInvitations()

    if (id) {
      fetchWallets()
      fetchInvitations()
    } else navigateTo(ROUTES.LOGIN)
  },
  { immediate: true }
)
</script>

<template>
  <UDashboardGroup unit="rem">
    <AppSidebar />
    <slot />
  </UDashboardGroup>
</template>

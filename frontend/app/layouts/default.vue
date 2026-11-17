<script setup lang="ts">
const { user } = useAuth()
const { fetchWallets, reset } = useWallets()
const { reset: resetCategories } = useCategories()
const { reset: resetTransactions } = useTransactions()
const { fetchInvitations, reset: resetInvitations } = useInvitations()

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

<script setup lang="ts">
const { user } = useAuth()
const { fetchWallets, reset } = useWallets()
const { reset: resetCategories } = useCategories()

watch(
  () => user.value?.id,
  (id) => {
    reset()
    resetCategories()

    if (id) fetchWallets()
    else navigateTo(ROUTES.LOGIN)
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

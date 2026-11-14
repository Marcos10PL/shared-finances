<script setup lang="ts">
const { user } = useAuth()
const { fetchWallets, reset } = useWallets()

watch(
  () => user.value?.id,
  (id) => {
    reset()

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

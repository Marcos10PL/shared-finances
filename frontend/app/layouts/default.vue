<script setup lang="ts">
const { user } = useAuth()
const { fetchWallets, reset } = useWallets()

watch(
  () => user.value?.id,
  (id) => {
    reset()
    if (id) fetchWallets()
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

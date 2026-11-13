<script setup lang="ts">
defineProps<{
  title: string
}>()
</script>

<template>
  <UDashboardPanel>
    <template #header>
      <UDashboardNavbar :title>
        <template #leading>
          <UDashboardSidebarCollapse />
        </template>

        <template #right>
          <slot name="right" />
        </template>
      </UDashboardNavbar>
    </template>

    <template #body>
      <slot />
    </template>

    <template v-if="$slots.footer" #footer>
      <slot name="footer" />
    </template>
  </UDashboardPanel>
</template>

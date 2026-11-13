<script setup lang="ts">
import type { BreadcrumbItem } from '@nuxt/ui'

defineProps<{
  title: string
  breadcrumb?: BreadcrumbItem[]
}>()
</script>

<template>
  <UDashboardPanel>
    <template #header>
      <UDashboardNavbar :title>
        <template #leading>
          <UDashboardSidebarCollapse />
        </template>

        <template v-if="breadcrumb" #title>
          <UBreadcrumb :items="breadcrumb" />
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

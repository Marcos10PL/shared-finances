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
          <div class="flex min-w-0 items-center gap-2 pr-3">
            <UBreadcrumb
              :items="breadcrumb"
              class="min-w-0 *:*:first:shrink-[0.4]"
              :ui="{ list: 'min-w-0', item: 'min-w-0', linkLabel: 'truncate' }"
            />
            <div class="shrink-0">
              <slot name="title-suffix" />
            </div>
          </div>
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

<script setup lang="ts">
import type { BreadcrumbItem } from '@nuxt/ui'

defineProps<{
  title: string
  breadcrumb?: BreadcrumbItem[]
}>()
</script>

<template>
  <AppPage :title :breadcrumb>
    <UEmpty icon="i-lucide-construction" title="Strona w budowie" class="m-auto" />
  </AppPage>
</template>

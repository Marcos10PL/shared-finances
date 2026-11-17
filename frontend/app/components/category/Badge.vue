<script setup lang="ts">
import type { Category } from '~/types'

const { category } = defineProps<{
  category: Category
}>()

const color = computed(() => getChartColorById(category.id))
</script>

<template>
  <UBadge
    color="neutral"
    variant="subtle"
    class="max-w-full font-medium"
    :class="color.badge"
    :ui="{ label: 'truncate' }"
    :label="category.name"
  />
</template>

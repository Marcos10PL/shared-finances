<script setup lang="ts">
const { userId, name } = defineProps<{
  userId: number
  name: string
}>()

const color = computed(() => getChartColorById(userId))
</script>

<template>
  <UAvatar
    :text="getInitials(name)"
    :alt="name"
    :ui="{ root: color.bg, fallback: color.initials }"
  />
</template>

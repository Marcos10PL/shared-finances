<script setup lang="ts">
const {
  title = undefined,
  description = undefined,
  loading = false
} = defineProps<{
  title?: string
  description?: string
  loading?: boolean
}>()
</script>

<template>
  <section class="@container border-t border-default py-6 first:border-t-0 first:pt-0">
    <div class="grid gap-4 @4xl:grid-cols-[20rem_minmax(0,1fr)] @4xl:gap-8">
      <div v-if="loading" class="flex flex-col gap-2">
        <USkeleton class="h-5 w-40" />
        <USkeleton class="h-4 w-full max-w-72" />
        <USkeleton class="h-4 w-2/3 max-w-48" />
      </div>
      <div v-else class="flex flex-col gap-1">
        <h2 class="text-base font-semibold text-highlighted">{{ title }}</h2>
        <p v-if="description" class="text-sm text-muted">{{ description }}</p>
      </div>

      <slot />
    </div>
  </section>
</template>

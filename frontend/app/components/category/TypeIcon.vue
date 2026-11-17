<script setup lang="ts">
import type { CategoryType } from '~/types'

const { type } = defineProps<{
  type: CategoryType
}>()

const isExpense = computed(() => type === CATEGORY_TYPES.EXPENSE)
</script>

<template>
  <span
    class="inline-flex size-8 shrink-0 items-center justify-center rounded-md"
    :class="isExpense ? 'bg-error/10 text-error' : 'bg-success/10 text-success'"
  >
    <UIcon
      :name="isExpense ? 'i-lucide-arrow-up-right' : 'i-lucide-arrow-down-left'"
      class="size-4"
    />
  </span>
</template>

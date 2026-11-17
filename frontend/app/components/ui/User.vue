<script setup lang="ts">
const {
  userId,
  name,
  description = undefined,
  truncate = true
} = defineProps<{
  userId: number
  name: string
  description?: string
  truncate?: boolean
}>()
</script>

<template>
  <UUser
    :name
    :description
    size="md"
    :ui="{
      root: 'min-w-0',
      wrapper: 'min-w-0',
      name: ['text-sm font-medium text-highlighted', truncate && 'truncate'],
      description: 'text-xs text-muted'
    }"
  >
    <template #avatar>
      <UiUserAvatar :user-id :name size="md" />
    </template>

    <template v-if="$slots.name" #name>
      <slot name="name" />
    </template>

    <template v-if="$slots.description" #description>
      <slot name="description" />
    </template>
  </UUser>
</template>

<script setup lang="ts">
import type { InputProps } from '@nuxt/ui'

const {
  type = 'text',
  size = 'md',
  placeholder = '',
  help = '',
  autocomplete = undefined,
  maxLength = undefined,
  required = false
} = defineProps<{
  name: string
  label: string
  type?: InputProps['type']
  size?: InputProps['size']
  placeholder?: string
  help?: string
  autocomplete?: string
  maxLength?: number
  required?: boolean
}>()

const model = defineModel<string>()
const showPassword = ref(false)
</script>

<template>
  <UFormField :name :label :help :required :size class="w-full">
    <UInput
      v-model="model"
      :type="type === 'password' && showPassword ? 'text' : type"
      :placeholder
      :autocomplete
      :maxlength="maxLength"
      :size
      class="w-full"
      :ui="{ trailing: 'pe-1' }"
    >
      <template v-if="type === 'password'" #trailing>
        <UButton
          color="neutral"
          variant="link"
          size="sm"
          :icon="showPassword ? 'i-lucide-eye-off' : 'i-lucide-eye'"
          :aria-label="showPassword ? 'Ukryj hasło' : 'Pokaż hasło'"
          :aria-pressed="showPassword"
          @click="showPassword = !showPassword"
        />
      </template>
    </UInput>
  </UFormField>
</template>

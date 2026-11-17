<script setup lang="ts">
import type { DateValue } from '@internationalized/date'
import { parseDate } from '@internationalized/date'

const model = defineModel<string>({ default: '' })

const { max = toDateKey(new Date()) } = defineProps<{
  max?: string
}>()

const SEGMENT_CLASS =
  'data-[segment=day]:w-auto data-[segment=month]:w-auto data-[segment=year]:w-auto'

const minValue = parseDate(VALIDATION.DATE_MIN)
const maxValue = computed(() => parseDate(max))

const root = useTemplateRef<HTMLElement>('root')
const open = ref(false)

const date = computed<DateValue | null>({
  get: () => (model.value ? parseDate(model.value) : null),
  set: (value) => (model.value = value ? value.toString() : '')
})

const onPick = (value: unknown) => {
  if (!value || Array.isArray(value) || typeof value !== 'object' || 'start' in value) return

  date.value = value as DateValue
  open.value = false
}
</script>

<template>
  <div ref="root" class="w-full">
    <UInputDate
      v-model="date"
      :min-value
      :max-value
      class="w-full"
      :ui="{ base: 'gap-0', segment: SEGMENT_CLASS }"
    >
      <template #trailing>
        <UPopover v-model:open="open" :reference="root ?? undefined" :content="{ align: 'start' }">
          <UButton
            color="neutral"
            variant="link"
            size="sm"
            icon="i-lucide-calendar"
            aria-label="Wybierz datę z kalendarza"
          />

          <template #content>
            <UCalendar
              :model-value="date"
              :min-value
              :max-value
              prevent-deselect
              class="p-2"
              @update:model-value="onPick"
            />
          </template>
        </UPopover>
      </template>
    </UInputDate>
  </div>
</template>

<script setup lang="ts">
const month = defineModel<string>({ required: true })

const currentMonth = getMonthKey(new Date())
const currentYear = Number(currentMonth.slice(0, 4))
const firstYear = Number(VALIDATION.DATE_MIN.slice(0, 4))
const firstMonth = VALIDATION.DATE_MIN.slice(0, 7)

const open = ref(false)

const year = ref(Number(month.value.slice(0, 4)))
watch(open, (isOpen) => {
  if (isOpen) year.value = Number(month.value.slice(0, 4))
})

const getKey = (index: number) => getMonthKey(new Date(year.value, index, 1))

const pick = (index: number) => {
  month.value = getKey(index)
  open.value = false
}
</script>

<template>
  <div class="inline-flex items-center rounded-md bg-default ring ring-default">
    <UButton
      icon="i-lucide-chevron-left"
      color="neutral"
      variant="ghost"
      aria-label="Poprzedni miesiąc"
      :disabled="month <= firstMonth"
      @click="month = shiftMonth(month, -1)"
    />

    <UPopover v-model:open="open" :content="{ align: 'center' }">
      <button
        type="button"
        class="inline-flex min-w-44 flex-1 cursor-pointer items-center justify-center gap-2 self-stretch px-2 text-sm font-medium text-highlighted hover:bg-elevated"
        aria-label="Wybierz miesiąc"
      >
        <UIcon name="i-lucide-calendar" class="size-4 text-muted" />
        {{ formatMonth(month) }}
      </button>

      <template #content>
        <div class="w-64 p-3">
          <div class="mb-2 flex items-center justify-between">
            <UButton
              icon="i-lucide-chevron-left"
              color="neutral"
              variant="ghost"
              size="sm"
              aria-label="Poprzedni rok"
              :disabled="year <= firstYear"
              @click="year--"
            />
            <span class="text-sm font-medium text-highlighted">{{ year }}</span>
            <UButton
              icon="i-lucide-chevron-right"
              color="neutral"
              variant="ghost"
              size="sm"
              aria-label="Następny rok"
              :disabled="year >= currentYear"
              @click="year++"
            />
          </div>

          <div class="grid grid-cols-3 gap-1">
            <UButton
              v-for="(label, index) in SHORT_MONTH_LABELS"
              :key="index"
              :label="label"
              :color="getKey(index) === month ? 'primary' : 'neutral'"
              :variant="getKey(index) === month ? 'solid' : 'ghost'"
              :disabled="getKey(index) > currentMonth"
              class="justify-center"
              @click="pick(index)"
            />
          </div>
        </div>
      </template>
    </UPopover>

    <UButton
      icon="i-lucide-chevron-right"
      color="neutral"
      variant="ghost"
      aria-label="Następny miesiąc"
      :disabled="month >= currentMonth"
      @click="month = shiftMonth(month, 1)"
    />
  </div>
</template>

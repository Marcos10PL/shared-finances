<script setup lang="ts">
import type { MemberContribution, WalletMember } from '~/types'

const { contributions, members, loading } = defineProps<{
  contributions: MemberContribution[]
  members: WalletMember[]
  loading: boolean
}>()

const SEARCH_FROM = 6

const memberById = computed(() => new Map(members.map((member) => [member.id, member])))

const rows = computed(() =>
  contributions.flatMap((contribution) => {
    const member = memberById.value.get(contribution.memberId)
    return member ? [{ member, income: contribution.income, expenses: contribution.expenses }] : []
  })
)

const search = ref('')
const visible = computed(() => {
  const text = search.value.trim().toLowerCase()
  return text
    ? rows.value.filter((row) => row.member.displayName.toLowerCase().includes(text))
    : rows.value
})
</script>

<template>
  <UCard variant="flush">
    <h2 class="p-4 text-base font-semibold text-highlighted">Wkład członków</h2>

    <ul v-if="loading" class="divide-y divide-default border-t border-default" aria-busy="true">
      <li v-for="index in 3" :key="index" class="flex items-center gap-3 px-4 py-3">
        <UiUserSkeleton />
        <div class="flex flex-col items-end">
          <USkeleton class="h-5 w-24" />
          <USkeleton class="h-5 w-24" />
        </div>
      </li>
    </ul>

    <template v-else>
      <div v-if="rows.length >= SEARCH_FROM" class="border-t border-default p-3">
        <UInput
          v-model="search"
          icon="i-lucide-search"
          placeholder="Szukaj członka"
          aria-label="Szukaj członka"
          class="w-full"
        />
      </div>

      <ul class="max-h-80 divide-y divide-default overflow-y-auto border-t border-default">
        <li v-for="row in visible" :key="row.member.id" class="flex items-center gap-3 px-4 py-3">
          <UiUser
            :user-id="row.member.id"
            :name="row.member.displayName"
            :description="WALLET_ROLE_LABELS[row.member.role]"
            class="flex-1"
          />

          <div class="flex flex-col items-end text-sm tabular-nums">
            <span class="text-income">{{ formatSignedCurrency(row.income) }}</span>
            <span class="text-expense">{{ formatSignedCurrency(-row.expenses) }}</span>
          </div>
        </li>
        <li v-if="visible.length === 0" class="px-4 py-6 text-center text-sm text-muted">
          Nie znaleziono członka.
        </li>
      </ul>
    </template>
  </UCard>
</template>

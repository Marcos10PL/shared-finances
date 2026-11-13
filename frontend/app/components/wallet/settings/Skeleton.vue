<script setup lang="ts">
const SECTIONS = [['h-40'], ['h-58'], ['h-40', 'h-81'], ['h-22']]
</script>

<template>
  <div class="flex w-full max-w-5xl flex-col" aria-busy="true">
    <WalletSettingsSection v-for="(cards, index) in SECTIONS" :key="index" loading>
      <div class="flex flex-col gap-4">
        <USkeleton
          v-for="(height, i) in cards"
          :key="i"
          class="w-full rounded-lg"
          :class="height"
        />
      </div>
    </WalletSettingsSection>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  title: string
}>()
</script>

<template>
  <AppPage :title>
    <UEmpty icon="i-lucide-construction" title="Strona w budowie" class="m-auto" />
  </AppPage>
</template>

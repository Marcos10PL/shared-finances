<script setup lang="ts">
const wallet = useCurrentWallet()

const title = computed(() => wallet.value?.name ?? 'Portfel')
const breadcrumb = computed(() => [{ label: 'Portfele', to: ROUTES.HOME }, { label: title.value }])

useHead({ title })
</script>

<template>
  <PagePlaceholder :title :breadcrumb />
</template>

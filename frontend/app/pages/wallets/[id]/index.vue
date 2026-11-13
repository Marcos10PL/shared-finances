<script setup lang="ts">
const { wallets } = useWallets()
const route = useRoute()

const wallet = computed(() => wallets.value.find((item) => item.id === route.params.id))
const title = computed(() => wallet.value?.name ?? 'Portfel')

useHead({ title })
</script>

<template>
  <PagePlaceholder :title />
</template>

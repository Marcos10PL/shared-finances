<script setup lang="ts">
import { pl } from '@nuxt/ui/locale'

useHead({
  titleTemplate: (title) => (title ? `${title} · Wspólne finanse` : 'Wspólne finanse'),
  meta: [{ name: 'viewport', content: 'width=device-width, initial-scale=1' }],
  link: [{ rel: 'icon', type: 'image/png', href: '/favicon.png' }],
  htmlAttrs: {
    lang: 'pl'
  }
})

useSeoMeta({
  description: 'Wspólne finanse - portfele, wspólne wydatki i miesięczny limit dla każdego.'
})
</script>

<template>
  <UApp :locale="pl">
    <UMain>
      <NuxtPage />
    </UMain>
  </UApp>
</template>

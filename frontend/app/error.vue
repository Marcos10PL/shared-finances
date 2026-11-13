<script setup lang="ts">
import type { NuxtError } from '#app'
import { pl } from '@nuxt/ui/locale'

const { error } = defineProps<{
  error: NuxtError
}>()

const isNotFound = computed(() => error.status === 404)

const title = computed(() =>
  isNotFound.value ? 'Strona, której szukasz, nie istnieje.' : 'Ups! Coś poszło nie tak.'
)

useHead({
  title: computed(() => `${title.value} · Wspólne finanse`),
  link: [{ rel: 'icon', type: 'image/svg+xml', href: '/favicon.svg' }],
  htmlAttrs: { lang: 'pl' }
})

const onBack = () => clearError({ redirect: ROUTES.HOME })
</script>

<template>
  <UApp :locale="pl">
    <NuxtLayout name="auth">
      <AuthCard :title>
        <UButton label="Wróć do portfeli" icon="i-lucide-arrow-left" block @click="onBack" />
      </AuthCard>
    </NuxtLayout>
  </UApp>
</template>

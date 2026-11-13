<script setup lang="ts">
import type { FormSubmitEvent } from '@nuxt/ui'
import { loginSchema, type LoginInput } from '~/schemas/auth'

definePageMeta({ layout: 'auth' })
useHead({ title: 'Logowanie' })

const { login } = useAuth()

const state = reactive<LoginInput>({ email: '', password: '' })
const loading = ref(false)
const errorMessage = ref('')

async function onSubmit(event: FormSubmitEvent<LoginInput>) {
  loading.value = true
  errorMessage.value = ''

  try {
    await login(event.data)
    await navigateTo(ROUTES.HOME)
  } catch (error) {
    errorMessage.value =
      getAuthErrorCode(error) === 'INVALID_CREDENTIALS'
        ? 'Nieprawidłowy adres e-mail lub hasło.'
        : 'Nie udało się zalogować. Spróbuj ponownie.'
    loading.value = false
  }
}
</script>

<template>
  <AuthCard title="Zaloguj się">
    <template #subtitle>
      Nie masz konta?
      <ULink :to="ROUTES.REGISTER"> Załóż konto </ULink>
    </template>

    <UAlert
      v-if="errorMessage"
      color="error"
      variant="subtle"
      icon="i-lucide-circle-alert"
      :title="errorMessage"
    />

    <UForm
      :schema="loginSchema"
      :state
      :disabled="loading"
      class="flex flex-col gap-4"
      @submit="onSubmit"
    >
      <UiInput
        v-model="state.email"
        name="email"
        label="E-mail"
        type="email"
        autocomplete="email"
        :max-length="VALIDATION.EMAIL_MAX_LENGTH"
        size="lg"
      />
      <UiInput
        v-model="state.password"
        name="password"
        label="Hasło"
        type="password"
        autocomplete="current-password"
        :max-length="VALIDATION.PASSWORD_MAX_LENGTH"
        size="lg"
      />

      <UButton type="submit" size="lg" block :loading class="mt-1">Zaloguj się</UButton>
    </UForm>
  </AuthCard>
</template>

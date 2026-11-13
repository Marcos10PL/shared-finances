<script setup lang="ts">
import type { FormSubmitEvent } from '@nuxt/ui'
import { registerSchema, type RegisterInput } from '~/schemas/auth'

definePageMeta({ layout: 'auth' })
useHead({ title: 'Rejestracja' })

const { register } = useAuth()

const form = useTemplateRef('form')
const state = reactive<RegisterInput>({
  displayName: '',
  email: '',
  password: '',
  confirmPassword: ''
})
const loading = ref(false)
const errorMessage = ref('')

async function onSubmit(event: FormSubmitEvent<RegisterInput>) {
  loading.value = true
  errorMessage.value = ''

  try {
    await register(event.data)
    await navigateTo(ROUTES.HOME)
  } catch (error) {
    if (getAuthErrorCode(error) === 'EMAIL_TAKEN') {
      form.value?.setErrors([
        { name: 'email', message: 'Konto z tym adresem e-mail już istnieje.' }
      ])
    } else {
      errorMessage.value = 'Nie udało się założyć konta. Spróbuj ponownie.'
    }
    loading.value = false
  }
}
</script>

<template>
  <AuthCard title="Załóż konto">
    <template #subtitle>
      Masz już konto?
      <ULink :to="ROUTES.LOGIN"> Zaloguj się </ULink>
    </template>

    <UAlert
      v-if="errorMessage"
      color="error"
      variant="subtle"
      icon="i-lucide-circle-alert"
      :title="errorMessage"
    />

    <UForm
      ref="form"
      :schema="registerSchema"
      :state
      :disabled="loading"
      class="flex flex-col gap-4"
      @submit="onSubmit"
    >
      <UiInput
        v-model="state.displayName"
        name="displayName"
        label="Twoja nazwa"
        help="Widzą ją członkowie Twoich portfeli."
        autocomplete="name"
        :max-length="VALIDATION.DISPLAY_NAME_MAX_LENGTH"
        size="lg"
      />
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
        :help="`Co najmniej ${VALIDATION.PASSWORD_MIN_LENGTH} znaków.`"
        autocomplete="new-password"
        :max-length="VALIDATION.PASSWORD_MAX_LENGTH"
        size="lg"
      />
      <UiInput
        v-model="state.confirmPassword"
        name="confirmPassword"
        label="Powtórz hasło"
        type="password"
        autocomplete="new-password"
        :max-length="VALIDATION.PASSWORD_MAX_LENGTH"
        size="lg"
      />

      <UButton type="submit" size="lg" block :loading class="mt-1">Załóż konto</UButton>
    </UForm>
  </AuthCard>
</template>

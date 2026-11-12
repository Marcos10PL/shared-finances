import * as z from 'zod'

const emailSchema = z
  .string()
  .trim()
  .max(VALIDATION.EMAIL_MAX_LENGTH, `Maksymalna długość adresu: ${VALIDATION.EMAIL_MAX_LENGTH}.`)
  .pipe(z.email('Podaj poprawny adres e-mail.'))

const passwordMaxMessage = `Maksymalna długość hasła: ${VALIDATION.PASSWORD_MAX_LENGTH}.`

export const loginSchema = z.object({
  email: emailSchema,
  password: z
    .string()
    .min(1, 'Podaj hasło.')
    .max(VALIDATION.PASSWORD_MAX_LENGTH, passwordMaxMessage)
})

export const registerSchema = z
  .object({
    displayName: z
      .string()
      .trim()
      .min(1, 'Podaj swoją nazwę.')
      .max(
        VALIDATION.DISPLAY_NAME_MAX_LENGTH,
        `Maksymalna długość nazwy: ${VALIDATION.DISPLAY_NAME_MAX_LENGTH}.`
      ),
    email: emailSchema,
    password: z
      .string()
      .min(
        VALIDATION.PASSWORD_MIN_LENGTH,
        `Hasło musi mieć co najmniej ${VALIDATION.PASSWORD_MIN_LENGTH} znaków.`
      )
      .max(VALIDATION.PASSWORD_MAX_LENGTH, passwordMaxMessage),
    confirmPassword: z.string().min(1, 'Powtórz hasło.')
  })
  .refine((data) => data.password === data.confirmPassword, {
    path: ['confirmPassword'],
    message: 'Hasła nie są takie same.'
  })

export type LoginInput = z.output<typeof loginSchema>
export type RegisterInput = z.output<typeof registerSchema>

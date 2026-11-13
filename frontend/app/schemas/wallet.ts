import * as z from 'zod'
import { emailSchema } from '~/schemas/auth'

export const walletSchema = z.object({
  name: z
    .string()
    .trim()
    .min(1, 'Podaj nazwę portfela.')
    .max(
      VALIDATION.WALLET_NAME_MAX_LENGTH,
      `Maksymalna długość nazwy: ${VALIDATION.WALLET_NAME_MAX_LENGTH}.`
    )
})

const thresholdRangeMessage = `Próg musi być między ${VALIDATION.WARNING_THRESHOLD_MIN} a ${VALIDATION.WARNING_THRESHOLD_MAX}%.`

export const limitSchema = z
  .object({
    monthlyLimit: z
      .number('Podaj kwotę limitu.')
      .positive('Limit musi być większy od zera.')
      .max(VALIDATION.AMOUNT_MAX, `Maksymalna kwota: ${formatCurrency(VALIDATION.AMOUNT_MAX)}.`)
      .nullish(),
    warningThreshold: z
      .number('Podaj próg jako liczbę.')
      .int('Podaj próg jako liczbę całkowitą.')
      .min(VALIDATION.WARNING_THRESHOLD_MIN, thresholdRangeMessage)
      .max(VALIDATION.WARNING_THRESHOLD_MAX, thresholdRangeMessage)
      .nullish()
  })
  .refine((data) => data.warningThreshold == null || data.monthlyLimit != null, {
    path: ['monthlyLimit'],
    message: 'Podaj kwotę limitu.'
  })

export const inviteSchema = z.object({
  email: emailSchema,
  role: z.enum([WALLET_ROLES.MEMBER, WALLET_ROLES.GUEST])
})

export type InviteInput = z.output<typeof inviteSchema>
export type WalletInput = z.output<typeof walletSchema>
export type LimitInput = z.output<typeof limitSchema>

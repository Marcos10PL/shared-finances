import * as z from 'zod'

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

export type WalletInput = z.output<typeof walletSchema>

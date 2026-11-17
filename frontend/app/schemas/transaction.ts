import * as z from 'zod'

export const transactionSchema = z.object({
  type: z.enum(CATEGORY_TYPES),
  name: z
    .string()
    .trim()
    .min(1, 'Podaj nazwę transakcji.')
    .max(
      VALIDATION.TRANSACTION_NAME_MAX_LENGTH,
      `Maksymalna długość nazwy: ${VALIDATION.TRANSACTION_NAME_MAX_LENGTH}.`
    ),
  amount: z
    .number('Podaj kwotę.')
    .positive('Kwota musi być większa od zera.')
    .max(VALIDATION.AMOUNT_MAX, `Maksymalna kwota: ${formatCurrency(VALIDATION.AMOUNT_MAX)}.`),
  categoryId: z.number('Wybierz kategorię.'),
  date: z
    .string()
    .min(1, 'Podaj datę.')
    .refine((value) => value >= VALIDATION.DATE_MIN, 'Podaj prawidłową datę.')
    .refine((value) => value <= toDateKey(new Date()), 'Data nie może być z przyszłości.'),
  description: z
    .string()
    .trim()
    .max(
      VALIDATION.TRANSACTION_DESCRIPTION_MAX_LENGTH,
      `Maksymalna długość opisu: ${VALIDATION.TRANSACTION_DESCRIPTION_MAX_LENGTH}.`
    )
    .optional()
})

export type TransactionInput = z.output<typeof transactionSchema>

import * as z from 'zod'

export const categorySchema = z.object({
  name: z
    .string()
    .trim()
    .min(1, 'Podaj nazwę kategorii.')
    .max(
      VALIDATION.CATEGORY_NAME_MAX_LENGTH,
      `Maksymalna długość nazwy: ${VALIDATION.CATEGORY_NAME_MAX_LENGTH}.`
    ),
  type: z.enum(CATEGORY_TYPES)
})

export const categoryNameSchema = categorySchema.pick({ name: true })

export type CategoryInput = z.output<typeof categorySchema>
export type CategoryNameInput = z.output<typeof categoryNameSchema>

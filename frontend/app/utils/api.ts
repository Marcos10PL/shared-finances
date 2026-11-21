import type { ErrorCode, ErrorMessages } from '~/types'

const isErrorCode = (value: unknown): value is ErrorCode =>
  Object.values<unknown>(ERROR_CODES).includes(value)

export const getApiErrorCode = (error: unknown) => {
  const message = (error as { data?: { message?: unknown } } | null)?.data?.message
  return isErrorCode(message) ? message : undefined
}

export const getErrorMessage = (error: unknown, messages: ErrorMessages, fallback: string) => {
  const code = getApiErrorCode(error)
  return (code && messages[code]) || fallback
}

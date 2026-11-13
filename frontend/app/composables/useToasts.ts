export const useToasts = () => {
  const toast = useToast()

  const showSuccess = (title: string) => toast.add({ title, color: 'success' })
  const showError = (title: string) => toast.add({ title, color: 'error' })

  return { showSuccess, showError }
}

export const useCurrentWallet = () => {
  const route = useRoute()
  const { wallets } = useWallets()

  return computed(() => wallets.value.find((item) => item.id === Number(route.params.id)))
}

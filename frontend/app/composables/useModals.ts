import { LazyUiConfirmModal, LazyWalletNewModal } from '#components'

type Overlay = ReturnType<typeof useOverlay>

const createNewWalletModal = (overlay: Overlay) => overlay.create(LazyWalletNewModal)
const createConfirmModal = (overlay: Overlay) => overlay.create(LazyUiConfirmModal)

let newWalletModal: ReturnType<typeof createNewWalletModal> | undefined
let confirmModal: ReturnType<typeof createConfirmModal> | undefined

export const useModals = () => {
  const overlay = useOverlay()

  return {
    openNewWallet: () => (newWalletModal ??= createNewWalletModal(overlay)).open(),
    confirm: (props: Parameters<ReturnType<typeof createConfirmModal>['open']>[0]) =>
      (confirmModal ??= createConfirmModal(overlay)).open(props)
  }
}

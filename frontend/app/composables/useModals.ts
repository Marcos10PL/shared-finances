import { LazyCategoryEditModal, LazyUiConfirmModal, LazyWalletNewModal } from '#components'
import type { Category } from '~/types'

type Overlay = ReturnType<typeof useOverlay>

const createNewWalletModal = (overlay: Overlay) => overlay.create(LazyWalletNewModal)
const createConfirmModal = (overlay: Overlay) => overlay.create(LazyUiConfirmModal)
const createEditCategoryModal = (overlay: Overlay) => overlay.create(LazyCategoryEditModal)

let newWalletModal: ReturnType<typeof createNewWalletModal> | undefined
let confirmModal: ReturnType<typeof createConfirmModal> | undefined
let editCategoryModal: ReturnType<typeof createEditCategoryModal> | undefined

export const useModals = () => {
  const overlay = useOverlay()

  return {
    openNewWallet: () => (newWalletModal ??= createNewWalletModal(overlay)).open(),
    openEditCategory: (category: Category) =>
      (editCategoryModal ??= createEditCategoryModal(overlay)).open({ category }),
    confirm: (props: Parameters<ReturnType<typeof createConfirmModal>['open']>[0]) =>
      (confirmModal ??= createConfirmModal(overlay)).open(props)
  }
}

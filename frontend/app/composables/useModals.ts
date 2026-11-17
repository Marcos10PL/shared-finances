import {
  LazyCategoryEditModal,
  LazyTransactionModal,
  LazyUiConfirmModal,
  LazyWalletNewModal
} from '#components'
import type { Category, Transaction } from '~/types'

type Overlay = ReturnType<typeof useOverlay>

const createNewWalletModal = (overlay: Overlay) => overlay.create(LazyWalletNewModal)
const createConfirmModal = (overlay: Overlay) => overlay.create(LazyUiConfirmModal)
const createEditCategoryModal = (overlay: Overlay) => overlay.create(LazyCategoryEditModal)
const createTransactionModal = (overlay: Overlay) => overlay.create(LazyTransactionModal)

let newWalletModal: ReturnType<typeof createNewWalletModal> | undefined
let confirmModal: ReturnType<typeof createConfirmModal> | undefined
let editCategoryModal: ReturnType<typeof createEditCategoryModal> | undefined
let transactionModal: ReturnType<typeof createTransactionModal> | undefined

export const useModals = () => {
  const overlay = useOverlay()

  return {
    openNewWallet: () => (newWalletModal ??= createNewWalletModal(overlay)).open(),
    openEditCategory: (category: Category) =>
      (editCategoryModal ??= createEditCategoryModal(overlay)).open({ category }),
    openTransaction: (props: {
      walletName: string
      transaction?: Transaction
      authorName?: string
    }) => (transactionModal ??= createTransactionModal(overlay)).open(props),
    confirm: (props: Parameters<ReturnType<typeof createConfirmModal>['open']>[0]) =>
      (confirmModal ??= createConfirmModal(overlay)).open(props)
  }
}

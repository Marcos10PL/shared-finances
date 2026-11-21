import type { AssignableRole, Wallet, WalletMember, WalletUpdate } from '~/types'

export const useWallets = () => {
  const api = useApi()
  const wallets = useState<Wallet[]>('wallets', () => [])
  const loaded = useState('wallets:loaded', () => false)
  const error = useState('wallets:error', () => false)

  // wallets
  const getWallet = (id: number) => {
    const wallet = wallets.value.find((item) => item.id === id)
    if (!wallet) throw new Error('Wallet not found')
    return wallet
  }

  const fetchWallets = async () => {
    error.value = false

    try {
      wallets.value = await api<Wallet[]>('/api/wallets')
      loaded.value = true
    } catch {
      error.value = true
    }
  }

  const createWallet = async (name: string) => {
    wallets.value.push(await api<Wallet>('/api/wallets', { method: 'POST', body: { name } }))
  }

  const updateWallet = async (id: number, input: WalletUpdate) => {
    const wallet = getWallet(id)
    const { name, monthlyLimit, warningThreshold } = wallet
    const updated = await api<Wallet>(`/api/wallets/${id}`, {
      method: 'PUT',
      body: { name, monthlyLimit, warningThreshold, ...input }
    })
    Object.assign(wallet, updated)
  }

  // delete / leave
  const dropWallet = (id: number) => {
    wallets.value = wallets.value.filter((wallet) => wallet.id !== id)
  }

  const deleteWallet = async (id: number) => {
    await api(`/api/wallets/${id}`, { method: 'DELETE' })
    dropWallet(id)
  }

  const leaveWallet = async (id: number) => {
    await api(`/api/wallets/${id}/leave`, { method: 'POST' })
    dropWallet(id)
  }

  // members
  const inviteMember = async (id: number, email: string, role: AssignableRole) => {
    await api(`/api/wallets/${id}/invitations`, { method: 'POST', body: { email, role } })
    await fetchWallets()
  }

  const changeMemberRole = async (id: number, memberId: number, role: AssignableRole) => {
    const updated = await api<WalletMember>(`/api/wallets/${id}/members/${memberId}/role`, {
      method: 'PUT',
      body: { role }
    })
    const member = getWallet(id).members.find((item) => item.id === memberId)
    if (member) Object.assign(member, updated)
  }

  const removeMember = async (id: number, memberId: number) => {
    await api(`/api/wallets/${id}/members/${memberId}`, { method: 'DELETE' })
    const wallet = getWallet(id)
    wallet.members = wallet.members.filter((member) => member.id !== memberId)
  }

  const reset = () => {
    wallets.value = []
    loaded.value = false
    error.value = false
  }

  return {
    wallets,
    loaded,
    error,

    fetchWallets,
    createWallet,
    updateWallet,
    deleteWallet,
    leaveWallet,

    inviteMember,
    changeMemberRole,
    removeMember,

    reset
  }
}

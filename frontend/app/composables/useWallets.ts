import type { AssignableRole, ErrorCode, LimitSettings, Wallet } from '~/types'

// TODO: MOCK
const wait = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms))

// TODO: MOCK
const KNOWN_EMAILS = [
  'marcin.wojcik@example.com',
  'szymon.nowak@example.com',
  'marek.kramarczyk@example.com',
  'anna.kowalska@example.com',
  'ola.zielinska@example.com',
  'jan.lis@example.com',
  'ewa.mazur@example.com'
]

// TODO: MOCK
const mockApiError = (code: ErrorCode) =>
  Object.assign(new Error(code), { data: { message: code } })

export const useWallets = () => {
  const api = useApi()
  const { user } = useAuth()
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

  const updateWallet = async (id: number, input: { name: string }) => {
    const updated = await api<Wallet>(`/api/wallets/${id}`, { method: 'PUT', body: input })
    Object.assign(getWallet(id), updated)
  }

  // delete / leave
  const dropWallet = (id: number) => {
    wallets.value = wallets.value.filter((wallet) => wallet.id !== id)
  }

  const deleteWallet = async (id: number) => {
    await api(`/api/wallets/${id}`, { method: 'DELETE' })
    dropWallet(id)
  }

  // TODO: MOCK
  const leaveWallet = async (id: number) => {
    await wait(300)
    dropWallet(id)
  }

  // members
  // TODO: MOCK
  const inviteMember = async (id: number, email: string, role: AssignableRole) => {
    await wait(300)
    const wallet = getWallet(id)
    const normalized = email.toLowerCase()

    if (normalized === user.value?.email.toLowerCase()) throw mockApiError(ERROR_CODES.SELF)
    if (!KNOWN_EMAILS.includes(normalized)) throw mockApiError(ERROR_CODES.NO_ACCOUNT)
    if (wallet.members.some((member) => member.email === normalized))
      throw mockApiError(ERROR_CODES.ALREADY_MEMBER)
    if (wallet.invitations.some((invitation) => invitation.email === normalized))
      throw mockApiError(ERROR_CODES.ALREADY_INVITED)

    wallet.invitations.push({
      id: Date.now(),
      email: normalized,
      role,
      createdAt: new Date().toISOString()
    })
  }

  // TODO: MOCK
  const changeMemberRole = async (id: number, memberId: number, role: AssignableRole) => {
    await wait(300)
    const member = getWallet(id).members.find((item) => item.id === memberId)
    if (member) member.role = role
  }

  // TODO: MOCK
  const removeMember = async (id: number, memberId: number) => {
    await wait(300)
    const wallet = getWallet(id)
    wallet.members = wallet.members.filter((member) => member.id !== memberId)
  }

  // other
  // TODO: MOCK
  const setLimit = async (id: number, input: LimitSettings) => {
    await wait(300)
    const wallet = getWallet(id)
    wallet.monthlyLimit = input.monthlyLimit
    wallet.warningThreshold = input.monthlyLimit === null ? null : input.warningThreshold
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

    setLimit,
    reset
  }
}

import type { AssignableRole, LimitSettings, Wallet, WalletMember } from '~/types'

// TODO: MOCK
const wait = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms))

type MockUser = Omit<WalletMember, 'role'>

const MARCIN: MockUser = {
  id: 'u-1',
  displayName: 'Marcin Wójcik',
  email: 'marcin.wojcik@example.com'
}
const SZYMON: MockUser = {
  id: 'u-2',
  displayName: 'Szymon Nowak',
  email: 'szymon.nowak@example.com'
}
const MAREK: MockUser = {
  id: 'u-5',
  displayName: 'Marek Kramarczyk',
  email: 'marek.kramarczyk@example.com'
}
const ANNA: MockUser = {
  id: 'u-3',
  displayName: 'Anna Kowalska',
  email: 'anna.kowalska@example.com'
}
const OLA: MockUser = {
  id: 'u-6',
  displayName: 'Ola Zielińska',
  email: 'ola.zielinska@example.com'
}
const JAN: MockUser = { id: 'u-7', displayName: 'Jan Lis', email: 'jan.lis@example.com' }
const EWA: MockUser = { id: 'u-8', displayName: 'Ewa Mazur', email: 'ewa.mazur@example.com' }

const KNOWN_EMAILS = [MARCIN, SZYMON, MAREK, ANNA, OLA, JAN, EWA].map((user) => user.email)

const asMember = (user: MockUser, role: WalletMember['role']): WalletMember => ({ ...user, role })

const mockWallets = (): Wallet[] => [
  {
    id: 'w-1',
    name: 'Mieszkanie Krakowska 12',
    role: WALLET_ROLES.OWNER,
    balance: 2013.6,
    monthlyExpenses: 2486.4,
    monthlyLimit: 3000,
    warningThreshold: 80,
    members: [
      asMember(MAREK, WALLET_ROLES.OWNER),
      asMember(SZYMON, WALLET_ROLES.MEMBER),
      asMember(MARCIN, WALLET_ROLES.MEMBER)
    ],
    invitations: [
      { id: 'i-1', email: OLA.email, role: WALLET_ROLES.GUEST, createdAt: '2026-11-12T10:00:00Z' }
    ]
  },
  {
    id: 'w-2',
    name: 'Moje wydatki',
    role: WALLET_ROLES.OWNER,
    balance: 3214.75,
    monthlyExpenses: 0,
    monthlyLimit: null,
    warningThreshold: null,
    members: [asMember(MAREK, WALLET_ROLES.OWNER)],
    invitations: []
  },
  {
    id: 'w-3',
    name: 'Prezent dla Kasi',
    role: WALLET_ROLES.GUEST,
    balance: -12,
    monthlyExpenses: 612,
    monthlyLimit: 600,
    warningThreshold: 90,
    members: [
      asMember(ANNA, WALLET_ROLES.OWNER),
      asMember(MAREK, WALLET_ROLES.GUEST),
      asMember(OLA, WALLET_ROLES.MEMBER),
      asMember(JAN, WALLET_ROLES.MEMBER),
      asMember(EWA, WALLET_ROLES.MEMBER)
    ],
    invitations: []
  }
]

const inviteError = (code: keyof typeof INVITE_ERRORS) => Object.assign(new Error(code), { code })

export const getInviteErrorCode = (error: unknown) =>
  (error as { code?: keyof typeof INVITE_ERRORS } | null)?.code

export const useWallets = () => {
  const { user } = useAuth()
  const wallets = useState<Wallet[]>('wallets', () => [])
  const loaded = useState('wallets:loaded', () => false)
  const error = useState('wallets:error', () => false)

  // wallets
  const getWallet = (id: string) => {
    const wallet = wallets.value.find((item) => item.id === id)
    if (!wallet) throw new Error('Wallet not found')
    return wallet
  }

  const fetchWallets = async () => {
    error.value = false

    try {
      await wait(300)
      wallets.value = mockWallets()
      loaded.value = true
    } catch {
      error.value = true
    }
  }

  const createWallet = async (name: string) => {
    await wait(300)
    wallets.value.push({
      id: crypto.randomUUID(),
      name,
      role: WALLET_ROLES.OWNER,
      balance: 0,
      monthlyExpenses: 0,
      monthlyLimit: null,
      warningThreshold: null,
      members: [
        {
          id: user.value?.id ?? '',
          displayName: user.value?.displayName ?? '',
          email: user.value?.email ?? '',
          role: WALLET_ROLES.OWNER
        }
      ],
      invitations: []
    })
  }

  const updateWallet = async (id: string, input: { name: string }) => {
    await wait(300)
    getWallet(id).name = input.name
  }

  // members
  const inviteMember = async (id: string, email: string, role: AssignableRole) => {
    await wait(300)
    const wallet = getWallet(id)
    const normalized = email.toLowerCase()

    if (normalized === user.value?.email.toLowerCase()) throw inviteError(INVITE_ERRORS.SELF)
    if (!KNOWN_EMAILS.includes(normalized)) throw inviteError(INVITE_ERRORS.NO_ACCOUNT)
    if (wallet.members.some((member) => member.email === normalized))
      throw inviteError(INVITE_ERRORS.ALREADY_MEMBER)
    if (wallet.invitations.some((invitation) => invitation.email === normalized))
      throw inviteError(INVITE_ERRORS.ALREADY_INVITED)

    wallet.invitations.push({
      id: crypto.randomUUID(),
      email: normalized,
      role,
      createdAt: new Date().toISOString()
    })
  }

  const changeMemberRole = async (id: string, memberId: string, role: AssignableRole) => {
    await wait(300)
    const member = getWallet(id).members.find((item) => item.id === memberId)
    if (member) member.role = role
  }

  const removeMember = async (id: string, memberId: string) => {
    await wait(300)
    const wallet = getWallet(id)
    wallet.members = wallet.members.filter((member) => member.id !== memberId)
  }

  // delete / leave
  const dropWallet = async (id: string) => {
    await wait(300)
    wallets.value = wallets.value.filter((wallet) => wallet.id !== id)
  }

  const deleteWallet = dropWallet
  const leaveWallet = dropWallet

  // other
  const setLimit = async (id: string, input: LimitSettings) => {
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

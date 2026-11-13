import type { Wallet, WalletMember } from '~/types'

const MARCIN: WalletMember = { id: 'u-1', displayName: 'Marcin Wójcik' }
const SZYMON: WalletMember = { id: 'u-2', displayName: 'Szymon Nowak' }
const MAREK: WalletMember = { id: 'u-5', displayName: 'Marek Kramarczyk' }
const ANNA: WalletMember = { id: 'u-3', displayName: 'Anna Kowalska' }

// Simulates the network delay of the mocked API
const wait = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms))

// TODO: MOCK
const mockWallets = (): Wallet[] => [
  {
    id: 'w-1',
    name: 'Mieszkanie Krakowska 12',
    role: WALLET_ROLES.OWNER,
    balance: 2013.6,
    monthlyExpenses: 2486.4,
    monthlyLimit: 3000,
    warningThreshold: 80,
    members: [MAREK, SZYMON, MARCIN]
  },
  {
    id: 'w-2',
    name: 'Moje wydatki',
    role: WALLET_ROLES.OWNER,
    balance: 3214.75,
    monthlyExpenses: 0,
    monthlyLimit: null,
    warningThreshold: null,
    members: [MAREK]
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
      ANNA,
      MAREK,
      { id: 'u-6', displayName: 'Ola Zielińska' },
      { id: 'u-7', displayName: 'Jan Lis' },
      { id: 'u-8', displayName: 'Ewa Mazur' }
    ]
  }
]

export const useWallets = () => {
  const { user } = useAuth()
  const wallets = useState<Wallet[]>('wallets', () => [])
  const loaded = useState('wallets:loaded', () => false)
  const error = useState('wallets:error', () => false)

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
      members: [{ id: user.value?.id ?? '', displayName: user.value?.displayName ?? '' }]
    })
  }

  const leaveWallet = async (id: string) => {
    await wait(300)
    wallets.value = wallets.value.filter((wallet) => wallet.id !== id)
  }

  const reset = () => {
    wallets.value = []
    loaded.value = false
    error.value = false
  }

  return { wallets, loaded, error, fetchWallets, createWallet, leaveWallet, reset }
}

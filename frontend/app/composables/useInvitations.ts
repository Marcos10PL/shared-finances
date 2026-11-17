import type { Invitation } from '~/types'

// TODO: MOCK
const wait = (ms: number) => new Promise((resolve) => setTimeout(resolve, ms))

const mockInvitations = (): Invitation[] => [
  {
    id: 1,
    ownerId: 2,
    ownerName: 'Szymon Nowak',
    walletId: 11,
    walletName: 'Wyjazd Zakopane 2027',
    role: WALLET_ROLES.MEMBER,
    createdAt: '2026-11-13T10:00:00Z'
  },
  {
    id: 2,
    ownerId: 3,
    ownerName: 'Anna Kowalska',
    walletId: 12,
    walletName: 'Prezent dla mamy',
    role: WALLET_ROLES.GUEST,
    createdAt: '2026-11-11T10:00:00Z'
  }
]

export const useInvitations = () => {
  const invitations = useState<Invitation[]>('invitations', () => [])
  const loaded = useState('invitations:loaded', () => false)

  const count = computed(() => invitations.value.length)

  // TODO: MOCK
  const fetchInvitations = async () => {
    await wait(300)
    invitations.value = mockInvitations()
    loaded.value = true
  }

  // TODO: MOCK
  const respond = async (id: number) => {
    await wait(300)
    invitations.value = invitations.value.filter((invitation) => invitation.id !== id)
  }

  const acceptInvitation = respond
  const declineInvitation = respond

  const reset = () => {
    invitations.value = []
    loaded.value = false
  }

  return {
    invitations,
    loaded,
    count,

    fetchInvitations,
    acceptInvitation,
    declineInvitation,
    reset
  }
}

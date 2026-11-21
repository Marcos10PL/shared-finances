import type { Invitation } from '~/types'

export const useInvitations = () => {
  const api = useApi()
  const invitations = useState<Invitation[]>('invitations', () => [])
  const loaded = useState('invitations:loaded', () => false)
  const error = useState('invitations:error', () => false)

  const count = computed(() => invitations.value.length)

  const fetchInvitations = async () => {
    error.value = false

    try {
      invitations.value = await api<Invitation[]>('/api/invitations')
      loaded.value = true
    } catch {
      error.value = true
    }
  }

  const drop = (id: number) => {
    invitations.value = invitations.value.filter((invitation) => invitation.id !== id)
  }

  const acceptInvitation = async (id: number) => {
    await api(`/api/invitations/${id}/accept`, { method: 'POST' })
    drop(id)
    await useWallets().fetchWallets()
  }

  const declineInvitation = async (id: number) => {
    await api(`/api/invitations/${id}/reject`, { method: 'POST' })
    drop(id)
  }

  const reset = () => {
    invitations.value = []
    loaded.value = false
    error.value = false
  }

  return {
    invitations,
    loaded,
    error,
    count,

    fetchInvitations,
    acceptInvitation,
    declineInvitation,
    reset
  }
}

// TODO: MOCK
export const useInvitations = () => {
  const count = useState('invitations:count', () => 2)

  return { count }
}

<script setup lang="ts">
useHead({ title: 'Ustawienia portfela' })

const { loaded } = useWallets()
const wallet = useCurrentWallet()

const breadcrumb = computed(() =>
  wallet.value
    ? [
        { label: 'Portfele', to: ROUTES.HOME },
        { label: wallet.value.name, to: ROUTES.WALLET(wallet.value.id) },
        { label: 'Ustawienia' }
      ]
    : undefined
)

watchEffect(() => {
  if (!loaded.value) return

  if (!wallet.value) navigateTo(ROUTES.HOME)
  else if (wallet.value.role !== WALLET_ROLES.OWNER) navigateTo(ROUTES.WALLET(wallet.value.id))
})
</script>

<template>
  <AppPage title="Ustawienia" :breadcrumb>
    <div v-if="wallet?.role === WALLET_ROLES.OWNER" class="flex w-full max-w-5xl flex-col">
      <WalletSettingsSection title="Ogólne" description="Nazwa widoczna dla wszystkich członków.">
        <WalletSettingsNameForm :wallet />
      </WalletSettingsSection>

      <WalletSettingsSection
        title="Miesięczny limit wydatków"
        description="Liczony z bieżącego miesiąca kalendarzowego oraz widoczny dla wszystkich członków. Nie blokuje dodawania nowych transakcji."
      >
        <WalletSettingsLimitForm :wallet />
      </WalletSettingsSection>

      <WalletSettingsSection
        title="Osoby w portfelu"
        description="Zapraszanie osób i zarządzanie ich rolami. Członek może dodawać i edytować swoje transakcje, gość ma prawo tylko do podglądu portfela."
      >
        <WalletSettingsMembers :wallet />
      </WalletSettingsSection>

      <WalletSettingsSection title="Strefa niebezpieczna" description="Operacji nie można cofnąć.">
        <WalletSettingsDangerZone :wallet />
      </WalletSettingsSection>
    </div>

    <WalletSettingsSkeleton v-else />
  </AppPage>
</template>

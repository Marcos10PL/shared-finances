<script setup lang="ts">
import type { NavigationMenuItem } from '@nuxt/ui'

const { user, logout } = useAuth()
const { wallets } = useWallets()
const { openNewWallet } = useModals()
const { count: invitationsCount } = useInvitations()

const items = computed<NavigationMenuItem[][]>(() => [
  [
    { label: 'Wszystkie portfele', icon: 'i-lucide-layout-grid', to: ROUTES.HOME, exact: true },
    {
      label: 'Zaproszenia',
      to: ROUTES.INVITATIONS,
      slot: 'invitations' as const,
      badge:
        invitationsCount.value > 0
          ? {
              label: invitationsCount.value,
              color: 'primary' as const,
              variant: 'soft' as const,
              class: 'rounded-full'
            }
          : undefined
    },
    { label: 'Kategorie', icon: 'i-lucide-tag', to: ROUTES.CATEGORIES }
  ],
  [
    { label: 'Portfele', type: 'label' },
    ...wallets.value.map((wallet) => ({
      label: wallet.name,
      value: String(wallet.id),
      to: ROUTES.WALLET(wallet.id),
      slot: 'wallet' as const,
      ui: { link: 'wallet-link' }
    })),
    {
      label: 'Nowy portfel',
      icon: 'i-lucide-plus',
      class: 'text-muted',
      onSelect: () => openNewWallet()
    }
  ]
])

const walletColors = computed(
  () => new Map(wallets.value.map((wallet, index) => [String(wallet.id), getChartColor(index)]))
)
const getWalletColor = (item: NavigationMenuItem) => walletColors.value.get(item.value!)!

const getItemLabel = (item: NavigationMenuItem) => item.label ?? ''

const onLogout = async () => {
  logout()
  await navigateTo(ROUTES.LOGIN)
}
</script>

<template>
  <UDashboardSidebar
    collapsible
    :default-size="16"
    :collapsed-size="4"
    toggle-side="right"
    :ui="{ header: 'px-5', content: 'bg-sidebar sidebar-hover' }"
  >
    <template #header="{ collapsed }">
      <NuxtLink :to="ROUTES.HOME" aria-label="Wspólne finanse" class="flex">
        <AppLogo :icon-only="collapsed" />
      </NuxtLink>
    </template>

    <template #default="{ collapsed }">
      <UNavigationMenu :items :collapsed orientation="vertical">
        <template #invitations-leading="{ ui, active }">
          <span class="relative inline-flex">
            <UIcon name="i-lucide-mail" :class="ui.linkLeadingIcon({ active })" />
            <span
              v-if="collapsed && invitationsCount > 0"
              class="absolute -top-0.5 -right-0.5 size-2 rounded-full bg-primary ring-2 ring-sidebar"
            />
          </span>
        </template>

        <template #wallet-leading="{ item, active }">
          <span
            v-if="collapsed"
            class="inline-flex size-7 shrink-0 items-center justify-center rounded-md text-xs font-semibold"
            :class="[
              getWalletColor(item).bg,
              getWalletColor(item).initials,
              active &&
                'ring-2 ring-primary ring-offset-2 ring-offset-white dark:ring-offset-zinc-900'
            ]"
          >
            {{ getInitials(getItemLabel(item)) }}
          </span>
          <span v-else class="size-2 shrink-0 rounded-xs" :class="getWalletColor(item).bg" />
        </template>
      </UNavigationMenu>
    </template>

    <template #footer="{ collapsed }">
      <div class="flex w-full flex-col gap-1">
        <UUser
          v-if="user && !collapsed"
          :name="user.displayName"
          :description="user.email"
          :ui="{ name: 'text-sm font-medium text-highlighted', description: 'text-xs text-muted' }"
          class="px-2 py-1.5"
        >
          <template #avatar>
            <UiUserAvatar :user-id="user.id" :name="user.displayName" />
          </template>
        </UUser>

        <div class="flex gap-1" :class="collapsed ? 'flex-col items-start' : 'items-center'">
          <UButton
            :label="collapsed ? undefined : 'Wyloguj się'"
            icon="i-lucide-log-out"
            color="neutral"
            variant="ghost"
            class="h-9 justify-start gap-2.5 font-medium text-default"
            :class="collapsed ? 'w-9 px-2.25' : 'flex-1 px-2.25'"
            :ui="{ leadingIcon: 'size-4.5' }"
            aria-label="Wyloguj się"
            loading-auto
            @click="onLogout"
          />
          <UColorModeButton class="size-9 justify-center" :ui="{ leadingIcon: 'size-4.5' }" />
        </div>
      </div>
    </template>
  </UDashboardSidebar>
</template>

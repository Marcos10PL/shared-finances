<script setup lang="ts">
useHead({ title: 'Portfele' })

const { wallets, loaded, error, fetchWallets } = useWallets()
const { openNewWallet } = useModals()
</script>

<template>
  <AppPage title="Portfele">
    <template #right>
      <UButton
        class="max-sm:hidden"
        icon="i-lucide-plus"
        label="Nowy portfel"
        @click="openNewWallet"
      />
    </template>

    <UEmpty
      v-if="error"
      icon="i-lucide-triangle-alert"
      title="Nie udało się wczytać portfeli."
      description="Sprawdź połączenie i spróbuj ponownie."
      :actions="[{ label: 'Spróbuj ponownie', icon: 'i-lucide-refresh-cw', onClick: fetchWallets }]"
      class="m-auto"
    />

    <UEmpty
      v-else-if="loaded && wallets.length === 0"
      icon="i-lucide-wallet"
      title="Nie masz jeszcze żadnego portfela."
      description="Utwórz nowy i zaproś do niego innych!"
      :actions="[
        { label: 'Utwórz portfel', icon: 'i-lucide-plus', onClick: () => openNewWallet() }
      ]"
      class="m-auto"
    />

    <section
      v-else
      aria-label="Lista portfeli"
      :aria-busy="!loaded"
      class="grid grid-cols-[repeat(auto-fill,minmax(22.5rem,1fr))] gap-4"
    >
      <template v-if="loaded">
        <WalletCard v-for="(wallet, index) in wallets" :key="wallet.id" :wallet :index />
      </template>
      <template v-else>
        <USkeleton v-for="n in 3" :key="n" class="h-62" />
      </template>

      <UButton
        color="neutral"
        variant="ghost"
        block
        class="min-h-56 flex-col justify-center gap-2.5 rounded-lg max-sm:hidden border border-dashed border-accented bg-muted text-highlighted"
        @click="openNewWallet"
      >
        <span
          class="inline-flex size-7 items-center justify-center rounded-md bg-default ring ring-default"
        >
          <UIcon name="i-lucide-plus" class="size-4" />
        </span>
        Utwórz portfel
      </UButton>
    </section>

    <template v-if="loaded && wallets.length > 0" #footer>
      <div class="border-t border-default bg-default p-4 sm:hidden">
        <UButton
          block
          size="lg"
          icon="i-lucide-plus"
          label="Utwórz portfel"
          @click="openNewWallet"
        />
      </div>
    </template>
  </AppPage>
</template>

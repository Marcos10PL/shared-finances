<script setup lang="ts">
useHead({ title: 'Zaproszenia' })

const { invitations, loaded, error, fetchInvitations } = useInvitations()

useRefreshOnEnter(loaded, fetchInvitations)
</script>

<template>
  <AppPage title="Zaproszenia">
    <UiLoadError v-if="error" title="Nie udało się wczytać zaproszeń." @retry="fetchInvitations" />

    <UEmpty
      v-else-if="loaded && invitations.length === 0"
      icon="i-lucide-mail"
      title="Nie masz jeszcze żadnych zaproszeń."
      description="Zaproszenia do portfeli pojawią się tutaj."
      class="m-auto"
    />

    <div v-else class="@container mx-auto flex w-full max-w-7xl flex-col gap-4">
      <UCard variant="flush">
        <ul v-if="!loaded" class="divide-y divide-default" aria-busy="true">
          <InvitationSkeleton v-for="index in 2" :key="index" />
        </ul>

        <ul v-else class="divide-y divide-default">
          <InvitationItem
            v-for="invitation in invitations"
            :key="invitation.id"
            :invitation="invitation"
          />
        </ul>
      </UCard>

      <p class="text-sm text-muted">
        Po przyjęciu zaproszenia portfel pojawi się na Twojej liście.
      </p>
    </div>
  </AppPage>
</template>

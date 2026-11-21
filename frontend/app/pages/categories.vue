<script setup lang="ts">
useHead({ title: 'Kategorie' })

const { loaded, error, fetchCategories } = useCategories()

if (!loaded.value) fetchCategories()
</script>

<template>
  <AppPage title="Kategorie">
    <UiLoadError v-if="error" title="Nie udało się wczytać kategorii." @retry="fetchCategories" />

    <div v-else class="@container mx-auto w-full max-w-7xl">
      <div class="grid items-start gap-4 @3xl:grid-cols-[minmax(0,1fr)_18rem]">
        <CategoryOwnCard />
        <CategoryDefaultCard />
      </div>
    </div>
  </AppPage>
</template>

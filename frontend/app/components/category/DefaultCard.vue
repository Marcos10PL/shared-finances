<script setup lang="ts">
const { categories, loaded } = useCategories()

const defaults = computed(() => categories.value.filter((category) => category.userId === null))

const groups = computed(() =>
  Object.values(CATEGORY_TYPES).map((type) => ({
    type,
    title: CATEGORY_TYPE_GROUP_LABELS[type],
    items: defaults.value.filter((category) => category.type === type)
  }))
)
</script>

<template>
  <UCard variant="flush">
    <h2 class="p-4 text-base font-semibold text-highlighted">Kategorie domyślne</h2>

    <template v-if="!loaded">
      <section
        v-for="(rows, group) in [7, 3]"
        :key="group"
        class="border-t border-default"
        aria-busy="true"
      >
        <h3 class="px-4 pt-4 pb-1 text-xs text-muted">{{ groups[group]?.title }}</h3>

        <ul class="divide-y divide-default">
          <li v-for="index in rows" :key="index" class="flex items-center gap-3 px-4 py-3">
            <USkeleton class="size-8 shrink-0 rounded-md" />
            <USkeleton class="h-5 w-28" />
          </li>
        </ul>
      </section>
    </template>

    <template v-else>
      <section v-for="group in groups" :key="group.type" class="border-t border-default">
        <h3 class="px-4 pt-4 pb-1 text-xs text-muted">{{ group.title }}</h3>

        <ul class="divide-y divide-default">
          <li
            v-for="category in group.items"
            :key="category.id"
            class="flex items-center gap-3 px-4 py-3"
          >
            <CategoryTypeIcon :type="category.type" />
            <span class="truncate text-sm text-highlighted">
              {{ category.name }}
            </span>
          </li>
        </ul>
      </section>
    </template>
  </UCard>
</template>

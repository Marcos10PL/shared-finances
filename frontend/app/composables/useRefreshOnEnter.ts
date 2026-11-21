import type { Ref } from 'vue'

export const useRefreshOnEnter = (loaded: Ref<boolean>, fetch: () => unknown) =>
  onMounted(() => {
    if (loaded.value) fetch()
  })

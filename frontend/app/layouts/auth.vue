<template>
  <div
    class="flex min-h-dvh flex-col items-center justify-center gap-6 bg-muted bg-[radial-gradient(var(--ui-border-accented)_0.0625rem,transparent_0.0625rem)] bg-size-[1.25rem_1.25rem] px-4 py-12"
  >
    <div class="flex w-full max-w-100 items-center justify-between">
      <AppLogo />
      <UColorModeButton />
    </div>

    <slot />

    <footer class="text-xs text-muted">
      © {{ new Date().getFullYear() }} Wspólne finanse. Wszelkie prawa zastrzeżone.
    </footer>
  </div>
</template>

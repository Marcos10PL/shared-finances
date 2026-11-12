<template>
  <div
    class="flex min-h-dvh flex-col items-center justify-center gap-6 bg-muted bg-[radial-gradient(var(--ui-border-accented)_1px,transparent_1px)] bg-size-[20px_20px] px-4 py-12"
  >
    <div class="flex w-full max-w-100 items-center justify-between">
      <AppLogo />
      <UColorModeButton />
    </div>

    <slot />
  </div>
</template>

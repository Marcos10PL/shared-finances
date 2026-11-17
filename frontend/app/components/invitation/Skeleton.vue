<template>
  <li class="flex flex-col gap-3 p-4 @xl:flex-row @xl:items-center @xl:gap-4">
    <div class="flex min-w-0 flex-1 items-center gap-3">
      <USkeleton class="size-8 shrink-0 rounded-full" />

      <div class="flex flex-col">
        <USkeleton class="h-5 w-full @xl:w-64" />
        <USkeleton class="h-5 w-40 @xl:hidden" />
        <USkeleton class="h-4 w-36" />
      </div>
    </div>

    <div class="grid grid-cols-2 gap-2 @xl:flex">
      <USkeleton class="h-8 w-full @xl:w-20" />
      <USkeleton class="h-8 w-full @xl:w-24" />
    </div>
  </li>
</template>

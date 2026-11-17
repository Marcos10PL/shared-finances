<template>
  <li class="flex flex-col gap-3 p-4 @xl:flex-row @xl:items-center @xl:gap-4">
    <UiUserSkeleton name-class="w-full @xl:w-64" />

    <div class="grid grid-cols-2 gap-2 @xl:flex">
      <USkeleton class="h-9 w-full @xl:w-20" />
      <USkeleton class="h-9 w-full @xl:w-24" />
    </div>
  </li>
</template>

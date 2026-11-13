export default defineAppConfig({
  ui: {
    colors: {
      primary: 'indigo',
      secondary: 'sky',
      neutral: 'zinc',
      success: 'emerald',
      warning: 'amber',
      error: 'red'
    },
    button: {
      slots: {
        base: 'cursor-pointer'
      },
      defaultVariants: {
        size: 'md'
      },
      compoundVariants: [
        {
          color: 'primary',
          variant: 'solid',
          class: 'hover:bg-(--primary-hover) active:bg-(--primary-hover)'
        },
        {
          color: 'neutral',
          variant: ['ghost', 'outline'],
          class: 'hover:bg-(--hover-bg) active:bg-(--hover-bg)'
        }
      ]
    },
    dropdownMenu: {
      slots: {
        item: 'cursor-pointer'
      }
    },
    select: {
      slots: {
        item: 'cursor-pointer'
      }
    },
    card: {
      slots: {
        root: 'rounded-lg',
        header: 'p-4 sm:px-5',
        body: 'p-5 sm:p-5',
        footer: 'p-4 sm:px-5'
      },
      variants: {
        variant: {
          interactive: {
            root: 'relative cursor-pointer bg-default ring ring-default divide-y divide-default transition-shadow hover:shadow-(--card-hover-shadow) hover:ring-(--card-hover-ring)'
          },
          settings: {
            root: 'bg-default ring ring-default divide-y divide-default',
            footer: 'flex items-center justify-end gap-2 bg-muted py-3.5'
          },
          danger: {
            root: 'bg-default ring ring-error/40 divide-y divide-default'
          },
          flush: {
            root: 'bg-default ring ring-default divide-y divide-default',
            body: 'p-0 sm:p-0'
          }
        }
      }
    },
    badge: {
      compoundVariants: [
        { size: ['sm', 'md'], class: 'rounded-full px-2.5 py-0.5' },
        { size: 'sm', class: 'text-[0.7rem]' }
      ]
    },
    formField: {
      variants: {
        required: {
          true: {
            label: 'after:ms-1'
          }
        }
      }
    },
    dashboardPanel: {
      slots: {
        root: 'bg-canvas'
      }
    },
    dashboardSidebar: {
      slots: {
        root: 'sidebar-hover overflow-hidden bg-sidebar transition-[width] duration-200 ease-out motion-reduce:transition-none',
        body: 'gap-5 px-3.5 py-4',
        footer: 'px-3.5 py-3'
      }
    },
    dashboardNavbar: {
      slots: {
        root: 'bg-default/70'
      }
    },
    navigationMenu: {
      slots: {
        root: 'gap-5',
        list: 'flex flex-col gap-0.5',
        separator: 'hidden',
        label: 'px-2.5 pb-1.5 font-medium text-muted',
        link: 'cursor-pointer gap-2.5',
        linkLeadingIcon: 'size-4.5'
      },
      compoundVariants: [
        { orientation: 'vertical', class: { link: 'min-h-9 px-2.25 before:inset-y-0' } },
        {
          orientation: 'vertical',
          active: false,
          class: {
            link: 'text-default hover:text-highlighted hover:before:bg-(--hover-bg)',
            linkLeadingIcon: 'text-muted'
          }
        },
        {
          orientation: 'vertical',
          active: true,
          class: {
            link: 'text-highlighted before:bg-(--sidebar-active-bg) before:shadow-(--sidebar-active-shadow) before:ring-1 before:ring-(--sidebar-active-ring)',
            linkLeadingIcon: 'text-(--sidebar-active-icon)'
          }
        }
      ]
    },
    link: {
      base: 'font-medium',
      variants: {
        active: {
          false: 'text-primary'
        }
      },
      compoundVariants: [{ active: false, disabled: false, class: 'hover:text-(--primary-hover)' }]
    },
    breadcrumb: {
      slots: {
        link: 'text-base'
      },
      compoundVariants: [{ active: true, class: { link: 'text-highlighted' } }]
    },
    avatar: {
      slots: {
        fallback: 'font-semibold'
      },
      compoundVariants: [
        { size: 'xs', class: { fallback: 'text-[0.625rem]' } },
        { size: 'md', class: { fallback: 'text-xs' } }
      ]
    },
    user: {
      slots: {
        wrapper: 'min-w-0',
        name: 'truncate',
        description: 'truncate'
      }
    },
    skeleton: {
      base: 'rounded-lg bg-accented dark:bg-elevated'
    }
  }
})

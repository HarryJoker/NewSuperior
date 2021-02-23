import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/* Router Modules */
// import componentsRouter from './modules/components'
// import chartsRouter from './modules/charts'
// import tableRouter from './modules/table'
// import nestedRouter from './modules/nested'

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'/'el-icon-x' the icon show in the sidebar
    noCache: true                if set true, the page will no be cached(default is false)
    affix: true                  if set true, the tag will affix in the tags-view
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect/index')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },
  {
    path: '/auth-redirect',
    component: () => import('@/views/login/auth-redirect'),
    hidden: true
  },
  {
    path: '/404',
    component: () => import('@/views/error-page/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error-page/401'),
    hidden: true
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard/index'),
        name: 'Dashboard',
        meta: { title: '控制台', icon: 'dashboard', affix: true }
      }
    ]
  },
  {
    path: '/admin/task',
    component: Layout,
    redirect: '/admin/task/1report/list',
    name: '任务管理',
    meta: {
      title: '任务管理',
      icon: 'excel'
    },
    children: [
      {
        path: '1report',
        component: () => import('@/views/admin/task/1report/index'),
        name: '政府工作报告',
        meta: { title: '政府工作报告' },
        redirect: '/admin/task/1report/list',
        children: [
          {
            path: 'list:id=1',
            component: () => import('@/views/admin/task/1report/list'),
            name: 'list',
            meta: { title: '任务列表' }
          },
          {
            path: 'draft:id=1',
            component: () => import('@/views/admin/task/1report/draft'),
            name: 'draft',
            meta: { title: '文稿管理' }
          },
          {
            path: 'draft/tasks:id=1',
            component: () => import('@/views/admin/task/1report/draft/tasks'),
            hidden: true,
            name: 'draftTasks',
            meta: { title: '文稿任务' }
          }
        ]
      },
      {
        path: '2report',
        component: () => import('@/views/admin/task/1report/index'),
        name: '7+3重点改革任务',
        meta: { title: '7+3重点改革任务' },
        redirect: '/admin/task/1report/list',
        children: [
          {
            path: 'list:id=2',
            component: () => import('@/views/admin/task/1report/list'),
            name: 'list',
            meta: { title: '任务列表' }
          },
          {
            path: 'draft:id=2',
            component: () => import('@/views/admin/task/1report/draft'),
            name: 'draft',
            meta: { title: '文稿管理' }
          },
          {
            path: 'draft/tasks:id=2',
            component: () => import('@/views/admin/task/1report/draft/tasks'),
            hidden: true,
            name: 'draftTasks',
            meta: { title: '文稿任务' }
          }
        ]
      },
      {
        path: '3report',
        component: () => import('@/views/admin/task/1report/index'),
        name: '建议提案',
        meta: { title: '建议提案' },
        redirect: '/admin/task/1report/list',
        children: [
          {
            path: 'list:id=3',
            component: () => import('@/views/admin/task/1report/list'),
            name: 'list',
            meta: { title: '任务列表' }
          },
          {
            path: 'draft:id=3',
            component: () => import('@/views/admin/task/1report/draft'),
            name: 'draft',
            meta: { title: '文稿管理' }
          },
          {
            path: 'draft/tasks:id=3',
            component: () => import('@/views/admin/task/1report/draft/tasks'),
            hidden: true,
            name: 'draftTasks',
            meta: { title: '文稿任务' }
          }
        ]
      },
      {
        path: '4report',
        component: () => import('@/views/admin/task/1report/index'),
        name: '会议议定事项',
        meta: { title: '会议议定事项' },
        redirect: '/admin/task/1report/list',
        children: [
          {
            path: 'list:id=4',
            component: () => import('@/views/admin/task/1report/list'),
            name: 'list',
            meta: { title: '任务列表' }
          },
          {
            path: 'draft:id=4',
            component: () => import('@/views/admin/task/1report/draft'),
            name: 'draft',
            meta: { title: '文稿管理' }
          },
          {
            path: 'draft/tasks:id=4',
            component: () => import('@/views/admin/task/1report/draft/tasks'),
            hidden: true,
            name: 'draftTasks',
            meta: { title: '文稿任务' }
          }
        ]
      },
      {
        path: '5report',
        component: () => import('@/views/admin/task/1report/index'),
        name: '领导批示',
        meta: { title: '领导批示' },
        redirect: '/admin/task/1report/list',
        children: [
          {
            path: 'list:id=5',
            component: () => import('@/views/admin/task/1report/list'),
            name: 'list',
            meta: { title: '任务列表' }
          },
          {
            path: 'draft:id=5',
            component: () => import('@/views/admin/task/1report/draft'),
            name: 'draft',
            meta: { title: '文稿管理' }
          },
          {
            path: 'draft/tasks:id=5',
            component: () => import('@/views/admin/task/1report/draft/tasks'),
            hidden: true,
            name: 'draftTasks',
            meta: { title: '文稿任务' }
          }
        ]
      },
      {
        path: '6report',
        component: () => import('@/views/admin/task/1report/index'),
        name: '专项督查',
        meta: { title: '专项督查' },
        redirect: '/admin/task/1report/list',
        children: [
          {
            path: 'list:id=6',
            component: () => import('@/views/admin/task/1report/list'),
            name: 'list',
            meta: { title: '任务列表' }
          },
          {
            path: 'draft:id=6',
            component: () => import('@/views/admin/task/1report/draft'),
            name: 'draft',
            meta: { title: '文稿管理' }
          },
          {
            path: 'draft/tasks:id=6',
            component: () => import('@/views/admin/task/1report/draft/tasks'),
            hidden: true,
            name: 'draftTasks',
            meta: { title: '文稿任务' }
          }
        ]
      },
      {
        path: '7report',
        component: () => import('@/views/admin/task/1report/index'),
        name: '重点项目',
        meta: { title: '重点项目' },
        redirect: '/admin/task/1report/list',
        children: [
          {
            path: 'list:id=7',
            component: () => import('@/views/admin/task/1report/list'),
            name: 'list',
            meta: { title: '任务列表' }
          },
          {
            path: 'draft:id=7',
            component: () => import('@/views/admin/task/1report/draft'),
            name: 'draft',
            meta: { title: '文稿管理' }
          },
          {
            path: 'draft/tasks:id=7',
            component: () => import('@/views/admin/task/1report/draft/tasks'),
            hidden: true,
            name: 'draftTasks',
            meta: { title: '文稿任务' }
          }
        ]
      },
      {
        path: '8report',
        component: () => import('@/views/admin/task/1report/index'),
        name: '线索督查',
        meta: { title: '线索督查' },
        redirect: '/admin/task/1report/list',
        children: [
          {
            path: 'list:id=8',
            component: () => import('@/views/admin/task/1report/list'),
            name: 'list',
            meta: { title: '任务列表' }
          },
          {
            path: 'draft:id=8',
            component: () => import('@/views/admin/task/1report/draft'),
            name: 'draft',
            meta: { title: '文稿管理' }
          },
          {
            path: 'draft/tasks:id=8',
            component: () => import('@/views/admin/task/1report/draft/tasks'),
            hidden: true,
            name: 'draftTasks',
            meta: { title: '文稿任务' }
          }
        ]
      },
      {
        path: '9report',
        component: () => import('@/views/admin/task/1report/index'),
        name: '民生在线',
        meta: { title: '民生在线' },
        redirect: '/admin/task/1report/list',
        children: [
          {
            path: 'list:id=9',
            component: () => import('@/views/admin/task/1report/list'),
            name: 'list',
            meta: { title: '任务列表' }
          },
          {
            path: 'draft:id=9',
            component: () => import('@/views/admin/task/1report/draft'),
            name: 'draft',
            meta: { title: '文稿管理' }
          },
          {
            path: 'draft/tasks:id=9',
            component: () => import('@/views/admin/task/1report/draft/tasks'),
            hidden: true,
            name: 'draftTasks',
            meta: { title: '文稿任务' }
          }
        ]
      }
    ]
  },
  {
    path: '/admin/people',
    component: Layout,
    edirect: '/admin/people/goverment/report',
    name: '群众督',
    meta: {
      title: '群众督',
      icon: 'excel'
    },
    children: [
      {
        path: 'goverment',
        component: () => import('@/views/admin/people/goverment/index'),
        name: '政务督查',
        meta: { title: '政务督查' },
        children: [
          // {
          //   path: 'report',
          //   component: () => import('@/views/admin/people/goverment/report'),
          //   name: '政府工作报告',
          //   meta: { title: '政府工作报告' }
          // },
          {
            path: 'public',
            component: () => import('@/views/admin/people/goverment/public'),
            name: '政务督查公开',
            meta: { title: '政务督查公开' }
          },
          {
            path: 'clue',
            component: () => import('@/views/admin/people/goverment/clue'),
            name: '督查线索征集',
            meta: { title: '督查线索征集' }
          }
        ]
      },
      {
        path: 'reward',
        component: () => import('@/views/admin/people/reward'),
        name: '督查激励',
        meta: { title: '督查激励' }
      },
      {
        path: 'review',
        component: () => import('@/views/admin/people/review'),
        name: '政创空间',
        meta: { title: '政创空间' }
      },
      {
        path: 'live',
        component: () => import('@/views/admin/people/live/index'),
        name: '民生在线',
        meta: { title: '民生在线' },
        children: [
          {
            path: 'opinion',
            component: () => import('@/views/admin/people/live/opinion'),
            name: '意见征集',
            meta: { title: '意见征集' }
          },
          {
            path: 'vote',
            component: () => import('@/views/admin/people/live/vote'),
            name: '网络投票',
            meta: { title: '网络投票' }
          },
          {
            path: 'progress',
            component: () => import('@/views/admin/people/live/progress'),
            name: '完成情况',
            meta: { title: '完成情况' }
          },
          {
            path: 'comment',
            component: () => import('@/views/admin/people/live/comment'),
            name: '公众评价',
            meta: { title: '公众评价' }
          }
        ]
      }
    ]
  },
  {
    path: '/admin/user',
    component: Layout,
    redirect: '/admin/user/in',
    name: '用户管理',
    meta: {
      title: '用户管理',
      icon: 'excel'
    },
    children: [
      {
        path: 'in',
        component: () => import('@/views/admin/user/in'),
        name: '内网用户',
        meta: { title: '内网用户' }
      },
      {
        path: 'out',
        component: () => import('@/views/admin/user/out'),
        name: '群众用户',
        meta: { title: '群众用户' }
      }
    ]
  },
  {
    path: '/admin/message',
    component: Layout,
    edirect: '/admin/message',
    name: '消息管理',
    meta: {
      title: '消息管理',
      icon: 'excel'
    },
    children: [
      {
        path: 'index',
        component: () => import('@/views/admin/message/index'),
        name: '消息管理',
        meta: { title: '消息管理' }
      }
    ]
  },
  {
    path: '/admin/unit',
    component: Layout,
    edirect: '/admin/unit',
    name: '部门管理',
    meta: {
      title: '部门管理',
      icon: 'excel'
    },
    children: [
      {
        path: 'index',
        component: () => import('@/views/admin/unit/index'),
        name: '部门管理',
        meta: { title: '部门管理' }
      }
    ]
  },
  {
    path: '/admin/banner',
    component: Layout,
    edirect: '/admin/banner',
    name: 'Banner管理',
    meta: {
      title: 'Banner管理',
      icon: 'excel'
    },
    children: [
      {
        path: 'index',
        component: () => import('@/views/admin/banner/index'),
        name: 'Banner管理',
        meta: { title: 'Banner管理' }
      }
    ]
  },
  {
    path: '/admin/version',
    component: Layout,
    edirect: '/admin/version',
    name: '版本管理',
    meta: {
      title: '版本管理',
      icon: 'excel'
    },
    children: [
      {
        path: 'index',
        component: () => import('@/views/admin/version/index'),
        name: '版本管理',
        meta: { title: '版本管理' }
      }
    ]
  },
  {
    path: '/test',
    component: Layout,
    edirect: '/test',
    hidden: true,
    name: '测试Excel',
    meta: {
      title: '测试Excel',
      icon: 'excel'
    },
    children: [
      {
        path: 'index',
        component: () => import('@/views/test/index'),
        name: '测试Excel',
        meta: { title: '测试Excel' }
      }
    ]
  },
  {
    path: '/documentation',
    component: Layout,
    hidden: true,
    children: [
      {
        path: 'index',
        component: () => import('@/views/documentation/index'),
        name: '使用文档',
        meta: { title: '使用文档', icon: 'documentation', affix: true }
      }
    ]
  },
  // {
  //   path: '/guide',
  //   component: Layout,
  //   redirect: '/guide/index',
  //   children: [
  //     {
  //       path: 'index',
  //       component: () => import('@/views/guide/index'),
  //       name: 'Guide',
  //       meta: { title: 'Guide', icon: 'guide', noCache: true }
  //     }
  //   ]
  // },
  {
    path: '/profile',
    component: Layout,
    redirect: '/profile/index',
    hidden: true,
    children: [
      {
        path: 'index',
        component: () => import('@/views/profile/index'),
        name: 'Profile',
        meta: { title: 'Profile', icon: 'user', noCache: true }
      }
    ]
  }
]

/**
 * asyncRoutes
 * the routes that need to be dynamically loaded based on user roles
 */
export const asyncRoutes = [
  {
    path: '/permission',
    component: Layout,
    redirect: '/permission/page',
    alwaysShow: true, // will always show the root menu
    name: '权限管理',
    meta: {
      title: '权限管理',
      icon: 'lock',
      roles: ['admin', 'editor'] // you can set roles in root nav
    },
    children: [
      {
        path: 'page',
        component: () => import('@/views/permission/page'),
        name: 'PagePermission',
        meta: {
          title: 'Page Permission',
          roles: ['admin'] // or you can only set roles in sub nav
        }
      },
      {
        path: 'directive',
        component: () => import('@/views/permission/directive'),
        name: 'DirectivePermission',
        meta: {
          title: 'Directive Permission'
          // if do not set roles, means: this page does not require permission
        }
      },
      {
        path: 'role',
        component: () => import('@/views/permission/role'),
        name: 'RolePermission',
        meta: {
          title: 'Role Permission',
          roles: ['admin']
        }
      }
    ]
  },

  // {
  //   path: '/icon',
  //   component: Layout,
  //   children: [
  //     {
  //       path: 'index',
  //       component: () => import('@/views/icons/index'),
  //       name: 'Icons',
  //       meta: { title: 'Icons', icon: 'icon', noCache: true }
  //     }
  //   ]
  // },

  // /** when your routing map is too long, you can split it into small modules **/
  // componentsRouter,
  // chartsRouter,
  // nestedRouter,
  // tableRouter,

  // {
  //   path: '/example',
  //   component: Layout,
  //   redirect: '/example/list',
  //   name: 'Example',
  //   meta: {
  //     title: 'Example',
  //     icon: 'el-icon-s-help'
  //   },
  //   children: [
  //     {
  //       path: 'create',
  //       component: () => import('@/views/example/create'),
  //       name: 'CreateArticle',
  //       meta: { title: 'Create Article', icon: 'edit' }
  //     },
  //     {
  //       path: 'edit/:id(\\d+)',
  //       component: () => import('@/views/example/edit'),
  //       name: 'EditArticle',
  //       meta: { title: 'Edit Article', noCache: true, activeMenu: '/example/list' },
  //       hidden: true
  //     },
  //     {
  //       path: 'list',
  //       component: () => import('@/views/example/list'),
  //       name: 'ArticleList',
  //       meta: { title: 'Article List', icon: 'list' }
  //     }
  //   ]
  // },

  // {
  //   path: '/tab',
  //   component: Layout,
  //   children: [
  //     {
  //       path: 'index',
  //       component: () => import('@/views/tab/index'),
  //       name: 'Tab',
  //       meta: { title: 'Tab', icon: 'tab' }
  //     }
  //   ]
  // },

  // {
  //   path: '/error',
  //   component: Layout,
  //   redirect: 'noRedirect',
  //   name: 'ErrorPages',
  //   meta: {
  //     title: 'Error Pages',
  //     icon: '404'
  //   },
  //   children: [
  //     {
  //       path: '401',
  //       component: () => import('@/views/error-page/401'),
  //       name: 'Page401',
  //       meta: { title: '401', noCache: true }
  //     },
  //     {
  //       path: '404',
  //       component: () => import('@/views/error-page/404'),
  //       name: 'Page404',
  //       meta: { title: '404', noCache: true }
  //     }
  //   ]
  // },

  // {
  //   path: '/error-log',
  //   component: Layout,
  //   children: [
  //     {
  //       path: 'log',
  //       component: () => import('@/views/error-log/index'),
  //       name: 'ErrorLog',
  //       meta: { title: 'Error Log', icon: 'bug' }
  //     }
  //   ]
  // },

  // {
  //   path: '/excel',
  //   component: Layout,
  //   redirect: '/excel/export-excel',
  //   name: 'Excel',
  //   meta: {
  //     title: 'Excel',
  //     icon: 'excel'
  //   },
  //   children: [
  //     {
  //       path: 'export-excel',
  //       component: () => import('@/views/excel/export-excel'),
  //       name: 'ExportExcel',
  //       meta: { title: 'Export Excel' }
  //     },
  //     {
  //       path: 'export-selected-excel',
  //       component: () => import('@/views/excel/select-excel'),
  //       name: 'SelectExcel',
  //       meta: { title: 'Export Selected' }
  //     },
  //     {
  //       path: 'export-merge-header',
  //       component: () => import('@/views/excel/merge-header'),
  //       name: 'MergeHeader',
  //       meta: { title: 'Merge Header' }
  //     },
  //     {
  //       path: 'upload-excel',
  //       component: () => import('@/views/excel/upload-excel'),
  //       name: 'UploadExcel',
  //       meta: { title: 'Upload Excel' }
  //     }
  //   ]
  // },

  // {
  //   path: '/zip',
  //   component: Layout,
  //   redirect: '/zip/download',
  //   alwaysShow: true,
  //   name: 'Zip',
  //   meta: { title: 'Zip', icon: 'zip' },
  //   children: [
  //     {
  //       path: 'download',
  //       component: () => import('@/views/zip/index'),
  //       name: 'ExportZip',
  //       meta: { title: 'Export Zip' }
  //     }
  //   ]
  // },

  // {
  //   path: '/pdf',
  //   component: Layout,
  //   redirect: '/pdf/index',
  //   children: [
  //     {
  //       path: 'index',
  //       component: () => import('@/views/pdf/index'),
  //       name: 'PDF',
  //       meta: { title: 'PDF', icon: 'pdf' }
  //     }
  //   ]
  // },
  // {
  //   path: '/pdf/download',
  //   component: () => import('@/views/pdf/download'),
  //   hidden: true
  // },

  // {
  //   path: '/theme',
  //   component: Layout,
  //   children: [
  //     {
  //       path: 'index',
  //       component: () => import('@/views/theme/index'),
  //       name: 'Theme',
  //       meta: { title: 'Theme', icon: 'theme' }
  //     }
  //   ]
  // },

  // {
  //   path: '/clipboard',
  //   component: Layout,
  //   children: [
  //     {
  //       path: 'index',
  //       component: () => import('@/views/clipboard/index'),
  //       name: 'ClipboardDemo',
  //       meta: { title: 'Clipboard', icon: 'clipboard' }
  //     }
  //   ]
  // },

  // {
  //   path: 'external-link',
  //   component: Layout,
  //   children: [
  //     {
  //       path: 'https://github.com/PanJiaChen/vue-element-admin',
  //       meta: { title: 'External Link', icon: 'link' }
  //     }
  //   ]
  // },

  // 404 page must be placed at the end !!!
  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router

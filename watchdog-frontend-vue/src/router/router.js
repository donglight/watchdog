import MainContent from '../views/MainContent'
import UrlMonitor from '../views/UrlMonitor'
import UrlList from '../views/UrlList'
import Home from '../views/Home'
import NotFoundComponent from '../views/NotFoundComponent'
import AppInfo from '../views/AppInfo'
import Url from '../views/Url'
import OSInfo from '../views/OSInfo'
import jvmInfo from '../views/JvmInfo'
import ThreadMonitor from '../views/ThreadMonitor'
import JVmMonitor from '../views/JVmMonitor'

export const mainRoutes = [
  {
    path: '/',
    component: Home,
    children: [
      {
        path: '/:appName/:instanceId',
        component: MainContent,
        props: true,
        children: [
          {
            path: 'appInfo',
            name: 'appInfo',
            component: AppInfo
          },
          {
            path: 'url',
            component: Url,
            children: [
              {
                path: '',
                component: UrlList
              },
              {
                path: ':urlId',
                name: 'urlState',
                component: UrlMonitor,
                props: (route) => ({ urlId: route.params.urlId })
              }
            ]
          },
          {
            path: 'jvmMonitor',
            name: 'jvmMonitor',
            component: JVmMonitor
          },
          {
            path: 'jvmInfo',
            name: 'jvmInfo',
            component: jvmInfo
          },
          {
            path: 'osInfo',
            name: 'osInfo',
            component: OSInfo
          },
          {
            path: 'threadMonitor',
            name: 'threadMonitor',
            component: ThreadMonitor
          }
        ]
      }
    ]
  },
  {
    // 会匹配所有路径
    path: '*',
    component: NotFoundComponent
  }
]

export const routes = [
  ...mainRoutes
]

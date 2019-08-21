import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'

axios.defaults.baseURL = 'http://localhost:10000/api'
axios.defaults.timeout = 10000

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    // 应用列表
    applications: [],
    // 当前监控应用的ID
    currentApp: {},
    currentInstance: {
      refreshMetricsIntervalInSecs: 4
    },
    appAndInsPath: '',
    currentUrlList: []
  },
  mutations: {
    refreshApps (state, data) {
      state.applications = data
      if (state.currentInstance.instanceId) {
        state.currentApp = state.applications
          .find(app => app.appName === state.currentApp.appName)
        state.currentInstance = state.currentApp.instanceInfos[state.currentInstance.instanceId]
      }
    },
    setCurrentAppAndInstance (state, instance) {
      state.appAndInsPath = '/' + instance.appName + '/' + instance.instanceId
      setTimeout(function () {
        state.currentApp = state.applications
          .find(app => app.appName === instance.appName)
        state.currentInstance = state.currentApp.instanceInfos[instance.instanceId]
        state.currentUrlList = state.currentInstance.urlInfoList
      }, 1200)
    },
    refreshUrls (state, data) {
      if (data !== '' && data.length > 0) {
        state.currentUrlList = data
      }
    },
    sortUrlList (state, sortBy) {
      state.currentUrlList.sort(sortBy)
    }
  },
  actions: {
    refreshApps ({ commit }) {
      axios.get('').then(function (response) {
        commit('refreshApps', response.data.data)
      })
    },
    pollingRefreshApps ({ commit, dispatch }) {
      // 递归长轮询
      axios.get('/changed_apps', { timeout: 1000 * 15 })
        .then(function (response) {
          if (response.data.code === 200) {
            // 有数据刷新
            commit('refreshApps', response.data.data)
          }
          dispatch('pollingRefreshApps')
        }).catch(function () {
          setTimeout(function () {
            dispatch('pollingRefreshApps')
          }, 1000 * 10)
        })
    },
    pollingRefreshUrls ({ state, commit, dispatch }) {
      axios.get(state.appAndInsPath + '/changed_urls', { timeout: 1000 * 15 })
        .then(function (response) {
          if (response.data.code === 200) {
            // 有数据刷新
            commit('refreshUrls', response.data.data)
          }
          dispatch('pollingRefreshUrls')
        }).catch(function () {
          setTimeout(function () {
            dispatch('pollingRefreshUrls')
          }, 1000 * 10)
        })
    }
  },
  getters: {
    getAllApps: state => {
      return state.applications
    },
    getMetricsRefreshTime: state => {
      return state.currentInstance.refreshMetricsIntervalInSecs * 1000
    },
    getAppCount: state => {
      return state.applications.length
    },
    getCurrentInstanceId: state => {
      return state.currentInstance.instanceId
    },
    getTotal: (state) => () => {
      if (state.applications.length > 0) {
        return state.currentUrlList.length
      }
    },
    pagingUrlList: (state) => (currentPage, pageSize, url) => {
      if (state.applications.length > 0) {
        let start = (currentPage - 1) * pageSize
        return state.currentUrlList
          .filter(data => !url.trim() || data.url.trim().toLowerCase().includes(url.trim().toLowerCase()))
          .slice(start, start + pageSize)
      }
    }
  }
})

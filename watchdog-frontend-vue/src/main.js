import Vue from 'vue'
import App from './App.vue'
import store from './store'
import router from './router'
import ElementUI from 'element-ui'
import echarts from 'echarts'
import 'element-ui/lib/theme-chalk/index.css'
import '@/assets/scss/element-variables.scss'
import '@/assets/css/style.css'
import axios from 'axios'
import utils from './utils/index.js'
axios.defaults.baseURL = 'http://localhost:10000/api'
Vue.prototype.$axios = axios
Vue.prototype.$utils = utils

Vue.config.productionTip = false

Vue.use(ElementUI)

// 全局变量,挂载到Vue实例上面
Vue.prototype.$echarts = echarts

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')

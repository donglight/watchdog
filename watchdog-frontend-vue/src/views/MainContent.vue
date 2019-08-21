<template>
  <div class="main-content-tabs">
      <el-tabs type="border-card" v-model="activeName" @tab-click="handleClick" :before-leave="beforeLeave">
        <el-tab-pane label="实例信息" name="appInfo"></el-tab-pane>
        <el-tab-pane label="系统信息" name="osInfo"></el-tab-pane>
        <el-tab-pane label="URL监控" name="url"></el-tab-pane>
        <el-tab-pane label="jvm概要" name="jvmInfo"></el-tab-pane>
        <el-tab-pane label="jvm监控" name="jvmMonitor"></el-tab-pane>
        <el-tab-pane label="线程监控" name="threadMonitor"></el-tab-pane>
      </el-tabs>
    <el-scrollbar style="height: 100%;">
      <transition name="fade" mode="out-in">
        <keep-alive include="VmMonitor">
          <router-view></router-view>
        </keep-alive>
      </transition>
    </el-scrollbar>
  </div>
</template>

<script>
export default {
  name: 'MainContent',
  props: ['appName', 'instanceId'],
  data () {
    return {
      activeName: 'appInfo'
    }
  },
  methods: {
    handleClick (tab, event) {
      this.$router.push({ path: this.$store.state.appAndInsPath + '/' + tab.name })
    },
    beforeLeave (activeName, oldActiveName) {
      this.activeName = activeName
    }
  },
  watch: {
    '$route.path': function (newVal, oldVal) {
      this.$store.commit('setCurrentAppAndInstance', { appName: this.appName, instanceId: this.instanceId })
      let path = this.$route.path
      this.activeName = path.substring(path.lastIndexOf('/') + 1)
    }
  },
  created: function () {
    this.$store.commit('setCurrentAppAndInstance', { appName: this.appName, instanceId: this.instanceId })
  },
  mounted: function () {
    this.$store.dispatch('pollingRefreshUrls')
    let path = this.$route.path
    this.activeName = path.substring(path.lastIndexOf('/') + 1)
  }
}
</script>

<style lang="scss" scoped>
  .main-content-tabs {
    height: 800px;
    width: 100%;
  }
</style>

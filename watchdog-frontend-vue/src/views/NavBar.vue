<template>
    <el-row class="tac">
      <el-scrollbar style="height: 100%">
      <el-col :span="24">
        <h2>应用列表</h2>
        <el-menu
          default-active="1"
          class="el-menu-vertical-demo"
          @select="handleSelect"
          @open="handleOpen"
          @close="handleClose">
          <!--:router="true"-->
          <!-- :index="instanceRouterPath(instance)" -->
          <transition-group name="fade" mode="out-in">
            <el-submenu v-for="app in getAllApps" :key="app.appName" :index="app.appName">
              <template slot="title">
                <i class="el-icon-menu"></i>
                <span class="app-name-span">{{ app.appName }}</span>
                <el-badge :value="Object.keys(app.instanceInfos).length" class="item app-state-badge" type="primary">
                </el-badge>
              </template>
              <el-menu-item-group>
                <template slot="title">实例列表</template>
                <el-menu-item v-for="instance in app.instanceInfos" :key="instance.instanceId" @click="route(instance)">
                  <template slot="title">
                    <i class="el-icon-place"></i>
                    <span>{{ instance.instanceId }}</span>
                    <el-badge :value="instance.status" :type="checkInstanceState(instance)" class="item app-state-badge">
                    </el-badge>
                    <el-badge :is-dot="instance.instanceId === getCurrentInstanceId" class="item app-state-badge">
                    </el-badge>
                  </template>
                </el-menu-item>
              </el-menu-item-group>
            </el-submenu>
          </transition-group>
        </el-menu>
      </el-col>
      </el-scrollbar>
    </el-row>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'

export default {
  name: 'NavBar',
  created: function () {
    this.refreshApps()
    this.pollingRefreshApps()
  },
  methods: {
    checkInstanceState (instance) {
      if (instance.status === 'UP') {
        return 'success'
      } else if (instance.status === 'DOWN') {
        this.$notify({
          title: '警告',
          message: '实例' + instance.instanceId + '正处于DOWN状态!',
          type: 'warning',
          center: true,
          duration: 5000
        })
        if (instance.instanceId === this.getCurrentInstanceId) {
          this.$router.push({ path: '/' })
        }
        return 'danger'
      }
      return 'warning'
    },
    route (instance) {
      this.$store.commit('setCurrentAppAndInstance', { appName: instance.appName, instanceId: instance.instanceId })
      if (instance.status === 'DOWN') {
        this.$message({
          message: '实例正处于DOWN状态,不能监控！',
          type: 'error',
          center: true,
          duration: 3000
        })
      } else {
        /* let oldPath = this.$route.path
        if (oldPath === '') {
          this.$router.push({ path: this.$store.state.appAndInsPath + '/appInfo' })
        } else {
          console.log(this.$store.state.appAndInsPath + oldPath)
          this.$router.push({ path: this.$store.state.appAndInsPath + oldTabPath })
        } */
        this.$router.push({ path: this.$store.state.appAndInsPath + '/appInfo' })
      }
    },
    handleOpen (key, keyPath) {
    },
    handleClose (key, keyPath) {
    },
    handleSelect (key, keyPath) {
    },
    ...mapActions([
      'refreshApps',
      'pollingRefreshApps'
    ])
  },
  watch: {
    getAppCount: function (newAppCount, oldAppCount) {
      if (newAppCount > oldAppCount) {
        this.$notify({
          title: '通知',
          message: '有应用上线',
          type: 'success',
          duration: 5000
        })
      }
    }
  },
  computed: {
    ...mapGetters([
      'getAllApps',
      'getAppCount',
      'getCurrentInstanceId'
    ])
  }
}
</script>
<style lang="scss" scoped>
  .tac {
    height: 900px;
    border-right: 2px solid #eaeefb;
  }

  .el-col {
    height: 100%;
  }

  .el-menu {
    text-align: left;
    border-top: 2px solid #eaeefb;
  }

  .app-name-span {
    font-size: 18px;
    font-weight: bold;
  }
  .el-menu-item {
    font-size: 16px;
  }
  .app-state-badge {
    position: relative;
    bottom: 12px;
  }
</style>

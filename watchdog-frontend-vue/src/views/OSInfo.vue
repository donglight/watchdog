<template>
  <el-table
    :data="osData"
    :header-cell-style="{background:'#FAFAFA',color:'#606266'}"
    border
    fit
    v-loading="loading"
    style="width: 100%;min-height: 600px">
    <el-table-column
      prop="name"
      label="属性"
      width="579">
    </el-table-column>
    <el-table-column
      prop="value"
      label="值"
      width="579">
    </el-table-column>
  </el-table>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'OSInfo',
  mounted: function () {
    this.doGetOsInfo()
    setTimeout(() => {
      this.getOsInfo()
      this.loading = false
    }, 1000)
  },
  data () {
    return {
      loading: true,
      interval: {},
      osData: [],
      cpuRatioThreshold: 90
    }
  },
  methods: {
    getOsInfo: function () {
      this.interval = setInterval(() => {
        this.doGetOsInfo()
      }, this.getMetricsRefreshTime)
    },
    doGetOsInfo: function () {
      let that = this
      this.$axios.get(this.$store.state.appAndInsPath + '/osInfo', { timeout: 1000 * 5 })
        .then(function (response) {
          let osInfo = response.data.data
          that.osData = []
          that.osData.push({
            name: '系统名称',
            value: osInfo.osName
          })
          that.osData.push({
            name: '系统版本',
            value: osInfo.osVersion
          })
          that.osData.push({
            name: '系统架构',
            value: osInfo.osArch
          })
          that.osData.push({
            name: '可用核心数',
            value: osInfo.availableProcessors
          })
          that.osData.push({
            name: 'cpu使用率',
            value: osInfo.systemCpuLoad + '%'
          })
          if (osInfo.systemCpuLoad >= that.cpuRatioThreshold) {
            that.$notify({
              title: '警告',
              message: '当前实例的CPU使用率高于' + that.cpuRatioThreshold + '%',
              type: 'warning',
              center: true,
              duration: 5000
            })
          }
          that.osData.push({
            name: '总物理内存',
            value: osInfo.osTotalPhysicalMemorySize + 'MB'
          })
          that.osData.push({
            name: '剩余物理内存',
            value: osInfo.osFreePhysicalMemorySize + 'MB'
          })
          that.osData.push({
            name: '总交换空间',
            value: osInfo.totalSwapSpaceSize + 'MB'
          })
          that.osData.push({
            name: '剩余交换空间',
            value: osInfo.freeSwapSpaceSize + 'MB'
          })
          that.osData.push({
            name: '当前进程已提交的虚拟内存',
            value: osInfo.committedVirtualMemorySize + 'MB'
          })
          that.osData.push({
            name: '用户名',
            value: osInfo.userName
          })
          that.osData.push({
            name: '用户家目录',
            value: osInfo.userHome
          })
          that.osData.push({
            name: '工作目录',
            value: osInfo.userDir
          })
          that.osData.push({
            name: '文件分隔符',
            value: osInfo.fileSeparator.replace('\\', '\\\\')
          })
          that.osData.push({
            name: '路径分隔符',
            value: osInfo.pathSeparator
          })
          let ls = osInfo.lineSeparator.replace('\n', '\\n')
          that.osData.push({
            name: '行分隔符',
            value: ls.replace('\r', '\\r')
          })
        }).catch(function () {
          clearInterval(that.interval)
          setTimeout(function () {
            that.getOsInfo()
          }, this.getMetricsRefreshTime)
        })
    }
  },
  computed: {
    ...mapGetters([
      'getMetricsRefreshTime'
    ])
  },
  beforeRouteUpdate (to, from, next) {
    clearInterval(this.interval)
    next()
  },
  beforeRouteLeave (to, from, next) {
    clearInterval(this.interval)
    next()
  },
  beforeDestroy () {
    clearInterval(this.interval)
  }
}
</script>

<style lang="scss" scoped>
</style>

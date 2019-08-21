<template>
  <keep-alive>
  <el-collapse v-loading="loading" @change="handleChange">
    <el-collapse-item :title="'JVM 概要' + '(进程pid ' + jvmInfo.pid + ')'" name="jvmOutline">
      <el-row :gutter="20">
        <el-col :span="12" class="jvm-info-text">
          <span>虚拟机名称:{{ jvmInfo.vmName }}</span>
        </el-col>
        <el-col :span="12" class="jvm-info-text">
          <span>版本:{{ jvmInfo.vmVersion }}</span>
        </el-col>
        <el-col :span="12" class="jvm-info-text">
          <span>供应商:{{ jvmInfo.vmVendor }}</span>
        </el-col>
        <el-col :span="12" class="jvm-info-text">
          <span>规范:{{ jvmInfo.vmSpecName }}</span>
        </el-col>
        <el-col :span="12" class="jvm-info-text">
          <span>规范版本:{{ jvmInfo.vmSpecVersion }}</span>
        </el-col>
        <el-col :span="12" class="jvm-info-text">
          <span>规范供应商:{{ jvmInfo.vmSpecVendor }}</span>
        </el-col>
        <el-col :span="12" class="jvm-info-text">
          <span>进程CPU使用率:{{ (jvmInfo.cpuUseRatio * 100).toFixed(2) }}%</span>
        </el-col>
        <el-col :span="12" class="jvm-info-text">
          <span>PID:{{ jvmInfo.pid }}</span>
        </el-col>
        <el-col :span="12" class="jvm-info-text">
          <span>启动时间:{{ $utils.dateFtt('yyyy-MM-dd hh:mm:ss', jvmInfo.vmStartTime) }}</span>
        </el-col>
        <el-col :span="12" class="jvm-info-text">
          <span>已正常运行时间:{{ $utils.duration( jvmInfo.vmUpTime ) }}</span>
        </el-col>
      </el-row>
    </el-collapse-item>
    <el-collapse-item title="内存和垃圾回收概述" name="garbage">
      <el-row>
        <el-col :span="12" class="jvm-info-text">
          <span>最大内存:{{ jvmInfo.maxMemory }}MB</span>
        </el-col>
        <el-col :span="12" class="jvm-info-text">
          <span>空闲大小:{{ jvmInfo.freeMemory }}MB</span>
        </el-col>
        <el-col :span="12" class="jvm-info-text">
          <span>已使用大小:{{ jvmInfo.maxMemory - jvmInfo.freeMemory }}MB</span>
        </el-col>
        <el-col :span="24" class="jvm-info-text">
          <span>垃圾收集器:</span>
        </el-col>
        <el-table
          :data="jvmInfo.garbageInfos"
          style="width: 90%">
          <el-table-column
            prop="name"
            label="名称"
            width="180">
            <template slot-scope="scope">
              <span style="margin-left: 12px">{{ scope.row.name }}</span>
            </template>
          </el-table-column>
          <el-table-column
            prop="collectionCount"
            label="收集次数"
            width="180">
            <template slot-scope="scope">
              <span style="margin-left: 12px">{{ scope.row.collectionCount + '次' }}</span>
            </template>
          </el-table-column>
          <el-table-column
            prop="collectionTime"
            width="180"
            label="用时">
            <template slot-scope="scope">
              <i class="el-icon-time"></i>
              <span style="margin-left: 12px">{{ scope.row.collectionTime/1000 + 's' }}</span>
            </template>
          </el-table-column>
          <el-table-column
            prop="memoryPoolNames"
            label="所收集的内存池">
            <template slot-scope="scope">
              <span style="margin-left: 12px">{{ scope.row.memoryPoolNames }}</span>
            </template>
          </el-table-column>
        </el-table>
      </el-row>
    </el-collapse-item>
    <el-collapse-item title="类加载信息" name="loadClass">
      <el-row :gutter="20">
        <el-col :span="12" class="jvm-info-text">
          <span>已加载类总数:{{ jvmInfo.totalLoadedClassCount }}</span>
        </el-col>
        <el-col :span="12" class="jvm-info-text">
          <span>当前已加载类:{{ jvmInfo.loadedClassCount }}</span>
        </el-col>
        <el-col :span="12" class="jvm-info-text">
          <span>已卸载类总数:{{ jvmInfo.unloadedClassCount }}</span>
        </el-col>
      </el-row>
    </el-collapse-item>

    <el-collapse-item title="java属性" name="java">
      <el-row :gutter="20">
        <el-col :span="12" class="jvm-info-text">
          <span>java版本:{{ jvmInfo.javaVersion }}</span>
        </el-col>
        <el-col :span="12" class="jvm-info-text">
          <span>java供应商:{{ jvmInfo.javaVendor }}</span>
        </el-col>
        <el-col :span="12" class="jvm-info-text">
          <span>java供应商URL:<a :href="jvmInfo.javaVendorUrl" target="_blank">{{ jvmInfo.javaVendorUrl }}</a></span>
        </el-col>
        <el-col :span="12" class="jvm-info-text">
          <span>java规范版本:{{ jvmInfo.javaSpecVersion }}</span>
        </el-col>
        <el-col :span="12" class="jvm-info-text">
          <span>java规范供应商:{{ jvmInfo.javaSpecVendor }}</span>
        </el-col>
        <el-col :span="12" class="jvm-info-text">
          <span>java home:{{ jvmInfo.javaHome }}</span>
        </el-col>
        <el-col :span="12" class="jvm-info-text">
          <span>java编译器:{{ jvmInfo.javaCompiler }}</span>
        </el-col>
        <el-col :span="12" class="jvm-info-text">
          <span>class版本:{{ jvmInfo.javaClassVersion }}</span>
        </el-col>
        <el-col :span="12" class="jvm-info-text">
          <span>临时文件路径:{{ jvmInfo.tmpdir }}</span>
        </el-col>
        <el-col :span="12" class="jvm-info-text">
          <span>扩展文件路径:{{ jvmInfo.extDirs }}</span>
        </el-col>
        <el-col :span="24" class="jvm-info-text">
          <span>VM参数:</span>
          <p class="jvm-info-big-text">{{ jvmInfo.inputArguments }}</p>
        </el-col>
        <el-col :span="24" class="jvm-info-text">
          <span>java类路径:</span>
          <p class="jvm-info-big-text">{{ jvmInfo.classPath }}</p>
        </el-col>
        <el-col :span="24" class="jvm-info-text">
          <span>库路径:</span>
          <p class="jvm-info-big-text">{{ jvmInfo.libraryPath }}</p>
        </el-col>
        <el-col :span="24" class="jvm-info-text">
          <span>引导类路径:</span>
          <p class="jvm-info-big-text">{{ jvmInfo.bootClassPath }}</p>
        </el-col>
      </el-row>
    </el-collapse-item>
  </el-collapse>
  </keep-alive>
</template>

<script>
export default {
  name: 'JvmInfo',
  data () {
    return {
      loading: true,
      activeNames: ['jvmOutline'],
      jvmInfo: {},
      interval: {}
    }
  },
  mounted: function () {
    this.doGetJvmInfo()
    setTimeout(() => {
      this.getJvmInfo()
      this.loading = false
    }, 500)
  },
  methods: {
    getJvmInfo () {
      this.interval = setInterval(() => {
        this.doGetJvmInfo()
      }, this.$store.getters.getMetricsRefreshTime)
    },
    doGetJvmInfo () {
      let that = this
      this.$axios.get(this.$store.state.appAndInsPath + '/jvmInfo', { timeout: 1000 * 5 })
        .then(function (response) {
          that.jvmInfo = response.data.data
        }).catch(function () {
          clearInterval(that.interval)
          setTimeout(function () {
            that.getJvmInfo()
          }, 5000)
        })
    },
    handleChange (val) {
    }
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
  .el-collapse {
    margin-top: 20px;
  }
  .el-row {
    padding-left: 10px;
  }
  .jvm-info-text {
    font-size: 18px;
    text-align: left;
    overflow-y: auto;
  }
  .el-table {
    background-color: #fafafa;
  }
  .jvm-info-big-text {
    font-size: 10px;
    width: 1000px;
    overflow-wrap: break-word;
  }
</style>

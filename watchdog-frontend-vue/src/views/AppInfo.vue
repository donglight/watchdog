<template>
  <el-row v-loading="loading" class="app-info-el-row">
    <el-card v-if="currentInstance" shadow="hover" class="box-card">
      <div class="text item" v-if="currentInstance.instanceId">
        <span class="text_span">实例ID:</span>
        <el-tag>{{ currentInstance.instanceId }}</el-tag>
      </div>
      <div class="text item" v-if="currentInstance.serverInfo">
        <span class="text_span">服务器信息:</span>
        <el-tag>{{ currentInstance.serverInfo }}</el-tag>
      </div>
      <div class="text item"  v-if="currentInstance.ipAddr">
        <span class="text_span">ip地址:</span>
        <el-tag>{{ currentInstance.ipAddr }}</el-tag>
      </div>
      <div class="text item" v-if="currentInstance.port">
        <span class="text_span">Web服务监听端口号:</span>
        <el-tag>{{ currentInstance.port }}</el-tag>
      </div>
      <div class="text item" v-if="currentInstance.homePageUrl">
        <span class="text_span">访问地址:</span>
        <el-tag><a :href="currentInstance.homePageUrl" target="_blank">{{ currentInstance.homePageUrl }}</a></el-tag>
      </div>
      <div class="text item" v-if="typeof(currentInstance.totalAccessCount) !== 'undefined'">
        <span class="text_span">最近实例访问次数:</span>
        <el-tag>{{ currentInstance.totalAccessCount }}</el-tag>
      </div>
      <div class="text item" v-if="currentInstance.hostName">
        <span class="text_span">主机名:</span>
        <el-tag>{{ currentInstance.hostName }}</el-tag>
      </div>
      <div class="text item" v-if="currentInstance.urlInfoList">
        <span class="text_span">URL总数:</span>
        <el-tag>{{ currentInstance.urlInfoList.length }}</el-tag>
      </div>
      <div class="text item">
        <span class="text_span">实例状态:</span>
        <el-tag>{{ currentInstance.status }}</el-tag>
      </div>
      <div class="text item" v-if="currentInstance.lastUpdateTimestamp">
        <span class="text_span">实例上次更新时间:</span>
        <el-tag>{{ $utils.dateFtt('yyyy-MM-dd hh:mm:ss', currentInstance.lastUpdateTimestamp) }}</el-tag>
      </div>
      <div class="text item" v-if="currentApp.timestamp">
        <span class="text_span">应用注册时间:</span>
        <el-tag>{{ $utils.dateFtt('yyyy-MM-dd hh:mm:ss', currentApp.timestamp) }}</el-tag>
      </div>
      <div class="text item">
        <span class="text_span">所属应用名:</span>
        <el-tag>{{ currentInstance.appName }}</el-tag>
      </div>
      <div class="text item" v-if="typeof(currentApp.appTotalAccessCount) !== 'undefined'">
        <span class="text_span">应用访问总次数:</span>
        <el-tag style="color: red">{{ currentApp.appTotalAccessCount }}</el-tag>
      </div>
    </el-card>
  </el-row>
</template>

<script>

export default {
  name: 'AppInfo',
  data () {
    return {
      loading: true
    }
  },
  mounted: function () {
    setTimeout(() => {
      this.loading = false
    }, 1000)
  },
  computed: {
    currentInstance () {
      return this.$store.state.currentInstance
    },
    currentApp () {
      return this.$store.state.currentApp
    }
  }
}
</script>

<style scoped lang="scss">
  .el-card {
    text-align: left;
    min-height: 650px;
  }
  .el-tag {
    margin: 15px;
    font-size: 20px;
    min-width: 250px;
    text-align: center;
  }
  .text_span {
    display: inline-block;
    height: 32px;
    line-height: 32px;
    min-width: 200px;
    margin-right: 30px;
    text-align: right;
    cursor: pointer;
  }
  .text {
    font-size: 20px;
  }
  .box-card div{
    float: left;
    margin-right: 20px;
  }
</style>

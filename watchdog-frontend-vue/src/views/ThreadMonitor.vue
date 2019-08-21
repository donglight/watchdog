<template>
  <el-row v-loading="loading" :gutter="100">
    <el-col :span="10">
      <div id="threadInfoChart" style="width: 450px; height: 400px;"></div>
      <div class="thread_info">
        <span class="thread_info_span" v-if="threadInfo.totalStartedThreadCount">总线程:{{ threadInfo.totalStartedThreadCount }}</span>
        <span class="thread_info_span"
              v-if="threadInfo.activeThreadCount">活动线程:{{ threadInfo.activeThreadCount }}</span>
        <span class="thread_info_span" v-if="threadInfo.peakThreadCount">峰值:{{ threadInfo.peakThreadCount }}</span>
        <span class="thread_info_span"
              v-if="threadInfo.daemonThreadCount">daemon线程:{{ threadInfo.daemonThreadCount }}</span>
      </div>
    </el-col>
    <el-col :span="14">
      <span>选择线程: </span>
      <el-cascader
        :options="options"
        :show-all-levels="false"
        @change="handleSelectChange"
      ></el-cascader>
      <el-card class="box-card">
        <div>
          名称:<span v-if="selectedThread.threadName !== undefined" class="box-card-span">{{ selectedThread.threadName }}</span>
        </div>
        <div>
          状态:<span v-if="selectedThread.threadState !== undefined" class="box-card-span">{{ selectedThread.threadState }}</span>
        </div>
        <div>
          总阻塞数:<span v-if="selectedThread.blockedCount !== undefined" class="box-card-span">{{ selectedThread.blockedCount }}</span>
        </div>
        <div>
          总阻塞时间:<span v-if="selectedThread.blockedCount !== undefined" class="box-card-span">{{ selectedThread.blockedCount }}</span>
        </div>
        <div>
          总等待数:<span v-if="selectedThread.waitedCount !== undefined" class="box-card-span">{{ selectedThread.waitedCount }}</span>
        </div>
        <div>
          总等待时间:<span v-if="selectedThread.waitedTime !== undefined" class="box-card-span">{{ selectedThread.waitedTime + ' ms' }}</span>
        </div>
        <div>
          线程堆栈信息:<p v-html="selectedThread.stackTrace" class="stack-trace"></p>
        </div>
      </el-card>
    </el-col>
  </el-row>
</template>

<script>
export default {
  name: 'ThreadMonitor',
  data () {
    return {
      loading: true,
      now: +new Date(),
      maxDataLength: 500,
      activeThread: [],
      totalThread: [],
      threadInfo: {},
      selectedThread: {},
      options: [{
        value: 'activeThread',
        label: '活动线程',
        children: []
      }, {
        value: 'deadlockedThread',
        label: '死锁线程',
        children: []
      }]
    }
  },
  mounted: function () {
    this.doGetThreadInfo()
    setTimeout(() => {
      this.getThreadInfo()
      this.drawThreadChart()
      this.loading = false
    }, 1000)
  },
  methods: {
    handleSelectChange (selected) {
      for (let thread of this.threadInfo.wdThreadInfos) {
        if (thread.threadId === selected[1]) {
          this.selectedThread = thread
        }
      }
    },
    setSelectOptions () {
      this.options[0].children = []
      for (let wdThreadInfo of this.threadInfo.wdThreadInfos) {
        this.options[0].children.push({
          value: wdThreadInfo.threadId,
          label: wdThreadInfo.threadName
        })
      }
    },
    drawThreadChart () {
      let threadInfoChart = this.$echarts.init(document.getElementById('threadInfoChart'))
      this.now = this.now - 5 * this.$store.getters.getMetricsRefreshTime
      for (let i = 0; i < 5; i++) {
        this.now = this.now + this.$store.getters.getMetricsRefreshTime
        this.activeThread.push(this.getThreadChartData(this.threadInfo.activeThreadCount))
        this.totalThread.push(this.getThreadChartData(this.threadInfo.totalStartedThreadCount))
      }
      let option = {
        title: {
          text: '线程监控'
        },
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            animation: false
          }
        },
        legend: {
          left: 'right',
          data: [
            '活动线程', '总共线程'
          ]
        },
        xAxis: {
          type: 'time',
          name: '时刻',
          splitLine: {
            show: false
          }
        },
        yAxis: {
          type: 'value',
          name: '线程个数',
          boundaryGap: [0, '100%'],
          splitLine: {
            show: false
          }
        },
        series: [
          {
            name: '活动线程',
            type: 'line',
            showSymbol: false,
            hoverAnimation: false,
            data: this.activeThread
          },
          {
            name: '总共线程',
            type: 'line',
            showSymbol: false,
            hoverAnimation: false,
            data: this.totalThread
          }
        ]
      }
      this.datatInterval = setInterval(() => {
        this.now = this.now + this.$store.getters.getMetricsRefreshTime
        if (this.activeThread.length <= this.maxDataLength) {
          this.activeThread.push(this.getThreadChartData(this.threadInfo.activeThreadCount))
          this.totalThread.push(this.getThreadChartData(this.threadInfo.totalStartedThreadCount))
        } else {
          this.activeThread.shift()
          this.totalThread.shift()
          this.activeThread.push(this.getThreadChartData(this.threadInfo.activeThreadCount))
          this.totalThread.push(this.getThreadChartData(this.threadInfo.totalStartedThreadCount))
        }
        threadInfoChart.setOption({
          series: [{
            data: this.activeThread
          }, {
            data: this.totalThread
          }]
        })
      }, this.$store.getters.getMetricsRefreshTime)
      threadInfoChart.setOption(option, true)
    },
    getThreadInfo () {
      this.interval = setInterval(() => {
        this.doGetThreadInfo()
      }, this.$store.getters.getMetricsRefreshTime)
    },
    doGetThreadInfo () {
      let that = this
      this.$axios.get(this.$store.state.appAndInsPath + '/threadInfo', { timeout: 1000 * 5 })
        .then(function (response) {
          that.threadInfo = response.data.data
          that.setSelectOptions()
        }).catch(function () {
          clearInterval(that.interval)
          setTimeout(function () {
            that.getThreadInfo()
          }, 5000)
        })
    },
    getThreadChartData (activeThreadCount) {
      return {
        name: this.now,
        value: [
          this.now,
          activeThreadCount
        ]
      }
    }
  },
  beforeRouteUpdate (to, from, next) {
    clearInterval(this.interval)
    clearInterval(this.datatInterval)
    next()
  },
  beforeRouteLeave (to, from, next) {
    clearInterval(this.interval)
    clearInterval(this.datatInterval)
    next()
  },
  beforeDestroy () {
    clearInterval(this.interval)
    clearInterval(this.datatInterval)
  }
}
</script>

<style scoped>

  .thread_info {
    text-align: left;
  }

  .thread_info_span {
    display: inline-block;
    margin-left: 15px;
    font-size: 18px;
  }

  .el-row {
    margin-top: 50px;
    padding: 0;
  }
  .el-col {
    text-align: left;
    padding: 0;
  }
  .text {
    font-size: 14px;
  }
  .item {
    padding: 18px 0;
  }
  .box-card {
    width: 600px;
    margin-top: 50px;
  }
  .box-card-span {
    display: inline-block;
    font-size: 18px;
  }
  .box-card div {
    margin-bottom: 20px;
  }
  .stack-trace {
    overflow-wrap: break-word;
  }
</style>

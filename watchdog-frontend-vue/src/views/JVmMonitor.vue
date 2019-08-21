<template>
  <el-collapse v-loading="loading" @change="handleChange">
    <el-collapse-item title="概览" name="quick_facts">
      <div class="quick_facts">
        <p>启动时间</p>
        <p class="quick_facts_value_1" v-if="jvmInfo.vmStartTime">{{ $utils.dateFtt('yyyy-MM-dd hh:mm:ss', jvmInfo.vmStartTime) }}</p>
      </div>
      <div class="quick_facts">
        <p>正常运行时间</p>
        <p class="quick_facts_value_1" v-if="jvmInfo.vmUpTime">{{ $utils.duration( jvmInfo.vmUpTime ) }}</p>
      </div>
      <div class="quick_facts">
        <p>进程CPU使用率</p>
        <p class="quick_facts_value_1" v-if="jvmInfo.vmUpTime">{{ (jvmInfo.cpuUseRatio * 100).toFixed(2)}}%</p>
      </div>
      <div class="quick_facts">
        <p>堆使用率</p>
        <p class="quick_facts_value_2" v-if="jvmInfo.heapUsedRatio">{{ jvmInfo.heapUsedRatio }}%</p>
      </div>
      <div class="quick_facts">
        <p>非堆使用率</p>
        <p class="quick_facts_value_2" v-if="jvmInfo.nonHeapUsedRatio">{{ jvmInfo.nonHeapUsedRatio }}%</p>
      </div>
      <div class="memory_charts_content">
        <div id="heapChart" style="width: 450px;height: 380px">
        </div>
        <div class="memory_charts_description">
          <div class="memory_charts_description_div">
            <span>已使用:{{ jvmInfo.heapUsed }}MB</span>
          </div>
          <div class="memory_charts_description_div">
            <span>已提交:{{ jvmInfo.heapCommitted }}MB</span>
          </div>
          <div class="memory_charts_description_div">
            <span>最大值:{{ jvmInfo.heapMax === -1 ? '未定义' : jvmInfo.heapMax + 'MB' }}</span>
          </div>
        </div>
      </div>
      <div class="memory_charts_content">
        <div id="nonHeapChart" style="width: 450px;height: 380px">
        </div>
        <div class="memory_charts_description">
          <div class="memory_charts_description_div">
            <span>已使用:{{ jvmInfo.nonHeapUsed }}MB</span>
          </div>
          <div class="memory_charts_description_div">
            <span>已提交:{{ jvmInfo.nonHeapCommitted }}MB</span>
          </div>
          <div class="memory_charts_description_div">
            <span>最大值:{{ jvmInfo.nonHeapMax === -1 ? '未定义' : jvmInfo.nonHeapMax + 'MB' }}</span>
          </div>
        </div>
      </div>
      <div class="clear"></div>
    </el-collapse-item>
    <el-collapse-item title="jvm 内存池" name="jvm_memory_pool">
      <div class="jvm_memory_pool_wrap">
        <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange" border>全选</el-checkbox>
        <div style="margin: 15px 0;"></div>
        <el-checkbox-group v-model="checkedPools" @change="handleCheckedPoolGroupChange">
          <el-checkbox
            v-for="(pool, index) in jvmInfo.memoryPoolInfos"
            v-model="pool.name"
            :label="pool.name"
            :key="pool.name" border @change="checked=>handleCheckedPoolChange(checked, index)">{{pool.name}}</el-checkbox>
        </el-checkbox-group>
        <transition-group name="el-zoom-in-center">
          <div v-for="(pool, index) in jvmInfo.memoryPoolInfos"
               v-show="checkStateArray[index]"
               :key="pool.name"
               :id="pool.name.replace(/\s/g, '') + '-content'"
               class="memory_charts_content">
            <div :id="pool.name.replace(/\s/g, '') + '-chart'" style="width: 450px;height: 380px">
            </div>
            <div class="memory_pool_charts_description">
              <div class="memory_pool_charts_description_div">
                <span>初始值:{{ pool.init === -1 ? '未定义' : pool.init + 'MB' }}</span>
              </div>
              <div class="memory_pool_charts_description_div">
                <span>已使用:{{ pool.used }}MB</span>
              </div>
              <div class="memory_pool_charts_description_div">
                <span>已提交:{{ pool.committed }}MB</span>
              </div>
              <div class="memory_pool_charts_description_div">
                <span>最大值:{{ pool.max === -1 ? '未定义' : pool.max + 'MB' }}</span>
              </div>
              <div class="memory_pool_charts_description_div">
                <span>使用率:{{ pool.usedRatio }}%</span>
              </div>
            </div>
          </div>
        </transition-group>
        <div class="clear"></div>
      </div>
    </el-collapse-item>
  </el-collapse>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  name: 'VmMonitor',
  data () {
    return {
      activeNames: ['quick_facts'],
      loading: true,
      show: false,
      jvmInfo: {},
      now: +new Date(),
      maxDataLength: 500,
      apiInterval: {},
      dataInterval: {},
      heapChart: null,
      nonHeapChart: null,
      heapUsedData: [],
      heapCommittedData: [],
      nonHeapUsedData: [],
      nonHeapCommittedData: [],
      memoryPoolsCharts: [],
      memoryUsedData: [],
      memoryCommittedData: [],
      cpuRatioData: [],
      checkAll: false,
      checkedPools: [],
      isIndeterminate: true,
      checkStateArray: []
    }
  },
  mounted: function () {
    setTimeout(() => {
      this.loading = false
    }, 1000)
  },
  watch: {
    getCurrentInstanceId: function (newVal, oldVal) {
      this.loading = true
      this.heapUsedData = []
      this.heapCommittedData = []
      this.nonHeapUsedData = []
      this.nonHeapCommittedData = []
      for (let i = 0; i < this.memoryUsedData.length; i++) {
        this.memoryUsedData[i] = []
      }
      for (let i = 0; i < this.memoryCommittedData.length; i++) {
        this.memoryCommittedData[i] = []
      }
      setTimeout(() => {
        this.loading = false
      }, 2000)
      this.setChartsData()
    }
  },
  methods: {
    handleCheckAllChange (val) {
      this.checkedPools = val ? this.jvmInfo.memoryPoolInfos.map(pool => { return pool.name }) : []
      this.show = val
      if (val) {
        /* this.checkedPools.forEach((poolName) => {
          document.getElementById(poolName.replace(/\s/g, '') + '-content').style.display = 'block'
        }) */
        for (let i = 0; i < this.checkStateArray.length; i++) {
          this.checkStateArray[i] = true
        }
      } else {
        /* for (let pool of this.jvmInfo.memoryPoolInfos) {
          document.getElementById(pool.name.replace(/\s/g, '') + '-content').style.display = 'none'
        } */
        for (let i = 0; i < this.checkStateArray.length; i++) {
          this.checkStateArray[i] = false
        }
      }
      this.isIndeterminate = false
    },
    handleCheckedPoolGroupChange (value) {
      let checkedCount = value.length
      this.checkAll = checkedCount === this.jvmInfo.memoryPoolInfos.length
      this.isIndeterminate = checkedCount > 0 && checkedCount < this.jvmInfo.memoryPoolInfos.length
    },
    handleCheckedPoolChange (checked, index) {
      if (checked) {
        // document.getElementById(poolName.replace(/\s/g, '') + '-content').style.display = 'block'
        this.checkStateArray[index] = true
      } else {
        // document.getElementById(poolName.replace(/\s/g, '') + '-content').style.display = 'none'
        this.checkStateArray[index] = false
      }
    },
    setChartsData () {
      this.heapChart.setOption({
        series: [{
          data: this.heapUsedData
        }, {
          data: this.heapCommittedData
        }]
      })
      this.nonHeapChart.setOption({
        series: [{
          data: this.nonHeapUsedData
        }, {
          data: this.nonHeapCommittedData
        }]
      })
      for (let i = 0; i < this.jvmInfo.memoryPoolInfos.length; i++) {
        this.memoryPoolsCharts[i].setOption({
          series: [{
            data: this.memoryUsedData[i]
          }, {
            data: this.memoryCommittedData[i]
          }]
        })
      }
    },
    getOption (name, usedData, committedData) {
      return {
        title: {
          left: 'left',
          text: name,
          textStyle: {
            color: '#409eff',
            fontSize: 20
          }
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
            'used', 'committed'
          ]
        },
        xAxis: {
          type: 'time',
          name: '时间',
          splitLine: {
            lineStyle: {
              color: '#ddd'
            }
          }
        },
        yAxis: {
          type: 'value',
          name: '内存/MB',
          boundaryGap: [0, '100%'],
          splitLine: {
            lineStyle: {
              color: '#ddd'
            }
          }
        },
        series: [
          {
            name: 'used',
            type: 'line',
            hoverAnimation: false,
            showSymbol: false,
            symbol: 'circle',
            symbolSize: 5,
            smooth: true,
            data: usedData,
            markPoint: {
              symbolSize: 30,
              data: [
                { type: 'max', name: '最大值' },
                { type: 'min', name: '最小值' }
              ]
            },
            markLine: {
              data: [
                { type: 'average', name: '平均值' }
              ]
            },
            itemStyle: {
              normal: {
                color: 'darkorange'
              }
            },
            areaStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [{
                  offset: 0, color: 'darkorange' // 0% 处的颜色
                }, {
                  offset: 1, color: '#ffe' // 100% 处的颜色
                }],
                global: false // 缺省为 false
              }
            }
          },
          {
            name: 'committed',
            type: 'line',
            showSymbol: false,
            symbolSize: 5,
            symbol: 'circle',
            hoverAnimation: false,
            smooth: true,
            data: committedData,
            itemStyle: {
              normal: {
                color: 'palegreen'
              }
            },
            areaStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [{
                  offset: 0, color: 'palegreen' // 0% 处的颜色
                }, {
                  offset: 1, color: '#ffe' // 100% 处的颜色
                }],
                global: false // 缺省为 false
              }
            }
          }
        ]
      }
    },
    drawMemoryLine () {
      if (this.heapChart == null) {
        this.now = this.now + this.getMetricsRefreshTime
        this.heapUsedData.push(this.usedData(this.jvmInfo.heapUsed))
        this.heapCommittedData.push(this.committedData(this.jvmInfo.heapCommitted))
        this.nonHeapUsedData.push(this.usedData(this.jvmInfo.nonHeapUsed))
        this.nonHeapCommittedData.push(this.committedData(this.jvmInfo.nonHeapCommitted))
        this.heapChart = this.$echarts.init(document.getElementById('heapChart'))
        this.nonHeapChart = this.$echarts.init(document.getElementById('nonHeapChart'))
        let heapOption = this.getOption('heap', this.heapUsedData, this.heapCommittedData)
        let nonHeapOption = this.getOption('nonHeap', this.nonHeapUsedData, this.nonHeapCommittedData)
        this.heapChart.setOption(heapOption, true)
        this.nonHeapChart.setOption(nonHeapOption, true)
        this.$nextTick(() => {
          for (let pool of this.jvmInfo.memoryPoolInfos) {
            let poolChart = this.$echarts.init(document.getElementById(pool.name.replace(/\s/g, '') + '-chart'))
            let used = []
            let committed = []
            used.push(this.usedData(pool.used))
            committed.push(this.committedData(pool.committed))
            this.memoryUsedData.push(used)
            this.memoryCommittedData.push(committed)
            poolChart.setOption(this.getOption(pool.name, used, committed), true)
            this.memoryPoolsCharts.push(poolChart)
          }
        })
      }
      this.dataInterval = setInterval(() => {
        this.now = this.now + this.getMetricsRefreshTime
        if (this.heapUsedData.length <= this.maxDataLength) {
          this.heapUsedData.push(this.usedData(this.jvmInfo.heapUsed))
          this.heapCommittedData.push(this.committedData(this.jvmInfo.heapCommitted))
          this.nonHeapUsedData.push(this.usedData(this.jvmInfo.nonHeapUsed))
          this.nonHeapCommittedData.push(this.committedData(this.jvmInfo.nonHeapCommitted))
          let count = 0
          for (let pool of this.jvmInfo.memoryPoolInfos) {
            this.memoryUsedData[count].push((this.usedData(pool.used)))
            this.memoryCommittedData[count].push((this.committedData(pool.committed)))
            count++
          }
        } else {
          this.heapUsedData.shift()
          this.heapCommittedData.shift()
          this.nonHeapUsedData.shift()
          this.nonHeapCommittedData.shift()
          this.heapUsedData.push(this.usedData(this.jvmInfo.heapUsed))
          this.heapCommittedData.push(this.committedData(this.jvmInfo.heapCommitted))
          this.nonHeapUsedData.push(this.usedData(this.jvmInfo.nonHeapUsed))
          this.nonHeapCommittedData.push(this.committedData(this.jvmInfo.nonHeapCommitted))
          let count = 0
          for (let pool of this.jvmInfo.memoryPoolInfos) {
            this.memoryUsedData[count].shift()
            this.memoryCommittedData[count].shift()
            this.memoryUsedData[count].push((this.usedData(pool.used)))
            this.memoryCommittedData[count].push((this.usedData(pool.committed)))
            count++
          }
        }
        this.setChartsData()
      }, this.getMetricsRefreshTime)
    },
    getJvmInfo () {
      this.apiInterval = setInterval(() => {
        this.doGetJvmInfo()
      }, this.getMetricsRefreshTime)
    },
    doGetJvmInfo () {
      let that = this
      this.$axios.get(this.$store.state.appAndInsPath + '/jvmInfo', { timeout: 1000 * 5 })
        .then(function (response) {
          that.jvmInfo = response.data.data
          that.checkStateArray.length = that.jvmInfo.memoryPoolInfos.length
        }).catch(function () {
          clearInterval(that.apiInterval)
          setTimeout(function () {
            that.getJvmInfo()
          }, 5000)
        })
    },
    usedData (used) {
      return {
        name: this.now,
        value: [
          this.now,
          used
        ]
      }
    },
    committedData (committed) {
      return {
        name: this.now,
        value: [
          this.now,
          committed
        ]
      }
    },
    handleChange (val) {
    }
  },
  computed: {
    ...mapGetters([
      'getMetricsRefreshTime',
      'getCurrentInstanceId'
    ])
  },
  beforeRouteEnter (to, from, next) {
    next(vm => {
      vm.$axios.get(vm.$store.state.appAndInsPath + '/jvmInfo', { timeout: 1000 * 5 })
        .then(function (response) {
          vm.now = +new Date()
          vm.jvmInfo = response.data.data
          vm.getJvmInfo()
          vm.drawMemoryLine()
        }).catch(function () {
          clearInterval(vm.apiInterval)
          clearInterval(vm.dataInterval)
          setTimeout(function () {
            vm.getJvmInfo()
          }, 5000)
        })
    })
  },
  beforeRouteLeave (to, from, next) {
    clearInterval(this.apiInterval)
    clearInterval(this.dataInterval)
    next()
  },
  beforeDestroy () {
    clearInterval(this.apiInterval)
    clearInterval(this.dataInterval)
  }
}
</script>

<style lang="scss" scoped>
  .jvm_memory_pool_wrap {
    min-height: 500px;
  }
  .quick_facts {
    float: left;
    font-size: 20px;
    line-height: 20px;
    margin-left: 20px;
    width: 200px;
    height: 120px;
    background-color: #fafafa;
    border-radius: 10px;
  }
  .clear {
    clear: both;
  }
  .quick_facts_value_1{
    font-size: 25px;
    line-height: 25px;
    color: darkorange;
  }
  .quick_facts_value_2{
    font-size: 25px;
    line-height: 25px;
    color: #409eff;
  }
  .memory_charts_content {
    float: left;
    width: 500px;
    height: 430px;
    margin: 20px;
    // display: none;
  }
  .memory_charts_description_div {
    float: left;
    width: 130px;
    font-size: 16px;
    margin-left: 10px;
    color: #409eff;
    background-color: #fafafa;
    border-radius: 5px;
  }
  .memory_pool_charts_description_div {
    float: left;
    width: 130px;
    font-size: 15px;
    margin-left: 10px;
    background-color: #fafafa;
    border-radius: 5px;
  }
</style>

<template>
  <div id=url-monitor>
    <div id="urlChart" style="width: 1200px; height: 600px;">
    </div>
    <el-button
      size="mini"
      @click="goBack()">
      返回url列表
    </el-button>
  </div>
</template>

<script>
export default {
  name: 'UrlMonitor',
  props: ['urlId'],
  data () {
    return {
      chartData: [],
      now: +new Date() - 1000 * 100,
      oneSecond: 1000
    }
  },
  mounted: function () {
    this.drawLine()
  },
  created () {
    /* let that = this
    this.$axios.get(this.$store.state.appAndInsPath + '/recent_url_state/' + this.urlId, { timeout: 1000 * 15 })
      .then(function (response) {
        if (response.data.code === 200) {
          let urlStates = response.data.data
          for (let i = 0; i < 100; i++) {
            if (i < urlStates.length) {
              that.now = new Date(+that.now + 1000)
              that.chartData.push({
                name: urlStates[i],
                value: [
                  that.now,
                  urlStates.length
                ]
              })
            } else {
              that.chartData.push(that.getEmptyData())
            }
          }
        }
      }) */
    for (let i = 0; i < 100; i++) {
      this.chartData.push(this.getEmptyData())
    }
  },
  methods: {
    getEmptyData () {
      this.now = new Date(+this.now + 1000)
      return {
        name: {
          currentConcurrency: 0,
          start: 0
        },
        value: [
          this.now,
          0
        ]
      }
    },
    drawLine () {
      let chart = this.$echarts.init(document.getElementById('urlChart'))
      let option = {
        title: {
          left: 'center',
          text: '监控URL: ' + (typeof (this.$route.params.url) === 'undefined' ? '' : this.$route.params.url)
        },
        toolbox: { // 可视化的工具箱
          show: true,
          feature: {
            dataView: { // 数据视图
              show: true
            },
            restore: { // 重置
              show: true
            },
            dataZoom: { // 数据缩放视图
              show: true
            },
            saveAsImage: {// 保存图片
              show: true
            },
            magicType: {// 动态类型切换
              type: ['bar', 'line']
            }
          }
        },
        tooltip: {
          trigger: 'axis',
          formatter: function (params) {
            params = params[0]
            let currentRequestTimes = params.data.value[1]
            let urlState = params.name
            let date = new Date(urlState.start)
            if (urlState.start === 0) {
              return '最大并发量:' + urlState.currentConcurrency + '<br> 访问量:' + currentRequestTimes + '<br>开始时间:' + '0:0:0'
            } else {
              return '最大并发量:' + urlState.currentConcurrency + '<br> 访问量:' + currentRequestTimes + '<br>开始时间:' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds()
            }
          },
          axisPointer: {
            animation: false
          }
        },
        legend: {
          data: ['url访问量', '最大并发量']
        },
        xAxis: {
          type: 'time',
          name: '访问时刻',
          // splitNumber: 10,
          splitLine: {
            lineStyle: {
              color: '#ddd'
            }
          }
        },
        yAxis: {
          type: 'value',
          name: '访问量/次',
          boundaryGap: [0, '100%'],
          splitLine: {
            lineStyle: {
              color: '#ddd'
            }
          }
        },
        dataZoom: [{
          type: 'inside',
          start: 0,
          end: 100,
          filterMode: 'empty'
        }, {
          start: 0,
          end: 100,
          handleIcon: 'M10.7,11.9v-1.3H9.3v1.3c-4.9,0.3-8.8,4.4-8.8,9.4c0,5,3.9,9.1,8.8,9.4v1.3h1.3v-1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,11.9z M13.3,24.4H6.7V23h6.6V24.4z M13.3,19.6H6.7v-1.4h6.6V19.6z',
          handleSize: '80%',
          handleStyle: {
            color: '#fff',
            shadowBlur: 3,
            shadowColor: 'rgba(0, 0, 0, 0.6)',
            shadowOffsetX: 2,
            shadowOffsetY: 2
          }
        }],
        series: [{
          name: 'url访问次数',
          smooth: true,
          type: 'line',
          showSymbol: false,
          hoverAnimation: false,
          data: this.chartData
        }]
      }
      this.interval = setInterval(() => {
        this.chartData.shift()
        this.getUrlStates(chart)
        chart.setOption({
          series: [{
            data: this.chartData
          }]
        })
      }, 1000)
      chart.setOption(option, true)
    },
    getUrlStates (chart) {
      let that = this
      return this.$axios.get('/latest_url_state/' + this.urlId, { timeout: 1000 * 15 })
        .then(function (response) {
          // 返回值最近一秒的的url state列表
          let urlStates = response.data.data
          if (urlStates === '' || urlStates === null || urlStates.length === 0) {
            that.chartData.push(that.getEmptyData())
          } else {
            that.now = new Date(+that.now + 1000)
            for (let urlState of urlStates) {
              that.chartData.push({
                name: urlState,
                value: [
                  that.now,
                  urlStates.length
                ]
              })
            }
          }
          chart.setOption({
            series: [{
              data: that.chartData
            }]
          })
        })
    },
    goBack () {
      clearInterval(this.interval)
      this.$router.push({ path: this.$store.state.appAndInsPath + '/url' })
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
    if (!this.urlChart) {
      return
    }
    this.urlChart.dispose()
    this.urlChart = null
  }
}
</script>

<style lang="scss" scoped>
  #url-monitor {
    margin-top: 20px;
  }
</style>

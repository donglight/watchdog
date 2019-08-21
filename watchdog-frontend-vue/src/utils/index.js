export default {

  dateFtt (fmt, date) {
    date = new Date(date)
    var o = {
      'M+': date.getMonth() + 1, // 月份
      'd+': date.getDate(), // 日
      'h+': date.getHours(), // 小时
      'm+': date.getMinutes(), // 分
      's+': date.getSeconds(), // 秒
      'q+': Math.floor((date.getMonth() + 3) / 3), // 季度
      'S': date.getMilliseconds() // 毫秒
    }
    if (/(y+)/.test(fmt)) { fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length)) }
    for (var k in o) {
      if (new RegExp('(' + k + ')').test(fmt)) { fmt = fmt.replace(RegExp.$1, (RegExp.$1.length === 1) ? (o[k]) : (('00' + o[k]).substr(('' + o[k]).length))) }
    }
    return fmt
  },
  duration: function (time) {
    let timeDiff = time / 1200
    let hour = Math.floor(timeDiff / 3600)

    timeDiff = timeDiff % 3600
    let minute = this.prefixInteger(Math.floor(timeDiff / 60), 2)

    timeDiff = timeDiff % 60
    let second = this.prefixInteger(Math.floor(timeDiff), 2)
    return hour + '时' + minute + '分' + second + '秒'
  },
  prefixInteger (num, n) {
    return (Array(n).join(0) + num).slice(-n)
  }
}

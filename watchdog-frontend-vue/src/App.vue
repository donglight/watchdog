<template>
  <div id="app">
    <el-scrollbar style="height: 100%">
      <router-view></router-view>
    </el-scrollbar>
  </div>
</template>

<script>
export default {
  name: 'app'
}
</script>

<style lang="scss" scoped>
  #app {
    height: 800px;
  }
</style>

<template>
  <el-container id="container">
    <el-header id="header">
      <top-bar></top-bar>
    </el-header>
    <el-container id="main-container">
      <el-aside id="main-container-aside">
        <nav-bar></nav-bar>
      </el-aside>
      <el-main id="main-container-right">
        <transition name="fade" mode="out-in">
          <router-view></router-view>
        </transition>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
// @ is an alias to /src
import TopBar from './Header'
import NavBar from './NavBar'

export default {
  name: 'home',
  components: { NavBar, TopBar }
}
</script>

<style lang="scss">
  body{
    margin:0;
  }
  #app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    text-align: center;
    color: #2c3e50;
    margin: 0 auto;
    padding-top:61px;
  }
  #header {
    padding: 0;
    position: fixed;
    width: 100%;
    top: 0;
    z-index: 100;
    display: block;
  }
  #main-container {
    height: 1000px;
  }
  #main-container-right {
    position: relative;
    max-width: 1200px;
    height: 100%;
    margin-left: 300px;
    padding-top: 38px;
  }
  #main-container-aside {
    position: fixed;
    top: 61px;
    height: 100%;
  }
  #nav {
    padding: 30px;
  a {
    font-weight: bold;
    color: #2c3e50;
  &.router-link-exact-active {
     color: #42b983;
   }
  }
  }
  h1 {
    text-align: center;
  }
  .fade-enter-active, .fade-leave-active {
    transition: opacity .3s;
  }
  .fade-enter, .fade-leave-to /* .fade-leave-active below version 2.1.8 */ {
    opacity: 0;
  }
</style>

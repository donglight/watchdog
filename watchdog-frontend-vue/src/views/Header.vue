<template>
    <el-row>
      <el-col :span="24">
        <div class="header-content">
          <span id="header-title">Watch Dog</span>
          <div class="header-tip">
            <a class="header-tip-a" href="https://github.com/donglight" target="_blank">帮助</a>
            <a class="header-tip-a" href="https://github.com/donglight" target="_blank">github</a>
          </div>
        </div>
      </el-col>
    </el-row>
</template>

<script>
export default {
  name: 'Header'
}
</script>

<style scoped lang="scss">
  .el-row {
    margin-bottom: 20px;
  &:last-child {
     margin-bottom: 0;
   }
  }
  .header-content {
    border-radius: 2px;
    min-height: 60px;
    background-color: #5fdbff;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    text-align: left;
    padding-left: 80px;
  }
  #header-title {
    font-size: 30px;
    color: #fff;
    font-weight: 600;
    white-space: nowrap;
    line-height: 60px;
    transition: .2s;
  }
  .header-tip {
    padding: 20px 150px;
    float: right;
  }
  .header-tip-a {
    margin-left: 20px;
    color: #fff;
    font-weight: 600;
    white-space: nowrap;
    text-decoration: none;
    font-size: 18px;
    line-height: 18px;
    background-color: transparent;
  }
  .header-tip-a:hover {
    color: #24292e;
    text-decoration: underline;
  }
</style>

<template>
  <div id="url-list">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-input
          placeholder="请输入URL"
          v-model="url"
          clearable>
        </el-input>
      </el-col>
    </el-row>
    <el-row>
      <el-table
        ref="table"
        :header-cell-style="{background:'#FAFAFA',color:'#606266'}"
        :data="pagingUrlList(currentPage,pageSize,url)"
        :default-sort="{prop: 'url', order: 'ascending'}"
        @sort-change="sortChange"
        v-loading="loading"
        fit
        style="width: 100%; min-height: 500px">
        <el-table-column
          prop="url"
          sortable
          label="URL"
          width="250">
          <template slot-scope="scope">
            <el-popover trigger="hover" placement="top">
              <p>请求Method: {{ scope.row.httpMethod}}<br>url: {{ scope.row.url}} </p>
              <div slot="reference" class="name-wrapper">
                <el-tag>{{ scope.row.url }}</el-tag>
              </div>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column
          label="请求方法"
          prop="requestMethod"
          sortable
          width="100">
          <template slot-scope="scope">
            <span style="margin-left: 10px">{{ scope.row.httpMethod}}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="请求次数"
          prop="requestTimes"
          sortable
          width="100">
          <template slot-scope="scope">
            <span style="margin-left: 10px">{{ scope.row.requestTimes }}</span>
          </template>
        </el-table-column>
        <el-table-column
          label="请求总时间"
          prop="totalTime"
          sortable
          width="120">
          <template slot-scope="scope">
            <i class="el-icon-time"></i>
            <span style="margin-left: 10px">{{ scope.row.totalTime }}ms</span>
          </template>
        </el-table-column>
        <el-table-column
          label="最长耗时"
          prop="maxDuration"
          sortable
          width="120">
          <template slot-scope="scope">
            <i class="el-icon-time"></i>
            <span style="margin-left: 10px">{{ scope.row.maxDuration }}ms</span>
          </template>
        </el-table-column>
        <el-table-column
          label="最大并发"
          prop="maxConcurrency"
          sortable
          width="120">
          <template slot-scope="scope">
            <span style="margin-left: 10px">{{ scope.row.maxConcurrency }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="failTimes"
          sortable
          label="失败次数"
          width="100">
          <template slot-scope="scope">
            <span style="margin-left: 10px">{{ scope.row.failTimes }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="successRate"
          sortable
          label="成功率"
          width="100">
          <template slot-scope="scope">
            <span style="margin-left: 10px">{{ scope.row.successRate*100 }}%</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right">
          <template slot-scope="scope">
            <el-button
              size="mini"
              @click="handleEdit(scope.$index, scope.row)">实时监控
            </el-button>
            <!--<el-button
              size="mini"
              @click="handleDelete(scope.$index, scope.row)">查看
            </el-button>-->
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="1"
        :page-sizes="[5,10,15,20]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="getTotal()">
      </el-pagination>
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'UrlList',
  data () {
    return {
      loading: true,
      url: '',
      currentPage: 1,
      pageSize: 10
    }
  },
  mounted: function () {
    setTimeout(() => {
      this.loading = false
    }, 500)
  },
  methods: {
    sortChange (sortObject) {
      let sortBy
      if (sortObject.order === 'ascending' || sortObject.prop === undefined) {
        sortBy = function (a, b) {
          let v1 = a[sortObject.prop]
          let v2 = b[sortObject.prop]
          return v1 - v2
        }
      } else {
        sortBy = function (a, b) {
          let v1 = a[sortObject.prop]
          let v2 = b[sortObject.prop]
          return v2 - v1
        }
      }
      this.$store.commit('sortUrlList', sortBy)
    },
    handleEdit (index, row) {
      this.$router.push({ name: 'urlState', params: { urlId: row.id, url: row.url } })
    },
    handleDelete (index, row) {
    },
    handleSizeChange (pageSize) {
      this.pageSize = pageSize
    },
    handleCurrentChange (currentPage) {
      this.currentPage = currentPage
    }
  },
  computed: {
    ...mapGetters([
      'getTotal',
      'pagingUrlList'
    ])
  }
}
</script>

<style lang="scss" scoped>
  .el-pagination {
    margin-top: 20px;
    text-align: left;
  }

  .el-row {
    margin: 20px 0 20px 0;
  }
</style>

<template>
  <div class="redisList">
    <!-- 查询条件 -->
    <div
      class="search-box-one"
      style="width: 78%; overflow: visible; height: 40px"
    >
      <div style="width: 100%; float: left; margin-top: 20px">
        <div style="width: 100%; float: left">
          <div class="search-box-top-name" style="width: 75px">缓存KEY：</div>
          <div
            style="
              width: calc(100% - 100px - 2.3%);
              float: right;
              padding-right: 2.3%;
            "
          >
            <el-input
              v-model="key"
              placeholder="请输入缓存KEY"
              clearable
              style="width: 100%"
            ></el-input>
          </div>
        </div>
      </div>
    </div>
    <div class="search-box-right" style="overflow: visible">
      <div
        class="query"
        @click="empty"
        style="margin-right: 20px; margin-top: -20px"
      >
        <font-awesome-icon icon="fas fa-refresh" />重置
      </div>
      <div
        class="query"
        @click="search"
        style="margin-right: 10px; margin-top: -20px"
      >
        <font-awesome-icon icon="fas fa-search" />查询
      </div>
    </div>
    <div class="clear"></div>
    <!-- 操作按钮 -->
    <div class="btn-box">
      <div
        class="start"
        style="width: 60px; background: #f56c6c"
        @click="deleteRedisList"
      >
        <font-awesome-icon icon="far fa-trash-alt" />删除
      </div>
    </div>
    <!--表格-->
    <div class="tableBox">
      <el-table
        :data="tableData"
        @selection-change="checkAll"
        border
        ref="table"
        style="width: 100%"
        tooltip-effect="light"
        :height="listHeight - 80"
      >
        <el-table-column type="selection" width="55" align="center">
        </el-table-column>
        <el-table-column fixed="left" label="操作" width="50" align="left">
          <template #default="scope">
            <div
              @click="removeOne(scope.row)"
              type="text"
              size="small"
              title="查看缓存数据"
            >
              <font-awesome-icon icon="far fa-trash-alt"
                style="
                  float: left;
                  color: #f56c6c;
                  font-size: 18px;
                  width: 26px;
                  cursor: pointer;
                "
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="缓存KEY" :show-overflow-tooltip="true">
          <template #default="scope">
            <div v-text="scope.row.key"></div>
          </template>
        </el-table-column>
        <el-table-column
          label="缓存到期时间"
          width="140"
          :show-overflow-tooltip="true"
        >
          <template #default="scope">
            <div v-text="$yqvutil.setFormTime(scope.row.expireTime, 1)"></div>
          </template>
        </el-table-column>
      </el-table>
      <div class="paging-box">
        <div class="block">
          <el-pagination
            background
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="currentPage"
            :page-sizes="[50, 100, 200, 500]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="totals"
          >
          </el-pagination>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { mapState } from "vuex";
export default {
  name: "redisList",
  data() {
    return {
      //缓存信息弹框
      dialogRedisInfoShow: false,
      //当前页
      currentPage: 1,
      pageSize: 50,
      totals: 0,
      //全选单选容器
      container: [],
      tableData: [],
      redisList: [],
      key: "",
      redisData: "",
    };
  },
  computed: {
    ...mapState(["yqvSelect", "listHeight"]),
  },
  mounted() {
    this.getRedisList();
  },
  components: {},
  methods: {
    /********************************查询缓存列表*****************************/
    search() {
      this.getRedisList(true);
    },
    /********************************清空查询条件*****************************/
    empty() {
      this.key = "";
      this.getRedisList(true);
    },
    /********************************全选*****************************/
    checkAll(val) {
      //进行赋值,判断是否选中内容
      this.container = val;
    },
    /********************************删除指定缓存*****************************/
    //单独删除
    removeOne(val) {
      let vm = this;
      this.redisList = [];
      this.redisList.push(val);
      this.$http
        .post(vm.$yqvutil.getContextPath()+`yqvservice/redismanage/delete`, vm.redisList)
        .then(function (response) {
          if (response.status == 200) {
            vm.$message({
              showClose: true,
              message: "删除成功",
              type: "success",
            });
            vm.getRedisList();
          }
        })
        .catch(function (error) {});
    },
    //多选删除
    deleteRedisList() {
      if (this.container.length != 0) {
        this.redisList = this.container;
        let vm = this;
        let arr = [];
        for (let i = 0; i < vm.redisList.length; i++) {
          arr.push({ key: vm.redisList[i].key });
        }
        vm.$http
          .post(vm.$yqvutil.getContextPath()+`yqvservice/redismanage/delete`, arr)
          .then(function (response) {
            if (response.status == 200) {
              vm.$message({
                showClose: true,
                message: "删除成功",
                type: "success",
              });
              vm.getRedisList();
            }
          })
          .catch(function (error) {});
      } else {
        this.$message({
          showClose: true,
          message: "请选择要删除的内容",
          type: "warning",
        });
      }
    },
    /********************************查询缓存列表*****************************/
    getRedisList(flag) {
      let vm = this;
      let data = {};
      if (flag) {
        vm.currentPage = 1;
        vm.pageSize = 50;
        vm.totals = 0;
      }
      if (vm.key) {
        data.key = vm.key;
      }
      vm.$http
        .post(
          vm.$yqvutil.getContextPath()+`yqvservice/redismanage/list?currentPage=${vm.currentPage}&pageSize=${vm.pageSize}`,
          data
        )
        .then(function (response) {
          if (response.status == 200) {
            vm.tableData = response.data.result;
            vm.totals = response.data.page.totalRows;
          }
        })
        .catch(function (error) {});
    },
    /********************************分页*****************************/
    handleSizeChange(val) {
      this.pageSize = val;
      this.getRedisList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getRedisList();
    },
  },
};
</script>

<style scoped lang="less">
.queryList ::v-deep(.el-form-item__label) {
  width: 90px !important;
  height: 35px;
}
.redisList {
  height: calc(100% - 98px);
  .queryList {
    margin-left: 24px;
    .el-form {
      display: flex;
      flex-wrap: nowrap;
      .el-form-item {
        flex: 1;
        display: flex;
        flex-wrap: nowrap;
      }
      .queryBtn {
        flex: 0 0 130px;
      }
    }
  }
  .btnList {
    margin-left: 24px;
    margin-bottom: 14px;
    padding-top: 18px;
    box-sizing: border-box;
    display: flex;
    height: 48px;
    span {
      line-height: 30px;
      margin-left: 14px;
    }
    .el-input {
      width: 300px;
      margin-right: 14px;
    }
  }
  .authorityBox {
    height: 100%;
    position: relative;
  }
}
</style>

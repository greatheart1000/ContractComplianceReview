<template>
  <div class="log">
    <div class="tabBox">
      <el-tabs v-model="activeName">
        <el-tab-pane label="操作列表" name="first"> </el-tab-pane>
      </el-tabs>
      <div class="tabBottom"></div>
    </div>
    <div v-if="activeName == 'first'">
      <div class="search-box-one" style="width:100%;overflow:visible;margin-top: -14px;">
        <div style="width:33%;float:left;margin-top:20px;">
          <div style="width:100%;float:left;">
            <div class="search-box-top-name" style="width: 75px;">操作日期：</div>
            <div style="width:calc(100% - 100px - 2.3%);float:right;padding-right:2.3%">
              <el-date-picker
                v-model="queryTime"
                type="daterange"
                style="width:100%;"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                :disabled-date="disabledDate"
                :shortcuts="shortcuts"
                size="default"
              >
              </el-date-picker>
            </div>
          </div>
        </div>
        <div style="width:33%;float:left;margin-top:20px;">
          <div style="width:100%;float:left;">
            <div class="search-box-top-name" style="width: 75px;">操作结果：</div>
            <div style="width:calc(100% - 100px - 2.3%);float:right;padding-right:2.3%">
              <el-select
                v-model="queryData.logStatus"
                placeholder="请选择"
                style="width:100%"
                clearable
              >
                <el-option
                  v-for="item in $yqvutil.getSelectList('WALLEY_LOG_STATUS')"
                  :key="item.itemCode"
                  :label="item.itemName"
                  :value="item.itemValue"
                >
                </el-option>
              </el-select>
            </div>
          </div>
        </div>
        <div style="width:33%;float:left;margin-top:20px;">
          <div class="search-box-right" style="overflow: visible;width: 100%;">
            <div class="query" @click="empty" style="margin-right: 20px;">
              <font-awesome-icon icon="fas fa-refresh" />重置
            </div>
            <div class="query" @click="search" style="margin-right: 10px;">
              <font-awesome-icon icon="fas fa-search" />查询
            </div>
            <div class="more-down" @click="moreCondition = !moreCondition" v-if="!moreCondition">
              <span v-text="'展开'"></span>
              <font-awesome-icon icon="fas fa-angle-double-down" />
            </div>
            <div class="more-down" @click="moreCondition = !moreCondition" v-else>
              <span v-text="'收起'"></span>
              <font-awesome-icon icon="fas fa-angle-double-up" />
            </div>
          </div>
        </div>
      </div>
      <div v-if="moreCondition" class="search-box-two" style="width: 100%; height: 52px; float: left; margin-top: -10px;">
        <div style="width:33%;float:left;margin-top:20px;">
          <div style="width:100%;float:left;">
            <div class="search-box-top-name" style="width: 75px;">操作用户：</div>
            <div style="width:calc(100% - 100px - 2.3%);float:right;padding-right:2.3%">
              <UserSelect val="createAccount" :showData="createAccount" />
            </div>
          </div>
        </div>
      </div>
      <div class="clear"></div>
      <div style="margin-top: 10px;">
        <el-table :data="logList" border style="width: 100%" tooltip-effect="light" :height="moreCondition?tabListHeight-55:tabListHeight-13">
          <el-table-column type="index" width="50" label="NO." align="center" fixed="left" >
          </el-table-column>
          <el-table-column label="操作用户" align="left" width="110" :show-overflow-tooltip="true">
            <template #default="scope">
              <div v-text="scope.row.createName"></div>
            </template>
          </el-table-column>
          <el-table-column label="操作时间" align="left" width="140" :show-overflow-tooltip="true">
            <template #default="scope">
              <div v-text="$yqvutil.setFormTime(scope.row.createDate, 1)"></div>
            </template>
          </el-table-column>
          <el-table-column label="操作内容" align="left" :show-overflow-tooltip="true">
            <template #default="scope">
              <div v-text="'【'+scope.row.logName+'】'+scope.row.logContent"></div>
            </template>
          </el-table-column>
          <el-table-column label="操作结果" align="center" width="80" :show-overflow-tooltip="true">
            <template #default="scope">
              <div :class="[
                  scope.row.logStatus == 'Y'
                    ? 'activeColorY'
                    : 'activeColorN'
                ]" v-text="$yqvutil.getSelectItemName('WALLEY_LOG_STATUS',scope.row.logStatus)"></div>
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
              :page-sizes="[10, 20, 50, 100]"
              :page-size="pageSize"
              layout="total, sizes, prev, pager, next, jumper"
              :total="totals"
            >
            </el-pagination>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import UserSelect from "@/components/yqvalley/UserSelect";
import { mapState, mapMutations } from "vuex";
export default {
  name: "userLog",
  data() {
    return {
      moreCondition:false,
      //当前页
      currentPage: 1,
      pageSize: 10,
      totals: 0,
      //导航控制
      activeName: "first",
      logList: [],
      createAccount: "",
      queryData: {
        createAccount: "",
        beginTime: new Date(),
        endTime: new Date(),
        logName: "",
        requestIp: "",
        logStatus: ""
      },
      queryTime: [new Date(),new Date()],
      disabledDate(time) {
        return time.getTime() > Date.now();
      },
      shortcuts: [
          {
            text: '当月',
            value: () => {
              let date = new Date()
              date.setDate(1)
              let year= date.getFullYear()
              let month = date.getMonth()  + 1
              month  = month > 10 ? month :'0' + month
              let day  = date.getDate()
              const start = new Date(year +'-'+ month +'-'+ day)
              const end = new Date()
              return [start, end]
            },
          },
          {
            text: '当年',
            value: () => {
              const end = new Date()
              const start = new Date(new Date().getFullYear(), 0)
              return [start, end]
            },
          },{
            text: '近七天',
            value: () => {
              const end = new Date()
              const start = new Date()
              start.setDate(start.getDay() - 7)
              return [start, end]
            },
          },
          {
            text: '近一月',
            value: () => {
              const end = new Date()
              const start = new Date()
              start.setMonth(start.getMonth() - 1)
              return [start, end]
            },
          },
          {
            text: '近三月',
            value: () => {
              const end = new Date()
              const start = new Date()
              start.setMonth(start.getMonth() - 3)
              return [start, end]
            },
          },
          {
            text: '近六月',
            value: () => {
              const end = new Date()
              const start = new Date()
              start.setMonth(start.getMonth() - 6)
              return [start, end]
            },
          },
        ],
    };
  },
  mounted() {
    this.queryLogList();
  },
  computed: {
    ...mapState(["tabListHeight", "yqvSelect"])
  },
  watch: {

  },
  components: { UserSelect },
  methods: {
    search() {
      this.currentPage = 1;
      this.pageSize = 10;
      this.totals = 0;
      this.queryLogList();
    },
    empty() {
      this.currentPage = 1;
      this.pageSize = 10;
      this.totals = 0;
      this.createAccount = "";
      this.queryData.createAccount = "";
      this.queryData.beginTime = new Date();
      this.queryData.endTime = new Date();
      this.queryTime = [new Date(),new Date()],
      this.queryData.logName = "";
      this.queryData.requestIp = "";
      this.queryData.logStatus = "";
      this.queryLogList();
    },
    //查询日志接口
    queryLogList() {
      let vm = this;
      let data = {
        logName: vm.queryData.logName,
        requestIp: vm.queryData.requestIp,
        logStatus: vm.queryData.logStatus,
        createAccount: vm.createAccount
      };
      if(vm.queryTime && vm.queryTime.length>1){
        data.beginTime = vm.queryTime[0];
        data.endTime = vm.queryTime[1];
      }else{
        data.beginTime = null;
        data.endTime = null;
      }
      this.$http.post(
          vm.$yqvutil.getContextPath()+`yqvservice/userlog/page?currentPage=${
            this.currentPage
          }&pageSize=${this.pageSize}`,
          data
        )
        .then(function(response) {
          if (response.status == 200) {
            vm.logList = response.data.result;
            vm.totals = response.data.page.totalRows;
          }
        })
        .catch(function(error) {});
    },
    //分页
    handleSizeChange(val) {
      this.pageSize = val;
      this.queryLogList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.queryLogList();
    }
  }
};
</script>

<style scoped lang="less">
.log {
  .more-down {
    width: 60px;
    float: left;
    color: #5675f5;
    height: 32px;
    line-height: 32px;
    text-align: center;
    cursor: pointer;
  }
  .input-class {
    width: 100%;
  }
  .log-info {
    margin: 20px;
    border: 1px solid #f0f2f5;
    overflow: hidden;
    .log-info-one-left {
      width: 120px;
      float: left;
      text-align: center;
      line-height: 53px;
      height: 53px;
      background: #fafafa;
      color: #1890ff;
    }
    .log-info-one-right {
      float: left;
      width: calc(100% - 160px);
      line-height: 53px;
      padding: 0 20px;
    }
    .log-info-two {
      overflow: hidden;
      border-top: 1px solid #f0f2f5;
      width: 100%;
      .log-info-two-left {
        width: 120px;
        float: left;
        text-align: center;
        line-height: 110px;
        min-height: 110px;
        background: #fafafa;
        color: #1890ff;
        height: 100%;
        margin-bottom: -10000px;
        padding-bottom: 10000px;
      }
      .log-info-two-right {
        float: left;
        width: calc(100% - 160px);
        line-height: 53px;
        padding: 0 20px;
        margin-bottom: -10000px;
        padding-bottom: 10000px;
      }
    }
  }
  .el-textarea {
    margin: 15px 0;
  }
  .el-textarea ::v-deep(.el-textarea__inner) {
    min-height: 120px !important;
  }
}
</style>

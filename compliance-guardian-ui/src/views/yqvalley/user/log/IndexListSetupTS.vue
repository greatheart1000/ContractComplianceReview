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
          </div>
        </div>
      </div>
      <div class="clear"></div>
      <div style="margin-top: 10px;">
        <el-table :data="logList" border style="width: 100%" tooltip-effect="light" :height="tabListHeight-13">
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
              :current-page="pagination.currentPage"
              :page-sizes="[10, 20, 50, 100]"
              :page-size="pagination.pageSize"
              layout="total, sizes, prev, pager, next, jumper"
              :total="pagination.totals"
            >
            </el-pagination>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script lang="ts" setup>
/***************************引入**************************/
import { ref,reactive, computed,getCurrentInstance,onMounted} from "vue";
import { mapState,useStore, mapMutations } from "vuex";

/***************************公共变量**************************/
const context:Record<string, any>|undefined = getCurrentInstance()?.appContext.config.globalProperties;
const store = useStore();
const [tabListHeight, yqvSelect] = Object.values(mapState([ "tabListHeight", "yqvSelect"])).map(it=>computed(it.bind({$store:store})))
const [changeMenu] = Object.values(mapMutations(["changeMenu"])).map(it=>(it.bind({$store:store})))

/***************************业务变量**************************/
let pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  totals: 0
});
let activeName = ref("first");
let logList = ref([]);
let queryData = reactive({
  createAccount: "",
  beginTime: new Date(),
  endTime: new Date(),
  logName: "",
  requestIp: "",
  logStatus: ""
});
let queryTime:any = ref([new Date(),new Date()]);
const disabledDate = (time:Date) => {
  return time.getTime() > Date.now();
};
const shortcuts:any = [
  {
    text: '当月',
    value: () => {
      let date = new Date()
      date.setDate(1)
      let year= date.getFullYear()
      let monthStr:string = "";
      let month:number = date.getMonth()  + 1
      monthStr = month > 10 ? month+"" :'0' + month
      let day  = date.getDate()
      const start = new Date(year +'-'+ monthStr +'-'+ day)
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
];

/***************************初始化执行**************************/
onMounted(() => {
    queryLogList();
});

/***************************业务API**************************/
const search = () => {
  pagination.currentPage = 1;
  pagination.pageSize = 10;
  pagination.totals = 0;
  queryLogList();
};
const empty = () => {
  pagination.currentPage = 1;
  pagination.pageSize = 10;
  pagination.totals = 0;
  queryData.createAccount = "";
  queryData.beginTime = new Date();
  queryData.endTime = new Date();
  queryTime.value = [new Date(),new Date()],
  queryData.logName = "";
  queryData.requestIp = "";
  queryData.logStatus = "";
  queryLogList();
};
//查询日志接口
const queryLogList = () => {
  let data = {
    logName: queryData.logName,
    requestIp: queryData.requestIp,
    logStatus: queryData.logStatus,
    createAccount: queryData.createAccount,
    beginTime: null,
    endTime: null
  };
  if(queryTime && queryTime._value.length>1){
    data.beginTime = queryTime._value[0];
    data.endTime = queryTime._value[1];
  }else{
    data.beginTime = null;
    data.endTime = null;
  }
  if(context){
    context.$http.post(context.$yqvutil.getContextPath()+`yqvservice/userlog/page?currentPage=${
        pagination.currentPage
      }&pageSize=${pagination.pageSize}`,
      data
    )
    .then(function(response:any) {
      if (response.status == 200) {
        logList.value = response.data.result;
        pagination.totals = response.data.page.totalRows;
      }
    })
    .catch(function(error:object) {})
  }
};
//分页
const handleSizeChange = (val:number) => {
  pagination.pageSize = val;
  queryLogList();
};
const handleCurrentChange = (val:number) => {
  pagination.currentPage = val;
  queryLogList();
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

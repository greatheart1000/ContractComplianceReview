<template>
  <div class="importList">
    <!-- 查看错误日志 -->
    <el-dialog
      title="失败原因"
      v-model="dialogExportErrorShow"
      :close-on-click-modal="false"
      @close="closeExportErrorDialog"
      width="480px"
    >
      <div style="width: 430px; height: 220px; margin: 0 auto">
        <div class="form-info">
          <div class="form-info-two" style="height: 220px">
            <div class="form-info-two-right">
              <el-input
                type="textarea"
                :disabled="true"
                class="errorText"
                :rows="11"
                v-model="errorData.exceptionMsg"
              ></el-input>
            </div>
          </div>
        </div>
      </div>
      <div style="clear: both"></div>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="dialogExportErrorShow = false">确 定</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 查询条件 -->
    <div
      class="search-box-one"
      style="width: 100%; overflow: visible; height: 40px"
    >
      <div style="width: 33%; float: left; margin-top: 20px">
        <div style="width: 100%; float: left">
          <div class="search-box-top-name" style="width: 75px">导出日期：</div>
          <div
            style="
              width: calc(100% - 100px - 2.3%);
              float: right;
              padding-right: 2.3%;
            "
          >
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
      <div style="width: 33%; float: left; margin-top: 20px">
        <div style="width: 100%; float: left">
          <div class="search-box-top-name" style="width: 75px">导出状态：</div>
          <div
            style="
              width: calc(100% - 100px - 2.3%);
              float: right;
              padding-right: 2.3%;
            "
          >
            <el-select
              v-model="queryData.processStatus"
              placeholder="请选择导出状态"
              style="width:100%;"
              clearable
            >
              <el-option
                v-for="item in $yqvutil.getSelectList('FILE_EXPORT_STATUS')"
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
      <el-table
        :data="dataList"
        border
        style="width: 100%"
        tooltip-effect="light"
        :height="listHeight - 30"
      >
        <el-table-column
          type="index"
          width="50"
          label="NO."
          align="center"
          fixed="left"
        ></el-table-column>
        <el-table-column fixed="left" label="操作" align="left" width="70">
          <template #default="scope">
            <font-awesome-icon icon="fas fa-download"
              v-if="
                scope.row.fileId &&
                scope.row.fileId != null &&
                scope.row.fileId != ''
              "
              style="
                float: left;
                color: #33db67;
                font-size: 18px;
                width: 26px;
                cursor: pointer;
              "
              @click="download(scope.row)"
              title="下载文件"
            />
            <font-awesome-icon icon="fas fa-exclamation-circle"
              v-if="
                scope.row.processStatus == 'partfail' ||
                scope.row.processStatus == 'fail'
              "
              style="
                float: left;
                color: #f56c6c;
                font-size: 18px;
                width: 18px;
                cursor: pointer;
              "
              @click="showExportErrorDialog(scope.row)"
              title="查看错误信息"
            />
          </template>
        </el-table-column>
        <el-table-column
          label="导出状态"
          align="left"
          min-width="80"
          :show-overflow-tooltip="true"
        >
          <template #default="scope">
            <span v-text="$yqvutil.getSelectItemName('FILE_EXPORT_STATUS',scope.row.processStatus)"></span>
          </template>
        </el-table-column>
        <el-table-column
          label="文件名称"
          align="left"
          min-width="240"
          :show-overflow-tooltip="true"
        >
          <template #default="scope">
            <span
              style="color: #1890ff"
              :title="scope.row.fileName"
              v-text="scope.row.fileName"
            ></span>
          </template>
        </el-table-column>
        <el-table-column
          label="文件大小"
          align="center"
          min-width="80"
          :show-overflow-tooltip="true"
        >
          <template #default="scope">
            <span v-text="$yqvutil.getFileSize(scope.row.fileSize)"></span>
          </template>
        </el-table-column>
        <el-table-column
          label="处理进度"
          align="center"
          min-width="80"
          :show-overflow-tooltip="true"
        >
          <template #default="scope">
            <span v-text="scope.row.processProgressStr"></span>
          </template>
        </el-table-column>
        <el-table-column
          label="成功数量"
          align="center"
          min-width="80"
          :show-overflow-tooltip="true"
        >
          <template #default="scope">
            <span v-text="scope.row.totalRows"></span>
          </template>
        </el-table-column>
        <el-table-column
          label="处理时长"
          align="center"
          min-width="80"
          :show-overflow-tooltip="true"
        >
          <template #default="scope">
            <span v-text="$yqvutil.formatDuring(scope.row.processDuration)"></span>
          </template>
        </el-table-column>
        <el-table-column
          label="操作用户"
          min-width="110"
          :show-overflow-tooltip="true"
        >
          <template #default="scope">
            <span v-text="scope.row.createName"></span>
          </template>
        </el-table-column>
        <el-table-column
          label="创建时间"
          align="left"
          width="140"
          :show-overflow-tooltip="true"
        >
          <template #default="scope">
            <span v-text="$yqvutil.setFormTime(scope.row.createDate, 1)"></span>
          </template>
        </el-table-column>
        <el-table-column
          label="开始时间"
          align="left"
          width="140"
          :show-overflow-tooltip="true"
        >
          <template #default="scope">
            <span
              v-text="$yqvutil.setFormTime(scope.row.processBeginTime, 1)"
            ></span>
          </template>
        </el-table-column>
        <el-table-column
          label="结束时间"
          align="left"
          width="140"
          :show-overflow-tooltip="true"
        >
          <template #default="scope">
            <span
              v-text="$yqvutil.setFormTime(scope.row.processEndTime, 1)"
            ></span>
          </template>
        </el-table-column>
      </el-table>
    </div>
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
</template>
<script>
import { mapState, mapMutations } from "vuex";
export default {
  name: "ExportList",
  data() {
    return {
      dialogExportErrorShow: false,
      //当前页
      currentPage: 1,
      pageSize: 10,
      totals: 0,
      dataList: [],
      queryData: {
        processStatus: "",
        beginTime: new Date(),
        endTime: new Date(),
      },
      errorData: {
        fileName: "",
        exceptionMsg: "",
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
    this.getExportList();
  },
  computed: {
    ...mapState(["listHeight", "yqvSelect"]),
  },
  components: {},
  methods: {
    /********************************分页*****************************/
    handleSizeChange(val) {
      this.pageSize = val;
      this.getExportList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getExportList();
    },
    /********************************查询列表*****************************/
    getExportList() {
      let vm = this;
      let data = this.queryData;
      if(vm.queryTime && vm.queryTime.length>1){
        data.beginTime = vm.queryTime[0];
        data.endTime = vm.queryTime[1];
      }else{
        data.beginTime = null;
        data.endTime = null;
      }
      this.$http
        .post(
          vm.$yqvutil.getContextPath()+`yqvservice/export/page?currentPage=${this.currentPage}&pageSize=${this.pageSize}`,
          data
        )
        .then(function (response) {
          if (response.status == 200) {
            vm.dataList = response.data.result;
            vm.totals = response.data.page.totalRows;
          }
        })
        .catch(function (error) {});
    },
    download(val) {
      let src = this.$yqvutil.getContextPath()+"fileservice/file/download?fileId=" + val.fileId;
      this.$yqvutil.downloads(src);
    },
    /********************************查看错误信息*****************************/
    showExportErrorDialog(val) {
      this.errorData.fileName = "";
      this.errorData.exceptionMsg = "";
      let vm = this;
      this.$http
        .get(vm.$yqvutil.getContextPath()+`yqvservice/export/find?exportId=` + val.exportId)
        .then(function (response) {
          if (response.status == 200) {
            vm.dialogExportErrorShow = true;
            vm.errorData.fileName = response.data.fileName;
            vm.errorData.exceptionMsg = response.data.exceptionMsg;
          }
        })
        .catch(function (error) {});
    },
    closeExportErrorDialog() {
      this.dialogExportErrorShow = false;
    },
    /********************************查询列表*****************************/
    search() {
      this.currentPage = 1;
      this.pageSize = 10;
      this.totals = 0;
      this.getExportList();
    },
    /********************************重置查询条件*****************************/
    empty() {
      this.currentPage = 1;
      this.pageSize = 10;
      this.totals = 0;
      this.queryData.processStatus = "";
      this.queryData.beginTime = new Date();
      this.queryData.endTime = new Date();
      this.queryTime = [new Date(),new Date()],
      this.getExportList();
    },
  },
};
</script>

<style scoped lang="less">
</style>

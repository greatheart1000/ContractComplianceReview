<template>
  <div class="mainTempList">
    <div class="tabBox">
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane label="邮件模板" name="first"> </el-tab-pane>
        <el-tab-pane
          v-if="judgeLabel"
          label="编辑模板"
          name="second"
        ></el-tab-pane>
        <el-tab-pane
          v-if="sendLabel"
          label="发送邮件"
          name="sendmail"
        ></el-tab-pane>
      </el-tabs>
      <div class="tabBottom"></div>
    </div>
    <div v-if="activeName == 'first'">
      <!-- 查询条件 -->
      <div
        class="search-box-one"
        style="width: 78%; overflow: visible; margin-top: -20px"
      >
        <div style="width: 99%; float: left; margin-top: 20px">
          <div style="width: 100%; float: left">
            <div class="search-box-top-name" style="width: 75px">
              模板编号：
            </div>
            <div
              style="
                width: calc(100% - 100px - 2.3%);
                float: right;
                padding-right: 2.3%;
              "
            >
              <el-input
                v-model="templateCode"
                placeholder="请输入模板编号"
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
          style="margin-right: 20px; margin-top: -15px"
        >
          <font-awesome-icon icon="fas fa-refresh" />重置
        </div>
        <div
          class="query"
          @click="search"
          style="margin-right: 10px; margin-top: -15px"
        >
          <font-awesome-icon icon="fas fa-search" />查询
        </div>
      </div>
      <div class="clear"></div>
      <!-- 操作按钮 -->
      <div class="btn-box" style="margin-top: 10px">
        <div class="new" style="width: 60px" @click="addMailTempDialog">
          <font-awesome-icon icon="fas fa-plus-circle" />新建
        </div>
        <div class="new" style="width: 90px" @click="sendMailDialog" v-if="$yqvutil.getVerifyAuthority('yqv.mail.manual_send_mail')">
          <font-awesome-icon icon="far fa-envelope" />手动发送
        </div>
      </div>
      <!--表格-->
      <div v-if="tableData.length">
        <el-table
          :data="tableData"
          border
          ref="table"
          style="width: 100%"
          tooltip-effect="light"
          :height="tabListHeight - 60"
        >
          <el-table-column
            type="index"
            width="50"
            label="NO."
            align="center"
            fixed="left"
          >
          </el-table-column>
          <el-table-column fixed="left" label="操作" width="95" align="left">
            <template #default="scope">
              <div
                type="text"
                size="small"
                @click="editMailTempDialog(scope.row)"
              >
                <font-awesome-icon icon="far fa-edit"
                  style="
                    float: left;
                    color: #33db67;
                    font-size: 20px;
                    width: 26px;
                    cursor: pointer;
                  "
                />
              </div>
              <div type="text" size="small" @click="deleteMailTemp(scope.row)">
                <font-awesome-icon icon="far fa-trash-alt"
                  style="
                    float: left;
                    color: #f56c6c;
                    font-size: 20px;
                    width: 26px;
                    cursor: pointer;
                  "
                />
              </div>
              <div
                type="text"
                size="small"
                @click="sendMailTempDialog(scope.row)"
                v-if="$yqvutil.getVerifyAuthority('yqv.mail.manual_send_mail')"
              >
                <font-awesome-icon icon="far fa-envelope"
                  style="
                    float: left;
                    color: #1890ff;
                    font-size: 20px;
                    width: 26px;
                    cursor: pointer;
                  "
                />
              </div>
            </template>
          </el-table-column>
          <el-table-column
            label="模板编号"
            align="left"
            width="220"
            :show-overflow-tooltip="true"
          >
            <template #default="scope">
              <span v-text="scope.row.templateCode"></span>
            </template>
          </el-table-column>
          <el-table-column
            label="模板说明"
            align="left"
            width="160"
            :show-overflow-tooltip="true"
          >
            <template #default="scope">
              <span v-text="scope.row.templateDesc"></span>
            </template>
          </el-table-column>
          <el-table-column
            label="邮件标题"
            align="left"
            minWidth="200"
            :show-overflow-tooltip="true"
          >
            <template #default="scope">
              <span v-text="scope.row.templateTitle"></span>
            </template>
          </el-table-column>
          <el-table-column
            label="修改用户"
            align="left"
            width="110"
            :show-overflow-tooltip="true"
          >
            <template #default="scope">
              <div v-text="scope.row.lastUpdateName"></div>
            </template>
          </el-table-column>
          <el-table-column
            label="修改时间"
            align="left"
            width="140"
            :show-overflow-tooltip="true"
          >
            <template #default="scope">
              <div
                v-text="$yqvutil.setFormTime(scope.row.lastUpdateDate, 1)"
              ></div>
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
    <div v-if="activeName == 'second'" class="authorityBox">
      <EditMailTemp
        :templateCode="mailTemp.templateCode"
        :templateDesc="mailTemp.templateDesc"
        :templateTitle="mailTemp.templateTitle"
        :templateContent="mailTemp.templateContent"
      />
    </div>
    <div v-if="activeName == 'sendmail'" class="authorityBox">
      <EditMailSend
        :templateCode="mailTemp.templateCode"
        :templateDesc="mailTemp.templateDesc"
        :templateTitle="mailTemp.templateTitle"
        :templateContent="mailTemp.templateContent"
      />
    </div>
  </div>
</template>
<script>
import EditMailTemp from "./EditMailTemp";
import EditMailSend from "./EditMailSend";
import { mapState, mapMutations } from "vuex";
export default {
  name: "MainTempList",
  data() {
    return {
      //当前页
      currentPage: 1,
      pageSize: 10,
      totals: 0,
      //导航控制
      activeName: "first",
      //判断哪个标签
      judgeLabel: false,
      sendLabel: false,
      //角色列表数据
      tableData: [],
      //编辑页面传参
      templateCode: "",
      mailTemp: {
        templateCode: "",
        templateDesc: "",
        templateTitle: "",
        templateContent: "",
      },
    };
  },
  mounted() {
    this.getMailTempList();
  },
  computed: {
    ...mapState(["tabListHeight"]),
  },
  components: {
    EditMailTemp,
    EditMailSend
  },
  methods: {
    /********************************查询列表*****************************/
    search() {
      this.getMailTempList();
    },
    /********************************清空查询条件*****************************/
    empty() {
      this.templateCode = "";
      this.getMailTempList();
    },
    /********************************删除配置*****************************/
    deleteMailTemp(val) {
      this.$confirm("此操作将删除邮件模板, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        closeOnClickModal: false,
      })
        .then(() => {
          let data = {
            templateCode: val.templateCode,
          };
          let vm = this;
          this.$http
            .post(vm.$yqvutil.getContextPath()+`yqvservice/mailtemp/delete`, data)
            .then(function (response) {
              vm.$message({
                type: "success",
                message: "删除成功!",
              });
              vm.getMailTempList();
            })
            .catch(function (error) {});
        })
        .catch(() => {});
    },
    /********************************查询配置列表*****************************/
    getMailTempList() {
      let vm = this;
      let data = { templateCode: vm.templateCode };
      this.$http
        .post(
          vm.$yqvutil.getContextPath()+`yqvservice/mailtemp/page?currentPage=${vm.currentPage}&pageSize=${vm.pageSize}`,
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
    /********************************标签操作*****************************/
    handleClick(row) {
      let labelName = null;
      if(row.label){
        labelName = row.label;
      }
      if(!labelName && row.props){
        labelName = row.props.label;
      }
      if (labelName == "邮件模板") {
        this.activeName = "first";
        this.judgeLabel = false;
        this.sendLabel = false;
      }
    },
    /********************************新增邮件模板*****************************/
    addMailTempDialog() {
      //打开配置页开关
      this.judgeLabel = true;
      //光标对位
      this.activeName = "second";
      this.mailTemp.templateCode = "";
      this.mailTemp.templateDesc = "";
      this.mailTemp.templateTitle = "";
      this.mailTemp.templateContent = "";
    },
    /********************************查询邮件模板*****************************/
    editMailTempDialog(val) {
      let vm = this;
      this.$http.get(vm.$yqvutil.getContextPath()+`yqvservice/mailtemp/find?tempCode=${val.templateCode}`)
        .then(function (response) {
          if (response.status == 200) {
            //打开配置页开关
            vm.judgeLabel = true;
            //光标对位
            vm.activeName = "second";
            vm.mailTemp = response.data;
          }
        })
        .catch(function (error) {});
    },
    /********************************新增邮件模板*****************************/
    sendMailDialog() {
      //打开配置页开关
      this.sendLabel = true;
      //光标对位
      this.activeName = "sendmail";
      this.mailTemp.templateCode = "";
      this.mailTemp.templateDesc = "";
      this.mailTemp.templateTitle = "";
      this.mailTemp.templateContent = "";
    },
    /********************************手动发送邮件*****************************/
    sendMailTempDialog(val) {
      let vm = this;
      this.$http.get(vm.$yqvutil.getContextPath()+`yqvservice/mailtemp/find?tempCode=${val.templateCode}`)
        .then(function (response) {
          if (response.status == 200) {
            //打开配置页开关
            vm.sendLabel = true;
            //光标对位
            vm.activeName = "sendmail";
            vm.mailTemp = response.data;
          }
        })
        .catch(function (error) {});
    },
    /********************************分页*****************************/
    handleSizeChange(val) {
      this.pageSize = val;
      this.getMailTempList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getMailTempList();
    },
  },
};
</script>

<style scoped lang="less">
.mainTempList {
  height: calc(100% - 98px);
  .btnList {
    overflow: hidden;
    margin-left: 24px;
  }
  .authorityBox {
    height: 100%;
    position: relative;
  }
}
.el-table ::v-deep(.warning-row) {
  background: #f1f1f1;
}
.form-info {
  border: 1px solid #f0f2f5;
  overflow: hidden;
  .form-info-one-left {
    width: 70px;
    float: left;
    text-align: center;
    line-height: 53px;
    height: 53px;
    background: #fafafa;
    color: #1890ff;
  }
  .form-info-one-right {
    float: left;
    width: calc(100% - 110px);
    line-height: 53px;
    padding: 0 20px;
  }
  .form-info-two {
    overflow: hidden;
    border-top: 1px solid #f0f2f5;
    width: 100%;
    .form-info-two-left {
      width: 70px;
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
    .form-info-two-right {
      float: left;
      width: calc(100% - 110px);
      line-height: 53px;
      padding: 0 20px;
      margin-bottom: -10000px;
      padding-bottom: 10000px;
    }
  }
}
</style>
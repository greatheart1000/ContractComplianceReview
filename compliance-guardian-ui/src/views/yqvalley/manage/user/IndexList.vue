<template>
  <div class="userManage">
    <!-- 修改账号有效期 -->
    <el-dialog
      title="修改账号有效期"
      v-model="dialogUserValiddateShow"
      :close-on-click-modal="false"
      @close="closeUserValiddateDialog"
      width="480px"
    >
      <div style="width: 430px; height: 300px; margin: 0 auto">
        <div class="form-info">
          <div>
            <div class="form-info-one-left">有效截止</div>
            <div class="form-info-one-right">
              <el-date-picker
                v-model="validDateData.validEndDate"
                type="date"
                style="width: 100%"
                placeholder="选择操作日期"
              ></el-date-picker>
            </div>
          </div>
          <div class="form-info-two" style="height: 240px">
            <div class="form-info-two-left">选择用户</div>
            <div class="form-info-two-right">
              <el-input
                type="textarea"
                :disabled="true"
                :rows="10"
                v-model="validDateData.valleyAccountNames"
              ></el-input>
            </div>
          </div>
        </div>
      </div>
      <div style="clear: both"></div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogUserValiddateShow = false">取 消</el-button>
          <el-button type="primary" @click="updateUserValiddate"
            >确 定</el-button
          >
        </div>
      </template>
    </el-dialog>
    <!-- 修改所属部门 -->
    <el-dialog
      title="修改所属部门"
      v-model="dialogUserDeptShow"
      :close-on-click-modal="false"
      @close="closeUserDeptDialog"
      width="480px"
    >
      <div style="width: 430px; height: 300px; margin: 0 auto">
        <div class="form-info">
          <div>
            <div class="form-info-one-left">所属部门</div>
            <div class="form-info-one-right">
              <el-select
                v-model="deptData.deptCode"
                placeholder="请选择部门"
                style="width: 100%"
                clearable
              >
                <el-option
                  v-for="item in deptList"
                  :key="item.deptCode"
                  :label="item.completeName"
                  :value="item.deptCode"
                >
                </el-option>
              </el-select>
            </div>
          </div>
          <div class="form-info-two" style="height: 240px">
            <div class="form-info-two-left">选择用户</div>
            <div class="form-info-two-right">
              <el-input
                type="textarea"
                :disabled="true"
                :rows="10"
                v-model="deptData.valleyAccountNames"
              ></el-input>
            </div>
          </div>
        </div>
      </div>
      <div style="clear: both"></div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogUserDeptShow = false">取 消</el-button>
          <el-button type="primary" @click="updateUserDept">确 定</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 修改汇报用户 -->
    <el-dialog
      title="修改汇报用户"
      v-model="dialogReportUserShow"
      :close-on-click-modal="false"
      @close="closeReportUserDialog"
      width="480px"
    >
      <div style="width: 430px; height: 300px; margin: 0 auto">
        <div class="form-info">
          <div>
            <div class="form-info-one-left">汇报用户</div>
            <div
              class="form-info-one-right"
              style="line-height: 32px; margin-top: 10px"
            >
              <UserSelect val="updReportAccount" :showData="updReportAccount" />
            </div>
          </div>
          <div class="form-info-two" style="height: 240px">
            <div class="form-info-two-left">选择用户</div>
            <div class="form-info-two-right">
              <el-input
                type="textarea"
                :disabled="true"
                :rows="10"
                v-model="reportUserData.valleyAccountNames"
              ></el-input>
            </div>
          </div>
        </div>
      </div>
      <div style="clear: both"></div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogReportUserShow = false">取 消</el-button>
          <el-button type="primary" @click="updateReportUser">确 定</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 默认密码 -->
    <el-dialog
      title="默认密码"
      v-model="dialogUserPwdShow"
      :close-on-click-modal="false"
      @close="closeUserPwdDialog"
      width="330px"
    >
      <div style="width: 280px; height: 100px; margin: 0 auto">
        <div class="form-info">
          <div>
            <div class="form-info-one-left">用户账号</div>
            <div class="form-info-one-right">
              <el-input
                disabled="disabled"
                v-model="userDefaultPwd.valleyAccount"
              ></el-input>
            </div>
          </div>
          <div>
            <div class="form-info-one-left">默认密码</div>
            <div class="form-info-one-right">
              <el-input
                readonly="readonly"
                v-model="userDefaultPwd.defaultLoginPwd"
              ></el-input>
            </div>
          </div>
        </div>
      </div>
      <div style="clear: both"></div>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="dialogUserPwdShow = false"
            >确 定</el-button
          >
        </div>
      </template>
    </el-dialog>
    <!-- 显示页面 -->
    <div class="tabBox">
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane label="用户列表" name="first"> </el-tab-pane>
        <el-tab-pane
          v-if="judgeLabel"
          label="用户信息"
          name="second"
        ></el-tab-pane>
      </el-tabs>
      <div class="tabBottom"></div>
    </div>
    <div v-if="activeName == 'first'">
      <!-- 查询条件 -->
      <div
        class="search-box-one"
        style="width: 100%; overflow: visible; margin-top: -20px"
      >
        <div style="width: 33%; float: left; margin-top: 20px">
          <div style="width: 100%; float: left">
            <div class="search-box-top-name" style="width: 75px">
              用户账号：
            </div>
            <div
              style="
                width: calc(100% - 100px - 2.3%);
                float: right;
                padding-right: 2.3%;
              "
            >
              <UserSelect val="valleyAccount" :showData="valleyAccount" />
            </div>
          </div>
        </div>
        <div style="width: 33%; float: left; margin-top: 20px">
          <div style="width: 100%; float: left">
            <div class="search-box-top-name" style="width: 75px">
              用户状态：
            </div>
            <div
              style="
                width: calc(100% - 100px - 2.3%);
                float: right;
                padding-right: 2.3%;
              "
            >
              <el-select
                v-model="queryData.accountStatus"
                placeholder="请选择用户状态"
                style="width: 100%"
                clearable
              >
                <el-option
                  v-for="item in $yqvutil.getSelectList('USER_VALID_STATE')"
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
            <div class="query" @click="emptyQuery" style="margin-right: 20px;">
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
        <div style="width: 33%; float: left; margin-top: 20px">
          <div style="width: 100%; float: left">
            <div class="search-box-top-name" style="width: 75px">
              汇报用户：
            </div>
            <div
              style="
                width: calc(100% - 100px - 2.3%);
                float: right;
                padding-right: 2.3%;
              "
            >
              <UserSelect val="reportAccount" :showData="reportAccount" />
            </div>
          </div>
        </div>
        <div style="width: 33%; float: left; margin-top: 20px">
          <div style="width: 100%; float: left">
            <div class="search-box-top-name" style="width: 75px">
              手机号码：
            </div>
            <div
              style="
                width: calc(100% - 100px - 2.3%);
                float: right;
                padding-right: 2.3%;
              "
            >
              <el-input
                v-model="queryData.phoneNumber"
                placeholder="请输入手机号码"
                clearable
                style="width: 100%"
              ></el-input>
            </div>
          </div>
        </div>
        <div style="width: 33%; float: left; margin-top: 20px">
          <div style="width: 100%; float: left">
            <div class="search-box-top-name" style="width: 75px">
              联系邮箱：
            </div>
            <div
              style="
                width: calc(100% - 100px - 2.3%);
                float: right;
                padding-right: 2.3%;
              "
            >
              <el-input
                v-model="queryData.mailAddress"
                placeholder="请输入联系邮箱"
                clearable
                style="width: 100%"
              ></el-input>
            </div>
          </div>
        </div>
      </div>
      <div v-if="moreCondition" class="search-box-two" style="width: 100%; height: 52px; float: left; margin-top: -10px;">
        <div style="width: 33%; float: left; margin-top: 20px">
          <div style="width: 100%; float: left">
            <div class="search-box-top-name" style="width: 75px">
              所属部门：
            </div>
            <div
              style="
                width: calc(100% - 100px - 2.3%);
                float: right;
                padding-right: 2.3%;
              "
            >
              <el-select
                v-model="queryData.deptCode"
                placeholder="请选择部门"
                style="width: 100%"
                clearable
              >
                <el-option
                  v-for="item in deptList"
                  :key="item.deptCode"
                  :label="item.completeName"
                  :value="item.deptCode"
                >
                </el-option>
              </el-select>
            </div>
          </div>
        </div>
      </div>
      <div class="clear"></div>
      <!-- 操作按钮 -->
      <div class="btn-box" style="margin-top: 10px">
        <div class="new" style="width: 60px" @click="editUserInfo">
          <font-awesome-icon icon="fas fa-user-plus" />新建
        </div>
        <div
          class="start"
          style="width: 60px"
          @click="updateUserStatus('enable')"
        >
          <font-awesome-icon icon="far fa-play-circle" />启用
        </div>
        <div
          class="prohibit"
          style="width: 60px; background: #f56c6c"
          @click="updateUserStatus('disable')"
        >
          <font-awesome-icon icon="fas fa-ban" />禁用
        </div>
        <div
          class="prohibit"
          style="width: 80px"
          @click="showUserValiddateDialog"
        >
          有效期修改
        </div>
        <div class="prohibit" style="width: 70px" @click="showUserDeptDialog">
          部门修改
        </div>
        <div
          class="prohibit"
          style="width: 100px"
          @click="showReportUserDialog"
        >
          汇报用户修改
        </div>
        <div
          class="prohibit"
          style="width: 100px"
          @click="updateUserOnlineCache"
        >
          刷新用户权限
        </div>
        <div class="query" style="width: 60px" @click="exportExcel()">
          <font-awesome-icon icon="fas fa-download" />导出
        </div>
        <div class="query" style="width: 90px" @click="downloadExcelTemplate()">
          <font-awesome-icon icon="fas fa-download" />模板下载
        </div>
        <el-upload
          class="upload-demo"
          ref="upload"
          style="width: 60px; float: left; margin-right: 6px"
          :action="$yqvutil.getContextPath()+'yqvservice/file/upload?fileType=import'"
          :headers="myHeaders"
          :on-success="handleAvatarSuccess"
          :on-error="handleAvatarError"
          multiple
          :limit="2"
          accept=".xls,.xlsx"
        >
          <el-button size="small" type="primary" style="width: 60px">
            <font-awesome-icon icon="far fa-file-excel"
              style="margin-right: 5px; margin-left: -6px"
            />导入
          </el-button>
        </el-upload>
      </div>
      <!--用户列表-->
      <div v-if="tableData.length">
        <el-table
          :data="tableData"
          border
          @selection-change="checkAll"
          style="width: 100%"
          :height="moreCondition?tabListHeight-132:tabListHeight-48"
          tooltip-effect="light"
          ref="table"
        >
          <!-- 选择项目 -->
          <el-table-column type="selection" width="55" align="center">
          </el-table-column>
          <!-- 操作选项 -->
          <el-table-column fixed="left" label="操作" width="125" align="left">
            <template #default="scope">
              <div
                @click="editUserInfo(scope.row)"
                type="text"
                size="small"
                title="用户修改"
              >
                <font-awesome-icon icon="far fa-edit"
                  style="
                    float: left;
                    color: #33db67;
                    font-size: 18px;
                    width: 26px;
                    cursor: pointer;
                  "
                />
              </div>
              <div
                type="text"
                size="small"
                @click="resetDefaultPwd(scope.row)"
                title="重置密码"
              >
                <font-awesome-icon icon="fas fa-undo"
                  style="
                    float: left;
                    color: #f56c6c;
                    font-size: 18px;
                    width: 26px;
                    cursor: pointer;
                  "
                />
              </div>
              <div
                type="text"
                size="small"
                @click="showUserPwdDialog(scope.row)"
                title="查看默认密码"
              >
                <font-awesome-icon icon="fas fa-key"
                  style="
                    float: left;
                    color: #1890ff;
                    font-size: 18px;
                    width: 26px;
                    cursor: pointer;
                  "
                />
              </div>
              <div
                type="text"
                size="small"
                @click="resetUserCache(scope.row)"
                title="刷新用户权限"
              >
                <font-awesome-icon icon="fas fa-retweet"
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
          <!-- 数据字段 -->
          <el-table-column
            fixed="left"
            label="登录账号"
            align="left"
            width="80"
            :show-overflow-tooltip="true"
          >
            <template #default="scope">
              <span v-text="scope.row.loginAccount"></span>
            </template>
          </el-table-column>
          <el-table-column
            fixed="left"
            label="用户姓名"
            align="left"
            width="120"
            :show-overflow-tooltip="true"
          >
            <template #default="scope">
              <span
                v-text="
                  scope.row.userName + '(' + scope.row.valleyAccount + ')'
                "
              ></span>
            </template>
          </el-table-column>
          <el-table-column
            label="手机号码"
            align="left"
            width="100"
            :show-overflow-tooltip="true"
          >
            <template #default="scope">
              <span v-text="scope.row.phoneNumber"></span>
            </template>
          </el-table-column>
          <el-table-column
            label="联系邮箱"
            align="left"
            width="160"
            :show-overflow-tooltip="true"
          >
            <template #default="scope">
              <span v-text="scope.row.mailAddress"></span>
            </template>
          </el-table-column>
          <el-table-column
            label="用户角色"
            align="left"
            width="220"
            :show-overflow-tooltip="true"
          >
            <template #default="scope">
              <span v-text="scope.row.roleNames"></span>
            </template>
          </el-table-column>
          <el-table-column label="有效截止" align="left" width="100">
            <template #default="scope">
              <span
                v-text="$yqvutil.setFormTime(scope.row.validEndDate, 4)"
              ></span>
            </template>
          </el-table-column>
          <el-table-column label="用户状态" align="center">
            <template #default="scope">
              <span
                :class="[
                  scope.row.accountStatus == 'Y'
                    ? 'activeColorY'
                    : 'activeColorN',
                ]"
                v-text="setState(scope.row.accountStatus)"
              ></span>
            </template>
          </el-table-column>
          <el-table-column
            label="所属部门"
            align="left"
            width="260"
            :show-overflow-tooltip="true"
          >
            <template #default="scope">
              <span v-text="scope.row.completeName"></span>
            </template>
          </el-table-column>
          <el-table-column
            label="汇报用户"
            align="left"
            width="110"
            :show-overflow-tooltip="true"
          >
            <template #default="scope">
              <span v-text="scope.row.reportName"></span>
            </template>
          </el-table-column>
          <el-table-column label="密码更新时间" align="left" width="140">
            <template #default="scope">
              <span
                v-text="$yqvutil.setFormTime(scope.row.loginPwdUpdDate, 1)"
              ></span>
            </template>
          </el-table-column>
          <el-table-column
            label="最后登录IP"
            align="left"
            width="100"
            :show-overflow-tooltip="true"
          >
            <template #default="scope">
              <span v-text="scope.row.lastLoginIp"></span>
            </template>
          </el-table-column>
          <el-table-column label="最后登录时间" align="left" width="140">
            <template #default="scope">
              <span
                v-text="$yqvutil.setFormTime(scope.row.lastLoginDate, 1)"
              ></span>
            </template>
          </el-table-column>
          <el-table-column
            label="创建用户"
            align="left"
            width="110"
            :show-overflow-tooltip="true"
          >
            <template #default="scope">
              <span v-text="scope.row.createName"></span>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" align="left" width="140">
            <template #default="scope">
              <span v-text="$yqvutil.setFormTime(scope.row.createDate, 1)"></span>
            </template>
          </el-table-column>
          <el-table-column
            label="修改用户"
            align="left"
            width="110"
            :show-overflow-tooltip="true"
          >
            <template #default="scope">
              <span v-text="scope.row.lastUpdateName"></span>
            </template>
          </el-table-column>
          <el-table-column label="修改时间" align="left" width="140">
            <template #default="scope">
              <span
                v-text="$yqvutil.setFormTime(scope.row.lastUpdateDate, 1)"
              ></span>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <!-- 分页 -->
      <div class="paging-box" v-if="tableData.length">
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
    <div v-if="activeName == 'second'">
      <EditUserInfo :accountData="configName" />
    </div>
  </div>
</template>
<script>
import EditUserInfo from "./EditUserInfo";
import UserSelect from "@/components/yqvalley/UserSelect";
import { mapState, mapMutations } from "vuex";
export default {
  name: "userManage",
  data() {
    return {
      moreCondition:false,
      //弹框
      dialogUserValiddateShow: false,
      dialogUserDeptShow: false,
      dialogReportUserShow: false,
      dialogUserPwdShow: false,
      //当前页
      currentPage: 1,
      pageSize: 10,
      totals: 0,
      //Tab导航控制
      activeName: "first",
      judgeLabel: false,
      //编辑页面传参
      configName: "",
      //全选单选容器
      container: [],
      //用户控件
      valleyAccount: "",
      reportAccount: "",
      //部门选择数据
      deptList: [],
      //所属部门修改数据
      deptData: {
        valleyAccountList: [],
        valleyAccountNames: "",
        deptCode: "",
      },
      //有效期修改数据
      validDateData: {
        valleyAccountList: [],
        valleyAccountNames: "",
        validEndDate: null,
      },
      //汇报用户修改数据
      updReportAccount: "",
      reportUserData: {
        valleyAccountList: [],
        valleyAccountNames: "",
        reportAccount: "",
      },
      userDefaultPwd: {
        valleyAccount: "",
        defaultLoginPwd: "",
      },
      //列表查询条件
      queryData: {
        valleyAccount: "",
        phoneNumber: "",
        mailAddress: "",
        reportAccount: "",
        accountStatus: "",
        deptCode: "",
      },
      //列表数据
      tableData: [],
    };
  },
  components: {
    EditUserInfo,
    UserSelect,
  },
  computed: {
    ...mapState(["tabListHeight", "yqvSelect"]),
    myHeaders() {
      return {
        Authorization: this.$yqvutil.getToken(),
      };
    },
  },
  mounted() {
    this.getUserList();
    this.getDeptList();
  },
  watch: {},
  methods: {
    ...mapMutations(["setPerson"]),
    //清空查询条件并回到第一页
    emptyQuery() {
      this.currentPage = 1;
      this.pageSize = 10;
      this.totals = 0;
      this.valleyAccount = "";
      this.reportAccount = "";
      this.queryData.valleyAccount = "";
      this.queryData.phoneNumber = "";
      this.queryData.mailAddress = "";
      this.queryData.reportAccount = "";
      this.queryData.accountStatus = "";
      this.queryData.deptCode = "";
      this.getUserList();
    },
    /********************************查询列表*****************************/
    search() {
      this.currentPage = 1;
      this.pageSize = 10;
      this.totals = 0;
      this.getUserList();
    },
    //获取所有部门
    getDeptList() {
      let vm = this;
      let data = { deptStatus: "Y" };
      this.$http
        .post(vm.$yqvutil.getContextPath()+`yqvservice/user/dept/list`, data)
        .then(function (response) {
          if (response.status == 200) {
            vm.deptList = response.data;
          }
        })
        .catch(function (error) {});
    },
    /********************************EXCEL导入*****************************/
    handleAvatarSuccess(res, file) {
      this.$refs.upload.clearFiles();
      let vm = this;
      this.$http
        .get(vm.$yqvutil.getContextPath()+`yqvservice/user/import?fileId=${res[0].fileId}`)
        .then(function (response) {
          vm.$confirm("导入成功，是否前往【我的导入】页面查看详情?", "提示", {
            confirmButtonText: "是",
            cancelButtonText: "否",
            type: "success",
            closeOnClickModal: false,
          })
            .then(() => {
              vm.$router.push({
                name: "YqvUserImportList",
              });
            })
            .catch(() => {});
        })
        .catch(function (error) {});
    },
    handleAvatarError(err, file, fileList) {
      this.$message.error(JSON.parse(err.message).message);
    },
    /********************************EXCEL导入模板下载*****************************/
    downloadExcelTemplate(e) {
      let src = this.$yqvutil.getContextPath()+"yqvservice/user/template";
      this.$yqvutil.downloads(src);
    },
    /********************************EXCEL导出*****************************/
    exportExcel(e) {
      let data = this.queryData;
      let vm = this;
      data.valleyAccount = this.valleyAccount;
      data.reportAccount = this.reportAccount;
      this.$http
        .post(vm.$yqvutil.getContextPath()+`yqvservice/user/export`, data)
        .then(function (response) {
          if (response.status == 200) {
            vm.$confirm("导出成功，是否前往【我的导出】页面查看详情?", "提示", {
              confirmButtonText: "是",
              cancelButtonText: "否",
              type: "success",
              closeOnClickModal: false,
            })
              .then(() => {
                vm.$router.push({
                  name: "YqvUserExportList",
                });
              })
              .catch(() => {});
          }
        })
        .catch(function (error) {});
    },
    /********************************重置默认密码*****************************/
    resetDefaultPwd(val) {
      this.$confirm("此操作将重置用户密码, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        closeOnClickModal: false,
      })
        .then(() => {
          this.userDefaultPwd.valleyAccount = "";
          this.userDefaultPwd.defaultLoginPwd = "";
          let data = {
            valleyAccount: val.valleyAccount,
            userName: val.userName
          };
          let vm = this;
          this.$http
            .post(vm.$yqvutil.getContextPath()+`yqvservice/user/pwd/reset`, data)
            .then(function (response) {
              if (response.status == 200) {
                vm.dialogUserPwdShow = true;
                vm.userDefaultPwd.valleyAccount =
                  data.userName + "(" + data.valleyAccount + ")";
                vm.userDefaultPwd.defaultLoginPwd = response.data.data;
              }
            })
            .catch(function (error) {});
        })
        .catch(() => {});
    },
    /********************************刷新指定用户权限*****************************/
    resetUserCache(val) {
      this.$confirm("此操作会刷新指定用户权限，权限变更无需退出立即生效, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        closeOnClickModal: false,
      })
        .then(() => {
          let vm = this;
          this.$http
            .get(vm.$yqvutil.getContextPath()+`yqvservice/user/usercache/update?valleyAccount=`+val.valleyAccount)
            .then(function (response) {
              vm.$message({
                type: "success",
                message: "更新任务已下发成功",
              });
            })
            .catch(function (error) {});
        })
        .catch(() => {});
    },
    /********************************查询用户列表*****************************/
    getUserList() {
      let data = this.queryData;
      let vm = this;
      data.valleyAccount = this.valleyAccount;
      data.reportAccount = this.reportAccount;
      this.$http
        .post(
          vm.$yqvutil.getContextPath()+`yqvservice/user/page?currentPage=${this.currentPage}&pageSize=${this.pageSize}`,
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
      if (labelName == "用户列表") {
        this.activeName = "first";
        this.judgeLabel = false;
      }
    },
    /********************************修改用户信息*****************************/
    editUserInfo(val) {
      //打开配置页开关
      this.judgeLabel = true;
      //重置数据
      this.container = [];
      //光标对位
      this.activeName = "second";
      this.configName = val.valleyAccount;
    },
    /********************************新增用户*****************************/
    addUserInfo() {
      //打开配置页开关
      this.judgeLabel = true;
      //重置数据
      this.container = [];
      //光标对位
      this.activeName = "second";
      this.configName = null;
    },
    /********************************全选*****************************/
    checkAll(val) {
      //进行赋值,判断是否选中内容
      this.container = val;
    },
    /********************************用户禁用*****************************/
    updateUserStatus(type) {
      if (this.container.length != 0) {
        let accountStatus = "";
        let strName;
        if (type == "enable") {
          accountStatus = "Y";
          strName = "此操作会启用操作账号, 是否继续?";
        } else {
          accountStatus = "N";
          strName = "此操作会禁用操作账号, 是否继续?";
        }
        this.$confirm(strName, "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
          closeOnClickModal: false,
        })
          .then(() => {
            let vm = this;
            let valleyAccountList = [];
            for (let i = 0; i < this.container.length; i++) {
              valleyAccountList.push(this.container[i].valleyAccount);
            }
            let data = {
              accountStatus: accountStatus,
              valleyAccountList: valleyAccountList,
            };
            this.$http
              .post(vm.$yqvutil.getContextPath()+`yqvservice/user/status/update`, data)
              .then(function (response) {
                if (response.status == 200) {
                  vm.$message({
                    showClose: true,
                    message: "操作成功",
                    type: "success",
                  });
                  vm.getUserList();
                }
              })
              .catch(function (error) {});
          })
          .catch(() => {});
      } else {
        this.$message({
          showClose: true,
          message: "请选择用户",
          type: "warning",
        });
      }
    },
    /********************************修改用户有效期*****************************/
    showUserValiddateDialog() {
      if (this.container.length != 0) {
        this.validDateData.validEndDate = new Date();
        this.validDateData.validEndDate.setFullYear(
          this.validDateData.validEndDate.getFullYear() + 1
        );
        this.validDateData.validEndDate.setDate(
          this.validDateData.validEndDate.getDate() - 1
        );
        this.dialogUserValiddateShow = true;
        let vm = this;
        let valleyAccountList = [];
        let valleyNames = "";
        for (let i = 0; i < this.container.length; i++) {
          valleyAccountList.push(this.container[i].valleyAccount);
          if (valleyNames.length <= 0) {
            valleyNames +=
              this.container[i].userName +
              "(" +
              this.container[i].valleyAccount +
              ")";
          } else {
            valleyNames +=
              "," +
              this.container[i].userName +
              "(" +
              this.container[i].valleyAccount +
              ")";
          }
        }
        this.validDateData.valleyAccountList = valleyAccountList;
        this.validDateData.valleyAccountNames = valleyNames;
      } else {
        this.$message({
          showClose: true,
          message: "请选择用户",
          type: "warning",
        });
      }
    },
    closeUserValiddateDialog() {
      this.dialogUserValiddateShow = false;
    },
    updateUserValiddate() {
      let data = {
        validEndDate: this.validDateData.validEndDate,
        valleyAccountList: this.validDateData.valleyAccountList,
      };
      let vm = this;
      this.$http
        .post(vm.$yqvutil.getContextPath()+`yqvservice/user/validdate/update`, data)
        .then(function (response) {
          if (response.status == 200) {
            vm.validDateData.valleyAccountList = [];
            vm.validDateData.valleyAccountNames = "";
            vm.validDateData.validEndDate = null;
            vm.dialogUserValiddateShow = false;
            vm.$message({
              showClose: true,
              message: "操作成功",
              type: "success",
            });
            vm.getUserList();
          }
        })
        .catch(function (error) {});
    },
    /********************************修改用户所属部门*****************************/
    showUserDeptDialog() {
      if (this.container.length != 0) {
        this.dialogUserDeptShow = true;
        let vm = this;
        let valleyAccountList = [];
        let valleyNames = "";
        for (let i = 0; i < this.container.length; i++) {
          valleyAccountList.push(this.container[i].valleyAccount);
          if (valleyNames.length <= 0) {
            valleyNames +=
              this.container[i].userName +
              "(" +
              this.container[i].valleyAccount +
              ")";
          } else {
            valleyNames +=
              "," +
              this.container[i].userName +
              "(" +
              this.container[i].valleyAccount +
              ")";
          }
        }
        this.deptData.valleyAccountList = valleyAccountList;
        this.deptData.valleyAccountNames = valleyNames;
      } else {
        this.$message({
          showClose: true,
          message: "请选择用户",
          type: "warning",
        });
      }
    },
    closeUserDeptDialog() {
      this.dialogUserdeptDataShow = false;
    },
    updateUserDept() {
      let data = {
        deptCode: this.deptData.deptCode,
        valleyAccountList: this.deptData.valleyAccountList,
      };
      let vm = this;
      this.$http
        .post(vm.$yqvutil.getContextPath()+`yqvservice/user/dept/update`, data)
        .then(function (response) {
          if (response.status == 200) {
            vm.deptData.valleyAccountList = [];
            vm.deptData.valleyAccountNames = "";
            vm.deptData.deptCode = "";
            vm.dialogUserDeptShow = false;
            vm.$message({
              showClose: true,
              message: "操作成功",
              type: "success",
            });
            vm.getUserList();
          }
        })
        .catch(function (error) {});
    },
    /********************************修改汇报用户*****************************/
    showReportUserDialog() {
      if (this.container.length != 0) {
        this.dialogReportUserShow = true;
        let vm = this;
        let valleyAccountList = [];
        let valleyNames = "";
        for (let i = 0; i < this.container.length; i++) {
          valleyAccountList.push(this.container[i].valleyAccount);
          if (valleyNames.length <= 0) {
            valleyNames +=
              this.container[i].userName +
              "(" +
              this.container[i].valleyAccount +
              ")";
          } else {
            valleyNames +=
              "," +
              this.container[i].userName +
              "(" +
              this.container[i].valleyAccount +
              ")";
          }
        }
        this.reportUserData.valleyAccountList = valleyAccountList;
        this.reportUserData.valleyAccountNames = valleyNames;
      } else {
        this.$message({
          showClose: true,
          message: "请选择用户",
          type: "warning",
        });
      }
    },
    closeReportUserDialog() {
      this.dialogReportUserShow = false;
    },
    updateReportUser() {
      this.reportUserData.reportAccount = this.updReportAccount;
      let data = {
        reportAccount: this.reportUserData.reportAccount,
        valleyAccountList: this.reportUserData.valleyAccountList,
      };
      let vm = this;
      this.$http
        .post(vm.$yqvutil.getContextPath()+`yqvservice/user/report/update`, data)
        .then(function (response) {
          if (response.status == 200) {
            vm.reportUserData.valleyAccountList = [];
            vm.reportUserData.valleyAccountNames = "";
            vm.reportUserData.reportAccount = "";
            vm.updReportAccount = "";
            vm.dialogReportUserShow = false;
            vm.$message({
              showClose: true,
              message: "操作成功",
              type: "success",
            });
            vm.getUserList();
          }
        })
        .catch(function (error) {});
    },
    /********************************查看用户默认密码*****************************/
    showUserPwdDialog(val) {
      this.userDefaultPwd.valleyAccount = "";
      this.userDefaultPwd.defaultLoginPwd = "";
      let data = {
        valleyAccount: val.valleyAccount,
        userName: val.userName,
      };
      let vm = this;
      this.$http
        .post(vm.$yqvutil.getContextPath()+`yqvservice/user/default/pwd`, data)
        .then(function (response) {
          if (response.status == 200) {
            vm.dialogUserPwdShow = true;
            vm.userDefaultPwd.valleyAccount =
              data.userName + "(" + data.valleyAccount + ")";
            vm.userDefaultPwd.defaultLoginPwd = response.data.data;
          }
        })
        .catch(function (error) {});
    },
    closeUserPwdDialog() {
      this.dialogUserPwdShow = false;
      this.userDefaultPwd.valleyAccount = "";
      this.userDefaultPwd.defaultLoginPwd = "";
    },
    /********************************刷新在线用户权限*****************************/
    updateUserOnlineCache() {
      this.$confirm("此操作会刷新在线用户权限，权限变更无需退出立即生效, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        closeOnClickModal: false,
      })
        .then(() => {
          let vm = this;
          this.$http
            .get(vm.$yqvutil.getContextPath()+`yqvservice/user/onlinecache/update`)
            .then(function (response) {
              if (response.status == 200) {
                vm.$message({
                  showClose: true,
                  message: "更新任务已下发成功",
                  type: "success",
                });
              }
            })
            .catch(function (error) {});
        })
        .catch(() => {});
    },
    /********************************渲染用户状态*****************************/
    setState(val) {
      if (val == "Y") {
        return "启用";
      } else {
        return "禁用";
      }
    },
    /********************************分页操作*****************************/
    handleSizeChange(val) {
      this.pageSize = val;
      this.getUserList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getUserList();
    },
  },
};
</script>

<style scoped lang="less">
.userManage {
  .more-down {
    width: 60px;
    float: left;
    color: #5675f5;
    height: 32px;
    line-height: 32px;
    text-align: center;
    cursor: pointer;
  }
  .upload-demo ::v-deep(.el-upload-list) {
    display: none !important;
  }
  .upload-demo ::v-deep(.el-button) {
    width: 72px;
    height: 28px;
    line-height: 8px;
    background: #597ef7;
    padding: 6px 15px;
    font-size: 14px;
    border-radius: 2px;
    border: none;
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
.el-textarea {
  margin: 15px 0;
}
.el-textarea ::v-deep(.el-textarea__inner) {
  min-height: 120px !important;
}
</style>

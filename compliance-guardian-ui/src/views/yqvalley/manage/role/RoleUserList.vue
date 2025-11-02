<template>
  <div class="roleUserList">
    <!-- 用户选择 -->
    <el-dialog
      title="选择用户"
      v-model="dialogAddUserRoleShow"
      :close-on-click-modal="false"
      @close="closeUserRoleDialog"
      width="580px"
    >
      <!-- 查询条件 -->
      <div class="search-box-one" style="width:400px;overflow:visible;margin-top: -20px;">
        <div style="width:99%;float:left;margin-top:20px;">
          <div style="width:100%;float:left;">
            <div class="search-box-top-name" style="width: 75px;margin-left: 0px;">用户信息：</div>
            <div style="width:calc(100% - 75px - 2.3%);float:right;padding-right:2.3%">
              <el-input v-model="userData.matchSearch" placeholder="请输入用户信息" clearable style="width:100%;"></el-input>
            </div>
          </div>
        </div>
      </div>
      <div class="search-box-right" style="overflow:visible;margin-top: -15px;">
        <div class="query" @click="userSearch" style="margin-right:10px;margin-top:-15px;">
          <font-awesome-icon icon="fas fa-search" />查询
        </div>
      </div>
      <div class="clear"></div>
      <div>
        <el-table :data="userListData" border @selection-change="checkUserAll" style="width: 100%" :row-class-name="tableRowClassName" :height="tabListHeight-200" tooltip-effect="light" ref="table">
          <!-- 选择项目 -->
          <el-table-column type="selection" width="55" align="center">
          </el-table-column>
          <!-- 数据字段 -->
          <el-table-column fixed="left" label="用户账号" align="left" width="100" :show-overflow-tooltip="true">
            <template #default="scope">
              <span v-text="scope.row.valleyAccount"></span>
            </template>
          </el-table-column>
          <el-table-column fixed="left" label="用户姓名" align="left" width="100" :show-overflow-tooltip="true">
            <template #default="scope">
              <span v-text="scope.row.userName"></span>
            </template>
          </el-table-column>
          <el-table-column label="手机号码" align="left" width="100" :show-overflow-tooltip="true">
            <template #default="scope">
              <span v-text="scope.row.phoneNumber"></span>
            </template>
          </el-table-column>
          <el-table-column label="联系邮箱" align="left" width="180" :show-overflow-tooltip="true">
            <template #default="scope">
              <span v-text="scope.row.mailAddress"></span>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <!-- 分页 -->
      <div class="paging-box">
        <div class="block">
          <el-pagination
            background
            @size-change="userHandleSizeChange"
            @current-change="userHandleCurrentChange"
            :current-page="userCurrentPage"
            :page-sizes="[5, 10, 20, 50]"
            :page-size="userPageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="userTotals"
          >
          </el-pagination>
        </div>
      </div>
      <div style="clear:both"></div>
      <template #footer>
        <div class="dialog-footer">
            <el-button @click="dialogAddUserRoleShow = false">取 消</el-button>
            <el-button type="primary" @click="addUserRole">确 定</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 操作按钮 -->
    <div class="btn-box">
      <div class="new" style="width:90px;" @click="addUserRoleDialog">
        <font-awesome-icon icon="fas fa-plus-circle" />绑定用户
      </div>
      <div class="new" style="width:90px;background: #f56c6c;" @click="removeUserRole">
        <font-awesome-icon icon="fas fa-trash-alt" />解绑用户
      </div>
    </div>
    <!--表格-->
    <!--用户列表-->
      <div v-if="tableData.length">
        <el-table :data="tableData" border @selection-change="checkRoleUserAll" style="width: 100%" :row-class-name="tableRowClassName" :height="tabListHeight-10" tooltip-effect="light" ref="table">
          <!-- 选择项目 -->
          <el-table-column type="selection" width="55" align="center">
          </el-table-column>
          <!-- 数据字段 -->
          <el-table-column fixed="left" label="用户账号" align="left" width="100" :show-overflow-tooltip="true">
            <template #default="scope">
              <span v-text="scope.row.valleyAccount"></span>
            </template>
          </el-table-column>
          <el-table-column fixed="left" label="用户姓名" align="left" width="100" :show-overflow-tooltip="true">
            <template #default="scope">
              <span v-text="scope.row.userName"></span>
            </template>
          </el-table-column>
          <el-table-column label="手机号码" align="left" width="100" :show-overflow-tooltip="true">
            <template #default="scope">
              <span v-text="scope.row.phoneNumber"></span>
            </template>
          </el-table-column>
          <el-table-column label="联系邮箱" align="left" width="180" :show-overflow-tooltip="true">
            <template #default="scope">
              <span v-text="scope.row.mailAddress"></span>
            </template>
          </el-table-column>
          <el-table-column label="用户角色" align="left" :show-overflow-tooltip="true">
            <template #default="scope">
              <span v-text="scope.row.roleNames"></span>
            </template>
          </el-table-column>
          <el-table-column label="用户状态" align="left" width="80">
            <template #default="scope">
              <span :class="[scope.row.accountStatus == 'Y' ? 'activeColorY' : 'activeColorN']" v-text="setState(scope.row.accountStatus)"></span>
            </template>
          </el-table-column>
          <el-table-column label="修改用户" align="left" width="110" :show-overflow-tooltip="true">
            <template #default="scope">
              <span v-text="scope.row.lastUpdateName"></span>
            </template>
          </el-table-column>
          <el-table-column label="修改时间" align="left" width="140">
            <template #default="scope">
              <span v-text="$yqvutil.setFormTime(scope.row.lastUpdateDate, 1)"></span>
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
</template>

<script>
import { mapState, mapMutations } from "vuex";
export default {
  name: "roleUserList",
  data() {
    return {
      //当前页
      currentPage: 1,
      pageSize: 10,
      totals: 0,
      //当前页
      userCurrentPage: 1,
      userPageSize: 5,
      userTotals: 0,
      //全选单选容器
      roleUserContainer: [],
      userContainer: [],
      //列表数据
      tableData: [],
      userListData: [],
      userData:{
        matchSearch:""
      },
      //用户选择弹框
      dialogAddUserRoleShow: false,
    };
  },
  props: ["roleCode"],
  watch: {},
  mounted() {
    this.roleUserContainer=[];
    this.getRoleUserList();
  },
  methods: {
    /********************************全选*****************************/
    checkRoleUserAll(val) {
      //进行赋值,判断是否选中内容
      this.roleUserContainer = val;
    },
    /********************************全选*****************************/
    checkUserAll(val) {
      //进行赋值,判断是否选中内容
      this.userContainer = val;
    },
    /********************************查询权限信息*****************************/
    getRoleUserList() {
      let vm = this;
      let data = {"roleCode":vm.roleCode};
      this.$http.post(
          vm.$yqvutil.getContextPath()+`yqvservice/role/user/page?currentPage=${
            this.currentPage
          }&pageSize=${this.pageSize}`,
          data
        )
        .then(function(response) {
          if (response.status == 200) {
            vm.tableData = response.data.result;
            vm.totals = response.data.page.totalRows;
          }
        })
        .catch(function(error) {});
    },
    /********************************查询列表*****************************/
    userSearch() {
      this.getUserList();
    },
    /********************************查询权限信息*****************************/
    getUserList() {
      let vm = this;
      let data = {"roleCode":vm.roleCode,"matchSearch":vm.userData.matchSearch};
      this.$http.post(
          vm.$yqvutil.getContextPath()+`yqvservice/role/userexclude/page?currentPage=${
            this.userCurrentPage
          }&pageSize=${this.userPageSize}`,
          data
        )
        .then(function(response) {
          if (response.status == 200) {
            vm.userListData = response.data.result;
            vm.userTotals = response.data.page.totalRows;
          }
        })
        .catch(function(error) {});
    },
    /********************************关闭添加用户弹框*****************************/
    closeUserRoleDialog() {
      this.dialogAddUserRoleShow = false;
    },
    /********************************打开添加用户弹框*****************************/
    addUserRoleDialog() {
      this.userContainer = [];
      this.userListData=[];
      this.dialogAddUserRoleShow = true;
    },
    /********************************添加用户*****************************/
    addUserRole() {
      if (this.userContainer.length != 0) {
        let data = {
          roleCode: this.roleCode,
          userList:[]
        };
        for (let i = 0; i < this.userContainer.length; i++) {
          data.userList.push({"valleyAccount":this.userContainer[i].valleyAccount});
        }
        let vm = this;
        this.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/role/userrole/add`, data)
          .then(function(response) {
            vm.$message({
              type: "success",
              message: "绑定成功!"
            });
            vm.dialogAddUserRoleShow = false;
            vm.getRoleUserList();
          })
          .catch(function(error) {});
      }else {
        this.$message({
          showClose: true,
          message: "请选择用户",
          type: "warning"
        });
      }
    },
    /********************************移除用户*****************************/
    removeUserRole() {
      if (this.roleUserContainer.length != 0) {
        this.$confirm("此操作将解绑当前角色关联用户, 是否继续?", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
          closeOnClickModal: false
        })
          .then(() => {
            let data = {
              roleCode: this.roleCode,
              userList:[]
            };
            for (let i = 0; i < this.roleUserContainer.length; i++) {
              data.userList.push({"valleyAccount":this.roleUserContainer[i].valleyAccount});
            }
            let vm = this;
            this.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/role/userrole/delete`, data)
              .then(function(response) {
                vm.$message({
                  type: "success",
                  message: "解绑成功!"
                });
                vm.getRoleUserList();
              })
              .catch(function(error) {});
          })
          .catch(() => {});
        } else {
        this.$message({
          showClose: true,
          message: "请选择用户",
          type: "warning"
        });
      }
    },
    /********************************渲染用户状态*****************************/
    setState(val) {
      if (val == 'Y') {
        return "启用";
      }else{
        return "禁用";
      }
    },
    tableRowClassName({ row, rowIndex }) {
      if (row.accountStatus == 'N') {
        return "warning-row";
      }
      return "";
    },
    /********************************分页操作*****************************/
    handleSizeChange(val) {
      this.pageSize = val;
      this.getRoleUserList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getRoleUserList();
    },
    /********************************分页操作*****************************/
    userHandleSizeChange(val) {
      this.userPageSize = val;
      this.getUserList();
    },
    userHandleCurrentChange(val) {
      this.userCurrentPage = val;
      this.getUserList();
    }
  },
  components: {},
  computed: {
    ...mapState(["tabListHeight", "yqvSelect"]),
  }
};
</script>

<style scoped lang="less">

</style>

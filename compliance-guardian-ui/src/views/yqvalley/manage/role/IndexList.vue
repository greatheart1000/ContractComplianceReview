<template>
  <div class="roleManage">
    <div class="tabBox">
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane label="角色列表" name="first"> </el-tab-pane>
        <el-tab-pane v-if="judgeLabel" label="角色设置" name="second"></el-tab-pane>
        <el-tab-pane v-if="userLabel" :label="roleUserLabel" name="user"></el-tab-pane>
      </el-tabs>
      <div class="tabBottom"></div>
    </div>
    <div v-if="activeName == 'first'">
      <!-- 查询条件 -->
      <div class="search-box-one" style="width:78%;overflow:visible;margin-top: -20px;">
        <div style="width:99%;float:left;margin-top:20px;">
          <div style="width:100%;float:left;">
            <div class="search-box-top-name" style="width: 75px;">角色名称：</div>
            <div style="width:calc(100% - 100px - 2.3%);float:right;padding-right:2.3%">
              <el-input v-model="roleName" placeholder="请输入角色名称" clearable style="width:100%;"></el-input>
            </div>
          </div>
        </div>
      </div>
      <div class="search-box-right" style="overflow:visible;">
        <div class="query" @click="empty" style="margin-right:20px;margin-top:-15px;">
          <font-awesome-icon icon="fas fa-refresh" />重置
        </div>
        <div class="query" @click="search" style="margin-right:10px;margin-top:-15px;">
          <font-awesome-icon icon="fas fa-search" />查询
        </div>
      </div>
      <div class="clear"></div>
      <!-- 操作按钮 -->
      <div class="btn-box" style="margin-top: 10px;">
        <div class="new" style="width:60px;" @click="addRoleInfoDialog">
          <font-awesome-icon icon="fas fa-plus-circle" />新建
        </div>
      </div>
      <!--表格-->
      <div v-if="tableData.length">
        <el-table :data="tableData" border ref="table" style="width: 100%" tooltip-effect="light" :height="tabListHeight-60">
          <el-table-column type="index" width="50" label="NO." align="center" fixed="left" >
          </el-table-column>
          <el-table-column fixed="left" label="操作" width="100" align="left">
            <template #default="scope">
              <div type="text" size="small" @click="editRoleInfoDialog(scope.row)">
                <font-awesome-icon icon="far fa-edit" style="float: left;color:#33db67;font-size:20px;width: 26px;cursor: pointer;" />
              </div>
              <div type="text" size="small" @click="roleUserDialog(scope.row)">
                <font-awesome-icon icon="fas fa-users" style="float: left;color:#597ef7;font-size:18px;width: 26px;cursor: pointer;" />
              </div>
              <div type="text" size="small" @click="deleteRoleInfo(scope.row)">
                <font-awesome-icon icon="far fa-trash-alt" style="float: left;color:#f56c6c;font-size:20px;width: 26px;cursor: pointer;" />
              </div>
            </template>
          </el-table-column>
          <el-table-column label="角色编号" align="left" width="120" :show-overflow-tooltip="true">
            <template #default="scope">
              <span v-text="scope.row.roleCode"></span>
            </template>
          </el-table-column>
          <el-table-column label="角色名称" align="left" width="160" :show-overflow-tooltip="true">
            <template #default="scope">
              <span v-text="scope.row.roleName"></span>
            </template>
          </el-table-column>
          <el-table-column label="角色描述" align="left" minWidth="200" :show-overflow-tooltip="true">
            <template #default="scope">
              <span v-text="scope.row.roleDesc"></span>
            </template>
          </el-table-column>
          <el-table-column label="排序" align="center" width="60" :show-overflow-tooltip="true">
            <template #default="scope">
              <span v-text="scope.row.roleSort"></span>
            </template>
          </el-table-column>
          <el-table-column label="修改用户" align="left" width="110" :show-overflow-tooltip="true">
            <template #default="scope">
              <div v-text="scope.row.lastUpdateName"></div>
            </template>
          </el-table-column>
          <el-table-column label="修改时间" align="left" width="140" :show-overflow-tooltip="true">
            <template #default="scope">
              <div v-text="$yqvutil.setFormTime(scope.row.lastUpdateDate, 1)"></div>
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
      <EditRoleInfo :roleCode="roleCode"/>
    </div>
    <div v-if="activeName == 'user'" class="authorityBox">
      <RoleUserList :roleCode="roleCode"/>
    </div>
  </div>
</template>
<script>
import EditRoleInfo from "./EditRoleInfo";
import RoleUserList from "./RoleUserList";
import { mapState, mapMutations } from "vuex";
export default {
  name: "authorityGroup",
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
      userLabel: false,
      //角色列表数据
      tableData: [],
      //编辑页面传参
      roleCode: "",
      //查询条件
      roleName: "",
      //用户角色
      roleUserLabel: "角色用户"
    };
  },
  mounted() {
    this.getRoleList();
  },
  computed: {
    ...mapState(["tabListHeight"])
  },
  components: {
    EditRoleInfo,
    RoleUserList
  },
  methods: {
    /********************************查询列表*****************************/
    search() {
      this.getRoleList();
    },
    /********************************清空查询条件*****************************/
    empty() {
      this.authorityGroupName = "";
      this.getRoleList();
    },
    /********************************删除角色*****************************/
    deleteRoleInfo(val) {
      this.$confirm("此操作将删除角色,删除后数据不可恢复, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        closeOnClickModal: false
      })
        .then(() => {
          let data = {
            roleCode: val.roleCode
          };
          let vm = this;
          this.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/role/delete`, data)
            .then(function(response) {
              vm.$message({
                type: "success",
                message: "删除成功!"
              });
              vm.getRoleList();
            })
            .catch(function(error) {});
        })
        .catch(() => {});
    },
    /********************************查询角色列表*****************************/
    getRoleList() {
      let vm = this;
      let data = {roleName:vm.roleName};
      this.$http.post(
          vm.$yqvutil.getContextPath()+`yqvservice/role/page?currentPage=${vm.currentPage}&pageSize=${
            vm.pageSize
          }`,
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
    /********************************标签操作*****************************/
    handleClick(row) {
      let labelName = null;
      if(row.label){
        labelName = row.label;
      }
      if(!labelName && row.props){
        labelName = row.props.label;
      }
      if (labelName == "角色列表") {
        this.activeName = "first";
        this.judgeLabel = false;
        this.userLabel = false;
      }
    },
    /********************************新增角色*****************************/
    addRoleInfoDialog() {
      //打开配置页开关
      this.judgeLabel = true;
      //光标对位
      this.activeName = "second";
      this.roleCode = null;
    },
    /********************************查询角色列表*****************************/
    editRoleInfoDialog(val) {
      //打开配置页开关
      this.judgeLabel = true;
      //光标对位
      this.activeName = "second";
      this.roleCode = val.roleCode;
    },
    /********************************查询角色列表*****************************/
    roleUserDialog(val) {
      //打开配置页开关
      this.userLabel = true;
      //光标对位
      this.activeName = "user";
      this.roleUserLabel = val.roleName+"("+val.roleCode+")";
      this.roleCode = val.roleCode;
    },
    /********************************分页*****************************/
    handleSizeChange(val) {
      this.pageSize = val;
      this.getRoleList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getRoleList();
    }
  }
};
</script>

<style scoped lang="less">
.roleManage {
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

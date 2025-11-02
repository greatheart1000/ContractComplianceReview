<template>
  <div class="AuthorityGroup">
    <div class="tabBox">
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane label="权限列表" name="first"> </el-tab-pane>
        <el-tab-pane v-if="judgeLabel" label="权限设置" name="second"></el-tab-pane>
      </el-tabs>
      <div class="tabBottom"></div>
    </div>
    <div v-if="activeName == 'first'">
      <!-- 查询条件 -->
      <div class="search-box-one" style="width:78%;overflow:visible;margin-top: -20px;">
        <div style="width:99%;float:left;margin-top:20px;">
          <div style="width:100%;float:left;">
            <div class="search-box-top-name" style="width: 75px;">权限名称：</div>
            <div style="width:calc(100% - 100px - 2.3%);float:right;padding-right:2.3%">
              <el-input v-model="authorityGroupName" placeholder="请输入权限名称" clearable style="width:100%;"></el-input>
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
        <div class="new" style="width:60px;" @click="addAuthorityGroupDialog">
          <font-awesome-icon icon="fas fa-plus-circle" />新建
        </div>
      </div>
      <!--表格-->
      <div v-if="tableData.length">
        <el-table :data="tableData" border ref="table" style="width: 100%" tooltip-effect="light" :height="tabListHeight-60">
          <el-table-column type="selection" width="55" align="center">
          </el-table-column>
          <el-table-column fixed="left" label="操作" width="75" align="left">
            <template #default="scope">
              <div type="text" size="small" @click="editAuthorityGroupDialog(scope.row)">
                <font-awesome-icon icon="far fa-edit" style="float: left;color:#33db67;font-size:18px;width: 26px;cursor: pointer;" />
              </div>
              <div type="text" size="small" @click="deleteAuthorityGroup(scope.row)">
                <font-awesome-icon icon="far fa-trash-alt" style="float: left;color:#f56c6c;font-size:18px;width: 26px;cursor: pointer;" />
              </div>
            </template>
          </el-table-column>
          <el-table-column label="权限编号" align="left" width="140" :show-overflow-tooltip="true">
            <template #default="scope">
              <span v-text="scope.row.authorityGroupCode"></span>
            </template>
          </el-table-column>
          <el-table-column label="权限名称" align="left" width="300" :show-overflow-tooltip="true">
            <template #default="scope">
              <span v-text="scope.row.authorityGroupName"></span>
            </template>
          </el-table-column>
          <el-table-column label="权限描述" align="left" minWidth="240" :show-overflow-tooltip="true">
            <template #default="scope">
              <span v-text="scope.row.authorityGroupDesc"></span>
            </template>
          </el-table-column>
          <el-table-column label="排序" align="left" width="60" :show-overflow-tooltip="true">
            <template #default="scope">
              <span v-text="scope.row.authorityGroupSort"></span>
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
      <EditAuthorityGroup :groupCode="groupCode"/>
    </div>
  </div>
</template>
<script>
import EditAuthorityGroup from "./EditAuthorityGroup";
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
      //权限列表数据
      tableData: [],
      //编辑页面传参
      groupCode: "",
      //查询条件
      authorityGroupName: ""
    };
  },
  mounted() {
    this.getAuthorityGroupList();
  },
  computed: {
    ...mapState(["tabListHeight"])
  },
  components: {
    EditAuthorityGroup
  },
  methods: {
    /********************************查询列表*****************************/
    search() {
      this.getAuthorityGroupList();
    },
    /********************************清空查询条件*****************************/
    empty() {
      this.authorityGroupName = "";
      this.getAuthorityGroupList();
    },
    /********************************删除角色*****************************/
    deleteAuthorityGroup(val) {
      this.$confirm("此操作将删除权限分组，删除后数据不可恢复, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        closeOnClickModal: false
      })
        .then(() => {
          let data = {
            authorityGroupCode: val.authorityGroupCode
          };
          let vm = this;
          this.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/authority/group/delete`, data)
            .then(function(response) {
              vm.$message({
                type: "success",
                message: "删除成功!"
              });
              vm.getAuthorityGroupList();
            })
            .catch(function(error) {});
        })
        .catch(() => {});
    },
    /********************************查询权限列表*****************************/
    getAuthorityGroupList() {
      let vm = this;
      let data = {authorityGroupName:vm.authorityGroupName};
      this.$http.post(
          vm.$yqvutil.getContextPath()+`yqvservice/authority/group/page?currentPage=${vm.currentPage}&pageSize=${
            vm.pageSize
          }`,
          data
        )
        .then(function(response) {
          if (response.status == 200) {
            vm.tableData = response.data.result;
            vm.totals = response.data.page.totalRows;
            vm.roleList = Object.assign({}, vm.defaultRoleList);
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
      if (labelName == "权限列表") {
        this.activeName = "first";
        this.judgeLabel = false;
      }
    },
    /********************************新增权限分组*****************************/
    addAuthorityGroupDialog() {
      //打开配置页开关
      this.judgeLabel = true;
      //光标对位
      this.activeName = "second";
      this.groupCode = null;
    },
    /********************************查询权限列表*****************************/
    editAuthorityGroupDialog(val) {
      //打开配置页开关
      this.judgeLabel = true;
      //光标对位
      this.activeName = "second";
      this.groupCode = val.authorityGroupCode;
    },
    /********************************分页*****************************/
    handleSizeChange(val) {
      this.pageSize = val;
      this.getAuthorityGroupList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getAuthorityGroupList();
    }
  }
};
</script>

<style scoped lang="less">
.AuthorityGroup {
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

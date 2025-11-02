<template>
  <div class="requestConfigList">
    <!-- 编辑应用配置 -->
    <el-dialog
      title="请求配置"
      v-model="dialogRequestConfigShow"
      :close-on-click-modal="false"
      @close="closeRequestConfigDialog"
      width="480px"
    >
      <div style="width: 460px;height:200px;margin:0 auto">
        <div class="editFormList">
          <div class="abox" style="width: 100%;margin-top: 8px;">
            <span class="abox-lableName"><span>*</span>配置类型：</span>
            <div class="input-box">
              <el-select
                  v-model="editData.configType"
                  placeholder="请选择配置类型"
                  style="width:100%"
                  clearable
                  collapse-tags
                >
                <el-option
                  v-for="item in configTypeList"
                  :key="item.itemCode"
                  :label="item.itemName"
                  :value="item.itemCode"
                >
                </el-option>
              </el-select>
            </div>
          </div>
          <div class="abox" style="width: 100%;">
            <span class="abox-lableName"><span>*</span>过滤URL：</span>
            <div class="input-box">
              <el-input style="width: 100%;" maxlength="100" v-model="editData.requestUrl" placeholder="请填写过滤URL"></el-input>
            </div>
          </div>
          <div class="abox" style="width: 100%;">
            <span class="abox-lableName"><span style="color:white">*</span>请求描述：</span>
            <div class="input-box">
              <el-input :rows="2" v-model="editData.requestDesc" placeholder="请输入请求描述" type="textarea"></el-input>
            </div>
          </div>
        </div>
      </div>
      <div style="clear:both"></div>
      <template #footer>
        <div class="dialog-footer">
            <el-button @click="dialogRequestConfigShow = false">取 消</el-button>
            <el-button type="primary" @click="saveRequestConfig">确 定</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 查询条件 -->
    <div class="search-box-one" style="width:78%;overflow:visible;">
      <div style="width:33%;float:left;margin-top:20px;">
        <div style="width:100%;float:left;">
          <div class="search-box-top-name" style="width: 75px;">配置类型：</div>
          <div style="width:calc(100% - 100px - 2.3%);float:right;padding-right:2.3%">
            <el-select
                v-model="queryData.configType"
                placeholder="请选择配置类型"
                style="width:100%"
                clearable
              >
                <el-option
                  v-for="item in configTypeList"
                  :key="item.itemCode"
                  :label="item.itemName"
                  :value="item.itemCode"
                >
                </el-option>
              </el-select>
          </div>
        </div>
      </div>
      <div style="width:66%;float:left;margin-top:20px;">
        <div style="width:100%;float:left;">
          <div class="search-box-top-name" style="width: 75px;">过滤URL：</div>
          <div style="width:calc(100% - 100px - 2.3%);float:right;padding-right:2.3%">
            <el-input v-model="queryData.requestUrl" placeholder="请输入过滤URL" clearable style="width:100%;"></el-input>
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
    <div class="btn-box">
      <div class="new" style="width:60px;" @click="showRequestConfigDialog">
        <font-awesome-icon icon="fas fa-plus-circle" />新建
      </div>
      <div class="new" style="width:90px;background: #f5a623;" @click="updateCache">
        <font-awesome-icon icon="fas fa-flask" />更新缓存
      </div>
    </div>
    <!--表格-->
    <div class="tableBox">
      <el-table
        :data="tableData"
        border
        ref="table"
        style="width: 100%"
        tooltip-effect="light"
        :height="listHeight-70"
      >
        <el-table-column type="index" width="50" label="NO." align="center" fixed="left" >
        </el-table-column>
        <el-table-column fixed="left" label="操作" width="75" align="left">
          <template #default="scope">
            <div type="text" size="small" @click="showRequestConfigDialog(scope.row)">
              <font-awesome-icon icon="far fa-edit" style="float: left;color:#33db67;font-size:18px;width: 26px;cursor: pointer;" />
            </div>
            <div type="text" size="small" @click="deleteRequestConfig(scope.row)">
              <font-awesome-icon icon="far fa-trash-alt" style="float: left;color:#f56c6c;font-size:18px;width: 26px;cursor: pointer;" />
            </div>
          </template>
        </el-table-column>
        <el-table-column fixed="left" label="配置类型" align="left" width="100" :show-overflow-tooltip="true">
          <template #default="scope">
            <span v-text="setConfigType(scope.row.configType)"></span>
          </template>
        </el-table-column>
        <el-table-column label="过滤URL" align="left" width="320" :show-overflow-tooltip="true">
          <template #default="scope">
            <div v-text="scope.row.requestUrl"></div>
          </template>
        </el-table-column>
        <el-table-column label="请求描述" align="left" :show-overflow-tooltip="true">
          <template #default="scope">
            <div v-text="scope.row.requestDesc"></div>
          </template>
        </el-table-column>
        <el-table-column label="修改用户" align="left" width="110" :show-overflow-tooltip="true">
          <template #default="scope">
            <div v-text="scope.row.lastUpdateName"></div>
          </template>
        </el-table-column>
        <el-table-column label="修改时间" width="140" :show-overflow-tooltip="true">
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
</template>
<script>
import { mapState } from "vuex";
export default {
  name: "RequestConfigList",
  data() {
    return {
      //配置信息弹框
      dialogRequestConfigShow: false,
      //当前页
      currentPage: 1,
      pageSize: 10,
      totals: 0,
      //列表数据
      tableData: [],
      //查询条件
      queryData:{
        configType:"",
        requestUrl:""
      },
      //编辑数据
      editData:{
        requestId:"",
        configType:"",
        requestUrl:"",
        requestDesc:""
      },
      edit:false,
      configTypeList: [{"itemCode":"public","itemName":"公开"},{"itemCode":"session","itemName":"Session用户"}],
    };
  },
  computed: {
    ...mapState(["listHeight"])
  },
  mounted() {
    this.getRequestConfigList();
  },
  components: {
    
  },
  watch: {

  },
  methods: {
    /********************************查询缓存列表*****************************/
    search() {
      this.getRequestConfigList(true);
    },
    /********************************清空查询条件*****************************/
    empty() {
      this.queryData.configType = "";
      this.queryData.requestUrl = "";
      this.getRequestConfigList(true);
    },
    /********************************渲染配置类型*****************************/
    setConfigType(val) {
      if (val == 'public') {
        return "公开";
      }else if(val == 'session') {
        return "Session用户";
      }else{
        return val;
      }
    },
    /********************************删除配置*****************************/
    deleteRequestConfig(row) {
      this.$confirm("此操作将删除配置, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        closeOnClickModal: false
      })
        .then(() => {
          let data = {
            requestId: row.requestId
          };
          let vm = this;
          this.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/requestconfig/delete`, data)
            .then(function(response) {
              vm.$message({
                type: "success",
                message: "删除成功!"
              });
              vm.getRequestConfigList(true);
            })
            .catch(function(error) {});
        })
        .catch(() => {});
    },
    /********************************保存配置*****************************/
    saveRequestConfig() {
      let data = this.editData;
      //判断是否可以保存
      if (data.configType && data.requestUrl) {
        let message = "新增成功";
        let url = this.$yqvutil.getContextPath()+"yqvservice/requestconfig/insert";
        if(this.edit){
          url = this.$yqvutil.getContextPath()+"yqvservice/requestconfig/update";
          message = "修改成功";
        }
        let vm = this;
        this.$http.post(url, data)
          .then(function(response) {
            if (response.status == 200) {
              vm.$message({
                type: "success",
                message: message
              });
              vm.dialogRequestConfigShow = false;
              vm.getRequestConfigList(true);
            }
          })
          .catch(function(error) {});
      } else {
        this.$message({
          showClose: true,
          message: "请填写配置信息",
          type: "warning"
        });
      }
    },
    /********************************编辑配置信息*****************************/
    showRequestConfigDialog(row) {
      if(row && row.requestId){
        let vm = this;
        let data = {"requestId":row.requestId};
        this.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/requestconfig/find`,data)
          .then(function(response) {
            if (response.status == 200) {
              vm.edit = true;
              vm.dialogRequestConfigShow = true;
              vm.editData = response.data;
            }
          })
          .catch(function(error) {});
      }else{
        let vm = this;
        vm.editData.requestId="";
        vm.editData.requestUrl="";
        vm.editData.requestDesc="";
        vm.editData.configType="";
        vm.edit = false;
        vm.dialogRequestConfigShow = true;
      }
    },
    closeRequestConfigDialog() {
      this.dialogRequestConfigShow = false;
    },
    /********************************查询配置列表*****************************/
    getRequestConfigList(flag) {
      let vm = this;
      let data = this.queryData;
      if (flag) {
        vm.currentPage = 1;
        vm.pageSize = 10;
        vm.totals = 0;
      }
      this.$http.post(
          vm.$yqvutil.getContextPath()+`yqvservice/requestconfig/page?currentPage=${vm.currentPage}&pageSize=${
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
    /********************************分页*****************************/
    handleSizeChange(val) {
      this.pageSize = val;
      this.getRequestConfigList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getRequestConfigList();
    },
    /********************************更新缓存*****************************/
    updateCache() {
      let strName = "此操作会更新公共缓存, 是否继续?";
      this.$confirm(strName, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        closeOnClickModal: false
      })
        .then(() => {
          let vm = this;
          this.$http.get(vm.$yqvutil.getContextPath()+`yqvservice/requestconfig/update/cache`)
            .then(function(response) {
              if (response.status == 200) {
                vm.$message({
                  showClose: true,
                  message: "更新成功",
                  type: "success"
                });
              }
            })
            .catch(function(error) {});
        })
        .catch(() => {});
    }
  }
};
</script>

<style scoped lang="less">
.queryList ::v-deep(.el-form-item__label) {
  width: 90px !important;
  height: 35px;
}
.requestConfigList {
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
  .abox-lableName {
    width: 76px;
    display: inline-block;
    span {
      color: red;
      font-size: 14px;
    }
  }
  .el-input {
    width: 220px;
  }
  .el-select {
    width: 220px;
  }
  .span-white{
    color: white!important;
  }
  .span-red{
    color: red;
  }
}
</style>

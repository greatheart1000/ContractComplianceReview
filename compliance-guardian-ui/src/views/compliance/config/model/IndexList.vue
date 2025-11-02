<template>
  <div class="ModelConfigList">
    <!-- 编辑配置信息 -->
    <el-dialog title="配置信息" 
      v-model="dialogModelConfigShow" 
      :close-on-click-modal="false" 
      @close="closeModelConfigDialog" 
      width="680px"
    >
      <div style="width: 640px; height: 260px; margin: 0 auto">
        <div class="editFormList">
          <div class="abox" style="margin-top: 8px">
            <span class="abox-lableName"><span>*</span>模型编号：</span>
            <div class="input-box">
              <el-input style="width: 100%" v-model="editData.modelCode" placeholder="请填写模型编号"></el-input>
            </div>
            <div class="error-point" v-if="modelCodeVerify">
              请填写模型编号
            </div>
          </div>
          <div class="abox" style="margin-top: 8px">
            <span class="abox-lableName"><span>*</span>模型名称：</span>
            <div class="input-box">
              <el-input style="width: 100%" v-model="editData.modelName" placeholder="请填写模型名称"></el-input>
            </div>
            <div class="error-point" v-if="modelNameVerify">
              请填写模型名称
            </div>
          </div>
          <div class="abox" style="width: 100%">
            <span class="abox-lableName"><span>*</span>模型秘钥：</span>
            <div class="input-box">
              <el-input style="width: 100%" v-model="editData.modelKey" placeholder="请填写模型秘钥"></el-input>
            </div>
            <div class="error-point" v-if="modelKeyVerify">
              请填写模型秘钥
            </div>
          </div>
          <div class="abox" style="width: 100%">
            <span class="abox-lableName"><span>*</span>模型地址：</span>
            <div class="input-box">
              <el-input style="width: 100%" v-model="editData.modelUrl" placeholder="请填写模型地址"></el-input>
            </div>
            <div class="error-point" v-if="modelUrlVerify">
              请填写模型地址
            </div>
          </div>
          <div class="abox">
            <span class="abox-lableName"><span style="color: white">*</span>模型排序：</span>
            <div class="input-box">
              <el-input onkeyup="value=value.replace(/[^\d]/g,'')" v-model="editData.modelSort" placeholder="请填写模型排序"></el-input>
            </div>
          </div>
        </div>
      </div>
      <div style="clear: both"></div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogModelConfigShow = false">取 消</el-button>
          <el-button type="primary" @click="saveModelConfig">确 定</el-button>
        </div>
      </template>
    </el-dialog>
    <div class="tabBox">
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane label="配置列表" name="first"> </el-tab-pane>
      </el-tabs>
      <div class="tabBottom"></div>
    </div>
    <div v-if="activeName == 'first'">
      <!-- 查询条件 -->
      <div class="search-box-one" style="width:100%;overflow:visible;margin-top: -20px;">
        <div style="width:33%;float:left;margin-top:20px;">
          <div style="width:100%;float:left;">
            <div class="search-box-top-name" style="width: 90px;">模型编号：</div>
            <div style="width:calc(100% - 115px - 2.3%);float:right;padding-right:2.3%">
              <el-input v-model="queryData.modelCode" placeholder="请输入模型编号" clearable style="width:100%;"></el-input>
            </div>
          </div>
        </div>
        <div style="width:33%;float:left;margin-top:20px;">
          <div style="width:100%;float:left;">
            <div class="search-box-top-name" style="width: 90px;">模型名称：</div>
            <div style="width:calc(100% - 115px - 2.3%);float:right;padding-right:2.3%">
              <el-input v-model="queryData.modelName" placeholder="请输入模型名称" clearable style="width:100%;"></el-input>
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
      <!-- 操作按钮 -->
      <div class="btn-box" style="margin-top: 10px;">
        <div class="new" style="width: 60px" @click="showModelConfigDialog">
          <font-awesome-icon icon="fas fa-plus-circle" />新建
        </div>
        <div class="start" style="width: 60px" @click="updateConfigStatus('Y')">
          <font-awesome-icon icon="far fa-play-circle" />启用
        </div>
        <div class="prohibit" style="width: 60px; background: #f56c6c" @click="updateConfigStatus('N')">
          <font-awesome-icon icon="fas fa-ban" />禁用
        </div>
      </div>
      <!--表格-->
      <div class="tableBox">
        <el-table :data="tableData" @selection-change="checkAll" border ref="table" style="width: 100%" tooltip-effect="light" :height="tabListHeight-50">
          <el-table-column type="selection" width="50" label="NO." align="center" fixed="left">
          </el-table-column>
          <el-table-column fixed="left" label="操作" width="70" align="center">
            <template #default="scope">
              <div type="text" size="small" @click="showModelConfigDialog(scope.row)">
                <font-awesome-icon icon="far fa-edit"
                  style="float: left;color: #33db67;font-size: 18px;width: 26px;cursor: pointer;"/>
              </div>
              <div type="text" size="small" @click="deleteModelConfig(scope.row)">
                <font-awesome-icon icon="far fa-trash-alt"
                  style="float: left; color: #f56c6c;font-size: 18px;width: 26px;cursor: pointer;"/>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="模型ID" align="left" width="100" :show-overflow-tooltip="true">
            <template #default="scope">
              <div v-text="scope.row.modelId"></div>
            </template>
          </el-table-column>
          <el-table-column label="模型编号" align="left" width="160" :show-overflow-tooltip="true">
            <template #default="scope">
              <div v-text="scope.row.modelCode"></div>
            </template>
          </el-table-column>
          <el-table-column label="模型名称" align="left" width="140" :show-overflow-tooltip="true">
            <template #default="scope">
              <div v-text="scope.row.modelName"></div>
            </template>
          </el-table-column>
          <el-table-column label="模型地址" align="left" :show-overflow-tooltip="true">
            <template #default="scope">
              <div v-text="scope.row.modelUrl"></div>
            </template>
          </el-table-column>
          <el-table-column label="模型状态" align="center" width="80">
            <template #default="scope">
              <span v-text="$yqvutil.getSelectItemName('MODEL_CONFIG_STATUS',scope.row.modelStatus)" :class="[scope.row.modelStatus == 'Y'? 'activeColorY': 'activeColorN',]"></span>
            </template>
          </el-table-column>
          <el-table-column label="模型排序" align="center" width="80">
            <template #default="scope">
              <div v-text="scope.row.modelSort"></div>
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
  </div>
</template>
<script>
import { mapState } from "vuex";
export default {
  name: "ModelConfigList",
  data() {
    return {
      //导航控制
      activeName: "first",
      //判断哪个标签
      judgeLabel: false,
      //配置信息弹框
      dialogModelConfigShow: false,
      //当前页
      currentPage: 1,
      pageSize: 10,
      totals: 0,
      //全选单选容器
      container: [],
      //列表数据
      tableData: [],
      //查询条件
      queryData: {
        modelCode: "",
        modelName: "",
      },
      //编辑数据
      editData: {
        modelId: "",
        modelCode: "",
        modelName: "",
        modelKey: "",
        modelUrl: "",
        modelStatus: "",
        modelSort: 99,
      },
      edit: false,
      //必填校验
      modelCodeVerify: false,
      modelNameVerify: false,
      modelKeyVerify: false,
      modelUrlVerify: false,
    };
  },
  computed: {
    ...mapState(["yqvSelect", "tabListHeight"]),
  },
  mounted() {
    this.getModelConfigList();
  },
  components: {},
  watch: {
    "editData.modelCode"() {
      this.verifyModelConfig("modelCode");
    },
    "editData.modelName"() {
      this.verifyModelConfig("modelName");
    },
    "editData.modelKey"() {
      this.verifyModelConfig("modelKey");
    },
    "editData.modelUrl"() {
      this.verifyModelConfig("modelUrl");
    },
  },
  methods: {
    /********************************全选*****************************/
    checkAll(val) {
      //进行赋值,判断是否选中内容
      this.container = val;
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
      if (labelName == "配置列表") {
        this.activeName = "first";
        this.judgeLabel = false;
      }
    },
    /********************************查询缓存列表*****************************/
    search() {
      this.getModelConfigList(true);
    },
    /********************************清空查询条件*****************************/
    empty() {
      this.queryData.modelCode = "";
      this.queryData.modelName = "";
      this.getModelConfigList(true);
    },
    verifyModelConfig(type) {
      let data = this.editData;
      //模型编号
      if (type == "all" || type == "modelCode") {
        if (!data.modelCode) {
          this.modelCodeVerify = true;
        } else {
          this.modelCodeVerify = false;
        }
      }
      //模型名称
      if (type == "all" || type == "modelName") {
        if (!data.modelName) {
          this.modelNameVerify = true;
        } else {
          this.modelNameVerify = false;
        }
      }
      //模型秘钥
      if (type == "all" || type == "modelKey") {
        if (!data.modelKey) {
          this.modelKeyVerify = true;
        } else {
          this.modelKeyVerify = false;
        }
      }
      //模型地址
      if (type == "all" || type == "openaiUrl") {
        if (!data.modelUrl) {
          this.modelUrlVerify = true;
        } else {
          this.modelUrlVerify = false;
        }
      }
    },
    /********************************删除配置*****************************/
    deleteModelConfig(row) {
      this.$confirm("此操作将删除配置, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        closeOnClickModal: false,
      })
        .then(() => {
          let data = {
            modelId: row.modelId,
          };
          let vm = this;
          this.$http
            .post(vm.$yqvutil.getContextPath()+`yqvservice/modelconfig/delete`, data)
            .then(function (response) {
              vm.$message({
                type: "success",
                message: "删除成功!",
              });
              vm.getModelConfigList(true);
            })
            .catch(function (error) {});
        })
        .catch(() => {});
    },
    /********************************保存配置*****************************/
    saveModelConfig() {
      this.verifyModelConfig("all");
      let data = this.editData;
      //判断是否可以保存
      if (!this.modelCodeVerify && !this.modelNameVerify && !this.modelKeyVerify && !this.modelUrlVerify) {
        let message = "新增成功";
        let url = this.$yqvutil.getContextPath()+"yqvservice/modelconfig/insert";
        if (this.edit) {
          url = this.$yqvutil.getContextPath()+"yqvservice/modelconfig/update";
          message = "修改成功";
        }
        let vm = this;
        vm.$http.post(url, data)
          .then(function (response) {
            if (response.status == 200) {
              vm.$message({
                type: "success",
                message: message,
              });
              vm.dialogModelConfigShow = false;
              vm.getModelConfigList(true);
            }
          })
          .catch(function (error) {});
      }
    },
    /********************************编辑配置信息*****************************/
    showModelConfigDialog(row) {
      let vm = this;
      if (row && row.modelId) {
        let data = { modelId: row.modelId };
        this.$http
          .post(vm.$yqvutil.getContextPath()+`yqvservice/modelconfig/find`, data)
          .then(function (response) {
            if (response.status == 200) {
              vm.edit = true;
              vm.dialogModelConfigShow = true;
              vm.editData = response.data;
            }
          })
          .catch(function (error) {});
      } else {
        vm.editData.modelId = "";
        vm.editData.modelCode = "";
        vm.editData.modelName = "";
        vm.editData.modelKey = "";
        vm.editData.modelUrl = "";
        vm.editData.modelSort = 99;
        vm.edit = false;
        vm.dialogModelConfigShow = true;
        setTimeout(function () {
          vm.modelCodeVerify = false;
          vm.modelNameVerify = false;
          vm.modelKeyVerify = false;
          vm.modelUrlVerify = false;
        }, 20);
      }
    },
    closeModelConfigDialog() {
      this.dialogModelConfigShow = false;
    },
    /********************************查询配置列表*****************************/
    getModelConfigList(flag) {
      let vm = this;
      let data = this.queryData;
      if (flag) {
        vm.currentPage = 1;
        vm.pageSize = 10;
        vm.totals = 0;
      }
      vm.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/modelconfig/page?currentPage=${vm.currentPage}&pageSize=${vm.pageSize}`,data)
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
      this.getModelConfigList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getModelConfigList();
    },
    /********************************修改配置状态*****************************/
    updateConfigStatus(modelStatus) {
      let vm = this;
      if (vm.container.length != 0) {
        let strName;
        if (modelStatus === "Y") {
          strName = "此操作会启用配置, 是否继续?";
        } else {
          strName = "此操作会禁用配置, 是否继续?";
        }
        vm.$confirm(strName, "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
          closeOnClickModal: false,
        })
          .then(() => {
            let modelIdList = [];
            for (let i = 0; i < this.container.length; i++) {
              modelIdList.push(this.container[i].modelId);
            }
            let data = {
              modelStatus: modelStatus,
              modelIdList: modelIdList,
            };
            this.$http
              .post(vm.$yqvutil.getContextPath()+`yqvservice/modelconfig/update/status`, data)
              .then(function (response) {
                if (response.status == 200) {
                  vm.$message({
                    showClose: true,
                    message: "操作成功",
                    type: "success",
                  });
                  vm.getModelConfigList();
                }
              })
              .catch(function (error) {});
          })
          .catch(() => {});
      } else {
        this.$message({
          showClose: true,
          message: "请选择配置",
          type: "warning",
        });
      }
    },
  },
};
</script>

<style scoped lang="less">
.ModelConfigList {
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
      width: 200px;
      margin-right: 14px;
    }
  }
  .authorityBox {
    height: 100%;
    position: relative;
  }
  .abox-lableName {
    width: 90px;
    display: inline-block;
    span {
      color: red;
      font-size: 14px;
    }
  }
  .el-input {
    width: 200px;
  }
  .el-select {
    width: 200px;
  }
  .span-white {
    color: white !important;
  }
  .span-red {
    color: red;
  }
}
</style>

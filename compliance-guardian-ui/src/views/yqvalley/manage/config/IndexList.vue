<template>
  <div class="appConfigList">
    <!-- 编辑应用配置 -->
    <el-dialog
      title="应用配置"
      v-model="dialogAppConfigShow"
      :close-on-click-modal="false"
      @close="closeAppConfigDialog"
      width="650px"
    >
      <div style="width: 630px;height:310px;margin:0 auto">
        <div class="editFormList">
          <div class="abox" style="width: 100%;margin-top: 8px;">
            <span class="abox-lableName"><span style="color:white">*</span>配置编号：</span>
            <div class="input-box">
              <el-input :disabled="true" maxlength="48" style="width: 100%;" v-model="editData.configCode" placeholder="请填写用户账号"></el-input>
            </div>
            <div class="error-point" v-if="configCodeVerify">请填写6-48位字母数字下划线</div>
          </div>
          <div class="abox" style="width: 100%;">
            <span class="abox-lableName"><span>*</span>配置内容：</span>
            <div class="input-box">
              <el-input maxlength="480" style="width: 100%;" v-model="editData.configValue" placeholder="请填写配置内容"></el-input>
            </div>
            <div class="error-point" v-if="configValueVerify">请填写符合值类型和约束的内容</div>
          </div>
          <div class="abox" style="width: 100%;">
            <span class="abox-lableName"><span style="color:white">*</span>配置约束：</span>
            <div class="input-box">
              <el-input :disabled="true" style="width: 100%;" v-model="editData.configBind" placeholder="请填写配置约束"></el-input>
            </div>
          </div>
          <div class="abox" style="width: 100%;">
            <span class="abox-lableName"><span style="color:white">*</span>值类型：</span>
            <div class="input-box">
              <el-select
                  v-model="editData.valueType"
                  placeholder="请选择值类型"
                  style="width:100%"
                  clearable
                  collapse-tags
                  :disabled="true"
                >
                <el-option
                  v-for="item in $yqvutil.getSelectList('APP_CONFIG_VALUETYPE')"
                  :key="item.itemCode"
                  :label="item.itemName"
                  :value="item.itemValue"
                >
                </el-option>
              </el-select>
            </div>
          </div>
          <div class="abox" style="width: 100%;">
            <span class="abox-lableName"><span style="color:white">*</span>配置描述：</span>
            <div class="input-box">
              <el-input :disabled="true" :rows="2" v-model="editData.configDesc" placeholder="请输入配置描述" type="textarea"></el-input>
            </div>
          </div>
        </div>
      </div>
      <div style="clear:both"></div>
      <template #footer>
        <div class="dialog-footer">
            <el-button @click="dialogAppConfigShow = false">取 消</el-button>
            <el-button type="primary" @click="saveAppConfig">确 定</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 查询条件 -->
    <div class="search-box-one" style="width:78%;overflow:visible;">
      <div style="width:33%;float:left;margin-top:20px;">
        <div style="width:100%;float:left;">
          <div class="search-box-top-name" style="width: 75px;">修改控制：</div>
          <div style="width:calc(100% - 100px - 2.3%);float:right;padding-right:2.3%">
            <el-select
              v-model="queryData.updateControl"
              placeholder="请选择修改控制"
              style="width:100%"
              clearable
            >
              <el-option
                v-for="item in $yqvutil.getSelectList('VALLEY_UPDATE_CONTROL')"
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
        <div style="width:100%;float:left;">
          <div class="search-box-top-name" style="width: 75px;">配置分组：</div>
          <div style="width:calc(100% - 100px - 2.3%);float:right;padding-right:2.3%">
            <el-select
                v-model="queryData.configGroup"
                placeholder="请选择配置分组"
                style="width:100%"
                clearable
              >
                <el-option
                  v-for="item in $yqvutil.getSelectList('APP_CONFIG_GROUP')"
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
        <div style="width:100%;float:left;">
          <div class="search-box-top-name" style="width: 75px;">配置编号：</div>
          <div style="width:calc(100% - 100px - 2.3%);float:right;padding-right:2.3%">
            <el-input v-model="queryData.configCode" placeholder="请输入配置编号" clearable style="width:100%;"></el-input>
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
        <el-table-column fixed="left" label="操作" width="50" align="center">
          <template #default="scope">
            <div v-if="scope.row.updateControl!=null && scope.row.updateControl=='Y'" type="text" size="small" @click="showAppConfigDialog(scope.row)">
              <font-awesome-icon icon="far fa-edit" style="float: left;color:#33db67;font-size:20px;width: 32px;cursor: pointer;" />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="配置分组" align="left" width="120" :show-overflow-tooltip="true">
          <template #default="scope">
            <span v-text="$yqvutil.getSelectItemName('APP_CONFIG_GROUP',scope.row.configGroup)"></span>
          </template>
        </el-table-column>
        <el-table-column label="配置编号" align="left" width="220" :show-overflow-tooltip="true">
          <template #default="scope">
            <div v-text="scope.row.configCode"></div>
          </template>
        </el-table-column>
        <el-table-column label="配置内容" align="left" width="260" :show-overflow-tooltip="true">
          <template #default="scope">
            <div v-text="scope.row.configValue"></div>
          </template>
        </el-table-column>
        <el-table-column label="配置描述" align="left" width="300" :show-overflow-tooltip="true">
          <template #default="scope">
            <div v-text="scope.row.configDesc"></div>
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
  name: "appConfigList",
  data() {
    return {
      //配置信息弹框
      dialogAppConfigShow: false,
      //当前页
      currentPage: 1,
      pageSize: 10,
      totals: 0,
      //列表数据
      tableData: [],
      //查询条件
      queryData:{
        configGroup:"",
        configCode:"",
        updateControl:"Y"
      },
      //编辑数据
      editData:{
        configGroup:"",
        configCode:"",
        configValue:"",
        configBind:"",
        configDesc:"",
        updateControl:"",
        contentOpen:"",
        valueType:"",
        sensitiveData:""
      },
      //是否敏感数据
      sensitiveValue:false,
      //必填校验
      configValueVerify: false,
      oldConfigValue:""
    };
  },
  computed: {
    ...mapState(["yqvSelect", "listHeight"])
  },
  mounted() {
    this.getAppConfigList();
  },
  components: {
    
  },
  watch: {
    "editData.configValue"() {
      this.verifyAppConfig();
    }
  },
  methods: {
    /********************************查询缓存列表*****************************/
    search() {
      this.getAppConfigList(true);
    },
    /********************************清空查询条件*****************************/
    empty() {
      this.queryData.configGroup = "";
      this.queryData.configCode = "";
      this.queryData.contentOpen = "";
      this.getAppConfigList(true);
    },
    verifyAppConfig() {
      let data = this.editData;
      //配置内容
      if (!data.configValue) {
        this.configValueVerify = true;
      } else {
        if(data.sensitiveData && data.sensitiveData==="Y" && data.configValue==="******"){
          this.configValueVerify = false;
        }else{
        if(data.valueType && data.valueType==="number"){
          let filter = /^\+?[1-9][0-9]*$/;
            if (!filter.test(data.configValue)) {
              this.configValueVerify = true;
            }else{
              if(data.configBind){
                let cb = false;
                let arr = data.configBind.split("-");
                if(arr.length==2){
                  try {
                    let cv = parseInt(data.configValue);
                    let cb1 = parseInt(arr[0]);
                    let cb2 = parseInt(arr[1]);
                    if(cv>=cb1 && cv<=cb2){
                      cb = true;
                    }
                  } catch (error) {}
                  if(!cb){
                    this.configValueVerify = true;
                  }else{
                    this.configValueVerify = false;
                  }
                }else{
                  this.configValueVerify = false;
                }
              }
            }
          }else if(data.valueType && data.valueType==="enum"){
            if(data.configBind){
              let cb = false;
              let arr = data.configBind.split("#");
              arr.forEach(item => {
                if(data.configValue===item){
                  cb = true;
                }
              });
              if(!cb){
                this.configValueVerify = true;
              }else{
                this.configValueVerify = false;
              }
            }
          }else{
            this.configValueVerify = false;
          }
        }
      }
    },
    /********************************修改配置*****************************/
    saveAppConfig() {
      if(this.sensitiveValue && this.editData && this.editData.configValue==="******"){
        this.$message.warning("敏感数据没有任何修改");
        return;
      }
      if(this.oldConfigValue===this.editData.configValue){
        this.$message.warning("没有任何修改");
        return;
      }
      this.verifyAppConfig();
      let data = this.editData;
      //判断是否可以保存
      if (!this.configValueVerify) {
        let url = this.$yqvutil.getContextPath()+"yqvservice/appconfig/update/value";
        let message = "修改成功";
        let vm = this;
        this.$http.post(url, data)
          .then(function(response) {
            if (response.status == 200) {
              vm.$message({
                type: "success",
                message: message
              });
              vm.dialogAppConfigShow = false;
              vm.getAppConfigList(true);
            }
          })
          .catch(function(error) {});
      }
    },
    /********************************编辑配置信息*****************************/
    showAppConfigDialog(row) {
      if(row && row.configCode){
        this.sensitiveValue = false;
        let vm = this;
        let data = {"configCode":row.configCode};
        this.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/appconfig/find`,data)
          .then(function(response) {
            if (response.status == 200) {
              vm.dialogAppConfigShow = true;
              vm.editData = response.data;
              vm.oldConfigValue = vm.editData.configValue;
              if(vm.editData.sensitiveData==="Y"){
                vm.sensitiveValue = true;
              }
            }
          })
          .catch(function(error) {});
      }
    },
    closeAppConfigDialog() {
      this.dialogAppConfigShow = false;
    },
    /********************************查询配置列表*****************************/
    getAppConfigList(flag) {
      let vm = this;
      let data = this.queryData;
      if (flag) {
        vm.currentPage = 1;
        vm.pageSize = 10;
        vm.totals = 0;
      }
      this.$http.post(
          vm.$yqvutil.getContextPath()+`yqvservice/appconfig/page?currentPage=${vm.currentPage}&pageSize=${
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
      this.getAppConfigList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getAppConfigList();
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
          this.$http.get(vm.$yqvutil.getContextPath()+`yqvservice/appconfig/update/cache`)
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
.appConfigList {
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

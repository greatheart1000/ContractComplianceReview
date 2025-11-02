<template>
  <div class="editSelectInfo">
    <div class="contentBox">
      <div :class="subShow?'select-info-left':'select-info-left2'">
        <div v-if="subShow" class="info-head">选择详情</div>
        <div class="apiztreeContent">
          <div style="margin:0 auto;margin-left: 20px;">
            <el-form :inline="true" class="demo-form-inline items" style="margin-top: 30px;">
              <el-form-item class="item3">
                <span class="lableName"><span style="color:red">*</span>选择编号：</span>
                <el-input :disabled="subShow" v-model="selectData.selectCode" placeholder="请输入选择编号"></el-input>
              </el-form-item>
              <el-form-item class="item3">
                <span class="lableName"><span style="color:red">*</span>选择名称：</span>
                <el-input v-model="selectData.selectName" placeholder="请输入选择名称"></el-input>
              </el-form-item>
              <el-form-item class="item3">
                <span class="lableName"><span style="color:white">*</span>修改控制：</span>
                <el-select v-model="selectData.updateControl" placeholder="请选择修改控制">
                  <el-option
                    v-for="item in $yqvutil.getSelectList('VALLEY_UPDATE_CONTROL')"
                    :key="item.itemCode"
                    :label="item.itemName"
                    :value="item.itemValue"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item class="item3">
                <span class="lableName"><span style="color:white">*</span>内容公开：</span>
                <el-select v-model="selectData.contentOpen" placeholder="请选择内容公开">
                  <el-option
                    v-for="item in $yqvutil.getSelectList('VALLEY_CONTENT_OPEN')"
                    :key="item.itemCode"
                    :label="item.itemName"
                    :value="item.itemValue"
                  >
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item class="item3">
                <span class="lableName"><span style="color:white">*</span>选择描述：</span>
                <el-input :rows="3" v-model="selectData.selectDesc" placeholder="请输入选择描述" type="textarea"></el-input>
              </el-form-item>
            </el-form>
          </div>
          <div class="clear"></div>
          <div style="text-align:center; margin-top:30px;overflow:hidden;width:100%;height:50px;">
            <div style="margin-left:calc(50% - 36px)" class="new" @click="saveSelectData">
              <font-awesome-icon icon="far fa-save" />保存
            </div>
          </div>
        </div>
      </div>
      <div v-if="subShow" class="select-info-right">
        <div class="tabBox">
          <el-tabs v-model="activeName" @tab-click="handleClick">
            <el-tab-pane label="子项列表" name="first"> </el-tab-pane>
            <el-tab-pane v-if="judgeLabel" label="子项详情" name="second"></el-tab-pane>
          </el-tabs>
          <div class="tabBottom"></div>
        </div>
        <div v-if="activeName == 'first'">
          <!-- 查询条件 -->
          <div class="search-box-one" style="width:calc(100% - 200px - 2.3%);overflow:visible;margin-top: -20px;">
            <div style="width:99%;float:left;margin-top:20px;">
              <div style="width:50%;float:left;">
                <div class="search-box-top-name" style="width: 75px;">子项内容：</div>
                <div style="width:calc(100% - 100px - 2.3%);float:right;padding-right:2.3%">
                  <el-input v-model="queryItemData.itemValue" placeholder="请输入子项内容" clearable style="width:100%;"></el-input>
                </div>
              </div>
              <div style="width:50%;float:left;">
                <div class="search-box-top-name" style="width: 75px;">子项名称：</div>
                <div style="width:calc(100% - 100px - 2.3%);float:right;padding-right:2.3%">
                  <el-input v-model="queryItemData.itemName" placeholder="请输入子项名称" clearable style="width:100%;"></el-input>
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
            <div class="new" style="width:60px;" @click="addSelectItem">
              <font-awesome-icon icon="fas fa-plus-circle" />新建
            </div>
            <div class="delete" style="width:60px;margin-top: 0px;" @click="deleteSelectItem">
              <font-awesome-icon icon="far fa-trash-alt" />删除
            </div>
          </div>
          <div>
            <el-table :data="selectItemList" border @selection-change="checkAll" style="width: 100%" tooltip-effect="light" :height="tabListHeight-130">
              <el-table-column type="selection" width="55" align="center">
              </el-table-column>
              <el-table-column fixed="left" label="操作" width="50" align="left">
                <template #default="scope">
                  <div @click="editSelectItem(scope.row)" type="text" size="small" title="修改">
                    <font-awesome-icon icon="far fa-edit" style="float: left;color:#33db67;font-size:18px;width: 26px;cursor: pointer;" />
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="子项内容" align="left" width="100" :show-overflow-tooltip="true">
                <template #default="scope">
                  <span v-text="scope.row.itemValue"></span>
                </template>
              </el-table-column>
              <el-table-column label="子项名称" align="left" width="120" :show-overflow-tooltip="true">
                <template #default="scope">
                  <span v-text="scope.row.itemName"></span>
                </template>
              </el-table-column>
              <el-table-column label="排序" align="left" width="60" :show-overflow-tooltip="true">
                <template #default="scope">
                  <span v-text="scope.row.itemSort"></span>
                </template>
              </el-table-column>
              <el-table-column label="级联编号" align="left" width="180" :show-overflow-tooltip="true">
                <template #default="scope">
                  <span v-text="scope.row.subSelectCode"></span>
                </template>
              </el-table-column>
              <el-table-column label="扩展字段1" align="left" width="100" :show-overflow-tooltip="true">
                <template #default="scope">
                  <span v-text="scope.row.expandValue1"></span>
                </template>
              </el-table-column>
              <el-table-column label="扩展字段2" align="left" width="100" :show-overflow-tooltip="true">
                <template #default="scope">
                  <span v-text="scope.row.expandValue2"></span>
                </template>
              </el-table-column>
              <el-table-column label="扩展字段3" align="left" width="100" :show-overflow-tooltip="true">
                <template #default="scope">
                  <span v-text="scope.row.expandValue3"></span>
                </template>
              </el-table-column>
              <el-table-column label="扩展字段4" align="left" width="100" :show-overflow-tooltip="true">
                <template #default="scope">
                  <span v-text="scope.row.expandValue4"></span>
                </template>
              </el-table-column>
              <el-table-column label="子项描述" align="left" width="200" :show-overflow-tooltip="true">
                <template #default="scope">
                  <span v-text="scope.row.itemDesc"></span>
                </template>
              </el-table-column>
              <el-table-column label="修改用户" align="left" width="110" :show-overflow-tooltip="true">
                <template #default="scope">
                  <div v-text="scope.row.lastUpdateName"></div>
                </template>
              </el-table-column>
              <el-table-column label="修改时间" align="left" width="140">
                <template #default="scope">
                  <div v-text="$yqvutil.setFormTime(scope.row.lastUpdateDate, 1)"></div>
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
        <div v-if="activeName == 'second'" class="editFormList">
          <div style="width: 600px;height:100%;margin:0 auto">
            <div class="abox">
              <span class="abox-lableName"><span>*</span>子项内容：</span>
              <div class="input-box">
                <el-input :disabled="subEdit" @input="verifySelectItemInfo('itemValue')" maxlength="30" v-model="selectItemData.itemValue" placeholder="请填写子项内容"></el-input>
              </div>
              <div class="error-point" v-if="selectItemVerify.itemValueVerify">请填写子项内容</div>
            </div>
            <div class="abox">
              <span class="abox-lableName"><span>*</span>子项名称：</span>
              <div class="input-box">
                <el-input maxlength="50" @input="verifySelectItemInfo('itemName')" v-model="selectItemData.itemName" placeholder="请填写子项名称"></el-input>
              </div>
              <div class="error-point" v-if="selectItemVerify.itemNameVerify">请填写子项名称</div>
            </div>
            <div class="abox">
              <span class="abox-lableName"><span style="color: white;">*</span>排&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;序：</span>
              <div class="input-box">
                <el-input onkeyup="value=value.replace(/[^\d]/g,'')" v-model="selectItemData.itemSort" placeholder="请填写排序"></el-input>
              </div>
            </div>
            <div class="abox">
              <span class="abox-lableName"><span style="color: white;">*</span>级联编号：</span>
              <div class="input-box">
                <el-input maxlength="30" v-model="selectItemData.subSelectCode" placeholder="请填写级联编号"></el-input>
              </div>
            </div>
            <div class="abox">
              <span class="abox-lableName"><span style="color: white;">*</span>扩展字段：</span>
              <div class="input-box">
                <el-input maxlength="100" v-model="selectItemData.expandValue1" placeholder="请填写扩展字段"></el-input>
              </div>
            </div>
            <div class="abox">
              <span class="abox-lableName"><span style="color: white;">*</span>扩展字段：</span>
              <div class="input-box">
                <el-input maxlength="100" v-model="selectItemData.expandValue2" placeholder="请填写扩展字段"></el-input>
              </div>
            </div>
            <div class="abox">
              <span class="abox-lableName"><span style="color: white;">*</span>扩展字段：</span>
              <div class="input-box">
                <el-input maxlength="100" v-model="selectItemData.expandValue3" placeholder="请填写扩展字段"></el-input>
              </div>
            </div>
            <div class="abox">
              <span class="abox-lableName"><span style="color: white;">*</span>扩展字段：</span>
              <div class="input-box">
                <el-input maxlength="100" v-model="selectItemData.expandValue4" placeholder="请填写扩展字段"></el-input>
              </div>
            </div>
            <div class="abox" style="width: 100%;">
              <span class="abox-lableName"><span style="color: white;">*</span>子项描述：</span>
              <div class="input-box">
                <el-input maxlength="150" v-model="selectItemData.itemDesc" placeholder="请填写子项描述"></el-input>
              </div>
            </div>
            <div class="clear"></div>
            <div style="text-align:center; margin-top:30px;overflow:hidden;width:100%;height:50px;">
              <div style="margin-left:calc(50% - 36px)" class="new" @click="saveSelectItemData">
                <font-awesome-icon icon="far fa-save" />保存
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapMutations } from "vuex";
export default {
  name: "developEditInfo",
  data() {
    return {
      //导航控制
      activeName: "first",
      //判断哪个标签
      judgeLabel: false,
      //当前页
      currentPage: 1,
      pageSize: 10,
      totals: 0,
      //是否编辑模式
      edit: false,
      //是否显示子项
      subShow: false,
      //子项是否编辑模式
      subEdit: false,
      //全选单选容器
      container: [],
      //子项列表数据
      selectItemList: [],
      //选择详情
      selectData:{
        selectCode:"",
        selectName:"",
        selectDesc:"",
        contentOpen:"Y",
        updateControl:"Y"
      },
      //子项查询条件
      queryItemData: {
        itemValue:"",
        itemName:""
      },
      //子项详情
      selectItemData:{
        itemCode:"",
        itemValue:"",
        itemName:"",
        itemDesc:"",
        itemSort:999,
        subSelectCode:"",
        expandValue1:"",
        expandValue2:"",
        expandValue3:"",
        expandValue4:"",
        contentOpen:""
      },
      //用户表单数据必填校验
      selectItemVerify:{
        itemValueVerify: false,
        itemNameVerify: false
      }
    };
  },
  props: ["selectCode","selectName","selectDesc","contentOpen","updateControl"],
  mounted() {
    this.selectData.selectCode = "";
    this.selectData.selectName = "";
    this.selectData.selectDesc = "";
    this.selectData.contentOpen = "Y";
    this.selectData.updateControl = "Y";
    this.selectItemList = [];
    if(this.selectCode!=null){
      this.edit = true;
      this.subShow = true;
      this.selectData.selectCode = this.selectCode;
      this.selectData.selectName = this.selectName;
      this.selectData.selectDesc = this.selectDesc;
      this.selectData.contentOpen = this.contentOpen;
      this.selectData.updateControl = this.updateControl;
      this.getSelectItemData();
    }
  },
  watch:{
    
  },
  components: {},
  computed: {
    ...mapState(["tabListHeight", "yqvSelect"])
  },
  methods: {
    /********************************全选*****************************/
    checkAll(val) {
      this.container = val;
    },
    /********************************分页*****************************/
    handleSizeChange(val) {
      this.pageSize = val;
      this.getSelectItemData();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getSelectItemData();
    },
    /********************************重置校验*****************************/
    empty() {
      this.queryItemData.itemValue = "";
      this.queryItemData.itemName = "";
    },
    /********************************查询*****************************/
    search() {
      this.getSelectItemData();
    },
    /********************************查询子项信息*****************************/
    getSelectItemData() {
      let vm = this;
      let data = {
        selectCode: this.selectData.selectCode,
        itemValue: this.queryItemData.itemValue,
        itemName: this.queryItemData.itemName
      };
      this.$http.post(
          vm.$yqvutil.getContextPath()+`yqvservice/selectconfig/item/page?currentPage=${
            this.currentPage
          }&pageSize=${this.pageSize}`,
          data
        )
        .then(function(response) {
          if (response.status == 200) {
            vm.totals = response.data.page.totalRows;
            vm.selectItemList = response.data.result;
          }
        })
        .catch(function(error) {});
    },
    /********************************保存选择详情*****************************/
    saveSelectData() {
      let vm = this;
      let data = {
        selectCode: vm.selectData.selectCode,
        selectName: vm.selectData.selectName,
        selectDesc: vm.selectData.selectDesc,
        contentOpen: vm.selectData.contentOpen,
        updateControl: vm.selectData.updateControl
      };
      if(!data.contentOpen){
        data.contentOpen="Y";
      }
      if(!data.updateControl){
        data.updateControl="Y";
      }
      //判断是否可以保存
      if (data.selectCode && data.selectName) {
        let message = "新增成功";
        let url = vm.$yqvutil.getContextPath()+"yqvservice/selectconfig/insert";
        if(this.edit){
          url = vm.$yqvutil.getContextPath()+"yqvservice/selectconfig/update";
          message = "修改成功";
        }
        this.$http.post(url, data)
          .then(function(response) {
            if (response.status == 200) {
              vm.edit = true;
              vm.subShow = true;
              vm.$message({
                showClose: true,
                message: message,
                type: "success"
              });
              vm.$parent.getSelectList();
            }
          })
          .catch(function(error) {});
      } else {
        vm.$message.warning("请输入选项编号和选项名称!");
      }
    },
    verifySelectItemInfo(type) {
      let data = this.selectItemData;
      //判断是否填写子项内容
      if(type=="all" || type=="itemValue"){
        if (!data.itemValue) {
          this.selectItemVerify.itemValueVerify = true;
        } else {
          this.selectItemVerify.itemValueVerify = false;
        }
      }
      //判断是否填写子项名称
      if(type=="all" || type=="itemName"){
        if (!data.itemName) {
          this.selectItemVerify.itemNameVerify = true;
        } else {
          this.selectItemVerify.itemNameVerify = false;
        }
      }
    },
    /********************************保存子项信息*****************************/
    saveSelectItemData() {
      this.verifySelectItemInfo("all");
      let vm = this;
      let data = {
        selectCode: vm.selectData.selectCode,
        itemCode: vm.selectItemData.itemCode,
        itemValue: vm.selectItemData.itemValue,
        itemName: vm.selectItemData.itemName,
        itemDesc: vm.selectItemData.itemDesc,
        itemSort: vm.selectItemData.itemSort,
        subSelectCode: vm.selectItemData.subSelectCode,
        expandValue1: vm.selectItemData.expandValue1,
        expandValue2: vm.selectItemData.expandValue2,
        expandValue3: vm.selectItemData.expandValue3,
        expandValue4: vm.selectItemData.expandValue4,
        contentOpen: vm.selectItemData.contentOpen
      };
      //判断是否可以保存
      if (!this.selectItemVerify.itemValueVerify &&
        !this.selectItemVerify.itemNameVerify) {
        let message = "新增成功";
        let url = vm.$yqvutil.getContextPath()+"yqvservice/selectconfig/item/insert";
        if(data.itemCode){
          url = vm.$yqvutil.getContextPath()+"yqvservice/selectconfig/item/update";
          message = "修改成功";
        }
        this.$http.post(url, data)
          .then(function(response) {
            if (response.status == 200) {
              vm.handleClick({"label":"子项列表"});
              vm.$message({
                showClose: true,
                message: message,
                type: "success"
              });
              vm.getSelectItemData();
            }
          })
          .catch(function(error) {});
      }
    },
    /********************************删除子项信息*****************************/
    deleteSelectItem() {
      if (this.container.length != 0) {
        this.$confirm("此操作将删除选择配置信息, 是否继续?", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
          closeOnClickModal: false
        })
          .then(() => {
            let vm = this;
            let itemCodeList = [];
            for (let i = 0; i < this.container.length; i++) {
              itemCodeList.push(this.container[i].itemCode);
            }
            let data = {"selectCode":vm.selectData.selectCode,"itemCodeList":itemCodeList};
            this.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/selectconfig/item/delete`, data)
              .then(function(response) {
                if (response.status == 200) {
                  vm.$message({
                    showClose: true,
                    message: "删除成功",
                    type: "success"
                  });
                  vm.currentPage = 1;
                  vm.pageSize = 10;
                  vm.getSelectItemData();
                }
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
    /********************************标签操作*****************************/
    handleClick(row) {
      let labelName = null;
      if(row.label){
        labelName = row.label;
      }
      if(!labelName && row.props){
        labelName = row.props.label;
      }
      if (labelName == "子项列表") {
        this.activeName = "first";
        this.judgeLabel = false;
      }
    },
    //配置
    addSelectItem() {
      this.judgeLabel = true;
      this.subEdit = false;
      //光标对位
      this.activeName = "second";
      this.selectItemData.itemCode = null;
      this.selectItemData.itemValue = null;
      this.selectItemData.itemName = null;
      this.selectItemData.itemDesc = null;
      this.selectItemData.itemSort = 999;
      this.selectItemData.subSelectCode = null;
      this.selectItemData.expandValue1 = null;
      this.selectItemData.expandValue2 = null;
      this.selectItemData.expandValue3 = null;
      this.selectItemData.expandValue4 = null;
      this.selectItemData.contentOpen = null;
    },
    //配置
    editSelectItem(val) {
      this.judgeLabel = true;
      this.subEdit = true;
      //光标对位
      this.activeName = "second";
      this.selectItemData.itemCode = val.itemCode;
      this.selectItemData.itemValue = val.itemValue;
      this.selectItemData.itemName = val.itemName;
      this.selectItemData.itemDesc = val.itemDesc;
      this.selectItemData.itemSort = val.itemSort;
      this.selectItemData.subSelectCode = val.subSelectCode;
      this.selectItemData.expandValue1 = val.expandValue1;
      this.selectItemData.expandValue2 = val.expandValue2;
      this.selectItemData.expandValue3 = val.expandValue3;
      this.selectItemData.expandValue4 = val.expandValue4;
      this.selectItemData.contentOpen = val.contentOpen;
    },
  }
};
</script>

<style scoped lang="less">
.editSelectInfo {
  height: 100%;
  .contentBox {
    box-sizing: border-box;
    height: 100%;
    display: flex;
    justify-content: space-between;
    padding: 14px 24px 14px 24px;
  }
  .select-info-left {
    border: 1px solid rgba(232, 232, 232, 1);
    width: 320px;
    height: 100%;
    overflow-y: auto;
    .el-input {
      width: 190px;
    }
    .el-textarea {
       width: 190px;
    }
    .el-select {
      width: 190px;
    }
    .info-head {
      text-align: left;
      height: 39px;
      line-height: 39px;
      font-size: 14px;
      font-weight: 500;
      color: #597ef7;
      padding-left: 24px;
      border-bottom: 1px solid #e8e8e8;
    }
  }
  .select-info-left2 {
    border: 1px solid rgba(232, 232, 232, 1);
    width: 480px;
    height: 100%;
    margin: 0px auto;
    .el-input{
      width: 360px;
    }
    .el-textarea {
       width: 360px;
    }
    .el-select {
      width: 360px;
    }
  }
  .select-info-right {
    border: 1px solid rgba(232, 232, 232, 1);
    width: calc(100% - 320px - 2.3%);
    height: 100%;
    overflow-y: auto;
  }
}
</style>

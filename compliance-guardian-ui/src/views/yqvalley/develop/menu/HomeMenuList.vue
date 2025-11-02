<template>
  <div class="homeMenuList">
    <div class="editHomeMenuList">
      <div class="contentBox" style="padding: 4px 24px 14px 24px;">
        <div :class="'select-info-left'">
        <!-- 操作按钮 -->
        <div data-show="true" class="ant-alert ant-alert-info">
            <span class="ant-alert-message">
            <div>
                <font-awesome-icon icon="fas fa-info-circle" class="ant-alert-icon" />
                当前选择：
                <span style="max-width: 10em;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;font-weight: bold;">{{ homeMenuData.menuName }}</span>
                <a v-if="showMenuInfo && !newMenuInfo" @click="cancelHomeMenuEdit" style="margin-left: 10px;">取消选择</a>
                <div v-if="showMenuInfo && !newMenuInfo" class="menuBtn" @click="deleteHomeMenu" style="float: right;margin-top: -4px;margin-right: 10px;width: 60px;background-color: rgb(245, 108, 108);color: #ffffff;margin-bottom: 10px;">
                <font-awesome-icon icon="far fa-trash-alt" style="margin-right: 5px;" />删除
                </div>
                <div v-if="!showMenuInfo && !newMenuInfo" class="menuBtn" @click="addHomeMenu" style="float: right;margin-top: -4px;margin-right: 10px;width: 60px;background-color: #597ef7;color: #ffffff;margin-bottom: 10px;">
                <font-awesome-icon icon="fas fa-plus-circle" style="margin-right: 5px;" />新建
                </div>
            </div>
            </span>
            <span class="ant-alert-description"></span>
        </div>
        <!--表格-->
        <div v-if="homeMenuList.length">
            <el-table :data="homeMenuList" border ref="table" style="width: 100%" tooltip-effect="light" :height="tabListHeight-40">
            <el-table-column type="index" width="50" label="NO." align="center" fixed="left" >
            </el-table-column>
            <el-table-column label="首页名称" align="left" width="150" :show-overflow-tooltip="true">
                <template #default="scope">
                  <span @click="selectMenu(scope.row)" :title="scope.row.menuName" style="color:#1890FF;cursor:pointer;">
                    <span v-text="scope.row.menuName"></span>
                  </span>
                </template>
            </el-table-column>
            <el-table-column label="首页路径" align="left" minWidth="220" :show-overflow-tooltip="true">
                <template #default="scope">
                <span v-text="scope.row.menuUrl"></span>
                </template>
            </el-table-column>
            <el-table-column label="权限分组" align="left" minWidth="160" :show-overflow-tooltip="true">
                <template #default="scope">
                <span v-if="scope.row.authorityGroupName" v-text="scope.row.authorityGroupName"></span>
                </template>
            </el-table-column>
            <el-table-column label="排序" align="center" width="50" :show-overflow-tooltip="true">
                <template #default="scope">
                <span v-text="scope.row.menuSort"></span>
                </template>
            </el-table-column>
            </el-table>
        </div>
        </div>
        <div class="select-info-right">
        <div class="tabBox">
            <el-tabs v-model="homeInfoTabName">
            <el-tab-pane label="首页详情" name="homefirst"> </el-tab-pane>
            </el-tabs>
            <div class="tabBottom"></div>
        </div>
        <div v-if="homeInfoTabName == 'homefirst'">
            <div class="apiztreeContent">
            <div style="margin:0 auto;margin-left: 20px;">
              <el-form :inline="true" class="demo-form-inline items">
                <el-form-item class="item3">
                    <span class="lableName"><span style="color:red">*</span>首页名称：</span>
                    <el-input v-model="homeMenuData.menuName" placeholder="请输入首页名称"></el-input>
                </el-form-item>
                <el-form-item class="item3">
                    <span class="lableName"><span style="color:red">*</span>首页路径：</span>
                    <el-input v-model="homeMenuData.menuUrl" placeholder="请输入首页路径"></el-input>
                </el-form-item>
                <el-form-item class="item3">
                    <span class="lableName"><span style="color:red">*</span>权限分组：</span>
                    <el-select v-model="homeMenuData.authorityGroupCode" placeholder="请选择首页权限">
                    <el-option
                        v-for="item in authorityGroupList"
                        :key="item.authorityGroupCode"
                        :label="item.authorityGroupName"
                        :value="item.authorityGroupCode"
                    >
                    </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item class="item3">
                    <span class="lableName"><span style="color:white">*</span>排&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;序：</span>
                    <el-input onkeyup="value=value.replace(/[^\d]/g,'')" v-model="homeMenuData.menuSort" placeholder="请输入排序"></el-input>
                </el-form-item>
                </el-form>
            </div>
            <div class="clear"></div>
            <div v-if="showMenuInfo" style="text-align:center; margin-top:30px;overflow:hidden;width:100%;height:50px;">
                <div style="margin-left:calc(50% - 36px)" class="new" @click="saveHomeMenu">
                <font-awesome-icon icon="far fa-save" />保存
                </div>
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
  name: "homeMenuList",
  data() {
    return {
      //导航控制
      homeInfoTabName: 'homefirst',
      //显示首页详情
      showMenuInfo: false,
      //编辑首页
      editMenuInfo: false,
      //新增首页
      newMenuInfo: false,
      //首页必填校验
      homeMenuInfoVerify:{
        menuNameVerify: false,
        menuUrlVerify: false,
        menuStyleVerify: false,
        authorityGroupCodeVerify: false
      },
      homeMenuList: [],
      homeMenuData:{
        menuCode:"",
        menuName:"",
        menuUrl:"",
        menuStyle:"",
        menuType:"folder",
        menuSort:99,
        authorityGroupCode:"",
        authorityGroupName:"",
        parentCode:"",
        level1MenuCode:"",
        level2MenuCode:"",
        level3MenuCode:""
      },
      //弹框
      dialogHomeMenuAuthorityShow: false,
      homeAuthorityGroupInfo: {
        menuCode:"",
        menuAuthorityCode:"",
        menuAuthorityName:"",
        menuAuthorityType:"",
        authorityGroupCode:"",
        authorityGroupName:""
      }
    };
  },
  props: ["authorityGroupList"],
  mounted() {
    this.getHomeMenuList();
  },
  computed: {
    ...mapState(["tabListHeight","yqvSelect"])
  },
  components: {
    
  },
  watch: {
    "homeMenuData.menuName"() {
      if(this.homeMenuData.menuName){
        this.homeMenuInfoVerify.menuNameVerify = false;
      } else {
        this.homeMenuInfoVerify.menuNameVerify = true;
      }
    },
    "homeMenuData.menuUrl"() {
      if(this.homeMenuData.menuUrl){
        this.homeMenuInfoVerify.menuUrlVerify = false;
      } else {
        this.homeMenuInfoVerify.menuUrlVerify = true;
      }
    },
    "homeMenuData.authorityGroupCode"() {
      if(this.homeMenuData.authorityGroupCode){
        this.homeMenuInfoVerify.authorityGroupCodeVerify = false;
      } else {
        this.homeMenuInfoVerify.authorityGroupCodeVerify = true;
      }
    }
  },
  methods: {
    /********************************选择首页*****************************/
    selectMenu(data){
      this.showMenuInfo = true;
      this.newMenuInfo = false;
      this.homeMenuData.menuCode = data.menuCode;
      this.getHomeMenuInfo();
      this.editMenuInfo = true;
    },
    /********************************取消首页选择*****************************/
    cancelHomeMenuEdit(){
      this.showMenuInfo = false;
      this.editMenuInfo = false;
      this.newMenuInfo = false;
      this.homeMenuData.menuCode = null;
      this.homeMenuData.menuName = null;
      this.homeMenuData.menuUrl = null;
      this.homeMenuData.menuStyle = null;
      this.homeMenuData.menuType = null;
      this.homeMenuData.menuSort = null;
      this.homeMenuData.authorityGroupCode = null;
      this.homeMenuData.authorityGroupName = null;
      this.homeMenuData.parentCode = null;
      this.homeMenuData.level1MenuCode = null;
      this.homeMenuData.level2MenuCode = null;
      this.homeMenuData.level3MenuCode = null;
    },
    /********************************添加首页*****************************/
    addHomeMenu(){
      this.showMenuInfo = true;
      this.newMenuInfo = true;
      this.editMenuInfo = false;
      this.homeMenuData.menuCode = null;
      this.homeMenuData.menuName = null;
      this.homeMenuData.menuUrl = null;
      this.homeMenuData.menuStyle = null;
      this.homeMenuData.menuType = "menu";
      this.homeMenuData.menuSort = 99;
      this.homeMenuData.authorityGroupCode = null;
      this.homeMenuData.authorityGroupName = null;
      this.homeMenuData.parentCode = null;
      this.homeMenuData.level1MenuCode = null;
      this.homeMenuData.level2MenuCode = null;
      this.homeMenuData.level3MenuCode = null;
    },
    /********************************查询权限列表*****************************/
    getHomeMenuList() {
      let vm = this;
      let data = {menuLevel:1,menuType:"home"};
      this.$http.post(
          vm.$yqvutil.getContextPath()+`yqvservice/menuconfig/page?currentPage=1&pageSize=1000`,
          data
        )
        .then(function(response) {
          if (response.status == 200) {
            vm.homeMenuList = response.data.result;
          }
        })
        .catch(function(error) {});
    },
    /********************************删除首页*****************************/
    deleteHomeMenu(){
      this.$confirm("此操作将删除当前选择首页, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        closeOnClickModal: false
      })
        .then(() => {
          let vm = this;
          let data = {
            menuCode: vm.homeMenuData.menuCode
          };
          this.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/menuconfig/delete`, data)
            .then(function(response) {
              vm.$message({
                type: "success",
                message: "删除成功!"
              });
              vm.cancelHomeMenuEdit();
              vm.getHomeMenuList();
            })
            .catch(function(error) {});
        })
        .catch(() => {});
    },
    /********************************保存首页*****************************/
    saveHomeMenu(){
      let vm = this;
      let data = {
        menuCode: vm.homeMenuData.menuCode,
        menuName: vm.homeMenuData.menuName,
        menuUrl: vm.homeMenuData.menuUrl,
        menuType: "home",
        menuSort: vm.homeMenuData.menuSort,
        authorityGroupCode: vm.homeMenuData.authorityGroupCode,
        menuLevel:1
      };
      if(!data.menuType){
        data.menuSort=0;
      }
      if(!data.menuSort){
        data.menuSort=99;
      }
      //判断是否可以保存
      if (!vm.homeMenuInfoVerify.menuNameVerify && !vm.homeMenuInfoVerify.menuUrlVerify && !vm.homeMenuInfoVerify.authorityGroupCodeVerify) {
        let message = "新增成功";
        let url = vm.$yqvutil.getContextPath()+"yqvservice/menuconfig/insert";
        if(this.editMenuInfo){
          url = vm.$yqvutil.getContextPath()+"yqvservice/menuconfig/update";
          message = "修改成功";
        }
        this.$http.post(url, data)
          .then(function(response) {
            if (response.status == 200) {
              vm.showMenuInfo = false;
              vm.editMenuInfo = false;
              vm.newMenuInfo = false;
              vm.$message({
                showClose: true,
                message: message,
                type: "success"
              });
              vm.cancelHomeMenuEdit();
              vm.getHomeMenuList();
            }
          })
          .catch(function(error) {});
      } else {
        vm.$message.warning("请输入首页名称!");
      }
    },
    /********************************查询首页信息*****************************/
    getHomeMenuInfo() {
      let vm = this;
      let data = {"menuCode":this.homeMenuData.menuCode};
      this.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/menuconfig/find`,data)
        .then(function(response) {
          if (response.status == 200) {
            vm.homeMenuData.menuCode = response.data.menuCode;
            vm.homeMenuData.menuName = response.data.menuName;
            vm.homeMenuData.menuUrl = response.data.menuUrl;
            vm.homeMenuData.menuStyle = response.data.menuStyle;
            vm.homeMenuData.menuType = response.data.menuType;
            vm.homeMenuData.menuSort = response.data.menuSort;
            vm.homeMenuData.authorityGroupCode = response.data.authorityGroupCode;
            vm.homeMenuData.authorityGroupName = response.data.authorityGroupName;
            vm.homeMenuData.parentCode = response.data.parentCode;
            vm.homeMenuData.level1MenuCode = response.data.level1MenuCode;
            vm.homeMenuData.level2MenuCode = response.data.level2MenuCode;
            vm.homeMenuData.level3MenuCode = response.data.level3MenuCode;
          }
        })
        .catch(function(error) {});
    },
  }
};
</script>

<style scoped lang="less">
.homeMenuList {
  height: calc(100% - 98px);
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
  
  .editHomeMenuList {
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
      width: calc(100% - 400px - 2.3%);
      height: 100%;
      overflow-y: auto;
    }
    .select-info-left2 {
      border: 1px solid rgba(232, 232, 232, 1);
      width: 100%;
      height: 100%;
      overflow-y: auto;
    }
    .select-info-right {
      border: 1px solid rgba(232, 232, 232, 1);
      width: 400px;
      height: 100%;
      overflow-y: auto;
      .el-input {
        width: 270px;
      }
      .el-textarea {
        width: 270px;
      }
      .el-select {
        width: 270px;
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
  }
  .menuBtn{
    height: 28px;
    border-radius: 2px;
    text-align: center;
    line-height: 28px;
    font-size: 14px;
    cursor: pointer;
  }

  .ant-alert{
    box-sizing: border-box;
    margin: 0;
    padding: 0;
    color: rgba(0,0,0,.65);
    font-size: 14px;
    font-variant: tabular-nums;
    line-height: 1.5;
    list-style: none;
    font-feature-settings: "tnum";
    position: relative;
    padding: 8px 15px 8px 15px;
    word-wrap: break-word;
    border-radius: 4px;
    .ant-alert-icon {
      color: #1890ff;
      cursor: pointer;
    }
    .anticon {
      display: inline-block;
      color: inherit;
      font-style: normal;
      line-height: 0;
      text-align: center;
      text-transform: none;
      vertical-align: -0.125em;
      text-rendering: optimizeLegibility;
      -webkit-font-smoothing: antialiased;
      -moz-osx-font-smoothing: grayscale;
    }
    .ant-alert-message {
      color: rgba(0,0,0,.85);
    }
    a {
      color: #1890ff;
      text-decoration: none;
      background-color: transparent;
      outline: none;
      cursor: pointer;
      transition: color .3s;
      -webkit-text-decoration-skip: objects;
    }
  }

  .ant-alert-info{
    background-color: #e6f7ff;
    border: 1px solid #91d5ff;
  }
  .flex-space {
    display: inline;
    height: 33px;
    width: 200px;
  }

}
.el-table ::v-deep(.warning-row) {
  background: #f1f1f1;
}
</style>

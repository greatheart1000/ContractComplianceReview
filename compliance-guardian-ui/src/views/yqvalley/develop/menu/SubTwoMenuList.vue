<template>
  <div class="subTwoMenuList" style="margin-top: -15px;">
    <!-- 修改菜单扩展权限 -->
    <el-dialog
      title="菜单扩展权限"
      v-model="dialogTwoMenuAuthorityShow"
      :close-on-click-modal="false"
      @close="closeTwoMenuAuthorityDialog"
      width="370px"
    >
      <div style="width: 330px;margin:0 auto">
        <div class="form-info">
          <div>
            <div class="form-info-one-left">权限名称</div>
            <div class="form-info-one-right" style="line-height: 32px;margin-top: 10px;width: 210px;">
              <el-input type="text" v-model="twoAuthorityGroupInfo.menuAuthorityName"></el-input>
            </div>
          </div>
          <div class="form-info-two">
            <div class="form-info-two-left" style="line-height: 50px;min-height: 50px;">权限分组</div>
            <div class="form-info-two-right" style="line-height: 50px;min-height: 50px;">
              <el-select v-model="twoAuthorityGroupInfo.authorityGroupCode" placeholder="请选择菜单权限">
                <el-option
                  v-for="item in authorityGroupList"
                  :key="item.authorityGroupCode"
                  :label="item.authorityGroupName"
                  :value="item.authorityGroupCode"
                >
                </el-option>
              </el-select>
            </div>
          </div>
        </div>
      </div>
      <div style="clear:both"></div>
      <template #footer>
        <div class="dialog-footer">
            <el-button @click="dialogTwoMenuAuthorityShow = false">取 消</el-button>
            <el-button type="primary" @click="saveTwoMenuAuthority">确 定</el-button>
        </div>
      </template>
    </el-dialog>
    <!--  -->
    <div class="tabBox">
      <el-tabs v-model="menuTabName" @tab-click="twoMenuClick">
        <el-tab-pane label="二级菜单" name="twotab"> </el-tab-pane>
        <el-tab-pane v-if="threeMenuTab" label="三级菜单" name="threetab"></el-tab-pane>
      </el-tabs>
      <div class="tabBottom"></div>
    </div>
    <div v-if="menuTabName == 'twotab'">
      <div class="editTwoMenuList">
        <div class="contentBox" style="padding: 4px 24px 14px 24px;">
          <div :class="showMenuInfo?'select-info-left':'select-info-left2'">
            <!-- 操作按钮 -->
            <div data-show="true" class="ant-alert ant-alert-info">
              <span class="ant-alert-message">
                <div>
                  <font-awesome-icon icon="fas fa-info-circle" class="ant-alert-icon" />
                  当前选择：
                  <span style="max-width: 10em;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;font-weight: bold;">{{ twoMenuData.menuName }}</span>
                  <a v-if="showMenuInfo && !newMenuInfo" @click="cancelTwoMenuEdit" style="margin-left: 10px;">取消选择</a>
                  <div v-if="showMenuInfo && !newMenuInfo" class="menuBtn" @click="deleteTwoMenu" style="float: right;margin-top: -4px;margin-right: 10px;width: 60px;background-color: rgb(245, 108, 108);color: #ffffff;margin-bottom: 10px;">
                    <font-awesome-icon icon="far fa-trash-alt" style="margin-right: 5px;" />删除
                  </div>
                  <div v-if="showMenuInfo && !showMenuList && !newMenuInfo" class="menuBtn" @click="openTwoSubMenu" style="float: right;margin-top: -4px;margin-right: 10px;width: 60px;background-color: #67c23a;color: #ffffff;margin-bottom: 10px;">
                    <font-awesome-icon icon="fas fa-arrow-circle-down" style="margin-right: 5px;" />子级
                  </div>
                  <div v-if="!showMenuInfo && !newMenuInfo" class="menuBtn" @click="addTwoMenu" style="float: right;margin-top: -4px;margin-right: 10px;width: 60px;background-color: #597ef7;color: #ffffff;margin-bottom: 10px;">
                    <font-awesome-icon icon="fas fa-plus-circle" style="margin-right: 5px;" />新建
                  </div>
                </div>
              </span>
              <span class="ant-alert-description"></span>
            </div>
            <!--表格-->
            <div v-if="twoMenuList.length">
              <el-table :data="twoMenuList" border ref="table" style="width: 100%" tooltip-effect="light" :height="tabListHeight-40">
                <el-table-column type="index" width="50" label="NO." align="center" fixed="left" >
                </el-table-column>
                <el-table-column label="菜单名称" align="left" width="120" :show-overflow-tooltip="true">
                  <template #default="scope">
                    <i v-if="scope.row.menuStyle" :class="scope.row.menuStyle" style="font-size:16px;margin-right: 2px;"></i>
                    <span @click="selectMenu(scope.row)" :title="scope.row.menuName" style="color:#1890FF;cursor:pointer;">
                      <span v-text="scope.row.menuName"></span>
                    </span>
                  </template>
                </el-table-column>
                <el-table-column label="菜单路径" align="left" minWidth="220" :show-overflow-tooltip="true">
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
                <el-table-column label="类型" align="left" width="50" :show-overflow-tooltip="true">
                  <template #default="scope">
                    <span v-text="$yqvutil.getSelectItemName('WALLEY_MENU_TYPE',scope.row.menuType)"></span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
          <div v-if="showMenuInfo" class="select-info-right">
            <div class="tabBox">
              <el-tabs v-model="twoInfoTabName" @tab-click="handleClick">
                <el-tab-pane label="菜单详情" name="twofirst"> </el-tab-pane>
                <el-tab-pane v-if="showMenuList && !newMenuInfo" label="扩展权限" name="twosecond"></el-tab-pane>
              </el-tabs>
              <div class="tabBottom"></div>
            </div>
            <div v-if="twoInfoTabName == 'twofirst'">
              <div class="apiztreeContent">
                <div style="margin:0 auto;margin-left: 20px;">
                  <el-form :inline="true" class="demo-form-inline items">
                    <el-form-item class="item3">
                      <span class="lableName"><span style="color:red">*</span>类&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;型：</span>
                      <el-radio-group class="flex-space" v-model="twoMenuData.menuType">
                        <el-radio v-for="(item, index) in prescriptionList" :disabled="editMenuInfo" :key="index" :label="item.itemCode">{{ item.itemName }}</el-radio>
                      </el-radio-group>
                    </el-form-item>
                    <el-form-item class="item3">
                      <span class="lableName"><span style="color:white">*</span>一级目录：</span>
                      <el-input disabled="disabled" v-model="twoMenuData.level1MenuName"></el-input>
                    </el-form-item>
                    <el-form-item class="item3">
                      <span class="lableName"><span style="color:red">*</span>菜单名称：</span>
                      <el-input v-model="twoMenuData.menuName" placeholder="请输入菜单名称"></el-input>
                    </el-form-item>
                    <el-form-item v-if="showMenuList" class="item3">
                      <span class="lableName"><span style="color:red">*</span>菜单路径：</span>
                      <el-input v-model="twoMenuData.menuUrl" placeholder="请输入菜单路径"></el-input>
                    </el-form-item>
                    <el-form-item v-if="showMenuList" class="item3">
                      <span class="lableName"><span style="color:red">*</span>权限分组：</span>
                      <el-select v-model="twoMenuData.authorityGroupCode" placeholder="请选择菜单权限">
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
                      <el-input onkeyup="value=value.replace(/[^\d]/g,'')" v-model="twoMenuData.menuSort" placeholder="请输入排序"></el-input>
                    </el-form-item>
                  </el-form>
                </div>
                <div class="clear"></div>
                <div style="text-align:center; margin-top:30px;overflow:hidden;width:100%;height:50px;">
                  <div style="margin-left:calc(50% - 36px)" class="new" @click="saveTwoMenu">
                    <font-awesome-icon icon="far fa-save" />保存
                  </div>
                </div>
              </div>
            </div>
            <div v-if="twoInfoTabName == 'twosecond'" class="editFormList">
              <!-- 操作按钮 -->
              <div class="btn-box">
                <div class="new" style="width:60px;" @click="addTwoMenuAuthority">
                  <font-awesome-icon icon="fas fa-plus-circle" />新建
                </div>
              </div>
              <el-table :data="twoMenuAuthorityList" border style="width: 100%" tooltip-effect="light" :height="tabListHeight-90">
                <el-table-column fixed="left" label="操作" width="75" align="left">
                  <template #default="scope">
                    <div @click="editTwoMenuAuthority(scope.row)" type="text" size="small" title="配置">
                      <font-awesome-icon icon="far fa-pen-to-square" style="float: left;color:#33db67;font-size:18px;width: 26px;cursor: pointer;" />
                    </div>
                    <div type="text" size="small" title="删除" @click="deleteTwoMenuAuthority(scope.row)">
                      <font-awesome-icon icon="far fa-trash-alt" style="float: left;color:#f56c6c;font-size:18px;width: 26px;cursor: pointer;" />
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="权限名称" width="120" align="left" :show-overflow-tooltip="true">
                  <template #default="scope">
                    <span v-text="scope.row.menuAuthorityName"></span>
                  </template>
                </el-table-column>
                <el-table-column label="权限分组" align="left" :show-overflow-tooltip="true">
                  <template #default="scope">
                    <span v-text="scope.row.authorityGroupName"></span>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-if="menuTabName == 'threetab'">
      <SubThreeMenuList 
      :level1MenuCode="twoMenuData.level1MenuCode" 
      :level1MenuName="twoMenuData.level1MenuName" 
      :level2MenuCode="twoMenuData.level2MenuCode" 
      :level2MenuName="twoMenuData.level2MenuName"
      :authorityGroupList="authorityGroupList"
      />
    </div>
  </div>
</template>
<script>
import { mapState, mapMutations } from "vuex";
import SubThreeMenuList from "./SubThreeMenuList";
export default {
  name: "subTwoMenuList",
  data() {
    return {
      //菜单列表导航控制
      menuTabName: "twotab",
      twoMenuTab: false,
      threeMenuTab: false,
      //二级菜单详情导航控制
      twoInfoTabName: "twofirst",
      //二级菜单传参
      level2MenuCode: "",
      //显示菜单详情
      showMenuInfo: false,
      //显示菜单为目录
      showMenuList: false,
      //编辑菜单
      editMenuInfo: false,
      //新增菜单
      newMenuInfo: false,
      //二级菜单必填校验
      twoMenuInfoVerify:{
        menuNameVerify: false,
        menuUrlVerify: false,
        menuStyleVerify: false,
        authorityGroupCodeVerify: false
      },
      twoMenuList: [],
      twoMenuAuthorityList: [],
      prescriptionList: [{"itemCode":"menu","itemName":"菜单"},{"itemCode":"folder","itemName":"目录"}],
      prescriptionRadio: "",
      twoMenuData:{
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
        level1MenuName:"",
        level2MenuCode:"",
        level2MenuName:""
      },
      //弹框
      dialogTwoMenuAuthorityShow: false,
      twoAuthorityGroupInfo: {
        menuCode:"",
        menuAuthorityCode:"",
        menuAuthorityName:"",
        menuAuthorityType:"",
        authorityGroupCode:"",
        authorityGroupName:""
      }
    };
  },
  props: ["level1MenuCode","level1MenuName","authorityGroupList"],
  mounted() {
    this.getTwoMenuList();
  },
  computed: {
    ...mapState(["tabListHeight","yqvSelect"])
  },
  components: {
    SubThreeMenuList
  },
  watch: {
    "twoMenuData.menuType"() {
      if(this.twoMenuData.menuType==='folder'){
        this.showMenuList = false;
      } else {
        this.showMenuList = true;
      }
    },
    "twoMenuData.menuName"() {
      if(this.twoMenuData.menuName){
        this.twoMenuInfoVerify.menuNameVerify = false;
      } else {
        this.twoMenuInfoVerify.menuNameVerify = true;
      }
    },
    "twoMenuData.menuUrl"() {
      if(this.twoMenuData.menuUrl){
        this.twoMenuInfoVerify.menuUrlVerify = false;
      } else {
        this.twoMenuInfoVerify.menuUrlVerify = true;
      }
    },
    "twoMenuData.menuStyle"() {
      if(this.twoMenuData.menuStyle){
        this.twoMenuInfoVerify.menuStyleVerify = false;
      } else {
        this.twoMenuInfoVerify.menuStyleVerify = true;
      }
    },
    "twoMenuData.authorityGroupCode"() {
      if(this.twoMenuData.authorityGroupCode){
        this.twoMenuInfoVerify.authorityGroupCodeVerify = false;
      } else {
        this.twoMenuInfoVerify.authorityGroupCodeVerify = true;
      }
    }
  },
  methods: {
    /********************************选择菜单*****************************/
    selectMenu(data){
      this.showMenuInfo = true;
      if(this.twoMenuData.menuType==='folder'){
        this.showMenuList = false;
      } else {
        this.showMenuList = true;
      }
      this.newMenuInfo = false;
      this.twoMenuData.menuCode = data.menuCode;
      this.getTwoMenuInfo();
      this.editMenuInfo = true;
    },
    /********************************取消二级菜单选择*****************************/
    cancelTwoMenuEdit(){
      this.showMenuInfo = false;
      this.showMenuList = false;
      this.editMenuInfo = false;
      this.newMenuInfo = false;
      this.twoMenuData.menuCode = null;
      this.twoMenuData.menuName = null;
      this.twoMenuData.menuUrl = null;
      this.twoMenuData.menuStyle = null;
      this.twoMenuData.menuType = null;
      this.twoMenuData.menuSort = null;
      this.twoMenuData.authorityGroupCode = null;
      this.twoMenuData.authorityGroupName = null;
      this.twoMenuData.parentCode = null;
      this.twoMenuData.level1MenuCode = null;
      this.twoMenuData.level2MenuCode = null;
      this.twoMenuData.level1MenuName = null;
      this.twoMenuData.level2MenuName = null;
    },
    /********************************打开二级子菜单*****************************/
    openTwoSubMenu(){
      this.threeMenuTab = true;
      this.menuTabName = "threetab";
    },
    /********************************添加二级菜单*****************************/
    addTwoMenu(){
      this.showMenuInfo = true;
      this.newMenuInfo = true;
      this.showMenuList = false;
      this.editMenuInfo = false;
      this.twoMenuData.menuCode = null;
      this.twoMenuData.menuName = null;
      this.twoMenuData.menuUrl = null;
      this.twoMenuData.menuStyle = null;
      this.twoMenuData.menuType = "menu";
      this.twoMenuData.menuSort = 99;
      this.twoMenuData.authorityGroupCode = null;
      this.twoMenuData.authorityGroupName = null;
      this.twoMenuData.parentCode = null;
      this.twoMenuData.level1MenuCode = null;
      this.twoMenuData.level2MenuCode = null;
      this.twoMenuData.level1MenuName = null;
      this.twoMenuData.level2MenuName = null;
    },
    /********************************查询权限列表*****************************/
    getTwoMenuList() {
      let vm = this;
      let data = {menuLevel:2,parentCode:vm.level1MenuCode};
      this.$http.post(
          vm.$yqvutil.getContextPath()+`yqvservice/menuconfig/page?currentPage=1&pageSize=1000`,
          data
        )
        .then(function(response) {
          if (response.status == 200) {
            vm.twoMenuList = response.data.result;
          }
        })
        .catch(function(error) {});
    },
    /********************************删除二级菜单*****************************/
    deleteTwoMenu(){
      this.$confirm("此操作将删除当前选择菜单, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        closeOnClickModal: false
      })
        .then(() => {
          let vm = this;
          let data = {
            menuCode: vm.twoMenuData.menuCode
          };
          this.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/menuconfig/delete`, data)
            .then(function(response) {
              vm.$message({
                type: "success",
                message: "删除成功!"
              });
              vm.cancelTwoMenuEdit();
              vm.getTwoMenuList();
            })
            .catch(function(error) {});
        })
        .catch(() => {});
    },
    /********************************保存二级菜单*****************************/
    saveTwoMenu(){
      let vm = this;
      let data = {
        menuCode: vm.twoMenuData.menuCode,
        menuName: vm.twoMenuData.menuName,
        menuUrl: vm.twoMenuData.menuUrl,
        menuStyle: vm.twoMenuData.menuStyle,
        menuType: vm.twoMenuData.menuType,
        menuSort: vm.twoMenuData.menuSort,
        authorityGroupCode: vm.twoMenuData.authorityGroupCode,
        parentCode: vm.level1MenuCode,
        level1MenuCode: vm.level1MenuCode,
        menuLevel:2
      };
      if(!data.menuType){
        data.menuSort=0;
      }
      if(!data.menuSort){
        data.menuSort=99;
      }
      let bo = false;
      if(data.menuType==='folder'){
        data.authorityGroupCode=null;
        data.menuUrl=null;
        if(!vm.twoMenuInfoVerify.menuNameVerify){
          bo = true;
        }
      }else {
        data.menuStyle=null;
        if(!vm.twoMenuInfoVerify.menuNameVerify && !vm.twoMenuInfoVerify.menuUrlVerify && !vm.twoMenuInfoVerify.authorityGroupCodeVerify){
          bo = true;
        }
      }
      //判断是否可以保存
      if (bo) {
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
              vm.showMenuList = false;
              vm.editMenuInfo = false;
              vm.newMenuInfo = false;
              vm.$message({
                showClose: true,
                message: message,
                type: "success"
              });
              vm.cancelTwoMenuEdit();
              vm.getTwoMenuList();
            }
          })
          .catch(function(error) {});
      } else {
        vm.$message.warning("请输入菜单名称!");
      }
    },
    /********************************标签操作*****************************/
    twoMenuClick(row) {
      let labelName = null;
      if(row.label){
        labelName = row.label;
      }
      if(!labelName && row.props){
        labelName = row.props.label;
      }
      if (labelName == "二级菜单") {
        this.menuTabName = "twotab";
        this.twoMenuTab = false;
        this.threeMenuTab = false;
      }else if(labelName == "三级菜单"){
        this.menuTabName = "threetab";
        this.threeMenuTab = false;
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
      if (labelName == "菜单详情") {
        this.twoInfoTabName = "twofirst";
      }else{
        this.twoInfoTabName = "twosecond";
      }
    },
    /********************************查询菜单信息*****************************/
    getTwoMenuInfo() {
      let vm = this;
      let data = {"menuCode":this.twoMenuData.menuCode};
      this.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/menuconfig/find`,data)
        .then(function(response) {
          if (response.status == 200) {
            vm.twoMenuData.menuCode = response.data.menuCode;
            vm.twoMenuData.menuName = response.data.menuName;
            vm.twoMenuData.menuUrl = response.data.menuUrl;
            vm.twoMenuData.menuStyle = response.data.menuStyle;
            vm.twoMenuData.menuType = response.data.menuType;
            vm.twoMenuData.menuSort = response.data.menuSort;
            vm.twoMenuData.authorityGroupCode = response.data.authorityGroupCode;
            vm.twoMenuData.authorityGroupName = response.data.authorityGroupName;
            vm.twoMenuData.parentCode = response.data.parentCode;
            vm.twoMenuData.level1MenuCode = response.data.level1MenuCode;
            vm.twoMenuData.level2MenuCode = response.data.level2MenuCode;
            vm.twoMenuData.level1MenuName = response.data.level1MenuName;
            vm.twoMenuData.level2MenuName = response.data.level2MenuName;
            vm.twoMenuAuthorityList = response.data.menuAuthorityList;
          }
        })
        .catch(function(error) {});
    },
    /********************************添加菜单权限*****************************/
    addTwoMenuAuthority() {
      this.twoAuthorityGroupInfo.menuAuthorityCode = null;
      this.twoAuthorityGroupInfo.menuAuthorityName = null;
      this.twoAuthorityGroupInfo.menuAuthorityType = null;
      this.twoAuthorityGroupInfo.authorityGroupCode = null;
      this.twoAuthorityGroupInfo.authorityGroupName = null;
      this.dialogTwoMenuAuthorityShow = true;
    },
    /********************************编辑菜单权限*****************************/
    editTwoMenuAuthority(val) {
      this.dialogTwoMenuAuthorityShow = true;
      this.twoAuthorityGroupInfo.menuAuthorityCode = val.menuAuthorityCode;
      this.twoAuthorityGroupInfo.menuAuthorityName = val.menuAuthorityName;
      this.twoAuthorityGroupInfo.menuAuthorityType = val.menuAuthorityType;
      this.twoAuthorityGroupInfo.authorityGroupCode = val.authorityGroupCode;
      this.twoAuthorityGroupInfo.authorityGroupName = val.authorityGroupName;
    },
    closeTwoMenuAuthorityDialog() {
      this.dialogTwoMenuAuthorityShow = false;
    },
    /********************************删除菜单权限*****************************/
    deleteTwoMenuAuthority(val) {
      this.$confirm("此操作将删除当前菜单扩展权限, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        closeOnClickModal: false
      })
        .then(() => {
          let vm = this;
          let data = {
            menuAuthorityCode: val.menuAuthorityCode
          };
          this.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/menuconfig/authority/delete`, data)
            .then(function(response) {
              vm.$message({
                type: "success",
                message: "删除成功!"
              });
              vm.getTwoMenuInfo();
            })
            .catch(function(error) {});
        })
        .catch(() => {});
    },
    /********************************保存权限类型*****************************/
    saveTwoMenuAuthority() {
      let vm = this;
      let data = {
        menuAuthorityCode: vm.twoAuthorityGroupInfo.menuAuthorityCode,
        menuAuthorityName: vm.twoAuthorityGroupInfo.menuAuthorityName,
        menuAuthorityType: "2",
        authorityGroupCode: vm.twoAuthorityGroupInfo.authorityGroupCode,
        menuCode: vm.twoMenuData.menuCode
      };
      //判断是否可以保存
      if (data.menuAuthorityName && data.authorityGroupCode) {
        let message = "新增成功";
        let url = vm.$yqvutil.getContextPath()+"yqvservice/menuconfig/authority/insert";
        if(data.menuAuthorityCode){
          url = vm.$yqvutil.getContextPath()+"yqvservice/menuconfig/authority/update";
          message = "修改成功";
        }
        this.$http.post(url, data)
          .then(function(response) {
            if (response.status == 200) {
              vm.dialogTwoMenuAuthorityShow = false;
              vm.$message({
                showClose: true,
                message: message,
                type: "success"
              });
              vm.getTwoMenuInfo();
            }
          })
          .catch(function(error) {});
      } else {
        vm.$message.warning("请输入权限名称和权限分组!");
      }
    }
  }
};
</script>

<style scoped lang="less">
.subTwoMenuList {
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
  
  .editTwoMenuList {
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

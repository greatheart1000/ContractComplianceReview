<template>
  <div class="authorityGroupItem">
    <!-- 修改菜单扩展权限 -->
    <el-dialog
      title="菜单权限"
      v-model="dialogMenuAuthorityShow"
      :close-on-click-modal="false"
      @close="closeMenuAuthorityDialog"
      width="400px"
    >
      <div class="apiztreeContent" style="height:250px">
        <div data-show="true" class="ant-alert ant-alert-info">
          <span class="ant-alert-message">
          <div>
              <font-awesome-icon icon="fas fa-info-circle" class="ant-alert-icon" />
              当前菜单：
              <span style="max-width: 10em;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;font-weight: bold;">{{ currentAuthorityMenu }}</span>
          </div>
          </span>
          <span class="ant-alert-description"></span>
        </div>
        <el-checkbox-group v-model="menuAuthorityArray">
          <el-checkbox v-for="(item, index) in menuAuthorityList" :key="index" :label="item.menuAuthorityCode" style="width: 300px;margin-left: 10px;margin-top: 10px;">{{ item.menuAuthorityName }}</el-checkbox>
        </el-checkbox-group>
      </div>
      <div style="clear:both"></div>
      <template #footer>
        <div class="dialog-footer">
            <el-button @click="dialogMenuAuthorityShow = false">取 消</el-button>
            <el-button type="primary" @click="saveMenuAuthority">确 定</el-button>
        </div>
      </template>
    </el-dialog>
    <div class="contentBox">
      <div class="role-info-left">
        <div class="info-head">角色信息</div>
        <div class="apiztreeContent">
          <div style="margin:0 auto;margin-left: 20px;">
            <el-form :inline="true" class="demo-form-inline items" style="margin-top: 30px;">
              <el-form-item class="item3">
                <span class="lableName"><span style="color:white">*</span>角色编号：</span>
                <el-input style="width:230px" :disabled="edit" v-model="roleData.roleCode" placeholder="请输入角色编号"></el-input>
              </el-form-item>
              <el-form-item class="item3">
                <span class="lableName"><span style="color:red">*</span>角色名称：</span>
                <el-input style="width:230px" v-model="roleData.roleName" placeholder="请输入权限名称"></el-input>
              </el-form-item>
              <el-form-item class="item3">
                <span class="lableName"><span style="color:white">*</span>排&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;序：</span>
                <el-input style="width:230px" onkeyup="value=value.replace(/[^\d]/g,'')" v-model="roleData.roleSort" placeholder="请输入数字"></el-input>
              </el-form-item>
              <el-form-item class="item3">
                <span class="lableName"><span style="color:white">*</span>角色描述：</span>
                <el-input style="width:230px" :rows="2" v-model="roleData.roleDesc" placeholder="请输入权限描述" type="textarea"></el-input>
              </el-form-item>
            </el-form>
          </div>
          <div class="clear"></div>
          <div style="text-align:center; margin-top:30px;overflow:hidden;width:100%;height:50px;">
            <div style="margin-left:calc(50% - 36px)" class="new" @click="saveRoleInfo">
              <font-awesome-icon icon="far fa-save" />保存
            </div>
          </div>
        </div>
      </div>
      <div class="role-info-right">
        <div class="tabBox">
          <el-tabs v-model="activeName" @tab-click="handleClick">
            <el-tab-pane label="菜单信息" name="first"> </el-tab-pane>
            <el-tab-pane label="公共信息" name="second"></el-tab-pane>
          </el-tabs>
          <div class="tabBottom"></div>
        </div>
        <div v-if="activeName == 'first'">
          <el-tree
            :data="apiZtreeData"
            show-checkbox
            node-key="id"
            ref="apiZtree"
            :default-checked-keys="apiZtreeData && apiZtreeData.checkedFlag"
            :props="defaultProps"
            @node-click="handleNodeClickApiZtree"
            @check-change="handleCheckChangeApiZtree"
            @click="getCheckedNodesApiZtree"
          >
          <template #default="{ data }">
            <span class="custom-tree-node" >
                <span>{{data.label}}</span>
                <span v-if="data.handleBtn">
                <font-awesome-icon icon="fas fa-align-left" style="margin-left: 5px; color: red;" />
                </span>
                <span v-if="data.handleBtn && data.handleName">【{{data.handleName}}】</span>
            </span>
          </template>
          </el-tree>
        </div>
        <div v-if="activeName == 'second'" style="display: flex;justify-content: space-between;height: calc(100% - 56px);">
          <div class="public-info-left">
            <div class="info-head">平台首页</div>
            <div class="apiztreeContent">
              <el-checkbox-group v-model="homeMenuArray">
                <el-checkbox v-for="(item, index) in homeMenuList" :key="index" :label="item.menuCode" style="width: 160px;margin-left: 10px;margin-top: 10px;">{{ item.menuName }}</el-checkbox>
              </el-checkbox-group>
            </div>
          </div>
          <div class="public-info-right">
            <div class="info-head">公共权限</div>
            <div class="apiztreeContent">
              <el-checkbox-group v-model="publicAuthorityArray">
                <el-checkbox v-for="(item, index) in publicAuthorityList" :key="index" :label="item.menuAuthorityCode" style="width: 160px;margin-left: 10px;margin-top: 10px;">{{ item.menuAuthorityName }}</el-checkbox>
              </el-checkbox-group>
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
  name: "editAuthorityGroup",
  data() {
    return {
      //导航控制
      activeName: "first",
      //是否编辑模式
      edit: false,
      //树结构组件
      defaultProps: {
        children: "children",
        label: "label"
      },
      //树结构组件渲染数据
      apiZtreeData: [],
      //选中权限控制API
      menuItemList: [],
      //权限数据
      roleData: {
        roleCode: "",
        roleName: "",
        roleDesc: "",
        roleSort: 999,
        rolePriority: 999,
        rmaList:[]
      },
      //首页菜单
      homeMenuList:[],
      homeMenuArray:[],
      //公共权限
      publicAuthorityList:[],
      publicAuthorityArray:[],
      //所有菜单权限
      menuAllAuthorityList: [],
      //当前编辑菜单权限
      menuAuthorityArray:[],
      menuAuthorityList: [],
      currentAuthorityMenu:"",
      //菜单权限弹框
      dialogMenuAuthorityShow: false
    };
  },
  props: ["roleCode"],
  watch: {},
  mounted() {
    this.roleData.roleCode = "";
    this.roleData.roleName = "";
    this.roleData.roleDesc = "";
    this.roleData.roleSort = 999;
    this.roleData.rolePriority = 999;
    this.apiZtreeData = [];
    this.menuItemList = [];
    if(this.roleCode!=null){
      this.edit = true;
      this.roleData.roleCode = this.roleCode;
    }
    this.getRoleInfo();
  },
  methods: {
    getMenuNode(val) {
      this.menuAuthorityList=[];
      if(this.menuAllAuthorityList){
        for(let i=0;i<this.menuAllAuthorityList.length;i++){
          if(this.menuAllAuthorityList[i].menuCode===val.id){
            this.menuAuthorityList.push(this.menuAllAuthorityList[i]);
          }
        }
      }
    },
    /********************************查询权限信息*****************************/
    getRoleInfo() {
      let vm = this;
      let attr = {
        id: "id",
        pId: "pId",
        firstParentCode: "one"
      };
      this.$http.get(vm.$yqvutil.getContextPath()+`yqvservice/role/develop/find?roleCode=${this.roleData.roleCode}`)
        .then(function(response) {
          if (response.status == 200) {
            vm.apiZtreeData = vm.$yqvutil.toMenuTreeData(response.data.ztreeList, attr);
            vm.roleData = response.data;
            vm.publicAuthorityList = response.data.roleMenu.publicAuthorityList;
            if(response.data.roleMenu.publicAuthorityList){
              for(let i=0;i<response.data.roleMenu.publicAuthorityList.length;i++){
                if(response.data.roleMenu.publicAuthorityList[i].checked){
                  vm.publicAuthorityArray.push(response.data.roleMenu.publicAuthorityList[i].menuAuthorityCode);
                }
              }
            }
            vm.homeMenuList = response.data.roleMenu.homeMenuList;
            if(response.data.roleMenu.homeMenuList){
              for(let i=0;i<response.data.roleMenu.homeMenuList.length;i++){
                if(response.data.roleMenu.homeMenuList[i].checked){
                  vm.homeMenuArray.push(response.data.roleMenu.homeMenuList[i].menuCode);
                }
              }
            }
            vm.menuAllAuthorityList = response.data.roleMenu.menuAuthorityList;
          }
        })
        .catch(function(error) {});
    },
    /********************************保存权限信息*****************************/
    saveRoleInfo() {
      let vm = this;
      let data = {
        roleCode: vm.roleData.roleCode,
        roleName: vm.roleData.roleName,
        roleDesc: vm.roleData.roleDesc,
        roleSort: vm.roleData.roleSort,
        rolePriority: vm.roleData.rolePriority,
        rmaList : []
      };
      try {
        vm.getCheckedNodesApiZtree();
      } catch (error) {}
      //获取选中菜单
      if(vm.menuItemList && vm.menuItemList.length>0){
        vm.menuItemList.forEach(item => {
          data.rmaList.push({"menuCode":item.id,"menuAuthorityType":"1"});
        });
      }
      //获取首页
      if(vm.homeMenuArray && vm.homeMenuArray.length>0){
        vm.homeMenuArray.forEach(item => {
          data.rmaList.push({"menuCode":item,"menuAuthorityType":"1"});
        });
      }
      //获取菜单权限
      if(vm.menuAllAuthorityList && vm.menuAllAuthorityList.length>0){
        vm.menuAllAuthorityList.forEach(item => {
          if(item.checked){
            data.rmaList.push({"menuCode":item.menuCode,"menuAuthorityCode":item.menuAuthorityCode,"menuAuthorityType":"2"});
          }
        });
      }
      //获取公共权限
      if(vm.publicAuthorityArray && vm.publicAuthorityArray.length>0){
        vm.publicAuthorityArray.forEach(item => {
          data.rmaList.push({"menuAuthorityCode":item,"menuAuthorityType":"3"});
        });
      }
      //判断是否可以保存
      if (data.roleCode && data.roleName) {
        let message = "新增成功";
        let url = vm.$yqvutil.getContextPath()+"yqvservice/role/insert";
        if(this.edit){
          url = vm.$yqvutil.getContextPath()+"yqvservice/role/update";
          message = "修改成功";
        }
        this.$http.post(url, data)
          .then(function(response) {
            if (response.status == 200) {
              vm.$parent.handleClick({"label":"角色列表"});
              vm.$message({
                showClose: true,
                message: message,
                type: "success"
              });
              vm.$parent.getRoleList();
            }
          })
          .catch(function(error) {});
      } else {
        vm.$message.warning("请输入权限名称!");
      }
    },
    /********************************标签操作*****************************/
    handleClick(row) {
      this.getCheckedNodesApiZtree();
      let labelName = null;
      if(row.label){
        labelName = row.label;
      }
      if(!labelName && row.props){
        labelName = row.props.label;
      }
      if (labelName == "菜单信息") {
        this.activeName = "first";
        this.judgeLabel = false;
      }
    },
    getCheckedNodesApiZtree() {
      this.menuItemList = this.$refs.apiZtree.getCheckedNodes();
    },
    handleCheckChangeApiZtree(data, checked, indeterminate) {
      let vm = this;
      vm.getCheckedNodesApiZtree();
    },
    handleNodeClickApiZtree(data) {
      let node = this.$refs.apiZtree.getCurrentNode();
      this.menuAuthorityList=[];
      this.menuAuthorityArray=[];
      this.currentAuthorityMenu = node.name;
      if(node && node.handleBtn){
        this.dialogMenuAuthorityShow = true;
        if(this.menuAllAuthorityList){
          for(let i=0;i<this.menuAllAuthorityList.length;i++){
            if(this.menuAllAuthorityList[i].menuCode===node.id){
              this.menuAuthorityList.push(this.menuAllAuthorityList[i]);
              if(this.menuAllAuthorityList[i].checked){
                this.menuAuthorityArray.push(this.menuAllAuthorityList[i].menuAuthorityCode);
              }
            }
          }
        }
      }
    },
    closeMenuAuthorityDialog() {
      this.dialogMenuAuthorityShow = false;
    },
    saveMenuAuthority() {
      let node = this.$refs.apiZtree.getCurrentNode();
      if(this.menuAuthorityArray && this.menuAuthorityArray.length>0){
        let aname = null;
        if(this.menuAllAuthorityList){
          this.menuAllAuthorityList.forEach(item => {
            if(item.menuCode===node.id){
              item.checked = false;
            }
          });
          this.menuAllAuthorityList.forEach(item => {
            if(item.menuCode===node.id){
              this.menuAuthorityArray.forEach(item2 => {
                if(item2===item.menuAuthorityCode){
                  item.checked = true;
                  if(aname){
                    aname=aname+"、";
                    aname=aname+item.menuAuthorityName;
                  }else{
                    aname=item.menuAuthorityName;
                  }
                }
              });
            }
          });
        }
        node.handleName=aname;
      }else{
        if(this.menuAllAuthorityList){
          this.menuAllAuthorityList.forEach(item => {
            if(item.menuCode===node.id){
              item.checked = false;
            }
          });
        }
        node.handleName="";
      }
      this.dialogMenuAuthorityShow = false;
    }
  },
  components: {},
  computed: {}
};
</script>

<style scoped lang="less">
.authorityGroupItem {
  height: 100%;
  .contentBox {
    box-sizing: border-box;
    height: 100%;
    display: flex;
    justify-content: space-between;
    padding: 14px 24px 14px 24px;
  }
  .role-info-left {
    border: 1px solid rgba(232, 232, 232, 1);
    width: 360px;
    height: 100%;
    overflow-y: auto;
    .el-input {
      width: 200px;
    }
    .el-textarea {
       width: 200px;
    }
    .el-select {
      width: 200px;
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
  .role-info-right {
    border: 1px solid rgba(232, 232, 232, 1);
    width: calc(100% - 360px - 2.3%);
    height: 100%;
    overflow-y: auto;
  }
  .public-info-left {
    border: 1px solid rgba(232, 232, 232, 1);
    width: calc(50% - 2.3%);
    height: 100%;
    overflow-y: auto;
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
  .public-info-right {
    border: 1px solid rgba(232, 232, 232, 1);
    width: calc(50% - 2.3%);
    height: 100%;
    overflow-y: auto;
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
  .apiztree {
    border: 1px solid rgba(232, 232, 232, 1);
    width: 49%;
    height: 100%;
    overflow-y: auto;
    .apiztreeHead {
      height: 45px;
      line-height: 45px;
      font-size: 16px;
      font-weight: 900;
      padding-left: 10px;
      background: #ecf5ff;
      border-bottom: 1px solid #e8e8e8;
    }
  }
}
</style>

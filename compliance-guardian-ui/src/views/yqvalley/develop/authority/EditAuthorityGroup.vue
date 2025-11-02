<template>
  <div class="authorityGroupItem">
    <div class="contentBox">
      <div class="apiztree">
        <div class="apiztreeHead">权限信息</div>
        <div class="apiztreeContent">
          <div style="margin:0 auto;margin-left: 20px;">
            <el-form :inline="true" class="demo-form-inline items" style="margin-top: 30px;">
              <el-form-item class="item3">
                <span class="lableName"><span style="color:white">*</span>权限编号：</span>
                <el-input style="width:340px" disabled="disabled" v-model="authorityGroupData.authorityGroupCode" placeholder="平台自动生成"></el-input>
              </el-form-item>
              <el-form-item class="item3">
                <span class="lableName"><span style="color:red">*</span>权限名称：</span>
                <el-input style="width:340px" v-model="authorityGroupData.authorityGroupName" placeholder="请输入权限名称"></el-input>
              </el-form-item>
              <el-form-item class="item3">
                <span class="lableName"><span style="color:white">*</span>权限描述：</span>
                <el-input style="width:340px" :rows="2" v-model="authorityGroupData.authorityGroupDesc" placeholder="请输入权限描述" type="textarea"></el-input>
              </el-form-item>
              <el-form-item class="item3">
                <span class="lableName"><span style="color:white">*</span>排&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;序：</span>
                <el-input style="width:340px" onkeyup="value=value.replace(/[^\d]/g,'')" v-model="authorityGroupData.authorityGroupSort" placeholder="请输入数字"></el-input>
              </el-form-item>
            </el-form>
          </div>
          <div class="clear"></div>
          <div style="text-align:center; margin-top:30px;overflow:hidden;width:100%;height:50px;">
            <div style="margin-left:calc(50% - 36px)" class="new" @click="saveGroupAuthority">
              <font-awesome-icon icon="far fa-save" />提交
            </div>
          </div>
        </div>
      </div>
      <div class="apiztree">
        <div class="apiztreeHead">权限控制API</div>
        <div class="apiztreeContent">
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
          </el-tree>
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
      groupItemList: [],
      //权限数据
      authorityGroupData: {
        groupCode: "",
        authorityGroupCode: "",
        authorityGroupName: "",
        authorityGroupDesc: "",
        authorityGroupSort: 999
      },
      
    };
  },
  props: ["groupCode"],
  mounted() {
    this.authorityGroupData.authorityGroupCode = "";
    this.authorityGroupData.authorityGroupName = "";
    this.authorityGroupData.authorityGroupDesc = "";
    this.authorityGroupData.authorityGroupSort = 999;
    this.apiZtreeData = [];
    this.groupItemList = [];
    if(this.groupCode!=null){
      this.authorityGroupData.groupCode = this.groupCode;
      this.edit = true;
    }else{
      this.authorityGroupData.groupCode = "";
    }
    this.getGroupAuthorityInfo();
  },
  methods: {
    /********************************查询权限信息*****************************/
    getGroupAuthorityInfo(val) {
      let vm = this;
      let attr = {
        id: "id",
        pId: "pId",
        rootId: 0
      };
      this.$http.get(this.$yqvutil.getContextPath()+`yqvservice/authority/group/find?groupCode=${this.authorityGroupData.groupCode}`)
        .then(function(response) {
          if (response.status == 200) {
            vm.apiZtreeData = vm.$yqvutil.toTreeData(response.data.ztreeList, attr);
            vm.authorityGroupData = response.data;
          }
        })
        .catch(function(error) {});
    },
    /********************************保存权限信息*****************************/
    saveGroupAuthority(val) {
      let vm = this;
      let data = {
        authorityGroupCode: vm.authorityGroupData.authorityGroupCode,
        authorityGroupName: vm.authorityGroupData.authorityGroupName,
        authorityGroupDesc: vm.authorityGroupData.authorityGroupDesc,
        authorityGroupSort: vm.authorityGroupData.authorityGroupSort,
        itemList : []
      };
      vm.getCheckedNodesApiZtree();
      if(vm.groupItemList && vm.groupItemList.length>0){
        vm.groupItemList.forEach(item => {
          data.itemList.push({"authorityCode":item.id});
        });
      }
      //判断是否可以保存
      if (data.authorityGroupName) {
        let message = "新增成功";
        let url = this.$yqvutil.getContextPath()+"yqvservice/authority/group/insert";
        if(this.edit){
          url = this.$yqvutil.getContextPath()+"yqvservice/authority/group/update";
          message = "修改成功";
        }
        this.$http.post(url, data)
          .then(function(response) {
            if (response.status == 200) {
              vm.$parent.handleClick({"label":"权限列表"});
              vm.$message({
                showClose: true,
                message: message,
                type: "success"
              });
              vm.$parent.getAuthorityGroupList();
            }
          })
          .catch(function(error) {});
      } else {
        vm.$message.warning("请输入权限名称!");
      }
    },
    getCheckedNodesApiZtree() {
      this.groupItemList = this.$refs.apiZtree.getCheckedNodes();
    },
    handleCheckChangeApiZtree(data, checked, indeterminate) {
      let vm = this;
      vm.getCheckedNodesApiZtree();
    },
    handleNodeClickApiZtree(data) {}
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

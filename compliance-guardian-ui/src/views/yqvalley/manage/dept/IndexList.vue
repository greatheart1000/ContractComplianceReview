<template>
  <div class="dataDictionary">
    <div class="content-box">
      <div class="infoHeadBtn">
        <div class="new" @click="addLevel1Dept" style="width: 90px;">
          <font-awesome-icon icon="fas fa-plus-circle" />添加一级
        </div>
        <div class="new" @click="addChildrenDept" v-if="addChildrenShow && selectDept" style="width: 90px;">
          <font-awesome-icon icon="fas fa-plus-circle" />添加下级
        </div>
      </div>
      <div class="content-box-info">
        <div class="dataList">
          <div data-show="true" class="ant-alert ant-alert-info">
            <span class="ant-alert-message">
              <div>
                <font-awesome-icon icon="fas fa-info-circle" class="ant-alert-icon" />
                当前选择：
                <span v-if="selectDept" style="max-width: 10em;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;font-weight: bold;">{{ selectDeptName }}</span>
                <div class="deleteBtn" v-if="selectDept && selectChildrenDept" @click="deleteDeptInfo" style="float: right;">
                  <font-awesome-icon icon="far fa-trash-alt" style="margin-right: 5px;" />删除
                </div>
                <!-- <a v-if="selectDept && selectChildrenDept" @click="deleteDeptInfo" style="float: right;color: red;">删除</a> -->
              </div>
            </span>
            <span class="ant-alert-description"></span>
          </div>
          <div class="ListContent">
            <el-tree
              node-key="id"
              :data="listTreeData"
              :props="defaultProps"
              @node-click="handleNodeClick"
              highlight-current
            >
              <template #default="{ node, data }">
                <span>
                  <font-awesome-icon :icon=data.iconName :class=data.className />
                  <span>{{ node.label }}</span>
                </span>
              </template>
            </el-tree>
          </div>
        </div>
        <div class="dataInfo">
          <div class="tabBox">
            <el-tabs v-model="activeName" @tab-click="handleClick">
              <el-tab-pane label="部门信息" name="first"> </el-tab-pane>
              <el-tab-pane v-if="judgeLabel" label="权限设置" name="second"></el-tab-pane>
            </el-tabs>
            <div class="tabBottom"></div>
          </div>
          <div v-if="activeName == 'first'">
            <div class="infoContent">
              <el-form ref="form" :model="deptData" label-width="95px">
                <el-form-item v-if="!selectLevel1Dept" label="上级部门" style="margin-top: 10px">
                  <el-input v-model="deptData.parentName" :disabled="true"></el-input>
                </el-form-item>
                <el-form-item label="部门编码" style="margin-top: 40px">
                  <el-input v-model="deptData.deptCode" :disabled="true"></el-input>
                </el-form-item>
                <el-form-item label="部门名称" style="margin-top: 40px">
                  <el-input v-model="deptData.deptName"></el-input>
                </el-form-item>
              </el-form>
              <div class="clear"></div>
              <div style="text-align:center; margin-top:30px;overflow:hidden;width:100%;height:50px;">
                <div style="margin-left:calc(50% - 36px)" class="new" @click="saveDeptInfo">
                  <font-awesome-icon icon="far fa-save" />提交
                </div>
              </div>
            </div>
          </div>
          <div v-if="activeName == 'second'" class="authorityBox">
            
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
export default {
  name: "dataDictionary",
  data() {
    return {
      //导航控制
      activeName: "first",
      //判断哪个标签
      judgeLabel: false,
      //部门信息
      deptData: {
        deptCode: "",
        deptName: "",
        parentCode: "",
        parentName: ""
      },
      //树列表信息
      listTreeData: [],
      //是否能添加下一级
      addChildrenShow: true,
      //选中部门是否一级
      selectLevel1Dept: false,
      //选中部门是否有子级
      selectChildrenDept: false,
      //选中部门信息
      selectDept: false,
      selectDeptCode: "",
      selectDeptName: "",
      //树列表控件
      defaultProps: {
        children: "children",
        label: "label"
      },
    };
  },
  methods: {
    /********************************添加一级部门*****************************/
    addLevel1Dept() {
      this.selectDept = false;
      this.selectDeptCode = "";
      this.selectDeptName = "";
      this.deptData.deptCode = "";
      this.deptData.deptName = "";
      this.deptData.parentCode = "";
      this.deptData.parentName = "";
    },
    /********************************添加下级部门*****************************/
    addChildrenDept() {
      if(this.selectDept){
        this.deptData.deptCode = "";
        this.deptData.deptName = "";
        this.deptData.parentCode = this.selectDeptCode;
        this.deptData.parentName = this.selectDeptName;
      }
    },
    /********************************获取所有部门*****************************/
    getDeptTree() {
      let vm = this;
      let attr = {
        id: "id",
        label: "label",
        pId: "pId",
        pName: "pName"
      };
      this.$http.post(vm.$yqvutil.getContextPath()+"yqvservice/dept/ztree", {"deptLevel":1})
        .then(function(response) {
          vm.listTreeData = vm.$yqvutil.toTreeDeptData(response.data, attr,1);
        })
        .catch(function(error) {});
    },
    /********************************保存部门信息*****************************/
    saveDeptInfo() {
      let vm = this;
      let data = {
        deptCode: vm.deptData.deptCode,
        deptName: vm.deptData.deptName,
        parentCode: vm.deptData.parentCode
      };
      if(vm.groupItemList && vm.groupItemList.length>0){
        vm.groupItemList.forEach(item => {
          data.itemList.push({"authorityCode":item.id});
        });
      }
      //判断是否可以保存
      if (data.deptName) {
        let message = "新增成功";
        let url = vm.$yqvutil.getContextPath()+"yqvservice/dept/insert";
        if(data.deptCode){
          url = vm.$yqvutil.getContextPath()+"yqvservice/dept/update";
          message = "修改成功";
        }
        this.$http.post(url, data)
          .then(function(response) {
            if (response.status == 200) {
              vm.$message({
                showClose: true,
                message: message,
                type: "success"
              });
              vm.addLevel1Dept();
              vm.getDeptTree();
            }
          })
          .catch(function(error) {});
      } else {
        vm.$message.warning("请输入权限名称!");
      }
    },
    /********************************删除部门*****************************/
    deleteDeptInfo() {
      if(this.deptData.deptCode){
        this.$confirm("此操作将删除部门, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        closeOnClickModal: false
        })
        .then(() => {
          let vm = this;
          let data = {
            deptCode: vm.deptData.deptCode
          };
          this.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/dept/delete`, data)
            .then(function(response) {
              vm.$message({
                type: "success",
                message: "删除成功!"
              });
              vm.getDeptTree();
            })
            .catch(function(error) {});
        })
        .catch(() => {});
      }
    },
    /********************************树节点点击事件*****************************/
    handleNodeClick(data) {
      if(data.id){
        this.selectDept = true;
        this.selectDeptCode = data.id;
        this.selectDeptName = data.name;
        this.deptData.deptCode = data.id;
        this.deptData.deptName = data.name;
        this.deptData.parentCode = data.pId;
        this.deptData.parentName = data.pName;
        if(this.deptData.parentCode){
          this.selectLevel1Dept = false;
        }else{
          this.selectLevel1Dept = true;
        }
        if(data.children.length>0){
          this.selectChildrenDept = false;
        }else{
          this.selectChildrenDept = true;
        }
        if(data.control==="1" || data.control==="2" || data.control==="3"){
          this.addChildrenShow = true;
        }else{
          this.addChildrenShow = false;
        }
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
      if (labelName == "部门信息") {
        this.activeName = "first";
        this.judgeLabel = false;
      }
    },
  },
  components: {
    
  },
  computed: {},
  mounted() {
    this.getDeptTree();
  },
  watch: {

  }
};
</script>

<style scoped lang="less">
.dataDictionary {
  height: calc(100% - 38px);
  padding: 14px 24px 24px 24px;
  .infoHeadBtn {
    .el-button {
      background: #597ef7;
    }
  }
  .content-box {
    height: 100%;
    display: flex;
    flex-direction: column;
    .content-box-info {
      height: 100%;
      display: flex;
      justify-content: space-between;
      .ListContent ::v-deep(.tree-icon-yellow) {
        margin-right: 6px;
        color: #f5a623;
      }
      .ListContent ::v-deep(.tree-icon-blue) {
        margin-right: 6px;
        color: #93c6ff;
      }
      .dataList {
        box-sizing: border-box;
        border: 1px solid rgba(232, 232, 232, 1);
        width: 49%;
        height: calc(100%-38px);
        overflow-y: auto;
        .ListHead {
          text-align: left;
          height: 42px;
          line-height: 42px;
          font-size: 16px;
          font-weight: 900;
          padding-left: 24px;
          background: rgba(236, 245, 255, 1);
          border-bottom: 1px solid rgba(232, 232, 232, 1);
        }
      }
      .deleteBtn{
        margin-top: -4px;
        margin-right: -10px;
        width: 60px;
        background-color: red;
        float: left;
        margin-bottom: 10px;
        height: 28px;
        border-radius: 2px;
        color: #ffffff;
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
      .light.dataInfo{
        background: #fff;
      }
      .dataInfo {
        width: 49%;
        height: calc(100%-38px);
        box-sizing: border-box;
        border: 1px solid rgba(232, 232, 232, 1);
        overflow-y: auto;
        overflow-x: hidden;
        .infoHeadText {
          text-align: left;
          height: 42px;
          line-height: 42px;
          font-size: 16px;
          font-weight: 900;
          color: #096dd9;
          padding-left: 24px;
          border-bottom: 1px solid rgba(232, 232, 232, 1);
        }
        .infoContent {
          .inputUnique {
            margin-bottom: 48px !important;
          }
          .el-input {
            width: 100%;
            margin-right: 40px;
          }
          .el-autocomplete {
            width: 100%;
            margin-right: 40px;
            input {
              padding-left: 30px !important;
            }
          }
          .el-form-item {
            margin-bottom: 14px;
            height: 28px;
            position: relative;
            .humanIcon {
              color: #096dd9;
              position: absolute;
              top: 10px;
              left: 10px;
              padding-right: 8px;
              font-size: 18px;
            }
            .borderIcon {
              position: absolute;
              border-right: 1px solid #dcdfe6;
              height: 32px;
              top: 2px;
              left: 32px;
            }
            .humanBody {
              position: absolute;
              top: 40px;
              box-sizing: border-box;
              border-radius: 5px;
              padding: 0 10px;
              width: 100%;
              max-height: 200px;
              overflow-y: auto;
              border: 1px solid #dcdfe6;
              li {
                cursor: pointer;
              }
            }
          }
          .textAreaStyle {
            textarea {
              height: 100px !important;
            }
          }
          padding: 10px;
          .el-select {
            width: 100%;
          }
        }
      }
    }
  }
}
</style>

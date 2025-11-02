<template>
  <div class="publicAuthorityList">
      <div class="contentBox" style="padding: 4px 24px 14px 24px;">
        <div :class="'select-info-left'">
        <!-- 操作按钮 -->
        <div data-show="true" class="ant-alert ant-alert-info">
            <span class="ant-alert-message">
            <div>
                <font-awesome-icon icon="fas fa-info-circle" class="ant-alert-icon" />
                当前选择：
                <span style="max-width: 10em;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;font-weight: bold;">{{ publicAuthorityData.menuAuthorityName }}</span>
                <a v-if="showAuthorityInfo && !newAuthorityInfo" @click="cancelPublicAuthorityEdit" style="margin-left: 10px;">取消选择</a>
                <div v-if="showAuthorityInfo && !newAuthorityInfo" class="menuBtn" @click="deletePublicAuthority" style="float: right;margin-top: -4px;margin-right: 10px;width: 60px;background-color: rgb(245, 108, 108);color: #ffffff;margin-bottom: 10px;">
                <font-awesome-icon icon="far fa-trash-alt" style="margin-right: 5px;" />删除
                </div>
                <div v-if="!showAuthorityInfo && !newAuthorityInfo" class="menuBtn" @click="addPublicAuthority" style="float: right;margin-top: -4px;margin-right: 10px;width: 60px;background-color: #597ef7;color: #ffffff;margin-bottom: 10px;">
                <font-awesome-icon icon="fas fa-plus-circle" style="margin-right: 5px;" />新建
                </div>
            </div>
            </span>
            <span class="ant-alert-description"></span>
        </div>
        <!--表格-->
        <div v-if="publicAuthorityList.length">
            <el-table :data="publicAuthorityList" border ref="table" style="width: 100%" tooltip-effect="light" :height="tabListHeight-40">
            <el-table-column type="index" width="50" label="NO." align="center" fixed="left" >
            </el-table-column>
            <el-table-column label="权限名称" align="left" width="150" :show-overflow-tooltip="true">
                <template #default="scope">
                  <span @click="selectAuthority(scope.row)" :title="scope.row.menuAuthorityName" style="color:#1890FF;cursor:pointer;">
                    <span v-text="scope.row.menuAuthorityName"></span>
                  </span>
                </template>
            </el-table-column>
            <el-table-column label="权限分组" align="left" minWidth="160" :show-overflow-tooltip="true">
                <template #default="scope">
                <span v-if="scope.row.authorityGroupName" v-text="scope.row.authorityGroupName"></span>
                </template>
            </el-table-column>
          </el-table>
        </div>
        </div>
        <div class="select-info-right">
        <div class="tabBox">
            <el-tabs v-model="homeInfoTabName">
            <el-tab-pane label="公共权限详情" name="homefirst"> </el-tab-pane>
            </el-tabs>
            <div class="tabBottom"></div>
        </div>
        <div v-if="homeInfoTabName == 'homefirst'">
          <div class="apiztreeContent">
            <div style="margin:0 auto;margin-left: 20px;">
              <el-form :inline="true" class="demo-form-inline items">
                <el-form-item class="item3">
                    <span class="lableName"><span style="color:red">*</span>权限名称：</span>
                    <el-input v-model="publicAuthorityData.menuAuthorityName" placeholder="请输入权限名称"></el-input>
                </el-form-item>
                <el-form-item class="item3">
                    <span class="lableName"><span style="color:red">*</span>权限分组：</span>
                    <el-select v-model="publicAuthorityData.authorityGroupCode" placeholder="请选择权限分组">
                    <el-option
                        v-for="item in authorityGroupList"
                        :key="item.authorityGroupCode"
                        :label="item.authorityGroupName"
                        :value="item.authorityGroupCode"
                    >
                    </el-option>
                    </el-select>
                </el-form-item>
                </el-form>
            </div>
            <div class="clear"></div>
            <div v-if="showAuthorityInfo" style="text-align:center; margin-top:30px;overflow:hidden;width:100%;height:50px;">
                <div style="margin-left:calc(50% - 36px)" class="new" @click="savePublicAuthority">
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
  name: "publicAuthorityList",
  data() {
    return {
      //导航控制
      homeInfoTabName: 'homefirst',
      //显示权限详情
      showAuthorityInfo: false,
      //编辑权限
      editAuthorityInfo: false,
      //新增权限
      newAuthorityInfo: false,
      publicAuthorityList: [],
      publicAuthorityData: {
        menuAuthorityCode:"",
        menuAuthorityName:"",
        authorityGroupCode:"",
        authorityGroupName:""
      }
    };
  },
  props: ["authorityGroupList"],
  mounted() {
    this.getPublicAuthorityList();
  },
  computed: {
    ...mapState(["tabListHeight","yqvSelect"])
  },
  components: {
    
  },
  methods: {
    /********************************选择权限*****************************/
    selectAuthority(data){
      this.showAuthorityInfo = true;
      this.newAuthorityInfo = false;
      this.publicAuthorityData.menuAuthorityCode = data.menuAuthorityCode;
      this.publicAuthorityData.menuAuthorityName = data.menuAuthorityName;
      this.publicAuthorityData.menuAuthorityType = data.menuAuthorityType;
      this.publicAuthorityData.authorityGroupCode = data.authorityGroupCode;
      this.publicAuthorityData.authorityGroupName = data.authorityGroupName;
      this.editAuthorityInfo = true;
    },
    /********************************取消权限选择*****************************/
    cancelPublicAuthorityEdit(){
      this.showAuthorityInfo = false;
      this.editAuthorityInfo = false;
      this.newAuthorityInfo = false;
      this.publicAuthorityData.menuAuthorityCode = null;
      this.publicAuthorityData.menuAuthorityName = null;
      this.publicAuthorityData.menuAuthorityType = null;
      this.publicAuthorityData.authorityGroupCode = null;
      this.publicAuthorityData.authorityGroupName = null;
    },
    /********************************添加权限*****************************/
    addPublicAuthority(){
      this.showAuthorityInfo = true;
      this.newAuthorityInfo = true;
      this.editAuthorityInfo = false;
      this.publicAuthorityData.menuAuthorityCode = null;
      this.publicAuthorityData.menuAuthorityName = null;
      this.publicAuthorityData.menuAuthorityType = null;
      this.publicAuthorityData.authorityGroupCode = null;
      this.publicAuthorityData.authorityGroupName = null;
    },
    /********************************查询权限列表*****************************/
    getPublicAuthorityList() {
      let vm = this;
      this.$http.get(vm.$yqvutil.getContextPath()+`yqvservice/menuconfig/publicauthority`)
        .then(function(response) {
          if (response.status == 200) {
            vm.publicAuthorityList = response.data;
          }
        })
        .catch(function(error) {});
    },
    /********************************删除权限*****************************/
    deletePublicAuthority(){
      this.$confirm("此操作将删除当前菜单扩展权限, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        closeOnClickModal: false
      })
        .then(() => {
          let vm = this;
          let data = {
            menuAuthorityCode: vm.publicAuthorityData.menuAuthorityCode
          };
          this.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/menuconfig/authority/delete`, data)
            .then(function(response) {
              vm.$message({
                type: "success",
                message: "删除成功!"
              });
              vm.cancelPublicAuthorityEdit();
              vm.getPublicAuthorityList();
            })
            .catch(function(error) {});
        })
        .catch(() => {});
    },
    /********************************保存权限*****************************/
    savePublicAuthority(){
      let vm = this;
      let data = {
        menuAuthorityCode: vm.publicAuthorityData.menuAuthorityCode,
        menuAuthorityName: vm.publicAuthorityData.menuAuthorityName,
        menuAuthorityType: "3",
        authorityGroupCode: vm.publicAuthorityData.authorityGroupCode
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
              vm.$message({
                showClose: true,
                message: message,
                type: "success"
              });
              vm.cancelPublicAuthorityEdit();
              vm.getPublicAuthorityList();
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
.publicAuthorityList {
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

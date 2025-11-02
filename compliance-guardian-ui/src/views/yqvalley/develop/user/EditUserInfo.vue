<template>
  <div class="setUser">
    <div class="editFormList">
      <div style="width: 680px;margin:0 auto">
        <div class="abox">
          <span class="abox-lableName"><span>*</span>用户账号：</span>
          <div class="input-box">
            <el-input :disabled="edit" maxlength="20" v-model="userData.valleyAccount" placeholder="请填写用户账号"></el-input>
          </div>
          <div class="error-point" v-if="valleyAccountVerify">请填写4-20位的用户账号</div>
        </div>
        <div class="abox">
          <span class="abox-lableName"><span>*</span>用户姓名：</span>
          <div class="input-box">
            <el-input maxlength="20" v-model="userData.userName" placeholder="请填写用户姓名"></el-input>
          </div>
          <div class="error-point" v-if="userNameVerify">请填写用户姓名</div>
        </div>
        <div class="abox">
          <span class="abox-lableName"><span>*</span>手机号码：</span>
          <div class="input-box">
            <el-input maxlength="11" v-model="userData.phoneNumber" placeholder="请填写手机号码"></el-input>
          </div>
          <div class="error-point" v-if="phoneNumberVerify">请填写正确的手机号码</div>
        </div>
        <div class="abox">
          <span class="abox-lableName"><span>*</span>联系邮箱：</span>
          <div class="input-box">
            <el-input maxlength="20" v-model="userData.mailAddress" placeholder="请填写联系邮箱"></el-input>
          </div>
          <div class="error-point" v-if="mailAddressVerify">请填写正确的联系邮箱</div>
        </div>
        <div class="abox">
          <span class="abox-lableName">汇报用户：</span>
          <div class="input-box">
            <UserSelect val="reportAccount" :showData="reportAccount" />
          </div>
        </div>
        <div class="abox">
          <span class="abox-lableName">有效截止：</span>
          <div class="input-box">
            <el-date-picker v-model="userData.validEndDate" type="date" style="width:100%;" placeholder="请选择日期"></el-date-picker>
          </div>
        </div>
        <div class="abox" style="width: 100%;">
          <span class="abox-lableName"><span>*</span>所属部门：</span>
          <div class="input-box">
            <el-select
                v-model="userData.deptCode"
                placeholder="请选择所属部门"
                style="width:100%"
                clearable
              >
              <el-option
                v-for="item in $parent.deptList"
                :key="item.deptCode"
                :label="item.completeName"
                :value="item.deptCode"
              >
              </el-option>
            </el-select>
          </div>
          <div class="error-point" v-if="deptCodeVerify">请选择所属部门</div>
        </div>
        <div class="abox" style="width: 100%;">
          <span class="abox-lableName"><span>*</span>用户角色：</span>
          <div class="input-box">
            <el-select
                v-model="roleCodeList"
                placeholder="请选择角色"
                style="width:100%"
                clearable
                multiple
                collapse-tags
              >
              <el-option
                v-for="item in roleList"
                :key="item.roleCode"
                :label="item.roleName"
                :value="item.roleCode"
              >
              </el-option>
            </el-select>
          </div>
          <div class="error-point" v-if="roleListVerify">请选择角色</div>
        </div>
        <div class="clear"></div>
        <div style="text-align:center; margin-top:30px;overflow:hidden;width:100%;height:50px;">
          <div style="margin-left:calc(50% - 36px)" class="new" @click="submitUserInfo">
            <font-awesome-icon icon="far fa-save" />提交
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import UserSelect from "@/components/yqvalley/UserSelect";
import { mapState, mapMutations } from "vuex";
export default {
  name: "editUserInfo",
  data() {
    return {
      //是否编辑模式
      edit: false,
      //部门选择数据
      deptList: [],
      //角色选择数据
      roleList: [],
      //角色选中数据
      roleCodeList: [],
      //汇报用户组件数据
      reportAccount: "",
      //用户表单数据必填校验
      valleyAccountVerify: false,
      userNameVerify: false,
      phoneNumberVerify: false,
      mailAddressVerify: false,
      deptCodeVerify: false,
      roleListVerify: false,
      //用户表单数据
      userData: {
        valleyAccount: "",
        userName: "",
        phoneNumber: "",
        mailAddress: "",
        reportAccount: "",
        validEndDate: null,
        deptCode: "",
        roleList: []
      }
    };
  },
  props: ["accountData"],
  computed: {
    ...mapState([])
  },
  components: {
    UserSelect
  },
  mounted() {
    this.userData.validEndDate=new Date(); 
    this.userData.validEndDate.setFullYear(this.userData.validEndDate.getFullYear()+1); 
    this.userData.validEndDate.setDate(this.userData.validEndDate.getDate()-1);
    this.getRoleList();
    if(this.accountData!=null){
      this.edit = true;
      this.getUserInfo();
    }
  },
  watch: {
    "userData.valleyAccount"() {
      this.verifyUserInfo('valleyAccount');
    },
    "userData.userName"() {
      this.verifyUserInfo('userName');
    },
    "userData.phoneNumber"() {
      this.verifyUserInfo('phoneNumber');
    },
    "userData.mailAddress"() {
      this.verifyUserInfo('mailAddress');
    },
    "userData.deptCode"() {
      this.verifyUserInfo('deptCode');
    },
    roleCodeList() {
      this.verifyUserInfo('roleList');
    }
  },
  methods: {
    //获取所有角色
    getRoleList() {
      let vm = this;
      this.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/user/develop/rolelist`,{})
        .then(function(response) {
          if (response.status == 200) {
            vm.roleList = response.data;
          }
        })
        .catch(function(error) {});
    },
    //获取用户信息
    getUserInfo() {
      let vm = this;
      let data = {"valleyAccount":this.accountData};
      this.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/user/find`,data)
        .then(function(response) {
          if (response.status == 200) {
            sessionStorage.setItem("yqv_user_select_"+response.data.reportAccount,response.data.reportName);
            vm.reportAccount=response.data.reportAccount;
            vm.userData.valleyAccount = response.data.valleyAccount;
            vm.userData.userName = response.data.userName;
            vm.userData.phoneNumber = response.data.phoneNumber;
            vm.userData.mailAddress = response.data.mailAddress;
            vm.userData.validEndDate = new Date(response.data.validEndDate);
            vm.userData.deptCode = response.data.deptCode;
            vm.roleCodeList = [];
            if(response.data.roleList && response.data.roleList.length>0){
              response.data.roleList.forEach(role => {
                vm.roleCodeList.push(role.roleCode);
              });
            }
          }
        })
        .catch(function(error) {});
    },
    verifyUserInfo(type) {
      let data = this.userData;
      data.reportAccount=this.reportAccount;
      data.roleList = [];
      if(this.roleCodeList && this.roleCodeList.length>0){
        this.roleCodeList.forEach(code => {
          data.roleList.push({"roleCode":code});
        });
      }
      //判断是否填电子邮箱
      if(type=="all" || type=="mailAddress"){
        if (!data.mailAddress) {
          this.mailAddressVerify = true;
        } else {
          let filter = /^([a-zA-Z0-9_.-])+@(([a-zA-Z0-9-])+.)+([a-zA-Z0-9]{2,4})+$/;
          if (!filter.test(data.mailAddress)) {
            this.mailAddressVerify = true;
          } else {
            this.mailAddressVerify = false;
          }
        }
      }
      //判断是否填手机号码
      if(type=="all" || type=="phoneNumber"){
        if (!data.phoneNumber) {
          this.phoneNumberVerify = true;
        } else {
          let myreg = /^[1][3,4,5,6,7,8,9][0-9]{9}$/;
          if (!myreg.test(data.phoneNumber)) {
            this.phoneNumberVerify = true;
          } else {
            this.phoneNumberVerify = false;
          }
        }
      }
      //判断是否填写用户账号
      if(type=="all" || type=="valleyAccount"){
        if (!data.valleyAccount || data.valleyAccount.length<4) {
          this.valleyAccountVerify = true;
        } else {
          this.valleyAccountVerify = false;
        }
      }
      //判断是否填写用户姓名
      if(type=="all" || type=="userName"){
        if (!data.userName) {
          this.userNameVerify = true;
        } else {
          this.userNameVerify = false;
        }
      }
      //判断是否选择部门
      if(type=="all" || type=="deptCode"){
        if (!data.deptCode) {
          this.deptCodeVerify = true;
        } else {
          this.deptCodeVerify = false;
        }
      }
      //判断是否选择部门
      if(type=="all" || type=="roleList"){
        if (!data.roleList || data.roleList.length==0) {
          this.roleListVerify = true;
        } else {
          this.roleListVerify = false;
        }
      }
    },
    //提交
    submitUserInfo() {
      this.verifyUserInfo("all");
      let data = this.userData;
      data.reportAccount=this.reportAccount;
      data.roleList = [];
      if(this.roleCodeList && this.roleCodeList.length>0){
        this.roleCodeList.forEach(code => {
          data.roleList.push({"roleCode":code});
        });
      }
      //判断是否可以保存
      if (
        !this.valleyAccountVerify &&
        !this.userNameVerify &&
        !this.phoneNumberVerify &&
        !this.mailAddressVerify &&
        !this.deptCodeVerify &&
        !this.roleListVerify
      ) {
        let message = "新增成功";
        let url = this.$yqvutil.getContextPath()+"yqvservice/user/develop/insert";
        if(this.edit){
          url = this.$yqvutil.getContextPath()+"yqvservice/user/update";
          message = "修改成功";
        }
        let vm = this;
        this.$http.post(url, data)
          .then(function(response) {
            if (response.status == 200) {
              vm.$parent.handleClick({"label":"用户列表"});
              vm.$parent.getUserList();
              vm.$message({
                type: "success",
                message: message
              });
            }
          })
          .catch(function(error) {});
      }
    }
  }
};
</script>

<style scoped lang="less">
.setUserClass {
  .lableName {
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
}
</style>

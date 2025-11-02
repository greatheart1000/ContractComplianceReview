<template>
  <div class="passwordModify">
    <div class="passwordModify-box">
      <div class="passwordModify-box-right">
        <div class="abox">
          <span class="abox-lableName"><span>*</span>用户账号：</span>
          <div class="input-box">
            <el-input
              v-model="list.valleyAccount"
              maxlength="20"
              placeholder="请填写用户姓名"
              :disabled="true"
            ></el-input>
          </div>
          <div class="abox">
            <span class="abox-lableName"><span>*</span>旧密码：</span>
            <div class="input-box">
              <el-input
                v-model="list.oldPassword"
                maxlength="20"
                placeholder="请填写旧密码"
                :type="showPasswordOld ? 'text' : 'password'"
              ></el-input>
              <font-awesome-icon :icon="showPasswordOld ? 'fas fa-eye' : 'fas fa-eye-slash'"
                @click="showPasswordOld = !showPasswordOld" aria-hidden="true"/>
            </div>
            <div class="error-point" v-if="judgeOld">请填写旧密码</div>
          </div>

          <div class="abox">
            <span class="abox-lableName"><span>*</span>新密码：</span>
            <div class="input-box">
              <el-input
                maxlength="20"
                v-model="list.newPasswordOne"
                placeholder="请填写新密码"
                :type="showPasswordNewOne ? 'text' : 'password'"
              >
              </el-input>
              <font-awesome-icon :icon="showPasswordNewOne ? 'fas fa-eye' : 'fas fa-eye-slash'"
                v-if="list.newPasswordOne"
                @click="showPasswordNewOne = !showPasswordNewOne"
                aria-hidden="true" />
            </div>
            <div class="error-point" v-if="judgeNewOne">
              {{ newPasswordOneText }}
            </div>
          </div>
          <div class="abox">
            <span class="abox-lableName"><span>*</span>确认密码：</span>
            <div class="input-box">
              <el-input
                maxlength="20"
                v-model="list.newPasswordTwo"
                placeholder="请填写新密码"
                :type="showPasswordNewTwo ? 'text' : 'password'"
              ></el-input>
              <font-awesome-icon :icon="showPasswordNewTwo ? 'fas fa-eye' : 'fas fa-eye-slash'"
                v-if="list.newPasswordTwo"
                @click="showPasswordNewTwo = !showPasswordNewTwo"
                aria-hidden="true" />
            </div>
            <div class="error-point" v-if="judgeNewTwo">
              {{ newPasswordTwoText }}
            </div>
          </div>
        </div>
        <div class="clear"></div>
        <div
          style="
            text-align: right;
            margin-top: 20px;
            overflow: hidden;
            width: 93%;
            height: 50px;
          "
        >
          <div style="margin-left: 50%" class="new" @click="storage">
            <font-awesome-icon icon="far fa-save" />保存
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapMutations } from "vuex";
export default {
  name: "passwordModify",
  data() {
    return {
      judgeOld: false,
      judgeNewOne: false,
      judgeNewTwo: false,
      newPasswordOneText: "请填写新密码",
      newPasswordTwoText: "请填写新密码",
      list: {
        valleyAccount: "",
        oldPassword: "",
        newPasswordOne: "",
        newPasswordTwo: "",
      },
      showPasswordOld: false,
      showPasswordNewOne: false,
      showPasswordNewTwo: false,
    };
  },
  computed: {
    ...mapState(["userInfo"]),
  },
  mounted() {
    let obj = this.list;
    obj.valleyAccount = this.userInfo.valleyAccount;
  },
  watch: {
    //userInfo
    userInfo() {
      let obj = this.list;
      obj.valleyAccount = this.userInfo.valleyAccount;
    },
    //判断旧密码
    "list.oldPassword"() {
      this.testOldPassword();
    },
    "list.newPasswordOne"() {
      this.testnewPasswordOne();
    },
    "list.newPasswordTwo"() {
      this.testnewPasswordOne();
    },
  },
  methods: {
    storage() {
      let obj = this.list;
      let vm = this;
      //判断是否填写新密码
      this.testOldPassword();
      this.testnewPasswordOne();
      if (!this.judgeOld && !this.judgeNewOne && !this.judgeNewTwo) {
        let data = {
          loginPwd: this.list.oldPassword,
          loginPwdNew: this.list.newPasswordOne,
        };
        this.$http.get(vm.$yqvutil.getContextPath()+`yqvservice/user/pwd/updateinit`)
        .then((response) => {
          if (response.status == 200) {
            data.loginPwd = this.$yqvutil.getPSRsaCode(data.loginPwd,response.data.data)
            data.loginPwdNew = this.$yqvutil.getPSRsaCode(data.loginPwdNew,response.data.data)
            this.$http.post(vm.$yqvutil.getContextPath()+"yqvservice/user/pwd/update", data)
            .then(function (response) {
              if (response.status == 200) {
                vm.$router.push({ name: "login" });
                vm.$yqvutil.removeToken();
              }
            }).catch(function (error) {});
          }
        }).catch((error) => {});
      }
    },
    //判断旧密码
    testOldPassword() {
      if (!this.list.oldPassword) {
        this.judgeOld = true;
      } else {
        this.judgeOld = false;
      }
    },
    //判断新密码
    testnewPasswordOne() {
      let patrnValue = this.$yqvutil.getAppConfigValue("valley_security_password_pattern_value");
      if(!patrnValue){
        patrnValue = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*()_+`\\-={}:\";'<>?,.\\/]).{8,16}$";
      }
      let failDesc = this.$yqvutil.getAppConfigValue("valley_security_password_pattern_desc");
      if(!failDesc){
        failDesc = "用户密码至少8-16位长度并同时包含字母、数字、特殊字符";
      }
      let patrn = new RegExp(patrnValue);
      if (!patrn.test(this.list.newPasswordOne)) {
        this.judgeNewOne = true;
        this.newPasswordOneText = failDesc;
      } else {
        this.judgeNewOne = false;
      }
      if (!patrn.test(this.list.newPasswordTwo)) {
        this.judgeNewTwo = true;
        this.newPasswordTwoText = failDesc;
      } else {
        this.judgeNewTwo = false;
      }
      if (
        patrn.test(this.list.newPasswordOne) &&
        patrn.test(this.list.newPasswordTwo)
      ) {
        if (this.list.newPasswordOne === this.list.newPasswordTwo) {
        } else {
          this.judgeNewTwo = true;
          this.newPasswordTwoText = "新密码必须相同";
          this.judgeNewOne = true;
          this.newPasswordOneText = "新密码必须相同";
        }
      }
    },
  },
};
</script>

<style  lang="less">
.passwordModify {
  width: 100%;
  height: 100%;
  .passwordModify-box {
    width: 850px;
    margin: 0 auto;
    height: 100%;
    .passwordModify-box-left {
      width: 400px;
      height: 100%;
      float: left;
      img {
        width: 280px;
        height: 208px;
        margin-top: 40px;
        margin-left: 50px;
      }
    }
    .passwordModify-box-right {
      width: 341px;
      height: 100%;
      overflow: hidden;
      //float: left;
      margin: 0px auto;
      .abox {
        margin-top: 28px;
        width: 100%;
        float: left;
        position: relative;
        .abox-lableName {
          width: 76px;
          float: left;
          line-height: 32px;
          span {
            color: red;
            font-size: 14px;
          }
        }
        .error-point {
          position: absolute;
          left: 76px;
          top: 35px;
          color: red;
          font-size: 12px;
        }
        .input-box {
          float: left;
          width: calc(100% - 101px);
          margin-right: 25px;
          position: relative;
          .el-select,
          .el-select--small,
          .el-date-editor {
            width: 100%;
          }
          .svg-inline--fa {
            position: absolute;
            right: 10px;
            top: 9px;
            cursor: pointer;
          }
        }
      }
    }
  }
}
</style>

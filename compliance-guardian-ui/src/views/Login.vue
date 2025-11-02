<template>
  <div id="login">
    <div class="login-img"></div>
    <div class="login-box">
      <div class="login-box-logo">
        <img src="@/assets/img/titleicon1.svg" />
      </div>
      <div class="login-box-nav" style="height: 25px"></div>
      <div class="login-box-content">
        <div class="login-box-content-userName">
          <el-input class="login-input" v-model="userName" placeholder="请输入账号"></el-input>
          <div class="point-userName" v-if="judgeUserName">请输入账号</div>
        </div>
        <div class="login-box-content-password">
          <el-input
            v-model="password"
            placeholder="请输入密码"
            type="password"
            class="login-input"
          ></el-input>
          <div class="point-password" v-if="judgePassword">请输入密码</div>
        </div>

        <div class="login-box-content-yzm">
          <el-input
            @keyup.enter="loginBtn"
            v-model="picLyanzhengma"
            placeholder="请输入验证码"
            class="login-input"
          ></el-input>
          <div v-if="judgeYzm" class="point-userName">{{ judgeText }}</div>
          <div v-if="!serviceCaptcha" class="yzm-box" @click="createCode">{{ checkCode }}</div>
          <div v-if="serviceCaptcha" class="img-box" @click="refreshImage();">
            <img title="看不清楚?点击图片刷新验证码" 
              alt="点击图片刷新"
              style="cursor: pointer;width: 100%;height: 100%;cursor:pointer;" 
              :src="serviceCaptchaBase64" />
          </div>
        </div>
        <div class="login-box-content-btn">
          <div @click="loginBtn" class="contact">登录</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "UserLogin",
  data() {
    return {
      //是否使用服务器验证码
      serviceCaptcha:false,
      serviceCaptchaUrl:null,
      serviceCaptchaBase64:null,
      activeIndex: "1",
      userName: "",
      password: "",
      judgeUserName: false,
      judgePassword: false,
      judgeYzm: false,
      judgeText: "请输入验证码",
      picLyanzhengma: "",
      checkCode: "",
    };
  },
  created() {
    window.isPasswordsExpiredDate = false;
    if(this.serviceCaptcha){
      this.refreshImage();
    }else{
      this.createCode();
    }
  },
  methods: {
    refreshImage(){
      let vm = this;
      vm.$http
        .get(vm.$yqvutil.getContextPath()+`yqvservice/valley/captcha/image`)
        .then((response) => {
          if (response.status == 200) {
            if (response.data.code == 1) {
              vm.serviceCaptchaBase64 = response.data.data;
            }
          }
        })
        .catch((error) => {});
    },
    //验证码
    createCode() {
      //先清空验证码的输入
      this.code = "";
      this.checkCode = "";
      this.picLyanzhengma = "";
      //验证码的长度
      let codeLength = 4;
      //随机数
      let random = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9];
      //
      for (let i = 0; i < codeLength; i++) {
        //取得随机数的索引（0~35）
        let index = Math.floor(Math.random() * 10);
        //根据索引取得随机数加到code上
        this.code += random[index];
      }
      //把code值赋给验证码
      this.checkCode = this.code;
    },
    judge() {
      if (this.userName == "") {
        this.judgeUserName = true;
      }
      if (this.password == "") {
        this.judgePassword = true;
      }
      if(this.serviceCaptcha){
        if (this.picLyanzhengma) {
          if(this.picLyanzhengma.length == 4){
            this.judgeYzm = false;
          }else if(this.picLyanzhengma.length > 4){
            this.judgeYzm = true;
            this.judgeText = "验证码长度错误";
          }
        }else{
          this.judgeYzm = true;
          this.judgeText = "请输入验证码";
        }
      }else{
        if (!this.picLyanzhengma) {
          this.judgeYzm = true;
          this.judgeText = "请输入验证码";
        }
        if (this.picLyanzhengma.length < 4) {
          this.judgeYzm = true;
          this.judgeText = "验证码错误";
        }
      }
    },
    loginBtn() {
      this.$http
        .get(this.$yqvutil.getContextPath()+`yqvservice/valley/init`)
        .then((response) => {
          if (response.status == 200) {
            //判断账号密码是否填写
            this.judge();
            //有账号密码时触发登录
            if (!this.judgeYzm && !this.judgeUserName && !this.judgePassword) {
              let vm = this;
              let data = {
                loginAccount: this.userName,
                loginPwd: this.$yqvutil.getRsaCode(this.password),
                //验证码
                captcha: this.picLyanzhengma,
              };
              this.$http
                .post(this.$yqvutil.getContextPath()+`yqvservice/valley/login`, data)
                .then(function (response) {
                  if (response.data.code == 1) {
                    vm.$yqvutil.updateToken(response.data.data.token);
                    if(response.data.data.defaultPwd){
                      vm.$router.push({
                        name: "YqvUserPassword",
                      });
                    }else if (response.data.data.defaultHome) {
                      vm.$yqvutil.setDefaultHome(response.data.data.defaultHome);
                      location.href = location.origin + "/#" + response.data.data.defaultHome;
                    } else {
                      location.href = location.origin + "/#/index";
                    }
                  } else {
                    if(vm.serviceCaptcha){
                      vm.refreshImage();
                    }
                    vm.$message({
                      showClose: true,
                      message: response.data.message,
                      type: "error",
                    });
                  }
                })
                .catch(function (error) {
                  if (error.code == 0) {
                    vm.$message({
                      showClose: true,
                      message: error.message,
                      type: "error",
                    });
                  }
                });
            }
          }
        })
        .catch((error) => {});
    },
  },
  mounted() {},
  watch: {
    userName() {
      if (this.userName == "") {
        this.judgeUserName = true;
      } else {
        this.judgeUserName = false;
      }
    },
    password() {
      if (this.password == "") {
        this.judgePassword = true;
      } else {
        this.judgePassword = false;
      }
    },
    picLyanzhengma() {
      if(this.serviceCaptcha){
        if (this.picLyanzhengma) {
          if(this.picLyanzhengma.length == 4){
            this.judgeYzm = false;
          }else if(this.picLyanzhengma.length > 4){
            this.judgeYzm = true;
            this.judgeText = "验证码长度错误";
          }
        }
      }else{
        if (this.picLyanzhengma.length > 3) {
          if (this.picLyanzhengma == this.checkCode) {
            this.judgeYzm = false;
          } else {
            this.judgeYzm = true;
            this.judgeText = "验证码错误";
          }
        }
      }
    },
  },
};
</script>

<style lang="less">
#login {
  width: 100%;
  height: 100%;
  position: relative;
  background: #0d47a1;
  //background-image: url("@/assets/img/login.png");
  .login-input {
    .el-input__inner {
      height: 34px !important;
    }
    .el-input__wrapper{
      height: 40px !important;
    }
  }
  .login-img {
    width: 742px;
    height: 517px;
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    right: 0;
    margin: auto;
  }
  .login-box {
    width: 400px;
    height: 370px;
    background: #ffffff;
    position: absolute;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    margin: auto;
    .login-box-logo {
      height: 70px;
      width: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      img {
        margin-top: 24px;
        width: 200px;
      }
    }
    .login-box-nav {
      width: 100%;
      height: 61px;
      .is-active {
        color: #597ef7;
      }
      .el-menu.el-menu--horizontal {
        border-bottom: none;
      }
    }
    .login-box-content {
      .login-box-content-userName,
      .login-box-content-password,
      .login-box-content-yzm {
        padding: 0px 50px;
        position: relative;
        input {
          width: 300px;
          height: 40px;
        }
      }
      .point-password,
      .point-userName {
        position: absolute;
        top: 45px;
        left: 50px;
        color: red;
      }
      .login-box-content-password,
      .login-box-content-yzm {
        margin-top: 24px;
        .yzm-box {
          font-size: 25px;
          width: 80px;
          height: 38px;
          line-height: 40px;
          text-align: center;
          border-top-right-radius: 4px;
          border-bottom-right-radius: 4px;
          position: absolute;
          top: 1px;
          left: 283px;
          cursor: pointer;
          background: #f1f1f1;
        }
        .img-box{
          height: 40px;
          width: 90px;
          top: 1px;
          left: 270px;
          cursor: pointer;
          border-top-right-radius: 4px;
          border-bottom-right-radius: 4px;
          position: absolute;
        }
      }
      .login-box-content-title {
        padding-left: 295px;
        color: #1890ff;
        font-size: 14px;
        margin-top: 10px;
        div {
          cursor: pointer;
        }
      }
      .login-box-content-btn {
        padding: 0px 50px;
        .contact {
          width: 300px;
          height: 40px;
          background: #1f9f3a;
          color: #ffffff;
          border-radius: 5px;
          line-height: 40px;
          text-align: center;
          margin-top: 24px;
          cursor: pointer;
        }
      }
    }
  }
  .yanzhengma_input {
    font-family: "Exo 2", sans-serif;
    border: 1px solid #fff;
    color: #fff;
    outline: none;
    border-radius: 12px;
    letter-spacing: 1px;
    font-size: 17px;
    font-weight: normal;
    background-color: rgba(82, 56, 76, 0.15);
    padding: 5px 0 5px 10px;
    margin-left: 30px;
    height: 30px;
    margin-top: 25px;
    border: 1px solid #e6e6e6;
  }
  .verification {
    border-radius: 12px;
    width: 100px;
    letter-spacing: 5px;
    margin-left: 50px;
    height: 40px;
    transform: translate(-15px, 0);
  }
  .captcha {
    height: 50px;
    text-align: justify;
  }
}
</style>

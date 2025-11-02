<template>
  <div class="Modify">
    <!--上传头像-->
    <div class="Modify-left">
      <el-upload
        class="avatar-uploader"
        :action="$yqvutil.getContextPath()+'fileservice/user/upload/face'"
        :headers="myHeaders"
        :show-file-list="false"
        :on-success="handleAvatarSuccess"
        :on-error="handleAvatarError"
        :before-upload="beforeAvatarUpload"
      >
        <img
          v-if="currentUserData.imageUrl"
          :src="currentUserData.imageUrl"
          class="avatar"
        />
        <font-awesome-icon icon="fas fa-user-tie" class="avatar-uploader-icon" v-else />
      </el-upload>
      <div class="photo-point" v-if="judgeImageUrl">请上传照片</div>
    </div>
    <div class="Modify-right">
      <div class="abox">
        <div class="abox-lableName">登录账号：</div>
        <div class="input-box">
          <el-input
            v-model="currentUserData.loginAccount"
            maxlength="20"
            :disabled="true"
          ></el-input>
        </div>
      </div>
      <div class="abox">
        <div class="abox-lableName">用户账号：</div>
        <div class="input-box">
          <el-input
            v-model="currentUserData.valleyAccount"
            maxlength="20"
            :disabled="true"
          ></el-input>
        </div>
      </div>
      <div class="abox">
        <span class="abox-lableName">用户姓名：</span>
        <div class="input-box">
          <el-input
            v-model="currentUserData.userName"
            maxlength="20"
            :disabled="true"
          ></el-input>
        </div>
      </div>
      <div class="abox">
        <span class="abox-lableName">汇报用户：</span>
        <div class="input-box">
          <el-input
            v-model="currentUserData.reportName"
            maxlength="20"
            :disabled="true"
          ></el-input>
        </div>
      </div>
      <div class="abox">
        <span class="abox-lableName"><span>*</span>电子邮箱：</span>
        <div class="input-box">
          <el-input
            v-model="currentUserData.mailAddress"
            maxlength="50"
            placeholder="请填写电子邮箱"
          ></el-input>
        </div>
        <div class="error-point" v-if="judgeEmail">请填写正确的电子邮箱</div>
      </div>
      <div class="abox">
        <span class="abox-lableName"><span>*</span>手机号码：</span>
        <div class="input-box">
          <el-input
            maxlength="11"
            v-model="currentUserData.phoneNumber"
            placeholder="请填写手机号码"
          ></el-input>
        </div>
        <div class="error-point" v-if="judgeMobileCode">
          请填写正确的手机号码
        </div>
      </div>
      <div class="abox">
        <span class="abox-lableName">密码过期：</span>
        <div class="input-box">
          <el-input
            v-model="currentUserData.loginPwdValidDayStr"
            maxlength="20"
            :disabled="true"
          ></el-input>
        </div>
      </div>
      <div class="abox">
        <span class="abox-lableName">账号过期：</span>
        <div class="input-box">
          <el-input
            v-model="currentUserData.validEndDateStr"
            maxlength="20"
            :disabled="true"
          ></el-input>
        </div>
      </div>
      <div class="abox" style="width: 100%">
        <span class="abox-lableName">所属部门：</span>
        <div class="input-box">
          <el-input
            v-model="currentUserData.completeName"
            maxlength="20"
            :disabled="true"
          ></el-input>
        </div>
      </div>
      <div class="abox" style="width: 100%">
        <span class="abox-lableName">拥有角色：</span>
        <div class="input-box">
          <el-input
            v-model="currentUserData.roleNames"
            maxlength="20"
            :disabled="true"
          ></el-input>
        </div>
      </div>
    </div>
    <div class="clear"></div>
    <div
      style="
        text-align: center;
        margin-top: 30px;
        overflow: hidden;
        width: 100%;
        height: 50px;
      "
    >
      <div style="margin-left: calc(50% - 36px)" class="new" @click="storage">
        <font-awesome-icon icon="far fa-save" />保存
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapMutations } from "vuex";
import defaultImgM from "@/assets/img/userM.jpg";
export default {
  name: "Modify",
  data() {
    return {
      setImgM: defaultImgM,
      judgeImageUrl: false,
      judgeSex: false,
      judgeEmail: false,
      judgeMobileCode: false,
      currentUserData: {
        imageUrl: "",
        valleyAccount: "",
        loginAccount: "",
        userName: "",
        mailAddress: "",
        phoneNumber: "",
        reportName: "",
        faceFileId: null,
        validEndDateStr: null,
        loginPwdValidDayStr: "",
        validBeginDate: "",
        validEndDate: "",
        completeName: "",
        roleNames: "",
      },
      pickerOptions: {
        disabledDate: (time) => {
          return time.getTime() > Date.now();
        },
      },
    };
  },
  mounted() {
    this.give();
  },
  computed: {
    ...mapState(["userInfo"]),
    myHeaders() {
      return {
        Authorization: this.$yqvutil.getToken(),
      };
    },
  },
  methods: {
    give() {
      let data = {};
      let vm = this;
      let obj = this.currentUserData;
      this.$http
        .get(vm.$yqvutil.getContextPath()+"yqvservice/user/current", data)
        .then(function (response) {
          if (response.status == 200) {
            let userData = response.data;
            obj.loginAccount = userData.loginAccount;
            obj.valleyAccount = userData.valleyAccount;
            obj.userName = userData.userName;
            obj.completeName = userData.completeName;
            obj.roleNames = userData.roleNames;
            obj.mailAddress = userData.mailAddress;
            obj.phoneNumber = userData.phoneNumber;
            obj.faceFileId = userData.faceFileId;
            obj.reportName = userData.reportName;
            if (userData.loginPwdValidDay) {
              obj.loginPwdValidDayStr = userData.loginPwdValidDay + "天后过期";
            }
            if (userData.validEndDate) {
              obj.validEndDateStr = vm.$yqvutil.setDateFormat(
                userData.validEndDate
              );
            }
            if (userData.faceFileId) {
              obj.imageUrl =vm.$yqvutil.getContextPath()+"fileservice/user/download/face";
            }
          }
        })
        .catch(function (error) {
          if (error.code == 0) {
          }
        });
    },
    //注销
    logout() {
      let vm = this;
      this.$http
        .get(vm.$yqvutil.getContextPath()+`yqvservice/valley/logout`)
        .then(function (response) {
          if (response.status == 200) {
            vm.$router.push({ name: "login" });
            vm.$yqvutil.removeToken();
          } else {
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
    },
    //确定
    storage() {
      let obj = this.currentUserData;
      //判断是否填电子邮箱
      if (!obj.mailAddress) {
        this.judgeEmail = true;
      } else {
        let filter =
          /^([a-zA-Z0-9_.-])+@(([a-zA-Z0-9-])+.)+([a-zA-Z0-9]{2,4})+$/;
        if (!filter.test(obj.mailAddress)) {
          this.judgeEmail = true;
        } else {
          this.judgeEmail = false;
        }
      }
      //判断是否填手机号码
      if (!obj.phoneNumber) {
        this.judgeMobileCode = true;
      } else {
        let myreg = /^[1][3,4,5,6,7,8,9][0-9]{9}$/;
        if (!myreg.test(obj.phoneNumber)) {
          this.judgeMobileCode = true;
        } else {
          this.judgeMobileCode = false;
        }
      }
      //判断是否上传图片
      if (!obj.imageUrl) {
        this.judgeImageUrl = false;
      } else {
        this.judgeImageUrl = false;
      }
      //判断是否可以保存
      if (
        !this.judgeImageUrl &&
        !this.judgeUserName &&
        !this.judgeMobileCode &&
        !this.judgeEmail
      ) {
        let data = this.currentUserData;
        let vm = this;
        this.$http
          .post(vm.$yqvutil.getContextPath()+"yqvservice/user/current/update", data)
          .then(function (response) {
            if (response.status == 200) {
              vm.$confirm("修改成功，是否需要重新登录?", "提示", {
                confirmButtonText: "确定",
                cancelButtonText: "取消",
                type: "success",
                closeOnClickModal: false,
              })
                .then(() => {
                  vm.logout();
                })
                .catch(() => {
                  vm.give();
                });
            }
          })
          .catch(function (error) {});
      }
    },
    handleAvatarSuccess(res, file) {
      this.currentUserData.imageUrl = URL.createObjectURL(file.raw);
      this.currentUserData.faceFileId = res.fileId;
    },
    handleAvatarError(err, file, fileList) {
      this.$message.error(JSON.parse(err.message).message);
    },
    beforeAvatarUpload(file) {
      const isJPG = file.type === "image/jpeg";
      const isLt2M = file.size / 1024 < 100;
      if (!isJPG) {
        this.$message.error("上传头像图片只能是 JPG 格式!");
      }
      if (!isLt2M) {
        this.$message.error("上传头像图片大小不能超过 100KB!");
      }
      return isJPG && isLt2M;
    },
  },
};
</script>

<style  lang="less">
.Modify {
  width: 100%;
  .el-form--inline .el-form-item {
    margin-right: 20px;
    margin-left: 20px;
  }
  .el-dialog__body {
    padding: 20px 0px 20px 0px;
  }
  .Modify-left {
    float: left;
    width: 350px;
    height: 100%;
    .photo-point {
      margin-left: 142px;
      color: red;
      font-size: 12px;
      height: 30px;
      line-height: 30px;
    }
    .upload-title {
      position: absolute;
      bottom: 10px;
      left: 83px;
      color: #666666;
    }
    .avatar-uploader .el-upload {
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      margin-left: 60px;
      margin-top: 25px;
    }
    .avatar-uploader .el-upload:hover {
      border-color: #409eff;
    }
    .avatar-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 220px;
      height: 220px;
      line-height: 220px;
      text-align: center;
    }
    .avatar {
      width: 220px;
      height: 220px;
      display: block;
    }
  }
  .Modify-right {
    float: left;
    width: calc(100% - 350px);
    height: 100%;
    .abox {
      margin-top: 28px;
      width: 50%;
      height: 32px;
      float: left;
      position: relative;
      .error-point {
        position: absolute;
        left: 76px;
        top: 35px;
        color: red;
        font-size: 12px;
      }
      .abox-lableName {
        width: 76px;
        float: left;
        line-height: 32px;
        span {
          color: red;
          font-size: 14px;
        }
      }
      .input-box {
        float: left;
        width: calc(100% - 101px);
        margin-right: 25px;
        .el-select,
        .el-select--small,
        .el-date-editor {
          width: 100%;
        }
      }
    }
  }
  .Modify-table {
    margin-left: 25px;
    margin-right: 25px;
    margin-top: 30px;
  }
}
</style>

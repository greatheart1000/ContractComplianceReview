<template>
  <div class="header dark">
    <div class="header-left" @click="menuClick">
      <font-awesome-icon icon="fas fa-outdent" style="font-size: 20px;" v-if="!menuLinkFlag"/>
      <font-awesome-icon icon="fas fa-indent" style="font-size: 20px;" v-else />
    </div>
    <el-dropdown
      v-if="homeList && homeList.length > 1"
      class="header-left"
      style="cursor: pointer"
    >
      <font-awesome-icon icon="fas fa-home" style="font-size: 20px;" class="more-menu" />
      <template #dropdown>
        <el-dropdown-menu v-for="item in homeList" :key="item.menuCode">
          <el-dropdown-item @click="$yqvutil.routerToUrl(item.menuUrl)">{{
            item.menuName
          }}</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
    <span style="float: left;margin: 16px 5px;font-size: 18px;font-weight: 600;">/</span>
    <div class="header-breadcrumb">
      <div v-if="JSON.stringify($route.params) == '{}'">
        <div v-for="(itema, index) in navigationNameList" :key="index">
          <div style="float: left; margin: -2px 5px;">
            <span v-if="index > 0" style="font-size: 18px;font-weight: 600;">/</span>
          </div>
          <div style="float: left;">
            {{ itema }}
          </div>
        </div>
      </div>
    </div>
    <div class="header-right">
      <div class="user-name" 
        style="float: right;display: inline-flex;position: relative;" 
        @mouseenter="userSettingShow=true" 
        @mouseleave="userSettingShow=false">
        <span style="font-weight: 600;">{{
          userInfo.userName
        }}</span>
        <div v-if="userSettingShow" class="user-info-show">
          <ul class="user-info-ul dark">
            <li class="li-click" @click="$yqvutil.routerToMenu('YqvUserInfo')">
              <div class="li-btn">
                <font-awesome-icon icon="fas fa-address-card" style="margin-right: 6px;font-size: 16px;" />
                <span>个人信息</span>
              </div>
            </li>
            <li class="li-click" @click="$yqvutil.routerToMenu('YqvUserPassword')">
              <div class="li-btn">
                <font-awesome-icon icon="fas fa-key" style="margin-right: 6px;font-size: 16px;" />
                <span>修改密码</span>
              </div>
            </li>
            <li class="divider"></li>
            <li>
              <div class="logout-btn" @click="userLogout">
                退出登录
              </div>
            </li>
          </ul>
        </div>
      </div>
      <div class="userImg" 
        style="cursor: pointer;"
        @mouseenter="userSettingShow=true" 
        @mouseleave="userSettingShow=false">
        <el-avatar :size="35" :src="setImgM" />
      </div>
      <div style="width: 34px;height: 34px;margin-top: 8px;float: right;cursor: pointer;" @click="toggleScreenfull">
        <el-tooltip content="全屏" placement="bottom" effect="light" >
          <font-awesome-icon icon="fas fa-arrows-alt" style="font-size: 20px;vertical-align: middle;cursor: pointer;" />
        </el-tooltip>
      </div>
      <div style="width: 34px;height: 34px;margin-top: 2px;float: right;cursor: pointer;margin-right: 20px;">
        <Dark/>
      </div>
    </div>
  </div>
</template>

<script>
import { mapState, mapMutations } from "vuex";
import defaultImgM from "@/assets/img/userM.jpg";
import headerApi from "@/yqvapi/header/header.api";
import screenfull from "screenfull"
import Dark from "@/components/yqvalley/Dark";
export default {
  name: "HeaderInfo",
  data() {
    return {
      setImgM: defaultImgM,
      userSettingShow:false,
      navigationNameList: [],
    };
  },
  computed: {
    ...mapState(["homeList", "userInfo", "menuLinkFlag","navigationNameMap","footerExcludeHeight"]),
  },
  watch: {
    //监听路由变化
    $route(to, from) {
      this.navigationNameList = this.$yqvutil.setNavigationName(this.$route.path);
    },
    navigationNameMap() {
      this.navigationNameList = this.$yqvutil.setNavigationName(this.$route.path);
    },
    userInfo() {
      if (this.userInfo.faceFileId) {
        this.setImgM = headerApi.getImgUrl(this.userInfo.faceFileId);
      }
    },
  },
  created: function () {
    this.navigationNameList = this.$yqvutil.setNavigationName(this.$route.path);
    this.setHeight();
  },
  components: {
    Dark
  },
  mounted() {
    window.addEventListener("beforeunload", () => {
      sessionStorage.setItem("path", this.$route.path);
    });
    window.addEventListener("resize", () => {
      //调整内容最小高度
      const contentDiv = document.getElementsByClassName("system-content");
      if(contentDiv && contentDiv.length>0){
        const height = contentDiv[0].scrollHeight;
        let pageContentDiv = document.getElementsByClassName('page-content');
        if(pageContentDiv && pageContentDiv.length>0){
          pageContentDiv[0].style.minHeight = (height-this.footerExcludeHeight)+"px";
        }
      }
    });
  },
  methods: {
    ...mapMutations(["changeMenu", "setHeight"]),
    menuClick() {
      this.changeMenu();
    },
    userSettingShowClick() {
      if(this.userSettingShow){
        this.userSettingShow=false;
      }else{
        this.userSettingShow=true;
      }
    },
    //全屏设置
    toggleScreenfull () {
      if (screenfull.isEnabled) {
        if (screenfull.isFullscreen) {
          // 关闭全屏
          screenfull.exit();
        } else {
          // 打开全屏
          const screenfullDiv = document.getElementById("screenfull-content");
          screenfull.request(screenfullDiv);
        }
      } else {
        this.$message.warning({
          content: '当前浏览器暂不支持全屏模式，请切换浏览器后重新尝试！',
          duration: 3
        })
      }
    },
    userLogout() {
      let vm = this;
      this.$confirm("此操作将退出登录, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        closeOnClickModal: false,
      })
        .then(() => {
          headerApi.userLogout(function (response) {
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
        })
        .catch(() => {});
    },
  },
};
</script>

<style scoped lang="less">
html.light{
  .header {
    //background-color: #fff;
    //background-color: #1890ff;
    background-color: #4eb4e4fc;
    //color: #000;
    color: #fff;
    ul.dark {
      background-color: #f0f2f5;
    }
    .el-dropdown.header-left{
      color: #fff;
    }
  }
}
.header-right ::v-deep(sup) {
  right: 0;
}
.loginOut-head {
  margin-left: 10px;
}
.header {
  width: 100%;
  height: 50px;
  .header-left {
    float: left;
    margin-left: 24px;
    margin-top: 15px;
    i {
      cursor: pointer;
      font-size: 20px;
    }
  }
  .header-breadcrumb {
    float: left;
    margin-top: 18px;
    width: 250px;
    height: 32px;
  }
  .header-right {
    .user-name {
      font-size: 14px;
      line-height: 42px;
      cursor: pointer;
      margin-right: 30px;
      margin-left: 10px;
    }
    float: right;
    margin-top: 8px;
    width: 400px;
    .badge-icon {
      float: right;
      margin-top: 13px;
      margin-right: 40px;
      cursor: pointer;
    }
    .goOut {
      width: 80px;
      color: rgba(0, 0, 0, 0.65);
      float: right;
      height: 42px;
      line-height: 42px;
      i {
        cursor: pointer;
      }
    }
    .highLight {
      width: 104px;
      height: 24px;
      line-height: 24px;
      text-align: center;
      border-radius: 2px;
      color: #5675f5;
      border: 1px solid rgba(217, 217, 217, 1);
      margin-top: 8px;
      margin-right: 30px;
    }
    .userName {
      height: 42px;
      float: right;
      line-height: 42px;
      margin-right: 30px;
      margin-left: 10px;
      color: rgba(0, 0, 0, 0.65);
    }
    .userImg {
      width: 34px;
      height: 34px;
      margin-top: 3px;
      float: right;
      img {
        width: 100%;
        height: 100%;
        border-radius: 50%;
      }
    }
    .user-info-show {
      display: block;
      position: absolute;
      box-sizing: border-box;
      transition: all 300ms ease-in-out 0s;
      color: var(--cb-color-text-primary,#333);
      border: 1px solid var(--cb-color-border-tertiary,#ebebeb);
      //background-color: var(--cb-color-bg-primary,#fff);
      box-shadow: 0 4px 8px 0 var(--cb-color-shadow,rgba(0,0,0,0.16));
      z-index: 99;
      width: 180px;
      top: 100%;
      right: -28px;
      margin-top: -2px;
      ul {
        //background-color: #f0f2f5;
        li{
          height: 40px;
          .li-btn{
            margin-left: 12px;
          }
        }
        .li-click:hover {
          color: #f0f2f5;
          text-decoration: none;
          background-color: #5675f5;
        }
        .li-click{
          cursor: pointer;
        }
        .divider {
          height: 2px;
          overflow: hidden;
          background-color: #d4cfcf;
        }
        .logout-btn:hover{
          color: #f0f2f5;
          text-decoration: none;
          background-color: #5675f5;
        }
        .logout-btn{
          width: 70px;
          background:#4c4b4e;
          height: 28px;
          border-radius: 2px;
          color: #ffffff;
          text-align: center;
          line-height: 28px;
          font-size: 14px;
          cursor: pointer;
          margin: 10px auto;
        }
      }
    }
  }
}
.revise-box {
  position: fixed;
  top: 0;
  right: 0;
  left: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  z-index: 1000;
  .revise-box-min {
    width: 100%;
    height: 100%;
    margin-top: 54px;
    background: #fff;
    .interval-top {
      width: 100%;
      height: 36px;
    }
    .revise-box-content {
      width: 586px;
      height: 310px;
      border: 1px solid rgba(232, 232, 232, 1);
      margin: auto;
      .revise-box-content-title {
        height: 22px;
        font-size: 16px;
        color: #5675f5;
        line-height: 22px;
        margin-top: 24px;
        text-align: center;
      }
      .revise-box-content-point {
        height: 22px;
        font-size: 14px;
        color: rgba(0, 0, 0, 0.65);
        line-height: 22px;
        margin-top: 19px;
        text-align: center;
        i {
          color: #f5a623;
          margin-right: 10px;
        }
      }
      .interval-bottom {
        width: 100%;
        height: 29px;
      }
      .revise-box-content-submit {
        width: 320px;
        margin: auto;
        height: 32px;
        background: #597ef7;
        color: #fff;
        text-align: center;
        line-height: 32px;
        border-radius: 2px;
        cursor: pointer;
      }
    }
  }
}
.abox {
  margin-top: 28px;
  width: 60%;
  margin-left: 120px;
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
    .el-select,
    .el-select--small,
    .el-date-editor {
      width: 100%;
    }
  }
}
</style>

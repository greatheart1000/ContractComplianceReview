<template>
  <div class="system-box">
    <div class="system-content">
      <div class="system-content-left" ref="menuLink">
        <div class="menuHead">
          <img :src="imgUrl" alt="logo-head" ref="menuLinkImg" @click="goDefaultHome"/>
        </div>
        <MenuLink :navDatas="navData" />
      </div>
      <div class="system-content-right">
        <HeaderDark />
        <div id="screenfull-content" class="page-content">
          <div class="system-content-min-add">
            <div
              class="content-min-box"
              ref="contentMinBox"
              style="overflow-y: hidden"
            >
              <div style="overflow-y: auto; height: 100%" class="scrollClass dark">
                <router-view></router-view>
              </div>
            </div>
          </div>
        </div>
        <div class="system-content-footer">
					<div class="footer-content">
						<span class="bigger-120">
							<span class="blue bolder">刘进会</span>
              &copy;2021-{{ doHandleYear }}
						</span>
						&nbsp; &nbsp;
						<span class="blue bolder">粤ICP备17146157号</span>
						&nbsp;&nbsp;
					</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import HeaderDark from "@/components/yqvalley/HeaderDark";
import MenuLink from "@/components/yqvalley/MenuLinkDark";
import { mapState, mapMutations } from "vuex";
import headerApi from "@/yqvapi/header/header.api";
export default {
  name: "box",
  data() {
    return {
      routerBox: "",
      routerChild: "",
      navigationNameList: [],
      navData: [],
      doHandleYear: "",
      imgUrl: require("@/assets/img/titleicon4.svg"),
    };
  },
  components: {
    HeaderDark,
    MenuLink
  },
  computed: {
    ...mapState([
      "menuLinkFlag",
      "userInfo",
      "navigationNameMap",
      "isPasswordsExpiredDate",
    ]),
  },
  created() {
    this.doHandleYear = new Date().getFullYear();
    this.navigationNameList = this.$yqvutil.setNavigationName(this.$route.path);
    this.initHeader();
  },
  watch: {
    //监听路由变化
    $route(to, from) {
      this.navigationNameList = this.$yqvutil.setNavigationName(this.$route.path);
    },
    navigationNameMap() {
      this.navigationNameList = this.$yqvutil.setNavigationName(this.$route.path);
    },
    //显示隐藏菜单栏
    menuLinkFlag(newVla, oldVal) {
      if (newVla) {
        this.$refs.menuLink.style.flex = "0 0 64px";
        this.$refs.menuLinkImg.style.width = "38px";
        this.$refs.contentMinBox.style.width = "calc(100%-65px)";
        this.imgUrl = require("@/assets/img/icon2.svg");
      } else {
        this.$refs.menuLink.style.flex = "0 0 180px";
        this.$refs.menuLinkImg.style.width = "150px";
        this.imgUrl = require("@/assets/img/titleicon4.svg");
      }
    },
  },
  methods: {
    ...mapMutations([
      "changeMenu",
      "setUserInfo",
      "setHomeList",
      "setNavigationText",
      "setYqvSelect",
      "setYqvConfig",
      "setYqvAuthority",
    ]),
    goDefaultHome() {
      location.href = location.origin + "/#" + this.$yqvutil.getDefaultHome();
    },
    //初始化信息
    initHeader() {
      let vm = this;
      headerApi.getHeaderInfo(function (response) {
        if (response.status == 200) {
          let data = response.data;
          //获取登录名
          vm.setUserInfo(data.userData);
          vm.setHomeList(data.homeList);
          vm.setYqvSelect(data.otherMap["select"]);
          vm.setYqvConfig(data.otherMap["config"]);
          vm.setYqvAuthority(data.otherMap["authority"]);
          let menuNameMap = vm.$yqvutil.getMenuNameMap(data.menuList,data.homeList);
          vm.setNavigationText(menuNameMap);
          //获取导航数据
          vm.navData = vm.convertMenuData(data.menuList);
          vm.checkPasswordUpdate(data.userData);
        } else {
          vm.$message({
            showClose: true,
            message: response.data.message,
            type: "error",
          });
        }
      });
    },
    convertMenuData(menuList) {
      let levelOneList = menuList.filter((menu) => {
        return menu.menuLevel == 1;
      });
      let levelTwoList = menuList.filter((menu) => {
        return menu.menuLevel == 2;
      });
      let levelThreeList = menuList.filter((menu) => {
        return menu.menuLevel == 3;
      });
      let levelTwoData = [];
      for (let i = 0; i < levelTwoList.length; i++) {
        let levelTwo = Object.assign({}, levelTwoList[i]);
        levelTwo.childrenList = [];
        for (let j = 0; j < levelThreeList.length; j++) {
          if (levelTwoList[i].menuCode === levelThreeList[j].parentCode) {
            levelTwo.childrenList.push(levelThreeList[j]);
          }
        }
        levelTwoData.push(levelTwo);
      }
      let levelOneData = [];
      for (let i = 0; i < levelOneList.length; i++) {
        let levelOne = Object.assign({}, levelOneList[i]);
        levelOne.childrenList = [];
        for (let j = 0; j < levelTwoData.length; j++) {
          if (levelOneList[i].menuCode === levelTwoData[j].parentCode) {
            levelOne.childrenList.push(levelTwoData[j]);
          }
        }
        levelOneData.push(levelOne);
      }
      return levelOneData;
    },
    //检查密码是否需要修改
    checkPasswordUpdate(userData){
      let vm = this;
      let updateDesc = null;
      //是否默认密码
      if(userData.defaultPwd){
        updateDesc = "当前为默认密码(默认密码仅在当天有效)，请及时修改密码！";
      }
      //是否7天内到期
      if(userData.loginPwdValidDay<=7){
        updateDesc = "您的密码将在【"+userData.loginPwdValidDay+"】天后过期，请及时修改密码！";
      }
      if(updateDesc && "YqvUserPassword" != vm.$router.currentRoute.value.name){
        vm.$confirm(updateDesc, "提示", {
          confirmButtonText: "确认",
          type: "warning",
          showCancelButton:false,
          showClose:false,
        })
          .then(() => {
            vm.$router.push({
              name: "YqvUserPassword",
            });
          })
          .catch(() => {});
      }
    }
  },
};
</script>

<style scoped lang="less">
html.light{
  .menuHead {
    //background-color: #fff;
    //background-color: #1890ff;
    background-color: #4eb4e4fc;
  }
  .system-content-Breadcrumb{
    background-color: #fff;
    border-left: 1px solid #d9d9d9;
  }
}
.system-box {
  position: relative;
  height: 100%;
  .el-breadcrumb {
    padding-top: 15px;
  }
  .system-content {
    height: 100%;
    display: flex;
    .system-content-left {
      flex: 0 0 180px;
      float: left;
      height: 100%;
      //border-right: 1px solid #d9d9d9;
      overflow-y: auto;
      /*滚动条样式*/
      &::-webkit-scrollbar {
        /*滚动条整体样式*/
        width: 0px; /*高宽分别对应横竖滚动条的尺寸*/
        height: 4px;
      }
      &::-webkit-scrollbar-thumb {
        border-radius: 5px;
      }
      &::-webkit-scrollbar-track {
        border-radius: 0;
      }
      .menuHead {
        height: 50px;
        display: block;
        flex-direction: column;
        justify-content: center;
        img {
          margin-left: 12px;
          width: 160px;
          height: 50px;
          cursor: pointer;
        }
      }
    }
    .system-content-right {
      overflow: auto;
      position: relative;
      float: left;
      flex: 1;
      width: calc(100% - 180px);
      height: 100%;
      .system-content-Breadcrumb {
        .Breadcrumb-box {
          .el-breadcrumb {
            font-size: 13px !important;
          }
          height: 46px;
          line-height: 46px;
          padding-left: 25px;
        }
      }
      .system-home-Breadcrumb {
        height: 46px;
        line-height: 46px;
        padding-left: 25px;
      }
      .system-content-min {
        height: calc(100% - 30px - 64px - 80px + 35px);
        padding: 0px 24px 0 24px;
        overflow-x: hidden;
        overflow-y: auto;
        .content-min-box {
          height: 100%;
        }
      }
      .system-content-min-add {
        height: calc(100% - 30px - 64px - 80px + 35px + 46px);
        padding: 10px 10px 0 10px;
        overflow-x: hidden;
        overflow-y: auto;
        .content-min-box {
          height: 100%;
        }
      }
      .system-content-footer {
        position: relative;
        width: 100%;
        left: 0;
        bottom: 0;
        height: 40px;
        line-height: 40px;
        text-align: center;
        span {
          font-size: 12px;
          display: inline-block;
          height: 32px;
          line-height: 28px;
        }
      }
    }
  }

  /*动画*/
  .slide-fade {
    position: absolute;
    left: 0;
    right: 0;
  }
  .slide-fade-enter-active {
    transition: all 1.2s ease;
  }
  .slide-fade-leave-active {
    transition: all 0.1s cubic-bezier(2, 0.5, 0.8, 1);
  }
  .slide-fade-enter,
  .slide-fade-leave-to {
    left: 0;
    right: 0;
    transform: translateX(50px);
    opacity: 0;
  }
}
</style>

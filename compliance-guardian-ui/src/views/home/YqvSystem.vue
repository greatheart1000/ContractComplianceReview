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
        <Header />
        <div id="screenfull-content">
          <div class="system-content-Breadcrumb">
            <div>
              <div class="Breadcrumb-box">
                <div v-if="JSON.stringify($route.params) == '{}'">
                  <div v-for="(itema, index) in navigationNameList" :key="index">
                    <div
                      style="
                        float: left;
                        color: rgba(0, 0, 0, 0.45);
                        margin: 0 5px;
                        font-size: 14px;
                      "
                    >
                      <font-awesome-icon icon="fas fa-angle-right"
                        v-if="index > 0"
                        aria-hidden="true"
                      />
                    </div>
                    <div style="float: left; color: rgba(0, 0, 0, 0.45)">
                      {{ itema }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <div class="system-content-min-add">
            <div
              class="content-min-box"
              ref="contentMinBox"
              style="overflow-y: hidden"
            >
              <div style="overflow-y: auto; height: 100%" class="scrollClass">
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
import Header from "@/components/yqvalley/Header";
import MenuLink from "@/components/yqvalley/MenuLink";
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
    Header,
    MenuLink,
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
      "setPasswordsExpiredDate",
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
  },
};
</script>

<style scoped lang="less">
.Breadcrumb-box ::v-deep(.el-breadcrumb__inner) {
  color: rgba(0, 0, 0, 0.45) !important;
}
.Breadcrumb-box ::v-deep(.el-breadcrumb__separator) {
  font-size: 10px !important;
  color: rgba(0, 0, 0, 0.45);
  margin: 0 1px;
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
      border-right: 1px solid #3f4457;
      background: #3f4457;
      overflow-y: auto;
      scrollbar-arrow-color: #666; /**/ /*三角箭头的颜色*/
      scrollbar-face-color: #999; /**/ /*立体滚动条的颜色*/
      scrollbar-3dlight-color: #ccc; /**/ /*立体滚动条亮边的颜色*/
      scrollbar-highlight-color: #666; /**/ /*滚动条空白部分的颜色*/
      scrollbar-shadow-color: #999; /**/ /*立体滚动条阴影的颜色*/
      scrollbar-darkshadow-color: #666; /**/ /*立体滚动条强阴影的颜色*/
      scrollbar-track-color: #ccc; /**/ /*立体滚动条背景颜色*/
      scrollbar-base-color: #f8f8f8; /**/ /*滚动条的基本颜色*/
      /*滚动条样式*/
      &::-webkit-scrollbar {
        /*滚动条整体样式*/
        width: 4px; /*高宽分别对应横竖滚动条的尺寸*/
        height: 4px;
      }
      &::-webkit-scrollbar-thumb {
        /*滚动条里面小方块*/
        border-radius: 5px;
        -webkit-box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
        box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
        background: rgba(0, 0, 0, 0.2);
      }
      &::-webkit-scrollbar-track {
        /*滚动条里面轨道*/
        -webkit-box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
        box-shadow: inset 0 0 5px rgba(0, 0, 0, 0.2);
        border-radius: 0;
        background: rgba(0, 0, 0, 0.1);
      }
      .menuHead {
        height: 50px;
        background: #3f4457;
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
      // height: calc(100% - 64px- 54px);
      height: 100%;
      // background: #f0f2f5;
      .system-content-Breadcrumb {
        .Breadcrumb-box {
          .el-breadcrumb {
            font-size: 13px !important;
          }
          height: 46px;
          line-height: 46px;

          padding-left: 25px;
          background: #f0f2f5;
        }
      }
      .system-home-Breadcrumb {
        height: 46px;
        line-height: 46px;
        padding-left: 25px;
        background: #f0f2f5;
      }
      .system-content-min {
        height: calc(100% - 30px - 64px - 80px + 35px);
        background: #f0f2f5;
        padding: 0px 24px 0 24px;
        overflow-x: hidden;
        overflow-y: auto;
        .content-min-box {
          height: 100%;
          /*width: calc(100%-220px);*/
          background: #fff;
        }
      }
      .system-content-min-add {
        height: calc(100% - 30px - 64px - 80px + 35px + 46px);
        background: #f0f2f5;
        padding: 0px 24px 0 24px;
        overflow-x: hidden;
        overflow-y: auto;
        .content-min-box {
          height: 100%;
          /*width: calc(100%-220px);*/
          background: #fff;
        }
      }
      .system-content-footer {
        position: absolute;
        width: 100%;
        left: 0;
        bottom: 0;
        height: 32px;
        line-height: 32px;
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

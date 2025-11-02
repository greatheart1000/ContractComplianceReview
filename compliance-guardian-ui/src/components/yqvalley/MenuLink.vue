<template>
  <div class="MenuLink">
    <el-row class="tac">
      <el-col :span="12">
        <el-menu
          class="el-menu-vertical-demo"
          router
          @open="handleOpen"
          @close="handleClose"
          :default-active="$route.path"
          :collapse="menuFlag"
          text-color="#fff"
          active-text-color="#fff"
          background-color="#3f4457"
          active-background-color="#ffffff"
          unique-opened
          :collapse-transition="false"
        >
          <template v-for="first in navDatas" :key="first.menuCode">
            <div>
              <el-menu-item :index="first.menuUrl?first.menuUrl:first.menuCode + ''" v-if="!first.childrenList.length">
                <div class="el-sub-menu__title" style="padding-left: 0;">
                  <i class="fa" v-if="first.menuStyle">
                    <font-awesome-icon :icon="first.menuStyle"/>
                  </i>
                  <span v-if="!menuFlag">{{ first.menuName }}</span>
                </div>
              </el-menu-item>
              <el-sub-menu
                ref="titleArrow"
                :index="first.menuCode + ''"
                :class="[!menuFlag ? 'arrowShow' : 'arrowHide']"
                v-else
              >
                <template #title>
                  <div class="fontStyle">
                    <i :class="first.menuStyle"></i>
                    <span v-if="!menuFlag">{{ first.menuName }}</span>
                  </div>
                </template>
                <template v-for="second in first.childrenList" :key="second.menuCode">
                  <div class="navSecond">
                    <el-menu-item-group v-if="!second.childrenList.length">
                      <el-menu-item :index="second.menuUrl + ''">{{
                        second.menuName
                      }}</el-menu-item>
                    </el-menu-item-group>
                    <el-sub-menu v-else :index="second.menuCode + ''">
                      <template #title>{{ second.menuName }}</template>
                      <el-menu-item
                        v-for="third in second.childrenList"
                        :key="third.menuCode"
                        :index="third.menuUrl + ''"
                        >{{ third.menuName }}</el-menu-item
                      >
                    </el-sub-menu>
                  </div>
                </template>
              </el-sub-menu>
            </div>
          </template>
        </el-menu>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapState, mapMutations } from "vuex";
export default {
  name: "MenuLink",
  data() {
    return {
      menuFlag: false
    };
  },
  props: ["navDatas"],
  components: {},
  computed: {
    ...mapState(["menuLinkFlag"])
  },
  mounted() {
    
  },
  watch: {
    "$route.name"() {
      
    },
    menuLinkFlag(newVal, oldVal) {
      this.menuFlag = newVal;
    }
  },
  methods: {
    ...mapMutations(["changeMenu"]),
    handleOpen(key, keyPath) {},
    handleClose(key, keyPath) {}
  }
};
</script>

<style scoped lang="less">
.MenuLink ::v-deep(.fa){
  margin-right: 10px;
}
.MenuLink ::v-deep(.el-sub-menu__title) {
  z-index: 999 !important;
}
.MenuLink ::v-deep(.el-menu-item) {
  z-index: 999 !important;
}
.MenuLink ::v-deep(.el-sub-menu__title:hover) {
  background: #202230 !important;
}
.MenuLink ::v-deep(.el-menu-item-group__title) {
  display: none;
  padding: 0px 0 0px 0px !important;
}
//显示隐藏菜单下拉箭头
.arrowShow ::v-deep(.el-sub-menu__icon-arrow) {
  display: block;
}
.arrowHide ::v-deep(.el-sub-menu__icon-arrow) {
  display: none;
}
.navSecond ::v-deep(.el-sub-menu) {
  .el-sub-menu__title {
    background: #303241 !important;
    padding-left: 51px !important;
  }
  .el-sub-menu__title:hover {
    background: #202230 !important;
  }
}
.navSecond ::v-deep(.el-menu-item-group,
.el-menu--popup) {
  .el-menu-item {
    background: #303241 !important;
    padding-left: 51px !important;
  }
  .el-menu-item:hover {
    background: #202230 !important; //鼠标hover颜色
  }
  .el-menu-item.is-active {
    background: #5675f5 !important;
  }
}
.fontStyle ::v-deep(span) {
  font-size: 15px;
  font-weight: 500;
}
.MenuLink {
  z-index: 10001;
  .navSecond {
    .el-sub-menu {
      border-bottom: none;
    }
  }
  .el-col-12 {
    width: 100% !important;
    text-align: left !important;
    max-width: 100% !important;
    flex: 0 0 100% !important;
  }
  .el-sub-menu__title * {
    vertical-align: middle !important;
  }
  .el-menu {
    border: none;
    .el-menu-item.is-active {
      background: #5675f5 !important;
    }
    .el-menu-item {
      background: #303241 !important;
    }
    .el-menu-item:hover {
      background: #202230 !important;
    }
  }

}
</style>

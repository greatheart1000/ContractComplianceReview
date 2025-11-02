import { createStore } from "vuex";

export default createStore({
  state: {
    //导航菜单名称
    navigationNameMap: new Map(),
    //加载中提示内容
    loadingText: "加载中...",
    //控制左侧菜单显示隐藏
    menuLinkFlag: false,
    //用户首页集合
    homeList:null,
    //用户信息
    userInfo: {},
    //下拉选择
    yqvSelect:null,
    //配置项
    yqvConfig: null,
    //用户权限
    yqvAuthority: null,
    //普通列表高度（自适应使用）
    listHeight: 336,
    //带Tab列表高度（自适应使用）
    tabListHeight: 336,
    //页面底部排除高度
    footerExcludeHeight:90,
    //后端服务访问path路径
    contextPath: "./compliance/",
  },
  getters: {
    
  },
  mutations: {
    ["setLoadingText"](state, payload) {
      state.loadingText = payload;
    },
    //修改导航菜单显示隐藏
    ["changeMenu"](state, payload) {
      if (payload) {
        state.menuLinkFlag = !payload;
        return;
      }
      state.menuLinkFlag = !state.menuLinkFlag;
    },
    //用户首页集合
    ["setHomeList"](state, payload) {
      state.homeList = payload;
    },
    //设置用户登录信息
    ["setUserInfo"](state, payload) {
      state.userInfo = payload;
    },
    //设置导航菜单名称Map
    ["setNavigationText"](state, payload) {
      state.navigationNameMap = payload;
    },
    //设置下拉列表信息
    ["setYqvSelect"](state, payload) {
      state.yqvSelect = payload;
    },
    //设置用户配置信息
    ["setYqvConfig"](state, payload) {
      state.yqvConfig = payload;
    },
    //设置公开权限信息
    ["setYqvAuthority"](state, payload) {
      state.yqvAuthority = payload;
    },
    //修改默认列表高度
    ["setHeight"](state) {
      state.listHeight = window.innerHeight - 194;
      state.tabListHeight = window.innerHeight - 194 - 54;
      //监听窗口大小变化
      window.onresize = function () {
        state.listHeight = window.innerHeight - 194;
        state.tabListHeight = window.innerHeight - 194 - 54;
      };
    },
  },
  actions: {
    ["showLoading"](state, payload) {
      if (payload) {
        state.commit("setLoading", payload);
      }
    },
  },
  modules: {},
});

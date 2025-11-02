import { createRouter, createWebHashHistory, RouteRecordRaw } from "vue-router";
import yqvutil from "../common/yqvalley";
//登录
import LoginView from "@/views/Login.vue";
//导航菜单
import SystemView from "@/views/home/YqvSystem.vue";
import SystemDarkView from "@/views/home/YqvSystemDark.vue";
//默认首页
import DefaultIndex from "@/views/home/IndexCommon.vue";
//用户首页
import UserIndex from "@/views/home/UserCommon.vue";
//管理员首页
import AdminIndex from "@/views/home/AdminCommon.vue";
//个人中心
import YqvUserInfo from "@/views/yqvalley/user/info/Index.vue";
import YqvUserPassword from "@/views/yqvalley/user/password/Index.vue";
import YqvUserImportList from "@/views/yqvalley/user/import/IndexList.vue";
import YqvUserExportList from "@/views/yqvalley/user/export/IndexList.vue";
import YqvUserLog from "@/views/yqvalley/user/log/IndexList.vue";
import YqvUserSignList from "@/views/yqvalley/user/sign/IndexList.vue";
//平台管理
import YqvManageUserList from "@/views/yqvalley/manage/user/IndexList.vue";
import YqvManageRoleList from "@/views/yqvalley/manage/role/IndexList.vue";
import YqvManageConfigList from "@/views/yqvalley/manage/config/IndexList.vue";
import YqvManageLogList from "@/views/yqvalley/manage/log/IndexList.vue";
import YqvManageSelectList from "@/views/yqvalley/manage/select/IndexList.vue";
import YqvManageDeptList from "@/views/yqvalley/manage/dept/IndexList.vue";
import YqvManageRedisList from "@/views/yqvalley/manage/redis/IndexList.vue";
import YqvManageFileList from "@/views/yqvalley/manage/file/IndexList.vue";
//开发管理
import YqvDevelopMenuList from "@/views/yqvalley/develop/menu/IndexList.vue";
import YqvDevelopAuthorityList from "@/views/yqvalley/develop/authority/IndexList.vue";
import YqvDevelopConfigList from "@/views/yqvalley/develop/config/DevelopList.vue";
import YqvDevelopSelectList from "@/views/yqvalley/develop/select/DevelopList.vue";
import YqvDevelopRedisList from "@/views/yqvalley/develop/redis/DevelopList.vue";
import YqvDevelopRequestList from "@/views/yqvalley/develop/request/IndexList.vue";
import YqvDevelopMailTempList from "@/views/yqvalley/develop/mailtemp/IndexList.vue";
import YqvDevelopUserList from "@/views/yqvalley/develop/user/IndexList.vue";
import YqvDevelopRoleList from "@/views/yqvalley/develop/role/IndexList.vue";
//公共配置
import ModelConfigManageList from "@/views/compliance/config/model/IndexList.vue";
import ModelUserManageList from "@/views/compliance/config/user/IndexList.vue";
//合同合规
import ContractStandardManageList from "@/views/compliance/contract/standard/IndexList.vue";
import ContractBusinessManageList from "@/views/compliance/contract/business/IndexList.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/login",
    name: "login",
    component: LoginView,
  },
  //首页
  {
    path: "/", //默认首页
    name: "system",
    component: SystemDarkView,
    redirect: "/index",
    children: [
      {
        path: "/index", //默认首页
        component: DefaultIndex,
      },
      {
        path: "/userindex", //用户首页
        component: UserIndex,
      },
      {
        path: "/adminindex", //管理员首页
        component: AdminIndex,
      },
      //个人信息
      { path: "/yqv/user/info", name: "YqvUserInfo", component: YqvUserInfo },
      //密码修改
      {
        path: "/yqv/user/password",
        name: "YqvUserPassword",
        component: YqvUserPassword,
      },
      //我的导入
      {
        path: "/yqv/user/importlist",
        name: "YqvUserImportList",
        component: YqvUserImportList,
      },
      //我的导出
      {
        path: "/yqv/user/exportlist",
        name: "YqvUserExportList",
        component: YqvUserExportList,
      },
      //操作记录
      { path: "/yqv/user/loglist", name: "YqvUserLog", component: YqvUserLog },
      //凭证管理
      {
        path: "/yqv/user/signlist",
        name: "YqvUserSignList",
        component: YqvUserSignList,
      },
      //选择设置
      {
        path: "/yqv/manage/selectlist",
        name: "YqvManageSelectList",
        component: YqvManageSelectList,
      },
      //应用设置
      {
        path: "/yqv/manage/configlist",
        name: "YqvManageConfigList",
        component: YqvManageConfigList,
      },
      //部门维护
      {
        path: "/yqv/manage/deptlist",
        name: "YqvManageDeptList",
        component: YqvManageDeptList,
      },
      //用户管理
      {
        path: "/yqv/manage/userlist",
        name: "YqvManageUserList",
        component: YqvManageUserList,
      },
      //角色管理
      {
        path: "/yqv/manage/rolelist",
        name: "YqvManageRoleList",
        component: YqvManageRoleList,
      },
      //系统日志
      {
        path: "/yqv/manage/loglist",
        name: "YqvManageLogList",
        component: YqvManageLogList,
      },
      //缓存管理
      {
        path: "/yqv/manage/redislist",
        name: "YqvManageRedisList",
        component: YqvManageRedisList,
      },
      //文件管理
      {
        path: "/yqv/manage/filelist",
        name: "YqvManageFileList",
        component: YqvManageFileList,
      },
      //应用配置
      {
        path: "/yqv/develop/configlist",
        name: "YqvDevelopConfigList",
        component: YqvDevelopConfigList,
      },
      //菜单配置
      {
        path: "/yqv/develop/menulist",
        name: "YqvDevelopMenuList",
        component: YqvDevelopMenuList,
      },
      //权限配置
      {
        path: "/yqv/develop/authoritylist",
        name: "YqvDevelopAuthorityList",
        component: YqvDevelopAuthorityList,
      },
      //选择配置
      {
        path: "/yqv/develop/selectlist",
        name: "YqvDevelopSelectList",
        component: YqvDevelopSelectList,
      },
      //缓存管理
      {
        path: "/yqv/develop/redislist",
        name: "YqvDevelopRedisList",
        component: YqvDevelopRedisList,
      },
      //请求配置
      {
        path: "/yqv/develop/requestlist",
        name: "YqvDevelopRequestList",
        component: YqvDevelopRequestList,
      },//邮件模板配置
      {
        path: "/yqv/develop/mailtemplist",
        name: "YqvDevelopMailTempList",
        component: YqvDevelopMailTempList,
      },
      {
        path: "/yqv/develop/userlist",
        name: "YqvDevelopUserList",
        component: YqvDevelopUserList,
      },
      {
        path: "/yqv/develop/rolelist",
        name: "YqvDevelopRoleList",
        component: YqvDevelopRoleList,
      },{
        path: "/cg/config/modellist",
        name: "ModelConfigManageList",
        component: ModelConfigManageList,
      },{
        path: "/cg/config/userlist",
        name: "ModelUserManageList",
        component: ModelUserManageList,
      },{
        path: "/cg/contract/standardlist",
        name: "ContractStandardManageList",
        component: ContractStandardManageList,
      },{
        path: "/cg/contract/businesslist",
        name: "ContractBusinessManageList",
        component: ContractBusinessManageList,
      },
    ],
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

//路由进入处理
router.beforeEach((to, from, next) => {
  if (yqvutil.getToken()) {
    //已登录处理
    if(to.path === "/login"){
      //当前如果是登录页，则强制进入首页
      next(yqvutil.getDefaultHome());
    }else{
      next();
    }
  } else {
    //未登陆强制进入登陆页面
    if (to.path === "/login") {
      next();
    } else {
      next("/login");
    }
  }
});

export default router;

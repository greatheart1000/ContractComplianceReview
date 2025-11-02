import { createRouter, createWebHashHistory, RouteRecordRaw } from "vue-router";
import yqvutil from "../common/yqvalley";
//登录
import LoginView from "@/views/Login.vue";
//首页
import IndexView from "@/views/Index.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/login",
    name: "login",
    component: LoginView,
  },
  //首页
  {
    path: "/", //默认首页
    name: "home",
    component: IndexView,
    redirect: "/index",
    children: [
    ],
  },
  {
    path: "/index", //默认首页
    name: "index",
    component: IndexView,
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
      next("/index");
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

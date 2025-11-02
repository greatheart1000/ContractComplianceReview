import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import axios from "./axios";
import "@/assets/less/main.less";
// 导入element-plus
import ElementPlus from "element-plus";
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import "element-plus/dist/index.css";
import "babel-polyfill";
import yqvutil from "./common/yqvalley";

//引入自定义图标
import "@/assets/icons";
import SvgIcon from "@/components/yqvicon/SvgIcon.vue";

const app = createApp(App);
//全局ctx(this)上挂载 $axios
app.config.globalProperties.$http = axios;
//全局ctx(this)上挂载 $yqvutil
app.config.globalProperties.$yqvutil = yqvutil;

app.use(store);
app.use(router);
app.use(ElementPlus, {
  locale: zhCn, //使用中文语言
  size: 'small',//组件大小设置(large/default/small)
});
app.component('svg-icon', SvgIcon);
app.mount("#app");

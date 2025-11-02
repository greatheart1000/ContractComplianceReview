import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import axios from "./axios";
import "@/assets/less/main.less";
//黑暗模式
import 'element-plus/theme-chalk/dark/css-vars.css'
// 导入element-plus
import ElementPlus from "element-plus";
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import "element-plus/dist/index.css";
import "babel-polyfill";
import yqvutil from "./common/yqvalley";

//Fontawesome组件导入
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
//Fontawesome图标导入
import './components/yqvicon/fontawesome'
//引入自定义图标
import "@/assets/icons";
import SvgIcon from "@/components/yqvicon/SvgIcon.vue";
//解决富文本警告
import 'default-passive-events'

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
//Fontawesome组件注册
app.component('font-awesome-icon', FontAwesomeIcon);
app.component('svg-icon', SvgIcon);
app.mount("#app");

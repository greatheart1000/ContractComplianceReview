import axios from "axios";
import store from "@/store/index";
import yqvutil from "./common/yqvalley";
import { ElMessage, ElLoading } from "element-plus";

let loading: any = null;
let loadingExcludeMap = new Map();
loadingExcludeMap.set(store.state.contextPath+"yqvservice/valley/init",true);
//默认设置
axios.defaults.baseURL = process.env.VUE_APP_API_URL;
axios.defaults.headers.post["Content-Type"] = "application/json";
axios.defaults.timeout = 60000;
//Request拦截器
axios.interceptors.request.use(
  (config) => {
    const url: string = config.url || "";
    if(!loadingExcludeMap.get(url)){
      loading = ElLoading.service({
        lock: true,
        text: store.state.loadingText,
        background: "rgba(0, 0, 0, 0.0)",
      });
    }
    if (yqvutil.getToken()) {
      if (config.headers) {
        config.headers.Authorization = yqvutil.getToken() || "";
      }
    }
    if (url.indexOf("?") === -1) {
      config.url = config.url + "?" + new Date().getTime();
    } else {
      config.url = config.url + "&" + new Date().getTime();
    }
    return config;
  },
  (err) => {
    ElMessage.error({
      message: "加载超时",
    });
    return Promise.reject(err);
  }
);
//Response拦截器
axios.interceptors.response.use(
  (response) => {
    if(loading){
      loading.close();
    }
    if (yqvutil.getToken()) {
      response.headers.Authorization = yqvutil.getToken() || "";
    }
    return response;
  },
  (err) => {
    if(loading){
      loading.close();
    }
    if (err.response) {
      const messageError = JSON.parse(JSON.stringify(err.response)).data
        .message;
      switch (err.response.status) {
        case 400:
          ElMessage.error(messageError || `请求错误(400)`);
          break;
        case 401:
          yqvutil.removeToken()
          location.href = location.origin + "/#/Login";
          break;
        case 403:
          ElMessage.error(messageError || `拒绝访问(403)`);
          break;
        case 404:
          ElMessage.error(messageError || `请求出错(404)`);
          break;
        case 500:
          ElMessage.error(messageError || `服务器错误(500)`);
          break;
        case 504:
          ElMessage.error(messageError || `网络超时(504)`);
          break;
        default:
          ElMessage.error(messageError || `连接出错(${err.response.status})!`);
      }
    }
    return Promise.reject(err);
  }
);
export default axios;

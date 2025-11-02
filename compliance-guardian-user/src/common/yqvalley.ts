import store from "@/store";
import router from '@/router'
import JSEncrypt from "jsencrypt";
import useClipboard from "vue-clipboard3";
import {ElMessage} from 'element-plus'

export default {
  name: "YqvalleyUtil",
  //菜单跳转
  routerToMenu: (routerId: string) => {
    router.push({
      name: routerId,
    });
  },
  //菜单跳转
  routerToUrl: (url: string) => {
    location.href = location.origin + "/#" + url;
  },
  getRsaCode: (str: string) => {
    let data = "";
    try {
      const reg = new RegExp("(^| )" + "public_secret" + "=([^;]*)(;|$)");
      const arr = document.cookie.match(reg);
      if (arr) {
        const pubKey = `${unescape(arr[2])}`;
        const encryptStr = new JSEncrypt();
        encryptStr.setPublicKey(pubKey);
        data = encryptStr.encrypt(str.toString()) || "";
      }
    } catch (error) {}
    return data;
  },
  getPSRsaCode: (str: string,pubKey: string) => {
    let data = "";
    try {
        const encryptStr = new JSEncrypt();
        encryptStr.setPublicKey(pubKey);
        data = encryptStr.encrypt(str.toString()) || "";
    } catch (error) {}
    return data;
  },
  updateToken: (token: string) => {
    //设置TOKEN
    localStorage.setItem("model_token", token);
    //设置有效期
    let expirationMinute = 600;
    let expirationTimeStr:string = (new Date().getTime()+(1000*60*expirationMinute))+"";
    localStorage.setItem("model_expiration", expirationTimeStr);
  },
  getToken: () => {
    let token = null;
    let nowTime:number = new Date().getTime();
    let expirationTime = Number(localStorage.getItem("model_expiration"));
    if (expirationTime && nowTime <= expirationTime) {
      token = localStorage.getItem("model_token");
      //更新有效期
      let expirationMinute = 600;
      let expirationTimeStr:string = (new Date().getTime()+(1000*60*expirationMinute))+"";
      localStorage.setItem("model_expiration", expirationTimeStr);
    }else{
      localStorage.removeItem("model_token");
    }
    return token;
  },
  removeToken: () => {
    localStorage.removeItem("model_token");
    localStorage.removeItem("model_expiration");
  },
  getContextPath: () => {
    return store.state.contextPath;
  },
  //JSON格式化
  getJsonText: (text: string) => {
    let jsonText = text;
    try {
      jsonText = JSON.stringify(text);
    } catch (e) {
      jsonText = text;
    }
    return jsonText;
  },
  //用于列表的时间搜索年月日,时分秒
  setDateFormat: (date: number, minutes: string) => {
    let mStr = "";
    let dStr = "";
    let newDate = new Date(date);
    let y = newDate.getFullYear();
    let m = newDate.getMonth() + 1;
    let d = newDate.getDate();
    mStr = (m < 10 ? "0" + m : m) + "";
    dStr = (d < 10 ? "0" + d : d) + "";
    if (minutes) {
      return y + "-" + mStr + "-" + dStr + " " + minutes + ":00";
    } else {
      return y + "-" + mStr + "-" + dStr;
    }
  },
  //设置时间日期
  setFormTime: (val: number, type: number) => {
    if (type === 3) {
      if (!val) {
        return "";
      } else {
        let second = val % 60;
        let minute = Math.floor(val / 60);
        if (minute) {
          return minute + "分" + second + "秒";
        } else if (second) {
          return second + "秒";
        }
      }
    }
    if (val) {
      let mStr = "";
      let dStr = "";
      let hStr = "";
      let minuteStr = "";
      let secondStr = "";
      let date = new Date(val);
      let y = date.getFullYear();
      let m = date.getMonth() + 1;
      mStr = (m < 10 ? "0" + m : m) + "";
      let d = date.getDate();
      dStr = (d < 10 ? "0" + d : d) + "";
      let h = date.getHours();
      hStr = (h < 10 ? "0" + h : h) + "";
      let minute = date.getMinutes();
      let second = date.getSeconds();
      minuteStr = (minute < 10 ? "0" + minute : minute) + "";
      secondStr = (second < 10 ? "0" + second : second) + "";
      if (type == 2) {
        return y + "-" + mStr + "-" + dStr + " " + hStr + ":" + minute;
      } else if (type == 1) {
        return (
          y +
          "-" +
          mStr +
          "-" +
          dStr +
          " " +
          hStr +
          ":" +
          minuteStr +
          ":" +
          secondStr
        );
      } else if(type == 5) {
        return y + "-" + mStr;
      } else {
        return y + "-" + mStr + "-" + dStr;
      }
    } else {
      return "";
    }
  },
  formatDuring(value: number) {
    let newValue = "";
    if(value!=null && value>=0){
      let seconds = 0;//秒
      let minutes = 0;//分
      let hours = 0;//小时
      let days = 0;//天
      if (value >= 60) {
        seconds = (value % 60);
        minutes = (value-seconds)/60;
        if (minutes >= 60) {
          const sum = minutes;
          minutes = sum%60;
          hours = (sum-minutes)/60;
          if (hours >= 24) {
            const sum = hours;
            hours = sum%24;
            days = (sum-hours)/24;
          }
        }
      }else{
        seconds = value;
      }
      if(days>0){
        newValue = days+"天";
      }
      if(hours>0){
        newValue += hours+"小时";
      }
      if(minutes>0){
        newValue += minutes+"分";
      }
      if(seconds>=0){
        newValue += seconds+"秒";
      }
    }else{
      newValue = 0+"秒";
    }
    return newValue;
  },
  //通过URL获取导航菜单名称
  setNavigationName: (data: string) => {
    if (data) {
      //调整内容最小高度
      const contentDiv = document.getElementsByClassName("system-content");
      if(contentDiv && contentDiv.length>0){
        const height = contentDiv[0].scrollHeight;
        let pageContentDiv = document.getElementById('screenfull-content');
        if(pageContentDiv){
          pageContentDiv.style.minHeight = (height-store.state.footerExcludeHeight)+"px";
        }
      }
      return store.state.navigationNameMap.get(data);
    }
  },
  //储存单位转换
  getFileSize: (size: number) => {
    if (!size) {
      return "0B";
    }
    let u = "B";
    if (size >= 1024) {
      size = Math.round(size / 1024);
      u = "KB";
    }
    if (size >= 1024) {
      size = Math.round(size / 1024);
      u = "MB";
    }
    if (size >= 1024) {
      size = Math.round(size / 1024);
      u = "G";
    }
    return size + u;
  },
  //文件下载
  downloads: (src: string) => {
    let divFrame = window.parent.document.getElementById("downLoadListFrame");
    //判断是否存在，如果存在先移除，再重新创建
    if (divFrame != null) {
      window.parent.document.body.removeChild(divFrame);
    }
    //重新创建
    const iframe = window.parent.document.createElement("iframe");
    iframe.setAttribute("id", "downLoadListFrame");
    window.parent.document.body.appendChild(iframe);
    divFrame = window.parent.document.getElementById("downLoadListFrame");
    if (divFrame) {
      divFrame.setAttribute("src", src);
      divFrame.style.display = "none";
    }
  },
  getTodayDate() {
    let date1 = new Date(new Date(new Date().toLocaleDateString()).getTime());
    return (
      date1.getFullYear() +
      "-" +
      (date1.getMonth() + 1 < 10
        ? "0" + (date1.getMonth() + 1)
        : date1.getMonth() + 1) +
      "-" +
      (date1.getDate() < 10 ? "0" + date1.getDate() : date1.getDate()) +
      " " +
      (date1.getHours() < 10 ? "0" + date1.getHours() : date1.getHours()) +
      ":" +
      (date1.getMinutes() < 10
        ? "0" + date1.getMinutes()
        : date1.getMinutes()) +
      ":" +
      (date1.getSeconds() < 10 ? "0" + date1.getSeconds() : date1.getSeconds())
    );
  },
  formatMoney(num:number) {
    let numStr = null;
    if(num){
      numStr = num.toLocaleString();
    }else{
      return num;
    }
    return numStr;
  },
  //复制文本(同步)
  async copyTextAsync(text: string,msg: string){
    if(text){
      const { toClipboard } = useClipboard();
      try {
        await toClipboard(text);
        if(msg){
          ElMessage.success(msg);
        }
      } catch (e) {
        if(msg){
          ElMessage.error('复制失败');
        }
      }
    }
  },
  //复制文本
  copyText(text: string,msg: string){
    if(text){
      const { toClipboard } = useClipboard();
      try {
        toClipboard(text).then(() => {
          if(msg){
            ElMessage.success(msg);
          }
        }).catch(() => {
          if(msg){
            ElMessage.error('复制失败');
          }
        });
      } catch (e) {
        if(msg){
          ElMessage.error('复制失败');
        }
      }
    }
  }
};

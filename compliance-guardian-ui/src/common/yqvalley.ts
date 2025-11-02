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
  updateExpiration: () => {
    let expirationMinute = 30;
    try {
      if (store.state.yqvConfig) {
        let value = Number(store.state.yqvConfig["valley_security_login_expiration"]);
        if(value){
          expirationMinute = value;
        }
      }
    } catch (err) {}
    let expirationTimeStr:string = (new Date().getTime()+(1000*60*expirationMinute))+"";
    localStorage.setItem("expiration", expirationTimeStr);
  },
  updateToken: (token: string) => {
    //设置TOKEN
    localStorage.setItem("token", token);
    //设置有效期
    let expirationMinute = 30;
    try {
      if (store.state.yqvConfig) {
        let value = Number(store.state.yqvConfig["valley_security_login_expiration"]);
        if(value){
          expirationMinute = value;
        }
      }
    } catch (err) {}
    let expirationTimeStr:string = (new Date().getTime()+(1000*60*expirationMinute))+"";
    localStorage.setItem("expiration", expirationTimeStr);
  },
  getToken: () => {
    let token = null;
    let nowTime:number = new Date().getTime();
    let expirationTime = Number(localStorage.getItem("expiration"));
    if (expirationTime && nowTime <= expirationTime) {
      token = localStorage.getItem("token");
      //更新有效期
      let expirationMinute = 30;
      try {
        if (store.state.yqvConfig) {
          let value = Number(store.state.yqvConfig["valley_security_login_expiration"]);
          if(value){
            expirationMinute = value;
          }
        }
      } catch (err) {}
      let expirationTimeStr:string = (new Date().getTime()+(1000*60*expirationMinute))+"";
      localStorage.setItem("expiration", expirationTimeStr);
    }else{
      localStorage.removeItem("token");
    }
    return token;
  },
  removeToken: () => {
    localStorage.removeItem("token");
    localStorage.removeItem("expiration");
  },
  setDefaultHome: (defaultHome: string) => {
    localStorage.setItem("home", defaultHome);
  },
  getDefaultHome: () => {
    let defaultHome = null;
    if(store.state.userInfo){
      let userInfo:any = store.state.userInfo;
      if(userInfo.defaultHome){
        defaultHome = userInfo.defaultHome;
      }
    }
    if(!defaultHome){
      defaultHome = localStorage.getItem("home");
    }
    if(!defaultHome){
      defaultHome = "/index";
    }
    return defaultHome;
  },
  getContextPath: () => {
    return store.state.contextPath;
  },
  getMenuNameMap: (menuNameList: Array<any>,homeNameList: Array<any>) => {
    type MenuInfo = {
      menuCode: string;
      menuName: string;
      menuUrl: string;
      level1MenuName: string;
      level2MenuName: string;
      level3MenuName: string;
      menuLevel: number;
      menuStyle: string;
      parentCode: string;
    };
    type HmoeInfo = {
      menuCode: string;
      menuName: string;
      menuUrl: string;
    };
    let nameMap = new Map();
    menuNameList.forEach((menu: MenuInfo) => {
      if (menu != null && menu.menuUrl != null && menu.menuUrl != "") {
        let list = [];
        if (menu.level1MenuName != null) {
          list.push(menu.level1MenuName);
        }
        if (menu.level2MenuName != null) {
          list.push(menu.level2MenuName);
        }
        if (menu.level3MenuName != null) {
          list.push(menu.level3MenuName);
        }
        nameMap.set(menu.menuUrl, list);
      }
    });
    if(homeNameList){
      homeNameList.forEach((menu: HmoeInfo) => {
        if (menu != null && menu.menuUrl != null && menu.menuUrl != "") {
          let list = [];
          if (menu.menuName != null) {
            list.push("首页");
          }
          if (menu.menuName != null) {
            list.push(menu.menuName);
          }
          nameMap.set(menu.menuUrl, list);
        }
      });
    }
    return nameMap;
  },
  //获取下拉值
  getSelectList: (selectCode: string) => {
    let selectList: any = [];
    if (selectCode && store.state.yqvSelect) {
      try {
        selectList = store.state.yqvSelect[selectCode];
      } catch (err) {}
    }
    return selectList;
  },
  //获取下拉值
  getSelectItemName: (selectCode: string, itemValue: string) => {
    let name = itemValue;
    if (selectCode && itemValue && store.state.yqvSelect) {
      try {
        let selectList: any = store.state.yqvSelect[selectCode];
        selectList.forEach((selectData: any) => {
          if (itemValue == selectData.itemValue) {
            name = selectData.itemName;
          }
        });
      } catch (err) {}
    }
    if (!name) {
      name = itemValue;
    }
    return name;
  },
  //获取应用配置值
  getAppConfigValue: (configCode: string) => {
    let value = null;
    if (configCode && store.state.yqvConfig) {
      try {
        value = store.state.yqvConfig[configCode];
      } catch (err) {}
    }
    return value;
  },
  //验证是否有权限
  getVerifyAuthority: (authorityCode: string) => {
    let bo = false;
    if (authorityCode && store.state.yqvAuthority) {
      try {
        if (store.state.yqvAuthority[authorityCode]) {
          bo = true;
        }
      } catch (err) {}
    }
    return bo;
  },
  //树结构数据组装
  toTreeData: (data: any, attr: any) => {
    let tree: any = [];
    let resData = data;
    let checked: any = [];
    for (let i = 0; i < resData.length; i++) {
      if (resData[i].checked) {
        checked.push(resData[i].id);
      }
      if (resData[i].pId == attr.rootId) {
        let obj = Object.assign({}, resData[i]);
        obj.label = resData[i].name;
        obj.children = [];
        tree.push(obj);
        resData.splice(i, 1);
        i--;
      }
    }
    let run = function (treeArrs: any) {
      if (resData.length > 0) {
        for (let i = 0; i < treeArrs.length; i++) {
          for (let j = 0; j < resData.length; j++) {
            if (treeArrs[i].id === resData[j][attr.pId]) {
              let index = checked.indexOf(treeArrs[i].id);
              if (index > -1) {
                checked.splice(index, 1);
              }
              resData[j].iconName = "fas fa-file-invoice";
              resData[j].className = "tree-icon-blue";
              let obj = Object.assign({}, resData[j]);
              obj.label = resData[j].name;
              obj.children = [];
              treeArrs[i].children.push(obj);
              resData.splice(j, 1);
              j--;
            }
          }
          if (treeArrs[i].children.length) {
            treeArrs[i].iconName = "fas fa-folder-plus ";
            treeArrs[i].className = "tree-icon-yellow";
          } else {
            treeArrs[i].iconName = "fas fa-file-invoice";
            treeArrs[i].className = "tree-icon-blue";
          }
          run(treeArrs[i].children);
        }
      }
    };
    run(tree);
    tree.checkedFlag = checked;
    return tree;
  },
  //部门数据处理
  toTreeDeptData: (data: any, attr: any) => {
    let tree = [];
    let resData = data;
    for (let i = 0; i < resData.length; i++) {
      if (!resData[i].pId) {
        let obj = Object.assign({}, resData[i]);
        obj.label = resData[i].name;
        obj.id = resData[i].id;
        obj.children = [];
        tree.push(obj);
        resData.splice(i, 1);
        i--;
      }
    }
    let run = function (treeArrs: any) {
      if (resData.length > 0) {
        for (let i = 0; i < treeArrs.length; i++) {
          for (let j = 0; j < resData.length; j++) {
            if (treeArrs[i].id === resData[j][attr.pId]) {
              resData[j].iconName = "fas fa-users";
              resData[j].className = "tree-icon-blue";
              let obj = Object.assign({}, resData[j]);
              obj.label = resData[j].name;
              obj.id = resData[j].id;
              obj.children = [];
              treeArrs[i].children.push(obj);
              resData.splice(j, 1);
              j--;
            }
          }
          if (treeArrs[i].children.length) {
            treeArrs[i].iconName = "fas fa-sitemap";
            treeArrs[i].className = "tree-icon-yellow";
          } else {
            treeArrs[i].iconName = "fas fa-users";
            treeArrs[i].className = "tree-icon-blue";
          }
          run(treeArrs[i].children);
        }
      }
    };
    run(tree);
    return tree;
  },
  //菜单树结构数据组装
  toMenuTreeData: (data: any, attr: any) => {
    let tree: any = [];
    let resData = data;
    let checked: any = [];
    for (let i = 0; i < resData.length; i++) {
      if (resData[i].checked) {
        checked.push(resData[i].id);
      }
      if (resData[i].pId == attr.firstParentCode) {
        let obj = Object.assign({}, resData[i]);
        obj.label = resData[i].name;
        obj.children = [];
        tree.push(obj);
        resData.splice(i, 1);
        i--;
      }
    }
    let run = function (treeArrs: any) {
      if (resData.length > 0) {
        for (let i = 0; i < treeArrs.length; i++) {
          for (let j = 0; j < resData.length; j++) {
            if (treeArrs[i].id === resData[j][attr.pId]) {
              let index = checked.indexOf(treeArrs[i].id);
              if (index > -1) {
                checked.splice(index, 1);
              }
              resData[j].iconName = "fas fa-file-invoice";
              resData[j].className = "tree-icon-blue";
              let obj = Object.assign({}, resData[j]);
              obj.label = resData[j].name;
              obj.children = [];
              treeArrs[i].children.push(obj);
              resData.splice(j, 1);
              j--;
            }
          }
          if (treeArrs[i].children.length) {
            treeArrs[i].iconName = "fas fa-folder-plus";
            treeArrs[i].className = "tree-icon-yellow";
          } else {
            treeArrs[i].iconName = "fas fa-file-invoice";
            treeArrs[i].className = "tree-icon-blue";
          }
          run(treeArrs[i].children);
        }
      }
    };
    run(tree);
    tree.checkedFlag = checked;
    return tree;
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
  getYearFirst(){
    let date = new Date();
    date.setDate(1);
    date.setMonth(0);
    return date;
  },
  getMonthFirst(date:Date){
    date.setDate(1);
    return date;
  },
  getMonthLast(date:Date){
    let currentMonth=date.getMonth();
    let nextMonth=++currentMonth;
    let nextMonthFirstDay:any=new Date(date.getFullYear(),nextMonth,1);
    let oneDay=1000*60*60*24;
    return new Date(nextMonthFirstDay-oneDay);
  },
  // 获取每个月的天数
  getMonthDays() {
    let date = new Date();
    let year = date.getFullYear();
    let month = date.getMonth() + 1;
    let d = new Date(year, month, 0);
    return d.getDate();
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

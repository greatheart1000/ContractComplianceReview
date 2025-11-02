import yqvhttp from "@/axios";
import store from "@/store";

enum HeaderUrl {
  headerInfo = `yqvservice/user/header`,
  userLogout = `yqvservice/valley/logout`,
  faceImg = `fileservice/user/download/face`
}

export default {
  name: "HeaderApi",
  /**
 * 获取当前信息
 * @param handleSuccess 
 */
   getHeaderInfo: (handleSuccess: any) => {
    yqvhttp.get(store.state.contextPath+HeaderUrl.headerInfo).then((response) => {
      handleSuccess(response);
    });
  },
/**
 * 退出登录
 * @param handleSuccess 
 */
  userLogout: (handleSuccess: any) => {
    yqvhttp.get(store.state.contextPath+HeaderUrl.userLogout).then((response) => {
      handleSuccess(response);
    });
  },
  /**
   * 获取头像地址
   * @param faceFileId 
   * @returns 
   */
  getImgUrl: (faceFileId: string) => {
    return store.state.contextPath+HeaderUrl.faceImg;
  }
}

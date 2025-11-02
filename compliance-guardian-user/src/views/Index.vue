<template>
  <div class="ComplianceChat">
    <!-- 修改密码 -->
    <el-dialog
      title="修改密码"
      v-model="dialogUserPasswordShow"
      :close-on-click-modal="false"
      @close="closeUserPasswordDialog"
      width="480px"
    >
      <div style="width: 430px; height: 280px; margin: 0 auto">
        <div class="passwordModify-box">
          <div class="passwordModify-box-right">
            <div class="abox">
              <span class="abox-lableName"><span>*</span>用户账号：</span>
              <div class="input-box">
                <el-input
                  v-model="userPasswordData.userAccount"
                  :disabled="true"
                ></el-input>
              </div>
              <div class="abox">
                <span class="abox-lableName"><span>*</span>旧密码：</span>
                <div class="input-box">
                  <el-input
                    v-model="userPasswordData.oldPassword"
                    maxlength="20"
                    placeholder="请填写旧密码"
                    :type="showPasswordOld ? 'text' : 'password'"
                  ></el-input>
                  <font-awesome-icon :icon="showPasswordOld ? 'fas fa-eye' : 'fas fa-eye-slash'"
                    @click="showPasswordOld = !showPasswordOld" aria-hidden="true"/>
                </div>
                <div class="error-point" v-if="judgeOld">请填写旧密码</div>
              </div>
              <div class="abox">
                <span class="abox-lableName"><span>*</span>新密码：</span>
                <div class="input-box">
                  <el-input
                    maxlength="20"
                    v-model="userPasswordData.newPasswordOne"
                    placeholder="请填写新密码"
                    :type="showPasswordNewOne ? 'text' : 'password'"
                  >
                  </el-input>
                  <font-awesome-icon :icon="showPasswordNewOne ? 'fas fa-eye' : 'fas fa-eye-slash'"
                    v-if="userPasswordData.newPasswordOne"
                    @click="showPasswordNewOne = !showPasswordNewOne"
                    aria-hidden="true" />
                </div>
                <div class="error-point" v-if="judgeNewOne">
                  {{ newPasswordOneText }}
                </div>
              </div>
              <div class="abox">
                <span class="abox-lableName"><span>*</span>确认密码：</span>
                <div class="input-box">
                  <el-input
                    maxlength="20"
                    v-model="userPasswordData.newPasswordTwo"
                    placeholder="请填写新密码"
                    :type="showPasswordNewTwo ? 'text' : 'password'"
                  ></el-input>
                  <font-awesome-icon :icon="showPasswordNewTwo ? 'fas fa-eye' : 'fas fa-eye-slash'"
                    v-if="userPasswordData.newPasswordTwo"
                    @click="showPasswordNewTwo = !showPasswordNewTwo"
                    aria-hidden="true" />
                </div>
                <div class="error-point" v-if="judgeNewTwo">
                  {{ newPasswordTwoText }}
                </div>
              </div>
            </div>
            <div class="clear"></div>
            <div style="margin-top: 30px;"></div>
          </div>
        </div>
      </div>
      <div style="clear: both"></div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogUserPasswordShow = false">取 消</el-button>
          <el-button type="primary" @click="updateUserPassword">确 定</el-button >
        </div>
      </template>
    </el-dialog>
    <el-container class="app-container">
      <!-- 左侧会话信息 -->
      <el-aside width="250px" class="left-sidebar">
        <!-- 平台标识 -->
        <div class="header">
          <img :src="headerImgUrl"/>
        </div>
        <!-- 操作按钮 -->
        <div style="width: 100%;display: flex;">
          <div class="createSession pointer" style="flex: 1;display: flex;justify-content: center;align-items: center;">
            <div @click="handleCreateSession">
                <el-icon size="20px"><Plus /></el-icon>
                <span>新检查</span>
            </div>
          </div>
        </div>
        <!-- 会话列表 -->
        <div class="list-box" style="flex: 0 90%;max-height: 80vh">
          <div class="list-box-title">
            <el-icon size="20px"><Comment /></el-icon>
            近期检查
          </div>
          <transition-group name="fade-list-to-right" class="h100">
            <li :class="{ itemActive: sessionData.chatId === item.chatId }"
                class="pointer rounded-md"
                v-for="(item,index) in sessionListData"
                :key="index">
                <span @click="clickSessionListItem(item)" style="flex: 1;line-height: 36px;">{{item.chatTitle || '无标题'}}</span>
                <el-dropdown placement="bottom">
                  <div class="custom-dropdown" style="flex-shrink: 0;width: 32px;display: flex;justify-content: center;align-items: center;">
                    <el-icon size="20px"><MoreFilled /></el-icon>
                  </div>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item @click="deleteSessionListItem(item)" style="display: flex;justify-content: center;align-items: center;color: red;">
                        <el-icon size="16px"><Delete /></el-icon>
                        <span style="font-size: 14px;">删除</span>
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
            </li>
          </transition-group>
        </div>
        <!-- 加载更多页 -->
        <div class="session-bottom w100" style="flex: 0 5%;display: flex;justify-content: center;" v-if="(currentPage*pageSize)<totals">
          <el-button type="primary" link @click="getMoreComplianceChatList" style="font-size: 14px;">加载更多</el-button>
        </div>
      </el-aside>
      <el-container style="overflow: hidden;">
        <!-- 头部工具选择 -->
        <el-header class="main-header">
          <el-select v-model="sessionData.complianceType" style="width: 165px" 
            class="custom-select"
            :disabled="buttonLock"
          >
            <template #prefix>
              <el-icon><ElSwitch /></el-icon>
            </template>
            <el-option
              v-for="item in complianceTypeList"
              :key="item.itemValue"
              :label="item.itemName"
              :value="item.itemValue"
            >
            </el-option>
          </el-select>
        </el-header>
        <el-main class="main-content" id="main-content">
          <!-- 检查内容输出 -->
          <div v-if="sessionRecordData.length<=0" style="display: flex;justify-content: center;align-items: center;padding: 180px;flex-direction: column;">
            <span style="font-size: 32px;">我是企业合规专家，很高兴见到你！</span>
            <span style="font-size: 20px;margin-top: 30px;color: #404040;" v-text="defaultContent"></span>
          </div>
          <div v-if="sessionRecordData.length>0" class="dialog-area" ref="sessionWindow" style="height: 300px;">
            <div class="chat-content" ref="chatContent" @scroll="onScroll">
              <div v-for="(item,index) in sessionRecordData" :key="index">
                <window-session-content
                  :role="item.chatRole"
                  :item-data="item"
                  :user-info="userData"
                  @handleFlushThisSession="handleFlushThisSession"
                ></window-session-content>
              </div>
              <div class="spacer"></div>
            </div>
          </div>
        </el-main>
        <!-- 检查输入信息 -->
        <el-footer height="100px" class="file-upload-section">
          <div class="chat-container">
            <!-- 等待检查的文件 -->
            <div class="top-file" v-if="!currentFileData.fileId">
                <span style="line-height: 50px;color: #909399;">{{defaultFilePrompt}}</span>
            </div>
            <div class="top-file" v-if="currentFileData.fileId">
              <div class="file-list">
                <div class="file-info">
                  <button class="delete-btn" @click="deleteCurrentFile(currentFileData)">
                    <el-icon size="12px"><CloseBold /></el-icon>
                  </button>
                  <div style="display: flex;align-items: center;white-space: nowrap;overflow: hidden;text-overflow: ellipsis;">
                    <el-icon><Document /></el-icon>
                    <div class="file-name">{{currentFileData.fileName}}</div>
                  </div>
                </div>
              </div>
            </div>
            <!-- 输入内容 -->
            <div class="bottom-operate">
              <!-- 属性选择 -->
              <div class="left-select">
                <!-- 选择模型 -->
                <el-select v-model="sessionData.modelId" 
                  style="width: 140px"
                  class="custom-select-down"
                  :disabled="buttonLock"
                >
                  <template #prefix>
                    <el-icon style="font-size: 20px;"><Cpu /></el-icon>
                  </template>
                  <el-option
                    v-for="item in modelList"
                    :key="item.modelId"
                    :label="item.modelName"
                    :value="item.modelId"
                  />
                </el-select>
                <!-- 选择合同类型 -->
                <el-select v-model="sessionData.contractType" 
                  style="width: 120px;margin-left: 20px;"
                  class="custom-select-down"
                  :disabled="buttonLock"
                >
                  <template #prefix>
                    <el-icon style="font-size: 20px;"><Notebook /></el-icon>
                  </template>
                  <el-option
                    v-for="item in contractTypeList"
                    :key="item.itemValue"
                    :label="item.itemName"
                    :value="item.itemValue"
                  />
                </el-select>
              </div>
              <!-- 操作按钮 -->
              <div class="right-send">
                <el-upload
                  class="upload-demo"
                  ref="upload"
                  style="width: 30px; float: left; margin-right: 6px;"
                  :action="$yqvutil.getContextPath()+'yqvservice/mfile/upload'"
                  :on-success="handleAvatarSuccess"
                  :on-error="handleAvatarError"
                  multiple
                  :limit="1"
                  :accept="defaultFileType"
                >
                  <el-button size="large" round style="width: 38px;height: 38px;" :disabled="currentFileData.fileId || loadingLine">
                    <el-icon style="font-size: 28px;"><UploadFilled /></el-icon>
                  </el-button>
                </el-upload>
                <el-button size="large" type="primary" round @click="sendInputMessage" style="width: 60px;height: 38px;margin-left: 20px;" :disabled="loadingLine">
                  <el-icon style="font-size: 28px;"><Promotion /></el-icon>
                </el-button>
              </div>
            </div>
          </div>
        </el-footer>
      </el-container>
      <!-- 用户信息 -->
      <div class="user-actions">
        <el-dropdown size="large">
          <span class="el-dropdown-link custom-dropdown">
            <div style="cursor: pointer;width: 38px;">
              <el-avatar :size="35" :src="setImgM" />
            </div>
            <span style="font-weight: 600;color: black;flex: 1;margin-left: 6px;">{{userData.userName}}</span>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="showUserPasswordDialog">
                <el-icon><Lock /></el-icon>
                修改密码
              </el-dropdown-item>
              <el-dropdown-item @click="userLogout">
                <el-icon><SwitchButton /></el-icon>
                退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-container>
  </div>
</template>
  
<script>
import { UploadFilled, Promotion,Document,Lock,SwitchButton,Plus,Comment,Delete,MoreFilled,Notebook,Cpu,CloseBold,Switch as ElSwitch} from '@element-plus/icons-vue'
import defaultImgM from "@/assets/img/userM.jpg";
import headerApi from "@/yqvapi/header/header.api";
import WindowSessionContent from "@/components/chatgpt/window/chat/WindowContent";
import {EventSourcePolyfill} from 'event-source-polyfill'
export default {
  name: 'ComplianceChat',
  components: {
    UploadFilled,
    Promotion,
    Document,
    Lock,
    SwitchButton,
    Plus,
    Comment,
    Delete,
    MoreFilled,
    Notebook,
    Cpu,
    CloseBold,
    ElSwitch,
    WindowSessionContent
  },
  data() {
    return {
      setImgM: defaultImgM,
      headerImgUrl: require("@/assets/img/titleicon2.svg"),
      userData: {},
      complianceTypeList:[],
      contractTypeList:[],
      modelList:[],
      defaultContent:"我可以帮你识别合同中的法律风险，又能结合公司制度规避业务风险，请把你的合同内容交给我吧~",
      defaultFilePrompt:"请选择您要识别的文件，支持doc、docx格式文件",
      defaultFileType:".doc,.docx",
      newChat: true,
      isAutoScroll: true,
      loadingLine: false,
      complianceType:"",
      currentFileData:{},
      sessionData:{
        complianceType:"",
        modelId:"",
        contractType:"",
      },
      //会话列表
      currentPage: 1,
      pageSize: 15,
      totals: 0,
      sessionListData:[],
      //检查回复记录
      sessionRecordData: [],
      /** 密码修改 */
      dialogUserPasswordShow: false,
      judgeOld: false,
      judgeNewOne: false,
      judgeNewTwo: false,
      newPasswordOneText: "请填写新密码",
      newPasswordTwoText: "请填写新密码",
      userPasswordData: {
        userAccount: "",
        oldPassword: "",
        newPasswordOne: "",
        newPasswordTwo: "",
      },
      showPasswordOld: false,
      showPasswordNewOne: false,
      showPasswordNewTwo: false,
    };
  },
  created() {
    this.initHeader();
  },
  watch: {
    //判断旧密码
    "userPasswordData.oldPassword"() {
      this.testOldPassword();
    },
    "userPasswordData.newPasswordOne"() {
      this.testnewPasswordOne();
    },
    "userPasswordData.newPasswordTwo"() {
      this.testnewPasswordOne();
    },
    sessionRecordData: {
      deep: true,
      handler(val) {
        if (val != null) {
          this.$nextTick(() => {
            this.scrollBottom();
          })
        }
      }
    },
  },
  methods: {
    //初始化信息
    initHeader() {
      let vm = this;
      headerApi.getHeaderInfo(function (response) {
        if (response.status == 200) {
          vm.userData = response.data.userData;
          vm.userPasswordData.userAccount = response.data.userData.userAccount;
          vm.complianceTypeList = response.data.otherMap["complianceTypeList"];
          vm.contractTypeList = response.data.otherMap["contractTypeList"];
          vm.modelList = response.data.otherMap["modelList"];
          if(!vm.sessionData.complianceType){
              vm.sessionData.complianceType = vm.complianceTypeList[0].itemValue;
              vm.defaultContent = vm.complianceTypeList[0].expandValue1;
              vm.defaultFilePrompt = vm.complianceTypeList[0].expandValue2;
              vm.defaultFileType = vm.complianceTypeList[0].expandValue3;
          }
          if(!vm.sessionData.contractType){
              vm.sessionData.contractType = vm.contractTypeList[0].itemValue;
          }
          if(!vm.sessionData.modelId){
              vm.sessionData.modelId = vm.modelList[0].modelId;
          }
          vm.getComplianceChatList();
        } else {
          vm.$message({
            showClose: true,
            message: response.data.message,
            type: "error",
          });
        }
      });
    },
    /********************************用户退出登录*****************************/
    userLogout() {
      let vm = this;
      this.$confirm("此操作将退出登录, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        closeOnClickModal: false,
      })
      .then(() => {
        clearInterval(vm.timer);
        localStorage.removeItem("time");
        headerApi.userLogout(function (response) {
        if (response.status == 200) {
            vm.$router.push({ name: "login" });
            vm.$yqvutil.removeToken();
        } else {
          vm.$message({
            showClose: true,
            message: response.data.message,
            type: "error",
          });
        }
      }).catch(function (error) {
        if (error.code == 0) {
            vm.$message({
              showClose: true,
              message: error.message,
              type: "error",
            });
          }
        });
      })
      .catch(() => {});
    },
    /********************************修改用户密码*****************************/
    showUserPasswordDialog() {
      this.dialogUserPasswordShow = true;
    },
    closeUserPasswordDialog() {
      this.dialogUserPasswordShow = false;
    },
    updateUserPassword() {
      let vm = this;
      //判断是否填写新密码
      this.testOldPassword();
      this.testnewPasswordOne();
      if (!this.judgeOld && !this.judgeNewOne && !this.judgeNewTwo) {
        let data = {
          loginPwd: this.userPasswordData.oldPassword,
          loginPwdNew: this.userPasswordData.newPasswordOne,
        };
        this.$http.get(vm.$yqvutil.getContextPath()+`yqvservice/musercenter/pwd/updateinit`)
        .then((response) => {
          if (response.status == 200) {
            data.loginPwd = this.$yqvutil.getPSRsaCode(data.loginPwd,response.data.data)
            data.loginPwdNew = this.$yqvutil.getPSRsaCode(data.loginPwdNew,response.data.data)
            this.$http.post(vm.$yqvutil.getContextPath()+"yqvservice/musercenter/pwd/update", data)
            .then(function (response) {
              if (response.status == 200) {
                vm.$router.push({ name: "login" });
                vm.$yqvutil.removeToken();
              }
            }).catch(function (error) {});
          }
        }).catch((error) => {});
      }
    },
    //判断旧密码
    testOldPassword() {
      if (!this.userPasswordData.oldPassword) {
        this.judgeOld = true;
      } else {
        this.judgeOld = false;
      }
    },
    //判断新密码
    testnewPasswordOne() {
      let patrnValue = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*()_+`\\-={}:\";'<>?,.\\/]).{8,16}$";
      let failDesc = "用户密码至少8-16位长度并同时包含字母、数字、特殊字符";
      let patrn = new RegExp(patrnValue);
      if (!patrn.test(this.userPasswordData.newPasswordOne)) {
        this.judgeNewOne = true;
        this.newPasswordOneText = failDesc;
      } else {
        this.judgeNewOne = false;
      }
      if (!patrn.test(this.userPasswordData.newPasswordTwo)) {
        this.judgeNewTwo = true;
        this.newPasswordTwoText = failDesc;
      } else {
        this.judgeNewTwo = false;
      }
      if (
        patrn.test(this.userPasswordData.newPasswordOne) &&
        patrn.test(this.userPasswordData.newPasswordTwo)
      ) {
        if (this.userPasswordData.newPasswordOne !== this.userPasswordData.newPasswordTwo) {
          this.judgeNewTwo = true;
          this.newPasswordTwoText = "新密码必须相同";
          this.judgeNewOne = true;
          this.newPasswordOneText = "新密码必须相同";
        }
      }
    },
    /********************************查询检查列表*****************************/ 
    getMoreComplianceChatList() {
      this.currentPage = this.currentPage+1;
      this.getComplianceChatList(true);
    },
    getComplianceChatList(append) {
      let vm = this;
      let data = {chatTitle:""};
      vm.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/mchat/page?currentPage=${this.currentPage}&pageSize=${this.pageSize}`,data)
        .then(function (response) {
          if (response.status == 200) {
            if(response.data && response.data.result.length>0){
              vm.totals = response.data.page.totalRows;
              if(append){
                vm.sessionListData = vm.sessionListData.concat(response.data.result);
              }else{
                vm.sessionListData = response.data.result;
              }
            }
          }
        })
        .catch(function (error) {});
    },
    /********************************获取对话数据*****************************/
    clickSessionListItem(item){
      let vm = this;
      let data = {"chatId":item.chatId};
      vm.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/mchat/find`,data)
        .then(function (response) {
          if (response.status == 200) {
            vm.newChat = false;
            vm.sessionData = response.data;
            vm.currentFileData = {};
            vm.getChatRecordList();
          }
        })
        .catch(function (error) {});
    },
    deleteSessionListItem(item){
      let vm = this;
      let data = {"chatId":item.chatId};
      vm.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/mchat/delete`,data)
        .then(function (response) {
          if (response.status == 200) {
            vm.sessionListData = [];
            vm.currentPage = 1;
            vm.getComplianceChatList();
            vm.handleCreateSession();
            vm.$message({
              showClose: true,
              message: "删除成功",
              type: "success",
            });
          }
        })
        .catch(function (error) {});
    },
    getChatRecordList() {
      let vm = this;
      if(vm.sessionData.chatId){
        vm.buttonLock = true;
        let data = {"chatId":vm.sessionData.chatId};
        vm.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/mchat/record/list`,data)
          .then(function (response) {
            if (response.status == 200) {
              vm.sessionRecordData = response.data;
            }
          })
          .catch(function (error) {});
      }
    },
    /********************************新增检查*****************************/
    handleCreateSession(){
      let vm = this;
      if(!vm.newChat){
        vm.newChat = true;
        vm.recordCurrentPage = 1;
        vm.sessionData = {
          complianceType:vm.complianceTypeList[0].itemValue,
          modelId:vm.modelList[0].modelId,
          contractType:vm.contractTypeList[0].itemValue,
        };
        vm.sessionRecordData = [];
        vm.currentFileData = {};
        vm.buttonLock = false;
      } else {
        vm.$message({
          showClose: true,
          message: "当前已是新检查",
          type: "warning",
        });
      }
    },
    /********************************文件上传成功回调*****************************/
    handleAvatarSuccess(res, file) {
        this.$refs.upload.clearFiles();
        let vm = this;
        vm.$message({
            showClose: true,
            message: "上传成功",
            type: "success",
        });
        vm.currentFileData = res.data;
    },
    handleAvatarError(err, file, fileList) {
        this.$message.error(JSON.parse(err.message).message);
    },
    deleteCurrentFile(file){
      let vm = this;
      vm.currentFileData = {};
      vm.$refs.upload.clearFiles();
    },
    /********************************发送文件检查请求*****************************/
    scrollBottom() {
      this.$nextTick(() => {
        if (!this.isAutoScroll) return;
        let scrollDom = document.getElementById('main-content');
        if (scrollDom) {
          scrollDom.scrollTop = scrollDom.scrollHeight;
        }
      });
    },
    sendInputMessage() {
      const vm = this;
      let fileList = [vm.currentFileData];
      if (vm.currentFileData.fileId) {
        if (window.EventSource) {
          let sseEvent = new EventSourcePolyfill(vm.$yqvutil.getContextPath()+`yqvservice/mchat/sse/connect`);
          sseEvent.onmessage = function (event) {
            let sessionId = event.lastEventId;
            let dataStr = event.data;
            let data = {};
            if (dataStr) {
                try {  
                    data = JSON.parse(event.data);   
                } catch (error) {  
                  dataStr = event.data; 
                }
            }
            if (sessionId == dataStr){
              //建立连接获取sseId
              vm.sessionData.sessionId=sessionId;
              vm.buttonLock=true;
              vm.loadingLine = true;
              vm.flushSendData(fileList);
              vm.sendTextPrompt(fileList);
              vm.currentFileData = {};
            }else if(data.done){
              //消息发送完成
              sseEvent.close();
              sseEvent = null;
              vm.loadingLine = false;
            }else{
              let popData = vm.sessionRecordData[vm.sessionRecordData.length - 1];
              let dataText = data.content;
              dataText = dataText.replaceAll("↖emsp↘"," ");
              dataText = dataText.replaceAll("↖br↘","\n");
              dataText = dataText.replaceAll("↖tab↘","   ");
              popData.content += dataText;
            }
          };
          sseEvent.onerror = function (event) {
            sseEvent.close();
            sseEvent = null;
            vm.currentFileData = {};
            vm.loadingLine = false;
          };
        } else {
          this.$message.error("浏览器版本不支持sse，请更换浏览器")
        }
      }else{
        this.$message.warning("文件不能为空")
      }
    },
    flushSendData(fileList) {
      let vm = this;
      if (vm.sessionData.sessionId && fileList && fileList.length > 0) {
        let userData = {
          chatRole: 'user',
          content: fileList[0].fileName,
          createDate: new Date().toLocaleString()
        }
        vm.sessionRecordData.push(userData);
        let assistantData = {
          chatRole: 'assistant',
          content: "",
          createDate: new Date().toLocaleString()
        }
        vm.sessionRecordData.push(assistantData);
      }
    },
    sendTextPrompt(fileList) {
      let vm = this;
      if (vm.sessionData.sessionId){
        let data = {
          chatId: vm.sessionData.chatId,
          sessionId: vm.sessionData.sessionId,
          complianceType: vm.sessionData.complianceType,
          contractType: vm.sessionData.contractType,
          modelId: vm.sessionData.modelId,
          fileList: fileList
      };
      vm.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/mchat/sse/send`,data)
      .then(function(response) {
        if (response.status == 200) {
          //刷新列表
          vm.newChat = false;
          vm.getComplianceChatList(false);
          if(response.data.code!=1){
            vm.loadingLine = false;
            vm.$message({
              showClose: true,
              message: "请求失败",
              type: "error"
            });
          }
        }
      })
      .catch(function(error) {});
      }
    },
    handleFlushThisSession(sessionId){
      this.$emit('handleFlushThisSession',sessionId)
    },
  }
}
</script>
  
<style>
.ComplianceChat{
  height: 100vh;
  .main-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    position: relative;
    background-color: #f0f0f0;
    padding: 20px;
  }
  .main-header {
    background-color: #f0f0f0;
    display: flex;
    align-items: center;
    padding: 0 40px;
    height: 60px;
    box-sizing: border-box;
  }
  .el-breadcrumb {
    font-size: 16px;
  }
  .left-sidebar {
    background-color: #edf2fa;
    height: 100vh;
    border-right: 1px solid #e0e0e0;
    padding: 10px;
  }
  .header {
    display: flex;
    align-items: center;
    padding: 20px;
    border-bottom: 1px solid #e0e0e0;
    height: 60px;
    box-sizing: border-box;
  }
  .createSession{
    width: 100%;
    display: flex;
    flex-wrap: wrap;
    overflow: hidden;
    justify-content: center;
    align-items: center;
  }
  .createSession>div{
    width: 100%;
    height: 32px;
    max-height: 32px;
    display: flex;
    flex-wrap: wrap;
    overflow: hidden;
    justify-content: center;
    align-items: center;
    border: 1px #02020280 dashed;
    transition: all 0.2s ease;
    margin-bottom: 16px;
    border-radius: 16px;
    border-color: #5675f5;
    color: #5675f5;
    background-color: #d6e4f3;
    font-weight: 500;
  }
  .createSession span{
    font-size: 16px;
    padding: 0 2px;
  }
  .createSession>div:hover{
    border: 1px dashed #02020280;
    transform: scale(0.99);
  }
  .pointer{
    cursor: pointer;
  }
  .custom-dropdown:focus-visible{
    outline: unset;
  }
  .list-box {
    position: relative;
    width: 100%;
    scrollbar-width: none;
    -ms-overflow-style: none;
    overflow-x: hidden;
    overflow-y: auto;
    li {
      list-style: none;
      position: relative;
      width: 100%;
      height: 38px;
      display: flex;
      align-items: center;
      margin: 4px 0;
      border-radius: 16px;
      &:hover {
        background: #cbc9c9;
      }
    }
    li.pointer.itemActive{
      background-color: #d6e4f3;
    }
    li span {
      max-width: 75%;
      font-size: 14px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      margin-left: 20px;
      color: #5c5a5a;
    }
  }
  .list-box::-webkit-scrollbar {
    width: 0;
    background: transparent;
    display: none;
  }
  .list-box-title{
    line-height: 32px;
    font-size: 14px;
    font-weight: 500;
    color: #4c4a4a;
    display: flex;
    align-items: center;
  }
  .user-actions {
    position: fixed;
    bottom: 10px;
    left: 10px;
    width: 200px;
    display: flex;
    z-index: 2000;
  }
  .el-dropdown-link {
    width: 180px;
    cursor: pointer;
    color: var(--el-color-primary);
    display: flex;
    align-items: center;
    display: flex;
  }
  .file-upload-section {
    background-color: #ffffff;
    display: flex;
    align-items: center;
    gap: 15px;
    padding: 0 40px;
    border-top: 1px solid #e0e0e0;
    box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
  }
  .contract-type {
    font-size: 16px;
    color: #606266;
    white-space: nowrap;
  }
  .upload-demo {
    display: inline-flex;
    gap: 8px;
  }
  .chat-container {
    display: grid;
    grid-template-rows: 1fr 1fr;
    width: 100%;
  }
  .top-file {
    height: 50px;
  }
  .bottom-operate {
    height: 50px;
    display: flex;
  }
  .left-select {
    flex: 1;
  }
  .right-send{
    flex: -1px;
  }
  .file-list{
    background-color: aliceblue;
    border-radius: 12px;
    width: 300px;
    transition: background-color .3s;
    position: relative;
    margin-top: 8px;
  }
  .file-info{
    align-items: center;
    padding: 8px;
    display: flex;
  }
  .file-name{
    text-overflow: ellipsis;
    white-space: nowrap;
    color: #262626;
    min-height: 20px;
    margin: 0;
    font-size: 14px;
    font-weight: 500;
    line-height: 20px;
    overflow: hidden;
  }
  .passwordModify-box-left {
    width: 400px;
    height: 100%;
    float: left;
    img {
      width: 280px;
      height: 208px;
      margin-top: 40px;
      margin-left: 50px;
    }
  }
  .passwordModify-box-right {
    width: 341px;
    height: 100%;
    overflow: hidden;
    margin: 0px auto;
    .abox {
      margin-top: 28px;
      width: 100%;
      float: left;
      position: relative;
      .abox-lableName {
        width: 76px;
        float: left;
        line-height: 32px;
        span {
          color: red;
          font-size: 14px;
        }
      }
      .error-point {
        position: absolute;
        left: 76px;
        top: 35px;
        color: red;
        font-size: 12px;
      }
      .input-box {
        float: left;
        width: calc(100% - 101px);
        margin-right: 25px;
        position: relative;
        .el-select,
        .el-select--small,
        .el-date-editor {
          width: 100%;
        }
        .svg-inline--fa {
          position: absolute;
          right: 10px;
          top: 9px;
          cursor: pointer;
        }
      }
    }
  }
  .custom-select-down{
    .el-select__prefix {
      color: #4D6BFE !important;
    }
    .el-select__suffix {
      color: #4D6BFE !important;
    }
    .el-select__wrapper {
      background-color: #DBEAFE !important;
      font-size: 14px !important;
      box-shadow: 0 0 0 1px #7c6cdb !important;
      border-radius: 12px !important;
    }
    .el-select__placeholder{
      color: #4D6BFE !important;
    }
    .is-transparent {
      color: #4D6BFE !important;
    }
    .el-select__wrapper.is-focused {
      border-color: #f0f0f0 !important;
      box-shadow: 0 0 0 1px #f0f0f0 !important;
    }
  }
  .custom-select {
    .el-select__prefix {
      color: #4D6BFE !important;
    }
    .el-select__suffix {
      color: #4D6BFE !important;
    }
    .el-select__wrapper {
      background-color: #DBEAFE !important;
      font-size: 18px !important;
      box-shadow: 0 0 0 1px #7c6cdb !important;
      border-radius: 20px !important;
    }
    .el-select__placeholder{
      color: #4D6BFE !important;
    }
    .is-transparent {
      color: #4D6BFE !important;
    }
    .el-select__wrapper.is-focused {
      border-color: #f0f0f0 !important;
      box-shadow: 0 0 0 1px #f0f0f0 !important;
    }
  }

  .delete-btn {
    position: absolute;
    top: 10px;
    right: 5px;
    cursor: pointer;
    background-color: #959595;
    color: white;
    border: none;
    border-radius: 50%;
    width: 16px;
    height: 16px;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}
</style>
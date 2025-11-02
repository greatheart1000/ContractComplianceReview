<template>
  <div class="window-session-content w100"
       :class="{ user: role === 'user' }"
       @mouseenter="handleMouseEnter"
       @mouseleave="handleMouseLeave"
  >
    <div class="info">
      <el-image :src="userImg"></el-image>
      <span v-if="role === 'user'">{{userInfo.userName}}</span>
      <span v-else>合规助手</span>
    </div>
    <div class="content rounded-md">
      <MarkdownView ref="mv" :value="itemData.content"></MarkdownView>
    </div>
    <div class="chat-tools">
      <div class="create-time">{{$yqvutil.setFormTime(itemData.createDate,1)}}</div>
      <el-tooltip content="复制" effect="dark" placement="bottom">
        <el-icon @click="$yqvutil.copyText(itemData.content,'复制成功')" class="icon"><CopyDocument /></el-icon>
      </el-tooltip>
    </div>
  </div>
</template>

<script>
  import {mapState} from "vuex";
  import { CopyDocument} from '@element-plus/icons-vue'
  import MarkdownView from '@/components/chatgpt/markdownview/index'
  import headerApi from "@/yqvapi/header/header.api";
  import defaultImgM from "@/assets/img/userM.jpg";
  export default {
    name: "WindowContent",
    components: {
      MarkdownView,
      CopyDocument
    },
    props: {
      userInfo: { type: String, required: true },
      role: { type: String, required: true },
      itemData: { type: Object, required: true },
      openTools: { type: Boolean, default: true }
    },
    data(){
      return{
        toolsShow: false,
        userImg: require('/src/assets/img/img-chat-gpt.png'),
      }
    },
    computed: {
      
    },
    mounted() {
      if(this.role==="user"){
        this.userImg = defaultImgM;
      }
    },
    created() {
    },
    watch: {
      
    },
    methods:{
      
    }
  }
</script>

<style scoped lang="less">
  .window-session-content {
    padding: 16px 20px 0 20px;
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    color: #000000;

    &:hover{
      background-color: rgba(0, 0, 0, 0.04);
    }
  }
  .window-session-content.user {
    align-items: flex-end;
  }
  .info{
    display: flex;
    justify-content: flex-start;
    align-items: center;
    span{
      font-size: 16px;
    }
  }
  ::v-deep.window-session-content .info .el-image img {
    width: 34px;
    height: 34px;
    border-radius: 50%;
    vertical-align: middle;
    margin-right: 10px;
    margin-left: 10px;
  }
  .content{
    margin-top: 10px;
    background: rgb(240, 240, 240);
    padding: 5px;
  }
  .content-html{
    padding: 8px;
    word-wrap: break-word;
    font-size: 15px;
    max-width: 60vw;
  }
  .window-session-content.user .content {
    background: #caf1c8;
    border-radius: 6px;
  }
  .window-session-content.user .content-html {
    color: black !important;
  }
  ::v-deep.window-session-content.user .content .markdown-body{
    color: black !important;
  }
  ::v-deep .chat-tools{
    display: flex;
    justify-content: flex-start;
  }
  ::v-deep.window-session-content.user .chat-tools {
    justify-content: flex-end;
  }
  ::v-deep .default-theme p {
    line-height: 26px !important;
    padding: 2px 0 !important;
    margin: 0;
  }
  ::v-deep .md-editor{
    background-color: #00000000 !important;
  }
  .chat-tools{
    width: 100%;
    max-height: 40px;
    height: 40px;
    display: flex;
    align-items: center;
  }
  .create-time{
    font-size: 16px;
    color: #929191;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-left: 10px;
    margin-right: 20px;
  }
  .chat-tools .icon{
    cursor: pointer;
    font-size: 18px;
    margin-left: 10px;
    color: #605d5d;
  }
</style>

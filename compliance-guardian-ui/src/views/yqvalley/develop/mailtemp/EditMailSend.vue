<template>
  <div>
    <!-- 操作按钮 -->
    <div class="btn-box" style="margin-top: 10px">
      <div class="new" @click="sendMailTemp" v-if="$yqvutil.getVerifyAuthority('yqv.mail.manual_send_mail')">
        <font-awesome-icon icon="fas fa-paper-plane" />发送
      </div>
    </div>
    <!-- 邮件内容 -->
    <div class="editFormList" style="width: 96%;margin: auto;">
      <div class="abox" style="margin-top: 8px">
        <span class="abox-lableName"><span>*</span>收件邮箱：</span>
        <div class="input-box">
          <el-select-v2
            v-model="mailTemp.to"
            :options="options"
            placeholder="请填写收件邮箱"
            style="width: 100%"
            allow-create
            filterable
            multiple
            clearable
            :reserve-keyword="false"
          />
        </div>
      </div>
      <div class="abox" style="margin-top: 8px">
        <span class="abox-lableName"
          ><span style="color: white">*</span>抄送邮箱：</span
        >
        <div class="input-box">
          <el-select-v2
            v-model="mailTemp.cc"
            :options="options"
            placeholder="请填写抄送邮箱"
            style="width: 100%"
            allow-create
            filterable
            multiple
            clearable
            :reserve-keyword="false"
          />
        </div>
      </div>
      <div class="abox" style="width: 100%; margin-top: 18px">
        <span class="abox-lableName"><span>*</span>邮件标题：</span>
        <div class="input-box">
          <el-input
            style="width: 100%"
            maxlength="100"
            v-model="mailTemp.subject"
            placeholder="请填写邮件标题"
          ></el-input>
        </div>
      </div>
      <div class="abox" style="width: 100%;height: 100%; margin-top: 18px;margin-bottom: 18px;">
        <Tinymce :value="editorContent" :height="410" @getContent="changeHtmlData" />
      </div>
    </div>
  </div>
</template>

<script>
import Tinymce from '@/components/tinymce'
export default {
  name: "editMailSend",
  components: {
    Tinymce,
  },
  props: ["templateCode","templateDesc","templateTitle","templateContent"],
  data() {
    return {
      mailTemp: {
        to: [],
        cc: [],
        subject: "",
        content: "",
      },
      editorContent: "",
      options: []
    };
  },
  mounted() {
    if (this.templateCode != null && this.templateCode != "") {
      this.mailTemp.subject = this.templateTitle;
      this.mailTemp.content = this.templateContent;
      this.editorContent = this.templateContent;
    }
  },
  methods: {
    //监听子组件抛出的事件
    changeHtmlData(text) {
      this.mailTemp.content = text;
    },
    /********************************发送邮件*****************************/
    sendMailTemp() {
      let data = this.mailTemp;
      //判断是否可以保存
      if (data.to && data.to.length>0 && data.subject && data.content) {
        let message = "发送成功";
        let url = this.$yqvutil.getContextPath()+"yqvservice/mailtemp/send";
        let vm = this;
        this.$http
          .post(url, data)
          .then(function (response) {
            if (response.status == 200) {
              vm.$parent.handleClick({ label: "邮件模板" });
              vm.$message({
                showClose: true,
                message: message,
                type: "success",
              });
            }
          })
          .catch(function (error) {});
      } else {
        this.$message({
          showClose: true,
          message: "请填写邮件信息",
          type: "warning",
        });
      }
    },
  },
};
</script>

<style scoped lang="less">
</style>
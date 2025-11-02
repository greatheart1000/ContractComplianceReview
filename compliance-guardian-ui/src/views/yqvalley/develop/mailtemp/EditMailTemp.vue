<template>
  <div>
    <!-- 操作按钮 -->
    <div class="btn-box" style="margin-top: 10px">
      <div class="new" @click="saveMailTemp">
        <font-awesome-icon icon="fas fa-save" />保存
      </div>
    </div>
    <div class="editFormList" style="width: 96%;margin: auto;">
      <div class="abox" style="margin-top: 8px">
        <span class="abox-lableName"><span>*</span>模板编号：</span>
        <div class="input-box">
          <el-input
            style="width: 100%"
            maxlength="100"
            v-model="mailTemp.templateCode"
            placeholder="请填写模板编号"
          ></el-input>
        </div>
      </div>
      <div class="abox" style="margin-top: 8px">
        <span class="abox-lableName"
          ><span style="color: white">*</span>模板说明：</span
        >
        <div class="input-box">
          <el-input
            style="width: 100%"
            maxlength="100"
            v-model="mailTemp.templateDesc"
            placeholder="请填写模板说明"
          ></el-input>
        </div>
      </div>
      <div class="abox" style="width: 100%; margin-top: 18px">
        <span class="abox-lableName"><span>*</span>邮件标题：</span>
        <div class="input-box">
          <el-input
            style="width: 100%"
            maxlength="100"
            v-model="mailTemp.templateTitle"
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
  name: "EditMailTemp",
  components: {
    Tinymce,
  },
  props: ["templateCode","templateDesc","templateTitle","templateContent"],
  data() {
    return {
      edit: false,
      mailTemp: {
        templateCode: "",
        templateDesc: "",
        templateTitle: "",
        templateContent: "",
      },
      editorContent: ""
    };
  },
  mounted() {
    if (this.templateCode != null && this.templateCode != "") {
      this.edit = true;
      this.mailTemp.templateCode = this.templateCode;
      this.mailTemp.templateDesc = this.templateDesc;
      this.mailTemp.templateTitle = this.templateTitle;
      this.mailTemp.templateContent = this.templateContent;
      this.editorContent = this.templateContent;
    } else {
      this.edit = false;
      this.mailTemp.templateCode = "";
      this.mailTemp.templateDesc = "";
      this.mailTemp.templateTitle = "";
      this.mailTemp.templateContent = "";
    }
    
  },
  methods: {
    //监听子组件抛出的事件
    changeHtmlData(text) {
      this.mailTemp.templateContent = text;
    },
    /********************************保存配置*****************************/
    saveMailTemp() {
      let data = this.mailTemp;
      //判断是否可以保存
      if (data.templateCode && data.templateTitle && data.templateContent) {
        let message = "新增成功";
        let url = this.$yqvutil.getContextPath()+"yqvservice/mailtemp/insert";
        if (this.edit) {
          url = this.$yqvutil.getContextPath()+"yqvservice/mailtemp/update";
          message = "修改成功";
        }
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
              vm.$parent.getMailTempList();
            }
          })
          .catch(function (error) {});
      } else {
        this.$message({
          showClose: true,
          message: "请填写邮件模板信息",
          type: "warning",
        });
      }
    },
  },
};
</script>

<style scoped lang="less">
</style>
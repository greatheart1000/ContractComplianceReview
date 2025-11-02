<template>
  <main>
    <editor v-model="htmlContent" :init="init" :disabled="disabled" :id="tinymceId"></editor>
  </main>
</template>
<script lang="ts" setup>
import { reactive, ref, onMounted, watch } from 'vue'
import yqvutil from "@/common/yqvalley";
import axios from "@/axios";
/**************************************TinyMCE组件**************************************/
import tinymce from 'tinymce/tinymce'
import Editor from '@tinymce/tinymce-vue'
import 'tinymce/skins/content/default/content.css'
import 'tinymce/themes/silver'
import 'tinymce/themes/silver/theme'
import 'tinymce/icons/default'
import "tinymce/icons/default/icons"
import 'tinymce/models/dom'
/**************************************TinyMCE插件**************************************/
import 'tinymce/plugins/importcss'//导入css插件
import 'tinymce/plugins/image'//插入图片插件
import 'tinymce/plugins/media'//插入视频插件
import 'tinymce/plugins/table'//插入表格插件
import 'tinymce/plugins/lists'//列表插件
import 'tinymce/plugins/advlist'//高级列表插件
import 'tinymce/plugins/autoresize'//自动调整高度
import 'tinymce/plugins/wordcount' //字数统计插件
import "tinymce/plugins/code";//查看源码
import "tinymce/plugins/fullscreen";//全屏
import 'tinymce/plugins/link'//超链接
import 'tinymce/plugins/preview'//预览
import "tinymce/plugins/searchreplace"//查询替换
import "tinymce/plugins/pagebreak"//分页
const pluginsList = "image media table lists advlist autoresize wordcount code fullscreen link preview searchreplace pagebreak";
const emits = defineEmits(["getContent"])
const props = defineProps({
  id: {
    type: String,
    default: function() {
      return 'yqv-tinymce-' + +new Date() + ((Math.random() * 1000).toFixed(0) + '')
    }
  },
  value: {
    type: String,
    default: () => {
      return ""
    },
  },
  uploadUrl: {
    type: String,
    default: () => {
      return yqvutil.getContextPath()+'yqvservice/file/image/upload'
    },
  },
  //菜单栏
  menubar: {
    type: [Boolean, String],
    default: 'file edit insert view format table'
  },
  //是否禁用
  disabled: {
    type: Boolean,
    default: false,
  },
  //工具栏
  toolbar: {
    type: [String, Array],
    default: "searchreplace bold italic underline strikethrough alignleft aligncenter alignright outdent indent pagebreak code codesample', 'hr bullist numlist link image media table forecolor backcolor preview print fullscreen",
  },
  height: {
    type: Number,
    default: 300
  },
})
//用于接收外部传递进来的富文本
const htmlContent = ref(props.value)
const tinymceId = ref(props.id)
const uploadUrl = ref(props.uploadUrl)
//定义一个对象 init初始化
const init = reactive({
  selector: '#' + tinymceId.value, //富文本编辑器的id,
  language_url: "/tinymce/langs/zh-Hans.js", //语言包的路径
  language: "zh-Hans", //语言
  skin_url: "/tinymce/skins/ui/oxide", //skin路径
  branding: false, //是否禁用“Powered by TinyMCE”
  menubar: props.menubar, //顶部菜单栏显示
  image_dimensions: false, //去除宽高属性
  height: props.height,//编辑器高度
  plugins: pluginsList,  //插件配置
  toolbar: props.toolbar, //工具栏配置，设为false则隐藏
  end_container_on_empty_block: true,//Enter键时如何拆分当前容器块元素
  font_family_formats: 'Arial=arial,helvetica,sans-serif; 宋体=SimSun; 微软雅黑=Microsoft Yahei; Impact=impact,chicago;', //字体
  font_size_formats: '11px 12px 14px 16px 18px 24px 36px 48px 64px 72px', //文字大小
  paste_data_images: true,// 粘贴的同时能把内容里的图片自动上传，非常强力的功能
  object_resizing: true, //图片拖拽功能
  /********************word start***************/
  paste_webkit_styles: 'all', //此选项允许您指定在WebKit中粘贴时要保留的样式
  paste_merge_formats: true, //此选项启用粘贴插件的合并格式功能
  paste_auto_cleanup_on_paste: false, //是否去除粘贴文本中的样式
  /********************word end***************/
  link_default_target: '_blank',//新窗口打开链接
  link_title: false,//在链接的title属性中自动添加链接文本，除非您在编辑器中手动添加
  nonbreaking_force_tab: true,//非空字符强制tab
  promotion: false,//是否禁用“升级”弹窗
  file_picker_types: 'file',//文件类型
  autoresize_bottom_margin: 10,//编辑器底部距离
  autoresize_overflow_padding: 10,//指定编辑器body初始化时两侧的距离
  min_height: props.height,//编辑器最小高度
  max_height: 500,//编辑器最大高度
  content_style: 'img {max-width:100%;}',//以css样式方式自定义可编辑区域
  content_css: '/tinymce/skins/content/default/content.css', //以css文件方式自定义可编辑区域的css样式
  //图片上传
  images_file_types: 'jpg,jpeg,svg,webp,gif,png',
  images_upload_handler: (blobInfo:any, progress:any) => new Promise((resolve, reject) => {
    const imgType='jpg,jpeg,svg,webp,gif,png'
    const imgFileType=['image/gif','image/jpeg','image/jpg','image/pjpeg','image/x-png','image/png','image/svg+xml','image/webp']
    const isJPG = imgFileType.includes(blobInfo.blob().type);
    const isLt2M = blobInfo.blob().size / 1024 / 1024 < 3;
    if (!isJPG) {
      reject({ message: '上传失败，图片只能是 '+imgType+' 格式', remove: true })
      return;
    }
    if (!isLt2M) {
      reject({ message: '上传失败，图片大小请控制在3M以内', remove: true })
      return;
    }
    if (isJPG && isLt2M) {
      let params = new FormData()
      params.append('file', blobInfo.blob())
      let config = {
        headers: {
          "Content-Type": "multipart/form-data",
          Authorization: yqvutil.getToken(),
        }
      }
      axios.post(uploadUrl.value, params, config).then(res => {
        if (res.data && res.data.length > 0) {
          //上传成功，在成功函数里填入图片路径
          resolve(res.data);
        } else {
          //上传失败，在失败函数里填入错误信息
          reject('HTTP Error: 上传失败');
          return
        }
      }).catch(() => {
        reject('上传出错，服务器异常')
        return
      })
    }
  }),
})
//监听外部传递进来的的数据变化
watch(
    () => props.value,
    () => {
      htmlContent.value = props.value
      emits("getContent", htmlContent.value)
    }
)
//监听富文本中的数据变化
watch(
    () => htmlContent.value,
    () => {
      emits("getContent", htmlContent.value)
    }
)
//在onMounted中初始化编辑器
onMounted(() => {
  tinymce.init({})
})
</script>
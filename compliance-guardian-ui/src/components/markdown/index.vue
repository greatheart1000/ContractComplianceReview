<template>
  <MdEditor
      :toolbars="toolbars"
      :editorId="markdownId"
      v-model="htmlContent"
      @onUploadImg="onUploadImg"
      :style="'height: '+divHeight+'px'"
  ></MdEditor>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { MdEditor } from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';
import yqvutil from "@/common/yqvalley";
import axios from "@/axios";

const emits = defineEmits(["getContent"])
//支持外部参数
const props = defineProps({
  id: {
    type: String,
    default: function () {
      return 'yqv-markdown-' + +new Date() + ((Math.random() * 1000).toFixed(0) + '')
    }
  },
  value: {
    type: String,
    default: () => {
      return ""
    },
  },
  height: {
    type: Number,
    default: 300
  },
  uploadUrl: {
    type: String,
    default: () => {
      return yqvutil.getContextPath() + 'yqvservice/file/image/upload'
    },
  },
  //工具栏
  toolbar: {
    type: [],
    default: [
      'bold',
      'underline',
      'italic',
      '-',
      'title',
      'strikeThrough',
      'sub',
      'sup',
      'quote',
      'unorderedList',
      'orderedList',
      'task',
      '-',
      'codeRow',
      'code',
      'link',
      'image',
      'table',
      'mermaid',
      'katex',
      '-',
      'revoke',
      'next',
      '=',
      'pageFullscreen',
      'fullscreen',
      'preview',
      'htmlPreview',
      'catalog'
    ],
  },
})
//用于接收外部传递进来的富文本
const htmlContent = ref(props.value)
const markdownId = ref(props.id)
const uploadUrl = ref(props.uploadUrl)
const toolbars = ref(props.toolbar)
const divHeight = ref(props.height)

//上传图片
const onUploadImg = async (files, callback) => {
  const res = await Promise.all(
      files.map((file) => {
        return new Promise((resolve, reject) => {
          const imgType = 'jpg,jpeg,svg,webp,gif,png'
          const imgFileType = ['image/gif', 'image/jpeg', 'image/jpg', 'image/pjpeg', 'image/x-png', 'image/png', 'image/svg+xml', 'image/webp']
          const isJPG = imgFileType.includes(file.type);
          const isLt2M = file.size / 1024 / 1024 < 3;
          if (!isJPG) {
            reject({ message: '上传失败，图片只能是 ' + imgType + ' 格式', remove: true })
            return;
          }
          if (!isLt2M) {
            reject({ message: '上传失败，图片大小请控制在3M以内', remove: true })
            return;
          }
          if (isJPG && isLt2M) {
            let params = new FormData()
            params.append('file', file)
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
        });
      })
  );
  callback(res.map((item) => item));
};

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

})
</script>
<template>
  <MdPreview
      hljs={{hljs}}
      :noPrettier="true"
      :editorId="markdownId"
      v-model="htmlContent"
  ></MdPreview>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { MdPreview } from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';
import hljs from 'highlight.js';

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
})
//用于接收外部传递进来的富文本
const htmlContent = ref(props.value);
const markdownId = ref(props.id);


//监听外部传递进来的的数据变化
watch(
    () => props.value,
    () => {
      htmlContent.value = props.value
    }
)
//在onMounted中初始化编辑器
onMounted(() => {

})

</script>
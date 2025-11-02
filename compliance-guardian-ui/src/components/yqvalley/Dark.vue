<template>
    <el-tooltip content="黑暗模式" placement="bottom" effect="light" >
        <el-switch
            inline-prompt
            v-model="theme"
            class="mt-2"
            width="40"
            @click="toggleDark()"
            :active-icon="Sunny"
            :inactive-icon="Moon"
            style="--el-switch-on-color: #606266; --el-switch-off-color: #606266"
            size="default"
        >
        </el-switch>
    </el-tooltip>
</template>
 
<script lang="ts">
import { ref,defineComponent} from "vue";
import { useToggle } from '@vueuse/shared'
import { useDark } from "@vueuse/core";
import { Sunny, Moon } from '@element-plus/icons-vue'
 
export default defineComponent({
  setup() {
    const theme = ref<boolean>(false)
    const isDark = useDark({
      // 存储到localStorage/sessionStorage中的Key 根据自己的需求更改
      storageKey: 'useDarkKEY',
      // 暗黑class名字
      valueDark: 'dark',
      // 高亮class名字
      valueLight: 'light',
    })
    const toggleDark = useToggle(isDark);
 
    return {
      theme,
      toggleDark,
      Sunny,
      Moon
    }
  }
})
</script>
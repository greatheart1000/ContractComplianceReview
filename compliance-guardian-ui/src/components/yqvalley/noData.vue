<template>
  <div style="text-align: center; color: #909399; font-size: 12px">
    <div
      :style="{
        'padding-top': smallHeight ? '10px' : '100px',
      }"
    >
      暂无数据
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {};
  },
  props: ["smallHeight"],
  methods: {},
  mounted() {},
};
</script>

<style scoped lang="less"></style>

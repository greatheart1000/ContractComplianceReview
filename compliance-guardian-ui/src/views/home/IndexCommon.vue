<template>
  <div class="icons-container">
    <el-tabs type="border-card">
      <el-tab-pane label="自定义">
        <div class="grid">
          <div v-for="item of svgIcons" :key="item" @click="$yqvutil.copyText(getYqvIcon(item),'复制成功')">
            <el-tooltip :content="getYqvIcon(item)" placement="top">
              <div class="icon-item">
                <svg-icon :icon-class="item" class-name="disabled" />
                <span>{{ item }}</span>
              </div>
            </el-tooltip>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="FontAwesome">
        <div class="fa-filters">
          <div> 共有{{faShowIcons.length}}个图标 </div>
          <div class="styles">
            <button :class="faShow=='fa'?'select':''" @click="getFontAwesomeData('fa')"> 全部(fa) </button>
            <button :class="faShow=='far'?'select':''" @click="getFontAwesomeData('far')"> 常规(far) </button>
            <button :class="faShow=='fas'?'select':''" @click="getFontAwesomeData('fas')"> 实体(fas) </button>
          </div>
        </div>
        <div class="grid">
          <div v-for="item of faShowIcons" :key="item" @click="$yqvutil.copyText(getFontAwesomeIcon(item),'复制成功')">
            <el-tooltip :content="getFontAwesomeIcon(item)" placement="top">
              <div class="icon-item">
                <font-awesome-icon :icon="item" />
                <span>{{ item }}</span>
              </div>
            </el-tooltip>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import {svgIcons,faIcons,farIcons,fasIcons} from '@/assets/icons/yqvicons'

export default {
  name: 'Icons',
  data() {
    return {
      svgIcons,
      faShowIcons:faIcons,
      faIcons,
      farIcons,
      fasIcons,
      faShow:"fa"
    }
  },
  created() {
    
  },
  methods: {
    getFontAwesomeData(type){
      if("fas"==type){
        this.faShowIcons = this.fasIcons;
        this.faShow = "fas";
      }else if("far"==type){
        this.faShowIcons = this.farIcons;
        this.faShow = "far";
      }else{
        this.faShowIcons = this.faIcons;
        this.faShow = "fa";
      }
    },
    getYqvIcon(symbol) {
      return `<svg-icon icon-class="${symbol}" />`
    },
    getFontAwesomeIcon(symbol) {
      return `<font-awesome-icon icon="${symbol}" />`
    },
  }
}
</script>

<style scoped lang="less">
  .icons-container {
    margin: 10px 20px 0;
    overflow: hidden;
    .grid {
      position: relative;
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    }
    .icon-item {
      margin: 20px;
      height: 85px;
      text-align: center;
      width: 100px;
      float: left;
      font-size: 30px;
      color: #24292e;
      cursor: pointer;
    }
    span {
      display: block;
      font-size: 16px;
      margin-top: 10px;
    }
    .disabled {
      pointer-events: none;
    }
    .fa-filters{
      display: flex;
      --un-bg-opacity: 1;
      align-items: center;
      background-color: #f5f7fa;
      border-radius: 10px;
      justify-content: space-between;
      margin-bottom: 20px;
      margin-top: 20px;
      padding-left: 20px;
      padding-right: 20px;
      .styles{
        display: flex;
        button{
          padding: 10px;
          background-color: transparent;
          background-image: none;
          cursor: pointer;
        }
        button:hover {
          color: #fb923c;
        }
        .select{
          background-color: #5675f5;
          color: #ffffff;
        }
      }
    }
  }
</style>

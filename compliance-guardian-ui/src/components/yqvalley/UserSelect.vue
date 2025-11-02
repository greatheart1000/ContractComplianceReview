<!-- 用户组件 -->
<template>
  <div class="userSelect">
    <div class="inputUserSelect">
      <el-input
        v-model="userSelectName"
        placeholder="请输入后回车搜索"
        @keyup.enter="inputEnter"
        @blur="blurInput"
        @clear="clearInput"
        :disabled="disabled"
        clearable
      ></el-input>
      <font-awesome-icon icon="fas fa-user" class="userSelectIcon"/>
      <i class="borderIcon"></i>
      <ul class="userSelectBody" v-if="uiListShow">
        <template v-if="uiListArr && uiListArr.length > 0">
          <li
            class="ellipsis"
            v-for="(item, index) in uiListArr"
            :key="index"
            @click="userSelectClick"
          >
            {{ item }}
          </li>
        </template>
        <li v-else class="noData">暂无数据</li>
      </ul>
    </div>
  </div>
</template>
<script>
export default {
  name: "UserSelect",
  props: ["val", "showData", "disabled"],
  data() {
    return {
      userSelectAccount: "",
      userSelectName: "",
      uiListArr: [],
      uiListShow: false,
      url: "",
      userRole: "",
    };
  },
  watch: {
    showData(val) {
      this.userSelectAccount = val;
      if (!this.showData) {
        this.userSelectName = "";
        if (typeof this.$parent[this.val] != "undefined") {
          this.$parent[this.val] = "";
        } else {
          this.$parent.$parent[this.val] = "";
        }
      } else {
        this.setUserName();
      }
    },
    userSelectName() {
      if (!this.userSelectName) {
        if (typeof this.$parent[this.val] != "undefined") {
          this.$parent[this.val] = "";
        } else {
          this.$parent.$parent[this.val] = "";
        }
      }
    },
  },
  methods: {
    //输入事件
    inputEnter() {
      if (!this.userSelectName) {
        this.uiListShow = true;
        this.uiListArr = [];
        return;
      }
      this.queryUserSelect();
    },
    //input失去焦点事件
    blurInput() {
      setTimeout(() => {
        this.uiListShow = false;
        this.uiListArr = [];
      }, 500);
    },
    //input清除事件
    clearInput() {
      this.uiListShow = false;
      this.uiListArr = [];
    },
    //人员控件数据查询
    queryUserSelect() {
      let vm = this;
      vm.uiListArr = [];
      vm.$http
        .post(vm.$yqvutil.getContextPath()+`yqvservice/user/search`, {
          matchSearch: vm.userSelectName,
        })
        .then(function (response) {
          if (response.status == 200) {
            let data = response.data;
            if (data && data.length == 1) {
              vm.userSelectAccount = data[0].valleyAccount;
              vm.userSelectName =
                data[0].userName + "(" + data[0].valleyAccount + ")";
              vm.uiListShow = false;
              if (typeof vm.$parent[vm.val] != "undefined") {
                vm.$parent[vm.val] = vm.userSelectAccount;
              } else {
                vm.$parent.$parent[vm.val] = vm.userSelectAccount;
              }
              sessionStorage.setItem(
                "yqv_user_select_" + vm.userSelectAccount,
                vm.userSelectName
              );
              return;
            }
            for (let i = 0; i < data.length; i++) {
              vm.uiListArr.push(
                data[i].userName + "(" + data[i].valleyAccount + ")"
              );
            }
            vm.uiListShow = true;
          }
        })
        .catch(function (error) {});
    },
    //人员控件数据选择
    userSelectClick(data) {
      let regex = /\((.+?)\)/g;
      let options = data.target.innerText.match(regex);
      let option = options[0];
      if (option && option.length > 2) {
        this.userSelectAccount = option.substring(1, option.length - 1);
      }
      this.userSelectName = data.target.innerText;
      this.uiListShow = false;
      sessionStorage.setItem(
        "yqv_user_select_" + this.userSelectAccount,
        this.userSelectName
      );
      if (typeof this.$parent[this.val] != "undefined") {
        this.$parent[this.val] = this.userSelectAccount;
      } else {
        this.$parent.$parent[this.val] = this.userSelectAccount;
      }
    },
    setUserName() {
      let name = sessionStorage.getItem("yqv_user_select_" + this.showData);
      if (typeof name != "undefined" && name != "undefined") {
        this.userSelectName = name;
      }
    },
  },
  updated() {},
  components: {},
  computed: {},
  mounted() {
    this.setUserName();
  },
};
</script>

<style scoped lang="less">
.inputUserSelect ::v-deep(input) {
  padding-left: 35px !important;
}
.inputUserSelect ::v-deep(.el-form-item__label) {
  width: 100px !important;
}
.userSelect {
  .inputUserSelect {
    height: 28px;
    display: flex;
    position: relative;
    .el-input {
      flex: 1;
    }
    .userSelectIcon {
      color: #096dd9;
      position: absolute;
      top: 8px;
      left: 10px;
      padding-right: 8px;
      font-size: 18px;
    }
    .borderIcon {
      position: absolute;
      border-right: 1px solid #dcdfe6;
      height: 30px;
      top: 1px;
      left: 30px;
    }
    .userSelectBody {
      position: absolute;
      top: 38px;
      box-sizing: border-box;
      border-radius: 5px;
      padding: 0 10px;
      width: 100%;
      z-index: 10000;
      right: 0;
      width: 100%;
      border: 1px solid #dcdfe6;
      background: #fff;
      max-height: 200px;
      overflow-y: auto;
      li {
        cursor: pointer;
        height: 30px;
        line-height: 30px;
      }
      .noData {
        text-align: center;
        color: #999;
        font-size: 14px;
      }
    }
  }
}
</style>

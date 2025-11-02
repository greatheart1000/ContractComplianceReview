<template>
  <div class="sign-box">
    <div class="sign-title">
      <font-awesome-icon icon="fas fa-exclamation-circle" /> AccessKey ID和AccessKey
      Secret是您访问平台对外开放API的密钥,请您妥善保管。
      <div class="sign-add" @click="addAccessKey" v-if="signList.length < 2">
        创建AccessKey
      </div>
      <div class="sign-add-not" v-else>创建AccessKey</div>
    </div>

    <div class="sign-table">
      <el-table
        :data="signList"
        border
        style="width: 100%"
        tooltip-effect="light"
      >
        <el-table-column
          type="index"
          width="50"
          label="NO."
          align="center"
          fixed="left"
        >
        </el-table-column>

        <el-table-column fixed="right" label="操作" align="left" width="130">
          <template #default="scope">
            <span
              @click="deleteSign(scope.row)"
              class="deleteCol"
              style="width: auto; color: rgb(24, 144, 255)"
              title="删除"
            >
              删除
            </span>
            <span
              @click="prohibit(scope.row)"
              class="deleteCol"
              style="margin-left: 10px; width: auto; color: rgb(24, 144, 255)"
              title="禁用"
              v-text="scope.row.accessKeyStatus == 1 ? '禁用' : '启用'"
            >
            </span>
          </template>
        </el-table-column>

        <el-table-column
          label="AccessKey ID"
          align="left"
          :show-overflow-tooltip="true"
        >
          <template #default="scope">
            <span v-text="scope.row.accessKeyId"></span>
          </template>
        </el-table-column>

        <el-table-column
          label="AccessKey Secret"
          align="left"
          :show-overflow-tooltip="true"
        >
          <template #default="scope">
            <span
              v-text="scope.row.accessKeySecret"
              :title="scope.row.accessKeySecret"
            ></span>
          </template>
        </el-table-column>
        <el-table-column
          label="状态"
          align="center"
          :show-overflow-tooltip="true"
          width="60"
        >
          <template #default="scope">
            <span
              v-text="scope.row.accessKeyStatus == 1 ? '启用' : '禁用'"
              :style="{
                color:
                  scope.row.accessKeyStatus == 1
                    ? '#06ec3e'
                    : 'rgb(245, 108, 108)',
              }"
            ></span>
          </template>
        </el-table-column>
        <el-table-column
          label="创建时间"
          align="left"
          :show-overflow-tooltip="true"
          width="140"
        >
          <template #default="scope">
            <span v-text="$yqvutil.setFormTime(scope.row.createDate, 1)"></span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
export default {
  name: "sign",
  data() {
    return {
      signList: [],
    };
  },
  mounted() {
    this.getSignList();
  },
  computed: {},
  methods: {
    //删除
    deleteSign(val) {
      let data = {
        accessKeyId: val.accessKeyId,
      };
      this.$http
        .post(this.$yqvutil.getContextPath()+`yqvservice/certificate/delete`, data)
        .then((response) => {
          if (response.status == 200) {
            this.getSignList();
          }
        })
        .catch((error) => {});
    },
    //启用/禁用
    prohibit(val) {
      let data = {
        accessKeyId: val.accessKeyId,
        accessKeyStatus: val.accessKeyStatus == 1 ? "0" : "1",
      };
      this.$http
        .post(this.$yqvutil.getContextPath()+`yqvservice/certificate/update`, data)
        .then((response) => {
          if (response.status == 200) {
            this.getSignList();
          }
        })
        .catch((error) => {});
    },
    //获取数据
    getSignList() {
      let data = {};
      this.$http
        .post(this.$yqvutil.getContextPath()+`yqvservice/certificate/list`, data)
        .then((response) => {
          if (response.status == 200) {
            this.signList = response.data;
          }
        })
        .catch((error) => {});
    },
    //添加
    addAccessKey() {
      let data = {};
      this.$http
        .post(this.$yqvutil.getContextPath()+`yqvservice/certificate/add`, data)
        .then((response) => {
          if (response.status == 200) {
            this.getSignList();
          }
        })
        .catch((error) => {});
    },
  },
};
</script>

<style  lang="less">
.sign-box {
  .sign-title {
    padding: 0 14px;
    height: 32px;
    line-height: 32px;
    color: #fcae5c;
  }
  .sign-add,
  .sign-add-not {
    float: right;
    height: 32px;
    line-height: 32px;
    padding: 0 10px;
    color: #fff;
    border-radius: 2px;
  }
  .sign-add {
    background: #597ef7;
    cursor: pointer;
  }
  .sign-add-not {
    background: #d9d9d9;
    cursor: not-allowed;
  }
}
</style>

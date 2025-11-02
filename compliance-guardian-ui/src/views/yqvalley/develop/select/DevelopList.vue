<template>
  <div class="SelectInfo">
    <div class="tabBox">
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane label="选择列表" name="first"> </el-tab-pane>
        <el-tab-pane
          v-if="judgeLabel"
          label="选择详情"
          name="second"
        ></el-tab-pane>
      </el-tabs>
      <div class="tabBottom"></div>
    </div>
    <!--选择列表-->
    <div v-if="activeName == 'first'">
      <!-- 查询条件 -->
      <div class="search-box-one" style="width:100%;overflow:visible;margin-top: -20px;">
        <div style="width:33%;float:left;margin-top:20px;">
          <div style="width:100%;float:left;">
            <div class="search-box-top-name" style="width: 75px;">选择编号：</div>
            <div style="width:calc(100% - 100px - 2.3%);float:right;padding-right:2.3%">
              <el-input v-model="queryData.selectCode" placeholder="请输入选择编号" clearable style="width:100%;"></el-input>
            </div>
          </div>
        </div>
        <div style="width:33%;float:left;margin-top:20px;">
          <div style="width:100%;float:left;">
            <div class="search-box-top-name" style="width: 75px;">选择名称：</div>
            <div style="width:calc(100% - 100px - 2.3%);float:right;padding-right:2.3%">
              <el-input v-model="queryData.selectName" placeholder="请输入选择名称" clearable style="width:100%;"></el-input>
            </div>
          </div>
        </div>
        <div style="width:33%;float:left;margin-top:20px;">
          <div class="search-box-right" style="overflow: visible;width: 100%;">
            <div class="query" @click="empty" style="margin-right: 20px;">
              <font-awesome-icon icon="fas fa-refresh" />重置
            </div>
            <div class="query" @click="search" style="margin-right: 10px;">
              <font-awesome-icon icon="fas fa-search" />查询
            </div>
          </div>
        </div>
      </div>
      <div class="clear"></div>
      <!-- 操作按钮 -->
      <div class="btn-box" style="margin-top: 10px;">
        <div class="new" style="width:60px;" @click="newSelect">
          <font-awesome-icon icon="fas fa-plus-circle" />新建
        </div>
        <div class="new" style="width:90px;background: #f5a623;" @click="updateCache">
          <font-awesome-icon icon="fas fa-flask" />更新缓存
        </div>
      </div>
      <div>
        <el-table :data="selectList" border style="width: 100%" tooltip-effect="light" :height="tabListHeight-50">
          <el-table-column type="index" width="50" label="NO." align="center" fixed="left" >
          </el-table-column>
          <el-table-column fixed="left" label="操作" width="75" align="left">
            <template #default="scope">
              <div @click="editSelect(scope.row)" type="text" size="small" title="配置">
                <font-awesome-icon icon="fas fa-cog" style="float: left;color:#33db67;font-size:18px;width: 26px;cursor: pointer;" />
              </div>
              <div type="text" size="small" title="删除" @click="deleteSelect(scope.row)">
                <font-awesome-icon icon="far fa-trash-alt" style="float: left;color:#f56c6c;font-size:18px;width: 26px;cursor: pointer;" />
              </div>
            </template>
          </el-table-column>
          <el-table-column label="选择编号" align="left" width="250" :show-overflow-tooltip="true">
            <template #default="scope">
              <span v-text="scope.row.selectCode"></span>
            </template>
          </el-table-column>
          <el-table-column label="选择名称" align="left" width="150" :show-overflow-tooltip="true">
            <template #default="scope">
              <span v-text="scope.row.selectName"></span>
            </template>
          </el-table-column>
          <el-table-column label="修改控制" align="left" width="80" :show-overflow-tooltip="true">
            <template #default="scope">
              <span v-text="$yqvutil.getSelectItemName('VALLEY_UPDATE_CONTROL',scope.row.updateControl)"></span>
            </template>
          </el-table-column>
          <el-table-column label="内容公开" align="left" width="80" :show-overflow-tooltip="true">
            <template #default="scope">
              <span v-text="$yqvutil.getSelectItemName('VALLEY_CONTENT_OPEN',scope.row.contentOpen)"></span>
            </template>
          </el-table-column>
          <el-table-column label="选择描述" align="left" width="350" :show-overflow-tooltip="true">
            <template #default="scope">
              <span v-text="scope.row.selectDesc"></span>
            </template>
          </el-table-column>
          <el-table-column label="修改用户" align="left" width="110" :show-overflow-tooltip="true">
            <template #default="scope">
              <div v-text="scope.row.lastUpdateName"></div>
            </template>
          </el-table-column>
          <el-table-column label="修改时间" align="left" width="140">
            <template #default="scope">
              <div v-text="$yqvutil.setFormTime(scope.row.lastUpdateDate, 1)"></div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="paging-box">
        <div class="block">
          <el-pagination
            background
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="currentPage"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="totals"
          >
          </el-pagination>
        </div>
      </div>
    </div>
    <!--选择明细-->
    <div v-if="activeName == 'second'">
      <DevelopEditInfo 
      :selectCode="selectCode"
      :selectName="selectName"
      :selectDesc="selectDesc"
      :contentOpen="contentOpen"
      :updateControl="updateControl"
      />
    </div>
  </div>
</template>
<script>
import DevelopEditInfo from "./DevelopEditInfo";
import { mapState, mapMutations } from "vuex";
export default {
  name: "DevelopSelectInfo",
  data() {
    return {
      //当前页
      currentPage: 1,
      pageSize: 10,
      totals: 0,
      //导航控制
      activeName: "first",
      //判断哪个标签
      judgeLabel: false,
      //选择列表数据
      selectList: [],
      //查询数据
      queryData: {
        selectCode:"",
        selectName:""
      },
      //传给子级调用方法
      selectCode: "",
      selectName: "",
      selectDesc: "",
      contentOpen: "",
      updateControl: ""
    };
  },
  mounted() {
    this.getSelectList();
  },
  watch: {},
  components: {
    DevelopEditInfo
  },
  computed: {
    ...mapState(["tabListHeight"])
  },
  methods: {
    /********************************重置*****************************/
    empty() {
      this.queryData.selectCode = "";
      this.queryData.selectName = "";
    },
    //标签操作
    /********************************标签操作*****************************/
    handleClick(row) {
      let labelName = null;
      if(row.label){
        labelName = row.label;
      }
      if(!labelName && row.props){
        labelName = row.props.label;
      }
      if (labelName == "选择列表") {
        this.activeName = "first";
        this.judgeLabel = false;
      }
    },
    /********************************删除选择信息*****************************/
    deleteSelect(val) {
      this.$confirm("此操作将删除选择配置信息, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        closeOnClickModal: false
      })
        .then(() => {
          let vm = this;
          let data = {"selectCode":val.selectCode};
          this.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/selectconfig/delete`, data)
            .then(function(response) {
              if (response.status == 200) {
                vm.$message({
                  showClose: true,
                  message: "删除成功",
                  type: "success"
                });
                vm.currentPage = 1;
                vm.pageSize = 10;
                vm.getSelectList();
              }
            })
            .catch(function(error) {});
        })
        .catch(() => {});
    },
    /********************************更新缓存*****************************/
    updateCache() {
      let strName = "此操作会更新公共缓存, 是否继续?";
      this.$confirm(strName, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        closeOnClickModal: false
      })
        .then(() => {
          let vm = this;
          this.$http.get(vm.$yqvutil.getContextPath()+`yqvservice/selectconfig/update/cache`)
            .then(function(response) {
              if (response.status == 200) {
                vm.$message({
                  showClose: true,
                  message: "更新成功",
                  type: "success"
                });
              }
            })
            .catch(function(error) {});
        })
        .catch(() => {});
    },
    /********************************查询*****************************/
    search() {
      this.getSelectList();
    },
    /********************************新增选择信息*****************************/
    newSelect() {
      this.judgeLabel = true;
      //光标对位
      this.activeName = "second";
      this.selectCode = null;
      this.selectName = null;
      this.selectDesc = null;
      this.contentOpen = null;
      this.updateControl = "Y";
    },
    /********************************编辑选择信息*****************************/
    editSelect(val) {
      this.judgeLabel = true;
      //光标对位
      this.activeName = "second";
      this.selectCode = val.selectCode;
      this.selectName = val.selectName;
      this.selectDesc = val.selectDesc;
      this.contentOpen = val.contentOpen;
      this.updateControl = val.updateControl;
    },
    /********************************分页*****************************/
    handleSizeChange(val) {
      this.pageSize = val;
      this.getSelectList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getSelectList();
    },
    /********************************获取选择数据*****************************/
    getSelectList() {
      let vm = this;
      let data = {
        selectCode: this.queryData.selectCode,
        selectName: this.queryData.selectName
      };
      this.$http.post(
          vm.$yqvutil.getContextPath()+`yqvservice/selectconfig/page?currentPage=${
            this.currentPage
          }&pageSize=${this.pageSize}`,
          data
        )
        .then(function(response) {
          if (response.status == 200) {
            vm.totals = response.data.page.totalRows;
            vm.selectList = response.data.result;
            vm.judgeQuery = false;
          }
        })
        .catch(function(error) {});
    }
  }
};
</script>

<style scoped lang="less">

</style>

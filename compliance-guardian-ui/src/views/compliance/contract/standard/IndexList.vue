<template>
  <div class="ContractRuleList">
    <div class="tabBox">
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane label="规则列表" name="first"> </el-tab-pane>
        <el-tab-pane v-if="judgeLabel" label="规则信息" name="second"></el-tab-pane>
      </el-tabs>
      <div class="tabBottom"></div>
    </div>
    <div v-if="activeName == 'first'">
      <!-- 查询条件 -->
      <div class="search-box-one" style="width:100%;overflow:visible;margin-top: -20px;">
        <div style="width:33%;float:left;margin-top:20px;">
          <div style="width:100%;float:left;">
            <div class="search-box-top-name" style="width: 90px;">合同类型：</div>
            <div style="width:calc(100% - 115px - 2.3%);float:right;padding-right:2.3%">
              <el-select
                v-model="queryData.contractType"
                placeholder="请选择合同类型"
                style="width: 100%"
                clearable
                collapse-tags
              >
                <el-option
                  v-for="item in $yqvutil.getSelectList('CG_CONTRACT_TYPE')"
                  :key="item.itemCode"
                  :label="item.itemName"
                  :value="item.itemValue"
                >
                </el-option>
              </el-select>
            </div>
          </div>
        </div>
        <div style="width:33%;float:left;margin-top:20px;">
          <div style="width:100%;float:left;">
            <div class="search-box-top-name" style="width: 90px;">规则名称：</div>
            <div style="width:calc(100% - 115px - 2.3%);float:right;padding-right:2.3%">
              <el-input v-model="queryData.ruleName" placeholder="请输入模型名称" clearable style="width:100%;"></el-input>
            </div>
          </div>
        </div>
        <div style="width:33%;float:left;margin-top:20px;">
          <div class="search-box-right" style="overflow: visible;width: 100%;">
            <div class="query" @click="editContractRule" style="margin-right: 20px;background: #67c23a;" v-if="$yqvutil.getVerifyAuthority('compliance.contract_standard.update')">
              <font-awesome-icon icon="fas fa-plus" />添加
            </div>
            <div class="query" @click="empty" style="margin-right: 10px;">
              <font-awesome-icon icon="fas fa-refresh" />重置
            </div>
            <div class="query" @click="search" style="margin-right: 10px;">
              <font-awesome-icon icon="fas fa-search" />查询
            </div>
            <div class="more-down" @click="moreCondition = !moreCondition" v-if="!moreCondition">
              <span v-text="'展开'"></span>
              <font-awesome-icon icon="fas fa-angle-double-down" />
            </div>
            <div class="more-down" @click="moreCondition = !moreCondition" v-else>
              <span v-text="'收起'"></span>
              <font-awesome-icon icon="fas fa-angle-double-up" />
            </div>
          </div>
        </div>
      </div>
      <div v-if="moreCondition" class="search-box-two" style="width: 100%; height: 52px; float: left; margin-top: -10px;">
        <div style="width:33%;float:left;margin-top:20px;">
          <div style="width:100%;float:left;">
            <div class="search-box-top-name" style="width: 90px;">规则类型：</div>
            <div style="width:calc(100% - 115px - 2.3%);float:right;padding-right:2.3%">
              <el-select
                v-model="queryData.ruleType"
                placeholder="请选择规则类型"
                style="width: 100%"
                clearable
                collapse-tags
              >
                <el-option
                  v-for="item in $yqvutil.getSelectList('CG_CONTRACT_RULE_TYPE')"
                  :key="item.itemCode"
                  :label="item.itemName"
                  :value="item.itemValue"
                >
                </el-option>
              </el-select>
            </div>
          </div>
        </div>
        <div style="width:33%;float:left;margin-top:20px;">
          <div style="width:100%;float:left;">
            <div class="search-box-top-name" style="width: 90px;">规则状态：</div>
            <div style="width:calc(100% - 115px - 2.3%);float:right;padding-right:2.3%">
              <el-select
                v-model="queryData.ruleStatus"
                placeholder="请选择规则状态"
                style="width: 100%"
                clearable
                collapse-tags
              >
                <el-option
                  v-for="item in $yqvutil.getSelectList('MODEL_CONFIG_STATUS')"
                  :key="item.itemCode"
                  :label="item.itemName"
                  :value="item.itemValue"
                >
                </el-option>
              </el-select>
            </div>
          </div>
        </div>
      </div>
      <div class="clear"></div>
      <div style="margin-top: 10px;"> </div>
      <!-- 操作按钮 -->
      <div class="btn-box" v-if="$yqvutil.getVerifyAuthority('compliance.contract_standard.update')">
        <div class="start" style="width: 60px" @click="updateContractRuleStatus('Y')">
          <font-awesome-icon icon="far fa-play-circle" />启用
        </div>
        <div class="prohibit" style="width: 60px; background: #f56c6c" @click="updateContractRuleStatus('N')">
          <font-awesome-icon icon="fas fa-ban" />禁用
        </div>
      </div>
      <!--表格-->
      <div class="tableBox">
        <el-table :data="tableData" 
          @selection-change="checkAll" 
          border  
          ref="table" 
          style="width: 100%" 
          tooltip-effect="light" 
          :height="$yqvutil.getVerifyAuthority('compliance.contract_standard.update')?(moreCondition?tabListHeight-93:tabListHeight-51):(moreCondition?tabListHeight-55:tabListHeight-13)"
        >
          <el-table-column type="selection" width="50" label="NO." align="center" fixed="left">
          </el-table-column>
          <el-table-column fixed="left" label="操作" width="70" align="center">
            <template #default="scope">
              <div type="text" size="small" @click="editContractRule(scope.row)" v-if="!$yqvutil.getVerifyAuthority('compliance.contract_standard.update')">
                <font-awesome-icon icon="fas fa-search"
                  style="float: left;color: #5675f5;font-size: 18px;width: 26px;cursor: pointer;"/>
              </div>
              <div type="text" size="small" @click="editContractRule(scope.row)" v-if="$yqvutil.getVerifyAuthority('compliance.contract_standard.update')">
                <font-awesome-icon icon="far fa-edit"
                  style="float: left;color: #33db67;font-size: 18px;width: 26px;cursor: pointer;"/>
              </div>
              <div type="text" size="small" @click="deleteContractRule(scope.row)" v-if="$yqvutil.getVerifyAuthority('compliance.contract_standard.update')">
                <font-awesome-icon icon="far fa-trash-alt"
                  style="float: left; color: #f56c6c;font-size: 18px;width: 26px;cursor: pointer;"/>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="规则ID" align="left" width="120" :show-overflow-tooltip="true">
            <template #default="scope">
              <div v-text="scope.row.ruleId"></div>
            </template>
          </el-table-column>
          <el-table-column label="规则类型" align="center" width="80" :show-overflow-tooltip="true">
            <template #default="scope">
              <span v-text="$yqvutil.getSelectItemName('CG_CONTRACT_RULE_TYPE',scope.row.ruleType)"></span>
            </template>
          </el-table-column>
          <el-table-column label="合同类型" align="center" width="160" :show-overflow-tooltip="true">
            <template #default="scope">
              <div v-text="scope.row.contractType"></div>
            </template>
          </el-table-column>
          <el-table-column label="规则名称" align="left" :show-overflow-tooltip="true">
            <template #default="scope">
              <div v-text="scope.row.ruleName"></div>
            </template>
          </el-table-column>
          <el-table-column label="规则状态" align="center" width="80">
            <template #default="scope">
              <span v-text="$yqvutil.getSelectItemName('MODEL_CONFIG_STATUS',scope.row.ruleStatus)" :class="[scope.row.ruleStatus == 'Y'? 'activeColorY': 'activeColorN',]"></span>
            </template>
          </el-table-column>
          <el-table-column label="修改用户" align="left" width="110" :show-overflow-tooltip="true">
            <template #default="scope">
              <div v-text="scope.row.lastUpdateName"></div>
            </template>
          </el-table-column>
          <el-table-column label="修改时间" width="140" :show-overflow-tooltip="true">
            <template #default="scope">
              <div v-text="$yqvutil.setFormTime(scope.row.lastUpdateDate, 1)"></div>
            </template>
          </el-table-column>
        </el-table>
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
    </div>
    <div v-if="activeName === 'second'" class="authorityBox">
      <EditRuleInfo :ruleId="ruleId"/>
    </div>
  </div>
</template>
<script>
import { mapState } from "vuex";
import EditRuleInfo from "./EditRuleInfo.vue";
export default {
  name: "ContractRuleList",
  data() {
    return {
      moreCondition:false,
      //导航控制
      activeName: "first",
      //判断哪个标签
      judgeLabel: false,
      //当前页
      currentPage: 1,
      pageSize: 10,
      totals: 0,
      //全选单选容器
      container: [],
      //列表数据
      tableData: [],
      ruleId: "",
      //查询条件
      queryData: {
        ruleType: "",
        ruleName: "",
        contractType: "",
        ruleStatus: "",
      },
    };
  },
  computed: {
    ...mapState(["yqvSelect", "tabListHeight"]),
  },
  mounted() {
    this.getContractRuleList();
  },
  components: {
    EditRuleInfo,
  },
  watch: {
    
  },
  methods: {
    /********************************全选*****************************/
    checkAll(val) {
      //进行赋值,判断是否选中内容
      this.container = val;
    },
    /********************************标签操作*****************************/
    handleClick(row) {
      let labelName = null;
      if(row.label){
        labelName = row.label;
      }
      if(!labelName && row.props){
        labelName = row.props.label;
      }
      if (labelName == "规则列表") {
        this.activeName = "first";
        this.judgeLabel = false;
      }
    },
    /********************************查询规则列表*****************************/
    search() {
      this.getContractRuleList(true);
    },
    /********************************清空查询条件*****************************/
    empty() {
      this.queryData.ruleType = "";
      this.queryData.ruleName = "";
      this.queryData.contractType = "";
      this.queryData.ruleStatus = "";
      this.getContractRuleList(true);
    },
    /********************************删除配置*****************************/
    deleteContractRule(row) {
      this.$confirm("此操作将删除规则, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
        closeOnClickModal: false,
      })
        .then(() => {
          let data = {
            ruleId: row.ruleId,
          };
          let vm = this;
          this.$http
            .post(vm.$yqvutil.getContextPath()+`yqvservice/cstandard/delete`, data)
            .then(function (response) {
              vm.$message({
                type: "success",
                message: "删除成功!",
              });
              vm.getContractRuleList(true);
            })
            .catch(function (error) {});
        })
        .catch(() => {});
    },
    /********************************编辑规则*****************************/
    editContractRule(val) {
      let vm = this;
      vm.judgeLabel = true;
      vm.activeName = "second";
      this.ruleId = val.ruleId;
    },
    /********************************查询规则列表*****************************/
    getContractRuleList(flag) {
      let vm = this;
      let data = this.queryData;
      if (flag) {
        vm.currentPage = 1;
        vm.pageSize = 10;
        vm.totals = 0;
      }
      vm.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/cstandard/page?currentPage=${vm.currentPage}&pageSize=${vm.pageSize}`,data)
        .then(function (response) {
          if (response.status == 200) {
            vm.tableData = response.data.result;
            vm.totals = response.data.page.totalRows;
          }
        })
        .catch(function (error) {});
    },
    /********************************修改规则状态*****************************/
    updateContractRuleStatus(ruleStatus) {
      let vm = this;
      if (vm.container.length != 0) {
        let strName;
        if (ruleStatus === "Y") {
          strName = "此操作会启用规则, 是否继续?";
        } else {
          strName = "此操作会禁用规则, 是否继续?";
        }
        vm.$confirm(strName, "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
          closeOnClickModal: false,
        })
          .then(() => {
            let ruleIdList = [];
            for (let i = 0; i < this.container.length; i++) {
              ruleIdList.push(this.container[i].ruleId);
            }
            let data = {
              ruleStatus: ruleStatus,
              ruleIdList: ruleIdList,
            };
            this.$http
              .post(vm.$yqvutil.getContextPath()+`yqvservice/cstandard/update/status`, data)
              .then(function (response) {
                if (response.status == 200) {
                  vm.$message({
                    showClose: true,
                    message: "操作成功",
                    type: "success",
                  });
                  vm.getContractRuleList();
                }
              })
              .catch(function (error) {});
          })
          .catch(() => {});
      } else {
        this.$message({
          showClose: true,
          message: "请选择规则",
          type: "warning",
        });
      }
    },
    /********************************分页*****************************/
    handleSizeChange(val) {
      this.pageSize = val;
      this.getContractRuleList();
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      this.getContractRuleList();
    },
  },
};
</script>

<style scoped lang="less">
.ContractRuleList {
  .more-down {
    width: 60px;
    float: left;
    color: #5675f5;
    height: 32px;
    line-height: 32px;
    text-align: center;
    cursor: pointer;
  }
  height: calc(100% - 98px);
  .queryList {
    margin-left: 24px;
    .el-form {
      display: flex;
      flex-wrap: nowrap;
      .el-form-item {
        flex: 1;
        display: flex;
        flex-wrap: nowrap;
      }
      .queryBtn {
        flex: 0 0 130px;
      }
    }
  }
  .btnList {
    margin-left: 24px;
    margin-bottom: 14px;
    padding-top: 18px;
    box-sizing: border-box;
    display: flex;
    height: 48px;
    span {
      line-height: 30px;
      margin-left: 14px;
    }
    .el-input {
      width: 200px;
      margin-right: 14px;
    }
  }
  .authorityBox {
    height: 100%;
    position: relative;
  }
  .abox-lableName {
    width: 90px;
    display: inline-block;
    span {
      color: red;
      font-size: 14px;
    }
  }
  .el-input {
    width: 200px;
  }
  .el-select {
    width: 200px;
  }
  .span-white {
    color: white !important;
  }
  .span-red {
    color: red;
  }
}
</style>

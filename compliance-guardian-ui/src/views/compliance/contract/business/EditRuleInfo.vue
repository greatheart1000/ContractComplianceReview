<template>
    <div class="EditRuleInfo">
        <div class="editFormList">
            <div style="height:100%;width: 960px;margin: 0px auto;">
                <div class="abox" style="width: 100%;">
                    <span class="abox-lableName"><span style="color:white">*</span>规则名称：</span>
                    <div class="input-box">
                        <el-input v-model="ruleData.ruleName" :maxlength="30" placeholder="请填写规则名称"></el-input>
                    </div>
                </div>
                <div class="abox">
                    <span class="abox-lableName"><span>*</span>规则类型：</span>
                    <div class="input-box">
                        <el-select
                            v-model="ruleData.ruleType"
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
                <div class="abox">
                    <span class="abox-lableName"><span style="color:white">*</span>合同类型：</span>
                    <div class="input-box">
                        <el-select
                            v-model="ruleData.contractTypes"
                            placeholder="请选择合同类型"
                            style="width: 100%"
                            clearable
                            collapse-tags
                            multiple
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
                <div class="abox" style="width: 100%;height: 100%;">
                    <span class="abox-lableName"><span>*</span>检查内容：</span>
                    <div class="input-box">
                        <el-input type="textarea" :rows="8" maxlength="320" v-model="ruleData.checkContent" placeholder="请填写检查内容,不超过160字"></el-input>
                    </div>
                </div>
                <div class="abox" style="width: 100%;height: 100%;">
                    <span class="abox-lableName"><span>*</span>解决建议：</span>
                    <div class="input-box">
                        <el-input type="textarea" :rows="8" maxlength="320" v-model="ruleData.solveSuggest" placeholder="请填写解决建议,不超过160字"></el-input>
                    </div>
                </div>
            </div>
        </div>
        <div class="clear"></div>
        <div style="text-align:center; margin-top:30px;overflow:hidden;width:100%;height:50px;">
            <div v-if="$yqvutil.getVerifyAuthority('compliance.contract_business.update')" style="margin-left:calc(50% - 36px)" class="new" @click="saveContractRule">
                <font-awesome-icon icon="far fa-save" />提交
            </div>
        </div>
    </div>
  </template>
  
  <script>
  import {mapState} from "vuex";
  export default {
    name: "EditRuleInfo",
    data() {
      return {
        //规则内容
        ruleData: {
            ruleId: "",
            ruleType: "",
            ruleName: "",
            contractTypes: [],
            checkContent: "",
            solveSuggest: "",
            ruleStatus: "",
        },
        //必填校验
        ruleTypeVerify: false,
        ruleNameVerify: false,
        contractTypesVerify: false,
        checkContentVerify: false,
        solveSuggestVerify: false,
      };
    },
    props: ["ruleId"],
    mounted() {
      if (this.ruleId) {
        this.getContractRule(this.ruleId);
      } else {
        this.ruleData.ruleId = "";
        this.ruleData.ruleType = "";
        this.ruleData.ruleName = "";
        this.ruleData.contractTypes = [];
        this.ruleData.checkContent = "";
        this.ruleData.solveSuggest = "";
        this.ruleData.ruleStatus = "";
      }
    },
    watch: {
        "ruleData.ruleType"() {
            this.verifyRuleInfo("ruleType");
        },
        "ruleData.ruleName"() {
            this.verifyRuleInfo("ruleName");
        },
        "ruleData.contractTypes"() {
            this.verifyRuleInfo("contractTypes");
        },
        "ruleData.checkContent"() {
            this.verifyRuleInfo("checkContent");
        },
        "ruleData.ruleStatus"() {
            this.verifyRuleInfo("ruleStatus");
        },
    },
    components: {
      
    },
    computed: {
      
    },
    methods: {
        verifyRuleInfo(type) {
            let data = this.ruleData;
            //规则类型
            if (type == "all" || type == "ruleType") {
                if (!data.ruleType) {
                    this.ruleTypeVerify = true;
                } else {
                    this.ruleTypeVerify = false;
                }
            }
            //规则名称
            if (type == "all" || type == "ruleName") {
                if (!data.ruleName) {
                    this.ruleNameVerify = true;
                } else {
                    this.ruleNameVerify = false;
                }
            }
            //合同类型
            if (type == "all" || type == "contractTypes") {
                if (!data.contractTypes || data.contractTypes.length == 0) {
                    this.contractTypesVerify = true;
                } else {
                    this.contractTypesVerify = false;
                }
            }
            //检查内容
            if (type == "all" || type == "checkContent") {
                if (!data.checkContent) {
                    this.checkContentVerify = true;
                } else {
                    this.checkContentVerify = false;
                }
            }
            //模型地址
            if (type == "all" || type == "solveSuggest") {
                if (!data.solveSuggest) {
                    this.solveSuggestVerify = true;
                } else {
                    this.solveSuggestVerify = false;
                }
            }
        },
      /********************************查询规则信息*****************************/
      getContractRule(ruleId) {
        let vm = this;
        vm.$http.post(vm.$yqvutil.getContextPath()+`yqvservice/cbusiness/find`,{"ruleId":ruleId})
          .then(function (response) {
            if (response.status === 200) {
              vm.ruleData = response.data;
            }
          })
          .catch(function (error) {});
      },
      /********************************保存规则*****************************/
      saveContractRule() {
        let vm = this;
        vm.verifyRuleInfo("all");
        let data = this.ruleData;
        //判断是否可以保存
        if (!this.ruleTypeVerify && !this.ruleNameVerify && !this.contractTypesVerify && !this.checkContentVerify && !this.solveSuggestVerify) {
          let message = "新增成功";
          let url = vm.$yqvutil.getContextPath()+"yqvservice/cbusiness/insert";
          if (vm.ruleData.ruleId) {
            url = vm.$yqvutil.getContextPath()+"yqvservice/cbusiness/update";
            message = "修改成功";
          }
          vm.$http
              .post(url, data)
              .then(function (response) {
                if (response.status === 200) {
                  vm.edit=true;
                  vm.$message({
                    showClose: true,
                    message: message,
                    type: "success",
                  });
                  vm.$parent.handleClick({"label":"规则列表"});
                  vm.$parent.getContractRuleList();
                }
              })
              .catch(function (error) {});
        } else {
          this.$message({
            showClose: true,
            message: "请填写完整的规则信息",
            type: "warning",
          });
        }
      },
    }
  };
  </script>
  
  <style scoped lang="less">
  .EditRuleInfo {
    height: 100%;
    overflow-y: auto;
    .el-input {
    min-width: 190px;
    }
    .el-textarea {
    min-width: 190px;
    }
    .el-select {
    min-width: 190px;
    }
  
  }
  </style>
package com.isoftstone.compliance.contract.model;

import java.util.List;

import com.ljh.yqvalley.core.model.ValleyEntity;

/**
 * 合同标准规则配置信息
 */
public class ContractStandardEntity extends ValleyEntity {
	
	private static final long serialVersionUID = -4507564590940753073L;
	/**
     * 规则ID
     */
    private String ruleId;
    /**
     * 数据类型
     */
    private String dataType;
	/**
     * 规则类型
     */
    private String ruleType;
    /**
     * 规则名称
     */
    private String ruleName;
    /**
     * 合同类型
     */
    private String contractType;
    /**
     * 合同类型
     */
    private String[] contractTypes;
    /**
     * 检查内容
     */
    private String checkContent;
    /**
     * 解决建议
     */
    private String solveSuggest;
    /**
     * 规则状态(生效:Y,失效:N)
     */
    private String ruleStatus;
    /**
     * 规则ID集合
     */
    private List<String> ruleIdList;
    
	public String getRuleId() {
		return ruleId;
	}
	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getRuleType() {
		return ruleType;
	}
	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	public String getContractType() {
		return contractType;
	}
	public void setContractType(String contractType) {
		this.contractType = contractType;
	}
	public String[] getContractTypes() {
		return contractTypes;
	}
	public void setContractTypes(String[] contractTypes) {
		this.contractTypes = contractTypes;
	}
	public String getCheckContent() {
		return checkContent;
	}
	public void setCheckContent(String checkContent) {
		this.checkContent = checkContent;
	}
	public String getSolveSuggest() {
		return solveSuggest;
	}
	public void setSolveSuggest(String solveSuggest) {
		this.solveSuggest = solveSuggest;
	}
	public String getRuleStatus() {
		return ruleStatus;
	}
	public void setRuleStatus(String ruleStatus) {
		this.ruleStatus = ruleStatus;
	}
	public List<String> getRuleIdList() {
		return ruleIdList;
	}
	public void setRuleIdList(List<String> ruleIdList) {
		this.ruleIdList = ruleIdList;
	}
    
}

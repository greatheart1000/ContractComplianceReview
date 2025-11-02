package com.isoftstone.compliance.chat.model;

import java.io.Serializable;

/**
 * 检查规则信息
 */
public class ChatCheckRuleEntity implements Serializable {
	
	private static final long serialVersionUID = 3324497664110842558L;
	
	/**
     * 规则名称
     */
    private String ruleName;
	/**
     * 规则类型
     */
    private String ruleType;
    /**
     * 合同类型
     */
    private String contractType;
    /**
     * 检查内容
     */
    private String checkContent;
    /**
     * 解决建议
     */
    private String solveSuggest;
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	public String getRuleType() {
		return ruleType;
	}
	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}
	public String getContractType() {
		return contractType;
	}
	public void setContractType(String contractType) {
		this.contractType = contractType;
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
    
}

package com.isoftstone.compliance.chat.model;

import java.io.Serializable;
import java.util.List;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class ChatInputEntity implements Serializable {
	
	private static final long serialVersionUID = 1593429901090026033L;
	
	/**
     * 会话Id
     */
    private String chatId;
	/**
	 * SSE通信
	 */
	private SseEmitter emitter;
	/**
     * 合规类型
     */
    private String complianceType;
    /**
     * 模型Id
     */
    private String modelId;
    /**
     * 合同类型
     */
    private String contractType;
    /**
     * 会话内容
     */
    private String content;
    /**
     * 提示词列表
     */
    private List<ChatPromptEntity> promptList;
    /**
     * 规则列表
     */
    private List<ChatCheckRuleEntity> ruleList;
    /**
     * 创建账号
     */
    private String createAccount;
	public String getChatId() {
		return chatId;
	}
	public void setChatId(String chatId) {
		this.chatId = chatId;
	}
	public SseEmitter getEmitter() {
		return emitter;
	}
	public void setEmitter(SseEmitter emitter) {
		this.emitter = emitter;
	}
	public String getComplianceType() {
		return complianceType;
	}
	public void setComplianceType(String complianceType) {
		this.complianceType = complianceType;
	}
	public String getModelId() {
		return modelId;
	}
	public void setModelId(String modelId) {
		this.modelId = modelId;
	}
	public String getContractType() {
		return contractType;
	}
	public void setContractType(String contractType) {
		this.contractType = contractType;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<ChatPromptEntity> getPromptList() {
		return promptList;
	}
	public void setPromptList(List<ChatPromptEntity> promptList) {
		this.promptList = promptList;
	}
	public List<ChatCheckRuleEntity> getRuleList() {
		return ruleList;
	}
	public void setRuleList(List<ChatCheckRuleEntity> ruleList) {
		this.ruleList = ruleList;
	}
	public String getCreateAccount() {
		return createAccount;
	}
	public void setCreateAccount(String createAccount) {
		this.createAccount = createAccount;
	}
	
}

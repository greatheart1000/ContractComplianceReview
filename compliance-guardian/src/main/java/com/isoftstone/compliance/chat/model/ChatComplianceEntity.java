package com.isoftstone.compliance.chat.model;

import java.util.List;

import com.ljh.yqvalley.core.model.ValleyEntity;

/**
 * 会话表
 */
public class ChatComplianceEntity extends ValleyEntity {
	
	private static final long serialVersionUID = 4691953219848715598L;
	/**
     * 会话id
     */
    private String chatId;
    /**
     * 会话标题
     */
    private String chatTitle;
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
     * 会话记录列表
     */
    private List<ChatRecordEntity> recordList;
    
	public String getChatId() {
		return chatId;
	}
	public void setChatId(String chatId) {
		this.chatId = chatId;
	}
	public String getChatTitle() {
		return chatTitle;
	}
	public void setChatTitle(String chatTitle) {
		this.chatTitle = chatTitle;
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
	public List<ChatRecordEntity> getRecordList() {
		return recordList;
	}
	public void setRecordList(List<ChatRecordEntity> recordList) {
		this.recordList = recordList;
	}
    
}

package com.isoftstone.compliance.chat.model;

import java.util.List;

import com.ljh.yqvalley.core.model.ValleyEntity;
import com.ljh.yqvalley.file.model.ValleyFileEntity;

/**
 * 会话记录表
 */
public class ChatRecordEntity extends ValleyEntity {
	
	private static final long serialVersionUID = -1167435137155657165L;
	/**
     * 记录Id
     */
    private String recordId;
	/**
     * 会话Id
     */
    private String chatId;
    /**
     * 对话角色
     */
    private String chatRole;
    /**
     * 会话内容
     */
    private String content;
    /**
     * 会话文件列表
     */
    private List<ValleyFileEntity> fileList;
    
	public String getRecordId() {
		return recordId;
	}
	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}
	public String getChatId() {
		return chatId;
	}
	public void setChatId(String chatId) {
		this.chatId = chatId;
	}
	public String getChatRole() {
		return chatRole;
	}
	public void setChatRole(String chatRole) {
		this.chatRole = chatRole;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public List<ValleyFileEntity> getFileList() {
		return fileList;
	}
	public void setFileList(List<ValleyFileEntity> fileList) {
		this.fileList = fileList;
	}
    
}

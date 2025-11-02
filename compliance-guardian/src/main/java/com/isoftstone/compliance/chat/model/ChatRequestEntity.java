package com.isoftstone.compliance.chat.model;

import java.util.List;

import com.ljh.yqvalley.file.model.ValleyFileEntity;

public class ChatRequestEntity extends ChatInputEntity {
	
    private static final long serialVersionUID = -9160686248536562509L;
    
	/**
     * 会话id
     */
    private String sessionId;
    /**
     * 提示词列表
     */
    private List<ValleyFileEntity> fileList;
    
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public List<ValleyFileEntity> getFileList() {
		return fileList;
	}
	public void setFileList(List<ValleyFileEntity> fileList) {
		this.fileList = fileList;
	}
    
}

package com.isoftstone.compliance.chat.model;

import java.io.Serializable;

public class ChatPromptEntity implements Serializable {
	
	private static final long serialVersionUID = -7349951964637654389L;
	
	/**
     * 对话角色
     */
    private String chatRole;
    /**
     * 会话内容
     */
    private String content;
    
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
	
}

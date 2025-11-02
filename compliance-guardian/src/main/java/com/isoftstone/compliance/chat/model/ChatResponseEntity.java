package com.isoftstone.compliance.chat.model;

/**
 * 会话响应
 */
public class ChatResponseEntity {
	
	/**
     * 会话Id
     */
    private String chatId;
    /**
     * 会话内容
     */
    private String content;
    /**
     * 是否结束
     */
    private Boolean done;
    
	public String getChatId() {
		return chatId;
	}
	public void setChatId(String chatId) {
		this.chatId = chatId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Boolean getDone() {
		return done;
	}
	public void setDone(Boolean done) {
		this.done = done;
	}
	
}

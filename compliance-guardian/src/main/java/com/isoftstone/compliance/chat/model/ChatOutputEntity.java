package com.isoftstone.compliance.chat.model;

import java.io.Serializable;

public class ChatOutputEntity implements Serializable {
	
	private static final long serialVersionUID = 246392776004462888L;
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
	public ChatOutputEntity(String chatId, String content, Boolean done,Boolean effective) {
		super();
		this.chatId = chatId;
		this.content = content;
		this.done = done;
	}
	public ChatOutputEntity(String content, Boolean done,Boolean effective) {
		super();
		this.content = content;
		this.done = done;
	}
	public ChatOutputEntity(String content, Boolean done) {
		super();
		this.content = content;
		this.done = done;
	}
	public ChatOutputEntity(String content) {
		super();
		this.content = content;
		this.done = false;
	}
	public ChatOutputEntity() {
		super();
	}
	
}

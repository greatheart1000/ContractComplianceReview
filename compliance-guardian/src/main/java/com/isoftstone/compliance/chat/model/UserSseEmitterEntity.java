package com.isoftstone.compliance.chat.model;

import java.io.Serializable;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class UserSseEmitterEntity implements Serializable {
	
	private static final long serialVersionUID = -2309059780666133968L;
	
	/**
     * 会话id
     */
    private String sessionId;
    /**
     * SSE
     */
    private SseEmitter emitter;
    
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public SseEmitter getEmitter() {
		return emitter;
	}
	public void setEmitter(SseEmitter emitter) {
		this.emitter = emitter;
	}

}

package com.isoftstone.compliance.chat.util;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class CacheSseUtil {
	
	private static final ConcurrentHashMap<String, SseEmitter> CACHE = new ConcurrentHashMap<String, SseEmitter>();
	
	/**
     * 获取缓存
     * @param sseId
     * @return
     */
    public static SseEmitter get(String sseId) {
        return CACHE.get(sseId);
    }
    
    /**
     * 添加缓存
     * @param sseId
     * @param sse
     */
    public static void add(String sseId, SseEmitter sse) {
        CACHE.put(sseId, sse);
    }

    /**
     * 删除缓存
     * @param sseId
     */
    public static void remove(String sseId) {
    	OpenRunnable.run(get(sseId),Objects::nonNull, ResponseBodyEmitter::complete);
        CACHE.remove(sseId);
    }
    
    /**
     * 清理缓存
     */
    public static void clear() {
    	for (String sseId : CACHE.keySet()) {
    		CacheSseUtil.remove(sseId);
		}
    }

}

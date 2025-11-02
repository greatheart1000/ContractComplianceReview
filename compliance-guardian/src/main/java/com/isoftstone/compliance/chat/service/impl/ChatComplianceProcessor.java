package com.isoftstone.compliance.chat.service.impl;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.isoftstone.compliance.chat.model.ChatOutputEntity;
import com.isoftstone.compliance.chat.model.ChatRequestEntity;
import com.isoftstone.compliance.chat.service.IChatComplianceService;
import com.ljh.yqvalley.asyn4j.service.IAsyn4jQueueProcessor;
import com.ljh.yqvalley.common.util.ValleyContextUtil;

@Service
public class ChatComplianceProcessor implements IAsyn4jQueueProcessor {
	
	private static Logger logger = LoggerFactory.getLogger(ChatComplianceProcessor.class);

	@SuppressWarnings("unchecked")
	@Override
	public void run(Serializable mapData) {
		Map<String, Object> map = (Map<String, Object>) mapData;
		Object paramObj = (Object)map.get("param");
		if (paramObj instanceof ChatRequestEntity) {
			ChatRequestEntity param = null;
			try {
				param = (ChatRequestEntity)paramObj;
				IChatComplianceService chatComplianceService = ValleyContextUtil.getBean("chatComplianceService",IChatComplianceService.class);
				chatComplianceService.sendChatProblem(param);
			} catch (Exception e) {
				if(param!=null) {
					SseEmitter emitter = param.getEmitter();
					if(emitter!=null) {
						try {
							emitter.send(SseEmitter.event().data(new ChatOutputEntity("异常："+e.getMessage())));
							emitter.send(SseEmitter.event().data(new ChatOutputEntity("",true)));
							emitter.complete();
						} catch (Exception e2) {}
					}
				}
				logger.error("异常："+e.getMessage());
			}
		}
	}

}

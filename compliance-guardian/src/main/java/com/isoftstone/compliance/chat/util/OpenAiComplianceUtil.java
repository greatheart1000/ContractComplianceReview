package com.isoftstone.compliance.chat.util;

import com.isoftstone.compliance.chat.model.ChatInputEntity;
import com.isoftstone.compliance.chat.model.ChatOutputEntity;
import com.isoftstone.compliance.chat.service.IContractCheckService;
import com.ljh.yqvalley.common.util.ValleyContextUtil;

public class OpenAiComplianceUtil {
	
	/**
     * 获取缓存
     * @param paramObj
     * @return
     */
    public static ChatOutputEntity chatEcho(ChatInputEntity param) {
    	ChatOutputEntity output = new ChatOutputEntity("",true);
    	IContractCheckService openAiTextService = null;
    	switch (param.getComplianceType()) {
    	case "contract":
			openAiTextService = ValleyContextUtil.getBean("contractCheckService",IContractCheckService.class);
			output = openAiTextService.chatEcho(param);
			break;
		default:
			return output;
		}
        return output;
    }

}

package com.isoftstone.compliance.config.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.retry.support.RetryTemplate;

import com.isoftstone.compliance.common.util.ComplianceServiceUtil;
import com.isoftstone.compliance.config.model.ModelConfigEntity;
import com.ljh.yqvalley.config.util.ValleyConfigUtil;

/**
 * 大模型对话工具类
 */
public class ModelChatUtil {
	
	public static final String MODEL_CONFIG_URL_KEY = "cg_model_config_url";
	public static final String MODEL_CONFIG_MODEL_KEY = "cg_model_config_model";
	public static final String MODEL_CONFIG_KEY_KEY = "cg_model_config_key";
	
	/**
	 * 获取对话客户端
	 * @return
	 */
	public static ChatClient getChatClient(String openaiCode) {
		String openaiUrl = ValleyConfigUtil.getAppConfigValue(MODEL_CONFIG_URL_KEY);
		String openaiModel = ValleyConfigUtil.getAppConfigValue(MODEL_CONFIG_MODEL_KEY);
		String openaiKey = ValleyConfigUtil.getAppConfigValue(MODEL_CONFIG_KEY_KEY);
		if(StringUtils.isNotEmpty(openaiCode)) {
			ModelConfigEntity aiConfig = ComplianceServiceUtil.getModelConfig(openaiCode);
			if(aiConfig != null) {
				openaiUrl = aiConfig.getModelUrl();
				openaiModel = aiConfig.getModelCode();
				openaiKey = aiConfig.getModelKey();
			}
		}
		OpenAiApi openApi = OpenAiApi.builder()
				.baseUrl(openaiUrl)
				.apiKey(openaiKey)
				.build();
		OpenAiChatOptions optionsConfig = OpenAiChatOptions.builder()
				.model(openaiModel)
				.temperature(0.7)//随机性
                .build();
		RetryTemplate retryTemp= RetryTemplate.builder()
				.maxAttempts(3)//重试尝试的最大次数
				.build();
		//初始化模型连接
        ChatModel chatModel = OpenAiChatModel.builder()
        		.openAiApi(openApi)
        		.defaultOptions(optionsConfig)
        		.retryTemplate(retryTemp)
        		.build();
        ChatClient client = ChatClient.builder(chatModel)
        		.build();
		return client;
	}

}

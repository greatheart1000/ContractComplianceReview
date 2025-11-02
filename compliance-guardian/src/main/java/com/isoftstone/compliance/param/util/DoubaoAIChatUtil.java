package com.isoftstone.compliance.param.util;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.retry.support.RetryTemplate;

/**
 * 大模型对话工具类
 */
public class DoubaoAIChatUtil {
	
	public static final String OPENAI_CONFIG_URL_KEY = "doubao_model_config_url";
	public static final String OPENAI_CONFIG_KEY_KEY = "doubao_model_config_key";
	public static final String OPENAI_CONFIG_MODEL_KEY = "doubao_model_config_model";
	
	
	/**
	 * 获取对话客户端
	 * @return
	 */
	public static ChatClient getChatClient(ResponseFormat jsonFormat) {
		//String modelUrl = ValleyConfigUtil.getAppConfigValue(OPENAI_CONFIG_URL_KEY);
		//String modelCode = ValleyConfigUtil.getAppConfigValue(OPENAI_CONFIG_MODEL_KEY);
		//String modelKey = ValleyConfigUtil.getAppConfigValue(OPENAI_CONFIG_KEY_KEY);
		String modelUrl = "https://ark.cn-beijing.volces.com/api";
		String modelCode = "doubao-1-5-pro-32k-250115";
		String modelKey = "f62bafd2-1269-4169-b072-7994b36541a7";
		OpenAiApi openApi = OpenAiApi.builder()
				.baseUrl(modelUrl)
				.apiKey(modelKey)
				.build();
		OpenAiChatOptions optionsConfig = OpenAiChatOptions.builder()
				.model(modelCode)
				.temperature(0.7)//随机性
				.responseFormat(jsonFormat)//输出内容格式化
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

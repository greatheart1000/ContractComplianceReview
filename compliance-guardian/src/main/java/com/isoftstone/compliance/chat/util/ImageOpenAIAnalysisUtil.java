package com.isoftstone.compliance.chat.util;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.model.Media;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import com.ljh.yqvalley.common.util.ValleyHelpUtil;
import com.ljh.yqvalley.config.util.ValleyConfigUtil;

public class ImageOpenAIAnalysisUtil {
	
	public static void main(String[] args) {
		String fileUrl = "https://hunyuan-1351401420.cos.ap-guangzhou.tencentcos.cn/20250509154206.png";
		String fileName = "20250509154206.png";
		try {
			String info = ImageOpenAIAnalysisUtil.extractUrlContent(fileUrl,fileName);
			System.out.println(info);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	/**
	 * 解析图片中的文字内容
	 * @param base64
	 * @return
	 */
	public static String extractContent(String filePath) {
		String contentText = "";
		ChatClient client = ImageOpenAIAnalysisUtil.getChatClient();
        try {
        	String postfix = ValleyHelpUtil.getPostfix(filePath);
        	MimeType mimeType = MimeTypeUtils.IMAGE_JPEG;
        	switch (postfix.toLowerCase()) {
			case "png":
				mimeType = MimeTypeUtils.IMAGE_PNG;
				break;
			case "jpg":
				mimeType = MimeTypeUtils.IMAGE_JPEG;
				break;
			case "jpeg":
				mimeType = MimeTypeUtils.IMAGE_JPEG;
				break;
			default:
				break;
			}
        	byte[] imageData = Files.readAllBytes(Paths.get(filePath));	
    		List<Media> mediaList = new ArrayList<Media>();
    		Media media = Media.builder().mimeType(mimeType).data(imageData).build();
    		mediaList.add(media);
            List<Message> messages = new ArrayList<>();
            messages.add(new UserMessage("识别图片文字内容并返回识别结果",mediaList));
        	ChatResponse response = client.prompt()
            		.messages(messages)
            		.call()
            		.chatResponse();
    		if(response.getResult()!=null && response.getResult().getOutput()!=null) {
    			contentText = response.getResult().getOutput().getText();
    		}
		} catch (Exception e) {
			contentText = "";
		}
		return contentText;
	}
	
	/**
	 * 解析图片中的文字内容
	 * @param base64
	 * @return
	 */
	public static String extractUrlContent(String fileUrl,String fileName) {
		String contentText = "";
		ChatClient client = ImageOpenAIAnalysisUtil.getChatClient();
        try {
        	String postfix = ValleyHelpUtil.getPostfix(fileName);
        	MimeType mimeType = MimeTypeUtils.IMAGE_JPEG;
        	switch (postfix.toLowerCase()) {
			case "png":
				mimeType = MimeTypeUtils.IMAGE_PNG;
				break;
			case "jpg":
				mimeType = MimeTypeUtils.IMAGE_JPEG;
				break;
			case "jpeg":
				mimeType = MimeTypeUtils.IMAGE_JPEG;
				break;
			default:
				break;
			}
    		List<Media> mediaList = new ArrayList<Media>();
    		Media media = Media.builder().mimeType(mimeType).data(new URI(fileUrl).toURL()).build();
    		mediaList.add(media);
            List<Message> messages = new ArrayList<>();
            messages.add(new UserMessage("识别图片文字内容并返回识别结果",mediaList));
        	ChatResponse response = client.prompt()
            		.messages(messages)
            		.call()
            		.chatResponse();
    		if(response.getResult()!=null && response.getResult().getOutput()!=null) {
    			contentText = response.getResult().getOutput().getText();
    		}
		} catch (Exception e) {
			contentText = "";
		}
		return contentText;
	}
	
	/**
	 * 获取对话客户端
	 * @return
	 */
	public static ChatClient getChatClient() {
		String modelUrl = ValleyConfigUtil.getAppConfigValue(OpenAIChatUtil.OPENAI_CONFIG_URL_KEY);
		String modelKey = ValleyConfigUtil.getAppConfigValue(OpenAIChatUtil.OPENAI_CONFIG_KEY_KEY);
		String modelCode = ValleyConfigUtil.getAppConfigValue(OpenAIChatUtil.OPENAI_CONFIG_VISION_MODEL_KEY);
		OpenAiApi openApi = OpenAiApi.builder()
				.baseUrl(modelUrl)
				.apiKey(modelKey)
				.build();
		OpenAiChatOptions optionsConfig = OpenAiChatOptions.builder()
				.model(modelCode)
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

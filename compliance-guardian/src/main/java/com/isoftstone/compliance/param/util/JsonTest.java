package com.isoftstone.compliance.param.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.openai.api.ResponseFormat;
import org.springframework.ai.openai.api.ResponseFormat.Type;

public class JsonTest {

	public static void main(String[] args) {
		String content = """
				今天是2025年5月12日，深圳的天气情况如下：

				天气现象：晴。
				温度：30℃，体感温度32℃，最低气温20℃，最高气温30℃。
				湿度：37%。
				风力风向：2级西南风，日间和夜间风向主要为北风或东风，风力等级多为1~3级。
				空气质量：空气质量指数为69，空气质量等级为良。
				紫外线指数：7，属于强防护等级。
				日出日落时间：日出时间为05:45，日落时间为18:55。
				根据这些信息，深圳今日天气晴朗，气温较高，紫外线较强，空气质量良好，适合外出活动，但需注意防晒和补水。
				""";
		String systemPrompt = """
				#### 定位
				- 智能助手名称 ：新闻分类专家
				- 主要任务 ：对输入的新闻文本进行自动分类，识别其所属的新闻种类。
				
				#### 能力
				- 文本分析 ：能够准确分析新闻文本的内容和结构。
				- 分类识别 ：根据分析结果，将新闻文本分类到预定义的种类中。
				
				#### 知识储备
				- 新闻种类 ：
				  - 政治
				  - 经济
				  - 科技
				  - 娱乐
				  - 体育
				  - 教育
				  - 健康
				  - 国际
				  - 国内
				  - 社会
				
				#### 使用说明
				- 输入 ：一段新闻文本。
				- 输出 ：只输出新闻文本所属的种类，不需要额外解释。
				""";
		ResponseFormat jsonFormat = ResponseFormat.builder()
				.type(Type.JSON_OBJECT)
		//.jsonSchema(jsonSchema)
		.build();
		
		ChatClient client = DoubaoAIChatUtil.getChatClient(jsonFormat);
		
		//构建问答信息
		List<Message> messages = new ArrayList<>();
		systemPrompt = "以JSON格式提取关键信息";
        messages.add(new SystemMessage(systemPrompt));
        //最新对话
        messages.add(new UserMessage(content));
        
        AssistantMessage assistant = sendGeneralOutput(client,messages);
        if(assistant!=null) {
        	System.out.println(assistant.getText());
        }
	}
	
	/**
	 * 发起普通请求
	 * @param client
	 * @param messages
	 * @param functionCallbacks
	 * @return
	 */
	private static AssistantMessage sendGeneralOutput(ChatClient client, List<Message> messages){
		AssistantMessage assistantMessage = null;
		try {
			if(client!=null && CollectionUtils.isNotEmpty(messages)) {
				ChatResponse response = client.prompt()
		        		.messages(messages)
		        		.call()
		        		.chatResponse();
				if(response.getResult()!=null) {
					assistantMessage = response.getResult().getOutput();
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			assistantMessage = null;
		}
		return assistantMessage;
	}

}

package com.isoftstone.compliance.chat.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.isoftstone.compliance.chat.model.ChatCheckRuleEntity;
import com.isoftstone.compliance.chat.model.ChatInputEntity;
import com.isoftstone.compliance.chat.model.ChatOutputEntity;
import com.isoftstone.compliance.chat.service.IContractCheckService;
import com.isoftstone.compliance.chat.util.OpenAIChatUtil;
import com.isoftstone.compliance.contract.model.ContractStandardEntity;
import com.isoftstone.compliance.contract.service.IContractStandardService;
import com.ljh.yqvalley.config.util.ValleyConfigUtil;

import jakarta.annotation.Resource;
import reactor.core.publisher.Flux;

@Service
public class ContractCheckService implements IContractCheckService {
	
	@Resource
	private IContractStandardService contractStandardService;
	
	@Override
	public ChatOutputEntity chatEcho(ChatInputEntity param) {
		String defaultSystemPrompt = "你是一个合同合规助手，能分析合同中的法律风险并对识别的风险点给出合理建议。";
		ChatOutputEntity output = new ChatOutputEntity("",true);
		SseEmitter emitter = param.getEmitter();
		if(emitter==null) {
			return output;
		}
		ChatClient client = OpenAIChatUtil.getChatClient(param.getModelId(),0.3);
        //构建问答信息
        List<Message> messages = new ArrayList<>();
        String systemPrompt = this.getSystemPrompt(param.getContractType());
        if(StringUtils.isEmpty(systemPrompt)) {
        	systemPrompt = defaultSystemPrompt;
        }
        messages.add(new SystemMessage(systemPrompt));
        //最新对话
        messages.add(new UserMessage(param.getContent()));
        //发起流式调用
        String content = sendStreamOutput(emitter, client, messages);
        output.setContent(content);
        try {
			emitter.send(SseEmitter.event().data(new ChatOutputEntity("",true)));
			emitter.complete();
		} catch (Exception e2) {
			
		}
		return output;
	}
	
	/**
	 * 发起流式请求
	 * @param emitter
	 * @param chatClient
	 * @param messages
	 */
	private String sendStreamOutput(SseEmitter emitter, ChatClient client, List<Message> messages){
		StringBuffer contentBuffer = new StringBuffer();
		try {
			if(client!=null && CollectionUtils.isNotEmpty(messages)) {
				Flux<ChatResponse> stream = client.prompt()
		        		.messages(messages)
		        		.stream()
		        		.chatResponse();
				stream.toStream().forEach(response -> {
		            response.getResults().forEach(item -> {
		                AssistantMessage chatOutput = item.getOutput();
		                if (chatOutput.getMetadata().containsKey("finishReason") && chatOutput.getMetadata().get("finishReason").equals("STOP")) {
		                	try {
		                		emitter.send(SseEmitter.event().data(new ChatOutputEntity("",true)));
			        			emitter.complete();
							} catch (Exception e) {
								return;
							}
		                    return;
		                }
		                contentBuffer.append(chatOutput.getText());
		                try {
		                	emitter.send(SseEmitter.event().data(new ChatOutputEntity(chatOutput.getText()+"")));
						} catch (Exception e) {}
		            });
		        });
			}
		} catch (Exception e) {
			try {
				emitter.send(SseEmitter.event().data(new ChatOutputEntity(e.getMessage())));
        		contentBuffer.append(e.getMessage());
			} catch (Exception e2) {}
		}
		return contentBuffer.toString();
	}
	
	/**
	 * 发送消息
	 * @param output
	 * @param emitter
	 * @param rawData
	 */
	private void sendChatOutput(SseEmitter emitter, String content) {
		if (StringUtils.isNotEmpty(content)) {
			try {
		    	if(StringUtils.isNotEmpty(content)) {
		    		for (int i = 0; i < content.length(); i++) {
		    			emitter.send(SseEmitter.event().data(new ChatOutputEntity(content.charAt(i)+"")));
		        		try {
							TimeUnit.MILLISECONDS.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
		            }
		    	}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 处理提示词
	 * @param contractType
	 * @return
	 */
	private String getSystemPrompt(String contractType) {
		Map<String,String> contractTypeMap = ValleyConfigUtil.getSelectItemNameMap("CG_CONTRACT_TYPE");
		Map<String,String> ruleTypeMap = ValleyConfigUtil.getSelectItemNameMap("CG_CONTRACT_RULE_TYPE");
		StringBuffer systemPromptBuffer = new StringBuffer();
		ContractStandardEntity rule = new ContractStandardEntity();
		rule.setContractType(contractType);
		rule.setRuleStatus("Y");
		List<ChatCheckRuleEntity> ruleList = contractStandardService.findContractStandardList(rule);
		if(CollectionUtils.isNotEmpty(ruleList)) {
			systemPromptBuffer.append("""
					## 角色定位
					你是一名专业的合同条款扫描引擎，严格按给定规则执行合规性检查。仅反馈命中规则的条款，不进行额外解释或建议。
					""".formatted(contractTypeMap.get(contractType)));
			systemPromptBuffer.append("""
					## 检查规则库
					""");
			for (ChatCheckRuleEntity checkRule : ruleList) {
				systemPromptBuffer.append("""
						```json
						{
							规则名称：%s ,
							规则类型：%s ,
							检查内容：%s ,
							命中建议：%s 
						}
						```
						""".formatted(checkRule.getRuleName(),ruleTypeMap.get(checkRule.getRuleType()),checkRule.getCheckContent(),checkRule.getSolveSuggest()));
			}
			systemPromptBuffer.append("""
					## 执行原则
					- 逐条匹配规则，未命中则静默跳过
					""");
			systemPromptBuffer.append("""
					## 输出规范
					### 一、[规则类型] 规则名称
					- **原文引用**：4.4.1 要求甲方除应依约支付应付费用外，还应按所欠费用10%的标准向乙方支付违约金。
					- **合规建议**：合同中的违约金支出分别已到达10%比例，超出BD要求5%以内，请联系BD成本中心负责人进行报备。
					""");
			systemPromptBuffer.append("""
					【输出示例】
					### 合同风险点分析及建议
					#### 一、【红线】违约责任中的违约金超出限额百分比
					- **原文引用**：
					1、4.4.1 要求甲方除应依约支付应付费用外，还应按所欠费用10%的标准向乙方支付违约金。
					- **合规建议**：合同中的违约金支出分别已到达10%比例，超出BD要求5%以内，请联系BD成本中心负责人进行报备。
					
					#### 二、【提示】合同中是否有合同总金额或单价
					- **原文引用**：
					1、附件2中序号1：采购产品对象为：腾讯云全栈产品，客户可根据实际业务需求进行按需采购；双方约定采购金额为：300000元/年，客户可根据每月业务需求情况进行按需回款。
					- **合规建议**：合同中已明确合同总金额。
					""");
		}
		return systemPromptBuffer.toString();
	}

}

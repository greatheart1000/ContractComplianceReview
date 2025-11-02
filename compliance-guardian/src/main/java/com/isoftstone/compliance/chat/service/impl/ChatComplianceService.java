package com.isoftstone.compliance.chat.service.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.isoftstone.compliance.chat.dao.IChatComplianceMapper;
import com.isoftstone.compliance.chat.model.ChatComplianceEntity;
import com.isoftstone.compliance.chat.model.ChatOutputEntity;
import com.isoftstone.compliance.chat.model.ChatPromptEntity;
import com.isoftstone.compliance.chat.model.ChatRecordEntity;
import com.isoftstone.compliance.chat.model.ChatRequestEntity;
import com.isoftstone.compliance.chat.service.IChatComplianceService;
import com.isoftstone.compliance.chat.util.CacheSseUtil;
import com.isoftstone.compliance.chat.util.OpenAiComplianceUtil;
import com.ljh.yqvalley.common.util.RedisHelpUtil;
import com.ljh.yqvalley.common.util.ValleyHelpUtil;
import com.ljh.yqvalley.core.model.PageEntity;
import com.ljh.yqvalley.core.model.PageResult;
import com.ljh.yqvalley.file.model.ValleyFileEntity;

import jakarta.annotation.Resource;

@Service
public class ChatComplianceService implements IChatComplianceService {
	
	@Resource
	private IChatComplianceMapper chatComplianceMapper;

	@Override
	public ChatComplianceEntity findChatSession(ChatComplianceEntity chatOpen) {
		return chatComplianceMapper.findChatSession(chatOpen);
	}

	@Override
	public PageResult<ChatComplianceEntity> findChatSessionByPage(ChatComplianceEntity chatOpen, PageEntity page) {
		return chatComplianceMapper.findChatSessionByPage(chatOpen, page);
	}

	@Override
	public ChatComplianceEntity insertChatSession(ChatComplianceEntity chatOpen) {
		chatOpen.setChatTitle(ValleyHelpUtil.substringByte(chatOpen.getChatTitle(), 60));
		chatOpen.setChatId(RedisHelpUtil.getIncrbyOnlyId("chat_id"));
		chatComplianceMapper.insertChatSession(chatOpen);
		return chatOpen;
	}

	@Override
	public int deleteChatSession(ChatComplianceEntity chatOpen) {
		chatComplianceMapper.deleteChatProblem(chatOpen.getChatId(),chatOpen.getCreateAccount());
		chatComplianceMapper.deleteChatRecordFile(chatOpen.getChatId(),chatOpen.getCreateAccount());
		return chatComplianceMapper.deleteChatSession(chatOpen.getChatId(),chatOpen.getCreateAccount());
	}

	@Override
	public ChatRecordEntity sendChatProblem(ChatRequestEntity param) {
		ChatRecordEntity userRecord = new ChatRecordEntity();
		//获取历史会话(用于连续会话)
		int problemMaxNum = 10;
		List<ChatPromptEntity> promptList = chatComplianceMapper.findChatProblemList(param.getChatId());
		if(CollectionUtils.isNotEmpty(promptList)) {
			if (promptList.size() > problemMaxNum) {
				promptList.subList(0, promptList.size()-problemMaxNum).clear();
	        }
			param.setPromptList(promptList);
		}
		//记录用户会话
		userRecord.setRecordId(RedisHelpUtil.getIncrbyOnlyId("chat_record_id"));
		userRecord.setChatRole("user");
		userRecord.setChatId(param.getChatId());
		userRecord.setContent(param.getContent());
		userRecord.setCreateAccount(param.getCreateAccount());
		//发起会话
		ChatOutputEntity output = null;
		SseEmitter emitter = CacheSseUtil.get(param.getSessionId());
		if(emitter!=null) {
			param.setEmitter(emitter);
			output = OpenAiComplianceUtil.chatEcho(param);
		}
		if(StringUtils.isNotEmpty(output.getContent())) {
			chatComplianceMapper.insertChatProblem(userRecord);
			chatComplianceMapper.insertChatRecordFile(param.getFileList(), userRecord.getRecordId(), userRecord.getChatId());
			userRecord.setRecordId(RedisHelpUtil.getIncrbyOnlyId("chat_record_id"));
			userRecord.setChatRole("assistant");
			userRecord.setContent(output.getContent());
			chatComplianceMapper.insertChatProblem(userRecord);
		}
		return userRecord;
	}

	@Override
	public PageResult<ChatRecordEntity> findChatProblemByPage(ChatRecordEntity record,PageEntity page) {
		PageResult<ChatRecordEntity> result = chatComplianceMapper.findChatProblemByPage(record, page);
		if(result!=null && CollectionUtils.isNotEmpty(result.getResult())) {
			for (ChatRecordEntity recordInfo : result.getResult()) {
				List<ValleyFileEntity> fileList = chatComplianceMapper.findChatRecordFileList(recordInfo.getRecordId());
				recordInfo.setFileList(fileList);
			}
		}
		return result;
	}
	
	@Override
	public List<ChatRecordEntity> findChatRecordList(ChatRecordEntity record) {
		List<ChatRecordEntity> recordList = chatComplianceMapper.findChatRecordList(record);
		if(CollectionUtils.isNotEmpty(recordList)) {
			for (ChatRecordEntity recordInfo : recordList) {
				if("user".equals(recordInfo.getChatRole())) {
					List<ValleyFileEntity> fileList = chatComplianceMapper.findChatRecordFileList(recordInfo.getRecordId());
					if(CollectionUtils.isNotEmpty(fileList)) {
						recordInfo.setContent(fileList.get(0).getFileName());
					}
					recordInfo.setFileList(fileList);
				}
			}
		}
		return recordList;
	}

	@Override
	public List<ChatPromptEntity> findChatProblemList(String chatId) {
		return chatComplianceMapper.findChatProblemList(chatId);
	}

}

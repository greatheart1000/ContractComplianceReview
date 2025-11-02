package com.isoftstone.compliance.chat.service;

import java.util.List;

import com.isoftstone.compliance.chat.model.ChatComplianceEntity;
import com.isoftstone.compliance.chat.model.ChatPromptEntity;
import com.isoftstone.compliance.chat.model.ChatRecordEntity;
import com.isoftstone.compliance.chat.model.ChatRequestEntity;
import com.ljh.yqvalley.core.model.PageEntity;
import com.ljh.yqvalley.core.model.PageResult;

public interface IChatComplianceService {
	
	/**
	 * 查询指定条件会话信息
	 * @param chatOpen
	 * @return
	 */
	public ChatComplianceEntity findChatSession(ChatComplianceEntity chatOpen);
	
	/**
	 * 分页查询会话列表
	 * @param chatOpen
	 * @param page
	 * @return
	 */
	public PageResult<ChatComplianceEntity> findChatSessionByPage(ChatComplianceEntity chatOpen,PageEntity page);
	
	/**
	 * 插入会话信息
	 * @param chatOpen
	 * @return
	 */
	public ChatComplianceEntity insertChatSession(ChatComplianceEntity chatOpen);
	
	/**
	 * 删除会话信息
	 * @param chatOpen
	 * @return
	 */
	public int deleteChatSession(ChatComplianceEntity chatOpen);
	
	/**
	 * 发送会话请求
	 * @param param
	 * @return
	 */
	public ChatRecordEntity sendChatProblem(ChatRequestEntity param);
	
	/**
	 * 分页查询会话记录
	 * @param record
	 * @return
	 */
	public PageResult<ChatRecordEntity> findChatProblemByPage(ChatRecordEntity record,PageEntity page);
	
	/**
	 * 查询会话记录列表
	 * @param record
	 * @return
	 */
	public List<ChatRecordEntity> findChatRecordList(ChatRecordEntity record);
	
	/**
	 * 查询历史会话记录
	 * @param chatId
	 * @return
	 */
	public List<ChatPromptEntity> findChatProblemList(String chatId);

}

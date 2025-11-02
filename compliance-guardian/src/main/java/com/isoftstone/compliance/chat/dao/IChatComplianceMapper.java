package com.isoftstone.compliance.chat.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.isoftstone.compliance.chat.model.ChatComplianceEntity;
import com.isoftstone.compliance.chat.model.ChatPromptEntity;
import com.isoftstone.compliance.chat.model.ChatRecordEntity;
import com.ljh.yqvalley.core.model.PageEntity;
import com.ljh.yqvalley.core.model.PageResult;
import com.ljh.yqvalley.file.model.ValleyFileEntity;

public interface IChatComplianceMapper {
	
	/**
	 * 查询指定条件会话信息
	 * @param chatOpen
	 * @return
	 */
	public ChatComplianceEntity findChatSession(@Param("ent") ChatComplianceEntity chatOpen);
	
	/**
	 * 分页查询会话列表
	 * @param chatOpen
	 * @param page
	 * @return
	 */
	public PageResult<ChatComplianceEntity> findChatSessionByPage(@Param("ent") ChatComplianceEntity chatOpen,@Param("page") PageEntity page);
	
	/**
	 * 插入会话信息
	 * @param chatOpen
	 * @return
	 */
	public int insertChatSession(@Param("ent") ChatComplianceEntity chatOpen);
	
	/**
	 * 删除会话信息
	 * @param chatId
	 * @param createAccount
	 * @return
	 */
	public int deleteChatSession(@Param("chatId") String chatId,@Param("createAccount") String createAccount);
	
	/**
	 * 删除会话记录
	 * @param chatId
	 * @param createAccount
	 * @return
	 */
	public int deleteChatProblem(@Param("chatId") String chatId,@Param("createAccount") String createAccount);
	
	/**
	 * 分页查询会话记录
	 * @param record
	 * @return
	 */
	public PageResult<ChatRecordEntity> findChatProblemByPage(@Param("ent") ChatRecordEntity record,@Param("page") PageEntity page);
	
	/**
	 * 查询会话记录列表
	 * @param record
	 * @return
	 */
	public List<ChatRecordEntity> findChatRecordList(@Param("ent") ChatRecordEntity record);
	
	/**
	 * 插入会话记录
	 * @param record
	 * @return
	 */
	public int insertChatProblem(@Param("ent") ChatRecordEntity record);
	
	/**
	 * 查询历史会话记录
	 * @param chatId
	 * @return
	 */
	public List<ChatPromptEntity> findChatProblemList(@Param("chatId") String chatId);
	
	/**
	 * 查询会话记录文件
	 * @param chatId
	 * @return
	 */
	public List<ValleyFileEntity> findChatRecordFileList(@Param("recordId") String recordId);
	
	/**
	 * 插入会话记录文件
	 * @param fileList
	 * @param recordId
	 * @param chatId
	 * @return
	 */
	public int insertChatRecordFile(@Param("list") List<ValleyFileEntity> fileList,@Param("recordId") String recordId,@Param("chatId") String chatId);
	
	/**
	 * 删除会话记录文件
	 * @param chatId
	 * @param createAccount
	 * @return
	 */
	public int deleteChatRecordFile(@Param("chatId") String chatId,@Param("createAccount") String createAccount);

}

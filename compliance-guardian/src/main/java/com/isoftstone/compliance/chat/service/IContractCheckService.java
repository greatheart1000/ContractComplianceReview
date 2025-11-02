package com.isoftstone.compliance.chat.service;

import com.isoftstone.compliance.chat.model.ChatInputEntity;
import com.isoftstone.compliance.chat.model.ChatOutputEntity;

public interface IContractCheckService {
	
	/**
	 * 聊天内容输出
	 * @param param
	 */
	public ChatOutputEntity chatEcho(ChatInputEntity param);

}

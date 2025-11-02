package com.isoftstone.compliance.chat.service;

import java.util.List;

import com.ljh.yqvalley.file.model.ValleyFileEntity;

public interface IFileContentExtractService {
	
	/**
	 * 提取文件内容
	 * @param fileList
	 * @return
	 */
	public String findFileContent(List<ValleyFileEntity> fileList);
}

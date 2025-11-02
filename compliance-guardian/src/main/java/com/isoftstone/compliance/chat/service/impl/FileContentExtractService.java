package com.isoftstone.compliance.chat.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.isoftstone.compliance.chat.service.IFileContentExtractService;
import com.isoftstone.compliance.chat.util.ImageCompressUtil;
import com.isoftstone.compliance.chat.util.ImageOpenAIAnalysisUtil;
import com.isoftstone.compliance.chat.util.PdfAliyunAnalysisUtil;
import com.isoftstone.compliance.chat.util.WordContentAnalysisUtil;
import com.ljh.yqvalley.common.util.ValleyContextUtil;
import com.ljh.yqvalley.file.model.ValleyFileEntity;
import com.ljh.yqvalley.file.service.IFileStorageCustom;
import com.ljh.yqvalley.file.service.IValleyFileStorageService;

import jakarta.annotation.Resource;

@Service
public class FileContentExtractService implements IFileContentExtractService {
	
	@Resource
    private IValleyFileStorageService valleyFlieService;
	
	public static final long MAX_FILE_SIZE = 3072;

	@Override
	public String findFileContent(List<ValleyFileEntity> fileList) {
		String content = "";
		String filePath = "";
		IFileStorageCustom fileStorageService = (IFileStorageCustom) ValleyContextUtil.getBean("fileStorageCustom", IFileStorageCustom.class);
		for (ValleyFileEntity valleyFile : fileList) {
			valleyFile = valleyFlieService.findValleyFileCache(valleyFile.getFileId());
			if(valleyFile!=null && StringUtils.isNotEmpty(valleyFile.getFilePath())) {
				byte[] bytes = fileStorageService.downloadFile(valleyFile);
				switch (valleyFile.getFileType()) {
				case "model_image":
					filePath = valleyFile.getFilePath();
					if(valleyFile.getFileSize()/1024>MAX_FILE_SIZE) {
						//压缩图片
						filePath = ImageCompressUtil.compressImageToSize(filePath, MAX_FILE_SIZE);
					}
					content += ImageOpenAIAnalysisUtil.extractContent(valleyFile.getFilePath());
					break;
				case "model_pdf":
					content += PdfAliyunAnalysisUtil.extractContent(valleyFile.getFilePath(),PdfAliyunAnalysisUtil.TEXT_TYPE_MARKDOWN);
					break;
				case "model_word":
					content += WordContentAnalysisUtil.extractContent(bytes);
					break;
				default:
					break;
				}
			}	
		}
		return content;
	}
	
}

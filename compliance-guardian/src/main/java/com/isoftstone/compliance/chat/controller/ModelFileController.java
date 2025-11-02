package com.isoftstone.compliance.chat.controller;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.isoftstone.compliance.common.annotation.ModelLoginVerify;
import com.isoftstone.compliance.common.constant.ComplianceQConstant;
import com.isoftstone.compliance.common.util.ModelRequestContextUtil;
import com.ljh.yqvalley.common.util.ValleyContextUtil;
import com.ljh.yqvalley.common.util.ValleyHelpUtil;
import com.ljh.yqvalley.core.annotation.RateLimit;
import com.ljh.yqvalley.core.model.ValleyResult;
import com.ljh.yqvalley.file.model.ValleyFileEntity;
import com.ljh.yqvalley.file.service.IFileStorageCustom;
import com.ljh.yqvalley.file.service.IValleyFileStorageService;
import com.ljh.yqvalley.user.util.RequestContextUtil;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(ComplianceQConstant.MODEL_FILE)
public class ModelFileController {
	
	private static Logger logger = LoggerFactory.getLogger(ModelFileController.class);
    
    @Resource
    private IValleyFileStorageService valleyFlieService;
    
    //文件类型
    public static final Map<String,String> FILE_TYPE_MAP = new HashMap<String,String>();
    static {
        FILE_TYPE_MAP.put("jpg", "image");
        FILE_TYPE_MAP.put("png", "image");
        FILE_TYPE_MAP.put("jpeg", "image");
        FILE_TYPE_MAP.put("pdf", "pdf");
        FILE_TYPE_MAP.put("doc", "word");
        FILE_TYPE_MAP.put("docx", "word");
    }
    
    /**
     * 上传微信图像
     * @param request
     * @return
     */
    @RateLimit(rate = 6.0)
    @ModelLoginVerify
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ValleyResult<ValleyFileEntity> fileUpload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
    	ValleyFileEntity newFile = null;
        String fileType = "model_";
        String authority = "private";
        if (file != null) {
        	String userAccount = ModelRequestContextUtil.getCurrentAccount();
            IFileStorageCustom fileStorageService = (IFileStorageCustom) ValleyContextUtil.getBean("fileStorageCustom", IFileStorageCustom.class);
            String fileName = file.getOriginalFilename();
            fileName = fileName.replaceAll("\\s*", "");
            String postfix = ValleyHelpUtil.getPostfix(fileName);
            if(StringUtils.isNotEmpty(postfix)) {
                postfix = postfix.toLowerCase();
                if(StringUtils.isNotEmpty(FILE_TYPE_MAP.get(postfix))) {
                	fileType += FILE_TYPE_MAP.get(postfix);
                    //上传文件
                    newFile = fileStorageService.saveFile(file, fileType);
                    newFile.setCreateAccount(userAccount);
                    newFile.setAccessAuthority(authority);
                    //插入文件
                    newFile = valleyFlieService.insertValleyFileInfo(newFile);
                }
            }
        }
        if(newFile==null || StringUtils.isEmpty(newFile.getFileId())) {
            return ValleyResult.newFailure();
        }
        return ValleyResult.newSuccess(newFile);
    }

    /**
     * 获取微信头像
     * @param response
     */
    @RateLimit(rate = 10.0)
    @ModelLoginVerify
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public void downloadUserFile(HttpServletResponse response, String fileId) {
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        String userAgent = "";
		HttpServletRequest request = RequestContextUtil.getRequest();
		if(request!=null) {
			userAgent = request.getHeader("User-Agent");
		}
        if (StringUtils.isNotEmpty(fileId)) {
            ValleyFileEntity fileInfo = valleyFlieService.findValleyFileCache(fileId);
            if (fileInfo != null && StringUtils.isNotEmpty(fileInfo.getFilePath())) {
            	try {
                    String fileName = "";
                    if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
                        fileName = URLEncoder.encode(fileInfo.getFileName(), "UTF-8");
                    } else {
                        //非IE浏览器的处理：
                        fileName = new String(fileInfo.getFileName().getBytes("UTF-8"), "ISO-8859-1");
                    }
                    response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);
                } catch (UnsupportedEncodingException e1) {
                	logger.error("下载设置失败",e1);
                }
                OutputStream os = null;
                try {
                    IFileStorageCustom fileStorageService = ValleyContextUtil.getBean("fileStorageCustom", IFileStorageCustom.class);
                    byte[] bytes = fileStorageService.downloadFile(fileInfo);
                    if (bytes != null && bytes.length > 0) {
                        os = response.getOutputStream();
                        os.write(bytes);
                    }
                } catch (Exception e) {
                    logger.info("获取文件失败");
                } finally {
                    try {
                        if (os != null) {
                            os.close();
                        }
                    } catch (Exception var) {
                        logger.info("OutputStream关闭失败");
                    }
                }
            }
        }
    }

}

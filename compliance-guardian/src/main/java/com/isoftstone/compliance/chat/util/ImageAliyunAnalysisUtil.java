package com.isoftstone.compliance.chat.util;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson2.JSONObject;
import com.aliyun.ocr_api20210707.Client;
import com.aliyun.ocr_api20210707.models.RecognizeAdvancedRequest;
import com.aliyun.ocr_api20210707.models.RecognizeAdvancedResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.ljh.yqvalley.config.util.ValleyConfigUtil;

/**
 * 图片OCR识别，使用阿里，会收费
 */
public class ImageAliyunAnalysisUtil {
	
	private static Logger logger = LoggerFactory.getLogger(ImageAliyunAnalysisUtil.class);
	
	public static final String ALIYUN_RECOGNIZE_ADVANCED_SWITCH_KEY = "aliyun_recognize_advanced_switch";
	public static final String ALIYUN_RECOGNIZE_ADVANCED_URL_KEY = "aliyun_recognize_advanced_url";
	public static final String ALIYUN_ACCESS_KEY_ID_KEY = "aliyun_access_key_id";
	public static final String ALIYUN_ACCESS_KEY_SECRET_KEY = "aliyun_access_key_secret";

	public static void main(String[] args) {
		String filePath = "D:\\docs\\20250509154206.png";
		try {
			String content = extractContent(filePath);
			logger.info(content);
		} catch (Exception e) {
			
		}

	}
	
	public static String extractContent(String filePath) {
		String content = "";
		Client client = ImageAliyunAnalysisUtil.createClient();
		if(client==null) {
			return content;
		}
		InputStream bodyStream = null;
		RecognizeAdvancedRequest recognizeAdvancedRequest = new RecognizeAdvancedRequest();
		RuntimeOptions runtime = new RuntimeOptions();
		try {
			bodyStream = new FileInputStream(filePath);
			recognizeAdvancedRequest.setBody(bodyStream);
			RecognizeAdvancedResponse response = client.recognizeAdvancedWithOptions(recognizeAdvancedRequest, runtime);
			if(response.getBody()!=null && StringUtils.isNotEmpty(response.getBody().data)) {
				content = analyzeResult(response.getBody().data);
			}
        } catch (TeaException error) {
        	content = null;
        	logger.error(error.getMessage());
        } catch (Exception _error) {
        	content = null;
            TeaException error = new TeaException(_error.getMessage(), _error);
            logger.error(error.getMessage());
        } finally {
        	if(bodyStream!=null) {
				try {
					bodyStream.close();
				} catch (Exception e2) {
					content = null;
				}
			}
		}
		return content;
	}
	
	/**
	 * 创建客户端
	 * @return
	 * @throws Exception
	 */
    public static Client createClient() {
    	String clientSwitch = ValleyConfigUtil.getAppConfigValue(ALIYUN_RECOGNIZE_ADVANCED_SWITCH_KEY);
    	String recognizeAdvancedUrl = ValleyConfigUtil.getAppConfigValue(ALIYUN_RECOGNIZE_ADVANCED_URL_KEY);
		String aliyunKey = ValleyConfigUtil.getAppConfigValue(ALIYUN_ACCESS_KEY_ID_KEY);
		String aliyunSecret = ValleyConfigUtil.getAppConfigValue(ALIYUN_ACCESS_KEY_SECRET_KEY);
    	Client client = null;
    	if("Y".equalsIgnoreCase(clientSwitch)) {
    		Config config = new Config()
    		        .setAccessKeyId(aliyunKey)
    		        .setAccessKeySecret(aliyunSecret);
    		    config.endpoint = recognizeAdvancedUrl;
    		try {
    			client = new Client(config);
    		} catch (Exception e) {
    			client = null;
    		}
    	}
        return client;
    }
    
    /**
	 * 解析返回结果
	 * @param result
	 * @return
	 */
	public static String analyzeResult(String result) {
		StringBuffer contentBuffer = new StringBuffer();
		try {
			if (StringUtils.isNotEmpty(result)) {
				JSONObject object = JSONObject.parseObject(result);
				if(object!=null) {
					contentBuffer.append(object.getString("content"));
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return contentBuffer.toString();
	}

}

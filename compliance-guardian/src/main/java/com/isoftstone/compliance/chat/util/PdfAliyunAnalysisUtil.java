package com.isoftstone.compliance.chat.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.aliyun.docmind_api20220711.Client;
import com.aliyun.docmind_api20220711.models.GetDocParserResultRequest;
import com.aliyun.docmind_api20220711.models.GetDocParserResultResponse;
import com.aliyun.docmind_api20220711.models.QueryDocParserStatusRequest;
import com.aliyun.docmind_api20220711.models.QueryDocParserStatusResponse;
import com.aliyun.docmind_api20220711.models.SubmitDocParserJobAdvanceRequest;
import com.aliyun.docmind_api20220711.models.SubmitDocParserJobResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.ljh.yqvalley.config.util.ValleyConfigUtil;

/**
 * PDF文件解析，使用阿里，会收费
 */
public class PdfAliyunAnalysisUtil {
	
	private static Logger logger = LoggerFactory.getLogger(PdfAliyunAnalysisUtil.class);
	
	public static final String ALIYUN_DOC_PARSER_SWITCH_KEY = "aliyun_doc_parser_switch";
	public static final String ALIYUN_DOC_PARSER_URL_KEY = "aliyun_doc_parser_url";
	public static final String ALIYUN_ACCESS_KEY_ID_KEY = "aliyun_access_key_id";
	public static final String ALIYUN_ACCESS_KEY_SECRET_KEY = "aliyun_access_key_secret";
	
	public static final String TEXT_TYPE_MARKDOWN = "markdownContent";
	public static final String TEXT_TYPE_TEXT = "text";
	public static final long MAX_WAIT_SECONDS = 30;
	public static final long POLL_INTERVAL = 1000;
	
	public static void main(String[] args) {
		String filePath = "D:\\docs\\111.pdf";
		try {
			String content = extractContent(filePath,TEXT_TYPE_MARKDOWN);
			logger.info(content);
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * 解析文件内容
	 * @param filePath
	 * @param textType
	 * @return
	 */
	public static String extractContent(String filePath,String textType) {
		String content = "";
		Client client = PdfAliyunAnalysisUtil.createClient();
		if(client==null) {
			return content;
		}
		String taskId = PdfAliyunAnalysisUtil.submit(client,filePath);
		if(StringUtils.isNotEmpty(taskId)) {
			LocalDateTime now = LocalDateTime.now();
			LocalDateTime nextSecond = now.plusSeconds(MAX_WAIT_SECONDS);
			boolean isBefore = now.isBefore(nextSecond);
			boolean end = false;
			while(isBefore && !end) {
				try {
					boolean complete = PdfAliyunAnalysisUtil.queryStatus(client,taskId);
					if(complete) {
						content = PdfAliyunAnalysisUtil.queryResult(client,taskId,textType);
						end = true;
						break;
					}
				} catch (Exception e) {
					Thread.currentThread().interrupt();
					break;
				}finally {
					now = LocalDateTime.now();
				}
			}
		}
		return content;
	}
	
	/**
	 * 提交异步处理请求
	 * @param filePath
	 * @return
	 */
	public static String submit(Client client,String filePath) {
		String taskId = null;
		InputStream bodyStream = null;
		try {
			if(client!=null) {
				bodyStream = new FileInputStream(filePath);
			    RuntimeOptions runtime = new RuntimeOptions();
			    SubmitDocParserJobAdvanceRequest advanceRequest = new SubmitDocParserJobAdvanceRequest();
			    advanceRequest.fileUrlObject = bodyStream;
			    advanceRequest.fileName = "example.pdf";
			    SubmitDocParserJobResponse response = client.submitDocParserJobAdvance(advanceRequest, runtime);
			    if(response.getBody()!=null && response.getBody().data!=null) {
			    	taskId = response.getBody().data.id;
			    }
			}
		} catch (Exception e) {
			taskId = null;
		} finally {
			if(bodyStream!=null) {
				try {
					bodyStream.close();
				} catch (Exception e2) {
					taskId = null;
				}
			}
		}
		return taskId;
	}
	
	/**
	 * 查询处理状态
	 * @param taskId
	 * @return
	 */
	public static boolean queryStatus(Client client,String taskId) {
		boolean complete = false;
		try {
		    QueryDocParserStatusRequest resultRequest = new QueryDocParserStatusRequest();
		    resultRequest.id = taskId;
		    QueryDocParserStatusResponse response = client.queryDocParserStatus(resultRequest);
		    if(response.getBody()!=null && response.getBody().data!=null && "success".equalsIgnoreCase(response.getBody().data.status)) {
		    	complete = true;
		    }
		} catch (Exception e) {
			complete = false;
		}
		return complete;
	}
	
	/**
	 * 获取处理结果
	 * @param taskId
	 * @return
	 */
	public static String queryResult(Client client,String taskId,String textType) {
		String content = "";
		try {
			if(client!=null) {
				GetDocParserResultRequest resultRequest = new GetDocParserResultRequest();
			    resultRequest.id = taskId;
			    resultRequest.layoutStepSize = 3000;
			    resultRequest.layoutNum = 0;
			    GetDocParserResultResponse response = client.getDocParserResult(resultRequest);
			    if(response.getBody()!=null && response.getBody().data!=null) {
			    	content = PdfAliyunAnalysisUtil.analyzeResult(response.getBody().data.get("layouts"),textType);
			    }
			}
		} catch (Exception e) {
			content = null;
		}
		return content;
	}
	
	/**
	 * 创建客户端
	 * @return
	 */
	public static Client createClient() {
		String clientSwitch = ValleyConfigUtil.getAppConfigValue(ALIYUN_DOC_PARSER_SWITCH_KEY);
		String parserUrl = ValleyConfigUtil.getAppConfigValue(ALIYUN_DOC_PARSER_URL_KEY);
		String aliyunKey = ValleyConfigUtil.getAppConfigValue(ALIYUN_ACCESS_KEY_ID_KEY);
		String aliyunSecret = ValleyConfigUtil.getAppConfigValue(ALIYUN_ACCESS_KEY_SECRET_KEY);
		Client client = null;
		if("Y".equalsIgnoreCase(clientSwitch)) {
			Config config = new Config()
			        .setAccessKeyId(aliyunKey)
			        .setAccessKeySecret(aliyunSecret);
			    config.endpoint = parserUrl;
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
	public static String analyzeResult(Object resultObj,String textType) {
		StringBuffer contentBuffer = new StringBuffer();
		try {
			if (resultObj!=null) {
				JSONArray resultArray = (JSONArray) JSON.toJSON(resultObj);
				if(resultArray!=null) {
	                for(int i = 0; i < resultArray.size(); i++){
	                	JSONObject rowObject = resultArray.getJSONObject(i);
	                	if(StringUtils.isNotEmpty(rowObject.getString(textType))) {
	                		contentBuffer.append(rowObject.getString(textType));
	                	}
	                }
	            }
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return contentBuffer.toString();
	}

}

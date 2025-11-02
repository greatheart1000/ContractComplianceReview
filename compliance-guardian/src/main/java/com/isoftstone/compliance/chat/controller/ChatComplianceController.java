package com.isoftstone.compliance.chat.controller;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.isoftstone.compliance.chat.model.ChatComplianceEntity;
import com.isoftstone.compliance.chat.model.ChatRecordEntity;
import com.isoftstone.compliance.chat.model.ChatRequestEntity;
import com.isoftstone.compliance.chat.service.IChatComplianceService;
import com.isoftstone.compliance.chat.service.IFileContentExtractService;
import com.isoftstone.compliance.chat.util.CacheSseUtil;
import com.isoftstone.compliance.common.annotation.ModelLoginVerify;
import com.isoftstone.compliance.common.constant.ComplianceQConstant;
import com.isoftstone.compliance.common.util.ModelRequestContextUtil;
import com.ljh.yqvalley.asyn4j.service.IAsyn4jQueueTask;
import com.ljh.yqvalley.common.util.RedisHelpUtil;
import com.ljh.yqvalley.core.exception.ValleyException;
import com.ljh.yqvalley.core.model.PageEntity;
import com.ljh.yqvalley.core.model.PageResult;
import com.ljh.yqvalley.core.model.ValleyResult;

import jakarta.annotation.Resource;

@RestController
@RequestMapping(ComplianceQConstant.MODEL_CHAT)
public class ChatComplianceController {
	
	private static Logger logger = LoggerFactory.getLogger(ChatComplianceController.class);
	
	@Resource
	private IAsyn4jQueueTask asyn4jQueueTask;
	@Resource
	private IChatComplianceService chatComplianceService;
	@Resource
	private IFileContentExtractService fileContentExtractService;
	
	/**
	 * 查询单条用户信息
	 * @param user
	 * @return
	 */
	@ModelLoginVerify
	@RequestMapping(value = "/find",method=RequestMethod.POST)
	public ChatComplianceEntity findChatSession(@RequestBody ChatComplianceEntity chatOpen) {
		if(StringUtils.isEmpty(chatOpen.getChatId())) {
			throw new ValleyException("会话ID不能为空");
		}
		String userAccount = ModelRequestContextUtil.getCurrentAccount();
		chatOpen.setCreateAccount(userAccount);
		return chatComplianceService.findChatSession(chatOpen);
	}
	
	/**
	 * 分页查询会话列表
	 * @param chatOpen
	 * @param page
	 * @return
	 */
	@ModelLoginVerify
	@RequestMapping(value = "/page",method=RequestMethod.POST)
	public PageResult<ChatComplianceEntity> findChatSessionByPage(@RequestBody ChatComplianceEntity chatOpen,PageEntity page) {
		String userAccount = ModelRequestContextUtil.getCurrentAccount();
		chatOpen.setCreateAccount(userAccount);
		return chatComplianceService.findChatSessionByPage(chatOpen, page);
	}
	
	/**
	 * 分页查询会话记录
	 * @param chatOpen
	 * @param page
	 * @return
	 */
	@ModelLoginVerify
	@RequestMapping(value = "/record/page",method=RequestMethod.POST)
	public PageResult<ChatRecordEntity> findChatProblemByPage(@RequestBody ChatRecordEntity record,PageEntity page) {
		if(StringUtils.isEmpty(record.getChatId())) {
			throw new ValleyException("会话ID不能为空");
		}
		String userAccount = ModelRequestContextUtil.getCurrentAccount();
		record.setCreateAccount(userAccount);
		return chatComplianceService.findChatProblemByPage(record, page);
	}
	
	/**
	 * 分页查询会话记录
	 * @param chatOpen
	 * @param page
	 * @return
	 */
	@ModelLoginVerify
	@RequestMapping(value = "/record/list",method=RequestMethod.POST)
	public List<ChatRecordEntity> findChatRecordList(@RequestBody ChatRecordEntity record) {
		if(StringUtils.isEmpty(record.getChatId())) {
			throw new ValleyException("会话ID不能为空");
		}
		String userAccount = ModelRequestContextUtil.getCurrentAccount();
		record.setCreateAccount(userAccount);
		return chatComplianceService.findChatRecordList(record);
	}
	
	/**
	 * 获取SSE连接
	 * @return
	 */
	@ModelLoginVerify
	@CrossOrigin
	@GetMapping(path = "/sse/connect",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@RequestParam(required = false) String chatId) {
		//设置超时时间,0表示不过期,默认30秒
        SseEmitter sse = new SseEmitter(0L);
        String sseId = RedisHelpUtil.getIncrbyOnlyId("sse_chat_connect");
        try {
            //退出sse回调
            sse.onCompletion(() -> {
            	CacheSseUtil.remove(sseId);
            });
            sse.send(SseEmitter.event().id(sseId).data(sseId).reconnectTime(5000));
            //存缓存
            CacheSseUtil.add(sseId, sse);
        } catch (Exception e) {
            logger.error("sse连接失败");
        }
        return sse;
    }
	
	/**
	 * 进行对话
	 * @param param
	 * @return
	 */
	@ModelLoginVerify
    @PostMapping("/sse/send")
    public ValleyResult<ChatComplianceEntity> send(@RequestBody ChatRequestEntity param) {
		if(StringUtils.isEmpty(param.getSessionId())) {
			return ValleyResult.newFailure("Session Id不能为空");
		}
		if(CollectionUtils.isEmpty(param.getFileList())) {
			return ValleyResult.newFailure("文件不能为空");
		}
		ChatComplianceEntity chatCompliance = new ChatComplianceEntity();
		try {
			String userAccount = ModelRequestContextUtil.getCurrentAccount();
			chatCompliance.setChatId(param.getChatId());
			chatCompliance.setCreateAccount(userAccount);
			String content = fileContentExtractService.findFileContent(param.getFileList());
			if(StringUtils.isNotEmpty(content)) {
				if(StringUtils.isNotEmpty(chatCompliance.getChatId())) {
					chatCompliance = chatComplianceService.findChatSession(chatCompliance);
				} else {
					chatCompliance.setChatTitle(param.getFileList().get(0).getFileName());
					chatCompliance.setComplianceType(param.getComplianceType());
					chatCompliance.setContractType(param.getContractType());
					chatCompliance.setModelId(param.getModelId());
					chatCompliance.setCreateAccount(userAccount);
					chatCompliance = chatComplianceService.insertChatSession(chatCompliance);
				}
				param.setCreateAccount(userAccount);
				param.setContent(content);
				param.setModelId(chatCompliance.getModelId());
				param.setChatId(chatCompliance.getChatId());
				param.setComplianceType(chatCompliance.getComplianceType());
				param.setContractType(chatCompliance.getContractType());
				SseEmitter emitter = CacheSseUtil.get(param.getSessionId());
				if(emitter!=null) {
					Map<String,Serializable> map = new HashMap<String,Serializable>();
					map.put("param", param);
					asyn4jQueueTask.send("chatComplianceProcessor", map);
				}else {
					return ValleyResult.newFailure("找不到连接");
				}
			} else {
				return ValleyResult.newFailure("文件解析失败");
			}
		} catch (Exception e) {
			return ValleyResult.newFailure("异常："+e.getMessage());
		}
        return ValleyResult.newSuccess(chatCompliance);
    }
	
	/**
	 * 删除指定会话
	 * @param chatOpen
	 * @return
	 */
    @ModelLoginVerify
	@RequestMapping(value = "/delete",method=RequestMethod.POST)
	public ValleyResult<Integer> deleteChatSession(@RequestBody ChatComplianceEntity chatOpen) {
		if(StringUtils.isEmpty(chatOpen.getChatId())) {
			throw new ValleyException("会话ID不能为空");
		}
		String userAccount = ModelRequestContextUtil.getCurrentAccount();
		chatOpen.setCreateAccount(userAccount);
		chatComplianceService.deleteChatSession(chatOpen);
		return ValleyResult.newSuccess();
	}
	
	/**
	 * 清理用户会话
	 * @param chatOpen
	 * @return
	 */
    @ModelLoginVerify
	@RequestMapping(value = "/clear",method=RequestMethod.POST)
	public ValleyResult<Integer> clearChatSession(@RequestBody ChatComplianceEntity chatOpen) {
    	String userAccount = ModelRequestContextUtil.getCurrentAccount();
		chatOpen.setCreateAccount(userAccount);
    	chatComplianceService.deleteChatSession(chatOpen);
		return ValleyResult.newSuccess();
	}

}

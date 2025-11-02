package com.isoftstone.compliance.config.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.isoftstone.compliance.common.constant.ComplianceAConstant;
import com.isoftstone.compliance.common.constant.ComplianceQConstant;
import com.isoftstone.compliance.config.model.ModelConfigEntity;
import com.isoftstone.compliance.config.service.IModelConfigService;
import com.ljh.yqvalley.core.annotation.AuthorityClass;
import com.ljh.yqvalley.core.annotation.AuthorityMethod;
import com.ljh.yqvalley.core.annotation.ValleyAppLog;
import com.ljh.yqvalley.core.exception.ValleyException;
import com.ljh.yqvalley.core.model.PageEntity;
import com.ljh.yqvalley.core.model.PageResult;
import com.ljh.yqvalley.user.util.ValleyUserUtil;

import jakarta.annotation.Resource;

/**
 * 模型配置入口
 * @author liujinhui
 */
@AuthorityClass(module=ComplianceAConstant.MODULE,code=ComplianceAConstant.ModelConfigP.CLASS_CODE,name=ComplianceAConstant.ModelConfigP.CLASS_NAME)
@RestController
@RequestMapping(ComplianceQConstant.MODEL_CONFIG)
public class ModelConfigController {
	
	@Resource
	private IModelConfigService modelConfigService;
	
	public static final String SENSITIVE_VALUE = "******";

	/**
	 * 查询模型配置详情
	 * @param config
	 * @return
	 */
	@AuthorityMethod(code=ComplianceAConstant.ModelConfigP.READ_LIST_CODE,name=ComplianceAConstant.ModelConfigP.READ_LIST_NAME)
	@RequestMapping(value = "/find",method=RequestMethod.POST)
	public ModelConfigEntity findModelConfigInfo(@RequestBody ModelConfigEntity config) {
		if(StringUtils.isEmpty(config.getModelId())) {
			throw new ValleyException("模型ID不能为空");
		}
		ModelConfigEntity conf = modelConfigService.findModelConfigInfo(config.getModelId());
		if(conf!=null) {
			conf.setModelKey(SENSITIVE_VALUE);
		}
		return conf;
	}
	
	/**
	 * 分页查询模型配置
	 * @param config
	 * @param page
	 * @return
	 */
	@AuthorityMethod(code=ComplianceAConstant.ModelConfigP.READ_LIST_CODE,name=ComplianceAConstant.ModelConfigP.READ_LIST_NAME)
	@RequestMapping(value = "/page",method=RequestMethod.POST)
	public PageResult<ModelConfigEntity> findModelConfigByPage(@RequestBody ModelConfigEntity config,PageEntity page) {
		PageResult<ModelConfigEntity> result = modelConfigService.findModelConfigByPage(config,page);
		if(result!=null && CollectionUtils.isNotEmpty(result.getResult())) {
			Map<String,String> userNameMap = ValleyUserUtil.getUserNameMap(true);
			for (ModelConfigEntity conf : result.getResult()) {
				if(StringUtils.isNotEmpty(conf.getLastUpdateAccount())) {
					conf.setLastUpdateName(ValleyUserUtil.getUserName(userNameMap,conf.getLastUpdateAccount()));
				}
			}
		}
		return result;
	}
	
	/**
	 * 修改验证
	 * @param config
	 */
	private void verifyUpdate(ModelConfigEntity config) {
		if(StringUtils.isEmpty(config.getModelCode())) {
			throw new ValleyException("模型编号不能为空");
		}
		if(StringUtils.isEmpty(config.getModelName())) {
			throw new ValleyException("模型名称不能为空");
		}
		if(StringUtils.isEmpty(config.getModelUrl())) {
			throw new ValleyException("模型地址不能为空");
		}
		if(StringUtils.isEmpty(config.getModelKey())) {
			throw new ValleyException("模型秘钥不能为空");
		}
	}
    
	/**
	 * 新增模型配置
	 * @param config
	 * @return
	 */
	@AuthorityMethod(code=ComplianceAConstant.ModelConfigP.UPDATE_CODE,name=ComplianceAConstant.ModelConfigP.UPDATE_NAME,show = true)
	@ValleyAppLog(name = "模型配置-新增")
	@RequestMapping(value = "/insert",method=RequestMethod.POST)
	public int insertModelConfig(@RequestBody ModelConfigEntity config) {
		verifyUpdate(config);
		config.setModelKey(config.getModelKey().trim());
		int count = modelConfigService.insertModelConfig(config);
		config.setModelKey(SENSITIVE_VALUE);
		return count;
		
	}
	
	/**
     * 修改模型配置
     * @param config
     * @return
     */
	@AuthorityMethod(code=ComplianceAConstant.ModelConfigP.UPDATE_CODE,name=ComplianceAConstant.ModelConfigP.UPDATE_NAME,show = true)
	@ValleyAppLog(name = "模型配置-修改")
	@RequestMapping(value = "/update",method=RequestMethod.POST)
	public int updateModelConfig(@RequestBody ModelConfigEntity config) {
		if(StringUtils.isEmpty(config.getModelId())) {
			throw new ValleyException("模型ID不能为空");
		}
		verifyUpdate(config);
		ModelConfigEntity conf = modelConfigService.findModelConfigInfo(config.getModelId());
		if(SENSITIVE_VALUE.equals(config.getModelKey())) {
			config.setModelKey(conf.getModelKey());
		}
		config.setModelKey(config.getModelKey().trim());
		int count = modelConfigService.updateModelConfig(config);
		config.setModelKey(SENSITIVE_VALUE);
		return count;
	}
	
	/**
     * 修改模型配置状态
     * @param config
     * @return
     */
	@AuthorityMethod(code=ComplianceAConstant.ModelConfigP.UPDATE_CODE,name=ComplianceAConstant.ModelConfigP.UPDATE_NAME,show = true)
	@ValleyAppLog(name = "模型配置-状态修改")
	@RequestMapping(value = "/update/status",method=RequestMethod.POST)
	public int updateOpenAiConfigStatus(@RequestBody ModelConfigEntity config) {
		int count = 0;
		if(CollectionUtils.isEmpty(config.getModelIdList())) {
			throw new ValleyException("模型ID不能为空");
		}
		if(StringUtils.isEmpty(config.getModelStatus())) {
			throw new ValleyException("模型状态不能为空");
		}
		config.setModelStatus(config.getModelStatus().toUpperCase());
		if(!("Y".equals(config.getModelStatus()) || "N".equals(config.getModelStatus()))) {
			throw new ValleyException("状态值错误");
		}
		for (String modelId : config.getModelIdList()) {
			config.setModelId(modelId);
			count += modelConfigService.updateModelConfigStatus(config);
		}
		return count;
	}

	/**
	 * 删除模型配置
	 * @param config
	 * @config
	 */
	@AuthorityMethod(code=ComplianceAConstant.ModelConfigP.UPDATE_CODE,name=ComplianceAConstant.ModelConfigP.UPDATE_NAME,show = true)
	@ValleyAppLog(name = "模型配置-删除")
	@RequestMapping(value = "/delete",method=RequestMethod.POST)
	public int deleteModelConfig(@RequestBody ModelConfigEntity config) {
		if(StringUtils.isEmpty(config.getModelId())) {
			throw new ValleyException("模型ID不能为空");
		}
		return modelConfigService.deleteModelConfig(config.getModelId());
	}
	
	/**
	 * 查询模型配置列表
	 * @return
	 */
	@AuthorityMethod(code=ComplianceAConstant.ModelConfigP.READ_DOWN_CODE,name=ComplianceAConstant.ModelConfigP.READ_DOWN_NAME)
	@RequestMapping(value = "/list",method=RequestMethod.GET)
	public List<ModelConfigEntity> findModelConfigList() {
		ModelConfigEntity config = new ModelConfigEntity();
		config.setModelStatus("Y");
		return modelConfigService.findModelConfigList(config);
	}

}

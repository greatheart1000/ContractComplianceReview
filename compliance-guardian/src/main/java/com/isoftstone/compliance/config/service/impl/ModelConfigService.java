package com.isoftstone.compliance.config.service.impl;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.isoftstone.compliance.common.constant.ComplianceCommonConstant;
import com.isoftstone.compliance.config.dao.IModelConfigMapper;
import com.isoftstone.compliance.config.model.ModelConfigEntity;
import com.isoftstone.compliance.config.service.IModelConfigService;
import com.ljh.yqvalley.common.util.DataEncryptUtil;
import com.ljh.yqvalley.common.util.DateHelpUtil;
import com.ljh.yqvalley.common.util.RedisHelpUtil;
import com.ljh.yqvalley.config.util.ValleyConfigUtil;
import com.ljh.yqvalley.core.model.PageEntity;
import com.ljh.yqvalley.core.model.PageResult;
import com.ljh.yqvalley.user.util.RequestContextUtil;

import jakarta.annotation.Resource;

/**
 * 模型配置服务实现
 */
@Service
public class ModelConfigService implements IModelConfigService {
	
	@Resource
	private IModelConfigMapper modelConfigMapper;

	@Override
	public ModelConfigEntity findModelConfigInfo(String modelId) {
		ModelConfigEntity config = modelConfigMapper.findModelConfigInfo(modelId);
		if(config!=null && StringUtils.isNotEmpty(config.getModelKey())) {
			config.setModelKey(DataEncryptUtil.decryptString(config.getModelKey()));
		}
		return config;
	}

	@Override
	public List<ModelConfigEntity> findModelConfigList(ModelConfigEntity config) {
		return modelConfigMapper.findModelConfigList(config);
	}

	@Override
	public PageResult<ModelConfigEntity> findModelConfigByPage(ModelConfigEntity config, PageEntity page) {
		return modelConfigMapper.findModelConfigByPage(config, page);
	}

	@Override
	public int insertModelConfig(ModelConfigEntity config) {
		int count = 0;
		String valleyAccount = RequestContextUtil.getCurrentAccount();
		config.setCreateAccount(valleyAccount);
		//String openaiId = RedisHelpUtil.getIncrbyOnlyId("yqvai_openai_id");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
		String modelId = "AI"+dateFormat.format(DateHelpUtil.getDayBegin())+ValleyConfigUtil.getNumberCode("model_openai_code", "", 4);
		config.setModelId(modelId);
		config.setModelStatus("Y");
		config.setModelKey(DataEncryptUtil.encryptString(config.getModelKey()));
		count = modelConfigMapper.insertModelConfig(config);
		RedisHelpUtil.deleteValue(ComplianceCommonConstant.MODEL_CONFIG_LIST);
		return count;
	}

	@Override
	public int updateModelConfig(ModelConfigEntity config) {
		int count = 0;
		String valleyAccount = RequestContextUtil.getCurrentAccount();
		config.setCreateAccount(valleyAccount);
		config.setModelKey(DataEncryptUtil.encryptString(config.getModelKey()));
		count = modelConfigMapper.updateModelConfig(config);
		RedisHelpUtil.deleteValue(ComplianceCommonConstant.MODEL_CONFIG_INFO+config.getModelId());
		RedisHelpUtil.deleteValue(ComplianceCommonConstant.MODEL_CONFIG_LIST);
		return count;
	}
	
	@Override
	public int updateModelConfigStatus(ModelConfigEntity config) {
		int count = 0;
		String valleyAccount = RequestContextUtil.getCurrentAccount();
		config.setCreateAccount(valleyAccount);
		count = modelConfigMapper.updateModelConfigStatus(config);
		RedisHelpUtil.deleteValue(ComplianceCommonConstant.MODEL_CONFIG_INFO+config.getModelId());
		RedisHelpUtil.deleteValue(ComplianceCommonConstant.MODEL_CONFIG_LIST);
		return count;
	}

	@Override
	public int deleteModelConfig(String modelId) {
		int count = modelConfigMapper.deleteModelConfig(modelId);
		RedisHelpUtil.deleteValue(ComplianceCommonConstant.MODEL_CONFIG_INFO+modelId);
		RedisHelpUtil.deleteValue(ComplianceCommonConstant.MODEL_CONFIG_LIST);
		return count;
	}

}

package com.isoftstone.compliance.common.util;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.isoftstone.compliance.common.constant.ComplianceCommonConstant;
import com.isoftstone.compliance.config.model.ModelConfigEntity;
import com.isoftstone.compliance.config.model.ModelUserEntity;
import com.isoftstone.compliance.config.service.IModelConfigService;
import com.ljh.yqvalley.common.util.RedisHelpUtil;
import com.ljh.yqvalley.common.util.ValleyContextUtil;

public class ComplianceServiceUtil {
	
	/**
     * 获取模型配置
     * @param openaiCode
     * @return
     */
    public static ModelConfigEntity getModelConfig(String modelId) {
    	ModelConfigEntity config = (ModelConfigEntity)RedisHelpUtil.getValue(ComplianceCommonConstant.MODEL_CONFIG_INFO+modelId);
    	if(config==null) {
    		IModelConfigService modelConfigService = ValleyContextUtil.getBean("modelConfigService",IModelConfigService.class);
    		config = modelConfigService.findModelConfigInfo(modelId);
    		if(config!=null) {
    			RedisHelpUtil.setValue(ComplianceCommonConstant.MODEL_CONFIG_INFO+modelId, config, RedisHelpUtil.second(60*24));
    		}
    	}
        return config;
    }
    
    /**
     * 获取模型配置
     * @param openaiCode
     * @return
     */
    @SuppressWarnings("unchecked")
	public static List<ModelConfigEntity> getModelConfigList() {
    	List<ModelConfigEntity> configList = (List<ModelConfigEntity>)RedisHelpUtil.getValue(ComplianceCommonConstant.MODEL_CONFIG_LIST);
    	if(CollectionUtils.isEmpty(configList)) {
    		ModelConfigEntity configInfo = new ModelConfigEntity();
    		configInfo.setModelStatus("Y");
    		IModelConfigService modelConfigService = ValleyContextUtil.getBean("modelConfigService",IModelConfigService.class);
    		configList = modelConfigService.findModelConfigList(configInfo);
    		if(CollectionUtils.isNotEmpty(configList)) {
    			RedisHelpUtil.setValue(ComplianceCommonConstant.MODEL_CONFIG_LIST, configList, RedisHelpUtil.second(60*24));
    		}
    	}
        return configList;
    }
    
    /**
	 * 获取完整部门
	 * @param valleyUser
	 * @return
	 */
	public static String getCompleteName(ModelUserEntity valleyUser) {
		String completeName = "";
		if(valleyUser!=null) {
			StringBuilder sb = new StringBuilder();
			if(StringUtils.isNotEmpty(valleyUser.getDeptName())) {
				sb.append("【");
				sb.append(valleyUser.getDeptName());
				sb.append("】");
			}
			if(StringUtils.isNotEmpty(valleyUser.getLevel1DeptName())) {
				sb.append(valleyUser.getLevel1DeptName());
			}
			if(StringUtils.isNotEmpty(valleyUser.getLevel2DeptName())) {
				if(StringUtils.isNotEmpty(valleyUser.getLevel1DeptName())) {
					sb.append("/");
				}
				sb.append(valleyUser.getLevel2DeptName());
			}
			if(StringUtils.isNotEmpty(valleyUser.getLevel3DeptName())) {
				if(StringUtils.isNotEmpty(valleyUser.getLevel1DeptName())) {
					sb.append("/");
				}
				sb.append(valleyUser.getLevel3DeptName());
			}
			if(StringUtils.isNotEmpty(valleyUser.getLevel4DeptName())) {
				if(StringUtils.isNotEmpty(valleyUser.getLevel1DeptName())) {
					sb.append("/");
				}
				sb.append(valleyUser.getLevel4DeptName());
			}
			if(StringUtils.isNotEmpty(valleyUser.getLevel5DeptName())) {
				if(StringUtils.isNotEmpty(valleyUser.getLevel1DeptName())) {
					sb.append("/");
				}
				sb.append(valleyUser.getLevel5DeptName());
			}
			completeName = sb.toString();
		}
		return completeName;
	}

}

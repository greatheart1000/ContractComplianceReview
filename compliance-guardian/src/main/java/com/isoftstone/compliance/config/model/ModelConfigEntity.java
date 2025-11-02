package com.isoftstone.compliance.config.model;

import java.util.List;

import com.ljh.yqvalley.core.model.ValleyEntity;

/**
 * 大模型配置信息
 */
public class ModelConfigEntity extends ValleyEntity {
	
	private static final long serialVersionUID = 8986320498642114632L;
	
	/**
     * 模型Id
     */
    private String modelId;
	/**
     * 模型编号
     */
    private String modelCode;
    /**
     * 模型名称
     */
    private String modelName;
    /**
     * 模型秘钥
     */
    private String modelKey;
    /**
     * 模型地址
     */
    private String modelUrl;
    /**
     * 模型状态(生效:Y,失效:N)
     */
    private String modelStatus;
    /**
     * 模型排序
     */
    private Integer modelSort;
    /**
     * 模型编号集合
     */
    private List<String> modelIdList;
    
	public String getModelId() {
		return modelId;
	}
	public void setModelId(String modelId) {
		this.modelId = modelId;
	}
	public String getModelCode() {
		return modelCode;
	}
	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getModelKey() {
		return modelKey;
	}
	public void setModelKey(String modelKey) {
		this.modelKey = modelKey;
	}
	public String getModelUrl() {
		return modelUrl;
	}
	public void setModelUrl(String modelUrl) {
		this.modelUrl = modelUrl;
	}
	public String getModelStatus() {
		return modelStatus;
	}
	public void setModelStatus(String modelStatus) {
		this.modelStatus = modelStatus;
	}
	public Integer getModelSort() {
		return modelSort;
	}
	public void setModelSort(Integer modelSort) {
		this.modelSort = modelSort;
	}
	public List<String> getModelIdList() {
		return modelIdList;
	}
	public void setModelIdList(List<String> modelIdList) {
		this.modelIdList = modelIdList;
	}
    
}

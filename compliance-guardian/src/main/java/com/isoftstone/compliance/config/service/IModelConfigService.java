package com.isoftstone.compliance.config.service;

import java.util.List;

import com.isoftstone.compliance.config.model.ModelConfigEntity;
import com.ljh.yqvalley.core.model.PageEntity;
import com.ljh.yqvalley.core.model.PageResult;

/**
 * 模型配置服务接口
 */
public interface IModelConfigService {
	
	/**
	 * 查询模型配置详情
	 * @param modelId
	 * @return
	 */
	public ModelConfigEntity findModelConfigInfo(String modelId);
	
	/**
	 * 按条件查询模型配置
	 * @param config
	 * @return
	 */
	public List<ModelConfigEntity> findModelConfigList(ModelConfigEntity config);
	
	/**
	 * 分页查询模型配置
	 * @param config
	 * @param page
	 * @return
	 */
	public PageResult<ModelConfigEntity> findModelConfigByPage(ModelConfigEntity config,PageEntity page);
	
	/**
	 * 新增模型配置
	 * @param config
	 * @return
	 */
	public int insertModelConfig(ModelConfigEntity config);
    
    /**
     * 修改模型配置
     * @param config
     * @return
     */
    public int updateModelConfig(ModelConfigEntity config);
    
    /**
     * 修改OpenAi配置状态
     * @param config
     * @return
     */
    public int updateModelConfigStatus(ModelConfigEntity config);
    
    /**
     * 删除模型配置
     * @param modelId
     * @return
     */
    public int deleteModelConfig(String modelId);

}

package com.isoftstone.compliance.config.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.isoftstone.compliance.config.model.ModelConfigEntity;
import com.ljh.yqvalley.core.model.PageEntity;
import com.ljh.yqvalley.core.model.PageResult;

/**
 * 模型配置数据接口
 */
public interface IModelConfigMapper {
	
	/**
	 * 查询模型配置详情
	 * @param modelId
	 * @return
	 */
	public ModelConfigEntity findModelConfigInfo(@Param("modelId") String modelId);
	
	/**
	 * 按条件查询模型配置
	 * @param config
	 * @return
	 */
	public List<ModelConfigEntity> findModelConfigList(@Param("ent") ModelConfigEntity config);
	
	/**
	 * 分页查询模型配置
	 * @param config
	 * @param page
	 * @return
	 */
	public PageResult<ModelConfigEntity> findModelConfigByPage(@Param("ent") ModelConfigEntity config,@Param("page") PageEntity page);
	
	/**
	 * 新增模型配置
	 * @param config
	 * @return
	 */
	public int insertModelConfig(@Param("ent") ModelConfigEntity config);
    
    /**
     * 修改模型配置
     * @param config
     * @return
     */
    public int updateModelConfig(@Param("ent") ModelConfigEntity config);
    
    /**
     * 修改模型配置状态
     * @param config
     * @return
     */
    public int updateModelConfigStatus(@Param("ent") ModelConfigEntity config);
    
    /**
     * 删除模型配置
     * @param modelId
     * @return
     */
    public int deleteModelConfig(@Param("modelId") String modelId);
    
}

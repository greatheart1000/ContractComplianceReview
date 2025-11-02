package com.isoftstone.compliance.contract.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.isoftstone.compliance.chat.model.ChatCheckRuleEntity;
import com.isoftstone.compliance.contract.model.ContractStandardEntity;
import com.ljh.yqvalley.core.model.PageEntity;
import com.ljh.yqvalley.core.model.PageResult;

/**
 * 标准规则数据接口
 */
public interface IContractStandardMapper {
	
	/**
	 * 查询标准规则详情
	 * @param ruleId
	 * @return
	 */
	public ContractStandardEntity findContractStandardInfo(@Param("ruleId") String ruleId);
	
	/**
	 * 按条件查询标准规则
	 * @param rule
	 * @return
	 */
	public List<ChatCheckRuleEntity> findContractStandardList(@Param("ent") ContractStandardEntity rule);
	
	/**
	 * 分页查询标准规则
	 * @param rule
	 * @param page
	 * @return
	 */
	public PageResult<ContractStandardEntity> findContractStandardByPage(@Param("ent") ContractStandardEntity rule,@Param("page") PageEntity page);
	
	/**
	 * 新增标准规则
	 * @param rule
	 * @return
	 */
	public int insertContractStandard(@Param("ent") ContractStandardEntity rule);
    
    /**
     * 修改标准规则
     * @param rule
     * @return
     */
    public int updateContractStandard(@Param("ent") ContractStandardEntity rule);
    
    /**
     * 修改标准规则状态
     * @param rule
     * @return
     */
    public int updateContractStandardStatus(@Param("ent") ContractStandardEntity rule);
    
    /**
     * 删除标准规则
     * @param ruleId
     * @return
     */
    public int deleteContractStandard(@Param("ruleId") String ruleId);
    
}

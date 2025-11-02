package com.isoftstone.compliance.contract.service;

import java.util.List;

import com.isoftstone.compliance.chat.model.ChatCheckRuleEntity;
import com.isoftstone.compliance.contract.model.ContractStandardEntity;
import com.ljh.yqvalley.core.model.PageEntity;
import com.ljh.yqvalley.core.model.PageResult;

/**
 * 标准规则服务接口
 */
public interface IContractStandardService {
	
	/**
	 * 查询标准规则详情
	 * @param ruleId
	 * @return
	 */
	public ContractStandardEntity findContractStandardInfo(String ruleId);
	
	/**
	 * 按条件查询标准规则
	 * @param rule
	 * @return
	 */
	public List<ChatCheckRuleEntity> findContractStandardList(ContractStandardEntity rule);
	
	/**
	 * 分页查询标准规则
	 * @param rule
	 * @param page
	 * @return
	 */
	public PageResult<ContractStandardEntity> findContractStandardByPage(ContractStandardEntity rule,PageEntity page);
	
	/**
	 * 新增标准规则
	 * @param rule
	 * @return
	 */
	public int insertContractStandard(ContractStandardEntity rule);
    
    /**
     * 修改标准规则
     * @param rule
     * @return
     */
    public int updateContractStandard(ContractStandardEntity rule);
    
    /**
     * 修改标准规则状态
     * @param rule
     * @return
     */
    public int updateContractStandardStatus(ContractStandardEntity rule);
    
    /**
     * 删除标准规则
     * @param ruleId
     * @return
     */
    public int deleteContractStandard(String ruleId);

}

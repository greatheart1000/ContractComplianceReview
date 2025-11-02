package com.isoftstone.compliance.contract.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.isoftstone.compliance.chat.model.ChatCheckRuleEntity;
import com.isoftstone.compliance.contract.dao.IContractStandardMapper;
import com.isoftstone.compliance.contract.model.ContractStandardEntity;
import com.isoftstone.compliance.contract.service.IContractStandardService;
import com.ljh.yqvalley.common.util.RedisHelpUtil;
import com.ljh.yqvalley.core.model.PageEntity;
import com.ljh.yqvalley.core.model.PageResult;
import com.ljh.yqvalley.user.util.RequestContextUtil;

import jakarta.annotation.Resource;

/**
 * 标准规则服务实现
 */
@Service
public class ContractStandardService implements IContractStandardService {
	
	@Resource
	private IContractStandardMapper contractStandardMapper;

	@Override
	public ContractStandardEntity findContractStandardInfo(String ruleId) {
		return contractStandardMapper.findContractStandardInfo(ruleId);
	}

	@Override
	public List<ChatCheckRuleEntity> findContractStandardList(ContractStandardEntity rule) {
		return contractStandardMapper.findContractStandardList(rule);
	}

	@Override
	public PageResult<ContractStandardEntity> findContractStandardByPage(ContractStandardEntity rule, PageEntity page) {
		return contractStandardMapper.findContractStandardByPage(rule, page);
	}

	@Override
	public int insertContractStandard(ContractStandardEntity rule) {
		String valleyAccount = RequestContextUtil.getCurrentAccount();
		rule.setCreateAccount(valleyAccount);
		String ruleId = RedisHelpUtil.getIncrbyOnlyId("cg_contract_standard_id");
		rule.setRuleId(ruleId);
		rule.setRuleStatus("Y");
		return contractStandardMapper.insertContractStandard(rule);
	}

	@Override
	public int updateContractStandard(ContractStandardEntity rule) {
		String valleyAccount = RequestContextUtil.getCurrentAccount();
		rule.setCreateAccount(valleyAccount);
		return contractStandardMapper.updateContractStandard(rule);
	}
	
	@Override
	public int updateContractStandardStatus(ContractStandardEntity rule) {
		String valleyAccount = RequestContextUtil.getCurrentAccount();
		rule.setCreateAccount(valleyAccount);
		return contractStandardMapper.updateContractStandardStatus(rule);
	}

	@Override
	public int deleteContractStandard(String ruleId) {
		int count = contractStandardMapper.deleteContractStandard(ruleId);
		return count;
	}

}

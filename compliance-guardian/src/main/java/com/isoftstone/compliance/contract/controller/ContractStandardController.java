package com.isoftstone.compliance.contract.controller;

import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.isoftstone.compliance.common.constant.ComplianceAConstant;
import com.isoftstone.compliance.common.constant.ComplianceCommonConstant;
import com.isoftstone.compliance.common.constant.ComplianceQConstant;
import com.isoftstone.compliance.contract.model.ContractStandardEntity;
import com.isoftstone.compliance.contract.service.IContractStandardService;
import com.ljh.yqvalley.config.util.ValleyConfigUtil;
import com.ljh.yqvalley.core.annotation.AuthorityClass;
import com.ljh.yqvalley.core.annotation.AuthorityMethod;
import com.ljh.yqvalley.core.annotation.ValleyAppLog;
import com.ljh.yqvalley.core.exception.ValleyException;
import com.ljh.yqvalley.core.model.PageEntity;
import com.ljh.yqvalley.core.model.PageResult;
import com.ljh.yqvalley.user.util.ValleyUserUtil;

import jakarta.annotation.Resource;

/**
 * 标准规则入口
 * @author liujinhui
 */
@AuthorityClass(module=ComplianceAConstant.MODULE,code=ComplianceAConstant.ContractStandardP.CLASS_CODE,name=ComplianceAConstant.ContractStandardP.CLASS_NAME)
@RestController
@RequestMapping(ComplianceQConstant.CONTRACT_STANDARD)
public class ContractStandardController {
	
	@Resource
	private IContractStandardService contractStandardService;

	/**
	 * 查询标准规则详情
	 * @param rule
	 * @return
	 */
	@AuthorityMethod(code=ComplianceAConstant.ContractStandardP.READ_LIST_CODE,name=ComplianceAConstant.ContractStandardP.READ_LIST_NAME)
	@RequestMapping(value = "/find",method=RequestMethod.POST)
	public ContractStandardEntity findContractStandardInfo(@RequestBody ContractStandardEntity rule) {
		if(StringUtils.isEmpty(rule.getRuleId())) {
			throw new ValleyException("规则ID不能为空");
		}
		ContractStandardEntity ruleInfo = contractStandardService.findContractStandardInfo(rule.getRuleId());
		ruleInfo.setContractTypes(ruleInfo.getContractType().split("#"));
		return ruleInfo;
	}
	
	/**
	 * 分页查询标准规则
	 * @param rule
	 * @param page
	 * @return
	 */
	@AuthorityMethod(code=ComplianceAConstant.ContractStandardP.READ_LIST_CODE,name=ComplianceAConstant.ContractStandardP.READ_LIST_NAME)
	@RequestMapping(value = "/page",method=RequestMethod.POST)
	public PageResult<ContractStandardEntity> findContractStandardByPage(@RequestBody ContractStandardEntity rule,PageEntity page) {
		rule.setDataType(ComplianceCommonConstant.COMPLIANCE_DATA_TYPE_STANDARD);
		PageResult<ContractStandardEntity> result = contractStandardService.findContractStandardByPage(rule,page);
		if(result!=null && CollectionUtils.isNotEmpty(result.getResult())) {
			Map<String,String> userNameMap = ValleyUserUtil.getUserNameMap(true);
			Map<String,String> contractTypeMap = ValleyConfigUtil.getSelectItemNameMap("CG_CONTRACT_TYPE");
			for (ContractStandardEntity ruleInfo : result.getResult()) {
				if(StringUtils.isNotEmpty(ruleInfo.getLastUpdateAccount())) {
					ruleInfo.setLastUpdateName(ValleyUserUtil.getUserName(userNameMap,ruleInfo.getLastUpdateAccount()));
				}
				if(StringUtils.isNotEmpty(ruleInfo.getContractType())) {
					StringBuilder contractTypeName = new StringBuilder();
					String[] typeList = ruleInfo.getContractType().split("#");
					for (String contractType : typeList) {
						if(StringUtils.isNotEmpty(contractTypeName.toString())) {
							contractTypeName.append(";");
						}
						contractTypeName.append(StringUtils.isNotEmpty(contractTypeMap.get(contractType))?contractTypeMap.get(contractType):contractType);
					}
					ruleInfo.setContractType(contractTypeName.toString());
				}
			}
		}
		return result;
	}
	
	/**
	 * 修改验证
	 * @param rule
	 */
	private void verifyUpdate(ContractStandardEntity rule) {
		if(StringUtils.isEmpty(rule.getRuleName())) {
			throw new ValleyException("规则名称不能为空");
		}
		if(StringUtils.isEmpty(rule.getRuleType())) {
			throw new ValleyException("规则类型不能为空");
		}
		if(rule.getContractTypes()==null || rule.getContractTypes().length<=0) {
			throw new ValleyException("合同类型不能为空");
		}
		if(StringUtils.isEmpty(rule.getCheckContent())) {
			throw new ValleyException("检查内容不能为空");
		}
		if(StringUtils.isEmpty(rule.getSolveSuggest())) {
			throw new ValleyException("解决建议不能为空");
		}
		rule.setContractType(String.join("#", rule.getContractTypes()));
		rule.setCheckContent(rule.getCheckContent().trim());
		rule.setSolveSuggest(rule.getSolveSuggest().trim());
	}
    
	/**
	 * 新增标准规则
	 * @param rule
	 * @return
	 */
	@AuthorityMethod(code=ComplianceAConstant.ContractStandardP.UPDATE_CODE,name=ComplianceAConstant.ContractStandardP.UPDATE_NAME,show = true)
	@ValleyAppLog(name = "标准规则-新增")
	@RequestMapping(value = "/insert",method=RequestMethod.POST)
	public int insertContractStandard(@RequestBody ContractStandardEntity rule) {
		verifyUpdate(rule);
		rule.setDataType(ComplianceCommonConstant.COMPLIANCE_DATA_TYPE_STANDARD);
		int count = contractStandardService.insertContractStandard(rule);
		return count;
		
	}
	
	/**
     * 修改标准规则
     * @param rule
     * @return
     */
	@AuthorityMethod(code=ComplianceAConstant.ContractStandardP.UPDATE_CODE,name=ComplianceAConstant.ContractStandardP.UPDATE_NAME,show = true)
	@ValleyAppLog(name = "标准规则-修改")
	@RequestMapping(value = "/update",method=RequestMethod.POST)
	public int updateContractStandard(@RequestBody ContractStandardEntity rule) {
		if(StringUtils.isEmpty(rule.getRuleId())) {
			throw new ValleyException("规则ID不能为空");
		}
		verifyUpdate(rule);
		int count = contractStandardService.updateContractStandard(rule);
		return count;
	}
	
	/**
     * 修改标准规则状态
     * @param rule
     * @return
     */
	@AuthorityMethod(code=ComplianceAConstant.ContractStandardP.UPDATE_CODE,name=ComplianceAConstant.ContractStandardP.UPDATE_NAME,show = true)
	@ValleyAppLog(name = "标准规则-状态修改")
	@RequestMapping(value = "/update/status",method=RequestMethod.POST)
	public int updateContractStandardStatus(@RequestBody ContractStandardEntity rule) {
		int count = 0;
		if(CollectionUtils.isEmpty(rule.getRuleIdList())) {
			throw new ValleyException("规则ID不能为空");
		}
		if(StringUtils.isEmpty(rule.getRuleStatus())) {
			throw new ValleyException("规则状态不能为空");
		}
		rule.setRuleStatus(rule.getRuleStatus().toUpperCase());
		if(!("Y".equals(rule.getRuleStatus()) || "N".equals(rule.getRuleStatus()))) {
			throw new ValleyException("规则状态值错误");
		}
		for (String ruleId : rule.getRuleIdList()) {
			rule.setRuleId(ruleId);
			count += contractStandardService.updateContractStandardStatus(rule);
		}
		return count;
	}

	/**
	 * 删除标准规则
	 * @param rule
	 * @config
	 */
	@AuthorityMethod(code=ComplianceAConstant.ContractStandardP.UPDATE_CODE,name=ComplianceAConstant.ContractStandardP.UPDATE_NAME,show = true)
	@ValleyAppLog(name = "标准规则-删除")
	@RequestMapping(value = "/delete",method=RequestMethod.POST)
	public int deleteContractStandard(@RequestBody ContractStandardEntity rule) {
		if(StringUtils.isEmpty(rule.getRuleId())) {
			throw new ValleyException("规则ID不能为空");
		}
		return contractStandardService.deleteContractStandard(rule.getRuleId());
	}

}

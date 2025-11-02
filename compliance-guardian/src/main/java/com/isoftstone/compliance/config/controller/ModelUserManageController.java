package com.isoftstone.compliance.config.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.isoftstone.compliance.common.constant.ComplianceAConstant;
import com.isoftstone.compliance.common.constant.ComplianceQConstant;
import com.isoftstone.compliance.common.util.ComplianceServiceUtil;
import com.isoftstone.compliance.config.model.ModelUserEntity;
import com.isoftstone.compliance.config.model.ModelUserLoginEntity;
import com.isoftstone.compliance.config.service.IModelUserManageService;
import com.ljh.yqvalley.common.util.ValleyContextUtil;
import com.ljh.yqvalley.config.model.PublicSelectItemEntity;
import com.ljh.yqvalley.config.util.ValleyConfigUtil;
import com.ljh.yqvalley.core.annotation.AuthorityClass;
import com.ljh.yqvalley.core.annotation.AuthorityMethod;
import com.ljh.yqvalley.core.annotation.ValleyAppLog;
import com.ljh.yqvalley.core.exception.ValleyException;
import com.ljh.yqvalley.core.model.PageEntity;
import com.ljh.yqvalley.core.model.PageResult;
import com.ljh.yqvalley.core.model.ValleyResult;
import com.ljh.yqvalley.file.model.ExportExcelEntity;
import com.ljh.yqvalley.file.model.ImportExcelEntity;
import com.ljh.yqvalley.file.service.IFileExportService;
import com.ljh.yqvalley.file.service.IFileImportService;
import com.ljh.yqvalley.file.util.ExcelTemplateUtil;
import com.ljh.yqvalley.user.model.ValleyDeptEntity;
import com.ljh.yqvalley.user.model.ValleyUserEntity;
import com.ljh.yqvalley.user.util.RequestContextUtil;
import com.ljh.yqvalley.user.util.ValleyUserUtil;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;

@AuthorityClass(module=ComplianceAConstant.MODULE,code=ComplianceAConstant.ComplianceUserP.CLASS_CODE,name=ComplianceAConstant.ComplianceUserP.CLASS_NAME)
@RestController
@RequestMapping(ComplianceQConstant.MODEL_USER)
public class ModelUserManageController {

	@Resource
	private IModelUserManageService modelUserManageService;
	
	/**
	 * 查询单条用户信息
	 * @param user
	 * @return
	 */
	@AuthorityMethod(code=ComplianceAConstant.ComplianceUserP.READ_LIST_CODE,name=ComplianceAConstant.ComplianceUserP.READ_LIST_NAME)
	@RequestMapping(value = "/find",method=RequestMethod.POST)
	public ModelUserEntity findModelUserInfo(@RequestBody ModelUserEntity user) {
		return modelUserManageService.findModelUserInfo(user.getUserAccount());
	}
	
	/**
	 * 分页查询用户列表
	 * @param user
	 * @param page
	 * @return
	 */
	@AuthorityMethod(code=ComplianceAConstant.ComplianceUserP.READ_LIST_CODE,name=ComplianceAConstant.ComplianceUserP.READ_LIST_NAME)
	@RequestMapping(value = "/page",method=RequestMethod.POST)
	public PageResult<ModelUserEntity> findValleyUserByPage(@RequestBody ModelUserEntity user,PageEntity page) {
		PageResult<ModelUserEntity> result = modelUserManageService.findModelUserByPage(user,page);
		if(CollectionUtils.isNotEmpty(result.getResult())) {
			for (ModelUserEntity valleyUser : result.getResult()) {
				//部门处理
				valleyUser.setCompleteName(ComplianceServiceUtil.getCompleteName(valleyUser));
			}
		}
		return result;
	}
	
	/**
	 * 部门信息查询
	 * @param dept
	 * @return
	 */
	@AuthorityMethod(code=ComplianceAConstant.ComplianceUserP.UPDATE_CODE,name=ComplianceAConstant.ComplianceUserP.UPDATE_NAME,show = true)
	@RequestMapping(value = "/dept/list",method=RequestMethod.POST)
	public List<ValleyDeptEntity> findSearchDeptList(@RequestBody ValleyDeptEntity dept) {
		List<ValleyDeptEntity> deptList = ValleyUserUtil.getDeptList();
		if(CollectionUtils.isNotEmpty(deptList)) {
			for (ValleyDeptEntity valleyDept : deptList) {
				valleyDept.setCompleteName(ValleyUserUtil.getCompleteName(valleyDept));
			}
			if(StringUtils.isNotEmpty(dept.getDeptStatus())) {
				deptList = deptList.stream().filter(deptInfo->"Y".equals(deptInfo.getDeptStatus())).collect(Collectors.toList());
			}
		}
		return deptList;
	}
	
    /**
     * 新增用户
     * @param user
     * @return
     */
	@AuthorityMethod(code=ComplianceAConstant.ComplianceUserP.UPDATE_CODE,name=ComplianceAConstant.ComplianceUserP.UPDATE_NAME,show = true)
    @RequestMapping(value = "/insert",method=RequestMethod.POST)
    @ValleyAppLog(name = "用户管理-添加用户")
    public ValleyResult<Integer> insertModelUserInfo(@RequestBody ModelUserEntity user) {
		String valleyAccount = RequestContextUtil.getCurrentAccount();
		user.setCreateAccount(valleyAccount);
		if(StringUtils.isEmpty(user.getUserAccount())) {
			throw new ValleyException("用户账号不能为空");
		}
		if(StringUtils.isEmpty(user.getUserName())) {
			throw new ValleyException("用户姓名不能为空");
		}
		if(StringUtils.isEmpty(user.getMailAddress())) {
			throw new ValleyException("用户邮箱不能为空");
		}
		if(StringUtils.isEmpty(user.getPhoneNumber())) {
			throw new ValleyException("手机号码不能为空");
		}
		modelUserManageService.insertModelUserInfo(user);
		user.setDefaultLoginPwd(null);
		user.setLoginPwd(null);
        return ValleyResult.newSuccess();
    }
    
    /**
	 * 修改用户信息
	 * @param user
	 * @return
	 */
	@AuthorityMethod(code=ComplianceAConstant.ComplianceUserP.UPDATE_CODE,name=ComplianceAConstant.ComplianceUserP.UPDATE_NAME,show = true)
	@RequestMapping(value = "/update",method=RequestMethod.POST)
	@ValleyAppLog(name = "用户管理-修改用户信息")
	public ValleyResult<Integer> updateModelUserInfo(@RequestBody ModelUserEntity user) {
		if(StringUtils.isEmpty(user.getUserAccount())) {
			throw new ValleyException("用户账号不能为空");
		}
		modelUserManageService.updateModelUserInfo(user);
		return ValleyResult.newSuccess();
	}
	
	/**
	 * 修改用户状态
	 * @param user
	 * @return
	 */
	@AuthorityMethod(code=ComplianceAConstant.ComplianceUserP.UPDATE_CODE,name=ComplianceAConstant.ComplianceUserP.UPDATE_NAME,show = true)
	@RequestMapping(value = "/status/update",method=RequestMethod.POST)
	@ValleyAppLog(name = "用户管理-修改用户状态")
	public ValleyResult<Integer> updateModelUserStatus(@RequestBody ModelUserEntity user) {
		if(StringUtils.isEmpty(user.getUserAccount()) && CollectionUtils.isEmpty(user.getUserAccountList())) {
			throw new ValleyException("用户账户不能为空");
		}
		if(StringUtils.isEmpty(user.getAccountStatus())) {
			throw new ValleyException("用户状态不能为空");
		}
		if(CollectionUtils.isEmpty(user.getUserAccountList())) {
			user.setUserAccountList(new ArrayList<String>());
		}
		if(StringUtils.isNotEmpty(user.getUserAccount())) {
			user.getUserAccountList().add(user.getUserAccount());
		}
		modelUserManageService.updateModelUserStatus(user);
		return ValleyResult.newSuccess();
	}
	
	/**
	 * 修改用户部门
	 * @param user
	 * @return
	 */
	@AuthorityMethod(code=ComplianceAConstant.ComplianceUserP.UPDATE_CODE,name=ComplianceAConstant.ComplianceUserP.UPDATE_NAME,show = true)
	@RequestMapping(value = "/dept/update",method=RequestMethod.POST)
	@ValleyAppLog(name = "用户管理-修改用户部门")
	public ValleyResult<Integer> updateModelUserDept(@RequestBody ModelUserEntity user) {
		if(StringUtils.isEmpty(user.getUserAccount()) && CollectionUtils.isEmpty(user.getUserAccountList())) {
			throw new ValleyException("用户账户不能为空");
		}
		if(StringUtils.isEmpty(user.getDeptCode())) {
			throw new ValleyException("用户部门不能为空");
		}
		if(CollectionUtils.isEmpty(user.getUserAccountList())) {
			user.setUserAccountList(new ArrayList<String>());
		}
		if(StringUtils.isNotEmpty(user.getUserAccount())) {
			user.getUserAccountList().add(user.getUserAccount());
		}
		modelUserManageService.updateModelUserDept(user);
		return ValleyResult.newSuccess();
	}
	
	
	
	/**
	 * 修改用户密码
	 * @param user
	 * @return
	 */
	@AuthorityMethod(code=ComplianceAConstant.ComplianceUserP.UPDATE_CODE,name=ComplianceAConstant.ComplianceUserP.UPDATE_NAME,show = true)
	@RequestMapping(value = "/pwd/reset",method=RequestMethod.POST)
	@ValleyAppLog(name = "用户管理-重置密码",response=false)
	public ValleyResult<String> resetModelLoginPwd(@RequestBody ModelUserLoginEntity user) {
		if(StringUtils.isEmpty(user.getUserAccount())) {
			throw new ValleyException("用户账号不能为空");
		}
		String defaultPwd = modelUserManageService.resetModelLoginPwd(user.getUserAccount());
		user.setLoginPwd(null);
		user.setLoginPwdNew(null);
		user.setDefaultLoginPwd(null);
		return ValleyResult.newSuccess(defaultPwd);
	}
	
	/**
	 * 查询用户默认密码
	 * @param user
	 * @return
	 */
	@AuthorityMethod(code=ComplianceAConstant.ComplianceUserP.UPDATE_CODE,name=ComplianceAConstant.ComplianceUserP.UPDATE_NAME,show = true)
	@RequestMapping(value = "/default/pwd",method=RequestMethod.POST)
	public ValleyResult<String> findDefaultLoginPwd(@RequestBody ModelUserEntity user) {
		if(StringUtils.isEmpty(user.getUserAccount())) {
			throw new ValleyException("用户账号不能为空");
		}
		String defaultPwd = modelUserManageService.findDefaultLoginPwd(user.getUserAccount());
		if(StringUtils.isEmpty(defaultPwd)) {
			throw new ValleyException("默认密码已失效");
		}
		return ValleyResult.newSuccess(defaultPwd);
	}
	
    /**
     * 用户导入模板下载
     * @param response
     */
	@RequestMapping(value = "/template",method=RequestMethod.GET)
    public void templateDownload(HttpServletResponse response){
		ValleyUserEntity user = RequestContextUtil.getCurrentUser(true);
    	if(user!=null) {
    		boolean bo = RequestContextUtil.verifyAuthority("compliance.compliance_user.update",true);
            if (bo) {
            	String confBeanId = "modelUser.excelImport";
            	Map<String, String[]> lookupData = new HashMap<String, String[]>();
            	//账号类型
            	List<PublicSelectItemEntity> accountTypeList = ValleyConfigUtil.findSelectItemConfigList("CG_MODEL_ACCOUNT_TYPE");
    			List<String> accountTypeNameList=new ArrayList<String>();
    			for(PublicSelectItemEntity accountType:accountTypeList){
    				accountTypeNameList.add(accountType.getItemName()+"("+accountType.getItemValue()+")");
    			}
    			String[] accountTypeNameArr=new String[accountTypeNameList.size()];
    			accountTypeNameList.toArray(accountTypeNameArr);
    			lookupData.put("账号类型",accountTypeNameArr);
    			//查询用户所有角色
        		ExcelTemplateUtil.downloadXlsxTemplate(response, confBeanId,lookupData);
            }
    	}
    }
    
    /**
     * 用户信息导入
     * @param fileId
     * @return
     */
	@AuthorityMethod(code=ComplianceAConstant.ComplianceUserP.UPDATE_CODE,name=ComplianceAConstant.ComplianceUserP.UPDATE_NAME,show = true)
    @ValleyAppLog(name = "用户管理-导入")
	@RequestMapping(value = "/import",method=RequestMethod.GET)
    public ValleyResult<Integer> importValleyUser(@RequestParam String fileId){
    	String handleBeanId = "excelFileImportService";
    	String confBeanId = "modelUser.excelImport";
    	IFileImportService fileImportService = ValleyContextUtil.getBean(handleBeanId, IFileImportService.class);
    	ImportExcelEntity excel = new ImportExcelEntity();
    	excel.setFileId(fileId);
    	excel.setConfBeanId(confBeanId);
    	excel.setValleyAccount(RequestContextUtil.getCurrentAccount());
    	excel.setSheetName("userInfo");
    	excel.setHeadRowIdx(0);
    	fileImportService.addWorkTask(handleBeanId, excel);
		return ValleyResult.newSuccess();
	}
    
    /**
     * 用户信息导出
     * @param user
     * @return
     */
	@AuthorityMethod(code=ComplianceAConstant.ComplianceUserP.UPDATE_CODE,name=ComplianceAConstant.ComplianceUserP.UPDATE_NAME,show = true)
    @ValleyAppLog(name = "用户管理-导出")
	@RequestMapping(value = "/export",method=RequestMethod.POST)
    public ValleyResult<Integer> exportValleyUser(@RequestBody ModelUserEntity user){
    	String handleBeanId = "excelFileExportService";
    	String consumerBeanId = "modelUserExportConsumer";
    	IFileExportService fileExportService = ValleyContextUtil.getBean(handleBeanId, IFileExportService.class);
    	ExportExcelEntity excel = new ExportExcelEntity();
    	user.setCreateAccount(RequestContextUtil.getCurrentAccount());
    	excel.setConsumerBeanId(consumerBeanId);
    	excel.setFileName("用户信息");
    	excel.setValleyAccount(RequestContextUtil.getCurrentAccount());
    	excel.setTitle("用户信息");
    	excel.setEnableSeqNum(true);
    	excel.setSheetName("user");
    	excel.setCondition(user);
    	fileExportService.addWorkTask(handleBeanId, excel);
		return ValleyResult.newSuccess();
	}

}

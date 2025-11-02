package com.isoftstone.compliance.config.service.impl;

import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.isoftstone.compliance.common.constant.ComplianceCommonConstant;
import com.isoftstone.compliance.config.dao.IModelUserManageMapper;
import com.isoftstone.compliance.config.model.ModelUserEntity;
import com.isoftstone.compliance.config.model.ModelUserLoginEntity;
import com.isoftstone.compliance.config.service.IModelUserManageService;
import com.ljh.yqvalley.common.util.DataEncryptUtil;
import com.ljh.yqvalley.core.exception.ValleyException;
import com.ljh.yqvalley.core.model.PageEntity;
import com.ljh.yqvalley.core.model.PageResult;
import com.ljh.yqvalley.user.dao.IValleyDeptMapper;
import com.ljh.yqvalley.user.model.ValleyDeptEntity;
import com.ljh.yqvalley.user.util.PasswordUtil;
import com.ljh.yqvalley.user.util.RequestContextUtil;
import com.ljh.yqvalley.user.util.ValleyUserUtil;

import jakarta.annotation.Resource;



@Service
public class ModelUserManageService implements IModelUserManageService {

	@Resource
    private IModelUserManageMapper modelUserManageMapper;
	@Resource
	private IValleyDeptMapper valleyDeptMapper;
	
	@Override
	public ModelUserEntity findModelUserSimpleInfo(String userAccount) {
		return modelUserManageMapper.findModelUserSimpleInfo(userAccount);
	}
	
	@Override
	public ModelUserEntity findModelUserInfo(String userAccount) {
		ModelUserEntity user = modelUserManageMapper.findModelUserInfo(userAccount);
		if(user!=null) {
			if(StringUtils.isNotEmpty(user.getDefaultLoginPwd())) {
				user.setDefaultLoginPwd(DataEncryptUtil.decryptString(user.getDefaultLoginPwd()));
			}
		}
		return user;
	}
	
	@Override
	public ModelUserLoginEntity findModelLoginInfo(ModelUserEntity user) {
		ModelUserLoginEntity userInfo = modelUserManageMapper.findModelLoginInfo(user);
		return userInfo;
	}
	
	@Override
	public PageResult<ModelUserEntity> findModelUserByPage(ModelUserEntity user, PageEntity page) {
		PageResult<ModelUserEntity> result = modelUserManageMapper.findModelUserByPage(user, page);
		if(CollectionUtils.isNotEmpty(result.getResult())) {
			Map<String,String> userNameMap = ValleyUserUtil.getUserNameMap(true);
			for (ModelUserEntity valleyUser : result.getResult()) {
				if(valleyUser!=null) {
					if(StringUtils.isNotEmpty(valleyUser.getCreateAccount())) {
						valleyUser.setCreateName(ValleyUserUtil.getUserName(userNameMap,valleyUser.getCreateAccount()));
					}
					if(StringUtils.isNotEmpty(valleyUser.getLastUpdateAccount())) {
						valleyUser.setLastUpdateName(ValleyUserUtil.getUserName(userNameMap,valleyUser.getLastUpdateAccount()));
					}
					user.setDefaultLoginPwd(null);
				}
			}
		}
		return result;
	}

	@Override
	public int insertModelUserInfo(ModelUserEntity user) {
		user.setCreateAccount(RequestContextUtil.getCurrentAccount());
		user.setUserAccount(user.getUserAccount().trim());
		if(!user.getUserAccount().matches("[0-9A-Za-z_]*")) {
			throw new ValleyException("用户账号只能是大小写字母、数字和下划线。");
		}
		if(user.getUserAccount().length()<5 || user.getUserAccount().length()>20) {
			throw new ValleyException("用户账号长度只能是5-20位。");
		}
		//处理部门
		if(StringUtils.isNotEmpty(user.getDeptCode())) {
			ValleyDeptEntity dept = valleyDeptMapper.findValleyDeptInfo(user.getDeptCode(),"Y");
			if(dept!=null) {
				user.setLevel1DeptCode(dept.getLevel1Code());
				user.setLevel2DeptCode(dept.getLevel2Code());
				user.setLevel3DeptCode(dept.getLevel3Code());
				user.setLevel4DeptCode(dept.getLevel4Code());
			}else {
				throw new ValleyException("部门信息不存在或已失效。");
			}
		}
		//用户信息查重
		int repeatNum = modelUserManageMapper.findModelUserCount(user,null);
		if(repeatNum>0) {
			throw new ValleyException("有【登录账号or用户账号or用户邮箱or手机号码】相同的用户已存在。");
		}
		user.setAccountStatus("Y");
		if(StringUtils.isEmpty(user.getAccountType())) {
			user.setAccountType(ComplianceCommonConstant.MODEL_USER_TYPE_DEFAULT);
		}
		//生成默认密码
		String loginPwd = PasswordUtil.getRandomPassword(12, 3);
		user.setLoginPwd(PasswordUtil.encryption(loginPwd));
		user.setDefaultLoginPwd(DataEncryptUtil.encryptString(loginPwd));
		return modelUserManageMapper.insertModelUserInfo(user);
	}
	
	@Override
	public int updateCurrentUserInfo(ModelUserEntity user) {
		ModelUserEntity olduser = modelUserManageMapper.findModelUserInfo(user.getUserAccount());
		if(olduser==null || !"Y".equalsIgnoreCase(olduser.getAccountStatus())) {
			throw new ValleyException("用户不存在 ");
		}
		ModelUserEntity ruser = new ModelUserEntity();
		ruser.setMailAddress(user.getMailAddress());
		ruser.setPhoneNumber(user.getPhoneNumber());
		int repeatNum = modelUserManageMapper.findModelUserCount(ruser,user.getUserAccount());
		if(repeatNum>0) {
			throw new ValleyException("有【用户邮箱or手机号码】相同的用户已存在。");
		}
		return modelUserManageMapper.updateModelUserInfo(user);
	}

	@Override
	public int updateModelUserInfo(ModelUserEntity user) {
		ModelUserEntity olduser = modelUserManageMapper.findModelUserInfo(user.getUserAccount());
		if(olduser==null) {
			throw new ValleyException("用户不存在 ");
		}
		//处理部门
		if(StringUtils.isNotEmpty(user.getDeptCode())) {
			ValleyDeptEntity dept = valleyDeptMapper.findValleyDeptInfo(user.getDeptCode(),"Y");
			if(dept!=null) {
				user.setLevel1DeptCode(dept.getLevel1Code());
				user.setLevel2DeptCode(dept.getLevel2Code());
				user.setLevel3DeptCode(dept.getLevel3Code());
				user.setLevel4DeptCode(dept.getLevel4Code());
			}else {
				throw new ValleyException("部门信息不存在或已失效。");
			}
		}
		return modelUserManageMapper.updateModelUserInfo(user);
	}

	@Override
	public int updateModelUserStatus(ModelUserEntity user) {
		int count = 0;
		String currentAccount = RequestContextUtil.getCurrentAccount();
		if(CollectionUtils.isNotEmpty(user.getUserAccountList())) {
			count = modelUserManageMapper.updateModelUserStatus(user.getUserAccountList(), user.getAccountStatus(),currentAccount);
		}
		return count;
	}

	@Override
	public int updateModelUserDept(ModelUserEntity user) {
		int count = 0;
		String currentAccount = RequestContextUtil.getCurrentAccount();
		//处理部门
		ValleyDeptEntity dept = valleyDeptMapper.findValleyDeptInfo(user.getDeptCode(),"Y");
		if(dept!=null) {
			if(CollectionUtils.isNotEmpty(user.getUserAccountList())) {
				count = modelUserManageMapper.updateModelUserDept(user.getUserAccountList(), dept,currentAccount);
			}
		}else {
			throw new ValleyException("部门信息不存在或已失效。");
		}
		return count;
	}

	@Override
	public int updateModelLoginPwd(String valleyAccount, String loginPwdNew) {
		int count = 0;
		String currentAccount = valleyAccount;
		String hitDesc = PasswordUtil.complianceVerify(loginPwdNew);
		if (StringUtils.isNotEmpty(hitDesc)) {
			throw new ValleyException(hitDesc);
		}
		//修改密码
		String password = PasswordUtil.encryption(loginPwdNew);
		count = modelUserManageMapper.updateModelLoginPwd(valleyAccount, password, null,currentAccount);
		return count;
	}
	
	@Override
	public String resetModelLoginPwd(String userAccount) {
		int count = 0;
		ModelUserEntity olduser = modelUserManageMapper.findModelUserInfo(userAccount);
		if(olduser==null) {
			throw new ValleyException("用户不存在 ");
		}
		String currentAccount = RequestContextUtil.getCurrentAccount();
		String defaultPwd = PasswordUtil.getRandomPassword(12, 3);
		String loginPwd = PasswordUtil.encryption(defaultPwd);
		count = modelUserManageMapper.updateModelLoginPwd(userAccount, loginPwd, null,currentAccount);
		if(count<=0) {
			defaultPwd = null;
		}
		return defaultPwd;
	}

	@Override
	public String findDefaultLoginPwd(String userAccount) {
		String defaultLoginPwd = null;
		ModelUserEntity olduser = modelUserManageMapper.findModelAllUserInfo(userAccount);
		if(olduser!=null && StringUtils.isNotEmpty(olduser.getDefaultLoginPwd()) && "Y".equalsIgnoreCase(olduser.getDefaultLoginPwdExpire())) {
			defaultLoginPwd = DataEncryptUtil.decryptString(olduser.getDefaultLoginPwd());
		}
		return defaultLoginPwd;
	}

}

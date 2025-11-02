package com.isoftstone.compliance.config.service;

import com.isoftstone.compliance.config.model.ModelUserEntity;
import com.isoftstone.compliance.config.model.ModelUserLoginEntity;
import com.ljh.yqvalley.core.model.PageEntity;
import com.ljh.yqvalley.core.model.PageResult;

public interface IModelUserManageService {
	
	/**
	 * 查询单条用户信息
	 * @param userAccount
	 * @return
	 */
	public ModelUserEntity findModelUserSimpleInfo(String userAccount);
	
	/**
	 * 查询单条用户信息
	 * @param userAccount
	 * @return
	 */
	public ModelUserEntity findModelUserInfo(String userAccount);
	
	/**
	 * 查询用户登录信息
	 * @param user
	 * @return
	 */
	public ModelUserLoginEntity findModelLoginInfo(ModelUserEntity user);
	
	/**
	 * 分页查询用户列表
	 * @param page
	 * @return
	 */
	public PageResult<ModelUserEntity> findModelUserByPage(ModelUserEntity user,PageEntity page);
	
	/**
	 * 新增用户
	 * @param user
	 * @return
	 */
	public int insertModelUserInfo(ModelUserEntity user);
	
	/**
	 * 修改当前用户信息
	 * @param user
	 * @return
	 */
	public int updateCurrentUserInfo(ModelUserEntity user);
	
	/**
	 * 修改用户信息
	 * @param user
	 * @return
	 */
	public int updateModelUserInfo(ModelUserEntity user);
	
	/**
	 * 修改用户状态
	 * @param user
	 * @return
	 */
	public int updateModelUserStatus(ModelUserEntity user);
	
	/**
	 * 修改账号部门
	 * @param user
	 * @return
	 */
	public int updateModelUserDept(ModelUserEntity user);
	
	/**
	 * 修改登录密码
	 * @param userAccount
	 * @param loginPwdNew
	 * @return
	 */
	public int updateModelLoginPwd(String userAccount,String loginPwdNew);
	
	/**
	 * 修改登录密码
	 * @param userAccount
	 * @return
	 */
	public String resetModelLoginPwd(String userAccount);
	
	/**
	 * 查看默认密码
	 * @param userAccount
	 * @return
	 */
	public String findDefaultLoginPwd(String userAccount);

}

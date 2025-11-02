package com.isoftstone.compliance.config.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.isoftstone.compliance.config.model.ModelUserEntity;
import com.isoftstone.compliance.config.model.ModelUserLoginEntity;
import com.ljh.yqvalley.core.model.PageEntity;
import com.ljh.yqvalley.core.model.PageResult;
import com.ljh.yqvalley.user.model.ValleyDeptEntity;

public interface IModelUserManageMapper {
	
	/**
	 * 查询简单用户信息
	 * @param userAccount
	 * @return
	 */
	public ModelUserEntity findModelUserSimpleInfo(@Param("userAccount") String userAccount);
	
	/**
	 * 查询单条用户信息
	 * @param userAccount
	 * @return
	 */
	public ModelUserEntity findModelUserInfo(@Param("userAccount") String userAccount);
	
	/**
	 * 查询用户完整信息
	 * @param userAccount
	 * @return
	 */
	public ModelUserEntity findModelAllUserInfo(@Param("userAccount") String userAccount);
	
	/**
	 * 查询用户登录信息
	 * @param loginAccount
	 * @return
	 */
	public ModelUserLoginEntity findModelLoginInfo(@Param("ent") ModelUserEntity user);
	
	/**
	 * 分页查询用户列表
	 * @param page
	 * @return
	 */
	public PageResult<ModelUserEntity> findModelUserByPage(@Param("ent") ModelUserEntity user,@Param("page") PageEntity page);
	
	/**
	 * 新增用户
	 * @param user
	 * @return
	 */
	public int insertModelUserInfo(@Param("ent") ModelUserEntity user);
	
	/**
	 * 修改用户信息
	 * @param user
	 * @return
	 */
	public int updateModelUserInfo(@Param("ent") ModelUserEntity user);
	
	/**
	 * 修改用户状态
	 * @param userAccountList
	 * @param accountStatus
	 * @param currentAccount
	 * @return
	 */
	public int updateModelUserStatus(@Param("list") List<String> userAccountList,@Param("accountStatus") String accountStatus,@Param("currentAccount") String currentAccount);
	
	/**
	 * 修改账号部门
	 * @param userAccountList
	 * @param dept
	 * @param currentAccount
	 * @return
	 */
	public int updateModelUserDept(@Param("list") List<String> userAccountList,@Param("ent") ValleyDeptEntity dept,@Param("currentAccount") String currentAccount);
	
	/**
	 * 修改登录密码
	 * @param userAccount
	 * @param loginPwd
	 * @param defaultLoginPwd
	 * @param currentAccount
	 * @return
	 */
	public int updateModelLoginPwd(@Param("userAccount") String userAccount,@Param("loginPwd") String loginPwd,@Param("defaultLoginPwd") String defaultLoginPwd,@Param("currentAccount") String currentAccount);
	
	/**
	 * 查询重复用户数量
	 * @param user
	 * @param userAccount
	 * @return
	 */
	public int findModelUserCount(@Param("ent") ModelUserEntity user,@Param("userAccount") String userAccount);
	
}

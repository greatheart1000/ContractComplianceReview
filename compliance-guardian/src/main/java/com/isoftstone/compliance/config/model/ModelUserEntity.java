package com.isoftstone.compliance.config.model;

import java.util.List;

import com.ljh.yqvalley.core.model.ValleyEntity;

public class ModelUserEntity extends ValleyEntity {
	
	private static final long serialVersionUID = -7767219014944870016L;
	private String userAccount;//用户账号
    private String userName;//用户姓名
    private String faceFileId;//头像ID
    private String mailAddress;//电子邮箱
    private String phoneNumber;//手机号码
    private String accountStatus;//用户状态(Y:启用,N:禁用)
    private String accountType;//账号类型
	private String deptCode;//部门编号
	private String level1DeptCode;//一级部门编号
	private String level2DeptCode;//二级部门编号
	private String level3DeptCode;//三级部门编号
	private String level4DeptCode;//四级部门编号
	private String level5DeptCode;//五级部门编号
	private String deptName;//部门名称
	private String level1DeptName;//一级部门名称
	private String level2DeptName;//二级部门名称
	private String level3DeptName;//三级部门名称
	private String level4DeptName;//四级部门名称
	private String level5DeptName;//五级部门名称
	private String completeName;//完整部门名称
    private String matchSearch;//匹配搜索
	private List<String> userAccountList;//账号列表
	private List<String> childrenDeptList;//下级部门
	private String defaultLoginPwd;//默认登录密码
	private String defaultLoginPwdExpire;//默认登录密码是否有效
	private String loginPwd;//用户密码
	private String parentCode;//父部门编号
	private List<String> excludeTypeList;//排除类型
	
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getFaceFileId() {
		return faceFileId;
	}
	public void setFaceFileId(String faceFileId) {
		this.faceFileId = faceFileId;
	}
	public String getMailAddress() {
		return mailAddress;
	}
	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getAccountStatus() {
		return accountStatus;
	}
	public void setAccountStatus(String accountStatus) {
		this.accountStatus = accountStatus;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getDeptCode() {
		return deptCode;
	}
	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}
	public String getLevel1DeptCode() {
		return level1DeptCode;
	}
	public void setLevel1DeptCode(String level1DeptCode) {
		this.level1DeptCode = level1DeptCode;
	}
	public String getLevel2DeptCode() {
		return level2DeptCode;
	}
	public void setLevel2DeptCode(String level2DeptCode) {
		this.level2DeptCode = level2DeptCode;
	}
	public String getLevel3DeptCode() {
		return level3DeptCode;
	}
	public void setLevel3DeptCode(String level3DeptCode) {
		this.level3DeptCode = level3DeptCode;
	}
	public String getLevel4DeptCode() {
		return level4DeptCode;
	}
	public void setLevel4DeptCode(String level4DeptCode) {
		this.level4DeptCode = level4DeptCode;
	}
	public String getLevel5DeptCode() {
		return level5DeptCode;
	}
	public void setLevel5DeptCode(String level5DeptCode) {
		this.level5DeptCode = level5DeptCode;
	}
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getLevel1DeptName() {
		return level1DeptName;
	}
	public void setLevel1DeptName(String level1DeptName) {
		this.level1DeptName = level1DeptName;
	}
	public String getLevel2DeptName() {
		return level2DeptName;
	}
	public void setLevel2DeptName(String level2DeptName) {
		this.level2DeptName = level2DeptName;
	}
	public String getLevel3DeptName() {
		return level3DeptName;
	}
	public void setLevel3DeptName(String level3DeptName) {
		this.level3DeptName = level3DeptName;
	}
	public String getLevel4DeptName() {
		return level4DeptName;
	}
	public void setLevel4DeptName(String level4DeptName) {
		this.level4DeptName = level4DeptName;
	}
	public String getLevel5DeptName() {
		return level5DeptName;
	}
	public void setLevel5DeptName(String level5DeptName) {
		this.level5DeptName = level5DeptName;
	}
	public String getCompleteName() {
		return completeName;
	}
	public void setCompleteName(String completeName) {
		this.completeName = completeName;
	}
	public String getMatchSearch() {
		return matchSearch;
	}
	public void setMatchSearch(String matchSearch) {
		this.matchSearch = matchSearch;
	}
	public List<String> getUserAccountList() {
		return userAccountList;
	}
	public void setUserAccountList(List<String> userAccountList) {
		this.userAccountList = userAccountList;
	}
	public List<String> getChildrenDeptList() {
		return childrenDeptList;
	}
	public void setChildrenDeptList(List<String> childrenDeptList) {
		this.childrenDeptList = childrenDeptList;
	}
	public String getDefaultLoginPwd() {
		return defaultLoginPwd;
	}
	public void setDefaultLoginPwd(String defaultLoginPwd) {
		this.defaultLoginPwd = defaultLoginPwd;
	}
	public String getDefaultLoginPwdExpire() {
		return defaultLoginPwdExpire;
	}
	public void setDefaultLoginPwdExpire(String defaultLoginPwdExpire) {
		this.defaultLoginPwdExpire = defaultLoginPwdExpire;
	}
	public String getLoginPwd() {
		return loginPwd;
	}
	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	public List<String> getExcludeTypeList() {
		return excludeTypeList;
	}
	public void setExcludeTypeList(List<String> excludeTypeList) {
		this.excludeTypeList = excludeTypeList;
	}
	
}

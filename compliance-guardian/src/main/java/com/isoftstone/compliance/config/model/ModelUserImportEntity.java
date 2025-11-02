package com.isoftstone.compliance.config.model;

import java.io.Serializable;

public class ModelUserImportEntity implements Serializable {
	
	private static final long serialVersionUID = -6325797014858496698L;
	
	private String userAccount;//用户账号
    private String userName;//用户姓名
    private String mailAddress;//电子邮箱
    private String phoneNumber;//手机号码
    private String accountType;//账号类型
    private String deptCode;//部门编号
    
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
	
}

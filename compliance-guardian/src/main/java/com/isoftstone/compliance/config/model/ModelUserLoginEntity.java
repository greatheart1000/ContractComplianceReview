package com.isoftstone.compliance.config.model;

import java.io.Serializable;

public class ModelUserLoginEntity implements Serializable {
	
	private static final long serialVersionUID = -8770756078180145169L;
	private String userAccount;//用户账号
	private String loginPwd;//用户密码
    private String loginPwdNew;//新密码
    private String userName;//用户姓名
    private String captcha;//验证码
    private String defaultLoginPwd;//默认登录密码
    private String accountType;//账号类型
    private String defaultLoginPwdExpire;//默认登录密码是否有效
    
	public String getUserAccount() {
		return userAccount;
	}
	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	public String getLoginPwd() {
		return loginPwd;
	}
	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}
	public String getLoginPwdNew() {
		return loginPwdNew;
	}
	public void setLoginPwdNew(String loginPwdNew) {
		this.loginPwdNew = loginPwdNew;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCaptcha() {
		return captcha;
	}
	public void setCaptcha(String captcha) {
		this.captcha = captcha;
	}
	public String getDefaultLoginPwd() {
		return defaultLoginPwd;
	}
	public void setDefaultLoginPwd(String defaultLoginPwd) {
		this.defaultLoginPwd = defaultLoginPwd;
	}
	public String getAccountType() {
		return accountType;
	}
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	public String getDefaultLoginPwdExpire() {
		return defaultLoginPwdExpire;
	}
	public void setDefaultLoginPwdExpire(String defaultLoginPwdExpire) {
		this.defaultLoginPwdExpire = defaultLoginPwdExpire;
	}
	
}

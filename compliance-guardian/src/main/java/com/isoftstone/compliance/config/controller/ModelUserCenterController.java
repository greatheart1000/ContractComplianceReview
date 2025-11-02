package com.isoftstone.compliance.config.controller;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.isoftstone.compliance.common.annotation.ModelLoginVerify;
import com.isoftstone.compliance.common.constant.ComplianceCommonConstant;
import com.isoftstone.compliance.common.constant.ComplianceQConstant;
import com.isoftstone.compliance.common.util.ComplianceServiceUtil;
import com.isoftstone.compliance.common.util.ModelRequestContextUtil;
import com.isoftstone.compliance.config.model.ModelConfigEntity;
import com.isoftstone.compliance.config.model.ModelHeaderEntity;
import com.isoftstone.compliance.config.model.ModelUserEntity;
import com.isoftstone.compliance.config.model.ModelUserLoginEntity;
import com.isoftstone.compliance.config.service.IModelUserManageService;
import com.ljh.yqvalley.common.constant.ValleyCConstant;
import com.ljh.yqvalley.common.util.RedisHelpUtil;
import com.ljh.yqvalley.common.util.ValleyContextUtil;
import com.ljh.yqvalley.config.model.PublicSelectItemEntity;
import com.ljh.yqvalley.config.util.ValleyConfigUtil;
import com.ljh.yqvalley.core.annotation.RateLimit;
import com.ljh.yqvalley.core.annotation.ValleyAppLog;
import com.ljh.yqvalley.core.annotation.ValleyUserLog;
import com.ljh.yqvalley.core.config.YqvalleyClassConfig;
import com.ljh.yqvalley.core.exception.Valley403Exception;
import com.ljh.yqvalley.core.exception.ValleyException;
import com.ljh.yqvalley.core.exception.ValleyNotLogException;
import com.ljh.yqvalley.core.model.ValleyResult;
import com.ljh.yqvalley.file.model.ValleyFileEntity;
import com.ljh.yqvalley.file.service.IFileStorageCustom;
import com.ljh.yqvalley.file.service.IValleyFileStorageService;
import com.ljh.yqvalley.user.model.LoginResult;
import com.ljh.yqvalley.user.model.ValleyUserLoginEntity;
import com.ljh.yqvalley.user.service.IValleyLoginCustom;
import com.ljh.yqvalley.user.util.JwtUtil;
import com.ljh.yqvalley.user.util.LoginRSAUtil;
import com.ljh.yqvalley.user.util.LoginUtil;
import com.ljh.yqvalley.user.util.PasswordUtil;

import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(ComplianceQConstant.MODEL_USER_CENTER)
public class ModelUserCenterController {
	
	private static Logger logger = LoggerFactory.getLogger(ModelUserCenterController.class);
	
	protected static final String VALLEY_SECURITY_JWT_ISSUER = "valley_security_jwt_issuer";//发布者的url地址
	protected static final String VALLEY_SECURITY_JWT_AUDIENCE = "valley_security_jwt_audience";//接受者的url地址
	protected static final String VALLEY_SECURITY_JWT_SECRET = "valley_security_jwt_secret";//JWT秘钥
	protected static final String LOGIN_FAIL_LOCK_NUM_CONF = "valley_security_login_fail_lock_num";//登录失败锁定次数
	protected static final String LOGIN_FAIL_LOCK_MINUTE_CONF = "valley_security_login_fail_lock_minute";//登录失败锁定时长
	protected static final int LOGIN_FAIL_LOCK_NUM = 3;//登录失败锁定次数
	protected static final int LOGIN_FAIL_LOCK_MINUTE = 30;//登录失败锁定时长
	protected static final String LOGIN_PUBLIC_SECRET="public_secret";//登录公钥
	protected static final String LOGIN_PRIVATE_SECRET_CACHE_KEY="private_secret_cache_key";//私钥缓存KEY
	//修改密码私钥缓存KEY
	protected static final String UPDPWD_PRIVATE_SECRET_CACHE_KEY="model_updpwd_private_secret_cache_key_";
	//用户头像文件类型
	public static final String FACE_FILE_TYPE = "face";
	
	public static final String DEFAULT_TOOL_PROMPT = "cg_default_tool_prompt";
	public static final String DEFAULT_TOOL_FILE_UPLOAD_PROMPT = "cg_default_tool_file_upload_prompt";
	public static final String DEFAULT_TOOL_FILE_UPLOAD_TYPE = "cg_default_tool_file_upload_type";
	
	@Resource
	private IModelUserManageService modelUserManageService;
	@Resource
    private IValleyFileStorageService valleyFlieService;
	
	/**
	 * 初始化
	 * @param response
	 */
	@RateLimit(rate=2.0)
	@RequestMapping(value = "/init",method=RequestMethod.GET)
	public void getPublicKey(HttpServletResponse response) {
		IValleyLoginCustom valleyLoginService = ValleyContextUtil.getBean(YqvalleyClassConfig.USER_LOGIN_BEAN_NAME,IValleyLoginCustom.class);
	    valleyLoginService.getPwdPublicKey(response);
	}
	
	/**
	 * 用户登录
	 * @param response
	 * @param userinfo
	 * @return
	 */
	@RateLimit(rate=2.0)
	@RequestMapping(value = "/login",method=RequestMethod.POST)
	@ValleyUserLog(name = "模型使用-用户登录",content="【{0}】登录系统，IP来源：{1}",keys = {"#userinfo.userAccount","ip"})
	@ValleyAppLog(name = "模型使用-用户登录",index = 1,request = false,response = false)
	public LoginResult<Object> getUserLogin(HttpServletResponse response,@RequestBody ModelUserLoginEntity userinfo) {
	    if(StringUtils.isEmpty(userinfo.getUserAccount()) || StringUtils.isEmpty(userinfo.getLoginPwd())) {
	    	return LoginResult.newFailure("用户名或密码错误！");
	    }
	    ModelUserEntity userSimple = null;
		try {
			int lockNum = ValleyConfigUtil.getAppConfigIntegerValue(LOGIN_FAIL_LOCK_NUM_CONF, LOGIN_FAIL_LOCK_NUM);
			int lockMinute = ValleyConfigUtil.getAppConfigIntegerValue(LOGIN_FAIL_LOCK_MINUTE_CONF, LOGIN_FAIL_LOCK_MINUTE);
			//去除账号空格
		    userinfo.setUserAccount(userinfo.getUserAccount().replaceAll("\\s*",""));
			//登录限制
			if(LoginUtil.loginFailLock("model_"+userinfo.getUserAccount(),lockNum)) {
				throw new ValleyNotLogException("账号锁定"+lockMinute+"分钟，请稍后再试！");
		    }
			//密码解密
		    String loginPwd = this.decryptLoginPwd(response, userinfo.getLoginPwd());
		    if(StringUtils.isEmpty(loginPwd)) {
		    	throw new ValleyNotLogException("用户名或密码错误！");
		    }
		    loginPwd = loginPwd.replaceAll("\\s*","");
		    //密码验证
		    ModelUserEntity newUser = new ModelUserEntity();
		    newUser.setUserAccount(userinfo.getUserAccount());
		    ModelUserLoginEntity loginUser = modelUserManageService.findModelLoginInfo(newUser);
		    if(loginUser!=null) {
		    	if(ComplianceCommonConstant.MODEL_USER_TYPE_DEFAULT.equals(loginUser.getAccountType())) {
		    		localLoginVerify(lockMinute, loginPwd, loginUser);
				} else {
					throw new ValleyException("还未开通此类型用户登录！");
				}
		    } else {
		    	LoginUtil.addLoginFailNum("model_"+userinfo.getUserAccount(),lockMinute);
				throw new ValleyException("用户名或密码错误！");
		    }
		    
		    //获取登陆返回信息
		    userSimple = modelUserManageService.findModelUserSimpleInfo(userinfo.getUserAccount());
		    String token = this.setLoginToken(response, userSimple);
		    if(StringUtils.isEmpty(token)) {
		    	throw new ValleyException("用户名或密码错误！");
		    }
		    userinfo.setUserAccount(userSimple.getUserAccount());
		    //删除失败信息
			LoginUtil.deleteLoginFailNum("model_"+userinfo.getUserAccount());
		} catch (Exception e) {
			String errorStr = null;
			if (e instanceof ValleyException) {
				errorStr = e.getMessage();
	        }else if (e instanceof ValleyNotLogException) {
	        	errorStr = e.getMessage();
	        }
			if(StringUtils.isEmpty(errorStr)) {
				errorStr = "登录失败，请联系管理员！";
			}
			return LoginResult.newFailure(errorStr);
		}
		return LoginResult.newSuccess(userSimple,"登录成功");
	}

	/**
	 * 本地用户登录密码验证
	 * @param userinfo
	 * @param lockMinute
	 * @param loginPwd
	 * @param loginUser
	 */
	private void localLoginVerify(int lockMinute, String loginPwd,ModelUserLoginEntity loginUser) {
		if(StringUtils.isNotEmpty(loginUser.getDefaultLoginPwd()) && "N".equalsIgnoreCase(loginUser.getDefaultLoginPwdExpire())) {
			throw new ValleyException("默认登录密码已过期！");
		}
		//密码对比
		boolean pwdbo = false;
		if(StringUtils.isNotEmpty(loginPwd) && StringUtils.isNotEmpty(loginUser.getLoginPwd())) {
			try {
				pwdbo = PasswordUtil.verify(loginPwd, loginUser.getLoginPwd());
			} catch (Exception e) {
				logger.error("验证登录密码失败:",e);
			}
		}
		if(!pwdbo) {
			LoginUtil.addLoginFailNum("model_"+loginUser.getUserAccount(),lockMinute);
			throw new ValleyException("用户名或密码错误！");
		}
	}
	
	/**
	 * 解密登录密码
	 * @param response
	 * @param loginPwd
	 * @return
	 */
	public String decryptLoginPwd(HttpServletResponse response,String loginPwd) {
		String pwd = null;
    	try {
    		String publicCa = ModelRequestContextUtil.getCookieValue(LOGIN_PUBLIC_SECRET);
    		String privateCa = (String)RedisHelpUtil.getValue(LOGIN_PRIVATE_SECRET_CACHE_KEY+"_"+publicCa);
    		if(StringUtils.isNotEmpty(loginPwd) && StringUtils.isNotEmpty(privateCa)) {
    			pwd = LoginRSAUtil.decrypt(loginPwd, privateCa);
        	}
        	RedisHelpUtil.deleteValue(LOGIN_PRIVATE_SECRET_CACHE_KEY+"_"+publicCa);
		} catch (Exception e) {
			logger.error("解密登录密码失败:",e);
		}
    	//清理公钥
    	Cookie pcookie = new Cookie(LOGIN_PUBLIC_SECRET, null);
    	pcookie.setPath("/");
		response.addCookie(pcookie);
		return pwd;
	}
	
	/**
	 * 设置TOKEN
	 * @param response
	 * @param userSimple
	 * @return
	 */
	public String setLoginToken(HttpServletResponse response,ModelUserEntity userSimple) {
		String jwtToken = null;
		if(userSimple!=null && StringUtils.isNotEmpty(userSimple.getUserAccount())) {
			String jwtIssuer = ValleyConfigUtil.getAppConfigValue(VALLEY_SECURITY_JWT_ISSUER,"http://localhost");
			String jwtAudienc = ValleyConfigUtil.getAppConfigValue(VALLEY_SECURITY_JWT_AUDIENCE,"http://localhost");
			String jwtSecret = ValleyConfigUtil.getAppConfigValue(VALLEY_SECURITY_JWT_SECRET,"xY3oWKpTtKzqyN2pBvYPkV5GJdGhFigGmkj2dZ6F");
			Integer loginExpirationMinute = ComplianceCommonConstant.MODEL_USER_LOGIN_EXPIRATION;
			Integer jwtExpirationMinute = 1440;
			jwtToken = JwtUtil.createJwt(userSimple.getUserAccount(),jwtAudienc,jwtIssuer,jwtExpirationMinute*60*1000,jwtSecret);
			String jwtStr[] = jwtToken.split("\\.");
			String ridesKey = ModelRequestContextUtil.MODEL_TOKEN_KEY+userSimple.getUserAccount()+ValleyCConstant.TOKEN_KEY_EXCISION+jwtStr[jwtStr.length-1];
			RedisHelpUtil.clearPatternValue(ModelRequestContextUtil.MODEL_TOKEN_KEY+userSimple.getUserAccount()+ValleyCConstant.TOKEN_KEY_EXCISION);
			RedisHelpUtil.setValue(ridesKey, userSimple, RedisHelpUtil.second(loginExpirationMinute));
			//向客户端写入
			ModelRequestContextUtil.setSession(LoginUtil.HEADER_TOKEN, jwtToken);
		}
		return jwtToken;
	}
	
	/**
	 * 用户退出登录
	 * @param request
	 * @return
	 */
	@RateLimit
	@ModelLoginVerify
	@RequestMapping(value = "/logout",method=RequestMethod.GET)
	@ValleyUserLog(name = "模型使用-退出登录",content="【{0}】退出登录，IP来源：{1}",keys = {"#userinfo.userAccount","ip"})
	@ValleyAppLog(name = "模型使用-退出登录",index = 1)
	public ValleyResult<String> getExitLogin(HttpServletResponse response,ModelUserLoginEntity userinfo) {
		userinfo.setUserAccount(ModelRequestContextUtil.getCurrentAccount());
		//清理Redis
		String token = ModelRequestContextUtil.getSessionToken();
		if(StringUtils.isNotEmpty(token)) {
			String cacheKey = ModelRequestContextUtil.getLoginUserCacheKey(token);
			if(StringUtils.isNotEmpty(cacheKey)) {RedisHelpUtil.deleteValue(cacheKey);}
		}
		//清理Session
		ModelRequestContextUtil.removeSession(LoginUtil.HEADER_TOKEN);
		return ValleyResult.newSuccess("退出登陆成功");
	}
	
	/**
	 * 查询当前用户信息
	 * @return
	 */
	@ModelLoginVerify
	@RequestMapping(value = "/current",method=RequestMethod.GET)
	public ModelUserEntity findCurrentUserInfo() {
		String userAccount = ModelRequestContextUtil.getCurrentAccount();
		return modelUserManageService.findModelUserSimpleInfo(userAccount);
	}
	
	/**
	 * 修改当前用户信息
	 * @param user
	 * @return
	 */
	@ModelLoginVerify
	@RequestMapping(value = "/current/update",method=RequestMethod.POST)
	@ValleyAppLog(name = "个人中心-修改用户信息")
	public ValleyResult<Integer> updateCurrentUserInfo(@RequestBody ModelUserEntity user) {
		String userAccount = ModelRequestContextUtil.getCurrentAccount();
		user.setUserAccount(userAccount);
		if(StringUtils.isEmpty(user.getUserName())) {
			throw new ValleyException("用户姓名不能为空");
		}
		if(StringUtils.isEmpty(user.getMailAddress())) {
			throw new ValleyException("用户邮箱不能为空");
		}
		if(StringUtils.isEmpty(user.getPhoneNumber())) {
			throw new ValleyException("手机号码不能为空");
		}
		modelUserManageService.updateCurrentUserInfo(user);
		return ValleyResult.newSuccess();
	}
	
	/**
	 * 获取修改密码公钥
	 */
	@ModelLoginVerify
	@RequestMapping(value = "/pwd/updateinit",method=RequestMethod.GET)
	public ValleyResult<String> getPublicKey() {
		String userAccount = ModelRequestContextUtil.getCurrentAccount();
		Map<Integer, String> keyMap = LoginRSAUtil.genKeyPair();
		RedisHelpUtil.setValue(UPDPWD_PRIVATE_SECRET_CACHE_KEY+userAccount, keyMap.get(1), 5L);
		return ValleyResult.newSuccess(keyMap.get(0));
	}
	
	/**
	 * 修改用户密码
	 * @param user
	 * @return
	 */
	@ModelLoginVerify
	@RequestMapping(value = "/pwd/update",method=RequestMethod.POST)
	@ValleyAppLog(name = "个人中心-密码修改",request = false)
	public ValleyResult<Integer> updateValleyLoginPwd(@RequestBody ValleyUserLoginEntity user) {
		int count = 0;
		String userAccount = ModelRequestContextUtil.getCurrentAccount();
		//获取私钥
		String privateCa = (String)RedisHelpUtil.getValue(UPDPWD_PRIVATE_SECRET_CACHE_KEY+userAccount);
		RedisHelpUtil.deleteValue(UPDPWD_PRIVATE_SECRET_CACHE_KEY+userAccount);
		//验证必填项
		if(StringUtils.isEmpty(user.getLoginPwd())) {
			throw new ValleyException("旧密码不能为空");
		}
		if(StringUtils.isEmpty(user.getLoginPwdNew())) {
			throw new ValleyException("新密码不能为空");
		}
		//密码解密
    	try {
    		if(StringUtils.isNotEmpty(privateCa)) {
    			user.setLoginPwd(LoginRSAUtil.decrypt(user.getLoginPwd(), privateCa));
        	}
		} catch (Exception e) {
			logger.error("解密旧密码失败:",e);
		}
    	try {
    		if(StringUtils.isNotEmpty(privateCa)) {
    			user.setLoginPwdNew(LoginRSAUtil.decrypt(user.getLoginPwdNew(), privateCa));
        	}
		} catch (Exception e) {
			logger.error("解密新密码失败:",e);
		}
    	if(StringUtils.isEmpty(user.getLoginPwd())) {
			throw new ValleyException("旧密码不能为空");
		}
		if(StringUtils.isEmpty(user.getLoginPwdNew())) {
			throw new ValleyException("新密码不能为空");
		}
		user.setLoginPwd(user.getLoginPwd().trim());
		user.setLoginPwdNew(user.getLoginPwdNew().trim());
		if(user.getLoginPwd().equals(user.getLoginPwdNew())) {
			throw new ValleyException("新密码不能和旧密码一致");
		}
		//旧密码验证
		ModelUserEntity suser = new ModelUserEntity();
		suser.setUserAccount(userAccount);
		ModelUserLoginEntity olduser = modelUserManageService.findModelLoginInfo(suser);
		if(olduser==null || StringUtils.isEmpty(olduser.getUserAccount())) {
			throw new ValleyException("用户不存在！");
		}
		if(!ComplianceCommonConstant.MODEL_USER_TYPE_DEFAULT.equals(olduser.getAccountType())) {
			throw new ValleyException("该无法通过此功能修改密码！");
		}
		if(StringUtils.isNotEmpty(user.getLoginPwd()) && StringUtils.isNotEmpty(olduser.getLoginPwd())) {
			boolean pwdbo = PasswordUtil.verify(user.getLoginPwd(), olduser.getLoginPwd());
			if(!pwdbo) {
				throw new ValleyException("旧密码错误！");
			}
		}
		count = modelUserManageService.updateModelLoginPwd(userAccount,user.getLoginPwdNew());
		user.setLoginPwd(null);
		user.setLoginPwdNew(null);
		user.setDefaultLoginPwd(null);
		return ValleyResult.newSuccess(count);
	}
	
	/**
	 * 用户头像上传
	 * @param files
	 * @param request
	 * @return
	 */
	@ModelLoginVerify
	@RateLimit(rate=2.0)
    @RequestMapping(value = "/upload/face", method = RequestMethod.POST)
	public ValleyFileEntity faceUpload(@RequestParam("file") List<MultipartFile> files,HttpServletRequest request) {
        String fileType ="face";
        String authority = "private";
        String currentAccount = ModelRequestContextUtil.getCurrentAccount();
        IFileStorageCustom fileStorageService = ValleyContextUtil.getBean(YqvalleyClassConfig.FILE_STORAGE_BEAN_NAME,IFileStorageCustom.class);
        ValleyFileEntity newFile = null;
        if(CollectionUtils.isNotEmpty(files)) {
        	newFile = fileStorageService.saveFile(files.get(0), fileType);
			newFile.setCreateAccount(currentAccount);
			newFile.setAccessAuthority(authority);
            newFile = valleyFlieService.insertValleyFileInfo(newFile);
            newFile.setFilePath(null);
        }
        return newFile;
    }

    /**
     * 获取用户图像
     * @param response
     */
	@ModelLoginVerify
    @RequestMapping(value = "/download/face", method = RequestMethod.GET,produces = MediaType.IMAGE_JPEG_VALUE)
    public void faceDownload(HttpServletResponse response) {
    	ModelUserEntity user = ModelRequestContextUtil.getCurrentUser();
    	if(user!=null) {
    		ValleyFileEntity fileInfo = null;
            if (StringUtils.isNotEmpty(user.getFaceFileId())) {
            	if(fileInfo==null && StringUtils.isNotEmpty(user.getFaceFileId())) {
            		fileInfo = valleyFlieService.findValleyFileInfo(user.getFaceFileId(),user.getUserAccount(),FACE_FILE_TYPE);
            	}
    	        if (fileInfo != null && StringUtils.isNotEmpty(fileInfo.getFilePath())) {
                    response.setContentType(MediaType.IMAGE_JPEG_VALUE);
                    //不使用缓存
                    response.setHeader("Cache-Control", "no-cache");
                    response.setHeader("Pragma", "no-cache");
                    response.setDateHeader("expires", -1);
                    response.setHeader("Content-Disposition", "inline");
                    OutputStream os = null;
                    try {
                    	IFileStorageCustom fileStorageService = ValleyContextUtil.getBean(YqvalleyClassConfig.FILE_STORAGE_BEAN_NAME,IFileStorageCustom.class);
                    	byte[] bytes = fileStorageService.downloadFile(fileInfo);
                    	if(bytes!=null && bytes.length>0) {
	                    	os = response.getOutputStream();
	        	            os.write(bytes);
                    	}
            	    } catch (Exception e){
            	    	logger.info("获取用户图像失败");
            	    } finally {
						try {
							if(os != null) {
								os.close();
							}
						} catch (Exception e2) {
							logger.info("OutputStream关闭失败");
						}
					}
    	        }
            }
    	}
    }
	
	/**
	 * 获取header信息
	 * @return
	 */
	@ModelLoginVerify
	@RequestMapping(value = "/header",method=RequestMethod.GET)
	public ModelHeaderEntity getUserHeaderInfo() {
		ModelHeaderEntity header = new ModelHeaderEntity();
		try {
			ModelUserEntity userSimple = ModelRequestContextUtil.getCurrentUser();
			header.setUserData(userSimple);
			if(header!=null && header.getUserData()!=null && StringUtils.isNotEmpty(header.getUserData().getUserAccount())) {
				Map<String, Object> otherMap = new HashMap<String, Object>();
				Map<String,List<PublicSelectItemEntity>> selectMap = ValleyConfigUtil.getSelectConfigMap("Y");
				if(CollectionUtils.isNotEmpty(selectMap.get("CG_CONTRACT_TYPE"))) {
					otherMap.put("contractTypeList", selectMap.get("CG_CONTRACT_TYPE"));
				}
				if(CollectionUtils.isNotEmpty(selectMap.get("CG_COMPLIANCE_TYPE"))) {
					List<PublicSelectItemEntity> complianceTypeList = selectMap.get("CG_COMPLIANCE_TYPE");
					if(CollectionUtils.isNotEmpty(complianceTypeList)) {
						for (PublicSelectItemEntity selectItem: complianceTypeList) {
							if(StringUtils.isNotEmpty(selectItem.getExpandValue1())) {
								selectItem.setExpandValue1(ValleyConfigUtil.getAppConfigValue(selectItem.getExpandValue1()));
							} else {
								selectItem.setExpandValue1(ValleyConfigUtil.getAppConfigValue(DEFAULT_TOOL_PROMPT));
							}
							if(StringUtils.isNotEmpty(selectItem.getExpandValue2())) {
								selectItem.setExpandValue2(ValleyConfigUtil.getAppConfigValue(selectItem.getExpandValue2()));
							} else {
								selectItem.setExpandValue2(ValleyConfigUtil.getAppConfigValue(DEFAULT_TOOL_FILE_UPLOAD_PROMPT));
							}
							if(StringUtils.isNotEmpty(selectItem.getExpandValue3())) {
								selectItem.setExpandValue3(ValleyConfigUtil.getAppConfigValue(selectItem.getExpandValue3()));
							} else {
								selectItem.setExpandValue3(ValleyConfigUtil.getAppConfigValue(DEFAULT_TOOL_FILE_UPLOAD_TYPE));
							}
						}
					}
					otherMap.put("complianceTypeList", complianceTypeList);
				}
				List<ModelConfigEntity> modelList = ComplianceServiceUtil.getModelConfigList();
				if(CollectionUtils.isNotEmpty(modelList)) {
					otherMap.put("modelList", modelList);
				}
				header.setOtherMap(otherMap);
			}else {
				throw new Valley403Exception();
			}
		} catch (Exception e) {
			throw new Valley403Exception();
		}
		return header;
	}

}

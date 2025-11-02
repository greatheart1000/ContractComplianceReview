package com.isoftstone.compliance.common.util;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.isoftstone.compliance.common.constant.ComplianceCommonConstant;
import com.isoftstone.compliance.config.model.ModelUserEntity;
import com.ljh.yqvalley.ValleyConfig;
import com.ljh.yqvalley.common.constant.ValleyCConstant;
import com.ljh.yqvalley.common.util.RedisHelpUtil;
import com.ljh.yqvalley.common.util.ValleyContextUtil;
import com.ljh.yqvalley.common.util.ValleyHelpUtil;
import com.ljh.yqvalley.config.util.ValleyConfigUtil;
import com.ljh.yqvalley.core.model.ErrorResult;
import com.ljh.yqvalley.user.util.JwtUtil;
import com.ljh.yqvalley.user.util.LoginUtil;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ModelRequestContextUtil {
	
	private static Logger logger = LoggerFactory.getLogger(ModelRequestContextUtil.class);
	
	public static final String MODEL_TOKEN_KEY = "model_token_";
	
	/**
	 * 获取Request
	 * @return
	 */
	public static HttpServletRequest getRequest() {
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = null;
		if(requestAttributes!=null) {
    		request = requestAttributes.getRequest();
    	}
		return request;
	}
	
	/**
	 * 获取请求Sessin
	 * @return
	 */
	public static HttpSession getSession() {
		HttpSession sessin=null;
		HttpServletRequest request = getRequest();
		if(null != request) {
			sessin =request.getSession();
		}
		return sessin;
	}
	
	/**
	 * 删除sessin
	 * @param name
	 */
	public static void removeSession(String name) {
		HttpSession sessin = getSession();
		if(sessin!=null) {
			sessin.removeAttribute(name);
		}
	}
	
	/**
	 * 写入sessin
	 * @param name
	 * @param value
	 */
	public static void setSession(String name,String value) {
		HttpSession sessin = getSession();
		if(sessin!=null) {
			sessin.setAttribute(name, value);
		}
	}
	
	/**
	 * 从Session获取Token
	 * @return
	 */
	public static String getSessionToken() {
		String token = null;
		try {
			HttpSession session = getSession();
			if(session!=null) {
				token = (String)session.getAttribute(LoginUtil.HEADER_TOKEN);
			}
		} catch (Exception e) {
			logger.info("获取SessionToken失败");
		}
	    return token;
	}
	
	/**
	 * 获取登陆用户缓存key
	 * @param token
	 * @return
	 */
	public static String getLoginUserCacheKey(String token) {
		String cacheKey = null;
		if(StringUtils.isNotEmpty(token)) {
			try {
	        	String jwtSecret = ValleyConfigUtil.getAppConfigValue("valley_security_jwt_secret","xY3oWKpTtKzqyN2pBvYPkV5GJdGhFigGmkj2dZ6F");
	        	Claims claims = JwtUtil.parseJwt(token, jwtSecret);
		        if(claims!=null) {
		        	cacheKey=(String)claims.get(JwtUtil.VALLEY_JWT_CLAIM_ACCOUNT_KEY);
		        	String jwtStr[] = token.split("\\.");
		        	cacheKey+=ValleyCConstant.TOKEN_KEY_EXCISION+jwtStr[jwtStr.length-1];
		        }
		        if(StringUtils.isNotEmpty(cacheKey)) {cacheKey=MODEL_TOKEN_KEY+cacheKey;}
		        ModelUserEntity user = (ModelUserEntity)RedisHelpUtil.getValue(cacheKey);
	        	if(user==null) {cacheKey=null;}
	        } catch (final Exception e) {
	        	logger.info("获取登陆用户缓存失败");
	        }
		}
        return cacheKey;
	}
	
	/**
	 * 获取用户缓存key
	 * @param valleyAccount
	 * @return
	 */
	public static Set<String> getUserCacheKey(String userAccount) {
		ValleyConfig valleyConfig = ValleyContextUtil.getBean(ValleyConfig.class);
		String redisKey = valleyConfig.getRedisKey()+MODEL_TOKEN_KEY+userAccount+ValleyCConstant.TOKEN_KEY_EXCISION;
        return RedisHelpUtil.getMatchKeys(redisKey);
	}
	
	/**
	 * 获取当前用户
	 * @return
	 */	
	public static ModelUserEntity getCurrentUser() {
		String cacheKey = null;
		ModelUserEntity user = null;
        try {
        	String token = getSessionToken();
        	cacheKey = getLoginUserCacheKey(token);
        } catch (final Exception e) {
        	logger.info("获取当前用户失败");
        }
        if(StringUtils.isNotEmpty(cacheKey)) {
        	user = (ModelUserEntity)RedisHelpUtil.getValue(cacheKey);
        }
        return user;
	}
	
	/**
	 * 获取Token用户
	 * @param token
	 * @return
	 */
	public static ModelUserEntity getTokenUser(String token) {
		String cacheKey = null;
		ModelUserEntity user = null;
		try {
        	cacheKey = getLoginUserCacheKey(token);
        } catch (final Exception e) {
        	logger.info("获取当前用户失败");
        }
        if(StringUtils.isNotEmpty(cacheKey)) {
        	user = (ModelUserEntity)RedisHelpUtil.getValue(cacheKey);
        }
        return user;
	}
	
	/**
	 * 获取当前用户账号
	 * @return
	 */
	public static String getCurrentAccount() {
		String userAccount = null;
		ModelUserEntity user = getCurrentUser();
		if(null != user) {
			userAccount=user.getUserAccount();
		}
		return userAccount;
	}
	
	/**
	 * 获取当前用户账号
	 * @return
	 */
	public static String getCurrentAccount(ModelUserEntity user) {
		if(user==null) {user = getCurrentUser();}
		String valleyAccount = null;
		if(null != user) {
			valleyAccount=user.getUserAccount();
		}
		return valleyAccount;
	}
	
	/**
	 * 获取当前用户姓名
	 * @return
	 */
	public static String getCurrentUserName() {
		String userName = null;
		ModelUserEntity user = getCurrentUser();
		if(null != user) {
			userName=user.getUserName();
		}
		return userName;
	}
	
	/**
	 * 验证Session登陆
	 * @param response
	 */
	public static ModelUserEntity verifySessionLogin(HttpServletResponse response) {
		ModelUserEntity user = getCurrentUser();
		if(user!=null && StringUtils.isNotEmpty(user.getUserAccount())) {
			try {
				ErrorResult result = new ErrorResult();
				result.setCode(HttpServletResponse.SC_UNAUTHORIZED);
				result.setMessage("当前用户未登陆!");
				ValleyHelpUtil.writerResponse(response, result);
			} catch (Exception e) {
				logger.info("验证登录失败");
			}
		}else {
			user = null;
		}
		return user;
	}
	
	/**
	 * 从Cookie获取值
	 * @return
	 */
	public static String getCookieValue(String key) {
		if(StringUtils.isEmpty(key)) {return null;}
		HttpServletRequest request = getRequest();
		String token = null;
		Cookie[] cookies = request.getCookies();
		if(cookies!=null && cookies.length>0) {
			for(Cookie c :cookies ){
		    	if(key.equals(c.getName())) {
		    		token = c.getValue();
		    	}
		    }
		}
	    return token;
	}
	
	/**
	 * 更新用户登陆缓存
	 * @param user
	 */
	public static void updateUserCache(ModelUserEntity user) {
		try {
			if(user==null) {
				user = getCurrentUser();
			}
			if(user!=null && StringUtils.isNotEmpty(user.getUserAccount())) {
				String token = getSessionToken();
				if(StringUtils.isNotEmpty(token)) {
					String cacheKey = getLoginUserCacheKey(token);
					if(StringUtils.isNotEmpty(cacheKey)) {
						RedisHelpUtil.setValue(cacheKey, user, RedisHelpUtil.second(ComplianceCommonConstant.MODEL_USER_LOGIN_EXPIRATION));
					}
				}
			}
		} catch (Exception e) {
			logger.error("修改用户缓存失败:",e);
		}
	}
	
	/**
	 * 更新用户登陆缓存
	 */
	public static void updateLoginCacheTime(String valleyAccount) {
		try {
			String token = getSessionToken();
			if(StringUtils.isNotEmpty(token)) {
		        String[] jwtStr = token.split("\\.");
		        StringBuilder sb = new StringBuilder();
		        sb.append(MODEL_TOKEN_KEY);
		        sb.append(valleyAccount);
		        sb.append(ValleyCConstant.TOKEN_KEY_EXCISION);
		        sb.append(jwtStr[jwtStr.length-1]);
				if(StringUtils.isNotEmpty(sb.toString())) {
					RedisHelpUtil.updateTime(sb.toString(), RedisHelpUtil.second(ComplianceCommonConstant.MODEL_USER_LOGIN_EXPIRATION));
				}
			}
		} catch (Exception e) {
			logger.error("更新用户缓存失败:",e);
		}
	}

}

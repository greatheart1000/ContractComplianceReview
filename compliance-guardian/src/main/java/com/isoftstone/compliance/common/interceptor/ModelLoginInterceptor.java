package com.isoftstone.compliance.common.interceptor;

import org.apache.groovy.parser.antlr4.util.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.isoftstone.compliance.common.util.ModelRequestContextUtil;
import com.isoftstone.compliance.config.model.ModelUserEntity;
import com.ljh.yqvalley.core.exception.Valley401Exception;

/**
 * 模型登录拦截
 */
@Component
@Aspect
public class ModelLoginInterceptor {
	
	/**
	 * 登录拦截切入点
	 */
	@Pointcut("@annotation(com.isoftstone.compliance.common.annotation.ModelLoginVerify)")
    public void modelLoginVerify(){}
	
	@Before("modelLoginVerify()")
	public void invoke(JoinPoint joinPoint) {
		//验证是否登录
		ModelUserEntity user = ModelRequestContextUtil.getCurrentUser();
		if(user==null || StringUtils.isEmpty(user.getUserAccount())) {
			throw new Valley401Exception();
		}
	}

}

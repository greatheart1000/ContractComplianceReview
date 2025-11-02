package com.isoftstone.compliance.config.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.isoftstone.compliance.common.constant.ComplianceCommonConstant;
import com.isoftstone.compliance.config.dao.IModelUserManageMapper;
import com.isoftstone.compliance.config.model.ModelUserEntity;
import com.isoftstone.compliance.config.model.ModelUserImportEntity;
import com.ljh.yqvalley.common.util.DataEncryptUtil;
import com.ljh.yqvalley.common.util.ValleyHelpUtil;
import com.ljh.yqvalley.core.exception.ValleyException;
import com.ljh.yqvalley.file.model.ImportExcelResultEntity;
import com.ljh.yqvalley.file.model.ImportParamEntity;
import com.ljh.yqvalley.file.service.IExcelImportConsumer;
import com.ljh.yqvalley.user.dao.IValleyDeptMapper;
import com.ljh.yqvalley.user.model.ValleyDeptEntity;
import com.ljh.yqvalley.user.util.PasswordUtil;
import com.ljh.yqvalley.user.util.ValleyUserUtil;

import jakarta.annotation.Resource;

@Service
public class ModelUserImportConsumer implements IExcelImportConsumer {
	
	private static Logger logger = LoggerFactory.getLogger(ModelUserImportConsumer.class);

	@Resource
    private IModelUserManageMapper modelUserManageMapper;
	@Resource
	private IValleyDeptMapper valleyDeptMapper;

    @Override
    public ImportExcelResultEntity batchData(ImportParamEntity param,Map<String, List<HashMap<String, String>>> dataMap) {
    	ImportExcelResultEntity result = new ImportExcelResultEntity();
        int successCount = 0;
        int idx = 0;
        String errorMsg = "";
        List<HashMap<String, String>> list = dataMap.get("userInfo");
        if (list != null && list.size() > 0) {
        	Map<String,String> roleMap = ValleyUserUtil.getRoleNameMap(false);
            for (HashMap<String, String> hashMap : list) {
                idx++;
                try {
                	ModelUserImportEntity userImport = new ModelUserImportEntity();
                    ValleyHelpUtil.mapToObject(userImport, hashMap);
                    //验证数据有效性(数据转换)
                    ModelUserEntity user = verifyAndConvertData(userImport, result,roleMap);
                    if (StringUtils.isAllEmpty(result.getErrorMsg())) {
                        try {
                        	user.setAccountStatus("Y");
                        	if(StringUtils.isEmpty(user.getAccountType())) {
                    			user.setAccountType(ComplianceCommonConstant.MODEL_USER_TYPE_DEFAULT);
                    		}
                    		user.setCreateAccount(param.getValleyAccount());
                    		//生成默认密码
                    		String loginPwd = PasswordUtil.getRandomPassword(12, 3);
                    		user.setLoginPwd(PasswordUtil.encryption(loginPwd));
                    		user.setDefaultLoginPwd(DataEncryptUtil.encryptString(loginPwd));
                    		modelUserManageMapper.insertModelUserInfo(user);
                            successCount++;
                        } catch (ValleyException e) {
                            errorMsg += ("第【" + idx + "】行数据错误:" + e.getMessage() + ";\r\n");
                        }
                    } else {
                        errorMsg += ("第【" + idx + "】行数据错误:" + result.getErrorMsg() + ";\r\n");
                        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    }
                } catch (Exception e) {
                    errorMsg += "第【" + idx + "】行数据导入失败;\r\n";
                    logger.error("导入失败");
                }
            }
            ValleyUserUtil.clearAllUserMap();
        } else {
            errorMsg = "找不到数据";
        }
        if(StringUtils.isNotEmpty(errorMsg)){
            successCount=0;
        }
        result.setSuccessCount(successCount);
        result.setErrorMsg(errorMsg);
        return result;
    }

    /**
     * 验证并转换数据
     * @param userImport
     * @param result
     */
    private ModelUserEntity verifyAndConvertData(ModelUserImportEntity userImport, ImportExcelResultEntity result,Map<String,String> roleMap) {
        String errorMsg="";
        ModelUserEntity user = new ModelUserEntity();
        try {
        	userImport.setUserAccount(userImport.getUserAccount().trim());
    		if(!userImport.getUserAccount().matches("[0-9A-Za-z_]*")) {
    			errorMsg += ("用户账号只能是大小写字母、数字和下划线;");
    		}
    		if(userImport.getUserAccount().length()<4 || userImport.getUserAccount().length()>20) {
    			errorMsg += ("用户账号长度只能是4-20位;");
    		}
    		if (StringUtils.isNotEmpty(userImport.getUserName()) && userImport.getUserName().getBytes("UTF-8").length > 50) {
                errorMsg += ("用户姓名超出长度限制;");
            }
            if (StringUtils.isNotEmpty(userImport.getMailAddress()) && userImport.getMailAddress().getBytes("UTF-8").length > 50) {
                errorMsg += ("电子邮箱超出长度限制;");
            }
            if (StringUtils.isNotEmpty(userImport.getPhoneNumber()) && userImport.getPhoneNumber().getBytes("UTF-8").length > 50) {
                errorMsg += ("手机号码超出长度限制;");
            }
    		user.setUserAccount(userImport.getUserAccount());
    		user.setMailAddress(userImport.getMailAddress());
    		user.setPhoneNumber(userImport.getPhoneNumber());
            //校验唯一性
    		int repeatNum = modelUserManageMapper.findModelUserCount(user,null);
    		if(repeatNum>0) {
    			errorMsg += ("有【登录账号or用户账号or用户邮箱or手机号码】相同的用户已存在;");
    		}
    		user.setUserName(userImport.getUserName());
    		//出门用户类型
    		String accountType = ValleyHelpUtil.getSubstringValue(userImport.getAccountType(),"(",")");
    		user.setAccountType(accountType);
    		String deptCode = ValleyHelpUtil.getSubstringValue(userImport.getDeptCode(),"(",")");
    		//处理部门
    		if(StringUtils.isNotEmpty(deptCode)) {
    			ValleyDeptEntity dept = valleyDeptMapper.findValleyDeptInfo(deptCode,"Y");
        		if(dept!=null) {
        			user.setDeptCode(deptCode);
        			user.setLevel1DeptCode(dept.getLevel1Code());
        			user.setLevel2DeptCode(dept.getLevel2Code());
        			user.setLevel3DeptCode(dept.getLevel3Code());
        			user.setLevel4DeptCode(dept.getLevel4Code());
        		}else {
        			errorMsg += ("部门信息不存在或已失效;");
        		}
    		}
        } catch (Exception e) {
        	logger.error("用户导入失败:",e);
        }
        result.setErrorMsg(errorMsg);
        return user;
    }

}

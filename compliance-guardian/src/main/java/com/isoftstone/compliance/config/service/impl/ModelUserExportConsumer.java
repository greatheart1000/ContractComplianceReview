package com.isoftstone.compliance.config.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.isoftstone.compliance.common.util.ComplianceServiceUtil;
import com.isoftstone.compliance.config.model.ModelUserEntity;
import com.isoftstone.compliance.config.service.IModelUserManageService;
import com.ljh.yqvalley.config.util.ValleyConfigUtil;
import com.ljh.yqvalley.core.model.PageEntity;
import com.ljh.yqvalley.core.model.PageResult;
import com.ljh.yqvalley.file.model.ExcelHeadEntity;
import com.ljh.yqvalley.file.model.ExcelHeadEntity.FieldType;
import com.ljh.yqvalley.file.model.ExportExcelResultEntity;
import com.ljh.yqvalley.file.service.IExcelExportConsumer;

import jakarta.annotation.Resource;

@Service
public class ModelUserExportConsumer implements IExcelExportConsumer {
	
	@Resource
	private IModelUserManageService modelUserManageService;

	@Override
	public ExportExcelResultEntity obtainData(Map<String, Object> data) {
		ExportExcelResultEntity result = new ExportExcelResultEntity();
		Map<String, Object> map = (Map<String, Object>) data;
		Object conditionObj = map.get("condition");
		Object pageObj = map.get("page");
		if (conditionObj instanceof ModelUserEntity) {
			ModelUserEntity condition = (ModelUserEntity)conditionObj;
			PageEntity page = (PageEntity)pageObj;
			try {
				PageResult<ModelUserEntity> userResult = modelUserManageService.findModelUserByPage(condition,page);
				if(CollectionUtils.isNotEmpty(userResult.getResult())) {
					Map<String,String> accountTypeMap = ValleyConfigUtil.getSelectItemNameMap("CG_MODEL_ACCOUNT_TYPE");
					for (ModelUserEntity user : userResult.getResult()) {
						user.setCompleteName(ComplianceServiceUtil.getCompleteName(user));
						if("Y".equals(user.getAccountStatus())) {
							user.setAccountStatus("启用");
						}else {
							user.setAccountStatus("禁用");
						}
						user.setAccountType(StringUtils.isNotEmpty(accountTypeMap.get(user.getAccountType()))?accountTypeMap.get(user.getAccountType()):user.getAccountType());
					}
					result.setTotalPage(userResult.getPage().getTotalPage());
					result.setTotalRows(userResult.getPage().getTotalRows());
				}
				result.setRows(userResult.getResult());
			} catch (Exception e) {
				result.setErrorMsg(e.getMessage());
			}
		}
		return result;
	}

	@Override
	public List<ExcelHeadEntity> getHeadList() {
		List<ExcelHeadEntity> heads = new ArrayList<ExcelHeadEntity>();
		heads.add(new ExcelHeadEntity("userAccount", "用户账号", FieldType.string));
		heads.add(new ExcelHeadEntity("userName", "用户姓名", FieldType.string));
		heads.add(new ExcelHeadEntity("phoneNumber", "手机号码", FieldType.string));
		heads.add(new ExcelHeadEntity("mailAddress", "联系邮箱", FieldType.string));
		heads.add(new ExcelHeadEntity("accountType", "账号类型", FieldType.string));
		heads.add(new ExcelHeadEntity("accountStatus", "用户状态", FieldType.string));
		heads.add(new ExcelHeadEntity("completeName", "所属部门", FieldType.string));
		return heads;
	}

}

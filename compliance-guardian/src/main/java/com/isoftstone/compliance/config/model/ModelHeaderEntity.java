package com.isoftstone.compliance.config.model;

import java.io.Serializable;
import java.util.Map;

public class ModelHeaderEntity implements Serializable {
	
	private static final long serialVersionUID = 1304041046355177204L;
	
	private ModelUserEntity userData;//用户信息
    private Map<String,Object> otherMap;//其他信息
    
	public ModelUserEntity getUserData() {
		return userData;
	}
	public void setUserData(ModelUserEntity userData) {
		this.userData = userData;
	}
	public Map<String, Object> getOtherMap() {
		return otherMap;
	}
	public void setOtherMap(Map<String, Object> otherMap) {
		this.otherMap = otherMap;
	}
	
}

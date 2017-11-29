package com.pralay.configuration.model;

public class DownTimeRulePojo extends RulePojo {
	
	public DownTimeRulePojo() {
		super();
		// TODO Auto-generated constructor stub
	}
	private String primaryAlarmIds="";
	private String correlatorAlarmIds = "";
	private String joinConditions = "";
	
	public String getRulename() {
		return rulename;
	}
	public void setRulename(String rulename) {
		this.rulename = rulename;
	}
	public String getPrimaryAlarmIds() {
		return primaryAlarmIds;
	}
	public void setPrimaryAlarmIds(String primaryAlarmIds) {
		this.primaryAlarmIds = primaryAlarmIds;
	}
	public String getCorrelatorAlarmIds() {
		return correlatorAlarmIds;
	}
	public void setCorrelatorAlarmIds(String correlatorAlarmIds) {
		this.correlatorAlarmIds = correlatorAlarmIds;
	}
	public String getJoinConditions() {
		return joinConditions;
	}
	public void setJoinConditions(String joinConditions) {
		this.joinConditions = joinConditions;
	}
	public String getIsProcessed() {
		return isProcessed;
	}
	public void setIsProcessed(String isProcessed) {
		this.isProcessed = isProcessed;
	}
		

}

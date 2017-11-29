package com.pralay.configuration.model;

public class AlarmIdPojo {
	
	private String message_text;
	private String alarm_id;
	private String functional_alarm_id;
	private String impact;
	private String resolution;
	
	public String getMessage_text() {
		return message_text;
	}
	public void setMessage_text(String message_text) {
		this.message_text = message_text;
	}
	public String getAlarm_id() {
		return alarm_id;
	}
	public void setAlarm_id(String alarm_id) {
		this.alarm_id = alarm_id;
	}
	public String getFunctional_alarm_id() {
		return functional_alarm_id;
	}
	public void setFunctional_alarm_id(String functional_alarm_id) {
		this.functional_alarm_id = functional_alarm_id;
	}
	public String getImpact() {
		return impact;
	}
	public void setImpact(String impact) {
		this.impact = impact;
	}
	public String getResolution() {
		return resolution;
	}
	public void setResolution(String resolution) {
		this.resolution = resolution;
	}
	
}

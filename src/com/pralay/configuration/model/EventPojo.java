package com.pralay.configuration.model;

public class EventPojo {

	
	String id;
	String aid_alarm_id;
	String functional_aid_alarm_id;
	String impact_alarm_id;
	String resolution_alarm_id;
	String funchostname;
	String eventdate;
	String objectclass;
	String object;
	String parameter;
	String parametervalue;
	String severity;
	String status;
	String message;
	String duration;
	String scomval;
	String nprioval;
	String laval;
	String zhval;
	String sarioval;
	String saridleval;
	String sarloadavgval;
	String openalertwarnval;
	String openalertcritval;
	
	public EventPojo(String id, String aid_alarm_id, String functional_aid_alarm_id, String impact_alarm_id , String resolution_alarm_id, String funchostname, String eventdate, String objectclass, String object, String parameter, String parametervalue,
			String severity, String status, String message, String duration, String scomval, String nprioval, String laval, String zhval, String sarioval, String saridleval,
			String sarloadavgval, String openalertwarnval, String openalertcritval){
		
		this.id = id;
		this.aid_alarm_id = aid_alarm_id;
		this.functional_aid_alarm_id = functional_aid_alarm_id;
		this.funchostname = funchostname;
		this.impact_alarm_id = impact_alarm_id;
		this.resolution_alarm_id = resolution_alarm_id;
		this.eventdate = eventdate;
		this.objectclass = objectclass;
		this.object = object;
		this.parameter = parameter;
		this.parametervalue = parametervalue;
		this.severity = severity;
		this.status = status;
		this.message = message;
		this.duration = duration;
		this.scomval = scomval;
		this.nprioval = nprioval;
		this.laval = laval;
		this.zhval = zhval;
		this.sarioval = sarioval;
		this.saridleval = saridleval;
		this.sarloadavgval = sarloadavgval;
		this.openalertwarnval = openalertwarnval;
		this.openalertcritval = openalertcritval;
	}

	

	public String getId() {
		return id;
	}

	public String getAid_alarm_id() {
		return aid_alarm_id;
	}

	public String getFunchostname() {
		return funchostname;
	}

	public String getEventdate() {
		return eventdate;
	}

	public String getObjectclass() {
		return objectclass;
	}

	public String getObject() {
		return object;
	}

	public String getParameter() {
		return parameter;
	}

	public String getParametervalue() {
		return parametervalue;
	}

	public String getSeverity() {
		return severity;
	}

	public String getStatus() {
		return status;
	}

	public String getMessage() {
		return message;
	}

	public String getDuration() {
		return duration;
	}
	
	public String getFunctional_aid_alarm_id() {
		return functional_aid_alarm_id;
	}

    
	
	public String getScomval() {
		return scomval;
	}



	public String getNprioval() {
		return nprioval;
	}



	public String getLaval() {
		return laval;
	}



	public String getZhval() {
		return zhval;
	}

	


	public String getSarioval() {
		return sarioval;
	}



	public String getSaridleval() {
		return saridleval;
	}



	public String getSarloadavgval() {
		return sarloadavgval;
	}



	public String getOpenalertwarnval() {
		return openalertwarnval;
	}


	public String getOpenalertcritval() {
		return openalertcritval;
	}
	
	public String getImpact_alarm_id() {
		return impact_alarm_id;
	}



	public void setImpact_alarm_id(String impact_alarm_id) {
		this.impact_alarm_id = impact_alarm_id;
	}



	public String getResolution_alarm_id() {
		return resolution_alarm_id;
	}



	public void setResolution_alarm_id(String resolution_alarm_id) {
		this.resolution_alarm_id = resolution_alarm_id;
	}


	@Override
	public String toString(){
		return "Id: "+ getId() +" AlarmId: " + getAid_alarm_id() +" FunctionalAlarmId: "+ getFunctional_aid_alarm_id() +" Host: "+ getFunchostname() 
		+ " Severity: "+ getSeverity() + " Date: " +getEventdate() + " Message: " + getMessage() +" Scom: " + getScomval() + " Nprio: "+ getNprioval()
		+" La: "+ getLaval() +" Zh: "+ getZhval() + " SarIO: " + getSarioval() + " SarIdle: " + getSaridleval() + " SarLoad: " + getSarloadavgval()
		+" OpenAlertWarn: "+ getOpenalertwarnval() + " OpenAlertCrit: " + getOpenalertcritval();
	}
}

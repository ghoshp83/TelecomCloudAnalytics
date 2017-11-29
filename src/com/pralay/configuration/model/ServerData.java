package com.pralay.configuration.model;

import java.util.Arrays;
import java.util.Map;

public class ServerData {
    private String name;
    private String inurl;
    private String inuserId;
    private String inpassword;
    private String indriverDB;
    private String outurl;
    private String outuserId;
    private String outpassword;
    private String outdriverDB;
    private String rawtruncatesql;
    private String insertrawsql;
    private String fetcheventsql;
    private String fetchalarmsql;
    private String fetchhostsql;
    private String checkalarmsql;
    private String insertalarmsql;
    private String updatealarmsql;
    private int numberOfInstances;
    private InputType type;
    private short interval;
    private boolean hasHeader;
    private boolean sendMsgToMsgBus;
    private String streamName;
    private String streamType; // Must be JSON or Byte
    private String output;
    private String xsdPath;
    private String xPath;
    private Map<String, String> mappingInfo;
    private String[] headers;
    private String dimName;
    private String timeStampName = "Timestamp";
    private String componentName = "Component";
    private String moName = "EquipmentId";
    private long timer;
    private String applicationName;
    private String processName;
    private boolean outputRequired;
    private boolean sendDataToAvro;
    private boolean sendDataToHDFS;
    private String DefColTrail;
    private String DisConGFSTrail;
    private String UnsynGFSTrail;
    private String ScomColTrail;
    private String NprioColTrail;
    private String LAColTrail;
    private String ZHColTrail;
    private String SwtColTrail;
    private String SariowaitColTrail;
    private String SaridleColTrail;
    private String SarloadavgColTrail;
    private String OpenalertwarnColTrail;
    private String OpenalertcritColTrail;
    private String AlarmAgentC;
    private String AlarmAgentNTR;
    private String AlarmAgentPA;
    private String AlarmSeverityC;
    private String AlarmSeverityW;
    private String ConnGFSMatchingText;
    private String DisConnGFSMatchingText;
    private String UnsynGFSMatchingText;
    private String UnsynGFSMatchingTextFull;
    private String SarIoWaitMatchingText;
    private String SarIdleMatchingText;
    private String SarLoadAvgMatchingText;
    private String OpenalertMatchingText;
    private String ScomMatchingText;
    private String LAMatchingText;
    private String ZHMatchingText;
    private String SwtMatchingText;
    private String SwtMiscInfo;
    private String NonMatchingAlarmId;
   
    private String downtimerulexmlname;
    private String downtimerulexmlrootnodepath;
    private String uptimerulexmlname;
    private String uptimerulexmlrootnodepath;
    private String UptimeAlarmAgentPA;
    private String actuationhost;
    private String actuationport;
    private String actuationusername;
    private String actuationpassword;
    private String actuationfrommailid;
    private String actuationtomailid;
    private String actuationsubjectpart1;
    private String actuationsubjectpart2;
    private String actuationsubjectuptime;
    private String espereventname;
    private String fetchlasteventmysql;
    private String fetchlasteventoracle;
    private String checkdatapresenceinnormalize;
    private String allrulexmlname;
    private String allrulexmlrootnodepath;
    private String downtimejoincondition;
    private String uptimeruleuniqueidentifier;
    private String watchdirfilename;
    private String shadowdirfilename;
    private String CheckColPresenceNorm;
    private String NonMatchingAlarmIdCol;
    private String SelectDowntimeRuleDetails;
    private String UpdateDowntimeRuleDetails;
    
    public ServerData() {
    }

    public ServerData(String name, String inurl,String outurl, int numberOfInstances, InputType type, short interval,
            boolean hasHeader, String streamName, String output, String xsdPath, String xPath,
            Map<String, String> mappingInfo, String[] headers, String dimName, String timeStampName,
            String componentName) {
        this.name = name;
        this.inurl = inurl;
        this.outurl = outurl;
        this.numberOfInstances = numberOfInstances;
        this.type = type;
        this.interval = interval;
        this.hasHeader = hasHeader;
        this.streamName = streamName;
        this.output = output;
        this.xsdPath = xsdPath;
        this.xPath = xPath;
        this.mappingInfo = mappingInfo;
        this.headers = headers;
        this.dimName = dimName;
        this.timeStampName = timeStampName;
        this.componentName = componentName;
    }

    public enum InputType {
        JSON, CSV, XML, HTML, CUSTOM, SQL, SQLI
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    

    public int getNumberOfInstances() {
        return numberOfInstances;
    }

    public void setNumberOfInstances(int numberOfInstances) {
        this.numberOfInstances = numberOfInstances;
    }

    public InputType getType() {
        return type;
    }

    public void setType(InputType type) {
        this.type = type;
    }

    public boolean isHasHeader() {
        return hasHeader;
    }

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }

    public short getInterval() {
        return interval;
    }

    public void setInterval(short interval) {
        this.interval = interval;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getXsdPath() {
        return xsdPath;
    }

    public void setXsdPath(String xsdPath) {
        this.xsdPath = xsdPath;
    }

    public String getxPath() {
        return xPath;
    }

    public void setxPath(String xPath) {
        this.xPath = xPath;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }

    public Map<String, String> getMappingInfo() {
        return mappingInfo;
    }

    public void setMappingInfo(Map<String, String> mappingInfo) {
        this.mappingInfo = mappingInfo;
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }

    public String getDimName() {
        return dimName;
    }

    public void setDimName(String dimName) {
        this.dimName = dimName;
    }

    public String getTimeStampName() {
        return timeStampName;
    }

    public void setTimeStampName(String timeStampName) {
        this.timeStampName = timeStampName;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public String getMoName() {
        return moName;
    }

    public void setMoName(String moName) {
        this.moName = moName;
    }

    

	public long getTimer() {
        return timer;
    }

    public void setTimer(long timer) {
        this.timer = timer;
    }

    public String getInDriverDB() {
        return indriverDB;
    }
    public void setInDriverDB(String indriverDB) {
        this.indriverDB = indriverDB;
    }
    public String getOutDriverDB() {
        return outdriverDB;
    }
    public void setOutDriverDB(String outdriverDB) {
        this.outdriverDB = outdriverDB;
    }
    
    public boolean isOutputRequired() {
        return outputRequired;
    }

    public void setOutputRequired(boolean outputRequired) {
        this.outputRequired = outputRequired;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public boolean isSendMsgToMsgBus() {
        return sendMsgToMsgBus;
    }

    public void setSendMsgToMsgBus(boolean sendMsgToMsgBus) {
        this.sendMsgToMsgBus = sendMsgToMsgBus;
    }

    public String getStreamType() {
        return streamType;
    }

    public void setStreamType(String streamType) {
        this.streamType = streamType;
    }

    public void setSendDataToAvro(boolean sendDataToAvro) {
        this.sendDataToAvro = sendDataToAvro;
    }

    public boolean isSendDataToAvro() {
        return this.sendDataToAvro;
    }

    public void setSendDataToHDFS(boolean sendDataToHDFS) {
        this.sendDataToHDFS = sendDataToHDFS;
    }

    public boolean isSendDataToHDFS() {
        return this.sendDataToHDFS;
    }

    
    public String getInurl() {
		return inurl;
	}

	public void setInurl(String inurl) {
		this.inurl = inurl;
	}

	public String getInuserId() {
		return inuserId;
	}

	public void setInuserId(String inuserId) {
		this.inuserId = inuserId;
	}

	public String getInpassword() {
		return inpassword;
	}

	public void setInpassword(String inpassword) {
		this.inpassword = inpassword;
	}

	public String getIndriverDB() {
		return indriverDB;
	}

	public void setIndriverDB(String indriverDB) {
		this.indriverDB = indriverDB;
	}

	public String getOuturl() {
		return outurl;
	}

	public void setOuturl(String outurl) {
		this.outurl = outurl;
	}

	public String getOutuserId() {
		return outuserId;
	}

	public void setOutuserId(String outuserId) {
		this.outuserId = outuserId;
	}

	public String getOutpassword() {
		return outpassword;
	}

	public void setOutpassword(String outpassword) {
		this.outpassword = outpassword;
	}

	public String getOutdriverDB() {
		return outdriverDB;
	}

	public void setOutdriverDB(String outdriverDB) {
		this.outdriverDB = outdriverDB;
	}

	public String getRawtruncatesql() {
		return rawtruncatesql;
	}

	public void setRawtruncatesql(String rawtruncatesql) {
		this.rawtruncatesql = rawtruncatesql;
	}

	public String getInsertrawsql() {
		return insertrawsql;
	}

	public void setInsertrawsql(String insertrawsql) {
		this.insertrawsql = insertrawsql;
	}

	public String getFetcheventsql() {
		return fetcheventsql;
	}

	public void setFetcheventsql(String fetcheventsql) {
		this.fetcheventsql = fetcheventsql;
	}

	public String getFetchalarmsql() {
		return fetchalarmsql;
	}

	public void setFetchalarmsql(String fetchalarmsql) {
		this.fetchalarmsql = fetchalarmsql;
	}

	public String getFetchhostsql() {
		return fetchhostsql;
	}

	public void setFetchhostsql(String fetchhostsql) {
		this.fetchhostsql = fetchhostsql;
	}

	public String getCheckalarmsql() {
		return checkalarmsql;
	}

	public void setCheckalarmsql(String checkalarmsql) {
		this.checkalarmsql = checkalarmsql;
	}

	public String getInsertalarmsql() {
		return insertalarmsql;
	}

	public void setInsertalarmsql(String insertalarmsql) {
		this.insertalarmsql = insertalarmsql;
	}

	public String getUpdatealarmsql() {
		return updatealarmsql;
	}

	public void setUpdatealarmsql(String updatealarmsql) {
		this.updatealarmsql = updatealarmsql;
	}

	public String getDefColTrail() {
		return DefColTrail;
	}

	public void setDefColTrail(String defColTrail) {
		DefColTrail = defColTrail;
	}

	public String getDisConGFSTrail() {
		return DisConGFSTrail;
	}

	public void setDisConGFSTrail(String disConGFSTrail) {
		DisConGFSTrail = disConGFSTrail;
	}

	public String getUnsynGFSTrail() {
		return UnsynGFSTrail;
	}

	public void setUnsynGFSTrail(String unsynGFSTrail) {
		UnsynGFSTrail = unsynGFSTrail;
	}

	public String getScomColTrail() {
		return ScomColTrail;
	}

	public void setScomColTrail(String scomColTrail) {
		ScomColTrail = scomColTrail;
	}

	public String getNprioColTrail() {
		return NprioColTrail;
	}

	public void setNprioColTrail(String nprioColTrail) {
		NprioColTrail = nprioColTrail;
	}

	public String getLAColTrail() {
		return LAColTrail;
	}

	public void setLAColTrail(String lAColTrail) {
		LAColTrail = lAColTrail;
	}

	public String getZHColTrail() {
		return ZHColTrail;
	}

	public void setZHColTrail(String zHColTrail) {
		ZHColTrail = zHColTrail;
	}

	public String getSwtColTrail() {
		return SwtColTrail;
	}

	public void setSwtColTrail(String swtColTrail) {
		SwtColTrail = swtColTrail;
	}

	public String getAlarmAgentC() {
		return AlarmAgentC;
	}

	public void setAlarmAgentC(String alarmAgentC) {
		AlarmAgentC = alarmAgentC;
	}

	public String getAlarmAgentNTR() {
		return AlarmAgentNTR;
	}

	public void setAlarmAgentNTR(String alarmAgentNTR) {
		AlarmAgentNTR = alarmAgentNTR;
	}

	public String getAlarmAgentPA() {
		return AlarmAgentPA;
	}

	public void setAlarmAgentPA(String alarmAgentPA) {
		AlarmAgentPA = alarmAgentPA;
	}

	public String getAlarmSeverityC() {
		return AlarmSeverityC;
	}

	public void setAlarmSeverityC(String alarmSeverityC) {
		AlarmSeverityC = alarmSeverityC;
	}

	public String getAlarmSeverityW() {
		return AlarmSeverityW;
	}

	public void setAlarmSeverityW(String alarmSeverityW) {
		AlarmSeverityW = alarmSeverityW;
	}

	public String getConnGFSMatchingText() {
		return ConnGFSMatchingText;
	}

	public void setConnGFSMatchingText(String connGFSMatchingText) {
		ConnGFSMatchingText = connGFSMatchingText;
	}

	public String getDisConnGFSMatchingText() {
		return DisConnGFSMatchingText;
	}

	public void setDisConnGFSMatchingText(String disConnGFSMatchingText) {
		DisConnGFSMatchingText = disConnGFSMatchingText;
	}

	public String getUnsynGFSMatchingText() {
		return UnsynGFSMatchingText;
	}

	public void setUnsynGFSMatchingText(String unsynGFSMatchingText) {
		UnsynGFSMatchingText = unsynGFSMatchingText;
	}

	public String getUnsynGFSMatchingTextFull() {
		return UnsynGFSMatchingTextFull;
	}

	public void setUnsynGFSMatchingTextFull(String unsynGFSMatchingTextFull) {
		UnsynGFSMatchingTextFull = unsynGFSMatchingTextFull;
	}

	public String getScomMatchingText() {
		return ScomMatchingText;
	}

	public void setScomMatchingText(String scomMatchingText) {
		ScomMatchingText = scomMatchingText;
	}

	public String getLAMatchingText() {
		return LAMatchingText;
	}

	public void setLAMatchingText(String lAMatchingText) {
		LAMatchingText = lAMatchingText;
	}

	public String getZHMatchingText() {
		return ZHMatchingText;
	}

	public void setZHMatchingText(String zHMatchingText) {
		ZHMatchingText = zHMatchingText;
	}

	public String getSwtMatchingText() {
		return SwtMatchingText;
	}

	public void setSwtMatchingText(String swtMatchingText) {
		SwtMatchingText = swtMatchingText;
	}

	public String getSwtMiscInfo() {
		return SwtMiscInfo;
	}

	public void setSwtMiscInfo(String swtMiscInfo) {
		SwtMiscInfo = swtMiscInfo;
	}

	public String getNonMatchingAlarmId() {
		return NonMatchingAlarmId;
	}

	public void setNonMatchingAlarmId(String nonMatchingAlarmId) {
		NonMatchingAlarmId = nonMatchingAlarmId;
	}

	
	public String getSarIoWaitMatchingText() {
		return SarIoWaitMatchingText;
	}

	public void setSarIoWaitMatchingText(String sarIoWaitMatchingText) {
		SarIoWaitMatchingText = sarIoWaitMatchingText;
	}

	public String getSarIdleMatchingText() {
		return SarIdleMatchingText;
	}

	public void setSarIdleMatchingText(String sarIdleMatchingText) {
		SarIdleMatchingText = sarIdleMatchingText;
	}

	public String getSarLoadAvgMatchingText() {
		return SarLoadAvgMatchingText;
	}

	public void setSarLoadAvgMatchingText(String sarLoadAvgMatchingText) {
		SarLoadAvgMatchingText = sarLoadAvgMatchingText;
	}

	public String getSariowaitColTrail() {
		return SariowaitColTrail;
	}

	public void setSariowaitColTrail(String sariowaitColTrail) {
		SariowaitColTrail = sariowaitColTrail;
	}

	public String getSaridleColTrail() {
		return SaridleColTrail;
	}

	public void setSaridleColTrail(String saridleColTrail) {
		SaridleColTrail = saridleColTrail;
	}

	public String getSarloadavgColTrail() {
		return SarloadavgColTrail;
	}

	public void setSarloadavgColTrail(String sarloadavgColTrail) {
		SarloadavgColTrail = sarloadavgColTrail;
	}
	
	public String getOpenalertwarnColTrail() {
		return OpenalertwarnColTrail;
	}

	public void setOpenalertwarnColTrail(String openalertwarnColTrail) {
		OpenalertwarnColTrail = openalertwarnColTrail;
	}

	public String getOpenalertcritColTrail() {
		return OpenalertcritColTrail;
	}

	public void setOpenalertcritColTrail(String openalertcritColTrail) {
		OpenalertcritColTrail = openalertcritColTrail;
	}

	public String getOpenalertMatchingText() {
		return OpenalertMatchingText;
	}

	public void setOpenalertMatchingText(String openalertMatchingText) {
		OpenalertMatchingText = openalertMatchingText;
	}

	
	
	public String getDowntimerulexmlname() {
		return downtimerulexmlname;
	}

	public void setDowntimerulexmlname(String downtimerulexmlname) {
		this.downtimerulexmlname = downtimerulexmlname;
	}

	public String getDowntimerulexmlrootnodepath() {
		return downtimerulexmlrootnodepath;
	}

	public void setDowntimerulexmlrootnodepath(String downtimerulexmlrootnodepath) {
		this.downtimerulexmlrootnodepath = downtimerulexmlrootnodepath;
	}

	public String getUptimerulexmlname() {
		return uptimerulexmlname;
	}

	public void setUptimerulexmlname(String uptimerulexmlname) {
		this.uptimerulexmlname = uptimerulexmlname;
	}

	public String getUptimerulexmlrootnodepath() {
		return uptimerulexmlrootnodepath;
	}

	public void setUptimerulexmlrootnodepath(String uptimerulexmlrootnodepath) {
		this.uptimerulexmlrootnodepath = uptimerulexmlrootnodepath;
	}

	public String getUptimeAlarmAgentPA() {
		return UptimeAlarmAgentPA;
	}

	public void setUptimeAlarmAgentPA(String uptimeAlarmAgentPA) {
		UptimeAlarmAgentPA = uptimeAlarmAgentPA;
	}
	
	public String getActuationhost() {
		return actuationhost;
	}

	public void setActuationhost(String actuationhost) {
		this.actuationhost = actuationhost;
	}

	public String getActuationport() {
		return actuationport;
	}

	public void setActuationport(String actuationport) {
		this.actuationport = actuationport;
	}

	public String getActuationusername() {
		return actuationusername;
	}

	public void setActuationusername(String actuationusername) {
		this.actuationusername = actuationusername;
	}

	public String getActuationpassword() {
		return actuationpassword;
	}

	public void setActuationpassword(String actuationpassword) {
		this.actuationpassword = actuationpassword;
	}

	public String getActuationfrommailid() {
		return actuationfrommailid;
	}

	public void setActuationfrommailid(String actuationfrommailid) {
		this.actuationfrommailid = actuationfrommailid;
	}

	public String getActuationtomailid() {
		return actuationtomailid;
	}

	public void setActuationtomailid(String actuationtomailid) {
		this.actuationtomailid = actuationtomailid;
	}
	
	public String getActuationsubjectpart1() {
		return actuationsubjectpart1;
	}

	public void setActuationsubjectpart1(String actuationsubjectpart1) {
		this.actuationsubjectpart1 = actuationsubjectpart1;
	}

	public String getActuationsubjectpart2() {
		return actuationsubjectpart2;
	}

	public void setActuationsubjectpart2(String actuationsubjectpart2) {
		this.actuationsubjectpart2 = actuationsubjectpart2;
	}
	
	public String getActuationsubjectuptime() {
		return actuationsubjectuptime;
	}

	public void setActuationsubjectuptime(String actuationsubjectuptime) {
		this.actuationsubjectuptime = actuationsubjectuptime;
	}

	public String getEspereventname() {
		return espereventname;
	}

	public void setEspereventname(String espereventname) {
		this.espereventname = espereventname;
	}

	public String getFetchlasteventmysql() {
		return fetchlasteventmysql;
	}

	public void setFetchlasteventmysql(String fetchlasteventmysql) {
		this.fetchlasteventmysql = fetchlasteventmysql;
	}
	
	public String getFetchlasteventoracle() {
		return fetchlasteventoracle;
	}

	public void setFetchlasteventoracle(String fetchlasteventoracle) {
		this.fetchlasteventoracle = fetchlasteventoracle;
	}

	public String getCheckdatapresenceinnormalize() {
		return checkdatapresenceinnormalize;
	}

	public void setCheckdatapresenceinnormalize(String checkdatapresenceinnormalize) {
		this.checkdatapresenceinnormalize = checkdatapresenceinnormalize;
	}

	public String getAllrulexmlname() {
		return allrulexmlname;
	}

	public void setAllrulexmlname(String allrulexmlname) {
		this.allrulexmlname = allrulexmlname;
	}

	public String getAllrulexmlrootnodepath() {
		return allrulexmlrootnodepath;
	}

	public void setAllrulexmlrootnodepath(String allrulexmlrootnodepath) {
		this.allrulexmlrootnodepath = allrulexmlrootnodepath;
	}

	public String getDowntimejoincondition() {
		return downtimejoincondition;
	}

	public void setDowntimejoincondition(String downtimejoincondition) {
		this.downtimejoincondition = downtimejoincondition;
	}

	public String getUptimeruleuniqueidentifier() {
		return uptimeruleuniqueidentifier;
	}

	public void setUptimeruleuniqueidentifier(String uptimeruleuniqueidentifier) {
		this.uptimeruleuniqueidentifier = uptimeruleuniqueidentifier;
	}

	public String getWatchdirfilename() {
		return watchdirfilename;
	}

	public void setWatchdirfilename(String watchdirfilename) {
		this.watchdirfilename = watchdirfilename;
	}

	public String getShadowdirfilename() {
		return shadowdirfilename;
	}

	public void setShadowdirfilename(String shadowdirfilename) {
		this.shadowdirfilename = shadowdirfilename;
	}
	
	public String getCheckColPresenceNorm() {
		return CheckColPresenceNorm;
	}

	public void setCheckColPresenceNorm(String checkColPresenceNorm) {
		CheckColPresenceNorm = checkColPresenceNorm;
	}

	public String getNonMatchingAlarmIdCol() {
		return NonMatchingAlarmIdCol;
	}

	public void setNonMatchingAlarmIdCol(String nonMatchingAlarmIdCol) {
		NonMatchingAlarmIdCol = nonMatchingAlarmIdCol;
	}

	@Override
    public String toString() {
        return "ServerData [name=" + name + ", inurl="+inurl+", outurl=" + outurl + ", numberOfInstances=" + numberOfInstances + ", type="
                + type + ", interval=" + interval + ", hasHeader=" + hasHeader + ", sendMsgToMsgBus=" + sendMsgToMsgBus
                + ", streamName=" + streamName + ", streamType=" + streamType + ", output=" + output + ", xsdPath="
                + xsdPath + ", xPath=" + xPath + ", mappingInfo=" + mappingInfo + ", headers="
                + Arrays.toString(headers) + ", dimName=" + dimName + ", timeStampName=" + timeStampName
                + ", componentName=" + componentName + ", moName=" + moName  + ", inuserId="+ inuserId +", outuserId=" + outuserId
                //+ ", password=" + password + ", sql=" + sql + ", timer=" + timer + ", driverDB=" + driverDB
                + ",timer=" + timer + ", indriverDB="+ indriverDB +", outdriverDB=" + outdriverDB
                + ", applicationName=" + applicationName + ", processName=" + processName + ", outputRequired="
                + outputRequired + "]";
    }

	public String getSelectDowntimeRuleDetails() {
		return SelectDowntimeRuleDetails;
	}

	public void setSelectDowntimeRuleDetails(String selectDowntimeRuleDetails) {
		SelectDowntimeRuleDetails = selectDowntimeRuleDetails;
	}

	public String getUpdateDowntimeRuleDetails() {
		return UpdateDowntimeRuleDetails;
	}

	public void setUpdateDowntimeRuleDetails(String updateDowntimeRuleDetails) {
		UpdateDowntimeRuleDetails = updateDowntimeRuleDetails;
	}

}
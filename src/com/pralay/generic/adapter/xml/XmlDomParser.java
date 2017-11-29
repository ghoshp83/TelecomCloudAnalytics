package com.pralay.generic.adapter.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.pralay.configuration.model.ServerData;
import com.pralay.configuration.model.ServerData.InputType;

public class XmlDomParser {
	final static Logger LOGGING = LoggerFactory.getLogger(XmlDomParser.class);
	private static Map<String, Map<String, String>> kpisMap = new HashMap<String, Map<String, String>>();
	private static boolean isHeaderIncluded;
	private static String component;
	private static long resolution;
	private static Interval interval;
	private static String streamName;
	private static String[] headersArr;
	private static Map<String, String> attrMappingMap = new HashMap<String, String>();
	private static List<ServerData> serverDataLst = null;
	private static Map<String, List<ServerData>> serverDataMap = new HashMap<String, List<ServerData>>();
	
	private static Map<String, Map<String, String>> xmlDetailMaps = new HashMap<String, Map<String, String>>(); 
	private static String seperator = "^";
	
 	public static void main(String[] args) throws Exception {
  		LOGGING.info("Entering the XMLDOM Parser");
        LOGGING.info("Arguments [0] : " + args[0]);
        LOGGING.info("Arguments [1] : " + args[1]);
        LOGGING.info("Arguments [2] : " + args[2]);
		XPathFactory xpf = XPathFactory.newInstance();
		
		LOGGING.info("Printing the XMLDOM Parser argument :: " + args[1]);
        XPath xPath = xpf.newXPath();
        InputSource inputSource = new InputSource(args[1]);

        // Getting the information of mapping csv fields to columns
        NodeList mappingNodes = (NodeList) xPath.evaluate("/ConfigDatas/Mappings/Mapping/Csvfield", inputSource, XPathConstants.NODESET);
        for(int instanceName = 0; instanceName < mappingNodes.getLength(); instanceName++) {
        	attrMappingMap.put(mappingNodes.item(instanceName).getAttributes().getNamedItem("name").getNodeValue(), mappingNodes.item(instanceName).getTextContent());
        }
        
        LOGGING.info("End the implementation of caching");
         // Headers
        LOGGING.info("Started the implementation of header");
        NodeList headerNodes = (NodeList) xPath.evaluate("/ConfigDatas/Headers/Header", inputSource, XPathConstants.NODESET);
        
        if(headerNodes != null && headerNodes.getLength() > 0 )
        {	
        	headersArr = new String[headerNodes.getLength()];
	        for(int headerNode = 0; headerNode < headerNodes.getLength(); headerNode++) {
	        	headersArr[headerNode] = headerNodes.item(headerNode).getTextContent();
	        }
        }
        NodeList headerIncluded = (NodeList) xPath.evaluate("/ConfigDatas/Headers/HeaderIncluded", inputSource, XPathConstants.NODESET);
        isHeaderIncluded = new Boolean(headerIncluded.item(0).getTextContent());
	    LOGGING.info("End the implementation of caching");
        
        // Servers information.
        XPathExpression serverNameExpression = xPath.compile("Name");
        XPathExpression InserverURLExpression = xPath.compile("InURL");
        XPathExpression InuserIdXpathExpression = xPath.compile("InUserId");
        XPathExpression InpasswordXpathExpression = xPath.compile("InPassword");
        XPathExpression IndriverDBXpathExpression = xPath.compile("InDriverDB");
        XPathExpression OutserverURLExpression = xPath.compile("OutURL");
        XPathExpression OutuserIdXpathExpression = xPath.compile("OutUserId");
        XPathExpression OutpasswordXpathExpression = xPath.compile("OutPassword");
        XPathExpression OutdriverDBXpathExpression = xPath.compile("OutDriverDB");
        XPathExpression rawtruncatesqlDBXpathExpression = xPath.compile("RawTruncateSQL");
        XPathExpression insertrawsqlDBXpathExpression = xPath.compile("InsertRawSQL");
        XPathExpression fetcheventsqlDBXpathExpression = xPath.compile("FetchEventSQL");
        XPathExpression fetchalarmsqlDBXpathExpression = xPath.compile("FetchAlarmIdSQL");
        XPathExpression fetchhostsqlDBXpathExpression = xPath.compile("FetchHostSQL");
        XPathExpression checkalarmsqlDBXpathExpression = xPath.compile("CheckAlarmNormalise");
        XPathExpression insertalarmsqlDBXpathExpression = xPath.compile("InsertAlarmNormalise");
        XPathExpression updatealarmsqlDBXpathExpression = xPath.compile("UpdateAlarmNormalise");
        XPathExpression DefColTrailXpathExpression = xPath.compile("DefColTrail");
        XPathExpression DisConGFSTrailXpathExpression = xPath.compile("DisConGFSTrail");
        XPathExpression UnsynGFSTrailXpathExpression = xPath.compile("UnsynGFSTrail");
        XPathExpression ScomColTrailXpathExpression = xPath.compile("ScomColTrail");
        XPathExpression NprioColTrailXpathExpression = xPath.compile("NprioColTrail");
        XPathExpression LAColTrailXpathExpression = xPath.compile("LAColTrail");
        XPathExpression ZHColTrailXpathExpression = xPath.compile("ZHColTrail");
        XPathExpression SwtColTrailXpathExpression = xPath.compile("SwtColTrail");
        XPathExpression SariowaitColTrailXpathExpression = xPath.compile("SariowaitColTrail");
        XPathExpression SaridleColTrailXpathExpression = xPath.compile("SaridleColTrail");
        XPathExpression SarloadavgColTrailXpathExpression = xPath.compile("SarloadavgColTrail");
        XPathExpression OpenalertwarnColTrailXpathExpression = xPath.compile("OpenalertwarnColTrail");
        XPathExpression OpenalertcritColTrailXpathExpression = xPath.compile("OpenalertcritColTrail");
        XPathExpression AlarmAgentCXpathExpression = xPath.compile("AlarmAgentC");
        XPathExpression AlarmAgentNTRXpathExpression = xPath.compile("AlarmAgentNTR");
        XPathExpression AlarmAgentPAXpathExpression = xPath.compile("AlarmAgentPA");
        XPathExpression AlarmSeverityCXpathExpression = xPath.compile("AlarmSeverityC");
        XPathExpression AlarmSeverityWXpathExpression = xPath.compile("AlarmSeverityW");
        XPathExpression ConnGFSMatchingTextXpathExpression = xPath.compile("ConnGFSMatchingText");
        XPathExpression DisConnGFSMatchingTextXpathExpression = xPath.compile("DisConnGFSMatchingText");
        XPathExpression UnsynGFSMatchingTextXpathExpression = xPath.compile("UnsynGFSMatchingText");
        XPathExpression UnsynGFSMatchingTextFullXpathExpression = xPath.compile("UnsynGFSMatchingTextFull");
        XPathExpression SarIoWaitMatchingTextXpathExpression = xPath.compile("SarIoWaitMatchingText");
        XPathExpression SarIdleMatchingTextXpathExpression = xPath.compile("SarIdleMatchingText");
        XPathExpression SarLoadAvgMatchingTextXpathExpression = xPath.compile("SarLoadAvgMatchingText");
        XPathExpression OpenalertMatchingTextXpathExpression = xPath.compile("OpenalertMatchingText");
        XPathExpression ScomMatchingTextXpathExpression = xPath.compile("ScomMatchingText");
        XPathExpression LAMatchingTextXpathExpression = xPath.compile("LAMatchingText");
        XPathExpression ZHMatchingTextXpathExpression = xPath.compile("ZHMatchingText");
        XPathExpression SwtMatchingTextXpathExpression = xPath.compile("SwtMatchingText");
        XPathExpression SwtMiscInfoXpathExpression = xPath.compile("SwtMiscInfo");
        XPathExpression NonMatchingAlarmIdXpathExpression = xPath.compile("NonMatchingAlarmId");
       
        XPathExpression downtimerulexmlnameXpathExpression = xPath.compile("DowntimeRuleXmlName");
        XPathExpression downtimerulexmlrootnodepathXpathExpression = xPath.compile("DowntimeRuleXmlRootNodePath");
        XPathExpression uptimerulexmlnameXpathExpression = xPath.compile("UptimeRuleXmlName");
        XPathExpression uptimerulexmlrootnodepathXpathExpression = xPath.compile("UptimeRuleXmlRootNodePath");
        XPathExpression UptimeAlarmAgentPAXpathExpression = xPath.compile("UptimeAlarmAgentPA");
        XPathExpression timerXpathExpression = xPath.compile("Timer");
        XPathExpression serverNoOfInstancesExpression = xPath.compile("NoOfInstances");
        XPathExpression serverTypeExpression = xPath.compile("Type");
        XPathExpression serverIntervalExpression = xPath.compile("Interval");
        XPathExpression sendMsgToMsgBusExpression = xPath.compile("SendMsgToMsgBus");
        XPathExpression serverStreamTypeExpression = xPath.compile("StreamType");
        XPathExpression serverStreamNameExpression = xPath.compile("StreamName");
        XPathExpression serverOutputExpression = xPath.compile("Output");
        XPathExpression serverXsdPathExpression = xPath.compile("Xsd-path");
        XPathExpression serverXpathExpression = xPath.compile("Xpath");
        XPathExpression processNameDBXpathExpression = xPath.compile("ProcessName");
        XPathExpression applicationNameXpathExpression = xPath.compile("ApplicationName");
        XPathExpression outputRequiredTypeXpathExpression = xPath.compile("OutputRequired");
        XPathExpression actuationhostXpathExpression = xPath.compile("actuationhost");
        XPathExpression actuationportXpathExpression = xPath.compile("actuationport");
        XPathExpression actuationusernameXpathExpression = xPath.compile("actuationusername");
        XPathExpression actuationpasswordXpathExpression = xPath.compile("actuationpassword");
        XPathExpression actuationfrommailidXpathExpression = xPath.compile("actuationFrom_mail_id");
        XPathExpression actuationtomailidXpathExpression = xPath.compile("actuationTo_mail_id");
        XPathExpression actuationsubjectpart1XpathExpression = xPath.compile("actuationsubjectpart1");
        XPathExpression actuationsubjectpart2XpathExpression = xPath.compile("actuationsubjectpart2");
        XPathExpression actuationsubjectuptimeXpathExpression = xPath.compile("actuationsubjectuptime");
        XPathExpression espereventnameXpathExpression = xPath.compile("EsperEventName");
        XPathExpression fetchlasteventmysqlXpathExpression = xPath.compile("FetchLastEventMySQL");
        XPathExpression fetchlasteventoracleXpathExpression = xPath.compile("FetchLastEventOracle");
        XPathExpression checkdatapresenceinnormalizeXpathExpression = xPath.compile("CheckDataPresenceInNormalize");
        XPathExpression allrulexmlnameXpathExpression = xPath.compile("AllRuleXmlName");
        XPathExpression allrulexmlrootnodepathXpathExpression = xPath.compile("AllRuleXmlRootNodePath");
        XPathExpression downtimejoinconditionXpathExpression = xPath.compile("downtimejoincondition");
        XPathExpression uptimeruleuniqueidentifierXpathExpression = xPath.compile("uptimeruleuniqueidentifier");
        XPathExpression watchdirfilenameXPathExpression = xPath.compile("watchdirfilename");
        XPathExpression shadowdirfilenameXPathExpression = xPath.compile("shadowdirfilename");
        XPathExpression CheckColPresenceNormXPathExpression = xPath.compile("CheckColPresenceNorm");
        XPathExpression NonMatchingAlarmIdColXPathExpression = xPath.compile("NonMatchingAlarmIdCol");
        XPathExpression SelectDowntimeRuleDetailsXPathExpression = xPath.compile("SelectDowntimeRuleDetails");
        XPathExpression UpdateDowntimeRuleDetailsXPathExpression = xPath.compile("UpdateDowntimeRuleDetails");
        
        LOGGING.info("Started the implementation of servers information ");
        NodeList serverNodes = (NodeList) xPath.evaluate("/ConfigDatas/Servers/Server", inputSource, XPathConstants.NODESET);
        for(int serverNode = 0; serverNode < serverNodes.getLength(); serverNode++) {
            Node serverElement = serverNodes.item(serverNode);
        	
            String serverName = serverNameExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	
        	String InserverURL = InserverURLExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String InuserIdXpath = InuserIdXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String InpasswordXpath = InpasswordXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String IndriverDBXpath = IndriverDBXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String OutserverURL = OutserverURLExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String OutuserIdXpath = OutuserIdXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String OutpasswordXpath = OutpasswordXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String OutdriverDBXpath = OutdriverDBXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String rawtruncatesqlDBXpath = rawtruncatesqlDBXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String insertrawsqlDBXpath = insertrawsqlDBXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String fetcheventsqlDBXpath = fetcheventsqlDBXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String fetchalarmsqlDBXpath = fetchalarmsqlDBXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String fetchhostsqlDBXpath = fetchhostsqlDBXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String checkalarmsqlDBXpath = checkalarmsqlDBXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String insertalarmsqlDBXpath = insertalarmsqlDBXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String updatealarmsqlDBXpath = updatealarmsqlDBXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String serverNoOfInstances = serverNoOfInstancesExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String serverType = serverTypeExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String serverInterval = serverIntervalExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	Boolean sendMsgToMsgBus = Boolean.valueOf(sendMsgToMsgBusExpression.evaluate(serverElement, XPathConstants.STRING).toString());
        	String serverStreamType = serverStreamTypeExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String serverStreamName = serverStreamNameExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String serverOutput = serverOutputExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String serverXsdPath = serverXsdPathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String serverXpath = serverXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	Long timerXpath = Long.valueOf(timerXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString());
        	String applicationNameXpath =  applicationNameXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	Boolean outputRequiredXpath =  Boolean.valueOf(outputRequiredTypeXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString());
        	String processNameXpath = processNameDBXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String DefColTrailXpath = DefColTrailXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String DisConGFSTrailXpath = DisConGFSTrailXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String UnsynGFSTrailXpath = UnsynGFSTrailXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String ScomColTrailXpath = ScomColTrailXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String NprioColTrailXpath = NprioColTrailXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String LAColTrailXpath = LAColTrailXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String ZHColTrailXpath = ZHColTrailXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String SwtColTrailXpath = SwtColTrailXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String SariowaitColTrailXpath = SariowaitColTrailXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String SaridleColTrailXpath = SaridleColTrailXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String SarloadavgColTrailXpath = SarloadavgColTrailXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String OpenalertwarnColTrailXpath = OpenalertwarnColTrailXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String OpenalertcritColTrailXpath = OpenalertcritColTrailXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String AlarmAgentCXpath = AlarmAgentCXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String AlarmAgentNTRXpath = AlarmAgentNTRXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String AlarmAgentPAXpath = AlarmAgentPAXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String AlarmSeverityCXpath = AlarmSeverityCXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String AlarmSeverityWXpath = AlarmSeverityWXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String ConnGFSMatchingTextXpath = ConnGFSMatchingTextXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String DisConnGFSMatchingTextXpath = DisConnGFSMatchingTextXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String UnsynGFSMatchingTextXpath = UnsynGFSMatchingTextXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String UnsynGFSMatchingTextFullXpath = UnsynGFSMatchingTextFullXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String SarIoWaitMatchingTextXpath = SarIoWaitMatchingTextXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String SarIdleMatchingTextXpath = SarIdleMatchingTextXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String SarLoadAvgMatchingTextXpath = SarLoadAvgMatchingTextXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String OpenalertMatchingTextXpath = OpenalertMatchingTextXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String ScomMatchingTextXpath = ScomMatchingTextXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String LAMatchingTextXpath = LAMatchingTextXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String ZHMatchingTextXpath = ZHMatchingTextXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String SwtMatchingTextXpath = SwtMatchingTextXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String SwtMiscInfoXpath = SwtMiscInfoXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String NonMatchingAlarmIdXpath = NonMatchingAlarmIdXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	
        	String downtimerulexmlnameXpath = downtimerulexmlnameXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String downtimerulexmlrootnodepathXpath = downtimerulexmlrootnodepathXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String uptimerulexmlnameXpath = uptimerulexmlnameXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String uptimerulexmlrootnodepathXpath = uptimerulexmlrootnodepathXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String UptimeAlarmAgentPAXpath = UptimeAlarmAgentPAXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String actuationhostXpath = actuationhostXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String actuationportXpath = actuationportXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String actuationusernameXpath = actuationusernameXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String actuationpasswordXpath = actuationpasswordXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String actuationfrommailidXpath = actuationfrommailidXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String actuationtomailidXpath = actuationtomailidXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String actuationsubjectpart1Xpath = actuationsubjectpart1XpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String actuationsubjectpart2Xpath = actuationsubjectpart2XpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String actuationsubjectuptimeXpath = actuationsubjectuptimeXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String espereventnameXpath = espereventnameXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String fetchlasteventmysqlXpath = fetchlasteventmysqlXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String fetchlasteventoracleXpath = fetchlasteventoracleXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String checkdatapresenceinnormalizeXpath = checkdatapresenceinnormalizeXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String allrulexmlnameXpath = allrulexmlnameXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String allrulexmlrootnodepathXpath = allrulexmlrootnodepathXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String downtimejoinconditionXpath = downtimejoinconditionXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String uptimeruleuniqueidentifierXpath = uptimeruleuniqueidentifierXpathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String watchdirfilenameXpath = watchdirfilenameXPathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String shadowdirfilenameXpath = shadowdirfilenameXPathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String CheckColPresenceNormXpath = CheckColPresenceNormXPathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String NonMatchingAlarmIdColXpath = NonMatchingAlarmIdColXPathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String SelectDowntimeRuleDetailsXpath = SelectDowntimeRuleDetailsXPathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	String UpdateDowntimeRuleDetailsXpath = UpdateDowntimeRuleDetailsXPathExpression.evaluate(serverElement, XPathConstants.STRING).toString();
        	
        	
        	ServerData sd = new ServerData();
        	sd.setName(serverName);
        	sd.setInurl(InserverURL);
        	sd.setInuserId(InuserIdXpath);
        	sd.setInpassword(InpasswordXpath);
        	sd.setIndriverDB(IndriverDBXpath);
        	sd.setOuturl(OutserverURL);
        	sd.setOutuserId(OutuserIdXpath);
        	sd.setOutpassword(OutpasswordXpath);
        	sd.setOutdriverDB(OutdriverDBXpath);
        	sd.setRawtruncatesql(rawtruncatesqlDBXpath);
        	sd.setFetcheventsql(fetcheventsqlDBXpath);
        	sd.setInsertrawsql(insertrawsqlDBXpath);
        	sd.setFetchalarmsql(fetchalarmsqlDBXpath);
        	sd.setFetchhostsql(fetchhostsqlDBXpath);
        	sd.setCheckalarmsql(checkalarmsqlDBXpath);
        	sd.setInsertalarmsql(insertalarmsqlDBXpath);
        	sd.setUpdatealarmsql(updatealarmsqlDBXpath);
        	sd.setNumberOfInstances(Integer.parseInt(serverNoOfInstances));
        	sd.setType(InputType.valueOf(serverType));
        	sd.setInterval(Short.valueOf(serverInterval));
        	sd.setStreamName(serverStreamName);
        	sd.setOutput(serverOutput);
        	sd.setStreamType(serverStreamType);
        	sd.setSendMsgToMsgBus(sendMsgToMsgBus);
        	sd.setXsdPath(serverXsdPath);
        	sd.setxPath(serverXpath);
        	sd.setTimer(timerXpath);
        	sd.setApplicationName(applicationNameXpath);
        	sd.setOutputRequired(outputRequiredXpath);
        	sd.setProcessName(processNameXpath);
        	sd.setHasHeader(isHeaderIncluded);
        	sd.setDefColTrail(DefColTrailXpath);
        	sd.setDisConGFSTrail(DisConGFSTrailXpath);
        	sd.setUnsynGFSTrail(UnsynGFSTrailXpath);
        	sd.setScomColTrail(ScomColTrailXpath);
        	sd.setNprioColTrail(NprioColTrailXpath);
        	sd.setLAColTrail(LAColTrailXpath);
        	sd.setZHColTrail(ZHColTrailXpath);
        	sd.setSwtColTrail(SwtColTrailXpath);
        	sd.setSariowaitColTrail(SariowaitColTrailXpath);
        	sd.setSaridleColTrail(SaridleColTrailXpath);
        	sd.setSarloadavgColTrail(SarloadavgColTrailXpath);
        	sd.setOpenalertwarnColTrail(OpenalertwarnColTrailXpath);
        	sd.setOpenalertcritColTrail(OpenalertcritColTrailXpath);
        	sd.setAlarmAgentC(AlarmAgentCXpath);
        	sd.setAlarmAgentNTR(AlarmAgentNTRXpath);
        	sd.setAlarmAgentPA(AlarmAgentPAXpath);
        	sd.setAlarmSeverityC(AlarmSeverityCXpath);
        	sd.setAlarmSeverityW(AlarmSeverityWXpath);
        	sd.setConnGFSMatchingText(ConnGFSMatchingTextXpath);
        	sd.setDisConnGFSMatchingText(DisConnGFSMatchingTextXpath);
        	sd.setUnsynGFSMatchingText(UnsynGFSMatchingTextXpath);
        	sd.setUnsynGFSMatchingTextFull(UnsynGFSMatchingTextFullXpath);
        	sd.setSarIoWaitMatchingText(SarIoWaitMatchingTextXpath);
        	sd.setSarIdleMatchingText(SarIdleMatchingTextXpath);
        	sd.setSarLoadAvgMatchingText(SarLoadAvgMatchingTextXpath);
        	sd.setOpenalertMatchingText(OpenalertMatchingTextXpath);
        	sd.setScomMatchingText(ScomMatchingTextXpath);
        	sd.setLAMatchingText(LAMatchingTextXpath);
        	sd.setZHMatchingText(ZHMatchingTextXpath);
        	sd.setSwtMatchingText(SwtMatchingTextXpath);
        	sd.setSwtMiscInfo(SwtMiscInfoXpath);
        	sd.setNonMatchingAlarmId(NonMatchingAlarmIdXpath);
        	
        	sd.setDowntimerulexmlname(downtimerulexmlnameXpath);
        	sd.setDowntimerulexmlrootnodepath(downtimerulexmlrootnodepathXpath);
        	sd.setUptimerulexmlname(uptimerulexmlnameXpath);
        	sd.setUptimerulexmlrootnodepath(uptimerulexmlrootnodepathXpath);
        	sd.setUptimeAlarmAgentPA(UptimeAlarmAgentPAXpath);
        	sd.setActuationhost(actuationhostXpath);
        	sd.setActuationport(actuationportXpath);
        	sd.setActuationusername(actuationusernameXpath);
        	sd.setActuationpassword(actuationpasswordXpath);
        	sd.setActuationfrommailid(actuationfrommailidXpath);
        	sd.setActuationtomailid(actuationtomailidXpath);
        	sd.setActuationsubjectpart1(actuationsubjectpart1Xpath);
        	sd.setActuationsubjectpart2(actuationsubjectpart2Xpath);
        	sd.setActuationsubjectuptime(actuationsubjectuptimeXpath);
        	sd.setEspereventname(espereventnameXpath);
        	sd.setFetchlasteventmysql(fetchlasteventmysqlXpath);
        	sd.setFetchlasteventoracle(fetchlasteventoracleXpath);
        	sd.setCheckdatapresenceinnormalize(checkdatapresenceinnormalizeXpath);
        	sd.setAllrulexmlname(allrulexmlnameXpath);
        	sd.setAllrulexmlrootnodepath(allrulexmlrootnodepathXpath);
        	sd.setDowntimejoincondition(downtimejoinconditionXpath);
        	sd.setUptimeruleuniqueidentifier(uptimeruleuniqueidentifierXpath);
        	sd.setWatchdirfilename(watchdirfilenameXpath);
        	sd.setShadowdirfilename(shadowdirfilenameXpath);
        	sd.setCheckColPresenceNorm(CheckColPresenceNormXpath);
        	sd.setNonMatchingAlarmIdCol(NonMatchingAlarmIdColXpath);
        	sd.setSelectDowntimeRuleDetails(SelectDowntimeRuleDetailsXpath);
        	sd.setUpdateDowntimeRuleDetails(UpdateDowntimeRuleDetailsXpath);
        	
        	if (InputType.CSV.name().equalsIgnoreCase(serverType)) {
        		sd.setHeaders(headersArr);
        		sd.setMappingInfo(attrMappingMap);
        	}
        	
        	if (!serverDataMap.containsKey(applicationNameXpath)) {
        		serverDataLst = new ArrayList<ServerData>();
        		serverDataMap.put(applicationNameXpath, serverDataLst);
        	} else {
        		serverDataLst = serverDataMap.get(applicationNameXpath);
        	}	
    		serverDataLst.add(sd);
        }
        LOGGING.info("serverDataMap value is :: " + serverDataMap);
        LOGGING.info("Exiting the implementation of caching");
        LOGGING.info("Exiting the XMLDOM Parser");
 	}

	public static Map<String, Map<String, String>> getKpisMap() {
		return kpisMap;
	}

	public static boolean isHeaderIncluded() {
		return isHeaderIncluded;
	}

	public static String getComponent() {
		return component;
	}

	public static long getResolution() {
		return resolution;
	}
	
	public static Interval getInterval() {
		return interval;
	}
	
	public static String getStreamName() {
		return streamName;
	}

	public static Map<String, Map<String, String>> getXmlDetailMaps() {
		return xmlDetailMaps;
	}

	public static List<ServerData> getServerDataLst(String applicationName) {
		return serverDataMap.get(applicationName);
	}
}
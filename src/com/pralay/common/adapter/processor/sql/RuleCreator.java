package com.pralay.common.adapter.processor.sql;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.pralay.common.adapter.processor.CommonRouteBuilder;
import com.pralay.configuration.model.DownTimeRulePojo;


public class RuleCreator {

	final static Logger LOGGING = LoggerFactory.getLogger(CommonRouteBuilder.class);
	
	public static void createDowntimeRule(DownTimeRulePojo DowntimeRulePojo) throws ParserConfigurationException, 
	SAXException, IOException, TransformerException {
		// TODO Auto-generated method stub
		String ruleName = DowntimeRulePojo.getRulename();
		String primaryAlarmIds = DowntimeRulePojo.getPrimaryAlarmIds();
		String correlatorAlarmIds = DowntimeRulePojo.getCorrelatorAlarmIds();
		String joinConditions = DowntimeRulePojo.getJoinConditions();
		String joinStr = "";
		//String ruleStartTag="";
		//String ruleEndTag = "";
		String ruleBody = "";
		
		LOGGING.info("03192016: gautamchecck5"+joinConditions); 
		//String ruleBodyTag = "";
		if(joinConditions.equals("Host") ){
			joinStr = "a1.funchostname=a2.funchostname";
			LOGGING.info("03192016: inside 1 IF");
		}else if (joinConditions.equals("Date")){
			joinStr = "convertDate(a1.eventdate) = convertDate(a2.eventdate)";
			LOGGING.info("03192016: inside 2 IF");
		}else if (joinConditions.equals("Both")){
			joinStr = "a1.funchostname=a2.funchostname and convertDate(a1.eventdate) = convertDate(a2.eventdate)";
			LOGGING.info("03192016: inside 3 IF");
		}
		
		//ruleStartTag = "<"+ruleName+">";
		//ruleEndTag = "</"+ruleName+">";
		//ruleBody = "select distinct * from IncomingDataStream(aid_alarm_id in('"+primaryAlarmIds+"')).win:time(120 min) as a1,IncomingDataStream(aid_alarm_id in('"+correlatorAlarmIds+"')).win:time(120 min) as a2 having convertDate(a1.eventdate) = convertDate(a2.eventdate)";    
		//ruleBodyTag = ruleStartTag+ruleBody+ruleEndTag;
		ruleBody = "select distinct * from IncomingDataStream(aid_alarm_id in('"+primaryAlarmIds+"')).win:time(120 min) as a1,IncomingDataStream(aid_alarm_id in('"+correlatorAlarmIds+"')).win:time(120 min) as a2 having "+joinStr;
		insertRule(ruleName,ruleBody);
	}

	
	public static void insertRule(String ruleName,String ruleBody) throws ParserConfigurationException, SAXException, 
	IOException, TransformerException {
		// TODO Auto-generated method stub
		
		 DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
			        .newInstance();
	    DocumentBuilder documentBuilder = documentBuilderFactory
	        .newDocumentBuilder();

	    /* parse existing file to DOM */
	    Document document = documentBuilder.parse(new File("C:\\Swisscom_final\\SwissComCloudAnalytics\\config\\AllRules.xml"));

	    // Root Element
	    Element rootElement = document.getDocumentElement();

	    // server elements
	    Element ruleNameTag = document.createElement(ruleName);
	    ruleNameTag.appendChild(document.createTextNode(ruleBody));
	    rootElement.appendChild(ruleNameTag);
	    
	    DOMSource source = new DOMSource(document);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        StreamResult result = new StreamResult("C:\\Swisscom_final\\SwissComCloudAnalytics\\config\\AllRules.xml");
        transformer.transform(source, result);
	}
	
	

}

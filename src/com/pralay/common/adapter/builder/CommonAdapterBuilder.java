package com.pralay.common.adapter.builder;

import java.io.FileInputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.camel.impl.SimpleRegistry;
import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
//import com.espertech.esper.core.service.EPAdministratorImpl;
import com.pralay.common.adapter.camel.AdapterFlow;
import com.pralay.common.adapter.factory.CommonRouteBuilderFactory;
import com.pralay.common.adapter.processor.sql.AlarmIdDeter;
import com.pralay.common.adapter.processor.sql.HostNameDeter;
import com.pralay.common.adapter.processor.sql.CorrelateEvent.CEPListener;
import com.pralay.common.adapter.processor.sql.UptimeMgmt.CEPListenerUptime;
import com.pralay.common.adapter.processor.sql.UptimeMgmt.CEPListenerUptimePlatformEvents;
import com.pralay.configuration.model.AlarmIdPojo;
import com.pralay.configuration.model.DatabaseConnection;
import com.pralay.configuration.model.EsperEngine;
import com.pralay.configuration.model.HostNamePojo;
import com.pralay.configuration.model.ServerData;

public class CommonAdapterBuilder {
    private static final Logger LOGGING = LoggerFactory.getLogger(CommonAdapterBuilder.class);
    private static CommonRouteBuilderFactory commonRouteBuilderFactory;
    private static EPServiceProvider cep = null;
    //private static EPStatement downtimeRule=null;
	//private static EPStatement uptimeRule=null;
    public static EPAdministrator cepAdm=null;
    //public static EPAdministratorImpl cepAdm=null;
	public static EPStatement allRuleStmt=null;
    public static EPRuntime cepRT=null;
    public static List<AlarmIdPojo> aidlst = new ArrayList<AlarmIdPojo>();
    public static List<HostNamePojo> hnlst = new ArrayList<HostNamePojo>();
    public static Connection conn = null;
    
	
    public static Map<String, Map<String, String>> build(List<ServerData> serverDataLst) {
        try {
            SimpleRegistry simpleRegistry = new SimpleRegistry();
            AdapterFlow.init(simpleRegistry);
            if (serverDataLst != null && !serverDataLst.isEmpty()) {
                for (ServerData serverData : serverDataLst) {
                    LOGGING.info("Reading the input file from the location " + serverData.toString());
                    //Creating the new db connection
                    DatabaseConnection dc = new DatabaseConnection(serverData);
                    conn = DatabaseConnection.makeConnection();
                    AdapterFlow.addRoute(commonRouteBuilderFactory.getRouteBuilder(serverData));
                    if (ServerData.InputType.SQL.name().equalsIgnoreCase(serverData.getType().name())) {
                        simpleRegistry.put(serverData.getOuturl() + serverData.getOutuserId(), setupOutDataSource(serverData));
                        simpleRegistry.put(serverData.getInurl() + serverData.getInuserId(), setupInDataSource(serverData));
                    }
                    
                    //Creating the esper engine 
                    EsperEngine ee = new EsperEngine(serverData);
                    cep = EsperEngine.makeEngine();
                    cepRT = cep.getEPRuntime();
            		cepAdm = cep.getEPAdministrator();
            		//cepAdm = (EPAdministratorImpl)cep.getEPAdministrator();
            		XPathFactory xpf = XPathFactory.newInstance();
        			XPath xPath = xpf.newXPath();
        			//InputSource inputSource = new InputSource(new FileInputStream("DownTimeRule.xml"));
        	        //InputSource inputSource = new InputSource(new FileInputStream(serverData.getDowntimerulexmlname()));
        	        InputSource inputSource = new InputSource(new FileInputStream(serverData.getAllrulexmlname()));
        	        XPathExpression ruleExpression = xPath.compile("node()");
        	        //NodeList ruleNodes = (NodeList) xPath.evaluate("/DownTimeRules/*", inputSource, XPathConstants.NODESET);
        	        //NodeList ruleNodes = (NodeList) xPath.evaluate(serverData.getDowntimerulexmlrootnodepath(), inputSource, XPathConstants.NODESET);
        	        NodeList ruleNodes = (NodeList) xPath.evaluate(serverData.getAllrulexmlrootnodepath(), inputSource, XPathConstants.NODESET);
        	        for(int ruleNode = 0; ruleNode < ruleNodes.getLength(); ruleNode++) {
        	            Node ruleElement = ruleNodes.item(ruleNode);
        	            String ruleStmt = ruleExpression.evaluate(ruleElement, XPathConstants.STRING).toString();
        	            String ruleStmtTag = ruleElement.getNodeName();
        	            LOGGING.info("Rule Value: "+ruleStmt);
        	            LOGGING.info("Rule Id: "+ruleStmtTag);
        	            allRuleStmt = cepAdm.createEPL(replaceXmlSplChar(ruleStmt),ruleStmtTag);
        	            
        	            if(ruleStmt.contains(serverData.getDowntimejoincondition())){
        	            	LOGGING.info("You are in Downtime Listener 03072016 "+ruleStmt);
        	            	allRuleStmt.addListener(new CEPListener());
        	            }else if(ruleStmt.contains(serverData.getUptimeAlarmAgentPA())){
        	            	LOGGING.info("You are in Uptime1 Listener 03072016 "+ruleStmt);
        	            	allRuleStmt.addListener(new CEPListenerUptimePlatformEvents());
        	            }else if(ruleStmt.contains(serverData.getUptimeruleuniqueidentifier())){
        	            	LOGGING.info("You are in Uptime2 Listener 03072016 "+ruleStmt);
        	            	allRuleStmt.addListener(new CEPListenerUptime());
        	            }else{
        	            	LOGGING.info("You are in defaul Listener 03072016 "+ruleStmt);
        	            	allRuleStmt.addListener(new CEPListener());
        	            }
        	        }
                    
        	        
	        	       /*inputSource = new InputSource(new FileInputStream(serverData.getUptimerulexmlname()));
	        	        ruleExpression = xPath.compile("node()");
	        	        //NodeList ruleNodes = (NodeList) xPath.evaluate("/UpTimeRules/*", inputSource, XPathConstants.NODESET);
	        	        ruleNodes = (NodeList) xPath.evaluate(serverData.getUptimerulexmlrootnodepath(), inputSource, XPathConstants.NODESET);
	        	        for(int ruleNode = 0; ruleNode < ruleNodes.getLength(); ruleNode++) {
	        	            Node ruleElement = ruleNodes.item(ruleNode);
	        	            String ruleStmt = ruleExpression.evaluate(ruleElement, XPathConstants.STRING).toString();
	        	            LOGGING.info("Rule Value: "+ruleStmt);
	        	            uptimeRule = cepAdm.createEPL(replaceXmlSplChar(ruleStmt));
	        	            if(ruleStmt.contains(serverData.getUptimeAlarmAgentPA()))
	        	            	 uptimeRule.addListener(new CEPListenerUptimePlatformEvents());
	        	            else 
	        	            	uptimeRule.addListener(new CEPListenerUptime());
	        	        }*/
        	        
        	        
                    // Getting all available alarm list
                    AlarmIdDeter aid = new AlarmIdDeter(serverData);
                    aidlst = aid.alarm();
                    
                    // Getting the list for all hostname's functional mapping 
                    HostNameDeter hn = new HostNameDeter(serverData);
                    hnlst = hn.hostnamelist();
                }
            }
            AdapterFlow.start();
            Thread.currentThread().join();
            //LOGGING.info("<--- BEFORE CONN STATIC COMMIT ---> ");
            conn.commit();
            conn.close();
        } catch (Exception e) {
        	e.printStackTrace();
            LOGGING.error("CommonAdapterBuilder --> build method " + e.getMessage());
        }
        
        LOGGING.info("Signing off...");
        return null;
    }

    private static DataSource setupOutDataSource(ServerData serverData) {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(serverData.getOutdriverDB());
        ds.setUsername(serverData.getOutuserId());
        ds.setPassword(serverData.getOutpassword());
        ds.setUrl(serverData.getOuturl());
        return ds;
    }
    private static DataSource setupInDataSource(ServerData serverData) {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(serverData.getIndriverDB());
        ds.setUsername(serverData.getInuserId());
        ds.setPassword(serverData.getInpassword());
        ds.setUrl(serverData.getInurl());
        return ds;
    }
    
    private static String replaceXmlSplChar(String ruletext){
		if(ruletext.contains("&gt;"))
			ruletext = ruletext.replace("&gt;", ">");
		else if(ruletext.contains("&lt;"))
			ruletext = ruletext.replace("&lt;", "<");
		return ruletext;
	}
}

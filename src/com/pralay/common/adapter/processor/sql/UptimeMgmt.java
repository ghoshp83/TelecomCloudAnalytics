package com.pralay.common.adapter.processor.sql;

import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.ConfigurationDBRef;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.pralay.common.actuation.SendMail;
import com.pralay.common.adapter.builder.CommonAdapterBuilder;
import com.pralay.common.adapter.processor.sql.CorrelateEvent.CEPListener;
import com.pralay.configuration.model.DatabaseConnection;
import com.pralay.configuration.model.EventPojo;
import com.pralay.configuration.model.ServerData;

public class UptimeMgmt {
	private static ServerData serverData;
	//private static ResultSet rsuptime = null;
	//private static ResultSet rspfuptime = null;
	private static SendMail sendmail;
	DatabaseConnection dbc;
	private static Connection conn = null;
	private static EPServiceProvider cep = null;
	private static EPRuntime cepRT=null;
	private static EPAdministrator cepAdm=null;
	private static EPStatement uptimeRule=null;
	private static final Logger LOGGING = LoggerFactory.getLogger(CorrelateEvent.class);
	
	public UptimeMgmt(ServerData serverData){
		this.serverData = serverData;
		conn = CommonAdapterBuilder.conn;
		//cep = CommonAdapterBuilder.cep;
		cepRT = CommonAdapterBuilder.cepRT;
		sendmail = new SendMail(serverData);
	}
	
	//User Defined Class to convert timestamp to date for EPL
	public static String evaluteEventDate(String eventdate){
		    return eventdate.substring(0,10).replaceAll("-", "");
	}
	
	public static long evaluteLongTimeStamp(String eventdate){
		long eventdatetimestamp = Long.valueOf(eventdate.substring(0, 19).replaceAll("-", "").replaceAll(":", "").replaceFirst(" ", "")).longValue();
	    return eventdatetimestamp;
	}
	
	public static long evaluteTimeDiffInMin(String appdate, String platformdate)
	{
		long timediff = (Timestamp.valueOf(appdate.substring(0, 19)).getTime()-Timestamp.valueOf(platformdate.substring(0, 19)).getTime())/(60*1000);
		return timediff;
	}
	
	public static String evaluteDynamicGetter(String methodname){
		String gettervalue = null;
		Class sdClass = serverData.getClass();
		Class[] sdparams = null;
		Method sdmethod = null;
		
		try{
			sdmethod = sdClass.getMethod(methodname, sdparams);
			LOGGING.info("method name from sd -> "+sdmethod);
		}catch(NoSuchMethodException nsme){
			nsme.printStackTrace();
		}
		try{
			gettervalue = sdmethod.invoke(serverData, null).toString();
			LOGGING.info("Dynamic getter value: "+gettervalue);
		}catch (IllegalAccessException iae) {
		    iae.printStackTrace();
		} catch (InvocationTargetException ite) {
		    ite.printStackTrace();
		}
		
		return gettervalue;
	}
	
	public static String evaluteCurrentDate(){
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("yyyyMMddhh24mmss");
		return df.format(date);
	}
	
	public static String evaluteCurrentDateTimestamp(){
		Date datets = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return df.format(datets);
	}
	
	/*public static String replaceXmlSplChar(String ruletext){
		if(ruletext.contains("&gt;"))
			ruletext = ruletext.replace("&gt;", ">");
		else if(ruletext.contains("&lt;"))
			ruletext = ruletext.replace("&lt;", "<");
		return ruletext;
	}*/
	
	public static String replaceXmlSplChar(String ruletext){
		ruletext = ruletext.replace("&gt;", ">");
		ruletext = ruletext.replace("&lt;", "<");
		return ruletext;
	}
	
	public static class CEPListenerUptime implements UpdateListener{
		//boolean uptimeeventpresence=false;
		ResultSet rsuptime = null;
		@Override
		public void update(EventBean[] newData, EventBean[] oldData) {
			System.out.println("Uptime Alarm Received: "+newData[0].getUnderlying());
			
			/*Thread uptimedbTh = new Thread(new UptimeDbInsert(newData[0].get("id").toString(),newData[0].get("aid_alarm_id").toString(),
					newData[0].get("functional_aid_alarm_id").toString(),newData[0].get("eventdate").toString(),newData[0].get("funchostname").toString(),
					newData[0].get("severity").toString(),newData[0].get("uptimenotify").toString(),newData[0].get("alarm_text").toString(),sendmail));
			uptimedbTh.start();*/
			
			String query = "insert into uptime_management_new(id,alarm_id,functional_alarm_id,eventdate,hostname,severity,uptime_mgmt_notification,alarm_text)"
					+ " values('"+newData[0].get("id")+"','"+newData[0].get("aid_alarm_id")+"','"+newData[0].get("functional_aid_alarm_id")
					+"','"+newData[0].get("eventdate")+"','"+newData[0].get("funchostname")+"','"+newData[0].get("severity")
					+"','"+newData[0].get("uptimenotify")+"','"+newData[0].get("message")+"')";
			
			try{
				LOGGING.info("query in up-alarm reception: "+query);
				String checkuptimeevent = "select id from uptime_management_new where id='"+newData[0].get("id")+"'";
				String updateuptimeevent = "update uptime_management_new set uptime_mgmt_notification='"+newData[0].get("uptimenotify")
						+"' where id='"+newData[0].get("id")+"' ";
				LOGGING.info("check uptime event query: "+checkuptimeevent);
				//rsuptime = conn.prepareStatement(checkuptimeevent).executeQuery();
				rsuptime = conn.createStatement().executeQuery(checkuptimeevent);
				LOGGING.info("I am here");
				//if(rsuptime!= null){
					if(!rsuptime.next()){
						//uptimeeventpresence=true;
						LOGGING.info("Uptime event is not present. Inserting new Record!");
						conn.prepareStatement(query).executeUpdate();
						conn.commit();
						//sendmail.generateAndSendEmail(newData[0].get("aid_alarm_id").toString(), newData[0].get("functional_aid_alarm_id").toString(),newData[0].get("id").toString(), newData[0].get("uptimenotify").toString());
						sendmail.generateAndSendEmailUpTime(newData[0].get("functional_aid_alarm_id").toString(),newData[0].get("id").toString(), newData[0].get("uptimenotify").toString(),newData[0].get("funchostname").toString(),newData[0].get("eventdate").toString());
					}/*else{
						conn.prepareStatement(updateuptimeevent).executeUpdate();
						conn.commit();
					}*/
					rsuptime.close();
				//}
				
					
			}catch(Exception e){
				e.printStackTrace();
				LOGGING.error(e.getMessage());
			}
		}
	}
	
	/*public static class CEPListenerUptimeSms implements UpdateListener{
		ResultSet rs = null;
		boolean uptimeeventpresence=false;
		@Override
		public void update(EventBean[] newData, EventBean[] oldData) {
			System.out.println("Uptime Alarm Received: "+newData[0].getUnderlying());
			
			String query = "insert into uptime_management(id,alarm_id,functional_alarm_id,eventdate,hostname,severity,uptime_mgmt_notification,ealarm_sms,ealarm_sms_text)"
					+ " values('"+newData[0].get("id")+"','"+newData[0].get("aid_alarm_id")+"','"+newData[0].get("functional_aid_alarm_id")
					+"','"+newData[0].get("eventdate")+"','"+newData[0].get("funchostname")+"','"+newData[0].get("severity")
					+"','"+newData[0].get("uptimenotify")+"','"+newData[0].get("ealarm_sms")+"','"+newData[0].get("ealarm_sms_text")+"')";
			
			try{
				LOGGING.info("query in up-alarm reception: "+query);
				String checkuptimeevent = "select id from uptime_management where id='"+newData[0].get("id")+"'";
				String updateuptimeevent = "update uptime_management set uptime_mgmt_notification='"+newData[0].get("uptimenotify")
						+"' where id='"+newData[0].get("id")+"' ";
				LOGGING.info("check uptime event query: "+checkuptimeevent);
				rs = conn.prepareStatement(checkuptimeevent).executeQuery();
				if(rs.next()){
					uptimeeventpresence=true;
				}
				rs.close();
				LOGGING.info("Is uptime event already present: "+uptimeeventpresence);
				if(!uptimeeventpresence){
					conn.prepareStatement(query).executeUpdate();
					conn.commit();
				}else{
					conn.prepareStatement(updateuptimeevent).executeUpdate();
					conn.commit();
				}
					
			}catch(Exception e){
				e.printStackTrace();
				LOGGING.error(e.getMessage());
			}
		}
	}
	
	public static class CEPListenerUptimeVoice implements UpdateListener{
		ResultSet rs = null;
		boolean uptimeeventpresence=false;
		@Override
		public void update(EventBean[] newData, EventBean[] oldData) {
			System.out.println("Uptime Alarm Received: "+newData[0].getUnderlying());
			
			String query = "insert into uptime_management(id,alarm_id,functional_alarm_id,eventdate,hostname,severity,uptime_mgmt_notification,ealarm_voice,ealarm_voice_text)"
					+ " values('"+newData[0].get("id")+"','"+newData[0].get("aid_alarm_id")+"','"+newData[0].get("functional_aid_alarm_id")
					+"','"+newData[0].get("eventdate")+"','"+newData[0].get("funchostname")+"','"+newData[0].get("severity")
					+"','"+newData[0].get("uptimenotify")+"','"+newData[0].get("ealarm_voice")+"','"+newData[0].get("ealarm_voice_text")+"')";
			
			try{
				LOGGING.info("query in up-alarm reception: "+query);
				String checkuptimeevent = "select id from uptime_management where id='"+newData[0].get("id")+"'";
				String updateuptimeevent = "update uptime_management set uptime_mgmt_notification='"+newData[0].get("uptimenotify")
						+"' where id='"+newData[0].get("id")+"' ";
				LOGGING.info("check uptime event query: "+checkuptimeevent);
				rs = conn.prepareStatement(checkuptimeevent).executeQuery();
				if(rs.next()){
					uptimeeventpresence=true;
				}
				rs.close();
				LOGGING.info("Is uptime event already present: "+uptimeeventpresence);
				if(!uptimeeventpresence){
					conn.prepareStatement(query).executeUpdate();
					conn.commit();
				}else{
					conn.prepareStatement(updateuptimeevent).executeUpdate();
					conn.commit();
				}
					
			}catch(Exception e){
				e.printStackTrace();
				LOGGING.error(e.getMessage());
			}
		}
	}
	
	public static class CEPListenerUptimeSar implements UpdateListener{
		ResultSet rs = null;
		boolean uptimeeventpresence=false;
		@Override
		public void update(EventBean[] newData, EventBean[] oldData) {
			System.out.println("Uptime Alarm Received: "+newData[0].getUnderlying());
			
			String query = "insert into uptime_management(id,alarm_id,functional_alarm_id,eventdate,hostname,severity,uptime_mgmt_notification,sar,sar_text)"
					+ " values('"+newData[0].get("id")+"','"+newData[0].get("aid_alarm_id")+"','"+newData[0].get("functional_aid_alarm_id")
					+"','"+newData[0].get("eventdate")+"','"+newData[0].get("funchostname")+"','"+newData[0].get("severity")
					+"','"+newData[0].get("uptimenotify")+"','"+newData[0].get("sar")+"','"+newData[0].get("sar_text")+"')";
			
			try{
				LOGGING.info("query in up-alarm reception: "+query);
				String checkuptimeevent = "select id from uptime_management where id='"+newData[0].get("id")+"'";
				String updateuptimeevent = "update uptime_management set uptime_mgmt_notification='"+newData[0].get("uptimenotify")
						+"' where id='"+newData[0].get("id")+"' ";
				LOGGING.info("check uptime event query: "+checkuptimeevent);
				rs = conn.prepareStatement(checkuptimeevent).executeQuery();
				if(rs.next()){
					uptimeeventpresence=true;
				}
				rs.close();
				LOGGING.info("Is uptime event already present: "+uptimeeventpresence);
				if(!uptimeeventpresence){
					conn.prepareStatement(query).executeUpdate();
					conn.commit();
				}else{
					conn.prepareStatement(updateuptimeevent).executeUpdate();
					conn.commit();
				}
					
			}catch(Exception e){
				e.printStackTrace();
				LOGGING.error(e.getMessage());
			}
		}
	}

	public static class CEPListenerUptimeOpenAlerts implements UpdateListener{
		ResultSet rs = null;
		boolean uptimeeventpresence=false;
		@Override
		public void update(EventBean[] newData, EventBean[] oldData) {
			System.out.println("Uptime Alarm Received: "+newData[0].getUnderlying());
			
			String query = "insert into uptime_management(id,alarm_id,functional_alarm_id,eventdate,hostname,severity,uptime_mgmt_notification,openalert,openalert_text)"
					+ " values('"+newData[0].get("id")+"','"+newData[0].get("aid_alarm_id")+"','"+newData[0].get("functional_aid_alarm_id")
					+"','"+newData[0].get("eventdate")+"','"+newData[0].get("funchostname")+"','"+newData[0].get("severity")
					+"','"+newData[0].get("uptimenotify")+"','"+newData[0].get("openalert")+"','"+newData[0].get("openalert_text")+"')";
			
			try{
				LOGGING.info("query in up-alarm reception: "+query);
				String checkuptimeevent = "select id from uptime_management where id='"+newData[0].get("id")+"'";
				String updateuptimeevent = "update uptime_management set uptime_mgmt_notification='"+newData[0].get("uptimenotify")
						+"' where id='"+newData[0].get("id")+"' ";
				LOGGING.info("check uptime event query: "+checkuptimeevent);
				rs = conn.prepareStatement(checkuptimeevent).executeQuery();
				if(rs.next()){
					uptimeeventpresence=true;
				}
				rs.close();
				LOGGING.info("Is uptime event already present: "+uptimeeventpresence);
				if(!uptimeeventpresence){
					conn.prepareStatement(query).executeUpdate();
					conn.commit();
				}else{
					conn.prepareStatement(updateuptimeevent).executeUpdate();
					conn.commit();
				}
					
			}catch(Exception e){
				e.printStackTrace();
				LOGGING.error(e.getMessage());
			}
		}
	}

	public static class CEPListenerUptimePlatformEvents implements UpdateListener{
		ResultSet rs = null;
		boolean uptimeeventpresence=false;
		@Override
		public void update(EventBean[] newData, EventBean[] oldData) {
			System.out.println("Uptime Alarm Received: "+newData[0].getUnderlying());
			
			String query = "insert into uptime_management(id,alarm_id,functional_alarm_id,eventdate,hostname,severity,uptime_mgmt_notification,platform_events,platform_events_text)"
					+ " values('"+newData[0].get("id")+"','"+newData[0].get("aid_alarm_id")+"','"+newData[0].get("functional_aid_alarm_id")
					+"','"+newData[0].get("eventdate")+"','"+newData[0].get("funchostname")+"','"+newData[0].get("severity")
					+"','"+newData[0].get("uptimenotify")+"','"+newData[0].get("platform_events")+"','"+newData[0].get("platform_events_text")+"')";
			
			try{
				LOGGING.info("query in up-alarm reception: "+query);
				String checkuptimeevent = "select id from uptime_management where id='"+newData[0].get("id")+"' and hostname='"+newData[0].get("funchostname")+"'";
				String updateuptimeevent = "update uptime_management set uptime_mgmt_notification='"+newData[0].get("uptimenotify")
						+"' where id='"+newData[0].get("id")+"' ";
				LOGGING.info("check uptime event query: "+checkuptimeevent);
				rs = conn.prepareStatement(checkuptimeevent).executeQuery();
				if(rs.next()){
					uptimeeventpresence=true;
				}
				rs.close();
				LOGGING.info("Is uptime event already present: "+uptimeeventpresence);
				if(!uptimeeventpresence){
					conn.prepareStatement(query).executeUpdate();
					conn.commit();
				}else{
					conn.prepareStatement(updateuptimeevent).executeUpdate();
					conn.commit();
				}
					
			}catch(Exception e){
				e.printStackTrace();
				LOGGING.error(e.getMessage());
			}
		}
	}*/

	public static class CEPListenerUptimePlatformEvents implements UpdateListener{
		//boolean uptimeeventpresence=false;
		ResultSet rspfuptime=null;
		@Override
		public void update(EventBean[] newData, EventBean[] oldData) {
			System.out.println("Uptime Platform Alarm Received: "+newData[0].getUnderlying());
			
			/*Thread uptimepfdbTh = new Thread(new UptimePfDbInsert(newData[0].get("id").toString(),newData[0].get("aid_alarm_id").toString(),
					newData[0].get("functional_aid_alarm_id").toString(),newData[0].get("eventdate").toString(),newData[0].get("funchostname").toString(),
					newData[0].get("severity").toString(),newData[0].get("uptimenotify").toString(),newData[0].get("alarm_text").toString(),sendmail));
			uptimepfdbTh.start();*/
			
			String query = "insert into uptime_management_new(id,alarm_id,functional_alarm_id,eventdate,hostname,severity,uptime_mgmt_notification,alarm_text)"
					+ " values('"+newData[0].get("uptimepfid")+evaluteCurrentDate()+"','"+newData[0].get("aid_alarm_id")+"','"+newData[0].get("functional_aid_alarm_id")
					+"','"+newData[0].get("eventdate")+"','"+newData[0].get("funchostname")+"','"+newData[0].get("severity")
					+"','"+newData[0].get("uptimenotify")+"','"+newData[0].get("message")+"')";
			
			try{
				LOGGING.info("query in up-alarm reception: "+query);
				String checkuptimeevent = "select id from uptime_management_new where alarm_id='"+newData[0].get("aid_alarm_id")+"' and"
						+ " hostname='"+newData[0].get("funchostname")+"'";
				String updateuptimeevent = "update uptime_management_new set eventdate='"+evaluteCurrentDateTimestamp()+"' where "
						+ "alarm_id='"+newData[0].get("aid_alarm_id")+"' and hostname='"+newData[0].get("funchostname")+"'";
				LOGGING.info("check uptime event query: "+checkuptimeevent);
				rspfuptime = conn.prepareStatement(checkuptimeevent).executeQuery();
				if (rspfuptime != null){
					if (!rspfuptime.next()) {
						LOGGING.info("Platform Uptime event is not present. Inserting new Record!");
						//uptimeeventpresence=true;
						conn.prepareStatement(query).executeUpdate();
						conn.commit();
						//sendmail.generateAndSendEmail(newData[0].get("aid_alarm_id").toString(), newData[0].get("functional_aid_alarm_id").toString(),newData[0].get("uptimepfid").toString(), newData[0].get("uptimenotify").toString());
						sendmail.generateAndSendEmailUpTime(newData[0].get("functional_aid_alarm_id").toString(),
								newData[0].get("uptimepfid").toString() + evaluteCurrentDate(),
								newData[0].get("uptimenotify").toString(), newData[0].get("funchostname").toString(),
								newData[0].get("eventdate").toString());
					} /*else{
						conn.prepareStatement(updateuptimeevent).executeUpdate();
						conn.commit();
						}*/
					rspfuptime.close();
				}
					
			}catch(Exception e){
				e.printStackTrace();
				LOGGING.error(e.getMessage());
			}
		}
	}
	
	public static void sendAlarm(EventPojo ep){
		
		/*Configuration cepConfig = new Configuration();
		ConfigurationDBRef dbConfig = new ConfigurationDBRef();
		cepConfig.addEventType("IncomingDataStream", EventPojo.class.getName());
		dbConfig.setDriverManagerConnection(serverData.getOutDriverDB(),serverData.getOuturl(),serverData.getOutuserId(),serverData.getOutpassword());
		//dbConfig.setConnectionLifecycleEnum(ConfigurationDBRef.ConnectionLifecycleEnum.POOLED);
		cepConfig.addDatabaseReference("db1", dbConfig);
		cepConfig.configure("swisscom.esper.cfg.xml");
		EPServiceProvider cep = EPServiceProviderManager.getProvider("UptimeEngine", cepConfig);
		String uptimeAlarms = serverData.getUptimeAlarms();
		StringTokenizer uptimeAlarmsTokens = new StringTokenizer(uptimeAlarms, ",");
		String uptimeMgmtNotification = null; 
		String tagtext = null;
		EPStatement uptimesmsrule1 = null;
		EPStatement uptimesmsrule2 = null;
		EPStatement uptimesmsrule3 = null;
		EPStatement uptimevoicerule1 = null;
		EPStatement uptimevoicerule2 = null;
		EPStatement uptimevoicerule3 = null;
		EPStatement uptimesarrule1 = null;
		EPStatement uptimesarrule2 = null;
		EPStatement uptimesarrule3 = null;
		EPStatement uptimeopenalertrule = null;
		EPStatement uptimeplatformeventcpuutilrule1 = null;
		EPStatement uptimeplatformeventcpuutilrule2 = null;
		EPStatement uptimeplatformeventcpuutilrule3 = null;
		EPStatement uptimeplatformeventcpuutilrule4 = null;
		EPStatement uptimeplatformeventcpuutilrule5 = null;
		EPStatement uptimeplatformeventcpuutilrule6 = null;
		EPStatement uptimeplatformeventcpuutilrule7 = null;
		EPStatement uptimeplatformeventcpuutilrule8 = null;*/
		
		//cepRT = cep.getEPRuntime();
		//cepAdm = cep.getEPAdministrator();
		
		try{
			/*XPathFactory xpf = XPathFactory.newInstance();
			XPath xPath = xpf.newXPath();
	        //InputSource inputSource = new InputSource(new FileInputStream("UpTimeRule.xml"));
	        InputSource inputSource = new InputSource(new FileInputStream(serverData.getUptimerulexmlname()));
	        XPathExpression ruleExpression = xPath.compile("node()");
	        //NodeList ruleNodes = (NodeList) xPath.evaluate("/UpTimeRules/*", inputSource, XPathConstants.NODESET);
	        NodeList ruleNodes = (NodeList) xPath.evaluate(serverData.getUptimerulexmlrootnodepath(), inputSource, XPathConstants.NODESET);
	        for(int ruleNode = 0; ruleNode < ruleNodes.getLength(); ruleNode++) {
	            Node ruleElement = ruleNodes.item(ruleNode);
	            String ruleStmt = ruleExpression.evaluate(ruleElement, XPathConstants.STRING).toString();
	            LOGGING.info("Rule Value: "+ruleStmt);
	            //uptimeRule = cepAdm.createEPL(ruleStmt.contains("&gt;")?replaceXmlSplChar(ruleStmt):(ruleStmt.contains("&lt;")?replaceXmlSplChar(ruleStmt):ruleStmt));
	            uptimeRule = cepAdm.createEPL(replaceXmlSplChar(ruleStmt));
	            if(ruleStmt.contains(serverData.getUptimeAlarmAgentPA()))
	            	 uptimeRule.addListener(new CEPListenerUptimePlatformEvents());
	            else 
	            	uptimeRule.addListener(new CEPListenerUptime());
	            LOGGING.info("Sending Uptime Alarm: "+ep);*/
			LOGGING.error("inside the Uptime send Alarm method"+ep);
				cepRT.sendEvent(ep);
	        //}
		}catch(Exception e){
			e.printStackTrace();
			LOGGING.error(e.getMessage());
		}
		
		/*while(uptimeAlarmsTokens.hasMoreElements()){
			if(uptimeAlarmsTokens.nextToken().matches(ep.getAid_alarm_id())){
				/*
				if(ep.getMessage().contains(serverData.getScomMatchingText())){
					tagtext = "get"+ep.getAid_alarm_id() +"_rule_"+ serverData.getScomMatchingText().toLowerCase()+"_"
												+ep.getScomval()+"_nprio_"+ep.getNprioval();
					uptimeMgmtNotification = evaluteDynamicGetter(tagtext);
					LOGGING.info("Dynamic getter evalutation in uptime: "+uptimeMgmtNotification);
					
					uptimerulesms = cepAdm.createEPL("select id,aid_alarm_id,functional_aid_alarm_id,eventdate,funchostname,severity,'"
								+uptimeMgmtNotification+"' as uptimenotify, 'Y' as ealarm_sms, message as ealarm_sms_text from Alarm"
										+ "(aid_alarm_id in('"+ep.getAid_alarm_id()+"')).win:time(10 min)");
					uptimerulesms.addListener(new CEPListenerUptimeSms());
					
				}else if(ep.getMessage().contains("LA:")){
					
					tagtext = "get"+ep.getAid_alarm_id() +"_rule_"+ serverData.getLAMatchingText().toLowerCase()+"_"
							+ep.getLaval()+"_"+serverData.getZHMatchingText().toLowerCase()+"_"+ep.getZhval();
					uptimeMgmtNotification = evaluteDynamicGetter(tagtext);
					LOGGING.info("Dynamic getter evalutation in uptime: "+uptimeMgmtNotification);

					uptimerulevoice = cepAdm.createEPL("select id,aid_alarm_id,functional_aid_alarm_id,eventdate,funchostname,severity,'"
									+uptimeMgmtNotification+"' as uptimenotify, 'Y' as ealarm_voice, message as ealarm_voice_text from Alarm"
									+ "(aid_alarm_id in('"+ep.getAid_alarm_id()+"')).win:time(10 min)");
					uptimerulevoice.addListener(new CEPListenerUptimeVoice());
				}else if(ep.getMessage().contains("SAR: iowaits")){
					int sarval = Integer.parseInt(ep.getMessage().substring(ep.getMessage().lastIndexOf(">")+1).trim());
						if(sarval > 10){
							uptimerulesario = cepAdm.createEPL("select id,aid_alarm_id,functional_aid_alarm_id,eventdate,funchostname,severity,"
											+ "'Possibility of Performance issues' as uptimenotify, 'Y' as sar, message as sar_text from Alarm"
											+ "(aid_alarm_id in('"+ep.getAid_alarm_id()+"')).win:time(10 min)");
							uptimerulesario.addListener(new CEPListenerUptimeSar());
						}
				}else if(ep.getMessage().contains("SAR: idle")){
					int sarval = Integer.parseInt(ep.getMessage().substring(ep.getMessage().lastIndexOf(">")+1).trim());
					if(sarval < 35){
						uptimerulesaridl = cepAdm.createEPL("select id,aid_alarm_id,functional_aid_alarm_id,eventdate,funchostname,severity,"
										+ "'Possibility of Performance issues' as uptimenotify, 'Y' as sar, message as sar_text from Alarm"
										+ "(aid_alarm_id in('"+ep.getAid_alarm_id()+"')).win:time(10 min)");
						uptimerulesaridl.addListener(new CEPListenerUptimeSar());
					}
				}else if(ep.getMessage().contains("load average")){
					int loadval = Integer.parseInt(ep.getMessage().substring(ep.getMessage().lastIndexOf(":")+1).trim());
						if(loadval > 140){
							uptimeruleloadavg = cepAdm.createEPL("select id,aid_alarm_id,functional_aid_alarm_id,eventdate,funchostname,severity,"
												+ "'Possibility of Performance issues' as uptimenotify, 'Y' as sar, message as sar_text from Alarm"
												+ "(aid_alarm_id in('"+ep.getAid_alarm_id()+"')).win:time(10 min)");
							uptimeruleloadavg.addListener(new CEPListenerUptimeSar());
					}
				}
				else if(ep.getMessage().contains("offene")){
					int warnval = Integer.parseInt(ep.getMessage().substring(ep.getMessage().indexOf("Warning:")+"Warning:".length(), ep.getMessage().indexOf("offene")).trim());
					int critval = Integer.parseInt(ep.getMessage().substring(ep.getMessage().indexOf("CRITIC@L:")+"CRITIC@L:".length(), ep.getMessage().lastIndexOf("offene")).trim());
					
					if(warnval > 150 && critval > 900){
						uptimeruleopenalerts = cepAdm.createEPL("select id,aid_alarm_id,functional_aid_alarm_id,eventdate,funchostname,severity,"
											   + "'Possibility of Performance issues & Alert Support Personnel.' as uptimenotify, 'Y' as openalert, message as openalert_text from Alarm"
											   + "(aid_alarm_id in('"+ep.getAid_alarm_id()+"')).win:time(10 min)");
						uptimeruleopenalerts.addListener(new CEPListenerUptimeOpenAlerts());
					}
				}
				else if(ep.getAid_alarm_id().contains("cpuutil")){
					String query = "select count(1) as count from normalised_sc_event_view where alarm_id = '"+ep.getAid_alarm_id()+"' and "
							+ "hostname_refined ='"+ep.getFunchostname()+"'";
					int count = 0;
					String uptimenotify = null;
					String consolidateId = null;
					try{
						rs = conn.prepareStatement(query).executeQuery();
						while(rs.next()){
							 count = Integer.parseInt(rs.getString("count"));
						}
						rs.close();
						if(count>5){
							uptimenotify="CPU Util events crossed threshold limit. Possible performance and dimensioning issues. Please take necessary action.";
							consolidateId = "PLATFORM_CPUUTIL_"+evaluteCurrentDate();
							uptimeruleplatformevents = cepAdm.createEPL("select '"+consolidateId+"' as id,aid_alarm_id,functional_aid_alarm_id,eventdate,funchostname,"
									                   + "severity,'"+uptimenotify+"' as uptimenotify,'Y' as platform_events, message as "
									                   + "platform_events_text from Alarm(aid_alarm_id in('"+ep.getAid_alarm_id()+"')).win:time(10 min)");
							uptimeruleplatformevents.addListener(new CEPListenerUptimePlatformEvents());
						}
					}catch(Exception e){
						LOGGING.info(e.getMessage());
					}
				}
				*/
				/*
				uptimesmsrule1 = cepAdm.createEPL(replaceXmlSplChar(serverData.getUptimeealarmsmsrule1()));
				uptimesmsrule2 = cepAdm.createEPL(replaceXmlSplChar(serverData.getUptimeealarmsmsrule2()));
				uptimesmsrule3 = cepAdm.createEPL(replaceXmlSplChar(serverData.getUptimeealarmsmsrule3()));
				uptimevoicerule1 = cepAdm.createEPL(replaceXmlSplChar(serverData.getUptimeealarmvoicerule1()));
				uptimevoicerule2 = cepAdm.createEPL(replaceXmlSplChar(serverData.getUptimeealarmvoicerule2()));
				uptimevoicerule3 = cepAdm.createEPL(replaceXmlSplChar(serverData.getUptimeealarmvoicerule3()));
				uptimesarrule1 = cepAdm.createEPL(replaceXmlSplChar(serverData.getUptimeealarmsarrule1()));
				uptimesarrule2 = cepAdm.createEPL(replaceXmlSplChar(serverData.getUptimeealarmsarrule2()));
				uptimesarrule3 = cepAdm.createEPL(replaceXmlSplChar(serverData.getUptimeealarmsarrule3()));
				uptimeopenalertrule = cepAdm.createEPL(replaceXmlSplChar(serverData.getUptimeopenalertrule()));
				uptimeplatformeventcpuutilrule1 = cepAdm.createEPL(replaceXmlSplChar(serverData.getUptimeplatformeventscpuutilrule1()));
				uptimeplatformeventcpuutilrule2 = cepAdm.createEPL(replaceXmlSplChar(serverData.getUptimeplatformeventscpuutilrule2()));
				uptimeplatformeventcpuutilrule3 = cepAdm.createEPL(replaceXmlSplChar(serverData.getUptimeplatformeventscpuutilrule3()));
				uptimeplatformeventcpuutilrule4 = cepAdm.createEPL(replaceXmlSplChar(serverData.getUptimeplatformeventscpuutilrule4()));
				uptimeplatformeventcpuutilrule5 = cepAdm.createEPL(replaceXmlSplChar(serverData.getUptimeplatformeventscpuutilrule5()));
				uptimeplatformeventcpuutilrule6 = cepAdm.createEPL(replaceXmlSplChar(serverData.getUptimeplatformeventscpuutilrule6()));
				uptimeplatformeventcpuutilrule7 = cepAdm.createEPL(replaceXmlSplChar(serverData.getUptimeplatformeventscpuutilrule7()));
				uptimeplatformeventcpuutilrule8 = cepAdm.createEPL(replaceXmlSplChar(serverData.getUptimeplatformeventscpuutilrule8()));
				
				uptimesmsrule1.addListener(new CEPListenerUptimeSms());
				uptimesmsrule2.addListener(new CEPListenerUptimeSms());
				uptimesmsrule3.addListener(new CEPListenerUptimeSms());
				uptimevoicerule1.addListener(new CEPListenerUptimeVoice());
				uptimevoicerule2.addListener(new CEPListenerUptimeVoice());
				uptimevoicerule3.addListener(new CEPListenerUptimeVoice());
				uptimesarrule1.addListener(new CEPListenerUptimeSar());
				uptimesarrule2.addListener(new CEPListenerUptimeSar());
				uptimesarrule3.addListener(new CEPListenerUptimeSar());
				uptimeopenalertrule.addListener(new CEPListenerUptimeOpenAlerts());
				uptimeplatformeventcpuutilrule1.addListener(new CEPListenerUptimePlatformEvents());
				uptimeplatformeventcpuutilrule2.addListener(new CEPListenerUptimePlatformEvents());
				uptimeplatformeventcpuutilrule3.addListener(new CEPListenerUptimePlatformEvents());
				uptimeplatformeventcpuutilrule4.addListener(new CEPListenerUptimePlatformEvents());
				uptimeplatformeventcpuutilrule5.addListener(new CEPListenerUptimePlatformEvents());
				uptimeplatformeventcpuutilrule6.addListener(new CEPListenerUptimePlatformEvents());
				uptimeplatformeventcpuutilrule7.addListener(new CEPListenerUptimePlatformEvents());
				uptimeplatformeventcpuutilrule8.addListener(new CEPListenerUptimePlatformEvents());
				
				LOGGING.info("Sending uptime Alarm: "+ep);
				cepRT.sendEvent(ep);
				
			}
		}*/
	}
}
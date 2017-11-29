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
	
	
	public static class CEPListenerUptimePlatformEvents implements UpdateListener{
		ResultSet rspfuptime=null;
		@Override
		public void update(EventBean[] newData, EventBean[] oldData) {
			System.out.println("Uptime Platform Alarm Received: "+newData[0].getUnderlying());
			
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
						conn.prepareStatement(query).executeUpdate();
						conn.commit();
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
		
		try{
			LOGGING.error("inside the Uptime send Alarm method"+ep);
				cepRT.sendEvent(ep);
	        }catch(Exception e){
			e.printStackTrace();
			LOGGING.error(e.getMessage());
		}		
	}
}

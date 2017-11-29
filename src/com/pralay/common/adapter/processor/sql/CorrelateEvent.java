package com.pralay.common.adapter.processor.sql;

import java.io.FileInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.StringTokenizer;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.ConfigurationDBRef;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPOnDemandPreparedQuery;
import com.espertech.esper.client.EPOnDemandQueryResult;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.pralay.common.actuation.SendMail;
import com.pralay.common.adapter.builder.CommonAdapterBuilder;
import com.pralay.configuration.model.DatabaseConnection;
import com.pralay.configuration.model.EventPojo;
import com.pralay.configuration.model.ServerData;

public class CorrelateEvent {
	private static ServerData serverData;
	private static SendMail sendmail;
	private static ResultSet rsdowntime = null;
	DatabaseConnection dbc;
	private static String correlationAlarms = null;
	private static StringTokenizer correlationAlarmsTokens = null;
	private static String rulenametag = null;
	private static String gettermethod = null;
	private static String correlationalarmtypes = null;
	
	private static Connection conn = null;
	private static EPServiceProvider cep = null;
	private static EPRuntime cepRT=null;
	private static EPAdministrator cepAdm=null;
	private static EPStatement downtimeRule=null;
	
	private static final Logger LOGGING = LoggerFactory.getLogger(CorrelateEvent.class);
	
	public CorrelateEvent(ServerData serverData){
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
	
	public static class CEPListener implements UpdateListener{
		boolean correlateeventpresence=false;
		@Override
		public void update(EventBean[] newData, EventBean[] oldData) {
			System.out.println("Downtime-Uptime Alarm Received: "+newData[0].getUnderlying());
			
			String correlate_details="This "+newData[0].get("a1.functional_aid_alarm_id")+" may relate to "+newData[0].get("a2.functional_aid_alarm_id")+". Please check "+newData[0].get("a2.functional_aid_alarm_id")+" details for further investigation";
			String query = "insert into correlation(id,alarm_id,functional_alarm_id,eventdate,hostname,severity,correlate_id,correlate_eventdate,"
					+ "correlate_details,impact,resolution)"
					+ " values('"+newData[0].get("a1.id")+"','"+newData[0].get("a1.aid_alarm_id")+"','"+newData[0].get("a1.functional_aid_alarm_id")
					+"','"+newData[0].get("a1.eventdate")
					+"','"+newData[0].get("a1.funchostname")+"','"+newData[0].get("a1.severity")+"','"+newData[0].get("a2.functional_aid_alarm_id")+"','"+newData[0].get("a2.eventdate")
					+"','"+correlate_details+"','"+newData[0].get("a1.impact_alarm_id")+"','"+newData[0].get("a2.resolution_alarm_id")+"')";
			try{
				LOGGING.info("query in alarm reception: "+query);
				
				String checkcorrelateevent = "select alarm_id from correlation where alarm_id='"+newData[0].get("a1.aid_alarm_id")+"' "
						+ "and correlate_id='"+newData[0].get("a2.functional_aid_alarm_id")+"' and eventdate='"+newData[0].get("a1.eventdate")+"'";
				
				LOGGING.info("check correlate event query: "+checkcorrelateevent);
				rsdowntime = conn.prepareStatement(checkcorrelateevent).executeQuery();
				if(!rsdowntime.next()){
					//correlateeventpresence=true;
					LOGGING.info("Correlate event is not present. Inserting new Record!");
					conn.prepareStatement(query).executeUpdate();
					conn.commit();
					//sendmail.generateAndSendEmail(newData[0].get("a1.aid_alarm_id").toString(), newData[0].get("a2.functional_aid_alarm_id").toString(),newData[0].get("a1.id").toString(), correlate_details);
					SendMail.generateAndSendEmailDownTime(newData[0].get("a1.functional_aid_alarm_id").toString(), newData[0].get("a2.functional_aid_alarm_id").toString(),newData[0].get("a1.id").toString(), correlate_details,
							newData[0].get("a2.funchostname").toString(),newData[0].get("a1.eventdate").toString(),
							newData[0].get("a2.eventdate").toString(),newData[0].get("a1.impact_alarm_id").toString(),newData[0].get("a2.resolution_alarm_id").toString());
				}
				rsdowntime.close();
				
			}catch(Exception e){
				e.printStackTrace();
				LOGGING.error(e.getMessage());
			}
		}
	}
	
	
	public static void sendAlarm(EventPojo ep){
		
		
		try{
			LOGGING.error("inside the send Alarm methode"+ep);
				cepRT.sendEvent(ep);
		}catch(Exception e){
			e.printStackTrace();
			LOGGING.error(e.getMessage());
		}
		
	}
}

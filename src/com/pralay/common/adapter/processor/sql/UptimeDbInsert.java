package com.pralay.common.adapter.processor.sql;

import java.sql.Connection;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pralay.common.actuation.SendMail;
import com.pralay.common.adapter.builder.CommonAdapterBuilder;


public class UptimeDbInsert implements Runnable{
	private static SendMail sendmail;
	private static ResultSet rsuptime = null;
	private static Connection conn = null;
	private static String id = null;
	private static String alarm_id = null;
	private static String functional_alarm_id = null;
	private static String eventdate = null;
	private static String hostname = null;
	private static String severity = null;
	private static String uptimenotify = null;
	private static String alarmtext = null;
	private static final Logger LOGGING = LoggerFactory.getLogger(UptimeDbInsert.class);
	
	public UptimeDbInsert(String id,String alarm_id, String functional_alarm_id, String eventdate, String hostname, String severity, 
			String uptimenotify, String alarmtext, SendMail sendmail){
		this.id = id;
		this.alarm_id = alarm_id;
		this.functional_alarm_id = functional_alarm_id;
		this.eventdate = eventdate;
		this.hostname = hostname;
		this.severity = severity;
		this.uptimenotify = uptimenotify;
		this.alarmtext = alarmtext;
		conn = CommonAdapterBuilder.conn;
		this.sendmail = sendmail;
	} 
	
	@Override
    public void run() {
		while(true){
            try{
            	LOGGING.info("<-- Inside Uptime DB Insert -->");
            	
            	String query = "insert into uptime_management_new(id,alarm_id,functional_alarm_id,eventdate,hostname,severity,uptime_mgmt_notification,alarm_text)"
				+ " values('"+id+"','"+alarm_id+"','"+functional_alarm_id+"','"+eventdate+"','"+hostname+"','"+severity+"','"+uptimenotify+"','"+alarmtext+"')";
		
				LOGGING.info("query in up-alarm reception: "+query);
				String checkuptimeevent = "select id from uptime_management_new where id='"+id+"'";
				String updateuptimeevent = "update uptime_management_new set uptime_mgmt_notification='"+uptimenotify+"' where id='"+id+"' ";
				LOGGING.info("check uptime event query: "+checkuptimeevent);
				rsuptime = conn.prepareStatement(checkuptimeevent).executeQuery();
				if(!rsuptime.next()){
					//uptimeeventpresence=true;
					LOGGING.info("Uptime event is not present. Inserting new Record!");
					conn.prepareStatement(query).executeUpdate();
					conn.commit();
					//sendmail.generateAndSendEmail(newData[0].get("aid_alarm_id").toString(), newData[0].get("functional_aid_alarm_id").toString(),newData[0].get("id").toString(), newData[0].get("uptimenotify").toString());
				}else{
					conn.prepareStatement(updateuptimeevent).executeUpdate();
					conn.commit();
				}
				rsuptime.close();
				
				
            }catch (Exception e) {
            	e.printStackTrace();
            	LOGGING.error(e.getMessage());
            }
            LOGGING.info("<-- Inside Uptime DB Insert ends for a record -->");
		}
    }

}

package com.pralay.common.adapter.processor.sql;

import java.sql.Connection;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pralay.common.actuation.SendMail;
import com.pralay.common.actuation.SendMailImp;
import com.pralay.common.adapter.builder.CommonAdapterBuilder;

public class CorrelateDBInsert implements Runnable{
	private static SendMail sendmail;
	private static ResultSet rsdowntime = null;
	private static Connection conn = null;
	private static String id = null;
	private static String alarm_id = null;
	private static String functional_alarm_id = null;
	private static String eventdate = null;
	private static String hostname = null;
	private static String severity = null;
	private static String correlate_id = null;
	private static String correlate_eventdate = null;
	private static String correlate_details = null;
	boolean correlateeventpresence=false;
	private static final Logger LOGGING = LoggerFactory.getLogger(CorrelateDBInsert.class);
	
	public CorrelateDBInsert(String id,String alarm_id, String functional_alarm_id, String eventdate, String hostname, String severity, String correlate_id,
			String correlate_eventdate, SendMail sendmail){
		this.id = id;
		this.alarm_id = alarm_id;
		this.functional_alarm_id = functional_alarm_id;
		this.eventdate = eventdate;
		this.hostname = hostname;
		this.severity = severity;
		this.correlate_id = correlate_id;
		this.correlate_eventdate = correlate_eventdate;
		conn = CommonAdapterBuilder.conn;
		this.sendmail = sendmail;
	}
	
	@Override
    public void run() {
            try{
            	LOGGING.info("<-- Inside the correlate DBInsert producer -->");
            	correlate_details="This "+functional_alarm_id+" may relate to "+correlate_id+". Please check "+correlate_id+" details for further investigation";
    			String query = "insert into correlation(id,alarm_id,functional_alarm_id,eventdate,hostname,severity,correlate_id,correlate_eventdate,"
    					+ "correlate_details)"
    					+ " values('"+id+"','"+alarm_id+"','"+functional_alarm_id+"','"+eventdate+"','"+hostname+"','"+severity+"','"
    					+correlate_id+"','"+correlate_eventdate+"','"+correlate_details+"')";
    			
    				LOGGING.info("query in alarm reception: "+query);
    				
    				/*String checkcorrelateevent = "select alarm_id from correlation where alarm_id='"+alarm_id+"' "
    						+ "and correlate_id='"+correlate_id+"' and eventdate='"+eventdate+"'";    				
    				String checkcorrelateevent = "select distinct id from correlation where id in('"+id+"') and alarm_id in('"+alarm_id+"') and "
    						+ "correlate_id in('"+correlate_id+"') and eventdate in('"+eventdate+"')";*/
    				
    				String checkcorrelateevent = "select distinct id from correlation where id in('"+id+"')";
    				
    				LOGGING.info("check correlate event query: "+checkcorrelateevent);
    				rsdowntime = conn.prepareStatement(checkcorrelateevent).executeQuery();
    				if(!rsdowntime.next()){
    					//correlateeventpresence=true;
    					LOGGING.info("Correlate event is not present. Inserting new Record!");
    					conn.prepareStatement(query).executeUpdate();
    					conn.commit();
    				}
    				rsdowntime.close();
    				/*LOGGING.info("Is correlate event already present: "+correlateeventpresence);
    				if(!correlateeventpresence){
    					conn.prepareStatement(query).executeUpdate();
    					conn.commit();
    					//Thread sendmailTh = new Thread(new SendMailImp(alarm_id, correlate_id,id, correlate_details,sendmail));
    					//sendmailTh.start();
    					//sendmail.generateAndSendEmail(alarm_id, correlate_id,id, correlate_details);
    				}*/
    			
            }catch (Exception e) {
            	e.printStackTrace();
				LOGGING.error(e.getMessage());
            }
            LOGGING.info("<-- Correlate DB Insert ends for a record -->"); 
    }	
}

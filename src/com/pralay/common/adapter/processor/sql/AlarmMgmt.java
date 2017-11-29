package com.pralay.common.adapter.processor.sql;

import java.sql.Connection;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pralay.common.adapter.builder.CommonAdapterBuilder;
import com.pralay.configuration.model.DatabaseConnection;
import com.pralay.configuration.model.ServerData;

public class AlarmMgmt {
	private ServerData serverData;
	private static final Logger LOGGING = LoggerFactory.getLogger(AlarmIdDeter.class);
	private static Connection conn = null;
	ResultSet rs = null;
	ResultSet rs1 = null;
	DatabaseConnection dbc;
	static String id=null;
	static String status=null;
	static String duration=null;
	String updatequery=null;
	boolean alarmPresence=false;
	boolean doUpdate=false;
	
	
	public AlarmMgmt(ServerData serverData){
		this.serverData = serverData;
		conn = CommonAdapterBuilder.conn;
	}
	
	public boolean alarmCheckPresence(String id) throws Exception{
		try{
			AlarmMgmt.id=id;
			//AlarmMgmt.conn = conn;
			LOGGING.info("<-- Checking data present for id? - "+id);
			//dbc = new DatabaseConnection(serverData);
			//conn=DatabaseConnection.makeConnection();
			//LOGGING.info("connection: "+conn.isClosed());
			//rs = conn.prepareStatement("select status,duration from normalised_sc_event_view where id='"+id+"'").executeQuery();
			rs = conn.prepareStatement(serverData.getCheckalarmsql()+"'"+id+"'").executeQuery();
			//LOGGING.info("rs: "+rs.getFetchSize());
			if(rs.next()){
				alarmPresence=true;
				status = rs.getString("status");
				duration = rs.getString("duration");
			}
			rs.close();
			//dbc.dropConnection();
			
		}catch(Exception e){
			e.printStackTrace();
			LOGGING.error(e.getMessage());
		}
		LOGGING.info("<-- Data presence status --> "+alarmPresence);
		return alarmPresence;
	}
	public void alarmUpdate(String status, String duration) throws Exception{
		try{
			//this.status = status;
			//this.duration = duration;
			/*LOGGING.info("class id: "+this.id);
			LOGGING.info("class status: "+this.status);
			LOGGING.info("class duration: "+this.duration);
			LOGGING.info("send status: "+status);
			LOGGING.info("send duration: "+duration);*/
			//AlarmMgmt.conn = conn;
			
			if(!AlarmMgmt.status.equalsIgnoreCase(status) && !AlarmMgmt.duration.equalsIgnoreCase(duration)){
				doUpdate=true;
				updatequery=serverData.getUpdatealarmsql()+" status='"+status+"', duration='"+duration+"' where id='"+AlarmMgmt.id+"'";
			}else if(AlarmMgmt.status.equalsIgnoreCase(status) && !AlarmMgmt.duration.equalsIgnoreCase(duration)){
				doUpdate=true;
				updatequery=serverData.getUpdatealarmsql()+" duration='"+duration+"' where id='"+AlarmMgmt.id+"'";
			}else if(!AlarmMgmt.status.equalsIgnoreCase(status) && AlarmMgmt.duration.equalsIgnoreCase(duration)){
				doUpdate=true;
				updatequery=serverData.getUpdatealarmsql()+" status='"+status+"' where id='"+AlarmMgmt.id+"'";
			}
			
			if(doUpdate){
				//dbc = new DatabaseConnection(serverData);
				//conn=DatabaseConnection.makeConnection();
				conn.prepareStatement(updatequery).executeUpdate();
				conn.commit();
				//dbc.dropConnection();
			}
			else
				LOGGING.info("<-- Nothing to Update for Id-->"+AlarmMgmt.id);
		}catch(Exception e){
			e.printStackTrace();
			LOGGING.error(e.getMessage());
		}
	}
	public void alarmInsert(String query) throws Exception {
		try{
			//AlarmMgmt.conn = conn;
			LOGGING.info("insert query: "+query);
			//dbc = new DatabaseConnection(serverData);
			//conn = DatabaseConnection.makeConnection();
			LOGGING.info("conn: "+conn.isClosed());
			//rs1 = AlarmMgmt.conn.prepareStatement("select message_text from alarm_id_determination").executeQuery();
			//LOGGING.info("simple: "+rs1.getFetchSize());
			conn.prepareStatement(query).executeUpdate();
			conn.commit();
			//rs1.close();
			//dbc.dropConnection();
            
		}catch(Exception e){
			e.printStackTrace();
			LOGGING.error(e.getMessage());
		}
	}
}
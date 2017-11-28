package com.pralay.configuration.model;

import java.sql.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.pralay.common.adapter.processor.sql.AlarmIdDeter;

public class DatabaseConnection {

	private static ServerData serverData;
	//private ServerData serverData;
	private static final Logger LOGGING = LoggerFactory.getLogger(AlarmIdDeter.class);
	private static Connection conn = null;
	private static String driverDB = null;
	private static String getUrl = null;
	private static String getUserId = null;
	private static String getPassword = null;
	
	public DatabaseConnection(ServerData serverData){
		//this.serverData = serverData;
		DatabaseConnection.serverData = serverData;
	}
	public static Connection makeConnection(){
		if(conn != null)
			return conn;
		
		driverDB = serverData.getOutdriverDB();
		getUrl = serverData.getOuturl();
		getUserId = serverData.getOutuserId();
		getPassword = serverData.getOutpassword();
		
		return makeConnection(driverDB,getUrl,getUserId,getPassword); 
	}
	
	private static Connection makeConnection(String db, String url, String user, String pass){
	//public Connection makeConnection() throws Exception{
		try{
			LOGGING.info("<------- Making DataBase Connection ------->");
			
			ComboPooledDataSource cpds = new ComboPooledDataSource();
			cpds.setDriverClass(db);
			cpds.setJdbcUrl(url);
			cpds.setUser(user);
			cpds.setPassword(pass);
			
			/*cpds.setDriverClass(serverData.getDriverDB());
			cpds.setJdbcUrl(serverData.getUrl());
			cpds.setUser(serverData.getUserId());
			cpds.setPassword(serverData.getPassword());*/
			
			conn = cpds.getConnection();
			//Class.forName(db);
			//conn = DriverManager.getConnection(url,user,pass);
			conn.setAutoCommit(false);
		}catch(Exception e){
			e.printStackTrace();
			LOGGING.error(e.getMessage());
		}
		LOGGING.info("<------- Returning new DataBase Connection to caller ------->");
		return conn;
	}
	
	public void dropConnection() throws Exception{
		try{
			LOGGING.info("<------- Dropping DataBase Connection ------->");
			conn.commit();
			conn.close();
			LOGGING.info("<------- DataBase Connection Dropped ------->");
		}catch(Exception e){
			e.printStackTrace();
			LOGGING.error(e.getMessage());
		}
	}
}

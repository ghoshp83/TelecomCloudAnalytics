package com.pralay.configuration.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.ConfigurationDBRef;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.pralay.common.adapter.processor.sql.AlarmIdDeter;

public class EsperEngine {
	
	private static ServerData serverData;
	private static final Logger LOGGING = LoggerFactory.getLogger(AlarmIdDeter.class);
	private static EPServiceProvider cep = null;
	private static String driverDB = null;
	private static String getUrl = null;
	private static String getUserId = null;
	private static String getPassword = null;
	private static String espereventname = null;
	
	public EsperEngine(ServerData serverData){
		//this.serverData = serverData;
		EsperEngine.serverData = serverData;
	}
	
	public static EPServiceProvider makeEngine(){
		if(cep!=null)
			return cep;
		
		espereventname = serverData.getEspereventname();
		driverDB = serverData.getOutdriverDB();
		getUrl = serverData.getOuturl();
		getUserId = serverData.getOutuserId();
		getPassword = serverData.getOutpassword();
		
		return makeEngine(espereventname,driverDB,getUrl,getUserId,getPassword);
	}
	
	private static EPServiceProvider makeEngine(String event, String db, String url, String user, String pass){
		try{
			LOGGING.info("Creation of Esper Enginer Starts");
			Configuration cepConfig = new Configuration();
			ConfigurationDBRef dbConfig = new ConfigurationDBRef();
			cepConfig.addEventType(event, EventPojo.class.getName());
			dbConfig.setDriverManagerConnection(db,url,user,pass);
			//dbConfig.setConnectionLifecycleEnum(ConfigurationDBRef.ConnectionLifecycleEnum.POOLED);
			cepConfig.addDatabaseReference("db1", dbConfig);
			cepConfig.configure("telecom.esper.cfg.xml");
			cep = EPServiceProviderManager.getProvider("EsperEngine", cepConfig);
		}catch(Exception e){
			e.printStackTrace();
			LOGGING.error(e.getMessage());
		}
		LOGGING.info("Returning of Esper Enginer provider with all configuration");
		return cep;
	}
}

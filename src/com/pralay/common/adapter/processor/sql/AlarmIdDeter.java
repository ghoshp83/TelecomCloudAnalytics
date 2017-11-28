package com.pralay.common.adapter.processor.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pralay.common.adapter.builder.CommonAdapterBuilder;
import com.pralay.configuration.model.AlarmIdPojo;
import com.pralay.configuration.model.ServerData;

public class AlarmIdDeter {
	private ServerData serverData;
	private static final Logger LOGGING = LoggerFactory.getLogger(AlarmIdDeter.class);
	private static Connection conn = null;
	ResultSet rs = null;
	private List<AlarmIdPojo> aidlst = new ArrayList<AlarmIdPojo>();
	public AlarmIdDeter(ServerData serverData){
		this.serverData = serverData;
		conn = CommonAdapterBuilder.conn;
	}
	public List<AlarmIdPojo> alarm() throws Exception{
		try{
			    LOGGING.info("Inside alarm method....");
				//DatabaseConnection dbc = new DatabaseConnection(serverData);
			    //conn = DatabaseConnection.makeConnection();
				//rs = AlarmIdDeter.conn.prepareStatement("select message_text,alarm_id from alarm_id_determination").executeQuery();
				rs = AlarmIdDeter.conn.prepareStatement(serverData.getFetchalarmsql()).executeQuery();
				if (rs!= null) {
					while (rs.next()) {
						AlarmIdPojo aip = new AlarmIdPojo();
						aip.setMessage_text(rs.getString("message_text"));
						aip.setAlarm_id(rs.getString("alarm_id"));
						aip.setFunctional_alarm_id(rs.getString("functional_alarm_id"));
						aip.setImpact(rs.getString("impact"));
						aip.setResolution(rs.getString("resolution"));
						aidlst.add(aip);
					}
					LOGGING.info("Before closing result set ....");
					rs.close();
					//dbc.dropConnection();
				}
		}catch(Exception e){
			e.printStackTrace();
			LOGGING.error(e.getMessage());
		}
		LOGGING.info("End of alarm method....");
		return aidlst;
	}
}
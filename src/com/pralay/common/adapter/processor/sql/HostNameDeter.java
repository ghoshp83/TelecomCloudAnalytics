package com.pralay.common.adapter.processor.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pralay.common.adapter.builder.CommonAdapterBuilder;
import com.pralay.configuration.model.HostNamePojo;
import com.pralay.configuration.model.ServerData;

public class HostNameDeter {

	private ServerData serverData;
	private static final Logger LOGGING = LoggerFactory.getLogger(AlarmIdDeter.class);
	private static Connection conn = null;
	ResultSet rs = null;
	private List<HostNamePojo> hnlst = new ArrayList<HostNamePojo>();
	public HostNameDeter(ServerData serverData){
		this.serverData = serverData;
		conn = CommonAdapterBuilder.conn;
	}
	public List<HostNamePojo> hostnamelist() throws Exception{
		try{
			    LOGGING.info("Inside hostname method....");
				//DatabaseConnection dbc = new DatabaseConnection(serverData);
			    //conn = dbc.makeConnection();
				//rs = HostNameDeter.conn.prepareStatement("select servername,functionalname from network_topology").executeQuery();
				rs = HostNameDeter.conn.prepareStatement(serverData.getFetchhostsql()).executeQuery();
				while(rs.next()){
					HostNamePojo hnp = new HostNamePojo();
					hnp.setServername(rs.getString("servername"));
					hnp.setFunctionalname(rs.getString("functionalname"));
					hnlst.add(hnp);
				}
				LOGGING.info("Before closing result set ....");
				rs.close();
				//dbc.dropConnection();
		}catch(Exception e){
			e.printStackTrace();
			LOGGING.error(e.getMessage());
		}
		LOGGING.info("End of hostname method....");
		return hnlst;
	}
	
}

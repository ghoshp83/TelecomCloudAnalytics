package com.pralay.common.adapter.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.net.ntp.TimeStamp;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pralay.common.adapter.builder.CommonAdapterBuilder;
import com.pralay.common.adapter.processor.sql.FileWatcher;
import com.pralay.common.adapter.processor.sql.RuleCreator;
import com.pralay.common.adapter.processor.sql.SQLProcessor;
import com.pralay.configuration.model.AlarmIdPojo;
import com.pralay.configuration.model.DownTimeRulePojo;
import com.pralay.configuration.model.ServerData;

import org.apache.camel.Processor;

import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommonRouteBuilder extends RouteBuilder{
	
	private ServerData serverData;
	private Exchange exchange;
	private static Connection conn = null;
	private static String oraclefetchQuery = null;
	final static Logger LOGGING = LoggerFactory.getLogger(CommonRouteBuilder.class);
	private static String watchDirName = null;
    private static String shadowDirName = null;
    private List<DownTimeRulePojo> dtrlst = new ArrayList<DownTimeRulePojo>();
    public CommonRouteBuilder(ServerData serverData){
		this.serverData = serverData;
		conn = CommonAdapterBuilder.conn;
	}
	
	@Override
	public void configure() throws Exception {
		LOGGING.info("Entering theXMLRouteBuilder -> configure method");
		LOGGING.debug(serverData.toString());
		
		/*ResultSet checkrs;
		//checkrs = conn.prepareStatement("select id from normalised_sc_event_view limit 1").executeQuery();
		checkrs = conn.prepareStatement(serverData.getCheckdatapresenceinnormalize()).executeQuery();
		if(!checkrs.next()){
			oraclefetchQuery = serverData.getFetcheventsql();
		}else{
			//oraclefetchQuery = serverData.getFetcheventsql()+" where eventdate >= date_add(now(), interval -10 minute)";
			oraclefetchQuery = serverData.getFetchlasteventsql();
		}
		checkrs.close();*/
		
		
		// Calling the fixed route path based on the data format
		if (ServerData.InputType.SQL.name().equals(serverData.getType().name())) {
			from("timer://foo?period="+serverData.getTimer()).threads(serverData.getNumberOfInstances()).
			//setBody(constant("delete from raw_event_view")).
			//setBody(constant(serverData.getRawtruncatesql())). -- commented 03042016 for infinite polling
			//to("jdbc:" + serverData.getUrl() + serverData.getUserId()).
			//to("jdbc:" + serverData.getOuturl() + serverData.getOutuserId()). -- commented 03042016 for infinite polling
			//setBody(constant(serverData.getSql())).
			//setBody(constant(serverData.getFetcheventsql())).
			//setBody(constant(oraclefetchQuery)). // this is to dynamically take the latest data from oracle bmc db
			//setBody(constant(fetchquery())).	   // this is to dynamically take the latest data from oracle bmc db
			//recipientList(simple("fetchquery()")).
			process(new Processor(){
				@Override
		        public void process(Exchange exchange) throws Exception {
					LOGGING.info("Determining fetch condition of BMC DB");
					
					ResultSet checkrs;
					try{
						//Using Filewatcher to take new changes to AllRules.xml file
						LOGGING.info("03192016: gautamchecck1");
						checkrs = conn.createStatement().executeQuery(serverData.getSelectDowntimeRuleDetails());
						LOGGING.info("03192016: gautamchecck2");
						if (checkrs!= null) {
							while (checkrs.next()) {
								DownTimeRulePojo dtr = new DownTimeRulePojo();
								dtr.setRulename(checkrs.getString("rule_name"));
								dtr.setPrimaryAlarmIds(checkrs.getString("primary_alarm_ids"));
								dtr.setCorrelatorAlarmIds(checkrs.getString("correlator_alarm_ids"));
								dtr.setJoinConditions(checkrs.getString("join_conditions"));
								LOGGING.info("03192016: gautamchecck3");
								dtrlst.add(dtr);
							}
							LOGGING.info("Before closing result set ....");
							LOGGING.info("03192016: gautamchecck4");
							checkrs.close();
						}
						
						if(dtrlst != null){
						
							for(DownTimeRulePojo dtr : dtrlst){
								
								LOGGING.info("03192016: gautamchecck5"); 
								RuleCreator.createDowntimeRule(dtr);	
								LOGGING.info("03192016: gautamchecck6");
								String ruleUpateQuery = serverData.getUpdateDowntimeRuleDetails()+"'"+dtr.getRulename()+"'";
								
								conn.prepareStatement(ruleUpateQuery).executeUpdate();
								conn.commit();
								
							}
						}
						
						//dtrlst = null;
						dtrlst.clear();
						checkrs.close();
						//conn.close();
						LOGGING.info("03192016: gautamchecck7");
						
		            	watchDirName = serverData.getWatchdirfilename();
		                shadowDirName = serverData.getShadowdirfilename();
		                //checkrs = conn.prepareStatement("select id from normalised_sc_event_view limit 1").executeQuery();
						checkrs = conn.prepareStatement(serverData.getCheckdatapresenceinnormalize()).executeQuery();
						if(!checkrs.next()){
							oraclefetchQuery = serverData.getFetcheventsql();
							LOGGING.info("Determining any rule changes for initial AllRules.xml");
							new FileWatcher(Paths.get(watchDirName), Paths.get(shadowDirName),serverData).firstrun();
							conn.prepareStatement(serverData.getRawtruncatesql()).executeUpdate();
							conn.commit();
						}else{
							//oraclefetchQuery = serverData.getFetcheventsql()+" where eventdate >= date_add(now(), interval -10 minute)";
							if(serverData.getInurl().contains("mysql")){
								oraclefetchQuery = serverData.getFetchlasteventmysql();
							}else{
								oraclefetchQuery = serverData.getFetchlasteventoracle();
							}
							
							LOGGING.info("Determining any rule changes for changed(if any?) AllRules.xml");
							new FileWatcher(Paths.get(watchDirName), Paths.get(shadowDirName),serverData).restrun();
						}
						checkrs.close();
						LOGGING.info("Query to be use for fetching: "+oraclefetchQuery);
						Message in = exchange.getIn();
						in.setBody(constant(oraclefetchQuery));
						
					}catch(Exception e){
						e.printStackTrace();
						LOGGING.error(e.getMessage());
					}
				}
			}).
			//to("jdbc:" + serverData.getUrl() + serverData.getUserId()).
			to("jdbc:" + serverData.getInurl() + serverData.getInuserId()).
			to("log:before-split?level=INFO&showAll=true&multiline=true").
			split(body()).
			to("log:after-split?level=INFO&showAll=true&multiline=true").
			process(new Processor() {
		        @Override
		        public void process(Exchange exchange) throws Exception {
		        	LOGGING.info("inside process method...................");
		            //Record of first query after splitter in the Map
		            Map<String, Object> record = exchange.getIn().getBody(Map.class);
		            LOGGING.info("record value: "+record.toString());
		            
		            //(id,hostname,eventdate,objectclass,object,parameter,parametervalue,severity,status,message,duration)
		            if(serverData.getInurl().contains("mysql")){
		            	//System.out.println("inside my sql: -->"+record.get("id"));
		            	exchange.getIn().setHeader("id",  record.get("id"));
			            exchange.getIn().setHeader("hostname",  record.get("hostname")==null?"Null":record.get("hostname"));
			            exchange.getIn().setHeader("eventdate",  record.get("eventdate")==null?"Null":record.get("eventdate"));
			            exchange.getIn().setHeader("objectclass",  record.get("objectclass")==null?"Null":record.get("objectclass"));
			            exchange.getIn().setHeader("object",  record.get("object")==null?"Null":record.get("object"));
			            exchange.getIn().setHeader("parameter",  record.get("parameter")==null?"Null":record.get("parameter"));
			            exchange.getIn().setHeader("parametervalue",  record.get("parametervalue")==null?"Null":record.get("parametervalue"));
			            exchange.getIn().setHeader("severity",  record.get("severity")==null?"Null":record.get("severity"));
			            exchange.getIn().setHeader("status",  record.get("status")==null?"Null":record.get("status"));
			            exchange.getIn().setHeader("message",  record.get("message")==null?"Null":record.get("message"));
			            exchange.getIn().setHeader("duration",  record.get("duration")==null?0:record.get("duration"));
		            }else{
		            exchange.getIn().setHeader("id",  record.get("ID"));
		            exchange.getIn().setHeader("hostname",  record.get("HOSTNAME")==null?"Null":record.get("HOSTNAME"));
		            exchange.getIn().setHeader("eventdate",  record.get("EVENTDATE")==null?"Null":record.get("EVENTDATE"));
		            exchange.getIn().setHeader("objectclass",  record.get("OBJECTCLASS")==null?"Null":record.get("OBJECTCLASS"));
		            exchange.getIn().setHeader("object",  record.get("OBJECT")==null?"Null":record.get("OBJECT"));
		            exchange.getIn().setHeader("parameter",  record.get("PARAMETER")==null?"Null":record.get("PARAMETER"));
		            exchange.getIn().setHeader("parametervalue",  record.get("PARAMETERVALUE")==null?"Null":record.get("PARAMETERVALUE"));
		            exchange.getIn().setHeader("severity",  record.get("SEVERITY")==null?"Null":record.get("SEVERITY"));
		            exchange.getIn().setHeader("status",  record.get("STATUS")==null?"Null":record.get("STATUS"));
		            exchange.getIn().setHeader("message",  record.get("MESSAGE")==null?"Null":record.get("MESSAGE"));
		            exchange.getIn().setHeader("duration",  record.get("DURATION")==null?0:record.get("DURATION"));
		            }
		        }
		    }).
			to("log:before2query?level=INFO&showAll=true&multiline=true").
			//setBody(constant("insert into raw_event_view (id,hostname,severity,status,message,iteration) values(:?id,:?hostname,:?severity,:?status,:?message,:?iteration)")).
			setBody(constant(serverData.getInsertrawsql())).
			//to("jdbc:" + serverData.getUrl() + serverData.getUserId()+"?allowNamedParameters=true&outputType=SelectOne&useHeadersAsParameters=true&resetAutoCommit=false").
			to("jdbc:" + serverData.getOuturl() + serverData.getOutuserId()+"?allowNamedParameters=true&outputType=SelectOne&useHeadersAsParameters=true&resetAutoCommit=false").
			process(new SQLProcessor(serverData));
			
			LOGGING.info("......Starting of new route........");
			
			//new SQLProcessor(serverData);
			
			//in this point, i have data in my raw table
			/*from("timer://foo?period="+serverData.getTimer()).threads(1).
			//setBody(constant("select numbersubscribers, id from performance_adapter_test")).
			setBody(constant("select (select count(*) from performance_adapter_test) as cntRaw,"
					+ " (select count(*) from new_raw_table_test) as cntNorm")).
			to("jdbc:" + serverData.getUrl() + serverData.getUserId()+"?outputType=SelectList").
			to("log:after-restate?level=INFO&showAll=true&multiline=true").
			process(new SQLProcessor(serverData));
			/*process(new Processor(){
				@Override
				public void process(Exchange exchange) throws Exception {
					LOGGING.info("inside process 22 method...................");
					LOGGING.info("exchange after sql processor: "+exchange.getIn().getBody(String.class));
				}
			});*/
			//process(new WriteOutputAsCSVProcess(serverData));
			LOGGING.info("Adding new route in Route....");
			LOGGING.info("server: "+serverData.getOutput());
			
			
		}else {
			throw new Exception("CommonRouteBuilder -> CommonAdapterInterfaceLst List cannont greater than 1 processor");
		}
		LOGGING.info("Exit theXMLRouteBuilder -> configure method");
	}
	
	/*private String fetchquery(){
		ResultSet checkrs;
		try{
			LOGGING.info("<-- Inside fetch BMC query method -->");
			//checkrs = conn.prepareStatement("select id from normalised_sc_event_view limit 1").executeQuery();
			checkrs = conn.prepareStatement(serverData.getCheckdatapresenceinnormalize()).executeQuery();
			if(!checkrs.next()){
				oraclefetchQuery = serverData.getFetcheventsql();
			}else{
				//oraclefetchQuery = serverData.getFetcheventsql()+" where eventdate >= date_add(now(), interval -10 minute)";
				oraclefetchQuery = serverData.getFetchlasteventmysql();
			}
			checkrs.close();
		}catch(Exception e){
			
		}
		LOGGING.info("Query to be use for fetching: "+oraclefetchQuery);
		return oraclefetchQuery;
	}*/
}

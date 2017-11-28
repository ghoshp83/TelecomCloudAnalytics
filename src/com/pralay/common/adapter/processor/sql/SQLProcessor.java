package com.pralay.common.adapter.processor.sql;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;
import java.sql.ResultSet;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.net.ntp.TimeStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.EPRuntime;
import com.pralay.common.adapter.builder.CommonAdapterBuilder;
import com.pralay.configuration.model.AlarmIdPojo;
import com.pralay.configuration.model.DataInfo;
import com.pralay.configuration.model.DownTimeRulePojo;
import com.pralay.configuration.model.EventPojo;
import com.pralay.configuration.model.HostNamePojo;
import com.pralay.configuration.model.ServerData;


public class SQLProcessor implements Processor{
    private DataInfo dataInfo;
    private Exchange exchange;
    private ServerData serverData;
    private DataConsumer dataconsumer;
    private static final String DELIMITTER = ",";
    private static final Logger LOGGING = LoggerFactory.getLogger(SQLProcessor.class);
    public static List<AlarmIdPojo> aidlst = new ArrayList<AlarmIdPojo>();
    public static List<HostNamePojo> hnlst = new ArrayList<HostNamePojo>();
    public static Connection conn = null;
    private List<DownTimeRulePojo> dtrlst = new ArrayList<DownTimeRulePojo>();
    private RuleCreator ruleCreator;

    public SQLProcessor(ServerData serverData) {
        this.serverData = serverData;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        // TODO Auto-generated method stub
        try {
            LOGGING.info("Entered the SQL Processor to process data");
            synchronized (SQLProcessor.class) {
            	aidlst = CommonAdapterBuilder.aidlst;
            	hnlst = CommonAdapterBuilder.hnlst;
            	conn = CommonAdapterBuilder.conn;
            	this.exchange = exchange;
            	
            	/*
            	//Using Filewatcher to take new changes to AllRules.xml file 
            	String watchDirName = "./config";
                String shadowDirName = "./configcopy";
                //int watchInterval = args.length > 2 ? Integer.getInteger(args[2]) : DEFAULT_WATCH_INTERVAL;
                new FileWatcher(Paths.get(watchDirName), Paths.get(shadowDirName),serverData).run();*/
            	
                readSQLData();
                exchange.getIn().setBody(dataInfo);
            }
        } catch (Exception e) {
            LOGGING.error(e.getMessage());
        } finally {
            LOGGING.info("Exiting the SQL Processor after processing data");
        }
    }

    public void readSQLData() throws IOException {
        Map<String, String> headerMapping = serverData.getMappingInfo();
        LOGGING.info("Entering the readSQLData method");
       
        String id=null;
        String hostname=null;
        String eventdateincoming=null;
        String eventdate=null;
        String objectclass=null;
        String object=null;
        String parameter=null;
        String parametervalue=null;
        String funchostname=null;
        String severity=null;
        String status=null;
        String message=null;
        String duration=null;
        boolean alarmPresence=false;
        String alarmInsertQuery=null;
        String aid_alarm_id=null;
        String functional_aid_alarm_id=null;
        String impact_alarm_id = null;
        String resolution_alarm_id = null;
        String colname=null;
        String colnameval=null;
        String colnametext=null;
        String colnametextval=null;
        String disconnvol = null;
        String unsynentry = null;
        String colnametextgfsvol = null;
        String colnametextgfsunsyn = null;
        String colnametextscom = null;
        String colnametextscomval = null;
        String colnametextnprio = null;
        String colnametextnprioval = null;
        String colnametextla = null;
        String colnametextlaval = null;
        String colnametextzh = null;
        String colnametextzhval = null;
        String colnametextswt = null;
        String colnametextswtval = null;
        String colnametextsariowait = null;
        String colnametextsariowaitval = null;
        String colnametextsaridle = null;
        String colnametextsaridleval = null;
        String colnametextsarloadavg = null;
        String colnametextsarloadavgval = null;
        String colnametextopenalertwarn = null;
        String colnametextopenalertcrit = null;
        String colnametextopenalertwarnval = null;
        String colnametextopenalertcritval = null;
        String[] ntrparts = null;
        StringTokenizer gbst = null;
        StringBuffer gbsb = null;
        SimpleDateFormat Incomingformat = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat outgoingformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ResultSet rs = null;
        ResultSet checkrs = null;
        
        boolean is_alarm_present = true;
        
        try {
        	String headerName = "";
            
            LOGGING.info("data1: "+exchange.getIn().getBody(String.class));
            
            LOGGING.info("id : "+exchange.getIn().getHeader("id"));
            LOGGING.info("hostname : "+exchange.getIn().getHeader("hostname"));
            LOGGING.info("eventdate : "+exchange.getIn().getHeader("eventdate"));
            LOGGING.info("objectclass : "+exchange.getIn().getHeader("objectclass"));
            LOGGING.info("object : "+exchange.getIn().getHeader("object"));
            LOGGING.info("parameter : "+exchange.getIn().getHeader("parameter"));
            LOGGING.info("parametervalue : "+exchange.getIn().getHeader("parametervalue"));
            LOGGING.info("severity : "+exchange.getIn().getHeader("severity"));
            LOGGING.info("status : "+exchange.getIn().getHeader("status"));
            LOGGING.info("message : "+exchange.getIn().getHeader("message"));
            LOGGING.info("duration : "+exchange.getIn().getHeader("duration"));
            
            id = exchange.getIn().getHeader("id").toString();
            hostname = exchange.getIn().getHeader("hostname").toString();
            
            eventdateincoming = exchange.getIn().getHeader("eventdate").toString();
            Date eventdateformat = Incomingformat.parse(eventdateincoming);
            eventdate = outgoingformat.format(eventdateformat);
            
            objectclass = exchange.getIn().getHeader("objectclass").toString();
            object = exchange.getIn().getHeader("object").toString();
            parameter = exchange.getIn().getHeader("parameter").toString();
            parametervalue = exchange.getIn().getHeader("parametervalue").toString();
            severity = exchange.getIn().getHeader("severity").toString();
            status = exchange.getIn().getHeader("status").toString();
            message = exchange.getIn().getHeader("message").toString();
            duration = exchange.getIn().getHeader("duration").toString();
            
            
            //LOGGING.info(new JSONTokener(exchange.getIn().getBody(String.class)).nextValue().toString());
            
            //JSONArray objectArray = (JSONArray) new JSONTokener(exchange.getIn().getBody(String.class)).nextValue();
            //LOGGING.info("objectArray: "+objectArray);
            dataInfo = new DataInfo();
            /*List<Map<String, String>> valueLst = new ArrayList<Map<String, String>>();
            for (int i = 0; i < objectArray.length(); i++) {
                JSONObject object = objectArray.getJSONObject(i);
                Map<String, String> valueMap = new HashMap<String, String>();
                LOGGING.info("key: "+object.keys().toString());
                for (Iterator<String> iterator = object.keys(); iterator.hasNext();) {
                    String key = (String) iterator.next();
                    if(key.equalsIgnoreCase("cntRaw")){
                    	cntRawTab = Integer.parseInt(String.valueOf(object.get(key)));
                    }else cntNormTab = Integer.parseInt(String.valueOf(object.get(key)));

                    if (headerMapping != null && !headerMapping.isEmpty()) {
                        headerName = headerMapping.get(key);
                        if (serverData.getMappingInfo().containsKey(headerName))
                            valueMap.put(serverData.getMappingInfo().get(headerName), String.valueOf(object.get(key)));
                    } else {
                        valueMap.put(key, String.valueOf(object.get(key)));
                    }
                }
                if (!valueMap.isEmpty()) {
                    valueLst.add(valueMap);
                }
            }*/
            
            
            /*LOGGING.info("03192016: gautamchecck1");
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
			
			for(DownTimeRulePojo dtr : dtrlst){
				
				LOGGING.info("03192016: gautamchecck5"); 
				ruleCreator.createDowntimeRule(dtr);	
				LOGGING.info("03192016: gautamchecck6");
				String ruleUpateQuery = serverData.getUpdateDowntimeRuleDetails()+"'"+dtr.getRulename()+"'";
				
				conn.prepareStatement(ruleUpateQuery).executeUpdate();
				conn.commit();
				
			}
			
			dtrlst = null;
			LOGGING.info("03192016: gautamchecck7");*/
            
                      
            AlarmMgmt au = new AlarmMgmt(serverData);
            alarmPresence = au.alarmCheckPresence(id);
            
            
            if(!alarmPresence){
            	funchostname = getfuncName(hostname);
            	
            	/* start : Code added by gautam : to pick the new alarm id dynamically from the table*/
            	for(AlarmIdPojo aip : aidlst){
            		if(message.contains(aip.getMessage_text())){
            			is_alarm_present = true;
            			break;
            		}else{
            			is_alarm_present = false;
            		}
            	}
            	
            	if(is_alarm_present){
            		LOGGING.info(message+" gautamcheck: The alarm is already present in the table");
            	}else{
            		LOGGING.info(message+" gautamcheck: The alarm is not present in the table");
            		LOGGING.info(message+" gautamcheck: Refreshing the alarm id from the table");
            		aidlst = null;
            		AlarmIdDeter aid = new AlarmIdDeter(serverData);
                    aidlst = aid.alarm();
            	}
            	
            	 /* end : Code added by gautam : to pick the new alarm id dynamically from the table*/

	            for(AlarmIdPojo aip : aidlst){
	        		LOGGING.info("message inside SQLP : "+aip.getMessage_text());
	            	LOGGING.info("alarm inside SQLP: "+aip.getAlarm_id());
	            	LOGGING.info("functional alarm inside SQLP: "+aip.getFunctional_alarm_id());
	            	LOGGING.info("Impact inside SQLP: "+aip.getImpact());
	            	LOGGING.info("Resolution SQLP: "+aip.getResolution());
	            	
	            	if(message.contains(aip.getMessage_text())){
	            		LOGGING.info("YES!!!!");
	            		aid_alarm_id = aip.getAlarm_id();
	            		LOGGING.info("YES!!!!"+aid_alarm_id);
	            		functional_aid_alarm_id = aip.getFunctional_alarm_id();
	            		impact_alarm_id = aip.getImpact();
	            		resolution_alarm_id = aip.getResolution();
	            		String alarmcol = "";
	            		//gautam changed 05/03/2016 for getting any names for alarms
	            		//
	            		if(aid_alarm_id.contains("_ntr") || aid_alarm_id.contains("_pa") || aid_alarm_id.contains("_cron")){
	            			 alarmcol = aid_alarm_id.substring(0,aid_alarm_id.lastIndexOf("_")).toLowerCase();
	            			 LOGGING.info("alarm substring"+alarmcol);
	            		}else{
	            			 alarmcol = aid_alarm_id.toLowerCase();
	            			 LOGGING.info("alarm without substring"+alarmcol);
	            		}
	            		
	            		
	            		boolean alarmcolpresence = false;
	            		//rs = conn.prepareStatement("select column_name from information_schema.columns WHERE table_name = 'normalised_sc_event_view'").executeQuery();
	            		rs = conn.prepareStatement(serverData.getCheckColPresenceNorm()).executeQuery();
	            		if (rs != null){
							while (rs.next()) {

								if (alarmcol.matches(rs.getString("column_name"))) {
									//System.out.println("<- alarm col present ->");
									alarmcolpresence = true;
									break;
								} else {
									//System.out.println("<- alarm col not present ->");
									alarmcolpresence = false;
								}
							}
							rs.close();
						}
						LOGGING.info("Alarm Col presence -->"+alarmcolpresence);
	            		
	            		if(!alarmcolpresence){
	            			
	            			//aid_alarm_id = serverData.getNonMatchingAlarmId();
		            		//functional_aid_alarm_id=aid_alarm_id;
		            		//colname=aid_alarm_id.substring(0,aid_alarm_id.indexOf("_"));
		            		//colname=serverData.getNonMatchingAlarmId().substring(0,aid_alarm_id.indexOf("_"));
		            		//colname="misc";
		            		colname=serverData.getNonMatchingAlarmIdCol();
	            			colnameval="Y";
	            			colnametext=colname+serverData.getDefColTrail();
	            			colnametextval=message;
	            			
	            			alarmInsertQuery=serverData.getInsertalarmsql()+colname+","+colnametext+") values"
	        	            		+ "('"+id+"','"+aid_alarm_id+"','"+functional_aid_alarm_id+"','"+funchostname+"','"+eventdate+"','"+objectclass+"','"+object+"','"+parameter+"','"+parametervalue+"','"+severity
	        	        			+"','"+status+"','"+duration+"','"+colnameval+"','"+ ( colnametextval.contains("'") ? colnametextval.replaceAll("'", "''") : colnametextval ) +"')";
	            		}else{
	            		
	            		if(aid_alarm_id.contains(serverData.getAlarmAgentC())){ 
	            			if(message.contains(severity)){
	            				message=message.substring(message.indexOf(severity)+severity.length()).trim();
	            			}else{
	            				if(severity.equalsIgnoreCase(serverData.getAlarmSeverityC())){
	            					message=message.substring(message.indexOf(serverData.getAlarmSeverityW())+serverData.getAlarmSeverityW().length()).trim();
	            				}
	            				else{
	            					message=message.substring(message.indexOf(serverData.getAlarmSeverityC())+serverData.getAlarmSeverityC().length()).trim();
	            				}
	            			}
	            			colname=aid_alarm_id.substring(0,aid_alarm_id.lastIndexOf("_"));
	            			colnameval="Y";
	            			colnametext=colname+serverData.getDefColTrail();
	            			colnametextval=message;

	            			if(colnametextval.contains(serverData.getConnGFSMatchingText()) || colnametextval.contains(serverData.getDisConnGFSMatchingText()) || colnametextval.contains(serverData.getUnsynGFSMatchingText())){
	            				colnametextgfsvol = colname + serverData.getDisConGFSTrail();
	            				colnametextgfsunsyn = colname + serverData.getUnsynGFSTrail(); 
	            				disconnvol = colnametextval.substring(colnametextval.indexOf("/")+1).substring(0,colnametextval.substring(colnametextval.indexOf("/")+1).
	            						indexOf(serverData.getUnsynGFSMatchingText())).substring(colnametextval.substring(colnametextval.indexOf("/")+1).substring(0,colnametextval.
	            								substring(colnametextval.indexOf("/")+1).indexOf(serverData.getUnsynGFSMatchingText())).indexOf(":")+1).trim();
	            				gbst = new StringTokenizer(colnametextval.substring(colnametextval.indexOf("/")+1).substring(colnametextval.
	            						substring(colnametextval.indexOf("/")+1).indexOf(serverData.getUnsynGFSMatchingText())), serverData.getUnsynGFSMatchingTextFull());
	            				gbsb = new StringBuffer();
	            				while(gbst.hasMoreElements()){
	            					if(gbsb.length()==0)
	            						gbsb.append(gbst.nextToken());
	            					else
	            						gbsb.append(",").append(gbst.nextToken());
	            				}
	            				unsynentry = gbsb.toString();
	            				
	            				//id,alarm_id,hostname_refined,eventdate,objectclass,object,parameter,parametervalue,severity,status,duration
	            				//alarmInsertQuery="insert into normalised_sc_event_view(id,alarm_id,hostname_refined,severity,status,duration,"+colname+","+colnametext+","+colnametextgfsvol+","+colnametextgfsunsyn+") values"
	            				alarmInsertQuery=serverData.getInsertalarmsql() +colname+","+colnametext+","+colnametextgfsvol+","+colnametextgfsunsyn+") values"
		        	            		+ "('"+id+"','"+aid_alarm_id+"','"+functional_aid_alarm_id+"','"+funchostname+"','"+eventdate+"','"+objectclass+"','"+object+"','"+parameter+"','"+parametervalue+"','"
	            						+severity+"','"+status+"','"+duration+"','"+colnameval+"','"
	            						+ ( colnametextval.contains("'") ? colnametextval.replaceAll("'", "''") : colnametextval ) 
		        	        			+"','"+disconnvol+"','"+unsynentry+"')";
	            				
	            			}else if(colnametextval.contains(serverData.getSarIoWaitMatchingText())){
	            				colnametextsariowait = colname + serverData.getSariowaitColTrail();
	            				colnametextsariowaitval = colnametextval.substring(colnametextval.lastIndexOf(">")+1).trim();
	            				alarmInsertQuery=serverData.getInsertalarmsql() +colname+","+colnametext+","+colnametextsariowait+") values"
		        	            		+ "('"+id+"','"+aid_alarm_id+"','"+functional_aid_alarm_id+"','"+funchostname+"','"+eventdate+"','"+objectclass+"','"+object+"','"+parameter+"','"+parametervalue+"','"
	            						+severity+"','"+status+"','"+duration+"','"+colnameval+"','"
	            						+ ( colnametextval.contains("'") ? colnametextval.replaceAll("'", "''") : colnametextval ) 
		        	        			+"','"+colnametextsariowaitval+"')";
	            			}else if(colnametextval.contains(serverData.getSarIdleMatchingText())){
	            				colnametextsaridle = colname + serverData.getSaridleColTrail();
	            				colnametextsaridleval = colnametextval.substring(colnametextval.lastIndexOf(">")+1).trim();
	            				alarmInsertQuery=serverData.getInsertalarmsql() +colname+","+colnametext+","+colnametextsaridle+") values"
		        	            		+ "('"+id+"','"+aid_alarm_id+"','"+functional_aid_alarm_id+"','"+funchostname+"','"+eventdate+"','"+objectclass+"','"+object+"','"+parameter+"','"+parametervalue+"','"
	            						+severity+"','"+status+"','"+duration+"','"+colnameval+"','"
	            						+ ( colnametextval.contains("'") ? colnametextval.replaceAll("'", "''") : colnametextval ) 
		        	        			+"','"+colnametextsaridleval+"')";
	            			}else if(colnametextval.contains(serverData.getSarLoadAvgMatchingText())){
	            				colnametextsarloadavg = colname + serverData.getSarloadavgColTrail();
	            				colnametextsarloadavgval = colnametextval.substring(colnametextval.lastIndexOf(":")+1).trim();
	            				alarmInsertQuery=serverData.getInsertalarmsql() +colname+","+colnametext+","+colnametextsarloadavg+") values"
		        	            		+ "('"+id+"','"+aid_alarm_id+"','"+functional_aid_alarm_id+"','"+funchostname+"','"+eventdate+"','"+objectclass+"','"+object+"','"+parameter+"','"+parametervalue+"','"
	            						+severity+"','"+status+"','"+duration+"','"+colnameval+"','"
	            						+ ( colnametextval.contains("'") ? colnametextval.replaceAll("'", "''") : colnametextval ) 
		        	        			+"','"+colnametextsarloadavgval+"')";
	            			}else if(colnametextval.contains(serverData.getSarLoadAvgMatchingText())){
	            				colnametextsarloadavg = colname + serverData.getSarloadavgColTrail();
	            				colnametextsarloadavgval = colnametextval.substring(colnametextval.lastIndexOf(":")+1).trim();
	            				alarmInsertQuery=serverData.getInsertalarmsql() +colname+","+colnametext+","+colnametextsarloadavg+") values"
		        	            		+ "('"+id+"','"+aid_alarm_id+"','"+functional_aid_alarm_id+"','"+funchostname+"','"+eventdate+"','"+objectclass+"','"+object+"','"+parameter+"','"+parametervalue+"','"
	            						+severity+"','"+status+"','"+duration+"','"+colnameval+"','"
	            						+ ( colnametextval.contains("'") ? colnametextval.replaceAll("'", "''") : colnametextval ) 
		        	        			+"','"+colnametextsarloadavgval+"')";
	            			}else if(colnametextval.contains(serverData.getOpenalertMatchingText())){
	            				colnametextopenalertwarn = colname + serverData.getOpenalertwarnColTrail();
	            				colnametextopenalertcrit = colname + serverData.getOpenalertcritColTrail();
	            				
	            				colnametextopenalertwarnval = colnametextval.substring(colnametextval.indexOf("Warning:")+"Warning:".length(), colnametextval.indexOf("offene")).trim();
	            				colnametextopenalertcritval = colnametextval.substring(colnametextval.indexOf("CRITIC@L:")+"CRITIC@L:".length(), colnametextval.lastIndexOf("offene")).trim();
	            				
	            				alarmInsertQuery=serverData.getInsertalarmsql() +colname+","+colnametext+","+colnametextopenalertwarn+","+colnametextopenalertcrit+") values"
		        	            		+ "('"+id+"','"+aid_alarm_id+"','"+functional_aid_alarm_id+"','"+funchostname+"','"+eventdate+"','"+objectclass+"','"+object+"','"+parameter+"','"+parametervalue+"','"
	            						+severity+"','"+status+"','"+duration+"','"+colnameval+"','"
	            						+ ( colnametextval.contains("'") ? colnametextval.replaceAll("'", "''") : colnametextval ) 
		        	        			+"','"+colnametextopenalertwarnval+"','"+colnametextopenalertcritval+"')";
	            			}else {
	            			
	            			alarmInsertQuery=serverData.getInsertalarmsql()+colname+","+colnametext+") values('"+id+"','"+aid_alarm_id+"','"+functional_aid_alarm_id+"','"+funchostname+"','"+eventdate+"','"+objectclass+"','"+object+"','"+parameter+"','"+parametervalue+"','"
	            					+severity+"','"+status+"','"+duration+"','"+colnameval+"','"+ ( colnametextval.contains("'") ? colnametextval.replaceAll("'", "''") : colnametextval ) +"')";
	            			}
	            			
	            		}else if(aid_alarm_id.contains(serverData.getAlarmAgentNTR())){
	            			LOGGING.info("Inside NTR else loop!!!!!!!!!!");
	            			message=message.substring(message.indexOf(aid_alarm_id.substring(0, aid_alarm_id.indexOf("_")))
	            					+aid_alarm_id.substring(0, aid_alarm_id.indexOf("_")).length()).trim();
	            			colname=aid_alarm_id.substring(0,aid_alarm_id.lastIndexOf("_"));
	            			colnameval="Y";
	            			colnametext=colname+serverData.getDefColTrail();
	            			colnametextval=message;
	            			
	            			if(colnametextval.contains(serverData.getScomMatchingText())){
	            				colnametextscom = colname + serverData.getScomColTrail();
	            				colnametextnprio = colname + serverData.getNprioColTrail();
	            				ntrparts = colnametextval.substring(colnametextval.indexOf("(")+1, colnametextval.lastIndexOf(")")).split(Pattern.quote("."));
	        					colnametextscomval = ntrparts[0].trim().substring(ntrparts[0].trim().indexOf(":")+1);
	        					colnametextnprioval = ntrparts[1].trim().substring(ntrparts[1].trim().indexOf(":")+1);
	            				
	            				alarmInsertQuery=serverData.getInsertalarmsql()+colname+","+
	            							colnametext+","+colnametextscom+","+colnametextnprio+") values"
		        	            		+ "('"+id+"','"+aid_alarm_id+"','"+functional_aid_alarm_id+"','"+funchostname+"','"+eventdate+"','"+objectclass+"','"+object+"','"+parameter+"','"+parametervalue+"','"+severity+"','"+status+"','"+duration+"','"
	            						+colnameval+"','"+ ( colnametextval.contains("'") ? colnametextval.replaceAll("'", "''") : colnametextval )
	            						+"','"+colnametextscomval+"','"+colnametextnprioval+"')";
	            			}else if(colnametextval.contains(serverData.getLAMatchingText()) || colnametextval.contains(serverData.getZHMatchingText())){
	            				colnametextla = colname + serverData.getLAColTrail();
	            				colnametextzh = colname + serverData.getZHColTrail();
	            				ntrparts = colnametextval.substring(colnametextval.indexOf("(")+1, colnametextval.lastIndexOf(")")).split(Pattern.quote("."));
	        					colnametextlaval = ntrparts[0].trim().substring(ntrparts[0].trim().indexOf(":")+1);
	        					colnametextzhval = ntrparts[1].trim().substring(ntrparts[1].trim().indexOf(":")+1);
	            				
	            				alarmInsertQuery=serverData.getInsertalarmsql()+colname+","+
	            							colnametext+","+colnametextla+","+colnametextzh+") values"
		        	            		+ "('"+id+"','"+aid_alarm_id+"','"+functional_aid_alarm_id+"','"+funchostname+"','"+eventdate+"','"+objectclass+"','"+object+"','"+parameter+"','"+parametervalue+"','"+severity+"','"+status+"','"+duration+"','"
	            						+colnameval+"','"+ ( colnametextval.contains("'") ? colnametextval.replaceAll("'", "''") : colnametextval )
	            						+"','"+colnametextlaval+"','"+colnametextzhval+"')";
	            			}else if (colnametextval.contains(serverData.getSwtMatchingText())){
	            				colnametextswt = colname + serverData.getSwtColTrail();
	            				colnametextswtval = colnametextval.substring(colnametextval.indexOf(serverData.getSwtMatchingText())+serverData.getSwtMatchingText().length(),colnametextval.indexOf("-")).trim();
	            				colnametextswtval = serverData.getSwtMiscInfo() + getfuncName(colnametextswtval);
	            				alarmInsertQuery=serverData.getInsertalarmsql()+colname
	            						+","+colnametext+","+colnametextswt+") values"
		        	            		+ "('"+id+"','"+aid_alarm_id+"','"+functional_aid_alarm_id+"','"+funchostname+"','"+eventdate+"','"+objectclass+"','"+object+"','"+parameter+"','"+parametervalue+"','"+severity+"','"+status+"','"+duration+"','"+colnameval
		        	            		+"','"+ ( colnametextval.contains("'") ? colnametextval.replaceAll("'", "''") : colnametextval ) +"','"+colnametextswtval+"')";
		            			
	            				
	            			}else{
	            			
	            			alarmInsertQuery=serverData.getInsertalarmsql()+colname+","+colnametext+") values"
	        	            		+ "('"+id+"','"+aid_alarm_id+"','"+functional_aid_alarm_id+"','"+funchostname+"','"+eventdate+"','"+objectclass+"','"+object+"','"+parameter+"','"+parametervalue+"','"+severity
	        	        			+"','"+status+"','"+duration+"','"+colnameval+"','"+ ( colnametextval.contains("'") ? colnametextval.replaceAll("'", "''") : colnametextval ) +"')";
	            			}
	            			
	            		}
	            		else if(aid_alarm_id.contains(serverData.getAlarmAgentPA())){
	            			LOGGING.info("Inside PA else loop!!!!!!!!!!");
	            			colname=aid_alarm_id.substring(0,aid_alarm_id.lastIndexOf("_"));
	            			colnameval="Y";
	            			colnametext=colname+serverData.getDefColTrail();
	            			colnametextval=message;
	            			//LOGGING.info("colval: "+colnametextval);
	            			
	            			alarmInsertQuery=serverData.getInsertalarmsql()+colname+","+colnametext+") values"
	        	            		+ "('"+id+"','"+aid_alarm_id+"','"+functional_aid_alarm_id+"','"+funchostname+"','"+eventdate+"','"+objectclass+"','"+object+"','"+parameter+"','"+parametervalue+"','"+severity
	        	        			+"','"+status+"','"+duration+"','"+colnameval+"','"+ ( colnametextval.contains("'") ? colnametextval.replaceAll("'", "''") : colnametextval ) +"')";
	        	            
	            		}
	            	}
	            		/*LOGGING.info("message: "+message);
	            		LOGGING.info("again from exch: "+exchange.getIn().getHeader("message"));
	            		LOGGING.info("colval before break : "+colnametextval);
	            		LOGGING.info("before break!!!");*/
	            		break;
	            	}else{ 
	            		LOGGING.info("NO!!!!");
	            		aid_alarm_id = serverData.getNonMatchingAlarmId();
	            		functional_aid_alarm_id=aid_alarm_id;
	            		colname=aid_alarm_id.substring(0,aid_alarm_id.indexOf("_"));
            			colnameval="Y";
            			colnametext=colname+serverData.getDefColTrail();
            			colnametextval=message;
            			
            			alarmInsertQuery=serverData.getInsertalarmsql()+colname+","+colnametext+") values"
        	            		+ "('"+id+"','"+aid_alarm_id+"','"+functional_aid_alarm_id+"','"+funchostname+"','"+eventdate+"','"+objectclass+"','"+object+"','"+parameter+"','"+parametervalue+"','"+severity
        	        			+"','"+status+"','"+duration+"','"+colnameval+"','"+ ( colnametextval.contains("'") ? colnametextval.replaceAll("'", "''") : colnametextval ) +"')";
        	            
	            	}
	        	}
	            
	            //au.alarmInsert(alarmInsertQuery, conn);
	            au.alarmInsert(alarmInsertQuery);
	            //Sending data to EventPojo
	            EventPojo ep = new EventPojo(id,aid_alarm_id,functional_aid_alarm_id,(impact_alarm_id.contains("'") ? impact_alarm_id.replaceAll("'", "''") : impact_alarm_id),
	            		(resolution_alarm_id.contains("'") ? resolution_alarm_id.replaceAll("'", "''") : resolution_alarm_id), funchostname, eventdate, objectclass, 
	            		object, parameter, parametervalue, severity, status, (message.contains("'") ? message.replaceAll("'", "''") : message), duration, 
	            		colnametextscomval, colnametextnprioval, colnametextlaval, colnametextzhval,colnametextsariowaitval,
	            		colnametextsaridleval,colnametextsarloadavgval,colnametextopenalertwarnval,colnametextopenalertcritval);
	            
	            //DataProducer dp = new DataProducer();
	            //dp.passData(ep);
	            
	            BlockingQueue sharedQueue = new LinkedBlockingQueue();
	            Thread prodTh = new Thread(new DataProducer(sharedQueue,ep));
	            Thread consTh = new Thread(new DataConsumer(sharedQueue,serverData));
	            prodTh.start();
	            consTh.start();
	            
	            //Thread.currentThread().start();
	            
	            /*CorrelateEvent ce = new CorrelateEvent(serverData);
	            ce.sendAlarm(ep);
	            UptimeMgmt upm = new UptimeMgmt(serverData);
	            upm.sendAlarm(ep);*/
	            
	            //conn.prepareStatement(alarmInsertQuery).executeUpdate();
	            //DatabaseConnection dc = new DatabaseConnection(serverData);
	            //Connection con = dc.makeConnection();
	            //con.prepareStatement(alarmInsertQuery).executeUpdate();
	            //dc.dropConnection();
	            
	            //LOGGING.info("value: "+valueLst);
	            //dataInfo.setValues(valueLst);
	            exchange.getIn().setBody(dataInfo);
            }else
            {
            	LOGGING.info("<--- alarm Update operation -->");
            	au.alarmUpdate(status, duration);
            }
        } catch (Exception e) {
        	e.printStackTrace();
            LOGGING.error(e.getMessage());
        } finally {
        	
        }
    }
    
    public String getfuncName(String hostname){
    	String funchostname=null;
    	for(HostNamePojo hnp : hnlst){
    		if(hnp.getServername().contains(hostname.trim())){
    			funchostname = hnp.getFunctionalname();
    			break;
    		}else funchostname = hostname;
    	}
    	LOGGING.info("funchost: "+funchostname);
    	return funchostname;
    }
}
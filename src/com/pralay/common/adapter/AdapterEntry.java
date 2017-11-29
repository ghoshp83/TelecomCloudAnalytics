package com.pralay.common.adapter;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pralay.common.adapter.builder.CommonAdapterBuilder;
import com.pralay.common.adapter.camel.AdapterFlow;
import com.pralay.configuration.model.ServerData;
import com.pralay.generic.adapter.xml.XmlDomParser;

public class AdapterEntry  extends java.lang.Object {
	private static final Logger LOGGING = LoggerFactory.getLogger(AdapterEntry.class);
	private static List<ServerData> severDataLst;
	public static void main(String[] args){
		try
		   {
			LOGGING.info("Entering the Performance adapter.");
			LOGGING.info("Arguments passed : " + args);
			// This condition will be applicable for Dev environment
			if (args.length == 3) {
	            LOGGING.info("Arguments [0] : " + args[0]);
	            LOGGING.info("Arguments [1] : " + args[1]);
	            LOGGING.info("Arguments [2] : " + args[2]);
		        Boolean readServerDataFromXMLfile = new Boolean(args[2]);
		         if (readServerDataFromXMLfile)
		         {    
		             LOGGING.info("Not connecting the Zookeper, reading the file from Input.xml file");
		             XmlDomParser.main(args);
		             severDataLst = XmlDomParser.getServerDataLst(args[0]);
		         } else {
		             throw new Exception("Please set up the passing argument correctly "
		                     + "Dev Env : argument[0] = Application Name and argument[1] == XML file path to load the server data"
		                     + "ARC Env : argument[1] = Application Name and argument[1] == Zookeeper server path example localhost:2181" 
		                     + "ReadServerDataFromXMLfile : argument[2] for reading the server data information from XML file, In other words, not from ZooKeeper");
		         }
		   } else {
			   throw new Exception("Please set up the passing argument correctly "
			   		+ "Dev Env : argument[0] = Application Name and argument[1] == XML file path to load the server data"
					+ "ARC Env : argument[1] = Application Name and argument[1] == Zookeeper server path example localhost:2181" );
		   }
			
		   // If no routes setup is available then stopping the process.
		   if (severDataLst == null || severDataLst.isEmpty())
			   throw new Exception("No route setup for application, please check the configuration");

		   // Building the camel path using the server data information available.
		   CommonAdapterBuilder.build(severDataLst);
		} catch (Exception exception)
		   {
			   LOGGING.error(exception.getMessage());
			   AdapterFlow.stop();
		   }
		   finally {
			   LOGGING.info("Exiting the Performance adapter.");
		   }
	}
}

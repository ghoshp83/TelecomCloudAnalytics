package com.pralay.common.adapter.processor.sql;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pralay.configuration.model.EventPojo;
import com.pralay.configuration.model.ServerData;

import java.util.concurrent.BlockingQueue;

public class DataConsumer implements Runnable{
	private static ServerData serverData;
	private final BlockingQueue sharedQueue;
	private static EventPojo ep = null;
	private static final Logger LOGGING = LoggerFactory.getLogger(DataConsumer.class);
	
	public DataConsumer(BlockingQueue sharedQueue, ServerData serverData){
		this.sharedQueue = sharedQueue;
		this.serverData = serverData;
	}
	@Override
    public void run() {
		while(true){
            try{
            	//LOGGING.info("Current Consumer Thread Name "+Thread.currentThread().getName());
            	LOGGING.info("<-- Inside Consumer -->");
            	ep = (EventPojo)sharedQueue.take();
            	LOGGING.info("Event ID details: "+ep.getId());
            	LOGGING.info("Event MSG details: "+ep.getMessage());
            	CorrelateEvent ce = new CorrelateEvent(serverData);
	            ce.sendAlarm(ep);
	            UptimeMgmt upm = new UptimeMgmt(serverData);
	            upm.sendAlarm(ep);
            	
            }catch (Exception e) {
            	e.printStackTrace();
            	LOGGING.error(e.getMessage());
            }
            LOGGING.info("<-- End of Consumer -->");
		}
    }
}
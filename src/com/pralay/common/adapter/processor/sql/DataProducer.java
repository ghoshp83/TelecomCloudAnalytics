package com.pralay.common.adapter.processor.sql;

import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pralay.configuration.model.EventPojo;

public class DataProducer implements Runnable{
	private final BlockingQueue sharedQueue;
	private static EventPojo ep = null;
	private static final Logger LOGGING = LoggerFactory.getLogger(DataProducer.class);
	
	public DataProducer(BlockingQueue sharedQueue,EventPojo ep){
		this.sharedQueue = sharedQueue;
		this.ep = ep;
	}
	
	@Override
    public void run() {
            try{
            	//LOGGING.info("Current Producer Thread Name "+Thread.currentThread().getName());
                LOGGING.info("<-- Inside the producer -->");
                LOGGING.info("<-- Inside the producer -->"+ep);
                LOGGING.info("Event MSG details: "+ep.getMessage());
            	sharedQueue.put(ep);
            }catch (Exception e) {
            	e.printStackTrace();
				LOGGING.error(e.getMessage());
            }
            LOGGING.info("<-- Data inserted into shared queue -->"); 
    }	
}

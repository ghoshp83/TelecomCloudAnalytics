package com.pralay.common.actuation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendMailImp implements Runnable{
	private static SendMail sendmail;
	private static String id = null;
	private static String alarm_id = null;
	private static String correlate_id = null;
	private static String correlate_details = null;
	
	private static final Logger LOGGING = LoggerFactory.getLogger(SendMailImp.class);
	
	public SendMailImp(String alarm_id, String correlate_id, String id, String correlate_details, SendMail sendmail){
		this.id = id;
		this.alarm_id = alarm_id;
		this.correlate_id = correlate_id;
		this.correlate_details = correlate_details;
		this.sendmail = sendmail;
	}
	
	@Override
    public void run() {
            try{
            	LOGGING.info("<-- Inside the send mail implementation producer -->");
            	//sendmail.generateAndSendEmail(alarm_id, correlate_id,id, correlate_details);
            }catch (Exception e) {
            	e.printStackTrace();
				LOGGING.error(e.getMessage());
            }
            LOGGING.info("<-- send mail implementation ends for a record -->"); 
    }
	
}

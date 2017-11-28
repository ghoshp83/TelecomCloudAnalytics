package com.pralay.common.actuation;

import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pralay.common.adapter.builder.CommonAdapterBuilder;
import com.pralay.configuration.model.ServerData;

public class SendMail {
	private static ServerData serverData;
	private static Properties mailServerProperties;
	private static Session getMailSession;
	private static MimeMessage generateMailMessage;
	private static final Logger LOGGING = LoggerFactory.getLogger(SendMail.class);
	private static String actuationhost = null;
    private static String actuationport = null;
    private static String actuationusername = null;
    private static String actuationpassword = null;
    private static String actuationFrom_mail_id = null;
	private static String actuationTo_mail_id = null;
	
	public SendMail(ServerData serverData){
		SendMail.serverData = serverData;
	}
	
	public static void generateAndSendEmailDownTime(String Alarm_id,String Correlator_id, String Event_id, 
			String Correlator_details, String Host_Name, String a1_event_date, String a2_event_date,String impact, String resolution) 
			throws AddressException, MessagingException {
		
			actuationhost = serverData.getActuationhost();
			actuationport = serverData.getActuationport();
			actuationusername = serverData.getActuationusername();
			actuationpassword = serverData.getActuationpassword();
			actuationFrom_mail_id = serverData.getActuationfrommailid();
			actuationTo_mail_id = serverData.getActuationtomailid();
			
			
			
		 
			String Subject = serverData.getActuationsubjectpart1()+Alarm_id + serverData.getActuationsubjectpart2() + Correlator_id + "." ;
		    String Message_Body = "Hi, <br>"
								  +"PFB the Details related to the <B>Event Id </B> <i>"+Event_id+"</i> and take appropriate action.<br><br>"
								  +"<B>Alarm Id:</B> " +Alarm_id+ ".<br>"
								  +"<B>Host Name:</B> " +Host_Name+ ".<br>"
								  +"<B>Event_TimeStamp:</B> " +a1_event_date+ ".<br>"
								  +"<B>Correlator Id:</B> " +Correlator_id+ ".<br>"
								  +"<B>Correlator_TimeStamp:</B> " +a2_event_date+ ".<br>"
								  +"<B>Correlator Details:</B> " +Correlator_details+ ".<br>"
								  +"<B>Impact:</B> " +impact+ ".<br>"
								  +"<B>Resolution:</B> " +resolution+ ".<br>"
								  +"For any Other Queries please contact Admin.<br><br>"
								  +"Regards,<br>"
								  +"Admin"; 
			 						
		    //String Subject = "";
		    //String Message_Body = "";
		 
		    // Step1
			LOGGING.info("\n 1st ===> setup Mail Server Properties..");
			mailServerProperties = System.getProperties();
			mailServerProperties.put("mail.transport.protocol", "smtp");
			mailServerProperties.put("mail.smtp.host", actuationhost);
			mailServerProperties.put("mail.smtp.port", actuationport);
			//mailServerProperties.put("mail.smtp.auth", "true");
			mailServerProperties.put("mail.smtp.auth", "false");
			mailServerProperties.put("mail.smtp.starttls.enable", "true");
			LOGGING.info("Mail Server Properties have been setup successfully..");
			

			Session emailSession = Session.getInstance(mailServerProperties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(actuationusername,actuationpassword);
                }
        });
		emailSession.setDebug(true);
		
		// Get the default Session object.
		Session mailSession = Session.getDefaultInstance(mailServerProperties);
		
		LOGGING.info("To Address : "+actuationTo_mail_id);
		
		
		
		// Create a default MimeMessage object.
      MimeMessage message = new MimeMessage(mailSession);
      // Set From: header field of the header.
      message.setFrom(new InternetAddress(actuationFrom_mail_id));
      // Set To: header field of the header.
      message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(actuationTo_mail_id));	
     // Set Subject: header field
      message.setSubject(Subject);
      // Now set the actual message
     // message.setText(Message_Body);
	 message.setContent(Message_Body, "text/html");
      // Send message
	  Transport transport = emailSession.getTransport("smtp");
     // transport.connect(host, username, password);
	  transport.connect();
      transport.sendMessage(message, message.getAllRecipients());
      //Transport.send(message);
      
			 
			// Step2
			/*LOGGING.info("\n\n 2nd ===> get Mail Session..");
			getMailSession = Session.getDefaultInstance(mailServerProperties, null);
			Session mailSession = Session.getDefaultInstance(mailServerProperties);
			generateMailMessage = new MimeMessage(getMailSession);
			generateMailMessage.setFrom(new InternetAddress(actuationFrom_mail_id));
			generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(actuationTo_mail_id));
			generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(actuationFrom_mail_id));
			generateMailMessage.setSubject(Subject);
			generateMailMessage.setContent(Message_Body, "text/html");
			LOGGING.info("Mail Session has been created successfully..");
			 
			// Step3
			LOGGING.info("\n\n 3rd ===> Get Session and Send mail");
			Transport transport = getMailSession.getTransport("smtps");
			//transport.connect(actuationhost, actuationusername, actuationpassword);
			transport.connect();
			transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
			transport.close();
			LOGGING.info("\n\n ===> Your Java Program has just sent an Email successfully. Check your email..");*/
		}
	
	
	public static void generateAndSendEmailUpTime(String Alarm_id, String Event_id, String Correlator_details,
			String Host_Name,String Event_Date) 
			throws AddressException, MessagingException {
		
			actuationhost = serverData.getActuationhost();
			actuationport = serverData.getActuationport();
			actuationusername = serverData.getActuationusername();
			actuationpassword = serverData.getActuationpassword();
			actuationFrom_mail_id = serverData.getActuationfrommailid();
			actuationTo_mail_id = serverData.getActuationtomailid();
			
			
			
		 
			//String Subject = serverData.getActuationsubjectpart1()+Alarm_id + serverData.getActuationsubjectpart2() + Correlator_id + "." ;
			
			//String Subject = "Notification for prediction details for Alarm Id : "+Alarm_id;
			String Subject = serverData.getActuationsubjectuptime()+Alarm_id;
			
			
		    /*String Message_Body = "Hi, <br>"
								  +"PFB the Details related to the <B>Event Id </B> <i>"+Event_id+"</i> and take appropriate action.<br><br>"
								  +"<B>Alarm Id:</B> " +Alarm_id+ ".<br>"
								  +"<B>Correlator Id:</B> " +Correlator_id+ ".<br>"
								  +"<B>Correlator Details:</B> " +Correlator_details+ ".<br>"
								  +"For any Other Queries please contact Admin.<br><br>"
								  +"Regards,<br>"
								  +"Admin"; */
			 
		    String Message_Body = "Hi, <br>"
		    		+"PFB the Prediction Details related to the <B>Event Id </B> <i>"+Event_id+"</i> and take appropriate action.<br><br>"
		    		+"<B>Alarm Id:</B> " +Alarm_id+ ".<br>"
		    		+"<B>Host Name:</B> " +Host_Name+ ".<br>"
		    		+"<B>Event_Date:</B> " +Event_Date+ ".<br>"
		    		+"<B>Prediction Details:</B> " +Correlator_details+ ".<br>"
		    		+"For any Other Queries please contact Admin.<br><br>"
		    		+"Regards,<br>"
		    		  +"Admin";

		    
		    //String Subject = "";
		    //String Message_Body = "";
		 
		    // Step1
			LOGGING.info("\n 1st ===> setup Mail Server Properties..");
			mailServerProperties = System.getProperties();
			mailServerProperties.put("mail.transport.protocol", "smtp");
			mailServerProperties.put("mail.smtp.host", actuationhost);
			mailServerProperties.put("mail.smtp.port", actuationport);
			//mailServerProperties.put("mail.smtp.auth", "true");
			mailServerProperties.put("mail.smtp.auth", "false");
			mailServerProperties.put("mail.smtp.starttls.enable", "true");
			LOGGING.info("Mail Server Properties have been setup successfully..");
			

			Session emailSession = Session.getInstance(mailServerProperties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(actuationusername,actuationpassword);
                }
        });
		emailSession.setDebug(true);
		
		// Get the default Session object.
		Session mailSession = Session.getDefaultInstance(mailServerProperties);
		
		LOGGING.info("To Address : "+actuationTo_mail_id);
		
		
		
		// Create a default MimeMessage object.
      MimeMessage message = new MimeMessage(mailSession);
      // Set From: header field of the header.
      message.setFrom(new InternetAddress(actuationFrom_mail_id));
      // Set To: header field of the header.
      message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(actuationTo_mail_id));	
     // Set Subject: header field
      message.setSubject(Subject);
      // Now set the actual message
     // message.setText(Message_Body);
	 message.setContent(Message_Body, "text/html");
      // Send message
	  Transport transport = emailSession.getTransport("smtp");
     // transport.connect(host, username, password);
	  transport.connect();
      transport.sendMessage(message, message.getAllRecipients());
      //Transport.send(message);
      
			 
			// Step2
			/*LOGGING.info("\n\n 2nd ===> get Mail Session..");
			getMailSession = Session.getDefaultInstance(mailServerProperties, null);
			Session mailSession = Session.getDefaultInstance(mailServerProperties);
			generateMailMessage = new MimeMessage(getMailSession);
			generateMailMessage.setFrom(new InternetAddress(actuationFrom_mail_id));
			generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(actuationTo_mail_id));
			generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(actuationFrom_mail_id));
			generateMailMessage.setSubject(Subject);
			generateMailMessage.setContent(Message_Body, "text/html");
			LOGGING.info("Mail Session has been created successfully..");
			 
			// Step3
			LOGGING.info("\n\n 3rd ===> Get Session and Send mail");
			Transport transport = getMailSession.getTransport("smtps");
			//transport.connect(actuationhost, actuationusername, actuationpassword);
			transport.connect();
			transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
			transport.close();
			LOGGING.info("\n\n ===> Your Java Program has just sent an Email successfully. Check your email..");*/
		}
}
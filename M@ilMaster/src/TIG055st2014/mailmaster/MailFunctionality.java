// Uses code from http://stackoverflow.com/questions/2020088/sending-email-in-android-using-javamail-api-without-using-the-default-built-in-a

package TIG055st2014.mailmaster;
import javax.mail.Authenticator;
import javax.activation.CommandMap;
import javax.activation.DataHandler;   
import javax.activation.DataSource;   
import javax.activation.MailcapCommandMap;
import javax.mail.Message;   
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;   
import javax.mail.Session;   
import javax.mail.Transport;   
import javax.mail.internet.InternetAddress;   
import javax.mail.internet.MimeMessage;   

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayInputStream;   
import java.io.IOException;   
import java.io.InputStream;   
import java.io.OutputStream;   
import java.security.Security;   
import java.util.Properties;   
import java.util.concurrent.ExecutionException;

public class MailFunctionality extends Authenticator {
    private String user;   
    private String password;   
    private Session session;   
    private String port;  
    private static MailFunctionality mf;

    static {   
        Security.addProvider(new JSSEProvider());   
    }  

    public MailFunctionality(String user, String password, String type) {   
        this.user = user;   
        this.password = password;  
        Log.d("MailFunctionality",  this.user + "   " + this.password + "    " + type);
        try{
        Properties props = new Properties();   
        props.setProperty("mail.transport.protocol", "smtp");  
        if(!type.equalsIgnoreCase("gmail.com")){ //Use TLS security and port 587
        	if(type.equalsIgnoreCase("student.gu.se")){
        		props.setProperty("mail.host", "smtpgw.gu.se");
        	}
        	else{
        		props.setProperty("mail.host", "smtp.live.com");
        	}
        	props.put("mail.smtp.starttls.enable","true");
        	this.port = "587";
        }
        else{ // Use SSL security and port 465
        	props.setProperty("mail.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
            props.put("mail.smtp.socketFactory.fallback", "false");   
            this.port = "465";
        }
        props.put("mail.smtp.auth", "true");   
        props.put("mail.smtp.port", port); //587 live/hotmail/outlook  //465 gmail 
        props.put("mail.smtp.socketFactory.port", port);    //587 live/hotmail/outlook  //465 gmail  
        props.setProperty("mail.smtp.quitwait", "false");   
        // There is something wrong with MailCap, javamail can not find a handler for the multipart/mixed part, 
        //so this bit needs to be added. 
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap(); 
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html"); 
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml"); 
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain"); 
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed"); 
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822"); 
        CommandMap.setDefaultCommandMap(mc); 
        session = Session.getInstance(props, this);   
        }
        catch (Exception e){
        	Log.e("error", e.getMessage());
        }
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {   
        return new PasswordAuthentication(user, password);   
    }   

    public void sendMail(String subject, String body, String sender, String recipients) {   
        try{
        	Log.d("MailFunctionality", "Send");
        	SendTask task = new SendTask(subject,body,sender,recipients);
        	task.executeOnExecutor(task.THREAD_POOL_EXECUTOR);
        }
        catch(Exception e){
        	e.printStackTrace();
        }
    }   
    private class SendTask extends AsyncTask<Void, Void, Void>{
    	
    	private String sb, bd, sd , rcp;
    	
    	private SendTask(String subject, String body, String sender, String recipients){
    		sb = subject;
    		bd = body;
    		sd = sender;
    		rcp = recipients;
    	}
    	
    	@Override
    	protected Void doInBackground(Void... arg0) {
    		try{
    	        MimeMessage message = new MimeMessage(session);   
    	        message.setSender(new InternetAddress(sd));  
    	        message.setFrom(new InternetAddress(sd));
    	        message.setSubject(sb);   
    	        message.setText(bd);
    	        if (rcp.indexOf(',') > 0)   
    	            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(rcp));   
    	        else  
    	            message.setRecipient(Message.RecipientType.TO, new InternetAddress(rcp));   
    	        Transport.send(message);   
    		}
    		catch(Exception e){
    			e.printStackTrace();
    		}
    		return null;
    	}
    }
    public boolean validate() {	
		try {
    			ConnectTest c = new ConnectTest(user,password);
    			c.executeOnExecutor(c.THREAD_POOL_EXECUTOR);
    			return(c.get());
			} 
			catch (Exception e) {
				return false;
			}
    }
    private class ConnectTest extends AsyncTask<Void, Void, Boolean>{
    	
    	private String user, password;
    	
    	private ConnectTest(String u, String p){
    		user = u;
    		password = p; 
    	}
    	
    	@Override
    	protected Boolean doInBackground(Void... arg0) {
			try {
				Transport t = session.getTransport("smtp");
	    		t.connect(user, password);
	    		t.close();
	    		return true;
			} 
			catch (Exception e) {
				e.printStackTrace();
				return false;
			}
    	}
    }
}  

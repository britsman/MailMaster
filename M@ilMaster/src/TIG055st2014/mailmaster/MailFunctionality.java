// Uses code from http://stackoverflow.com/questions/2020088/sending-email-in-android-using-javamail-api-without-using-the-default-built-in-a

package TIG055st2014.mailmaster;
import javax.mail.Authenticator;
import javax.activation.CommandMap;
import javax.activation.DataHandler;   
import javax.activation.DataSource;   
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;   
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;   
import javax.mail.Session;   
import javax.mail.Store;
import javax.mail.Transport;   
import javax.mail.internet.InternetAddress;   
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;   
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayInputStream;   
import java.io.IOException;   
import java.io.InputStream;   
import java.io.OutputStream;   
import java.security.Security;   
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;   
import java.util.concurrent.ExecutionException;

public class MailFunctionality extends Authenticator {
    private String user;   
    private String password;   
    private Session session;   
    private String port; 
    private String imapHost;  
    private Multipart mp;

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
        props.setProperty("mail.store.protocol", "imaps"); 
        props.setProperty("mail.imaps.auth.plain.disable", "true");
        props.setProperty("mail.imaps.auth.ntlm.disable", "true");
        props.setProperty("mail.imaps.auth.gssapi.disable", "true");
        props.put("mail.imaps.ssl.enable", "true");  
        props.setProperty("mail.imap.starttls.enable", "true");
        
        if(!type.equalsIgnoreCase("gmail.com")){ //Use TLS security and port 587
        	if(type.equalsIgnoreCase("student.gu.se")){
        		props.setProperty("mail.host", "smtpgw.gu.se");
        		imapHost = "imap.gmail.com";
        		
        	}
        	else{
        		props.setProperty("mail.host", "smtp.live.com");
        		imapHost = "imap-mail.outlook.com";
        	}
        	props.put("mail.smtp.starttls.enable","true");
        	this.port = "587";
        }
        else{ // Use SSL security and port 465
        	props.setProperty("mail.host", "smtp.gmail.com");
        	imapHost = "imap.gmail.com";
            props.put("mail.smtp.ssl.enable", "true");   
            this.port = "465";
        }
        props.put("mail.smtp.auth", "true");   
        props.put("mail.smtp.port", port); //587 live/hotmail/outlook  //465 gmail 
        
        
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
        //session.setDebug(true);
        }
        catch (Exception e){
        	Log.e("error", e.getMessage());
        }
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {   
        return new PasswordAuthentication(user, password);   
    }   

    public void sendMail(String subject, String body, String sender, String recipients, ArrayList<String> attachments) {   
        try{
        	Log.d("MailFunctionality", "Send");
        	SendTask task = new SendTask(subject,body,sender,recipients, attachments);
        	task.executeOnExecutor(task.THREAD_POOL_EXECUTOR);
        }
        catch(Exception e){
        	e.printStackTrace();
        }
    }   
    private class SendTask extends AsyncTask<Void, Void, Void>{
    	
    	private String sb, bd, sd , rcp;
    	private ArrayList<String> atch;
    	
    	private SendTask(String subject, String body, String sender, String recipients, ArrayList<String> attachments){
    		sb = subject;
    		bd = body;
    		sd = sender;
    		rcp = recipients;
    		atch = attachments;
    	}
    	
    	@Override
    	protected Void doInBackground(Void... arg0) {
    		try{
    			mp = new MimeMultipart();
    	        MimeMessage message = new MimeMessage(session); 
    	        message.setFrom(new InternetAddress(sd));
    	        DataHandler handler = new DataHandler(new ByteArrayDataSource(bd.getBytes(), "text/plain"));  
    	        message.setDataHandler(handler);
    	        message.setSubject(sb);   
    	        BodyPart messageBodyPart = new MimeBodyPart(); 
    	        messageBodyPart.setText(bd); 
    	        mp.addBodyPart(messageBodyPart); 
    	        for(String file : atch){
    	        	addAttachment(file);
    	        }
    	        if (rcp.indexOf(',') > 0){
    	            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(rcp));   
    	        }
    	        else{  
    	            message.setRecipient(Message.RecipientType.TO, new InternetAddress(rcp));   
    	        }
    	        message.setContent(mp);
    	        Transport.send(message);   
    		}
    		catch(Exception e){
    			e.printStackTrace();
    		}
    		return null;
    	}
    }
	private void addAttachment(String filePath){
		try{
			BodyPart messageBodyPart = new MimeBodyPart(); 
			DataSource source = new FileDataSource(filePath); 
			messageBodyPart.setDataHandler(new DataHandler(source)); 
			messageBodyPart.setFileName(filePath);
			mp.addBodyPart(messageBodyPart);
		}
		catch(Exception e){
			e.printStackTrace();
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
    public ArrayList<Message> getInbox() {	
		try {
    			ReadTask task = new ReadTask(user,password);
    			task.executeOnExecutor(task.THREAD_POOL_EXECUTOR);
    			Log.d("MailFunctionality",  "Reading reached");
    			return task.get();
			} 
			catch (Exception e) {
				return new ArrayList<Message>();
			}
    }
    //Partly based on http://www.compiletimeerror.com/2013/06/reading-email-using-javamail-api-example.html
    private class ReadTask extends AsyncTask<Void, Void, ArrayList<Message>>{
    	
    	private String user, password;
    	
    	private ReadTask(String u, String p){
    		user = u;
    		password = p; 
    	}
    	
    	@Override
    	protected ArrayList<Message> doInBackground(Void... arg0) {
    		ArrayList<Message> emails = new ArrayList<Message>();
			try {
			    Store store = session.getStore();
			    store.connect(imapHost, user, password);
			    Folder inbox = store.getFolder("INBOX");
			    inbox.open(Folder.READ_ONLY);
			    int limit = 19;
			    int count = inbox.getMessageCount();
			    if(count < 20){
			    	limit = count-1;
			    } 
			    Message[] temp = inbox.getMessages(count-limit, count);
			    Collections.addAll(emails, temp);
			    Collections.reverse(emails);
			    for(int i = 0; i < emails.size(); i++){//Crashes with folderclosed exception if printout is removed.
			    	Log.d("MailFunctionality", emails.get(i).getSubject());
			    } 
			    inbox.close(false);
			    store.close();
    			return emails;
			}
			catch (Exception e) {
				e.printStackTrace();
				return emails;
			}
    	}
    }
}  

// Uses code from http://stackoverflow.com/questions/2020088/sending-email-in-android-using-javamail-api-without-using-the-default-built-in-a

package TIG055st2014.mailmaster;
import javax.mail.Authenticator;
import javax.activation.CommandMap;
import javax.activation.DataHandler;   
import javax.activation.DataSource;   
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;   
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;   
import javax.mail.Session;   
import javax.mail.Store;
import javax.mail.Transport;   
import javax.mail.internet.InternetAddress;   
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;   
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log; 
import android.view.Gravity;
import android.widget.Toast;

import java.security.Security;   
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;   

public class MailFunctionality extends Authenticator {
    private String user;   
    private String password;   
    private Session session;   
    private String port; 
    private String imapHost;  
    private Multipart mp;
    private String sendProtocol;

    static {   
        Security.addProvider(new JSSEProvider());   
    }  

    public MailFunctionality(String user, String password, String type) {   
        this.user = user;   
        this.password = password;  
        Log.d("MailFunctionality",  this.user + "   " + this.password + "    " + type);
        DisplayEmail d = DisplayEmail.getInstance();
        try{
        Properties props = new Properties();   
          
        props.setProperty("mail.store.protocol", "imaps"); 
        props.setProperty("mail.imaps.auth.plain.disable", "true");
        props.setProperty("mail.imaps.auth.ntlm.disable", "true");
        props.setProperty("mail.imaps.auth.gssapi.disable", "true");
        props.setProperty("mail.imaps.ssl.enable", "true");  
        props.setProperty("mail.imaps.starttls.enable", "true");
        props.setProperty("mail.imaps.connectionpoolsize", "10");
    	props.setProperty("mail.imaps.partialfetch", "false");
        
        if(!type.equalsIgnoreCase("gmail.com")){ //Use TLS security and port 587
        	if(type.equalsIgnoreCase("student.gu.se")){
        		props.setProperty("mail.host", "smtpgw.gu.se");
        		imapHost = "imap.gmail.com";
        		
        	}
        	else{
        		props.setProperty("mail.host", "smtp.live.com");
        		imapHost = "imap-mail.outlook.com";
        		if(d.getFolderName() != null && d.getFolderName().equalsIgnoreCase("[Gmail]/Sent Mail")){
        			d.setFolderName("Sent");
        		}
        		else if(d.getFolderName() != null && d.getFolderName().equalsIgnoreCase("[Gmail]/Drafts")){
        			d.setFolderName("Drafts");
        		}
        	}
        	sendProtocol = "smtp";
        	props.setProperty("mail.transport.protocol", sendProtocol);
        	props.setProperty("mail.smtp.starttls.enable","true");
        	port = "587";
            props.setProperty("mail.smtp.auth", "true");   
            props.setProperty("mail.smtp.port", port); 
        }
        else{ // Use SSL security and port 465
        	sendProtocol = "smtps";
        	props.setProperty("mail.transport.protocol", sendProtocol);
        	props.setProperty("mail.host", "smtp.gmail.com");
        	imapHost = "imap.gmail.com";
            port = "465";
            props.setProperty("mail.smtps.auth", "true");   
            props.setProperty("mail.smtps.port", port); 
        }
        props.setProperty("mail.imaps.ssl.trust", imapHost);
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
        	e.printStackTrace();
        }
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {   
        return new PasswordAuthentication(user, password);   
    }   

    public void sendMail(String subject, String body, String sender, String recipients, 
    		             String cc, String bcc, ArrayList<String> attachments, Context context) {   
        try{
        	Log.d("MailFunctionality", "Send");
        	SendTask task = new SendTask(subject,body,sender,recipients, cc, bcc, attachments, context);
        	task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        catch(Exception e){
        	e.printStackTrace();
        }
    }   
    private class SendTask extends AsyncTask<Void, Void, Void>{
    	
    	private String sb, bd, sd , rcp, cc, bcc;
    	private ArrayList<String> atch;
    	private boolean sent;
    	private Context c;
    	
    	private SendTask(String subject, String body, String sender, String recipients, 
    					 String _cc, String _bcc,ArrayList<String> attachments, Context context){
    		sb = subject;
    		bd = body;
    		sd = sender;
    		rcp = recipients;
    		cc = _cc;
    	    bcc = _bcc;
    		atch = attachments;
    		c = context;
    		sent = false;
    	}
    	
    	@Override
    	protected Void doInBackground(Void... arg0) {
    		try{
    			mp = new MimeMultipart();
    	        MimeMessage message = new MimeMessage(session); 
    	        message.setFrom(new InternetAddress(sd));
    	        DataHandler handler = new DataHandler(new ByteArrayDataSource(bd.getBytes(), "text/html;charset=utf-8"));  
    	        message.setDataHandler(handler);
    	        message.setSubject(sb, "utf-8");   
    	        BodyPart messageBodyPart = new MimeBodyPart(); 
    	        messageBodyPart.setText(bd); 
    	        mp.addBodyPart(messageBodyPart); 
    	        for(String file : atch){
    	        	addAttachment(file);
    	        }
    	        if(!rcp.equals("")){
    	        	addRecipients(message, rcp, Message.RecipientType.TO);
    	        }
    	        if(!cc.equals("")){
    	        	addRecipients(message, cc, Message.RecipientType.CC);
    	        }
    	        if(!bcc.equals("")){
    	        	addRecipients(message, bcc, Message.RecipientType.BCC);
    	        };
    	        message.setContent(mp, "text/html");
				Transport t = session.getTransport(sendProtocol);
	    		t.connect(user, password);
	    		t.sendMessage(message, message.getAllRecipients());
	    		t.close();
	    		sent = true;
    		}
    		catch(Exception e){
    			e.printStackTrace();
    		}
    		return null;
    	}

    	@Override
    	protected void onPostExecute(Void v){
    		if(sent){
				Toast toast = Toast.makeText(c,
            			"Send successful! (if email was close to max size, this may appear " +
            			"minutes after pressing send).", Toast.LENGTH_SHORT);
            	toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
            	toast.show();
    		}
    		else{//Need to save draft.
				Toast toast = Toast.makeText(c,
            			"Send failed, one or more supplied adresses contain illegal characters " +
            			"(email has been saved as draft).", Toast.LENGTH_LONG);
            	toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
            	toast.show();
    		}
    	}
    }
	private void addRecipients(Message msg, String adresses, 
            Message.RecipientType type) throws Exception{
		if (adresses.indexOf(',') > 0){
			msg.setRecipients(type, InternetAddress.parse(adresses));   
		}
		else{  
			msg.setRecipient(type, new InternetAddress(adresses));   
		}
	}
	public void addAttachment(String filePath){
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
    			c.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
				Transport t = session.getTransport(sendProtocol);
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
    			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
				DisplayEmail d = DisplayEmail.getInstance();
				if(d.getStore()!=null && d.getStore().isConnected()){
					d.getStore().close();
				}
			    Store store = session.getStore("imaps");
			    store.connect(imapHost, user, password);
			    if(d.getEmailFolder()!=null && d.getEmailFolder().isOpen()){
			    	d.getEmailFolder().close(false);
			    }
			    Folder inbox = store.getFolder(d.getFolderName());
			    d.setEmailFolder(inbox);
			    inbox.open(Folder.READ_WRITE);
			    int limit = 19;
			    int count = inbox.getMessageCount();
			    if(count < 20){
			    	limit = count-1;
			    } 
			    Message[] temp = inbox.getMessages(count-limit, count);
			    //Fetch code based on http://codereview.stackexchange.com/questions/36878/is-there-any-way-to-make-this-javamail-code-faster
			    //Noticeable improvement compared to looping through each message.
			    FetchProfile profile = new FetchProfile();
			    profile.add(FetchProfile.Item.CONTENT_INFO);
			    profile.add(FetchProfile.Item.ENVELOPE);
			    profile.add(FetchProfile.Item.FLAGS);
			    inbox.fetch(temp, profile);
			    Collections.addAll(emails, temp);
			    Collections.reverse(emails);
    			return emails;
			}
			catch (Exception e) {
				e.printStackTrace();
				return emails;
			}
    	}
    }
    public String getContents() {	
		try {
    			ContentsTask ct = new ContentsTask(user,password);
    			ct.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    			Log.d("MailFunctionality",  "Getting contents");
    			return ct.get();
			} 
			catch (Exception e) {
				return "";
			}
    }
    private class ContentsTask extends AsyncTask<Void, Void, String>{
    	
    	private String user, password;
    	
    	private ContentsTask(String u, String p){
    		user = u;
    		password = p; 
    	}
    	
    	@Override
    	protected String doInBackground(Void... arg0) {
    		String plainContents = "";
    		String htmlContents = "";
			DisplayEmail d = DisplayEmail.getInstance();
			try{
				if(d.getStore()!=null && !d.getStore().isConnected()){
					d.getStore().connect(imapHost, user, password);
				}
				if(d.getEmailFolder()!=null && !d.getEmailFolder().isOpen()){
					d.getEmailFolder().open(Folder.READ_WRITE);
				}
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
			try {
				if(d.getEmail().isMimeType("text/*")){
					plainContents = d.getEmail().getContent().toString();
				}
				else{
					MimeMultipart _mp = (MimeMultipart) d.getEmail().getContent();
					for(int i = 0; i < _mp.getCount(); i++){
						BodyPart bp = _mp.getBodyPart(i);
						if(bp.isMimeType("text/html")){
							htmlContents = bp.getContent().toString();
						
						}
						else if(bp.isMimeType("text/*")){
							plainContents += bp.getContent().toString() + "\n";
						
						}
						else if(bp.isMimeType("multipart/*")){
							MimeMultipart _mp1 = (MimeMultipart)bp.getContent();
							plainContents +=_mp1.getBodyPart(0).getContent().toString()+ "\n";
						
						}
						else{
							try{
								Log.d("type", bp.getContentType() );
								//String [] temp = bp.getDataHandler().getName().split("/");
								DataSource file = bp.getDataHandler().getDataSource();
								d.addFile(file);
								d.addAttachment(bp.getDataHandler().getName());
								
							}
							catch(Exception exe){
								exe.printStackTrace();
						    	//return plainContents;
							}
						}
					}
				}
				if(htmlContents.equals("")){
					return plainContents;
				}
				else{
					return htmlContents + "\n"+ plainContents;
				}
			}
			catch(Exception e){
				e.printStackTrace();
		    	return plainContents;
			}
		}   	
    }
    public Message getReply(Message m) {	
		try {
    			ReplyTask rt = new ReplyTask(user,password);
    			rt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, m);
    			Log.d("MailFunctionality",  "Getting default fields for reply");
    			return rt.get();
			} 
			catch (Exception e) {
				return null;
			}
    }
    private class ReplyTask extends AsyncTask<Message, Void, Message>{
    	
    	private String user, password;
    	
    	private ReplyTask(String u, String p){
    		user = u;
    		password = p; 
    	}
    	
    	@Override
    	protected Message doInBackground(Message... m) {
			try {
				DisplayEmail d = DisplayEmail.getInstance();
				if(d.getStore()!=null && !d.getStore().isConnected()){
					d.getStore().connect(imapHost, user, password);
				}
				if(d.getEmailFolder()!=null && !d.getEmailFolder().isOpen()){
					d.getEmailFolder().open(Folder.READ_WRITE);
				}
				return m[0].reply(true);
			}
			catch(Exception e){
				e.printStackTrace();
		    	return null;
			}
		}   	
    }
    public void saveDraft(String subject, String body, String sender, String recipients, 
            String cc, String bcc, ArrayList<String> attachments, Context context) {   
    	try{
    		DraftTask task = new DraftTask(user, password, subject,body,sender,recipients, cc, bcc, attachments, context);
    		task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    	}
			catch (Exception e) {
				e.printStackTrace();
			}
    }
    private class DraftTask extends AsyncTask<Void, Void, Void>{
    	
    	private String user, password;
    	private String sb, bd, sd , rcp, cc, bcc;
    	private boolean saved;
    	private Context c;
    	
    	private DraftTask(String u, String p, String subject, String body, String sender, String recipients, 
		 String _cc, String _bcc,ArrayList<String> attachments, Context context){
    		sb = subject;
    		bd = body;
    		sd = sender;
    		rcp = recipients;
    		cc = _cc;
    		bcc = _bcc;
    		c = context;
    		saved = false;
    		user = u;
    		password = p; 
    	}
    	
    	@Override
    	protected Void doInBackground(Void... arg0) {
			try {
				DisplayEmail d = DisplayEmail.getInstance();
				if(d.getStore()!=null && d.getStore().isConnected()){
					d.getStore().close();
				}
			    Store store = session.getStore("imaps");
			    store.connect(imapHost, user, password);
			    if(d.getEmailFolder()!=null && d.getEmailFolder().isOpen()){
			    	d.getEmailFolder().close(false);
			    }
			    Folder drafts = store.getFolder(d.getFolderName());
			    d.setEmailFolder(drafts);
			    drafts.open(Folder.READ_WRITE);
    	        MimeMessage message = new MimeMessage(session); 
    	        message.setFrom(new InternetAddress(sd));
    	        DataHandler handler = new DataHandler(new ByteArrayDataSource(bd.getBytes(), "text/plain;charset=utf-8"));  
    	        message.setDataHandler(handler);
    	        message.setSubject(sb, "utf-8");   
    	        if(!rcp.equals("")){
    	        	addRecipients(message, rcp, Message.RecipientType.TO);
    	        }
    	        if(!cc.equals("")){
    	        	addRecipients(message, cc, Message.RecipientType.CC);
    	        }
    	        if(!bcc.equals("")){
    	        	addRecipients(message, bcc, Message.RecipientType.BCC);
    	        };
    	        message.setContent(bd, "text/plain");
    	        message.setFlag(Flag.DRAFT, true);
			    drafts.appendMessages(new Message[]{message});
			    saved = true;
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return null;
    	}
    	@Override
    	protected void onPostExecute(Void v){
    		if(saved){
				Toast toast = Toast.makeText(c,
            			"Draft Saved.", Toast.LENGTH_SHORT);
            	toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
            	toast.show();
    		}
    		else{//Need to save draft.
				Toast toast = Toast.makeText(c,
            			"Failed to save draft.", Toast.LENGTH_SHORT);
            	toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
            	toast.show();
    		}
    	}
    }
}
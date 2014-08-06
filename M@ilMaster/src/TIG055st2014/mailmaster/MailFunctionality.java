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

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log; 
import android.view.Gravity;
import android.webkit.WebView.FindListener;
import android.widget.EditText;
import android.widget.Toast;

import java.security.Security;   
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;   

/**
 * Class used as interface to the javamail API.
 */
public class MailFunctionality extends Authenticator {
	private String user;   
	private String password;   
	private String type; 
	private Session session;   
	private String port; 
	private String imapHost;  
	private Multipart mp;
	private String sendProtocol;
	String plainContents;
	String htmlContents;

	/**
	 * Used to sent securely.
	 */
	static {   
		Security.addProvider(new JSSEProvider());   
	}  
	/**
	 * Used to set properties for sending/retrieving email, based on the
	 * account's email provider.
	 * 
	 * @param user The email address to use.
	 * @param password The password to use.
	 * @param type Used to determine which email provider the adress is from.
	 */
	public MailFunctionality(String user, String password, String type) {   
		this.user = user;   
		Encryption encryption = new Encryption();
		String key = "Some Key";
		this.password = password;
		String encrypted = encryption.encrypt(key, password);
		this.type = type;
		Log.d("MailFunctionality",  this.user + "   " + encrypted + "    " + type);
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
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
			//Use TLS security and port 587
			if(!type.equalsIgnoreCase("gmail.com")){ 
				if(type.equalsIgnoreCase("student.gu.se")){
					props.setProperty("mail.host", "smtpgw.gu.se");
					imapHost = "imap.gmail.com";
				}
				else{
					props.setProperty("mail.host", "smtp.live.com");
					imapHost = "imap-mail.outlook.com";
					if(apv.getFolderName(user).equalsIgnoreCase("[Gmail]/Sent Mail")){
						apv.setFolderName(user, "Sent");
					}
					else if(apv.getFolderName(user).equalsIgnoreCase("[Gmail]/Drafts")){
						apv.setFolderName(user, "Drafts");
					}
				}
				sendProtocol = "smtp";
				props.setProperty("mail.transport.protocol", sendProtocol);
				props.setProperty("mail.smtp.starttls.enable","true");
				port = "587";
				props.setProperty("mail.smtp.auth", "true");   
				props.setProperty("mail.smtp.port", port); 
			}
			// Use SSL security and port 465
			else{ 
				sendProtocol = "smtps";
				props.setProperty("mail.transport.protocol", sendProtocol);
				props.setProperty("mail.host", "smtp.gmail.com");
				imapHost = "imap.gmail.com";
				port = "465";
				props.setProperty("mail.smtps.auth", "true");   
				props.setProperty("mail.smtps.port", port); 
			}
			props.setProperty("mail.imaps.ssl.trust", imapHost);
			// "There is something wrong with MailCap, javamail can not find a handler for the multipart/mixed part, 
			//so this bit needs to be added." <- quote from:
			//http://stackoverflow.com/questions/2020088/sending-email-in-android-using-javamail-api-without-using-the-default-built-in-a
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
			e.printStackTrace();
		}
	}

	@Override
	public PasswordAuthentication getPasswordAuthentication() {   
		return new PasswordAuthentication(user, password);   
	}   
	/**
	 * Starts the AsyncTask for sending mail. Reference to activity is needed in order to display/dismiss loading dialog.
	 */
	public void sendMail(String subject, String body, String sender, String recipients, 
			String cc, String bcc, ArrayList<String> attachments, Context context, ComposeActivity a) {   
		try{
			Log.d("MailFunctionality", "Send");
			SendTask task = new SendTask(subject,body,sender,recipients, cc, bcc, attachments, context, a);
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}   
	/**
	 * AsyncTask for sending email (with attachments). If the send fails ('unable to send', not 'not received') 
	 * then the message is saved as a draft.
	 */
	private class SendTask extends AsyncTask<Void, Void, Void>{

		private String subject, body, sender, recipients, cc, bcc;
		private ArrayList<String> attachments;
		private boolean sent;
		private Context context;
		private ProgressDialog dialog;

		private SendTask(String sb, String bd, String sd, String rcp, 
				String _cc, String _bcc,ArrayList<String> atch, Context c, ComposeActivity a){
			subject = sb;
			body = bd;
			sender = sd;
			recipients = rcp;
			cc = _cc;
			bcc = _bcc;
			attachments = atch;
			context = c;
			sent = false;
			dialog = new ProgressDialog(a);
		}
		@Override
		protected void onPreExecute() {
			dialog.setMessage("Sending Email...");
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.show();
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			try{
				mp = new MimeMultipart();
				MimeMessage message = new MimeMessage(session); 
				message.setFrom(new InternetAddress(sender));
				DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/html;charset=UTF-8")); 
				message.setDataHandler(handler);
				message.setSubject(subject, "utf-8");   
				BodyPart messageBodyPart = new MimeBodyPart(); 
				messageBodyPart.setText(body); 
				mp.addBodyPart(messageBodyPart); 
				for(String file : attachments){
					addAttachment(file);
				}
				if(!recipients.equals("")){
					addRecipients(message, recipients, Message.RecipientType.TO);
				}
				if(!cc.equals("")){
					addRecipients(message, cc, Message.RecipientType.CC);
				}
				if(!bcc.equals("")){
					addRecipients(message, bcc, Message.RecipientType.BCC);
				}
				message.setContent(mp, "multipart/mixed");
				Transport t = session.getTransport(sendProtocol);
				t.connect(user, password);
				t.sendMessage(message, message.getAllRecipients());
				t.close();
				sent = true;
				if(type.equals("student.gu.se")){
					SaveAsSentTask sst = new SaveAsSentTask(message, context);
					sst.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v){
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			if(sent){
				Toast toast = Toast.makeText(context,
						"Send successful! (if email was close to max size, this may appear " +
								"minutes after pressing send).", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
				toast.show();
			}
			//Need to save draft since send failed.
			else{
				saveDraft(subject, body, sender, recipients, cc, bcc, context);
				Toast toast = Toast.makeText(context,
						"Send failed, one or more supplied adresses contain illegal characters " +
								"(email has been saved as draft).", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
				toast.show();      
			}
		}
	}
	/**
	 * Attempts to parse out separate addresses from one string. Caues exception if string contains
	 * illegal characters.
	 */
	private void addRecipients(Message msg, String adresses, 
			Message.RecipientType type) throws Exception{
		if (adresses.indexOf(',') > 0){
			msg.setRecipients(type, InternetAddress.parse(adresses));   
		}
		else{  
			msg.setRecipient(type, new InternetAddress(adresses));   
		}
	}
	/**
	 * Creates a attachable bodypart from a filename.
	 */
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
	/**
	 * Starts the AsyncTask for verifying account. Reference to activity is needed in order to display/dismiss 
	 * loading dialog.
	 */
	public void validate(AddAccountActivity a) {	
		try {
			ConnectTest c = new ConnectTest(user,password, a);
			c.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} 
		catch (Exception e) {
		}
	}
	/**
	 * Testing variant that provides a return value so that result can be asserted.
	 */
	public boolean validateTest() {	
		try {
			TestConnectTest c = new TestConnectTest(user,password);
			c.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			return(c.get());
		} 
		catch (Exception e) {
			return false;
		}
	}
	/**
	 * AsyncTask for trying to connect with the provided account credentials.
	 */
	private class ConnectTest extends AsyncTask<Void, Void, Void>{

		private String user, password;
		private ProgressDialog dialog;
		private AddAccountActivity activity;
		private boolean sucess;

		private ConnectTest(String u, String p, AddAccountActivity a){
			user = u;
			password = p; 
			activity = a;
			dialog = new ProgressDialog(a);
		}
		@Override
		protected void onPreExecute() {
			dialog.setMessage("Validating Account...");
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.show();
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				Transport t = session.getTransport(sendProtocol);
				t.connect(user, password);
				t.close();
				sucess = true;
			} 
			catch (Exception e) {
				e.printStackTrace();
				sucess = false;
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void v){
			if(sucess){
				//Account remembered even if app is force stopped.
				Encryption encryption = new Encryption();
				String key = "Some Key";
				String encrypted = encryption.encrypt(key, password);
				activity.accEdit.putString(user, encrypted);
				activity.accEdit.commit();
				Intent i = new Intent("TIG055st2014.mailmaster.AccountSettingsActivity");
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				activity.getApplicationContext().startActivity(i);
			}
			else{
				//Invalid email account (will also trigger if no internet connection).
				Toast toast = Toast.makeText(activity.getApplicationContext(),
						"Wrong password or inexistant account.", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
				toast.show();
			}
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	}
	/**
	 * Testing variant that returns the value of success.
	 */
	private class TestConnectTest extends AsyncTask<Void, Void, Boolean>{

		private String user, password;

		private TestConnectTest(String u, String p){
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
	/**
	 * Starts the AsyncTask for getting the contents of the current folder. Reference to activity is 
	 * needed in order to display/dismiss loading dialog.
	 */
	public void getFolder(MailFolderActivity a) {	
		try {
			ReadTask task = new ReadTask(user,password, a);
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			Log.d("MailFunctionality",  "Reading reached");
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Testing variant that is used both to assert in tests + to provide the EmailNootificationService
	 * with emails.
	 */
	public ArrayList<Message> getFolderTest() {	
		try {
			TestReadTask task = new TestReadTask(user,password);
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			Log.d("MailFunctionality",  "Reading reached");
			return task.get();
		} 
		catch (Exception e) {
			return new ArrayList<Message>();
		}
	}
	/**
	 * AsyncTask for retrieving emails from a folder via IMAP.
	 * Partly based on http://www.compiletimeerror.com/2013/06/reading-email-using-javamail-api-example.html
	 */
	private class ReadTask extends AsyncTask<Void, Void, Void>{

		private String user, password;
		private ProgressDialog dialog;
		private ArrayList<Message> emails;
		MailFolderActivity activity;

		private ReadTask(String u, String p, MailFolderActivity a){
			user = u;
			password = p; 
			activity = a;
			emails = new ArrayList<Message>();
			dialog = new ProgressDialog(a);
		}
		@Override
		protected void onPreExecute() {
			AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
			if(apv.getFolderName(user).contains("Drafts")){
				dialog.setMessage("Fetching Drafts...");
			}
			else if(apv.getFolderName(user).contains("Sent")){
				dialog.setMessage("Fetching Sent Emails...");
			}
			else{
				dialog.setMessage("Fetching Inbox...");
			}
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.show();
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			emails.addAll(getMailList(user, password));
			sort();
			return null;
		}
		private void sort(){
			ArrayList<Message> temp = new ArrayList<Message>();
			if(activity.emails.size() == 0){
				activity.emails = emails;
			}
			else{
				try{
					int limit = 20;
					int limit1 = activity.emails.size();
					int limit2 = emails.size();
					int i = 0;
					int j = 0;
					while(temp.size() < limit && i < limit1 && j < limit2){
						if(activity.emails.get(i).getReceivedDate().after(emails.get(j).getReceivedDate())){
							temp.add(activity.emails.get(i));
							i++;
						}
						else{
							temp.add(emails.get(j));
							j++;
						}
					}
					while(temp.size() < limit && limit1 > i){
						temp.add(activity.emails.get(i));
						i++;
					}
					while(temp.size() < limit && limit2 > j){
						temp.add(emails.get(j));
						j++;
					}
				}

				catch(Exception e){
					e.printStackTrace();
				}
				activity.emails = temp;
			}
		}
		@Override
		protected void onPostExecute(Void v){
			Log.d("async size", activity.emails.size() + "");
			activity.listView.setAdapter(new EmailAdapter(activity.getApplicationContext(),R.layout.email_item,
					R.id.email_preview, activity.emails));
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	}
	/**
	 * Testing variant that returns the list of emails.
	 * Partly based on http://www.compiletimeerror.com/2013/06/reading-email-using-javamail-api-example.html
	 */
	private class TestReadTask extends AsyncTask<Void, Void, ArrayList<Message>>{

		private String user, password;

		private TestReadTask(String u, String p){
			user = u;
			password = p; 
		}   	
		@Override
		protected ArrayList<Message> doInBackground(Void... arg0) {
			return getMailList(user, password);
		}
	}
	/**
	 * Helper method to reduce code duplication in normal/testing variant of ReadTask.
	 * Should not be called manually.
	 */
	public ArrayList<Message> getMailList(String user, String password){		
		ArrayList<Message> emails = new ArrayList<Message>();
		try {
			AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
			if(apv.getStore(user)!=null && apv.getStore(user).isConnected()){
				apv.getStore(user).close();
			}
			Store store = session.getStore("imaps");
			store.connect(imapHost, user, password);
			apv.setStore(user, store);
			if(apv.getEmailFolder(user)!=null && apv.getEmailFolder(user).isOpen()){
				apv.getEmailFolder(user).close(false);
			}
			Folder foldr = store.getFolder(apv.getFolderName(user));
			apv.setEmailFolder(user, foldr);
			foldr.open(Folder.READ_WRITE);
			int limit = 19;
			int count = foldr.getMessageCount();
			//Only getting latest 20 items for performance reasons.
			if(count < 20){
				limit = count-1;
			} 
			Message[] temp = foldr.getMessages(count-limit, count);
			//Fetch code based on http://codereview.stackexchange.com/questions/36878/is-there-any-way-to-make-this-javamail-code-faster
			//Noticeable improvement compared to looping through each message.
			FetchProfile profile = new FetchProfile();
			profile.add(FetchProfile.Item.CONTENT_INFO);
			profile.add(FetchProfile.Item.ENVELOPE);
			profile.add(FetchProfile.Item.FLAGS);
			foldr.fetch(temp, profile);
			Collections.addAll(emails, temp);
			Collections.reverse(emails);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return emails;
	}
	/**
	 * Starts the AsyncTask for getting the contents of the current email. Reference to activity is 
	 * needed in order to display/dismiss loading dialog.
	 */
	public void getContents(ShowEmailActivity a) {	
		try {
			ContentsTask ct = new ContentsTask(user,password, a);
			ct.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			Log.d("MailFunctionality",  "Getting contents");
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Alternative that is used when email contents is needed to fill
	 * body field when loading a draft into ComposeActivity.
	 */
	public void getContents(ComposeActivity a) {	
		try {
			ContentsTask ct = new ContentsTask(user,password, a);
			ct.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			Log.d("MailFunctionality",  "Getting contents");
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Testing variant that returns the content for assertion.
	 */
	public String getTestContents() {	
		try {
			TestContentsTask ct = new TestContentsTask(user,password);
			ct.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			Log.d("MailFunctionality",  "Getting contents");
			return ct.get();
		} 
		catch (Exception e) {
			return "";
		}
	}
	/**
	 * AsyncTask for retrieving the plain/html contents of an email, including names of attachments.
	 * Supports nested multiparts via recursion of parse helper method.
	 */
	private class ContentsTask extends AsyncTask<Void, Void, Void>{

		private String user, password, contents;
		private ProgressDialog dialog;
		private ShowEmailActivity seAct;
		private ComposeActivity cmAct;

		private ContentsTask(String u, String p, ShowEmailActivity a){
			user = u;
			password = p; 
			dialog = new ProgressDialog(a);
			seAct = a;
		}
		private ContentsTask(String u, String p, ComposeActivity a){
			user = u;
			password = p; 
			dialog = new ProgressDialog(a);
			cmAct = a;
		}
		@Override
		protected void onPreExecute() {
			contents = "";
			dialog.setMessage("Reading Email Contents...");    	
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.show();
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			contents = getBody(user, password);
			return null;
		}
		@Override
		protected void onPostExecute(Void v){
			Log.d("Contents", contents);
			if(seAct != null){
				seAct.load(contents);
			}
			else{
				EditText body = ((EditText) cmAct.findViewById(R.id.body));
				body.setText(contents);
			}
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	}
	/**
	 * Testing variant that returns the email content for assertion.
	 */
	private class TestContentsTask extends AsyncTask<Void, Void, String>{

		private String user, password;

		private TestContentsTask(String u, String p){
			user = u;
			password = p; 
		}

		@Override
		protected String doInBackground(Void... arg0) {
			return getBody(user, password);
		}
	}
	/**
	 * Helper method to reduce code duplication in normal/testing variant of ContentsTask.
	 * Should not be called manually.
	 */
	public String getBody(String user, String password){
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
		apv.resetLists();
		plainContents = "";
		htmlContents = "";
		try{
			if(apv.getStore(user)!=null && !apv.getStore(user).isConnected()){
				apv.getStore(user).connect(imapHost, user, password);
			}
			if(apv.getEmailFolder(user)!=null && !apv.getEmailFolder(user).isOpen()){
				apv.getEmailFolder(user).open(Folder.READ_WRITE);
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
			return plainContents;
		}
		try {
			if(apv.getEmail().isMimeType("text/*")){
				plainContents = apv.getEmail().getContent().toString();
			}
			else{
				parse((MimeMultipart) apv.getEmail().getContent());
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		if(htmlContents.equals("")){
			return plainContents;
		}
		else if(apv.getFolderName(user).contains("Drafts")){
			return Html.fromHtml(htmlContents).toString();
		}
		else{
			return htmlContents;
		}
	}
	/**
	 * Helper method to reduce code duplication in normal/testing variant of ContentsTask.
	 * Should not be called manually.
	 */
	public void parse(MimeMultipart _mp){
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
		try {
			for(int i = 0; i < _mp.getCount(); i++){
				BodyPart bp = _mp.getBodyPart(i);
				if(bp.isMimeType("text/html")){
					htmlContents = bp.getContent().toString();

				}
				else if(bp.isMimeType("text/*")){
					plainContents = bp.getContent().toString();

				}
				else if(bp.isMimeType("multipart/*")){
					parse((MimeMultipart)bp.getContent());

				}
				else{
					try{
						Log.d("type", bp.getContentType() );
						DataSource file = bp.getDataHandler().getDataSource();
						apv.addFile(file);
						apv.addAttachment(bp.getDataHandler().getName());

					}
					catch(Exception exe){
						exe.printStackTrace();
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * Starts the AsyncTask for constructing a reply to the current email. Reference to activity is 
	 * needed in order to display/dismiss loading dialog.
	 */
	public void getReply(Message m, ShowEmailActivity a) {	
		try {
			ReplyTask rt = new ReplyTask(user,password, a);
			rt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, m);
			Log.d("MailFunctionality",  "Getting default fields for reply");
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Testing variant that returns the generated reply for assertion.
	 */
	public Message getTestReply(Message m) {	
		try {
			TestReplyTask rt = new TestReplyTask(user,password);
			rt.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, m);
			Log.d("MailFunctionality",  "Getting default fields for reply");
			return rt.get();
		} 
		catch (Exception e) {
			return null;
		}
	}
	/**
	 * AsyncTask for constructing reply based on existing message.
	 */
	private class ReplyTask extends AsyncTask<Message, Void, Void>{

		private String user, password;
		private ProgressDialog dialog;
		private ShowEmailActivity activity;

		private ReplyTask(String u, String p, ShowEmailActivity a){
			user = u;
			password = p; 
			dialog = new ProgressDialog(a);
			activity = a;
		}
		@Override
		protected void onPreExecute() {
			dialog.setMessage("Constructing Reply...");    	
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.show();
		}
		@Override
		protected Void doInBackground(Message... m) {
			try {
				AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
				if(apv.getStore(user)!=null && !apv.getStore(user).isConnected()){
					apv.getStore(user).connect(imapHost, user, password);
				}
				if(apv.getEmailFolder(user)!=null && !apv.getEmailFolder(user).isOpen()){
					apv.getEmailFolder(user).open(Folder.READ_WRITE);
				}
				//reply(true) = reply all
				apv.setReply(m[0].reply(true));
				apv.setIsReply(true);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void v){
			Intent i = new Intent("TIG055st2014.mailmaster.ComposeActivity");
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			activity.startActivity(i);
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	}
	/**
	 * Testing variant that returns the generated reply for assertion.
	 */
	private class TestReplyTask extends AsyncTask<Message, Void, Message>{

		private String user, password;

		private TestReplyTask(String u, String p){
			user = u;
			password = p; 
		}
		@Override
		protected Message doInBackground(Message... m) {
			try {
				AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
				if(apv.getStore(user)!=null && !apv.getStore(user).isConnected()){
					apv.getStore(user).connect(imapHost, user, password);
				}
				if(apv.getEmailFolder(user)!=null && !apv.getEmailFolder(user).isOpen()){
					apv.getEmailFolder(user).open(Folder.READ_WRITE);
				}
				//reply(true) = reply all
				return m[0].reply(true);
			}
			catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}   	
	}
	/**
	 * Starts the AsyncTask for saving a draft.
	 */
	public void saveDraft(String subject, String body, String sender, String recipients, 
			String cc, String bcc, Context context, ComposeActivity a) {   
		try{
			DraftTask task = new DraftTask(user, password, subject,body,sender,recipients, cc, bcc, context, a);
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Variant used for auto draft save on failed email send.
	 */
	public void saveDraft(String subject, String body, String sender, String recipients, 
			String cc, String bcc, Context context) {   
		try{
			DraftTask task = new DraftTask(user, password, subject,body,sender,recipients, cc, bcc, context);
			task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	private class DraftTask extends AsyncTask<Void, Void, Void>{

		private String user, password;
		private String subject, body, sender , recipients, cc, bcc;
		private boolean saved;
		private Context context;
		private ProgressDialog dialog;

		private DraftTask(String u, String p, String sb, String bd, String sd, String rcp, 
				String _cc, String _bcc, Context c, ComposeActivity a){
			subject = sb;
			body = bd;
			sender = sd;
			recipients = rcp;
			cc = _cc;
			bcc = _bcc;
			context = c;
			saved = false;
			user = u;
			password = p; 
			dialog = new ProgressDialog(a);
		}
		private DraftTask(String u, String p, String sb, String bd, String sd, String rcp, 
				String _cc, String _bcc, Context c){
			subject = sb;
			body = bd;
			sender = sd;
			recipients = rcp;
			cc = _cc;
			bcc = _bcc;
			context = c;
			saved = false;
			user = u;
			password = p; 
		}
		@Override
		protected void onPreExecute() {
			if(dialog != null){
				dialog.setMessage("Saving Draft...");    	
				dialog.setIndeterminate(true);
				dialog.setCancelable(false);
				dialog.show();
			}
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				Folder drafts;
				Store store = session.getStore("imaps");
				store.connect(imapHost, user, password);
				if(!type.equalsIgnoreCase("gmail.com") && !type.equalsIgnoreCase("student.gu.se")){
					drafts = store.getFolder(context.getResources().getString(R.string.msDrafts));
				}
				else{
					drafts = store.getFolder(context.getResources().getString(R.string.gmailDrafts));
				}
				drafts.open(Folder.READ_WRITE);
				MimeMessage message = new MimeMessage(session); 
				message.setFrom(new InternetAddress(sender));
				DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/html;charset=UTF-8"));  
				message.setDataHandler(handler);
				message.setSubject(subject, "utf-8");   
				if(!recipients.equals("")){
					addRecipients(message, recipients, Message.RecipientType.TO);
				}
				if(!cc.equals("")){
					addRecipients(message, cc, Message.RecipientType.CC);
				}
				if(!bcc.equals("")){
					addRecipients(message, bcc, Message.RecipientType.BCC);
				};
				message.setContent(body, "text/html;charset=UTF-8");
				message.setFlag(Flag.DRAFT, true);
				drafts.appendMessages(new Message[]{message});
				saved = true;
				drafts.close(false);
				store.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void v){
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}
			if(saved){
				Toast toast = Toast.makeText(context,
						"Draft Saved.", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
				toast.show();
			}
			else{
				Toast toast = Toast.makeText(context,
						"Failed to save draft.", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
				toast.show();
			}
		}
	}
	private class SaveAsSentTask extends AsyncTask<Void, Void, Void>{

		private Message message;
		private Context context;

		private SaveAsSentTask(Message m, Context c){
			message = m;
			context = c;
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				
				Store store = session.getStore("imaps");
				store.connect(imapHost, user, password);
				Folder sent = store.getFolder(context.getResources().getString(R.string.gmailSent));
				sent.open(Folder.READ_WRITE);
				sent.appendMessages(new Message[]{message});
				sent.close(false);
				store.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}
}

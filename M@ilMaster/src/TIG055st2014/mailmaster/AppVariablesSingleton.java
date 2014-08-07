package TIG055st2014.mailmaster;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.activation.DataSource;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;

/**
 * Singleton class used to store and retrieve data throughout the application.
 * Simpler but less persistent than a database solution, probably causes more
 * overhead but also avoids expensive database operations.
 */
public class AppVariablesSingleton {
	private static AppVariablesSingleton current;
	private Message email;
	private Message reply;
	/**
	 * Used to check if folder has to be reopened, or if old folder exists
	 * that should be closed.
	 */
	private HashMap<String, Folder> emailFolder;
	/**
	 * Used to check if store session has to be reopened, or if old session exists
	 * that should be closed.
	 */
	private HashMap<String, Store> store;
	private boolean isReply;
	private ArrayList<String> attachments;
	private ArrayList<DataSource> files; 
	public HashMap<String, String> folderName;
	String currentAcc;

	private AppVariablesSingleton(){
		resetLists();
		currentAcc = "";
	}

	public void setEmail(Message m){
		this.email = m;
	}
	/**
	 * Used to reset lists when going back and forth between message/attachment
	 * view on the same email message.
	 */
	public void resetLists(){
		attachments = new ArrayList<String>();
		files = new ArrayList<DataSource>();
	}
	public Message getEmail(){
		return this.email;
	}
	public void setReply(Message m){
		this.reply = m;
	}
	public Message getReply(){
		return this.reply;
	}
	public void setEmailFolder(String account, Folder f){
		this.emailFolder.put(account, f);
	}
	public Folder getEmailFolder(String account){
		if(this.emailFolder != null && this.emailFolder.containsKey(account)){
			return this.emailFolder.get(account);
		}
		else{
			return null;
		}
	}
	public void setStore(String account, Store s){
		this.store.put(account, s);
	}
	public Store getStore(String account){
		if(this.store != null && this.store.containsKey(account)){
			return this.store.get(account);
		}
		else{
			return null;
		}	
	}
	public void setIsReply(boolean b){
		this.isReply = b;
	}
	public boolean getIsReply(){
		return this.isReply;
	}
	public void setFolderName(String account, String name){		
		this.folderName.put(account, name);
	}
	public String getFolderName(String account){
		if(this.folderName != null && this.folderName.containsKey(account)){
		return this.folderName.get(account);
		}
		else{
			return "INBOX";
		}
	}
	public String getFolderNames(){
		String name = "INBOX";
		if(this.folderName != null && this.folderName.size() > 0){
			Set<String> temp = folderName.keySet();
			for(String s : temp){
				name = folderName.get(s);
				break;
			}
		}
		return name;
	}
	public void addAttachment(String name){
		this.attachments.add(name);
	}
	public void addFile(DataSource file){
		this.files.add(file);
	}
	public ArrayList<DataSource> getFiles(){
		return this.files;
	}
	public ArrayList<String> getAttachments(){
		return this.attachments;
	}
	public void setAccount(String account){
		this.currentAcc = account;
	}
	public String getAccount(){
		return this.currentAcc;
	}
	/**
	 * Makes sure the same instance of AppVariables is used throughout
	 * the app.
	 */
	public static AppVariablesSingleton getInstance(){
		if(current == null){
			current = new AppVariablesSingleton();
		}
		return current;
	}
	public void setAllFolders(String name){
		Set<String> temp = folderName.keySet();
		for(String s : temp){
			folderName.put(s, name);
		}
	}
	public void initAccounts(){
		this.folderName = new HashMap<String, String>();
		this.emailFolder = new HashMap<String, Folder>();
		this.store = new HashMap<String, Store>();
	}
}

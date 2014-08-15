package TIG055st2014.mailmaster.HelpClasses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.activation.DataSource;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;

/* M@ilMaster Multi-Account Email Client
Copyright (C) 2014 Eric Britsman & Khaled Alnawasreh

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License 
Version 2 only; as published by the Free Software Foundation.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

Contact Info: eric_britsman@hotmail.com / khaled.nawasreh@gmail.com
*/

/**
 * Singleton class used to store and retrieve data throughout the application.
 * Simpler but less persistent than a database solution, probably causes more
 * overhead but also avoids expensive database operations?
 */
public class AppVariablesSingleton {
	private static AppVariablesSingleton current;
	private Message email;
	private Message reply;
	/**
	 * Used to check if folder has to be reopened, or if old folder exists
	 * that should be closed.
	 */
	private HashMap<String, Folder> emailFolders;
	/**
	 * Used to check if store session has to be reopened, or if old session exists
	 * that should be closed.
	 */
	private HashMap<String, Store> stores;
	private boolean isReply;
	private ArrayList<String> attachmentNames;
	private ArrayList<DataSource> attachmentSources; 
	public HashMap<String, String> folderNames;
	private String currentAcc;
	private boolean testing;

	private AppVariablesSingleton(){
		resetLists();
		currentAcc = "";
		testing = false;
	}
	/**
	 * Used to reset lists when going back and forth between message/attachment
	 * view on the same email message.
	 */
	public void resetLists(){
		attachmentNames = new ArrayList<String>();
		attachmentSources = new ArrayList<DataSource>();
	}
	public void setEmail(Message m){
		this.email = m;
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
		this.emailFolders.put(account, f);
	}
	public Folder getEmailFolder(String account){
		if(this.emailFolders != null && this.emailFolders.containsKey(account)){
			return this.emailFolders.get(account);
		}
		else{
			return null;
		}
	}
	public void setStore(String account, Store s){
		this.stores.put(account, s);
	}
	public Store getStore(String account){
		if(this.stores != null && this.stores.containsKey(account)){
			return this.stores.get(account);
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
		this.folderNames.put(account, name);
	}
	public String getFolderName(String account){
		if(this.folderNames != null && this.folderNames.containsKey(account)){
			return this.folderNames.get(account);
		}
		else{
			return "INBOX";
		}
	}
	/**
	 * Used to get foldername without having/knowing accounts.
	 */
	public String getFolderNames(){
		String name = "INBOX";
		if(this.folderNames != null && this.folderNames.size() > 0){
			Set<String> temp = folderNames.keySet();
			for(String s : temp){
				name = folderNames.get(s);
				break;
			}
		}
		return name;
	}
	public void addAttachment(String name){
		this.attachmentNames.add(name);
	}
	public ArrayList<String> getFileNames(){
		return this.attachmentNames;
	}
	public void addFile(DataSource file){
		this.attachmentSources.add(file);
	}
	public ArrayList<DataSource> getFiles(){
		return this.attachmentSources;
	}
	public void setAccount(String account){
		this.currentAcc = account;
	}
	public String getAccount(){
		return this.currentAcc;
	}
	public void setTesting(boolean b){
		this.testing = b;
	}
	public boolean isTesting(){
		return this.testing;
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
		Set<String> temp = folderNames.keySet();
		for(String s : temp){
			folderNames.put(s, name);
		}
	}
	public void initAccounts(){
		this.folderNames = new HashMap<String, String>();
		this.emailFolders = new HashMap<String, Folder>();
		this.stores = new HashMap<String, Store>();
	}
}

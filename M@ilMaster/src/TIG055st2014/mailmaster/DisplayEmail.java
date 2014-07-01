package TIG055st2014.mailmaster;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;

public class DisplayEmail {
	private static DisplayEmail current;
	private Message email;
	private Message reply;
	private Folder emailFolder;
	private Store store;
	
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
	public void setEmailFolder(Folder f){
		this.emailFolder = f;
	}
	public Folder getEmailFolder(){
		return this.emailFolder;
	}
	public void setStore(Store s){
		this.store = s;
	}
	public Store getStore(){
		return this.store;
	}
	public static DisplayEmail getInstance(){
		if(current == null){
			current = new DisplayEmail();
		}
		return current;
	}
}

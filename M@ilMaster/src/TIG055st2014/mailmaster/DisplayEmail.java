package TIG055st2014.mailmaster;

import java.util.ArrayList;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;

public class DisplayEmail {
	private static DisplayEmail current;
	private Message email;
	private Message reply;
	private Folder emailFolder;
	private Store store;
	private boolean isReply;
	private ArrayList<String> attachments;
	
	private DisplayEmail(){
		attachments = new ArrayList<String>();
	}
	
	public void setEmail(Message m){
		attachments = new ArrayList<String>();
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
	public void setIsReply(boolean b){
		this.isReply = b;
	}
	public boolean getIsReply(){
		return this.isReply;
	}
	public void addAttachment(String name){
		this.attachments.add(name);
	}
	public ArrayList<String> getAttachments(){
		return this.attachments;
	}
	public static DisplayEmail getInstance(){
		if(current == null){
			current = new DisplayEmail();
		}
		return current;
	}
}

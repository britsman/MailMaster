package TIG055st2014.mailmaster.Services;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.mail.Flags.Flag;
import javax.mail.Message;

import TIG055st2014.mailmaster.R;
import TIG055st2014.mailmaster.Activities.MailFolderActivity;
import TIG055st2014.mailmaster.HelpClasses.AppVariablesSingleton;
import TIG055st2014.mailmaster.HelpClasses.EmailNotificationForwarder;
import TIG055st2014.mailmaster.HelpClasses.EmailNotificationVariables;
import TIG055st2014.mailmaster.HelpClasses.Encryption;
import TIG055st2014.mailmaster.HelpClasses.MailFunctionality;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

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
 * Background service used to both check for new emails to notify about + to automatically
 * update the list of emails in MailFolder on a timed interval.
 */
public class EmailNotificationService extends Service{
	//partially based on http://stackoverflow.com/questions/11390018/how-to-cal-the-activity-method-from-the-service
	private Thread thread;
	private boolean running = false;
	private SharedPreferences accounts;
	private final int emailId = 1;
	private ArrayList<Message> emails;
	private Set<String> activeAccs;
	public SharedPreferences pageNumbers;
	public SharedPreferences.Editor numEdit;

	/**
	 * Interface used to access MailFolderActivity function inside of the service.
	 */
	public interface EmailNotificationServiceClient {
		void autoUpdate(ArrayList<Message> m);
	}

	/**
	 * Reference to the activity that starts the service.
	 */
	private EmailNotificationServiceClient mClient;

	/**
	 * Used to assign the service's activity reference from inside the activity.
	 */
	public void setServiceClient(EmailNotificationServiceClient client) {
		if(client == null) {
			mClient = null;
			return;
		}

		mClient = client;
	}
	/**
	 * Used to find this service from inside MailFolderActivity.
	 */
	public class EmailNotificationBinder extends Binder {
		public EmailNotificationService getService() {
			return EmailNotificationService.this;
		}
	}

	private IBinder mBinder = new EmailNotificationBinder();

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Toast.makeText(this,getApplicationContext().getResources()
				.getString(R.string.toast_service_creat), Toast.LENGTH_LONG).show();
		
		pageNumbers = getSharedPreferences("pages", MODE_PRIVATE);
		numEdit = pageNumbers.edit();

		thread = new Thread(){
			@Override
			public void run() {
				NotificationManager notifyManager =
						(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

				NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
				//Used for action that is triggered when notification is pressed/deleted.
				Intent emailIntent = new Intent(getApplicationContext(), EmailNotificationForwarder.class);
				accounts = getSharedPreferences("StoredAccounts", MODE_PRIVATE);
				activeAccs = new HashSet<String>();
				activeAccs.addAll(accounts.getStringSet("default", new HashSet<String>()));

				while(running){
					initVariables();
					getLatest();
					for(Message m : emails){
						try{
							if(!m.getFlags().contains(Flag.SEEN)){
								EmailNotificationVariables.nrUnreadEmail++;
							}
						}
						catch(Exception e){
							e.printStackTrace();
						}

					}
					//check if number of unseen emails are higher than 0
					if(EmailNotificationVariables.nrUnreadEmail > 0){
						String text;
						if(EmailNotificationVariables.nrUnreadEmail == 20){
							text = "You have 20+ new emails.";
						}
						else{
							text = "You have " + EmailNotificationVariables.nrUnreadEmail + " new emails.";
						}
						Notification emailNoti = builder
								.setSmallIcon(R.drawable.ic_action_new_email)
								.setContentTitle("Unread Messages!")
								.setContentText(text)
								.setAutoCancel(true)
								.setContentIntent(PendingIntent.getActivity(EmailNotificationService.this, 0, emailIntent, 0))
								.setDeleteIntent(PendingIntent.getActivity(EmailNotificationService.this, 0, emailIntent, 0))
								.build();

						notifyManager.notify(emailId, emailNoti);
					}

					try {
						Log.d("in service", "" + (mClient != null));
						if(mClient != null){
							Log.d("autoupdate", "in service");
							mClient.autoUpdate(emails);
							/*Wait for 45 seconds (approx time for rest of loop iteration is 15 sec,
                    	  So total time for each iteration is close to 1 minute*/
							synchronized (this) {
								this.wait(45000);
							}
						}
						else{
							running = false;
						}
					} 
					catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		thread.setPriority(Thread.MIN_PRIORITY);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		running = false;
		Toast.makeText(this,getApplicationContext().getResources()
				.getString(R.string.toast_service_dest), Toast.LENGTH_LONG).show();
		NotificationManager notifyManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		notifyManager.cancel(emailId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this,getApplicationContext().getResources()
				.getString(R.string.toast_service_start), Toast.LENGTH_LONG).show();
		running = true;
		thread.start();
		return super.onStartCommand(intent, flags, startId);
	}
	private void initVariables(){
		EmailNotificationVariables.nrUnreadEmail = 0;
		emails = new ArrayList<Message>();
	}
	private void getLatest(){
		String key = "Some Key";
		Encryption decrypter = new Encryption();

		for(String s : activeAccs){		
			String pw = decrypter.decrypt(key, accounts.getString(s, ""));
			MailFunctionality mf = new MailFunctionality(s, pw, (s.split("@"))[1]);
			sort(mf.getFolderTest(pageNumbers.getInt("current", 1)));
		}
	}
	private void sort(ArrayList<Message> list){
		ArrayList<Message> temp = new ArrayList<Message>();
		if(emails.size() == 0){
			emails = list;
		}
		else{
			try{
				int limit = 20;
				int limit1 = emails.size();
				int limit2 = list.size();
				int i = 0;
				int j = 0;
				limit = list.size();
				while(temp.size() < limit && i < limit1 && j < limit2){
					if(emails.get(i).getReceivedDate().after(list.get(j).getReceivedDate())){
						temp.add(emails.get(i));
						i++;
					}
					else{
						temp.add(list.get(j));
						j++;
					}
				}
				while(temp.size() < limit && limit1 > i){
					temp.add(emails.get(i));
					i++;
				}
				while(temp.size() < 20 && limit2 > j){
					temp.add(list.get(j));
					j++;
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			emails = temp;
		}
	}
	/**
	 * Used when manually refreshing inbox, will cancel the 45 second
	 * wait if it is progress.
	 */
	public void restart(){
		synchronized (thread) {
			thread.notify();
		}
	}
}
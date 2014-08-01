package TIG055st2014.mailmaster;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import javax.mail.Flags.Flag;
import javax.mail.Message;

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
	private String defaultAcc, pw;

	/**
	 * Interface used to access MailFolderActivity function inside of the service.
	 */
	public interface EmailNotificationServiceClient {
		void autoUpdate(ArrayList<Message> m);
	}

	/**
	 * Reference to the activity that starts the service.
	 */
	private WeakReference<EmailNotificationServiceClient> mClient;

	/**
	 * Used to assign the service's activity reference from inside the activity.
	 */
	public void setServiceClient(EmailNotificationServiceClient client) {
		if(client == null) {
			mClient = null;
			return;
		}

		mClient = new WeakReference<EmailNotificationServiceClient>(client);
	}
	/**
	 * Used to find this service from inside MailFolderActivity.
	 */
	public class EmailNotificationBinder extends Binder {
		EmailNotificationService getService() {
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
		Toast.makeText(this,"Service created", Toast.LENGTH_LONG).show();

		thread = new Thread(){
			@Override
			public void run() {
				NotificationManager NotifyManager =
						(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

				NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
				//Used for action that is triggered when notification is pressed/deleted.
				Intent emailIntent = new Intent(getApplicationContext(), EmailForwarder.class);
				accounts = getSharedPreferences("StoredAccounts", MODE_PRIVATE);
				defaultAcc = accounts.getString("default", "");
				String key = "Some Key";
				Encryption encryption = new Encryption();
				pw = encryption.decrypt(key, (accounts.getString(defaultAcc, "")));
				MailFunctionality mf = new MailFunctionality(defaultAcc, pw, (defaultAcc.split("@"))[1]);

				while(running){
					initVariables();
					ArrayList<Message> emails = mf.getFolderTest();
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

						NotifyManager.notify(emailId, emailNoti);
					}

					try {
						Log.d("in service", "" + (mClient != null));
						if(mClient != null){
							Log.d("autoupdate", "in service");
							((MailFolderActivity)mClient.get()).autoUpdate(emails);
						}
						/*Sleep for 45 seconds (approx time for rest of loop iteration is 15 sec,
                    	  So total time for each iteration is close to 1 minute*/
						sleep(45000);
					} catch (InterruptedException e) {
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
		Toast.makeText(this,"Service destroyed", Toast.LENGTH_LONG).show();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Toast.makeText(this,"Service started", Toast.LENGTH_LONG).show();
		running = true;
		thread.start();
		return super.onStartCommand(intent, flags, startId);
	}
	private void initVariables(){
		EmailNotificationVariables.nrUnreadEmail = 0;
	}
}
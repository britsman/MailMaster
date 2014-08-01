package TIG055st2014.mailmaster;

import java.util.ArrayList;

import javax.mail.Message;

import TIG055st2014.mailmaster.EmailNotificationService.EmailNotificationBinder;
import TIG055st2014.mailmaster.EmailNotificationService.EmailNotificationServiceClient;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class InboxActivity extends Activity implements AdapterView.OnItemClickListener, EmailNotificationServiceClient{
	//partially based on http://stackoverflow.com/questions/11390018/how-to-cal-the-activity-method-from-the-service
    public SharedPreferences accounts;
    private String defaultAcc;
    private String pw;
    protected ListView listView;
    protected ArrayList<Message> emails;
    private EmailNotificationServiceConnection mServiceConnection = new EmailNotificationServiceConnection();
    private EmailNotificationService mService = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
    	getActionBar().setDisplayShowHomeEnabled(false);
        accounts = getSharedPreferences("StoredAccounts", MODE_PRIVATE);
        defaultAcc = accounts.getString("default", "");
        String key = "Some Key";
        Encryption encryption = new Encryption();
        pw = encryption.decrypt(key, (accounts.getString(defaultAcc, "")));
    	DisplayEmail d = DisplayEmail.getInstance();
    	if(d.getFolderName() == null){//Default to inbox if no other folder has been selected.
    		d.setFolderName("INBOX");
    	}
        if(defaultAcc.equals("")){
        	startActivity(new Intent("TIG055st2014.mailmaster.AddAccountActivity"));
        }
        else{
    		MailFunctionality mf = new MailFunctionality(defaultAcc, pw, (defaultAcc.split("@"))[1]);
    		listView = (ListView) findViewById(R.id.inbox_list);
    		listView.setClickable(true);
    		listView.setOnItemClickListener(this);
    		mf.getInbox(this);
        	if(d.getFolderName().equals("INBOX") && !isServiceRunning()){
        		Intent i = new Intent(this, EmailNotificationService.class);
        		startService(i);
        		bindService(i, mServiceConnection, 0);
        	}
        }
    }
    @Override
    public void onStart(){
    	super.onStart();
    	DisplayEmail d = DisplayEmail.getInstance();
    	if(d.getFolderName().contains("Drafts")){
    		getActionBar().setTitle(R.string.drafts);
    	}
    	else if(d.getFolderName().contains("Sent")){
    		getActionBar().setTitle(R.string.sent);
    	}
    	else{
    		getActionBar().setTitle(R.string.inbox);
    	}
    }
    public void onClickCompose(MenuItem m) {
        if(isServiceRunning()){
        	stopService(new Intent(getApplicationContext(),
        			EmailNotificationService.class));
        	unbindService(mServiceConnection);
        }
    	DisplayEmail d = DisplayEmail.getInstance();
    	d.setIsReply(false);
    	startActivity(new Intent("TIG055st2014.mailmaster.ComposeActivity"));
    }
   
    public void onClickSettings(MenuItem m) {
        if(isServiceRunning()){
        	stopService(new Intent(getApplicationContext(),
        			EmailNotificationService.class));
        	unbindService(mServiceConnection);
        }
    	startActivity(new Intent("TIG055st2014.mailmaster.AccountSettingsActivity"));
    }
    /**
    * We disable the back button while the user is on the add account screen, in order to prevent
    * certain possible issues.
    */
    @Override
    public void onBackPressed() {
    	//Do nothing.
    }
	@Override
	public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
        if(isServiceRunning()){
        	stopService(new Intent(getApplicationContext(),
        			EmailNotificationService.class));
        	unbindService(mServiceConnection);
        }
		DisplayEmail d = DisplayEmail.getInstance();
		d.setEmail(emails.get(position));
		if(d.getFolderName().contains("Drafts")){
			d.setIsReply(false);
			startActivity(new Intent("TIG055st2014.mailmaster.ComposeActivity"));
		}
		else{
			startActivity(new Intent("TIG055st2014.mailmaster.ShowEmailActivity"));
		}
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inbox, menu);
        return true;
    }
    public void changeFolder(MenuItem m){
        int id = m.getItemId();
        DisplayEmail d = DisplayEmail.getInstance();
        if (id == R.id.action_inbox) {
        	d.setFolderName("INBOX");
        	getActionBar().setTitle(R.string.inbox);   
        	if(!isServiceRunning()){
        		Intent i = new Intent(this, EmailNotificationService.class);
        		startService(i);
        		bindService(i, mServiceConnection, 0);
        	}
        }
        else if (id == R.id.action_sent) {
            if(isServiceRunning()){
            	stopService(new Intent(getApplicationContext(),
            			EmailNotificationService.class));
            	unbindService(mServiceConnection);
            }
        	d.setFolderName("[Gmail]/Sent Mail");
        	getActionBar().setTitle(R.string.sent);
        }
        else{
            if(isServiceRunning()){
            	stopService(new Intent(getApplicationContext(),
            			EmailNotificationService.class));
            	unbindService(mServiceConnection);
            }
        	d.setFolderName("[Gmail]/Drafts");
        	getActionBar().setTitle(R.string.drafts);
        }
		MailFunctionality mf = new MailFunctionality(defaultAcc, pw, (defaultAcc.split("@"))[1]);
		mf.getInbox(this);
    }
    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (EmailNotificationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
	@Override
	public void autoUpdate(final ArrayList<Message> m) {
		
		runOnUiThread(new Runnable() {

			public void run() {
				Log.d("autoupdate", "in activity");
				emails = m;
				listView.setAdapter(new EmailAdapter(getApplicationContext(),R.layout.email_item,
						R.id.email_preview, emails));
			}
		});
	}
    class EmailNotificationServiceConnection implements ServiceConnection {

        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = ((EmailNotificationBinder)service).getService();

            mService.setServiceClient(InboxActivity.this);
        }

        public void onServiceDisconnected(ComponentName name) {
            mService.setServiceClient(null);
            mService = null;

        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    	stopService(new Intent(getApplicationContext(),
    			EmailNotificationService.class));
        unbindService(mServiceConnection);
    }
}

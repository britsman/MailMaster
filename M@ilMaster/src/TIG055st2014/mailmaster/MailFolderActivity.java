package TIG055st2014.mailmaster;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.mail.Message;

import TIG055st2014.mailmaster.EmailNotificationService.EmailNotificationBinder;
import TIG055st2014.mailmaster.EmailNotificationService.EmailNotificationServiceClient;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
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

/**
 * Activity for displaying the 20 latest items in either inbox, drafts or sent folder.
 */
public class MailFolderActivity extends Activity implements AdapterView.OnItemClickListener, EmailNotificationServiceClient{
	//partially based on http://stackoverflow.com/questions/11390018/how-to-cal-the-activity-method-from-the-service
	public SharedPreferences accounts;
	private Set<String> defAcc;
	private String pw;
	protected ListView listView;
	protected ArrayList<Message> emails;
	private EmailNotificationServiceConnection mServiceConnection = new EmailNotificationServiceConnection();
	private EmailNotificationService mService = null;
	private ProgressDialog dialog;
	public Menu testMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mail_folder);
		getActionBar().setDisplayShowHomeEnabled(false);
		accounts = getSharedPreferences("StoredAccounts", MODE_PRIVATE);
		defAcc = new HashSet<String>();
		defAcc.addAll(accounts.getStringSet("default", new HashSet<String>()));
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
		if(defAcc.size() == 0){//No default accounts added
			startActivity(new Intent("TIG055st2014.mailmaster.AddAccountActivity"));
		}
		else{
			if(apv.folderName == null){
				apv.initAccounts();
				for(String s : defAcc){
					apv.setFolderName(s, apv.getFolderName(s));
				}
			}
			emails = new ArrayList<Message>();
			listView = (ListView) findViewById(R.id.inbox_list);
			listView.setClickable(true);
			listView.setOnItemClickListener(this);
			if(apv.getFolderNames().equals("INBOX") && !isServiceRunning() && !apv.isTesting()){
				dialog = new ProgressDialog(this);
			    //reading from the resource file depending on which language is selected
		        String fetchinbox = (String) getResources().getText(R.string.fetch_inbox);
				dialog.setMessage(fetchinbox);
				dialog.setIndeterminate(true);
				dialog.setCancelable(false);
				dialog.show();
				startBackground();
			}
			else{
				refreshList();
			}
		}
	}
	@Override
	public void onStart(){
		super.onStart();
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
		if(apv.getFolderNames().contains("Drafts")){
			getActionBar().setTitle(R.string.drafts);
		}
		else if(apv.getFolderNames().contains("Sent")){
			getActionBar().setTitle(R.string.sent);
		}
		else{
			getActionBar().setTitle(R.string.inbox);
		}
	}
	/**
	 * Redirects to composing a new email when the compose icon is pressed.
	 */
	public void onClickCompose(MenuItem m) {
		if(isServiceRunning()){
			stopBackground();
		}
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
		apv.setIsReply(false);
		apv.setEmail(null);
		for(String s : defAcc){	
			apv.setAccount(s);
			break;
		}
		startActivity(new Intent("TIG055st2014.mailmaster.ComposeActivity"));
	}

	/**
	 * Redirects to AccountSettings.
	 */
	public void onClickSettings(MenuItem m) {
		if(isServiceRunning()){
			stopBackground();
		}
		startActivity(new Intent("TIG055st2014.mailmaster.AccountSettingsActivity"));
	}
	/**
	 * We disable the back button while the user is on the add account screen, in order to prevent
	 * certain possible issues (going back in history stack can cause display of "outdated" 
	 * information.
	 */
	@Override
	public void onBackPressed() {
		//Do nothing.
	}
	/**
	 * Reacts when item is pressed in the emails list. If in inbox or sent, the contents
	 * of the email is displayed. if in draft, the contents of the email will be loaded 
	 * into compose.
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
		if(isServiceRunning()){
			stopBackground();
		}
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
		apv.setEmail(emails.get(position));
		for(String s : defAcc){
			if(apv.getEmail().getFolder().equals(apv.getEmailFolder(s))){
				apv.setAccount(s);
				break;
			}
		}
		if(getActionBar().getTitle().toString().equals((getResources().getString(R.string.drafts)))){
			apv.setIsReply(false);
			startActivity(new Intent("TIG055st2014.mailmaster.ComposeActivity"));
		}
		else{
			startActivity(new Intent("TIG055st2014.mailmaster.ShowEmailActivity"));
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.mail_folder, menu);
		testMenu = menu;
		return true;
	}
	/**
	 * Called when user wishes to change the current folder.
	 */
	public void changeFolder(MenuItem m){
		int id = m.getItemId();
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
		if (id == R.id.action_inbox) {
			apv.setAllFolders("INBOX");
			getActionBar().setTitle(R.string.inbox);   
			if(!isServiceRunning()){
				startBackground();
			}
		}
		else if (id == R.id.action_sent) {
			if(isServiceRunning()){
				stopBackground();
			}
			apv.setAllFolders("[Gmail]/Sent Mail");
			getActionBar().setTitle(R.string.sent);
		}
		else{
			if(isServiceRunning()){
				stopBackground();
			}
			apv.setAllFolders("[Gmail]/Drafts");
			getActionBar().setTitle(R.string.drafts);
		}
		refreshList();
	}
	/**
	 * Used to check if service needs to be started/stopped.
	 */
	private boolean isServiceRunning() {
		ActivityManager manager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (EmailNotificationService.class.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Called from EmailNotificationService (which is why runOnUIThread is required).
	 * Used to update the displayed email list.
	 */
	@Override
	public void autoUpdate(final ArrayList<Message> m) {

		runOnUiThread(new Runnable() {

			public void run() {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				if(isServiceRunning()){
					Log.d("autoupdate", "in activity");
					emails = m;
					Log.d("autoupdate size", emails.size() + "");
					listView.setAdapter(new EmailAdapter(getApplicationContext(),R.layout.email_item,
							R.id.email_preview, emails));
				}
			}
		});
	}
	/**
	 * Used when binding/unbinding EmailNotificationService.
	 */
	class EmailNotificationServiceConnection implements ServiceConnection {

		public void onServiceConnected(ComponentName name, IBinder service) {
			mService = ((EmailNotificationBinder)service).getService();
			mService.setServiceClient(MailFolderActivity.this);
		}
		public void onServiceDisconnected(ComponentName name) {
			mService.setServiceClient(null);
			mService = null;
		}
	}
	/**
	 * Makes sure service is killed if app is closed by OS rather than user.
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(isServiceRunning()){
			stopBackground();
		}
	}
	/**
	 * Used to start and bind EmailNotificationService.
	 */
	public void startBackground(){
		Intent i = new Intent(this, EmailNotificationService.class);
		startService(i);
		bindService(i, mServiceConnection, 0);
	}
	/**
	 * Used to stop and unbind EmailNotificationService.
	 */
	public void stopBackground(){
		stopService(new Intent(getApplicationContext(),
				EmailNotificationService.class));
		unbindService(mServiceConnection);
	}
	private void refreshList(){
		String key = "Some Key";
		Encryption decrypter = new Encryption();
		emails = new ArrayList<Message>();
		for(String s : defAcc){		
			String pw = decrypter.decrypt(key, accounts.getString(s, ""));
			MailFunctionality mf = new MailFunctionality(s, pw, (s.split("@"))[1]);
			mf.getFolder(this);
		}
	}
}

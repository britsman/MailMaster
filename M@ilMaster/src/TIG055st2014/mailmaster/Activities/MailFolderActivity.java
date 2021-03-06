package TIG055st2014.mailmaster.Activities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javax.mail.Message;

import TIG055st2014.mailmaster.R;
import TIG055st2014.mailmaster.Adapters.EmailAdapter;
import TIG055st2014.mailmaster.HelpClasses.AppVariablesSingleton;
import TIG055st2014.mailmaster.HelpClasses.Encryption;
import TIG055st2014.mailmaster.HelpClasses.MailFunctionality;
import TIG055st2014.mailmaster.Services.EmailNotificationService;
import TIG055st2014.mailmaster.Services.EmailNotificationService.EmailNotificationBinder;
import TIG055st2014.mailmaster.Services.EmailNotificationService.EmailNotificationServiceClient;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
 * Activity for displaying the 20 latest items in either inbox, drafts or sent folder.
 */
public class MailFolderActivity extends Activity implements AdapterView.OnItemClickListener, EmailNotificationServiceClient{
	//partially based on http://stackoverflow.com/questions/11390018/how-to-cal-the-activity-method-from-the-service
	public SharedPreferences accounts;
	public SharedPreferences pageNumbers;
	public SharedPreferences.Editor numEdit;
	private Set<String> activeAccs;
	public ListView listView;
	public ArrayList<Message> emails;
	private EmailNotificationServiceConnection mServiceConnection = new EmailNotificationServiceConnection();
	private EmailNotificationService mService = null;
	private ProgressDialog dialog;
	/** Used to to give tests access to menuitems. **/
	public Menu testMenu;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mail_folder);
		getActionBar().setDisplayShowHomeEnabled(false);
		accounts = getSharedPreferences("StoredAccounts", MODE_PRIVATE);
		pageNumbers = getSharedPreferences("pages", MODE_PRIVATE);
		numEdit = pageNumbers.edit();
		numEdit.putInt("current", 1);
		numEdit.commit();
		activeAccs = new HashSet<String>();
		activeAccs.addAll(accounts.getStringSet("default", new HashSet<String>()));
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
		//Redirect if no active accounts are fund.
		if(activeAccs.size() == 0){
			startActivity(new Intent("TIG055st2014.mailmaster.Activities.AddAccountActivity"));
		}
		else{
			if(apv.folderNames == null){
				apv.initAccounts();
				for(String s : activeAccs){
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
				String fetchinbox = getResources().getString(R.string.fetch_inbox);
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
		if(activeAccs.size() > 0){
			AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
			int page = pageNumbers.getInt("current", 1);
			if(apv.getFolderNames().contains("Drafts")){
				getActionBar().setTitle(getResources().getString(R.string.drafts) 
						+ " " + getResources().getString(R.string.page) + " " + page);
			}
			else if(apv.getFolderNames().contains("Sent")){
				getActionBar().setTitle(getResources().getString(R.string.sent)
						+ " " + getResources().getString(R.string.page) + " " + page);
			}
			else{
				getActionBar().setTitle(getResources().getString(R.string.inbox)
						+ " " + getResources().getString(R.string.page) + " " + page);
				if(!isServiceRunning() && !apv.isTesting()){
					startBackground();
				}
			}
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
		for(String s : activeAccs){	
			apv.setAccount(s);
			break;
		}
		startActivity(new Intent("TIG055st2014.mailmaster.Activities.ComposeActivity"));
	}

	/**
	 * Redirects to AccountSettings.
	 */
	public void onClickSettings(MenuItem m) {
		if(isServiceRunning()){
			stopBackground();
		}
		startActivity(new Intent("TIG055st2014.mailmaster.Activities.AccountSettingsActivity"));
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
		for(String s : activeAccs){
			if(apv.getEmail().getFolder().equals(apv.getEmailFolder(s))){
				apv.setAccount(s);
				break;
			}
		}
		if(getActionBar().getTitle().toString().contains((getResources().getString(R.string.drafts)))){
			apv.setIsReply(false);
			startActivity(new Intent("TIG055st2014.mailmaster.Activities.ComposeActivity"));
		}
		else{
			startActivity(new Intent("TIG055st2014.mailmaster.Activities.ShowEmailActivity"));
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.mail_folder, menu);
		return true;
	}
	/**
	 * Called when user wishes to change the current folder.
	 */
	public void changeFolder(MenuItem m){
		int id = m.getItemId();
		int page = pageNumbers.getInt("current", 1);
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
		if (id == R.id.action_inbox) {
			//If going to inbox from other folder.
			if(!apv.getFolderNames().equals("INBOX")){
				numEdit.putInt("current", 1);
				numEdit.commit();
			}
			dialog = new ProgressDialog(this);
			//reading from the resource file depending on which language is selected
			String fetchinbox = getResources().getString(R.string.fetch_inbox);
			dialog.setMessage(fetchinbox);
			dialog.setIndeterminate(true);
			dialog.setCancelable(false);
			dialog.show();

			getActionBar().setTitle(getResources().getString(R.string.inbox) 
					+ " " + getResources().getString(R.string.page) + " " + page);  
			apv.setAllFolders("INBOX");
			if(!isServiceRunning()){
				startBackground();
			}
			else{
				mService.restart();
			}
		}
		else if (id == R.id.action_sent) {
			//If going to sent from other folder.
			if(!apv.getFolderNames().contains("Sent")){
				numEdit.putInt("current", 1);
				numEdit.commit();
			}
			if(isServiceRunning()){
				stopBackground();
			}
			getActionBar().setTitle(getResources().getString(R.string.sent) 
					+ " " + getResources().getString(R.string.page) + " " + page);
			apv.setAllFolders("[Gmail]/Sent Mail");
			refreshList();
		}
		else{
			//If going to drafts from other folder.
			if(!apv.getFolderNames().contains("Drafts")){
				numEdit.putInt("current", 1);
				numEdit.commit();
			}
			if(isServiceRunning()){
				stopBackground();
			}
			getActionBar().setTitle(getResources().getString(R.string.drafts)
					+ " " + getResources().getString(R.string.page) + " " + page);
			apv.setAllFolders("[Gmail]/Drafts");
			refreshList();
		}
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
				if (dialog != null && dialog.isShowing()) {
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
		try{
			unbindService(mServiceConnection);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	/**
	 * Used to update contents of email-list based on when user pressses a foldername
	 * on the actionbar.
	 */
	private void refreshList(){
		String key = "Some Key";
		Encryption decrypter = new Encryption();
		emails = new ArrayList<Message>();
		for(String s : activeAccs){		
			String pw = decrypter.decrypt(key, accounts.getString(s, ""));
			MailFunctionality mf = new MailFunctionality(s, pw, (s.split("@"))[1]);
			mf.getFolder(this);
		}
	}
	/**
	 * Used when navigating to the next page (the next 20 emails). The menuitem
	 * related to this function is only enabled when the number of active accounts
	 * is exactly 1, since our page change algorithm does not support multiple
	 * accounts. 
	 */
	public void toNextPage(MenuItem m){
		int current = pageNumbers.getInt("current", 1);
		if(emails.size() < 20){
			Toast toast = Toast.makeText(getApplicationContext(), getApplicationContext()
					.getResources().getString(R.string.on_last), Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
			toast.show();
		}
		else{
			AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
			String temp = apv.getFolderNames();
			numEdit.putInt("current", current+1);
			numEdit.commit();
			if(temp.contains("Drafts")){
				changeFolder(testMenu.findItem(R.id.action_drafts));
			}
			else if(temp.contains("Sent")){
				changeFolder(testMenu.findItem(R.id.action_sent));
			}
			else{
				changeFolder(testMenu.findItem(R.id.action_inbox));
			}
		}
	}
	/**
	 * Used when navigating to the previous page (the previous 20 emails). The menuitem
	 * related to this function is only enabled when the number of active accounts
	 * is exactly 1, since our page change algorithm does not support multiple
	 * accounts. 
	 */
	public void toPreviousPage(MenuItem m){
		int current = pageNumbers.getInt("current", 1);
		if(current == 1){
			Toast toast = Toast.makeText(getApplicationContext(), getApplicationContext()
					.getResources().getString(R.string.on_first), Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
			toast.show();
		}
		else{
			AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
			String temp = apv.getFolderNames();
			numEdit.putInt("current", current-1);
			numEdit.commit();
			if(temp.contains("Drafts")){
				changeFolder(testMenu.findItem(R.id.action_drafts));
			}
			else if(temp.contains("Sent")){
				changeFolder(testMenu.findItem(R.id.action_sent));
			}
			else{
				changeFolder(testMenu.findItem(R.id.action_inbox));
			}
		}
	}
	/**
	 * Used when navigating to the first page (the newest 20 emails). The menuitem
	 * related to this function is only enabled when the number of active accounts
	 * is exactly 1, since our page change algorithm does not support multiple
	 * accounts. 
	 */
	public void toFirstPage(MenuItem m){
		int current = pageNumbers.getInt("current", 1);
		if(current == 1){
			Toast toast = Toast.makeText(getApplicationContext(), getApplicationContext()
					.getResources().getString(R.string.on_first), Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
			toast.show();
		}
		else{
			AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
			String temp = apv.getFolderNames();
			numEdit.putInt("current", 1);
			numEdit.commit();
			if(temp.contains("Drafts")){
				changeFolder(testMenu.findItem(R.id.action_drafts));
			}
			else if(temp.contains("Sent")){
				changeFolder(testMenu.findItem(R.id.action_sent));
			}
			else{
				changeFolder(testMenu.findItem(R.id.action_inbox));
			}
		}
	}
	/**
	 * Menu lifecycle function used to determine how many active accounts there
	 * are, followed by enabling/disabling certain menuitems accordingly.
	 */
	@Override
	public boolean onPrepareOptionsMenu (Menu menu){
		testMenu = menu;
		//if one account is active we allow page change.
		if(activeAccs.size() == 1){
			MenuItem f = menu.findItem(R.id.action_to_first);
			MenuItem n = menu.findItem(R.id.action_to_next);
			MenuItem p = menu.findItem(R.id.action_to_previous);
			f.setEnabled(true);
			f.setVisible(true);
			n.setEnabled(true);
			n.setVisible(true);
			p.setEnabled(true);
			p.setVisible(true);
		}
		/*If more than one account is active we disable page change, since our page change
		  algorithm currently doesn't support multiple accounts. **/
		else{
			MenuItem f = menu.findItem(R.id.action_to_first);
			MenuItem n = menu.findItem(R.id.action_to_next);
			MenuItem p = menu.findItem(R.id.action_to_previous);
			f.setEnabled(false);
			f.setVisible(false);
			n.setEnabled(false);
			n.setVisible(false);
			p.setEnabled(false);
			p.setVisible(false);
		}
		return true;
	}
}

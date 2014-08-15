package TIG055st2014.mailmaster.Activities;

import TIG055st2014.mailmaster.R;
import TIG055st2014.mailmaster.Adapters.AccountAdapter;
import TIG055st2014.mailmaster.HelpClasses.AppVariablesSingleton;
import android.app.Activity;

import java.util.Locale;
import android.os.Bundle;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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
 * Activity for selecting which added account to use (and for deleting
 * accounts).
 */
public class AccountSettingsActivity extends Activity implements
AdapterView.OnItemClickListener {

	public SharedPreferences accounts;
	public SharedPreferences.Editor accEdit;
	public ListView listView;
	public ArrayList<String> columns;
	/** Used to access menu items from test code.**/
	public Menu testMenu;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview_accounts);
		String chooseacc = getResources().getString(R.string.choose_acc);
		setTitle(chooseacc);
		getActionBar().setDisplayShowHomeEnabled(false);
		columns = new ArrayList<String>();
		accounts = getSharedPreferences("StoredAccounts", MODE_PRIVATE);
		accEdit = accounts.edit();
		Set<String> c = accounts.getAll().keySet();
		if (c != null) {
			c.remove("default");
			c.remove("enabled");
		}
		columns.addAll(c);
		listView = (ListView) findViewById(R.id.account_list);
		listView.setClickable(true);
		listView.setOnItemClickListener(this);
		/* Used to make sure the textview shows up nicely at the bottom of the list,
		   regardless of list size. **/
		listView.addFooterView(getLayoutInflater().inflate(
				R.layout.activity_account_settings, null));
	}
	@Override
	protected void onStart(){
		super.onStart();
		updateList();
	}
	/**
	 * Overridden method used to change the default account.
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View newDef, int position,
			long id) {
		if(position == columns.size()){
			/* Ignore since this item is the footer of the list, the textview
			showing the counter of active accounts.**/
		}
		else{
			int count = accounts.getInt("enabled", 0);
			Set<String> activeAccs = new HashSet<String>();
			activeAccs.addAll(accounts.getStringSet("default", new HashSet<String>()));
			//Max amount of active accounts is 3 (for performance reasons).
			if (!activeAccs.contains(columns.get(position)) && count < 3) {
				activeAccs.add(columns.get(position));
				accEdit.putStringSet("default", activeAccs);
				accEdit.putInt("enabled", count + 1);
				accEdit.commit();
				//If first active account is being added, we need show the folder icon.
				if (activeAccs.size() == 1) {
					invalidateOptionsMenu();
				}
			} else if (activeAccs.contains(columns.get(position))) {
				activeAccs.remove(columns.get(position));
				accEdit.putStringSet("default", activeAccs);
				accEdit.putInt("enabled", count - 1);
				accEdit.commit();
				//If last active account is being removed, we need to hide the folder icon.
				if (activeAccs.size() == 0) {
					invalidateOptionsMenu();
				}
			}
			updateList();	
		}
	}
	/**
	 * Redirect triggered by pressing the add account icon.
	 */
	public void toAdd(MenuItem m) {
		startActivity(new Intent("TIG055st2014.mailmaster.Activities.AddAccountActivity"));
	}

	/**
	 * Redirect triggered by selecting one of 3 folders available for viewing.
	 * Note that the foldername string is updated inside MailFunctionality if
	 * the current default email address is not gmail/student.gu.
	 */
	public void toFolder(MenuItem m) {
		int id = m.getItemId();
		Set<String> activeAccs = new HashSet<String>();
		activeAccs.addAll(accounts.getStringSet("default", new HashSet<String>()));
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
		apv.initAccounts();
		for (String s : activeAccs) {
			apv.setFolderName(s, apv.getFolderName(s));
		}
		if (id == R.id.action_toInbox) {
			apv.setAllFolders("INBOX");

		} else if (id == R.id.action_toSent) {
			apv.setAllFolders("[Gmail]/Sent Mail");
		} else {
			apv.setAllFolders("[Gmail]/Drafts");
		}
		startActivity(new Intent("TIG055st2014.mailmaster.Activities.MailFolderActivity"));
	}
	/**
	 * onClick method for the language menuitems. Only changes UI language, we do not
	 * support email addresses that have their IMAP foldernames saved in another language
	 * besides english.
	 */
	public void changeLanguage(MenuItem m) {
		switch (m.getItemId()) {

		case R.id.action_En:
			changeTo("en");
			break;

		case R.id.action_Sw:
			changeTo("sv");
			break;

		case R.id.action_Ar:
			changeTo("ar");
			break;

		case R.id.action_De:
			changeTo("de");
			break;
		}
	}
	/**
	 * Helper method for switching language/locale. This is done for the app only and does
	 * not affect the system settings of the phone itself.
	 * @param language The code for the language to switch to.
	 */
	private void changeTo(String language) {
		Locale locale = new Locale(language);
		Locale.setDefault(locale);
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = locale;
		res.updateConfiguration(conf, dm);
		Intent switchLanguage = new Intent(this, AccountSettingsActivity.class);
		startActivity(switchLanguage);
		Toast.makeText(this, "Changed to locale: " + language +"!", Toast.LENGTH_LONG)
		.show();
	}
	/**
	 * We disable the back button while the user is on the add account screen,
	 * in order to prevent certain possible issues (going back in history stack
	 * can cause display of "outdated" information.
	 */
	@Override
	public void onBackPressed() {
		// Do nothing.
	}

	/**
	 * Creates the AccountSettings optionsmenu. The folder icon is hidden if no
	 * accounts have been added.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.account_settings, menu);
		testMenu = menu;
		MenuItem folder = menu.findItem(R.id.action_folder);
		Set<String> defAcc = new HashSet<String>();
		defAcc.addAll(accounts.getStringSet("default", new HashSet<String>()));

		if (defAcc.size() == 0) {
			folder.setEnabled(false);
			folder.setVisible(false);
		} else {
			folder.setEnabled(true);
			folder.setVisible(true);
		}
		return true;
	}
	/**
	 * Used whenever the contents of the accounts list needs to be changed in the UI.
	 */
	public void updateList() {
		int count = accounts.getInt("enabled", 0);
		listView.setAdapter(new AccountAdapter(getApplicationContext(),
				R.layout.account_item, R.id.account_text, columns, this));
		TextView activeAccounts = (TextView) findViewById(R.id.active_acc);
		//reading from the resource file depending on which language is selected
		String active_account = getResources().getString(R.string.active_account);
		activeAccounts.setText(active_account + count + "/3");
	}
}

package TIG055st2014.mailmaster;

import android.app.Activity;

import java.util.Locale;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.*;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

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
	TextView activeaccount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_settings);
		String chooseacc = (String) getResources().getText(R.string.choose_acc);
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
		updateList();

	}

	/**
	 * Overridden method used to change the default account.
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View newDef, int position,
			long id) {
		int count = accounts.getInt("enabled", 0);
		Set<String> defAcc = new HashSet<String>();
		defAcc.addAll(accounts.getStringSet("default", new HashSet<String>()));
		if (!defAcc.contains(columns.get(position)) && count < 3) {
			defAcc.add(columns.get(position));
			accEdit.putStringSet("default", defAcc);
			accEdit.putInt("enabled", count + 1);
			accEdit.commit();
			
			
			if (defAcc.size() == 1) {
				invalidateOptionsMenu();
			}
		} else if (defAcc.contains(columns.get(position))) {
			defAcc.remove(columns.get(position));
			accEdit.putStringSet("default", defAcc);
			accEdit.putInt("enabled", count - 1);
			accEdit.commit();
			if (defAcc.size() == 0) {
				invalidateOptionsMenu();
			}
		}
		updateList();
		activeaccount = (TextView) findViewById(R.id.active_acc);
		//reading from the resource file depending on which language is selected
		String active_account = (String) activeaccount.getResources().getText(R.string.active_account);
		activeaccount.setText(active_account + defAcc.size());

	
	}

	/**
	 * Redirect triggered by pressing the add account icon.
	 */
	public void toAdd(MenuItem m) {
		startActivity(new Intent("TIG055st2014.mailmaster.AddAccountActivity"));
	}

	/**
	 * Redirect triggered by selecting one of 3 folders available for viewing.
	 * Note that the foldername string is updated inside MailFunctionality if
	 * the current default email address is not gmail/student.gu.
	 */

	public void toFolder(MenuItem m) {
		int id = m.getItemId();
		Set<String> defAcc = new HashSet<String>();
		defAcc.addAll(accounts.getStringSet("default", new HashSet<String>()));
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
		apv.initAccounts();
		for (String s : defAcc) {
			apv.setFolderName(s, apv.getFolderName(s));
		}
		if (id == R.id.action_toInbox) {
			apv.setAllFolders("INBOX");

		} else if (id == R.id.action_toSent) {
			apv.setAllFolders("[Gmail]/Sent Mail");
		} else {
			apv.setAllFolders("[Gmail]/Drafts");
		}
		startActivity(new Intent("TIG055st2014.mailmaster.MailFolderActivity"));
	}

	public boolean toLanguge(MenuItem m1) {
		switch (m1.getItemId()) {

		case R.id.action_En:
			Locale locale2 = new Locale("en");
			Locale.setDefault(locale2);
			Resources res = getResources();
			DisplayMetrics dm = res.getDisplayMetrics();
			Configuration conf = res.getConfiguration();
			conf.locale = locale2;
			res.updateConfiguration(conf, dm);
			// getBaseContext().getResources().updateConfiguration(config2,
			// getBaseContext().getResources().getDisplayMetrics());
			Intent refresh = new Intent(this, AccountSettingsActivity.class);
			startActivity(refresh);
			Toast.makeText(this, "Locale in English!", Toast.LENGTH_LONG)
					.show();
			break;

		case R.id.action_Sw:
			Locale locale3 = new Locale("sv");
			Locale.setDefault(locale3);
			Resources res1 = getResources();
			DisplayMetrics dm1 = res1.getDisplayMetrics();
			Configuration conf1 = res1.getConfiguration();
			conf1.locale = locale3;
			res1.updateConfiguration(conf1, dm1);
			Intent refresh1 = new Intent(this, AccountSettingsActivity.class);
			startActivity(refresh1);
			Toast.makeText(this, "Locale på svenska!", Toast.LENGTH_LONG)
					.show();
			break;
			
		case R.id.action_Ar:
			Locale locale4 = new Locale("ar");
			Locale.setDefault(locale4);
			Resources res2 = getResources();
			DisplayMetrics dm2 = res2.getDisplayMetrics();
			Configuration conf2 = res2.getConfiguration();
			conf2.locale = locale4;
			res2.updateConfiguration(conf2, dm2);
			Intent refresh2 = new Intent(this, AccountSettingsActivity.class);
			startActivity(refresh2);
			Toast.makeText(this, "Locale in Arabic!", Toast.LENGTH_LONG)
					.show();
			break;
			
		case R.id.action_De:
			Locale locale5 = new Locale("de");
			Locale.setDefault(locale5);
			Resources res3 = getResources();
			DisplayMetrics dm3 = res3.getDisplayMetrics();
			Configuration conf3 = res3.getConfiguration();
			conf3.locale = locale5;
			res3.updateConfiguration(conf3, dm3);
			Intent refresh3 = new Intent(this, AccountSettingsActivity.class);
			startActivity(refresh3);
			Toast.makeText(this, "Gebietsschema auf Deutsch!", Toast.LENGTH_LONG)
					.show();
			break;
			
		
		}
		return super.onOptionsItemSelected(m1);
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

	public void updateList() {
		listView.setAdapter(new AccountAdapter(getApplicationContext(),
				R.layout.account_item, R.id.account_text, columns, this));
	}
}
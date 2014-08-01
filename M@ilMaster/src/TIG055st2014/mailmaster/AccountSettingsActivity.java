package TIG055st2014.mailmaster;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.Set;

/**
 * Activity for selecting which added account to use (and for deleting accounts).
 */
public class AccountSettingsActivity extends Activity implements AdapterView.OnItemClickListener {

	public SharedPreferences accounts;
	private SharedPreferences.Editor accEdit;
	private ListView listView;
	public ArrayList<String> columns;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_settings);
		getActionBar().setDisplayShowHomeEnabled(false);
		columns = new ArrayList<String>();
		accounts = getSharedPreferences("StoredAccounts", MODE_PRIVATE);
		accEdit = accounts.edit();
		Set<String> c = accounts.getAll().keySet();
		if(c != null){
			c.remove("default");
		}
		columns.addAll(c);
		listView = (ListView) findViewById(R.id.account_list);
		listView.setClickable(true);
		listView.setOnItemClickListener(this);
		listView.setAdapter(new AccountAdapter(getApplicationContext(),R.layout.account_item,
				R.id.account_text, columns, this));
	}
	/**
	 * Overridden method used to change the default account.
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View newDef, int position, long id) {
		String temp = accounts.getString("default", "");
		if(!temp.equals(columns.get(position))){
			accEdit.putString("default", columns.get(position));
			accEdit.commit();
			listView.setAdapter(new AccountAdapter(getApplicationContext(),R.layout.account_item,
					R.id.account_text, columns, this));
		}
	}
	/**
	 * Redirect triggered by pressing the add account icon.
	 */
	public void toAdd(MenuItem m){
		startActivity(new Intent("TIG055st2014.mailmaster.AddAccountActivity"));
	}
	/**
	 * Redirect triggered by selecting one of 3 folders available for viewing.
	 * Note that the foldername string is updated inside MailFunctionality if the 
	 * current default email address is not gmail/student.gu.
	 */
	public void toFolder(MenuItem m){
		int id = m.getItemId();
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
		if (id == R.id.action_toInbox) {
			apv.setFolderName("INBOX");
		}
		else if (id == R.id.action_toSent) {
			apv.setFolderName("[Gmail]/Sent Mail");
		}
		else{
			apv.setFolderName("[Gmail]/Drafts");
		}
		startActivity(new Intent("TIG055st2014.mailmaster.MailFolderActivity"));
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
	 * Creates the AccountSettings optionsmenu. The folder icon is hidden if no accounts have been
	 * added.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.account_settings, menu);
		MenuItem folder = menu.findItem(R.id.action_folder);
		if(columns.size() == 0){
			folder.setEnabled(false);
			folder.setVisible(false);
		}
		else{
			folder.setEnabled(true);
			folder.setVisible(true);
		}
		return true;
	}
}
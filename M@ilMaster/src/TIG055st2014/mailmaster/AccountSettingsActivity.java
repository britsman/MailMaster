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
* Activity for choosing which added accounts to show email for (and deleting accounts entirely).
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
* Overridden method used to change the default sending account.
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
    public void toAdd(MenuItem m){
    	startActivity(new Intent("TIG055st2014.mailmaster.AddAccountActivity"));
    }
    public void toInbox(MenuItem m){
        int id = m.getItemId();
        DisplayEmail d = DisplayEmail.getInstance();
        if (id == R.id.action_toInbox) {
        	d.setFolderName("INBOX");
        }
        else if (id == R.id.action_toSent) {
        	d.setFolderName("[Gmail]/Sent Mail");
        }
        else{
        	d.setFolderName("[Gmail]/Drafts");
        }
    	startActivity(new Intent("TIG055st2014.mailmaster.InboxActivity"));
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
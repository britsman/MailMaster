package TIG055st2014.mailmaster;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;
import android.view.Gravity;
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

    private SharedPreferences accounts;
    private SharedPreferences.Editor accEdit;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        ArrayList<String> columns = new ArrayList<String>();
        accounts = getSharedPreferences("StoredAccounts", MODE_PRIVATE);
        accEdit = accounts.edit();
        Set<String> c = accounts.getAll().keySet();
        columns.addAll(c);
        listView = (ListView) findViewById(R.id.account_list);
        listView.setClickable(true);
        listView.setOnItemClickListener(this);
        listView.setAdapter(new AccountAdapter(getApplicationContext(),R.layout.account_item,
                R.id.account_text, columns));
    }

    /**
* Overriden method used to try and log in with the selected account.
*/
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (true){ //Add code to check if mail is currently "active".
        	//"Unselect" account.
        }
        else{
            //Select account.
        }
    }
}
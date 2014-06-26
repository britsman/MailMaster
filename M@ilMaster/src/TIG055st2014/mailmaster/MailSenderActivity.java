package TIG055st2014.mailmaster;

import java.util.ArrayList;

import javax.mail.Message;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MailSenderActivity extends Activity implements AdapterView.OnItemClickListener{
	
    private SharedPreferences accounts;
    private String defaultAcc;
    private String pw;
    private ListView listView;
    private ArrayList<Message> emails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_sender);
        accounts = getSharedPreferences("StoredAccounts", MODE_PRIVATE);
        defaultAcc = accounts.getString("default", "");
        pw = accounts.getString(defaultAcc, "");
        if(defaultAcc.equals("")){
        	startActivity(new Intent("TIG055st2014.mailmaster.AddAccountActivity"));
        }
        else{
			MailFunctionality mf = new MailFunctionality(defaultAcc, pw, (defaultAcc.split("@"))[1]);
			emails = mf.getInbox(); 
	        listView = (ListView) findViewById(R.id.inbox_list);
	        listView.setClickable(true);
	        listView.setOnItemClickListener(this);
	        listView.setAdapter(new EmailAdapter(getApplicationContext(),R.layout.email_item,
	                R.id.email_preview, emails));
        }
    }
    public void onClickCompose(View v) {
    	startActivity(new Intent("TIG055st2014.mailmaster.ComposeActivity"));
    }
   
    public void onClickSettings(View v) {
    	startActivity(new Intent("TIG055st2014.mailmaster.AccountSettingsActivity"));
    }
    
    public void onClickAttach(View v) {
    	startActivity(new Intent("TIG055st2014.mailmaster.ComposeActivity"));
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
		// TODO Auto-generated method stub
		
	}
}

package TIG055st2014.mailmaster;

import java.util.ArrayList;

import javax.mail.Message;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class InboxActivity extends Activity implements AdapterView.OnItemClickListener{
	
    private SharedPreferences accounts;
    private String defaultAcc;
    private String pw;
    private ListView listView;
    private ArrayList<Message> emails;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        accounts = getSharedPreferences("StoredAccounts", MODE_PRIVATE);
        defaultAcc = accounts.getString("default", "");
        pw = accounts.getString(defaultAcc, "");
        if(defaultAcc.equals("")){
        	startActivity(new Intent("TIG055st2014.mailmaster.AddAccountActivity"));
        }
        else{
        	DisplayEmail d = DisplayEmail.getInstance();
        	if(d.getFolderName() == null){//Default to inbox if no other folder has been selected.
        		d.setFolderName("INBOX");
        	}
			MailFunctionality mf = new MailFunctionality(defaultAcc, pw, (defaultAcc.split("@"))[1]);
			emails = mf.getInbox(); 
	        listView = (ListView) findViewById(R.id.inbox_list);
	        listView.setClickable(true);
	        listView.setOnItemClickListener(this);
	        listView.setAdapter(new EmailAdapter(getApplicationContext(),R.layout.email_item,
	                R.id.email_preview, emails));
        }
    }
    public void onClickCompose(MenuItem m) {
    	DisplayEmail d = DisplayEmail.getInstance();
    	d.setIsReply(false);
    	startActivity(new Intent("TIG055st2014.mailmaster.ComposeActivity"));
    }
   
    public void onClickSettings(MenuItem m) {
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
        }
        else if (id == R.id.action_sent) {
        	d.setFolderName("[Gmail]/Sent Mail");
        }
        else{
        	d.setFolderName("[Gmail]/Drafts");
        }
		MailFunctionality mf = new MailFunctionality(defaultAcc, pw, (defaultAcc.split("@"))[1]);
		emails = mf.getInbox(); 
        listView.setAdapter(new EmailAdapter(getApplicationContext(),R.layout.email_item,
                R.id.email_preview, emails));
    }
}

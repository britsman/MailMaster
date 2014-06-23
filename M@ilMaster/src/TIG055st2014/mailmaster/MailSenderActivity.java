package TIG055st2014.mailmaster;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MailSenderActivity extends Activity {
	
    private SharedPreferences accounts;
    private String defaultAcc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_sender);
        accounts = getSharedPreferences("StoredAccounts", MODE_PRIVATE);
        defaultAcc = accounts.getString("default", "");
        if(defaultAcc.equals("")){
        	startActivity(new Intent("TIG055st2014.mailmaster.AddAccountActivity"));
        }
    }
    public void onClickCompose(View v) {
    	startActivity(new Intent("TIG055st2014.mailmaster.ComposeActivity"));
    }
    public void onClickSettings(View v) {
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
}
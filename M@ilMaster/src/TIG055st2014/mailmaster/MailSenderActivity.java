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
    private String pw;

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
			mf.getInbox(); 
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
}
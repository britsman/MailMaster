package TIG055st2014.mailmaster;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MailSenderActivity extends Activity {
	
    private SharedPreferences accounts;
    private SharedPreferences.Editor accEdit;
    private String loggedInAcc;
    private String pw;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail_sender);
        accounts = getSharedPreferences("StoredAccounts", MODE_PRIVATE);
        accEdit = accounts.edit();
        loggedInAcc = accounts.getString("email", "");
        pw = accounts.getString("password", "");
        if(loggedInAcc.equals("")){
        	startActivity(new Intent("TIG055st2014.mailmaster.LoginActivity"));
        }
    }
    public void onClickSend(View v) {

        try {   
            MailFunctionality mf = new MailFunctionality(loggedInAcc, pw, (loggedInAcc.split("@"))[1]);
            mf.sendMail("M@ilMaster Message",  
                    "This is a hardcoded email generated via the M@ilMaster android app",   
                    loggedInAcc,   
                    "eric_britsman@hotmail.com");  
        } 
        catch (Exception e) {   
            Log.e("SendMail", e.getMessage(), e);   
        } 

    }
}
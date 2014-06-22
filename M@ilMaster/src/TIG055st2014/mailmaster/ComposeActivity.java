package TIG055st2014.mailmaster;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ComposeActivity extends Activity {
	
    private SharedPreferences accounts;
    private String defaultAcc;
    private String pw;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_compose);
        accounts = getSharedPreferences("StoredAccounts", MODE_PRIVATE);
        defaultAcc = accounts.getString("default", "");
        pw = accounts.getString(defaultAcc, "");
	}
	@Override
	protected void onStart() {
		super.onStart();
		TextView sender = (TextView) findViewById(R.id.sendAcc);
		sender.setText(defaultAcc);
	}
	public void onClickSend(View v){
		String recipients = ((EditText) findViewById(R.id.receiveAccs)).getText().toString();
		String subject = ((EditText) findViewById(R.id.subject)).getText().toString();
		String body = ((EditText) findViewById(R.id.body)).getText().toString();
		
		if(!recipients.equals("") && !subject.equals("") && !body.equals("")){
			try {   
				MailFunctionality mf = new MailFunctionality(defaultAcc, pw, (defaultAcc.split("@"))[1]);
				mf.sendMail(subject, body, defaultAcc, recipients);  
			} 
			catch (Exception e) {   
				Log.e("SendMail", e.getMessage(), e);   
			}
		}
		else{
            //Missed fields
            Toast toast = Toast.makeText(getApplicationContext(),
                    "One or more fields are unfilled.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
            toast.show();
		}
	}
}

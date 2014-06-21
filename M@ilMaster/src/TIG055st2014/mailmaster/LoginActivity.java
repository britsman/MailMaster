package TIG055st2014.mailmaster;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
* Activity representing the application's login screen.
*/
public class LoginActivity extends Activity {

    private SharedPreferences accounts;
    private SharedPreferences.Editor accEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        accounts = getSharedPreferences("StoredAccounts", MODE_PRIVATE);
        accEdit = accounts.edit();
    }

    /**
* onClick for the add account button, evaluates if info is valid.
*/
    public void login(View view) {
        final String email = ((EditText)findViewById(R.id.email)).getText().toString().trim().toLowerCase();
        final String pw = ((EditText)findViewById(R.id.password)).getText().toString().trim();
        final String value = accounts.getString(email, "");
        final String[] pieces = email.split("@");

        if(email.length() < 1 || pw.length() < 1){
            //Error handling too short/blank field.
            Toast toast = Toast.makeText(getApplicationContext(),
            "Field(s) too short/unfilled", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP| Gravity.LEFT, 0, 0);
            toast.show();
        }
        else if (value.equals("")) {
            // Logging in with New/Unsaved account.
            if (pieces.length == 2 && (pieces[1].equalsIgnoreCase("hotmail.com") || 
            	pieces[1].equalsIgnoreCase("live.com") || 
            	pieces[1].equalsIgnoreCase("outlook.com") || 
            	pieces[1].equalsIgnoreCase("gmail.com") ||
            	pieces[1].equalsIgnoreCase("student.gu.se"))) {//Also need to check if login succeeds (acc exists).
                //Account remembered even if app is force stopped.
                accEdit.putString(email, pw);
                accEdit.putString("default", email);
                accEdit.commit();
                startActivity(new Intent("TIG055st2014.mailmaster.MailSenderActivity"));
            }
            else{
                //Invalid  or unsupported Email.
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Not a valid/supported Email adress", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
                toast.show();
            }
        }
        else {
            //Accoount already added
            Toast toast = Toast.makeText(getApplicationContext(),
                    "This account has already been added.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
            toast.show();
        }
    }
    /**
    * User is redirected if the account settings button is pressed.
    */
    public void pickAcc(View view) {//Disabled until multiaccount is enabled.
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
package TIG055st2014.mailmaster;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Activity representing the application's add account screen. This is used
 * to provide the app with accounts to retrieve/send emails with. Note that
 * passwords are encrypted (in MailFunctionality) before they are saved to
 * sharedpreference. 
 */
public class AddAccountActivity extends Activity {

	public SharedPreferences accounts;
	public SharedPreferences.Editor accEdit;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_account);
		// Disabling icon in top left corner 
		getActionBar().setDisplayShowHomeEnabled(false);
		accounts = getSharedPreferences("StoredAccounts", MODE_PRIVATE);
		accEdit = accounts.edit();
	}

	/**
	 * onClick method for the add account button. Evaluates the contents of the email & password
	 * fields.
	 */
	public void add(View view) {
		final String email = ((EditText)findViewById(R.id.email)).getText().toString().trim()
				.toLowerCase();
		final String pw = ((EditText)findViewById(R.id.password)).getText().toString().trim();
		final String value = accounts.getString(email, "");
		final String[] pieces = email.split("@");

		//Error handling too short or blank field.
		if(email.length() < 1 || pw.length() < 1){
			Toast toast = Toast.makeText(getApplicationContext(),
					"Field(s) too short/unfilled", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP| Gravity.LEFT, 0, 0);
			toast.show();
		}
		// Logging in with New/Unsaved account.
		else if (value.equals("")) {
			if (pieces.length == 2 && (pieces[1].equalsIgnoreCase("hotmail.com") || 
					pieces[1].equalsIgnoreCase("live.com") || 
					pieces[1].equalsIgnoreCase("outlook.com") || 
					pieces[1].equalsIgnoreCase("gmail.com") ||
					pieces[1].equalsIgnoreCase("student.gu.se"))) {

				MailFunctionality mf = new MailFunctionality(email, pw, pieces[1]);
				mf.validate(this);
			}
			//Unsupported Email.
			else{
				Toast toast = Toast.makeText(getApplicationContext(),
						"Email provider not supported.", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
				toast.show();
			}
		}
		//Account has already been added previously.
		else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"This account has already been added.", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
			toast.show();
		}
	}
	/**
	 * User is redirected if the account settings button is pressed.
	 */
	public void toSettings(MenuItem m) {
		startActivity(new Intent("TIG055st2014.mailmaster.AccountSettingsActivity"));
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
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_account, menu);
		return true;
	}
}
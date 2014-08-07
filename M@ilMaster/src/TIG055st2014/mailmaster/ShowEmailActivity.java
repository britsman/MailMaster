package TIG055st2014.mailmaster;

import java.util.Set;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

/**
 * Activity used to display the contents of a selected email's body.
 */
public class ShowEmailActivity extends Activity {

	private SharedPreferences accounts;
	private String currentAcc;
	private String pw;
	/**
	 * A WebView is used in order to easily display HTML contents.
	 */
	private WebView wv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_email);
		getActionBar().setDisplayShowHomeEnabled(false);
		accounts = getSharedPreferences("StoredAccounts", MODE_PRIVATE);
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
		currentAcc = apv.getAccount();
		String key = "Some Key";
		Encryption encryption = new Encryption();
		pw = encryption.decrypt(key, (accounts.getString(currentAcc, "")));
	}
	@Override
	protected void onStart() {
		super.onStart();
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
		//In case app has been left on show email screen untouched for about 30 min
		//Basically if OS destroys the AppVariablesSingleton instance.
		if(apv.getEmail() == null){
			startActivity(new Intent("TIG055st2014.mailmaster.MailFolderActivity"));
		}
		else{
			wv = (WebView) findViewById(R.id.display);
			//Enables zoom.
			wv.getSettings().setBuiltInZoomControls(true);
			wv.getSettings().setDisplayZoomControls(false);
			//Increases max zoomout level.
			wv.getSettings().setUseWideViewPort(true);
			try {
				MailFunctionality mf = new MailFunctionality(currentAcc, pw, (currentAcc.split("@"))[1]);
				mf.getContents(this);
			} 
			catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}
	/**
	 * Called from onPostExecute in AsyncTask after content has been parsed in the background.
	 */
	protected void load(String contents){	
		wv.loadData(contents, "text/html;charset=UTF-8", null);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.show_email, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_reply) {
			MailFunctionality mf = new MailFunctionality(currentAcc, pw, (currentAcc.split("@"))[1]);
			AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
			try{
				mf.getReply(apv.getEmail(), this);
			}
			catch(Exception e){
				e.printStackTrace();
			}

			return true;
		}
		if (id == R.id.get_attachments) {
			startActivity(new Intent("TIG055st2014.mailmaster.AttachmentsActivity"));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void onBackPressed() {
		//Force update of email list when back is pressed.
		startActivity(new Intent("TIG055st2014.mailmaster.MailFolderActivity"));
	}
}

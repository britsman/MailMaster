package TIG055st2014.mailmaster;

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
	private String defaultAcc;
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
		defaultAcc = accounts.getString("default", "");
		String key = "Some Key";
		Encryption encryption = new Encryption();
		pw = encryption.decrypt(key, (accounts.getString(defaultAcc, "")));
	}
	@Override
	protected void onStart() {
		super.onStart();
		wv = (WebView) findViewById(R.id.display);
		//Enables zoom.
		wv.getSettings().setBuiltInZoomControls(true);
		wv.getSettings().setDisplayZoomControls(false);
		//Increases max zoomout level.
		wv.getSettings().setUseWideViewPort(true);
		try {
			MailFunctionality mf = new MailFunctionality(defaultAcc, pw, (defaultAcc.split("@"))[1]);
			mf.getContents(this);
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
	}
	/**
	 * Called from onPostExecute in AsyncTask after content has been parsed in the background.
	 */
	protected void load(String contents){	
		wv.loadData(contents, "text/html", null);
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
        	MailFunctionality mf = new MailFunctionality(defaultAcc, pw, (defaultAcc.split("@"))[1]);
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

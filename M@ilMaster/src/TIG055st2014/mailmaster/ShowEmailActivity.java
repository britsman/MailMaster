package TIG055st2014.mailmaster;

import javax.mail.MessagingException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

public class ShowEmailActivity extends Activity {
	
	private SharedPreferences accounts;
	private String defaultAcc;
	private String pw;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_email);
		accounts = getSharedPreferences("StoredAccounts", MODE_PRIVATE);
		defaultAcc = accounts.getString("default", "");
        pw = accounts.getString(defaultAcc, "");
	}
	@Override
	protected void onStart() {
		super.onStart();
		WebView wv = (WebView) findViewById(R.id.display);
		wv.getSettings().setBuiltInZoomControls(true);
		wv.getSettings().setDisplayZoomControls(false);
		wv.getSettings().setUseWideViewPort(true);
		try {
			MailFunctionality mf = new MailFunctionality(defaultAcc, pw, (defaultAcc.split("@"))[1]);
			wv.loadData(mf.getContents(), "text/html", null);
		} 
		catch (Exception e) {
			e.printStackTrace();
		} 
	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_email, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_reply) {
            //Call reply function
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

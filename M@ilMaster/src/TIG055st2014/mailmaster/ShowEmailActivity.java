package TIG055st2014.mailmaster;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

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
        getMenuInflater().inflate(R.menu.show_email, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_reply) {
        	MailFunctionality mf = new MailFunctionality(defaultAcc, pw, (defaultAcc.split("@"))[1]);
            DisplayEmail d = DisplayEmail.getInstance();
            try{
            	d.setReply(mf.getReply(d.getEmail()));
            	d.setIsReply(true);
            	startActivity(new Intent("TIG055st2014.mailmaster.ComposeActivity"));
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
}

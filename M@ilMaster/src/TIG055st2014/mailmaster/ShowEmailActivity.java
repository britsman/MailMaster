package TIG055st2014.mailmaster;

import java.io.IOException;

import javax.mail.MessagingException;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.Menu;
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
}

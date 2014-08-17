package TIG055st2014.mailmaster.Activities;

import TIG055st2014.mailmaster.R;
import TIG055st2014.mailmaster.HelpClasses.AppVariablesSingleton;
import TIG055st2014.mailmaster.HelpClasses.Encryption;
import TIG055st2014.mailmaster.HelpClasses.MailFunctionality;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebSettings.ZoomDensity;

/* M@ilMaster Multi-Account Email Client
Copyright (C) 2014 Eric Britsman & Khaled Alnawasreh

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License 
Version 2 only; as published by the Free Software Foundation.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

Contact Info: eric_britsman@hotmail.com / khaled.nawasreh@gmail.com
*/

/**
 * Activity used to display the contents of a selected email's body.
 */
public class ShowEmailActivity extends Activity {

	private SharedPreferences accounts;
	private String currentAcc;
	private String pw;
	/** used to access  menuitems in tests.**/
	public Menu testMenu;
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
			startActivity(new Intent("TIG055st2014.mailmaster.Activities.MailFolderActivity"));
		}
		else{
			wv = (WebView) findViewById(R.id.display);
			//Enables zoom.
			wv.getSettings().setBuiltInZoomControls(true);
			wv.getSettings().setDisplayZoomControls(false);
			//Zooms out as much as needed to display whole contents.
			wv.getSettings().setLoadWithOverviewMode(true);
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
	public void load(String contents){	
		wv.loadData(contents, "text/html;charset=UTF-8", null);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.show_email, menu);
		testMenu = menu;
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
		else if (id == R.id.get_attachments) {
			startActivity(new Intent("TIG055st2014.mailmaster.Activities.AttachmentsActivity"));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	public void onBackPressed() {
		//Force update of email list when back is pressed.
		startActivity(new Intent("TIG055st2014.mailmaster.Activities.MailFolderActivity"));
	}
}

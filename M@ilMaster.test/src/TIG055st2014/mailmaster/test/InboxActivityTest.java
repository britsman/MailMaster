package TIG055st2014.mailmaster.test;

import TIG055st2014.mailmaster.AddAccountActivity;
import static org.junit.Assert.*;

import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.Test;
import TIG055st2014.mailmaster.R;
import android.content.Intent;
import android.content.SharedPreferences;
import android.test.ActivityUnitTestCase;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import TIG055st2014.mailmaster.AccountSettingsActivity;
import TIG055st2014.mailmaster.AppVariablesSingleton;
import TIG055st2014.mailmaster.MailFolderActivity;
import TIG055st2014.mailmaster.R;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.InstrumentationTestCase;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

public class InboxActivityTest extends ActivityUnitTestCase<MailFolderActivity> {
	private MailFolderActivity activity;
	private SharedPreferences.Editor accEdit;
	private AppVariablesSingleton apv;

	public InboxActivityTest() {
		super(MailFolderActivity.class);
		apv = AppVariablesSingleton.getInstance();
		// TODO Auto-generated constructor stub
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testComposePress() {
		new Thread(new Runnable() {

			public void run() {
				Intent intent = new Intent(getInstrumentation().getTargetContext(),
						MailFolderActivity.class);
				startActivity(intent, null, null);
				activity = getActivity();
				String target = "TIG055st2014.mailmaster.ComposeActivity";
				MenuItem composeIcon = (MenuItem) activity
						.findViewById(R.id.action_toCompose);
				activity.onClickCompose(composeIcon);
				assertTrue(getStartedActivityIntent().getAction()
						.equals(target));
			}
		});
	}
	public void testSelectEmail() {
		  new Thread(new Runnable() {

		        public void run() {
		        	Intent intent = new Intent(getInstrumentation().getTargetContext(),
		    				MailFolderActivity.class);
		    		startActivity(intent, null, null);
		    		activity = getActivity();
		    		String target = "TIG055st2014.mailmaster.ComposeActivity";
		    		String target2 = "TIG055st2014.mailmaster.ShowEmailActivity";
		        	activity.onItemClick(null, null, 0, 0);
		        	if(apv.getFolderName().contains("Drafts")){
		        	assertTrue(getStartedActivityIntent().getAction().equals(target));
		        	}
		        	else{
		        		assertTrue(getStartedActivityIntent().getAction().equals(target2));
		        	}
		       }
		    });
	}
	public void testSettingPress() {
		new Thread(new Runnable() {

			public void run() {
				Intent intent = new Intent(getInstrumentation().getTargetContext(),
						MailFolderActivity.class);
				startActivity(intent, null, null);
				activity = getActivity();
				String target = "TIG055st2014.mailmaster.AccountSettingsActivity";
				MenuItem settingIcon = (MenuItem) activity
						.findViewById(R.id.action_toSettings);
				activity.onClickSettings(settingIcon);
				assertTrue(getStartedActivityIntent().getAction()
						.equals(target));
			}
		});
	}

	public void testNoDefaultAcc() {
		new Thread(new Runnable() {
			public void run() {
				Intent intent = new Intent(getInstrumentation().getTargetContext(),
						MailFolderActivity.class);
				startActivity(intent, null, null);
				activity = getActivity();
				accEdit= activity.accounts.edit();
				accEdit.remove("default");
				accEdit.commit();
				String target = "TIG055st2014.mailmaster.AddAccountActivity";
				activity.accounts.getString("defaultAcc", "");
				assertTrue(getStartedActivityIntent().getAction()
						.equals(target));
			}
		});
	}
	public void testChangeFolder() {
		new Thread(new Runnable() {
			public void run() {
				Intent intent = new Intent(getInstrumentation().getTargetContext(),
						MailFolderActivity.class);
				startActivity(intent, null, null);
				activity = getActivity();
				activity.accounts.getString("defaultAcc", "");
				MenuItem m = (MenuItem)activity.findViewById(R.id.action_sent);
				activity.changeFolder(m);
				assertTrue(apv.getFolderName().contains("Drafts"));
				m = (MenuItem)activity.findViewById(R.id.action_inbox);
				activity.changeFolder(m);
				assertTrue(apv.getFolderName().contains("INBOX"));
			}
		});
	}
}

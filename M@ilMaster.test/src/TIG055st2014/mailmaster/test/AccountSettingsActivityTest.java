package TIG055st2014.mailmaster.test;

import java.util.HashSet;
import java.util.Set;

import TIG055st2014.mailmaster.*;

import TIG055st2014.mailmaster.R;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.UiThreadTest;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;

public class AccountSettingsActivityTest extends ActivityInstrumentationTestCase2<AccountSettingsActivity> {

	private AccountSettingsActivity activity;
	private Encryption encryption;
	private String key;
	
	public AccountSettingsActivityTest() {
		super(AccountSettingsActivity.class);
		encryption = new Encryption();
		key = "Some Key";
	}
	@Override
	protected void setUp() throws Exception{
		super.setUp();
		activity = getActivity();
	}
	@UiThreadTest 
	public void testChangeDefault() {
		String adress = "mailmastertesting@gmail.com";
		Set<String> temp = new HashSet<String>();
		temp.add(adress);
		getInstrumentation().callActivityOnCreate(activity, null);
		getInstrumentation().callActivityOnStart(activity);
		activity.accEdit.clear();
		activity.accEdit.putInt("enabled", 0);
		activity.accEdit.putString(adress, encryption.encrypt(key, "mailmaster123"));
		activity.accEdit.commit();
		activity.columns.add(adress);
		activity.updateList();
		activity.onItemClick(null, null, 0, 0);
		String defAcc = "fail";
		for(String s : activity.accounts.getStringSet("default", new HashSet<String>())){
			defAcc = s;
			break;
		}
		Log.d(adress, defAcc);
		activity.accEdit.clear();
		activity.accEdit.commit();
		assertTrue(adress.equals(defAcc));
	}/*
	public void testToAdd() {
		  new Thread(new Runnable() {

		        public void run() {
		        	Intent intent = new Intent(getInstrumentation().getTargetContext(),
		    				AccountSettingsActivity.class);
		    		startActivity(intent, null, null);
		    		activity = getActivity();
		        	String target = "TIG055st2014.mailmaster.AddAccountActivity";
		        	MenuItem addIcon = (MenuItem)activity.findViewById(R.id.action_toAddAccount);
		        	activity.toAdd(addIcon);
		        	assertTrue(getStartedActivityIntent().getAction().equals(target));
		       }
		    });
	}
	public void testToInbox() {
		  new Thread(new Runnable() {

		        public void run() {
		        	Intent intent = new Intent(getInstrumentation().getTargetContext(),
		    				AccountSettingsActivity.class);
		    		startActivity(intent, null, null);
		    		activity = getActivity();
		        	String target = "TIG055st2014.mailmaster.MailFolderActivity";
		        	MenuItem inboxIcon = (MenuItem)activity.findViewById(R.id.action_toInbox);
		        	activity.toFolder(inboxIcon);
		        	assertTrue(getStartedActivityIntent().getAction().equals(target));
		       }
		    });
	}
	public void testIconHidden() {
		  new Thread(new Runnable() {

		        public void run() {
		        	Intent intent = new Intent(getInstrumentation().getTargetContext(),
		    				AccountSettingsActivity.class);
		    		startActivity(intent, null, null);
		    		activity = getActivity();
		        	MenuItem inboxIcon = (MenuItem)activity.findViewById(R.id.action_toInbox);
		        	assertFalse(inboxIcon.isEnabled());
		       }
		    });
	}**/
}

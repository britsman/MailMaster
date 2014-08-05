package TIG055st2014.mailmaster.test;

import TIG055st2014.mailmaster.*;

import TIG055st2014.mailmaster.R;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.util.Log;
import android.view.MenuItem;

public class AccountSettingsActivityTest extends ActivityUnitTestCase<AccountSettingsActivity> {

	private AccountSettingsActivity activity;
	
	public AccountSettingsActivityTest() {
		super(AccountSettingsActivity.class);
	}
	@Override
	protected void setUp() throws Exception{
		super.setUp();
	}

	public void testChangeDefault() {
		  new Thread(new Runnable() {

		        public void run() {
		        	Intent intent = new Intent(getInstrumentation().getTargetContext(),
		    				AccountSettingsActivity.class);
		    		startActivity(intent, null, null);
		    		activity = getActivity();
		        	String adress = "mailmastertesting@gmail.com";
		        	activity.columns.add(adress);
		        	activity.onItemClick(null, null, 0, 0);
		        	assertTrue(activity.accounts.getString("default", "").equals(adress));
		       }
		    });
	}
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
	}
}

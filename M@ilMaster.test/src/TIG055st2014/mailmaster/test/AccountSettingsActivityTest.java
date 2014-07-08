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
		Intent intent = new Intent(getInstrumentation().getTargetContext(),
		        AccountSettingsActivity.class);
		startActivity(intent, null, null);
		activity = getActivity();
	}
	/*@Override
	protected void tearDown(){
	}**/

	public void testChangeDefault() {
		  activity.runOnUiThread(new Runnable() {

		        public void run() {
		        	String adress = "mailmastertesting@gmail.com";
		        	activity.columns.add(adress);
		        	activity.onItemClick(null, null, 0, 0);
		        	assertTrue(activity.accounts.getString("default", "").equals(adress));
		       }
		    });
	}
	public void testToAdd() {
		  activity.runOnUiThread(new Runnable() {

		        public void run() {
		        	String target = "TIG055st2014.mailmaster.AddAccountActivity";
		        	MenuItem addIcon = (MenuItem)activity.findViewById(R.id.action_toAddAccount);
		        	activity.toAdd(addIcon);
		        	assertTrue(getStartedActivityIntent().getAction().equals(target));
		       }
		    });
	}
	public void testToInbox() {
		  activity.runOnUiThread(new Runnable() {

		        public void run() {
		        	String target = "TIG055st2014.mailmaster.InboxActivity";
		        	MenuItem inboxIcon = (MenuItem)activity.findViewById(R.id.action_toInbox);
		        	activity.toInbox(inboxIcon);
		        	assertTrue(getStartedActivityIntent().getAction().equals(target));
		       }
		    });
	}
}

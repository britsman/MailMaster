package TIG055st2014.mailmaster.test.Activities;

import TIG055st2014.mailmaster.R;
import TIG055st2014.mailmaster.Activities.AccountSettingsActivity;
import TIG055st2014.mailmaster.Activities.AddAccountActivity;
import TIG055st2014.mailmaster.HelpClasses.AppVariablesSingleton;
import TIG055st2014.mailmaster.HelpClasses.Encryption;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.EditText;

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
 * This class tests adding accounts and navigating from the add account page.
 */
public class AddAccountActivityTest extends ActivityInstrumentationTestCase2<AddAccountActivity> {

	private AddAccountActivity activity;
	private String address;
	private Encryption encryption;

	public AddAccountActivityTest() {
		super(AddAccountActivity.class);
		encryption = new Encryption();
		address = "mailmastertesting@gmail.com";
	}
	@Override
	protected void setUp() throws Exception{
		super.setUp();
		setActivityInitialTouchMode(false);
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
		apv.setTesting(true);
		activity = getActivity();
	}
	/**
	 * This test tries to verify that after adding the first account, the application shall¨
	 * go to account settings and have exactly one account in its list. It also verifies that an 
	 * account with correct info is added in the first place.
	 */
	public void testAddNewAcc() {
		activity.runOnUiThread(new Runnable() {

			public void run() {
				EditText email = ((EditText)activity.findViewById(R.id.email));
				EditText pw = ((EditText)activity.findViewById(R.id.password));
				email.setText(address);
				pw.setText("mailmaster123");
				activity.accEdit.remove(address);
				activity.accEdit.commit();
				activity.add(null);
				activity.accEdit.clear();
				activity.accEdit.commit();
				activity.finish();
			}
		});
		ActivityMonitor monitor =
				getInstrumentation().
				addMonitor(AccountSettingsActivity.class.getName(), null, false);
		// wait 10 seconds max for the start of the activity
		AccountSettingsActivity startedActivity = (AccountSettingsActivity) monitor
				.waitForActivityWithTimeout(10000);
		assertTrue(startedActivity != null && startedActivity.columns.size() == 1);
		startedActivity.finish();
	}
	/**
	 * This test tries to verify that trying to add an account that has been saved previously
	 * shall cause the app to reject the account and not leave the add page.
	 */
	public void testUniqueAccounts() {
		Log.d("in uniquetest", "after wait");
		activity.runOnUiThread(new Runnable() {

			public void run() {
				EditText email = ((EditText)activity.findViewById(R.id.email));
				EditText pw = ((EditText)activity.findViewById(R.id.password));
				email.setText(address);
				pw.setText("mailmaster123");
				activity.accEdit.putString(address, encryption.encrypt("Some Key", "mailmaster123"));
				activity.accEdit.commit();
				activity.add(null);
				activity.accEdit.clear();
				activity.accEdit.commit();
				activity.finish();
			}
		});
		ActivityMonitor monitor =
				getInstrumentation().
				addMonitor(AccountSettingsActivity.class.getName(), null, false);
		// wait 5 seconds max for the start of the activity
		AccountSettingsActivity startedActivity = (AccountSettingsActivity) monitor
				.waitForActivityWithTimeout(5000);
		assertNull(startedActivity);
	}
	/**
	 * This test tries to press the add page's settings icon and verify that it leads
	 * to account settings.
	 */
	public void testToSettingsViaIcon() {
		ActivityMonitor monitor =
				getInstrumentation().
				addMonitor(AccountSettingsActivity.class.getName(), null, false);
		activity.toSettings(null);
		activity.finish();
		// wait 2 seconds for the start of the activity
		AccountSettingsActivity startedActivity = (AccountSettingsActivity) monitor
				.waitForActivityWithTimeout(2000);
		assertNotNull(startedActivity);
		startedActivity.finish();
	}
	public void tearDown() throws Exception{
		super.tearDown();
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
		apv.setTesting(false);
	}
}
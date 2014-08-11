package TIG055st2014.mailmaster.test;

import static org.junit.Assert.*;

import org.junit.Test;

import TIG055st2014.mailmaster.AccountSettingsActivity;
import TIG055st2014.mailmaster.AddAccountActivity;
import TIG055st2014.mailmaster.AppVariablesSingleton;
import TIG055st2014.mailmaster.Encryption;
import TIG055st2014.mailmaster.MailFunctionality;
import TIG055st2014.mailmaster.R;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

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
		startedActivity.toAdd(null);
	}
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
	public void testToSettingsViaIcon() {
		ActivityMonitor monitor =
				getInstrumentation().
				addMonitor(AccountSettingsActivity.class.getName(), null, false);
		activity.toSettings(null);
		activity.finish();
		// wait 2 seconds for the start of the activity
		AccountSettingsActivity startedActivity = (AccountSettingsActivity) monitor
				.waitForActivityWithTimeout(2000);
		startedActivity.toAdd(null);
		assertNotNull(startedActivity);
	}
	public void tearDown() throws Exception{
		super.tearDown();
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
		apv.setTesting(false);
	}
}
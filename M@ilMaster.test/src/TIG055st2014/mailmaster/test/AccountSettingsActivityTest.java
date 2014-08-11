package TIG055st2014.mailmaster.test;

import java.util.HashSet;
import java.util.Set;

import TIG055st2014.mailmaster.*;

import TIG055st2014.mailmaster.R;
import android.app.Instrumentation;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.UiThreadTest;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


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
		setActivityInitialTouchMode(false);
		activity = getActivity();
	}
	@UiThreadTest 
	public void testChangeDefault() {
		String adress = "mailmastertesting@gmail.com";
		activity.accEdit.clear();
		activity.accEdit.putInt("enabled", 0);
		activity.accEdit.putString(adress, encryption.encrypt(key, "mailmaster123"));
		activity.accEdit.commit();
		activity.columns.add(adress);
		activity.updateList();
		activity.onItemClick(null, null, 0, 0);
		String defAcc = "";
		for(String s : activity.accounts.getStringSet("default", new HashSet<String>())){
			defAcc = s;
			break;
		}
		Log.d(adress, defAcc);
		activity.accEdit.clear();
		activity.accEdit.commit();
		assertTrue(adress.equals(defAcc));
	}
	public void testToAdd() {
		ActivityMonitor monitor =
				getInstrumentation().
				addMonitor(AddAccountActivity.class.getName(), null, false);
		activity.toAdd(null);
		// wait 2 seconds for the start of the activity
		AddAccountActivity startedActivity = (AddAccountActivity) monitor
				.waitForActivityWithTimeout(2000);
		startedActivity.toSettings(null);
		assertNotNull(startedActivity);
	}
	public void testToInbox() {
		activity.runOnUiThread(new Runnable() {

			public void run() {
				String adress = "mailmastertesting@gmail.com";
				activity.accEdit.clear();
				activity.accEdit.putInt("enabled", 0);
				activity.accEdit.putString(adress, encryption.encrypt(key, "mailmaster123"));
				activity.accEdit.commit();
				activity.columns.add(adress);
				activity.updateList();
				activity.onItemClick(null, null, 0, 0);
			}
		});
		ActivityMonitor monitor =
				getInstrumentation().
				addMonitor(MailFolderActivity.class.getName(), null, false);
		MenuItem inboxIcon = activity.testMenu.findItem(R.id.action_toInbox);
		activity.toFolder(inboxIcon);
		MailFolderActivity startedActivity = (MailFolderActivity) monitor
				.waitForActivityWithTimeout(2000);
		activity.accEdit.clear();
		activity.accEdit.commit();
		assertNotNull(startedActivity);
		startedActivity.onClickSettings(null);
	}
	public void testIconHidden() {
		activity.accEdit.clear();
		activity.accEdit.commit();
		MenuItem folderIcon = activity.testMenu.findItem(R.id.action_folder);
		assertFalse(folderIcon.isEnabled());
	}
}

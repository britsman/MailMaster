package TIG055st2014.mailmaster.test.Activities;

import java.util.HashSet;
import java.util.Set;
import TIG055st2014.mailmaster.R;
import android.app.Instrumentation.ActivityMonitor;
import android.content.SharedPreferences;
import TIG055st2014.mailmaster.Activities.AccountSettingsActivity;
import TIG055st2014.mailmaster.Activities.AddAccountActivity;
import TIG055st2014.mailmaster.Activities.ComposeActivity;
import TIG055st2014.mailmaster.Activities.MailFolderActivity;
import TIG055st2014.mailmaster.Activities.ShowEmailActivity;
import TIG055st2014.mailmaster.HelpClasses.AppVariablesSingleton;
import TIG055st2014.mailmaster.HelpClasses.Encryption;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;

public class MailFolderActivityTest extends ActivityInstrumentationTestCase2<MailFolderActivity> {
	private MailFolderActivity activity;
	private SharedPreferences.Editor accEdit;
	private AppVariablesSingleton apv;
	private Encryption encryption;
	private Set<String> defAcc;

	public MailFolderActivityTest() {
		super(MailFolderActivity.class);
		defAcc = new HashSet<String>();
		defAcc.add("mailmastertesting@gmail.com");
		encryption = new Encryption();
	}
	protected void setUp() throws Exception {
		super.setUp();

		accEdit = getInstrumentation().getTargetContext().getSharedPreferences("StoredAccounts", 
				getInstrumentation().getTargetContext().MODE_PRIVATE).edit();
		apv = AppVariablesSingleton.getInstance();
		apv.setTesting(true);
		accEdit.putString("mailmastertesting@gmail.com", encryption.encrypt("Some Key", "mailmaster123"));
		accEdit.putInt("enabled", 1);
		accEdit.putStringSet("default", defAcc);
		accEdit.commit();
		activity = getActivity();
	}

	public void testComposePress() {
		ActivityMonitor monitor =
				getInstrumentation().
				addMonitor(ComposeActivity.class.getName(), null, false);
		activity.runOnUiThread(new Runnable() {

			public void run() {
				activity.onClickCompose(null);
				accEdit.clear();
				accEdit.commit();
			}
		});
		// wait 2 seconds for the start of the activity
		ComposeActivity startedActivity = (ComposeActivity) monitor
				.waitForActivityWithTimeout(2000);
		activity.finish();
		assertNotNull(startedActivity);
		startedActivity.finish();
	}
	public void testSelectEmail() {
		try {
			Thread.sleep(18000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ActivityMonitor monitor =
				getInstrumentation().
				addMonitor(ShowEmailActivity.class.getName(), null, false);
		activity.runOnUiThread(new Runnable() {

			public void run() {
				activity.onItemClick(null, null, 0, 0);
			}
		});
		ShowEmailActivity startedActivity = (ShowEmailActivity) monitor
				.waitForActivityWithTimeout(5000);
		assertNotNull(startedActivity);
		
		accEdit.clear();
		accEdit.commit();
		activity.finish();
		startedActivity.finish();
	}
	public void testIsSorted() {
		try {
			Thread.sleep(18000L);
			assertTrue(activity.emails.get(0).getReceivedDate()
					.after(activity.emails.get(1).getReceivedDate()));
			accEdit.clear();
			accEdit.commit();
			activity.finish();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Should not be reached.");
		}
	}
	public void testSettingPress() {
		ActivityMonitor monitor =
				getInstrumentation().
				addMonitor(AccountSettingsActivity.class.getName(), null, false);
		activity.runOnUiThread(new Runnable() {

			public void run() {
				activity.onClickSettings(null);
				accEdit.clear();
				accEdit.commit();
			}
		});
		// wait 2 seconds for the start of the activity
		AccountSettingsActivity startedActivity = (AccountSettingsActivity) monitor
				.waitForActivityWithTimeout(2000);
		activity.finish();
		assertNotNull(startedActivity);
		startedActivity.finish();
	}
	public void testNoDefaultAcc() {
		ActivityMonitor monitor =
				getInstrumentation().
				addMonitor(AddAccountActivity.class.getName(), null, false);
		activity.runOnUiThread(new Runnable() {
			public void run() {
				accEdit.remove("default");
				accEdit.commit();
				getInstrumentation().callActivityOnCreate(activity, null);
			}
		});
		AddAccountActivity startedActivity = (AddAccountActivity) monitor
				.waitForActivityWithTimeout(5000);
		assertNotNull(startedActivity);
		startedActivity.finish();
		accEdit.clear();
		accEdit.commit();
		activity.finish();
	}
	@UiThreadTest
	public void testChangeFolder() {
		activity.changeFolder(activity.testMenu.findItem(R.id.action_drafts));
		assertTrue(apv.getFolderName("mailmastertesting@gmail.com").contains("Drafts"));
		activity.changeFolder(activity.testMenu.findItem(R.id.action_inbox));
		accEdit.clear();
		accEdit.commit();
		activity.finish();
	}
	public void tearDown() throws Exception{
		super.tearDown();
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
		apv.setTesting(false);
	}
}

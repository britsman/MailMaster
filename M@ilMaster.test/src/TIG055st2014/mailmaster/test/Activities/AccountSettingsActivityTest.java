package TIG055st2014.mailmaster.test.Activities;

import java.util.HashSet;

import TIG055st2014.mailmaster.R;
import TIG055st2014.mailmaster.Activities.AccountSettingsActivity;
import TIG055st2014.mailmaster.Activities.AddAccountActivity;
import TIG055st2014.mailmaster.Activities.MailFolderActivity;
import TIG055st2014.mailmaster.HelpClasses.AppVariablesSingleton;
import TIG055st2014.mailmaster.HelpClasses.Encryption;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.util.Log;
import android.view.MenuItem;

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
 * This class tests interacting with interface components on the account settings page,
 * as well as testing to successfully navigate to other pages from this page.
 */
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
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
		apv.setTesting(true);
		activity = getActivity();
	}
	/**
	 * This test attempts to set the testing account as active. simulating that it was
	 * clicked shall result in it being written to the sharedpreferences. 
	 */
	@UiThreadTest 
	public void testSetAsActive() {
		String adress = "mailmastertesting@gmail.com";
		activity.accEdit.clear();
		activity.accEdit.putInt("enabled", 0);
		activity.accEdit.putString(adress, encryption.encrypt(key, "mailmaster123"));
		activity.accEdit.commit();
		activity.columns.add(adress);
		activity.updateList();
		activity.onItemClick(null, null, 0, 0);
		String activeAcc = "";
		for(String s : activity.accounts.getStringSet("default", new HashSet<String>())){
			activeAcc = s;
			break;
		}
		Log.d(adress, activeAcc);
		activity.accEdit.clear();
		activity.accEdit.commit();
		assertTrue(adress.equals(activeAcc));
	}
	/**
	 * This test attempts to press the icon that redirects to AddAccountActivity.
	 */
	public void testToAdd() {
		ActivityMonitor monitor =
				getInstrumentation().
				addMonitor(AddAccountActivity.class.getName(), null, false);
		getInstrumentation().runOnMainSync(new Runnable() {

			public void run() {
		activity.toAdd(null);;
			}
		});
		getInstrumentation().waitForIdleSync();
		// wait 2 seconds for the start of the activity
		AddAccountActivity startedActivity = (AddAccountActivity) monitor
				.waitForActivityWithTimeout(10000);
		activity.finish();
		assertNotNull(startedActivity);
		startedActivity.finish();
	}
	/**
	 * This test attempts to press the icon that redirects to MailFolderActivity.
	 */
	public void testToInbox() {
		ActivityMonitor monitor =
				getInstrumentation().
				addMonitor(MailFolderActivity.class.getName(), null, false);
		getInstrumentation().runOnMainSync(new Runnable() {

			public void run() {
				String adress = "mailmastertesting@gmail.com";
				activity.accEdit.clear();
				activity.accEdit.putInt("enabled", 0);
				activity.accEdit.putString(adress, encryption.encrypt(key, "mailmaster123"));
				activity.accEdit.commit();
				activity.columns.add(adress);
				activity.updateList();
				activity.onItemClick(null, null, 0, 0);
				MenuItem inboxIcon = activity.testMenu.findItem(R.id.action_toInbox);
				activity.toFolder(inboxIcon);
			}
		});
		getInstrumentation().waitForIdleSync();
		MailFolderActivity startedActivity = (MailFolderActivity) monitor
				.waitForActivityWithTimeout(2000);
		activity.accEdit.clear();
		activity.accEdit.commit();
		activity.finish();
		assertNotNull(startedActivity);
		startedActivity.finish();
	}
	/**
	 * This test tries to verify that the folder icon is hidden when no accounts are
	 * marked as active.
	 */
	public void testIconHidden() {
		activity.accEdit.clear();
		activity.accEdit.commit();
		MenuItem folderIcon = activity.testMenu.findItem(R.id.action_folder);
		assertFalse(folderIcon.isEnabled());
	}
	public void tearDown() throws Exception{
		super.tearDown();
		AppVariablesSingleton apv = AppVariablesSingleton.getInstance();
		apv.setTesting(false);
	}
}

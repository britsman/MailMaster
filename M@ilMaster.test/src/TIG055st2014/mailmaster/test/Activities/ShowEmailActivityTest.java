package TIG055st2014.mailmaster.test.Activities;

import javax.mail.Message;
import TIG055st2014.mailmaster.R;
import TIG055st2014.mailmaster.Activities.AttachmentsActivity;
import TIG055st2014.mailmaster.Activities.ComposeActivity;
import TIG055st2014.mailmaster.Activities.ShowEmailActivity;
import TIG055st2014.mailmaster.HelpClasses.AppVariablesSingleton;
import TIG055st2014.mailmaster.HelpClasses.MailFunctionality;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.view.MenuItem;

/* M@ilMaster Multi-Account Email Client
Copyright (C) 2014 Eric Britsman & Khaled Alnawasreh
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General 
Public License as published by the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the 
implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License 
for more details. You should have received a copy of the GNU General Public License along with this program.  
If not, see <http://www.gnu.org/licenses/>.

Contact Info: eric_britsman@hotmail.com / khaled.nawasreh@gmail.com
*/

/**
 * This class tests navigating from the showemail contents page.
 */
public class ShowEmailActivityTest extends  ActivityInstrumentationTestCase2<ShowEmailActivity>  {
	private ShowEmailActivity activity;
	private MailFunctionality mf;
	private AppVariablesSingleton apv;
	private String account;
	
	public ShowEmailActivityTest() {
		super(ShowEmailActivity.class);
		account = "mailmastertesting@gmail.com";
		apv = AppVariablesSingleton.getInstance();
		apv.initAccounts();
		apv.resetLists();
		mf = new MailFunctionality(account, "mailmaster123", "gmail.com");
		apv.setFolderName(account, "INBOX");
		Message m = mf.getFolderTest().get(0);
		apv.setEmail(m);		
	}

	protected void setUp() throws Exception{
		super.setUp();
		apv = AppVariablesSingleton.getInstance();
		apv.setAccount(account);
		apv.setTesting(true);
		activity = getActivity();
	}
	/**
	 * This test tries to press the reply icon and verify that it takes you to
	 * the compose page.
	 */
	public void testReplyPress() {
		ActivityMonitor monitor =
				getInstrumentation().
				addMonitor(ComposeActivity.class.getName(), null, false);
		  activity.runOnUiThread(new Runnable() {

		        public void run() {
					MenuItem replyIcon = activity.testMenu.findItem(R.id.action_reply);
					activity.onOptionsItemSelected(replyIcon);
					activity.finish();
		       }
		    });
			ComposeActivity startedActivity = (ComposeActivity) monitor
					.waitForActivityWithTimeout(5000); 
			assertNotNull(startedActivity);
			startedActivity.finish();
	}
	/**
	 * This test tries to press the attachments icon and verify that it takes you to
	 * the attachments page.
	 */
	public void testAttachPress() {
		ActivityMonitor monitor =
				getInstrumentation().
				addMonitor(AttachmentsActivity.class.getName(), null, false);
		  activity.runOnUiThread(new Runnable() {

		        public void run() {
					MenuItem attachIcon = activity.testMenu.findItem(R.id.get_attachments);
					activity.onOptionsItemSelected(attachIcon);
					activity.finish();
		       }
		    });
			final AttachmentsActivity startedActivity = (AttachmentsActivity) monitor
					.waitForActivityWithTimeout(5000); 
			assertNotNull(startedActivity);
			startedActivity.finish();
	}
	@Override
	public void tearDown() throws Exception{
		super.tearDown();
		apv = AppVariablesSingleton.getInstance();
		apv.resetLists();
		apv.initAccounts();
		apv.setAccount(null);
		apv.setTesting(false);
	}
}

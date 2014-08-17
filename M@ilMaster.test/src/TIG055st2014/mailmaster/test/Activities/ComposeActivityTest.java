package TIG055st2014.mailmaster.test.Activities;

import javax.mail.Message;
import TIG055st2014.mailmaster.R;
import TIG055st2014.mailmaster.Activities.ComposeActivity;
import TIG055st2014.mailmaster.Activities.MailFolderActivity;
import TIG055st2014.mailmaster.HelpClasses.AppVariablesSingleton;
import TIG055st2014.mailmaster.HelpClasses.MailFunctionality;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

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
 * This class tests that ComposeActivity constructs replies/new messages as expected, and also
 * checks that sending will not trigger if the message to consturct is not in an acceptable
 * state.
 */
public class ComposeActivityTest extends ActivityInstrumentationTestCase2<ComposeActivity> {

	private ComposeActivity activity;
	private MailFunctionality mf;
	private AppVariablesSingleton apv;

	public ComposeActivityTest() {
		super(ComposeActivity.class);
		apv = AppVariablesSingleton.getInstance();
		apv.initAccounts();
		apv.setAccount("mailmastertesting@gmail.com");
		apv.setTesting(true);
		mf = new MailFunctionality("mailmastertesting@gmail.com", "mailmaster123", "gmail.com");
		Message m = mf.getFolderTest().get(0);
		apv.setEmail(m);
		apv.setReply(mf.getTestReply(m));
	}
	@Override
	protected void setUp() throws Exception{
		super.setUp();
		apv = AppVariablesSingleton.getInstance();
	}
	/**
	 * This test attempts to verify that if message is labeled as a reply, the correct subject
	 * should be automatically filled in.
	 */
	public void testReply() {

		apv.setIsReply(true);
		//Sometimes the finish() in testCompose does not finish quickly enough.
		try {
			Thread.sleep(5000L);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		activity = getActivity();
		activity.runOnUiThread(new Runnable() {

			public void run() {
				try{
					TextView subject = (TextView) activity.findViewById(R.id.subjectReply);
					assertTrue(subject.getText().toString().equals(apv.getEmail().getSubject()) ||
							subject.getText().toString().equalsIgnoreCase("RE: " + apv.getEmail().getSubject()));
					activity.finish();
				}
				catch(Exception e){
					e.printStackTrace();
					fail("This should not be reached.");
				}
			}
		});
	}
	/**
	 * This test attempts to verify that fields are empty on a new message.
	 */
	@UiThreadTest
	public void testCompose() {

		apv.setIsReply(false);
		activity = getActivity();
				try{
					MultiAutoCompleteTextView recipients = (MultiAutoCompleteTextView) activity.findViewById(R.id.receiveAccs);
					EditText subject = (EditText) activity.findViewById(R.id.subject);
					EditText body = (EditText) activity.findViewById(R.id.body);
					assertTrue(recipients.getText().toString().equals("") && subject.getText()
							.toString().equals("") && body.getText().toString().equals(""));
					activity.finish();
				}
				catch(Exception e){
					e.printStackTrace();
					fail("This should not be reached.");
				}
	}
	/**
	 * This test attempts to verify that pressing send after starting on an new message should
	 * not send it (since fields are unfilled).
	 */
	public void testSendWithEmptyFields() {

		apv.setIsReply(false);
		activity = getActivity();
		ActivityMonitor monitor =
				getInstrumentation().
				addMonitor(MailFolderActivity.class.getName(), null, false);
		activity.runOnUiThread(new Runnable() {

			public void run() {
				try{
					activity.onClickSend(null);
					activity.finish();
				}
				catch(Exception e){
					e.printStackTrace();
					fail("This should not be reached.");
				}
			}
		});
		// wait 2 seconds for the start of the activity
		MailFolderActivity startedActivity = (MailFolderActivity) monitor
				.waitForActivityWithTimeout(2000);
		assertNull(startedActivity);
	}
	/**
	 * This test attempts to verify that pressing send will not send the message if
	 * total size of attachments is too large.
	 */
	public void testSendWithTooLargeSize() {

		apv.setIsReply(true);
		activity = getActivity();
		ActivityMonitor monitor =
				getInstrumentation().
				addMonitor(MailFolderActivity.class.getName(), null, false);
		activity.runOnUiThread(new Runnable() {

			public void run() {
				try{
					activity.sizeEdit.putFloat("total", 99999);
					activity.sizeEdit.commit();
					activity.onClickSend(null);
					activity.sizeEdit.clear();
					activity.sizeEdit.commit();
					activity.finish();
				}
				catch(Exception e){
					e.printStackTrace();
					fail("This should not be reached.");
				}
			}
		});
		// wait 2 seconds for the start of the activity
		MailFolderActivity startedActivity = (MailFolderActivity) monitor
				.waitForActivityWithTimeout(2000);
		assertNull(startedActivity);
	}
	@Override
	public void tearDown() throws Exception{
		super.tearDown();
	}
}
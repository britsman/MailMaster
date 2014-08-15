package TIG055st2014.mailmaster.test.Activities;

import javax.mail.Message;

import TIG055st2014.mailmaster.R;
import TIG055st2014.mailmaster.Activities.AttachmentsActivity;
import TIG055st2014.mailmaster.HelpClasses.AppVariablesSingleton;
import TIG055st2014.mailmaster.HelpClasses.MailFunctionality;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.util.Log;

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
 * This class tests how attachmentsactivity reacts to the presence/lack of attachments.
 */
public class AttachmentsActivityTest extends ActivityInstrumentationTestCase2<AttachmentsActivity> {

	private AttachmentsActivity activity;
	private AppVariablesSingleton apv;
	private MailFunctionality mf;

	public AttachmentsActivityTest() {
		super(AttachmentsActivity.class);
		apv = AppVariablesSingleton.getInstance();
		apv.initAccounts();
		mf = new MailFunctionality("mailmastertesting@gmail.com", "mailmaster123", "gmail.com");
		Message m = mf.getFolderTest().get(0);
		apv.setEmail(m);
	}
	@Override
	protected void setUp() throws Exception{
		super.setUp();
		apv = AppVariablesSingleton.getInstance();
		apv.resetLists();
		apv.addAttachment("FilePath");
		activity = getActivity();
	}
	/**
	 * This test tries to verify that the UI will inform the user if the email has no attachments
	 * to download.
	 */
	@UiThreadTest
	public void testNoAttachments() {
		apv.resetLists();
		getInstrumentation().callActivityOnCreate(activity, null);
		Log.d("No Attachment", activity.fileNames.get(0));
		assertTrue(activity.fileNames.get(0).equals(activity.getResources().getString(R.string.no_attachments)));
	}
	/**
	 * This test tries to verify that the the test email's attachment is added as an item on the attachments
	 * list.
	 */
	public void testAttachments() {
		assertTrue(activity.fileNames.get(0).equals("FilePath"));
	}
	@Override
	public void tearDown() throws Exception{
		super.tearDown();
	}
}
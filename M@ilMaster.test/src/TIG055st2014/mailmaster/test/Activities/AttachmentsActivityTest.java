package TIG055st2014.mailmaster.test.Activities;

import javax.mail.Message;

import TIG055st2014.mailmaster.R;
import TIG055st2014.mailmaster.Activities.AttachmentsActivity;
import TIG055st2014.mailmaster.HelpClasses.AppVariablesSingleton;
import TIG055st2014.mailmaster.HelpClasses.MailFunctionality;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.util.Log;

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
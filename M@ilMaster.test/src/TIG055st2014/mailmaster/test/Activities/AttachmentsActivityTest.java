package TIG055st2014.mailmaster.test.Activities;

import javax.mail.Message;
import TIG055st2014.mailmaster.Activities.AttachmentsActivity;
import TIG055st2014.mailmaster.HelpClasses.AppVariablesSingleton;
import TIG055st2014.mailmaster.HelpClasses.MailFunctionality;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.util.Log;

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
	@UiThreadTest
	public void testNoAttachments() {
		apv.resetLists();
		getInstrumentation().callActivityOnCreate(activity, null);
		Log.d("No Attachment", activity.fileNames.get(0));
		assertTrue(activity.fileNames.get(0).equals("This email contains no attachments"));
	}
	public void testAttachments() {
		Log.d("Has Attachment", apv.getFileNames().get(0));
		Log.d("Has Attachment", activity.fileNames.get(0));
		assertTrue(activity.fileNames.get(0).equals("FilePath"));
	}
	@Override
	public void tearDown() throws Exception{
		super.tearDown();
	}
}
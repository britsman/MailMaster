package TIG055st2014.mailmaster.test;
import static org.junit.Assert.*;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;

import org.junit.Test;

import TIG055st2014.mailmaster.MailFolderActivity;
import TIG055st2014.mailmaster.R;
import TIG055st2014.mailmaster.AccountSettingsActivity;
import TIG055st2014.mailmaster.ComposeActivity;
import TIG055st2014.mailmaster.AppVariablesSingleton;
import TIG055st2014.mailmaster.MailFunctionality;
import android.app.Instrumentation.ActivityMonitor;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.InstrumentationTestCase;
import android.test.UiThreadTest;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;


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
	public void testReply() {

		apv.setIsReply(true);
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
	@UiThreadTest
	public void testCompose() {

		apv.setIsReply(false);
		activity = getActivity();
				try{
					EditText subject = (EditText) activity.findViewById(R.id.subject);
					assertTrue(subject.getText().toString().equals(""));
					activity.finish();
				}
				catch(Exception e){
					e.printStackTrace();
					fail("This should not be reached.");
				}
	}
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
	public void testSendWithToLargeSize() {

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
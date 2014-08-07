package TIG055st2014.mailmaster.test;

import static org.junit.Assert.*;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;

import org.junit.Test;

import TIG055st2014.mailmaster.AccountSettingsActivity;
import TIG055st2014.mailmaster.AttachmentsActivity;
import TIG055st2014.mailmaster.AppVariablesSingleton;
import TIG055st2014.mailmaster.MailFunctionality;
import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.InstrumentationTestCase;

public class AttachmentsActivityTest extends ActivityInstrumentationTestCase2<AttachmentsActivity> {

	private AttachmentsActivity activity;
	private AppVariablesSingleton d;
	private MailFunctionality mf;
	
	public AttachmentsActivityTest() {
		super(AttachmentsActivity.class);
		d = AppVariablesSingleton.getInstance();
		d.initAccounts();
		mf = new MailFunctionality("mailmastertesting@gmail.com", "mailmaster123", "gmail.com");
		Message m = mf.getFolderTest().get(0);
		d.setEmail(m);
	}
	@Override
	protected void setUp() throws Exception{
		super.setUp();
		activity = getActivity();
	}

	public void testNoAttachments() {
		  new Thread(new Runnable() {

		        public void run() {
		        		d.resetLists();
		        		getInstrumentation().callActivityOnCreate(activity, null);
		        		assertTrue(activity.fileNames.get(0).equals("This email contains no attachments"));
			       }
			    });
	}
	public void testAttachments() {
		  new Thread(new Runnable() {

		        public void run() {
		        		d.resetLists();
		        		d.addAttachment("FilePath");
		        		getInstrumentation().callActivityOnCreate(activity, null);
		        		assertTrue(activity.fileNames.get(0).equals("FilePath"));
			       }
			    });
	}
}
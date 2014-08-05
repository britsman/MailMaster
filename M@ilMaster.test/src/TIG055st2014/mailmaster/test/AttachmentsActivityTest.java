package TIG055st2014.mailmaster.test;

import static org.junit.Assert.*;

import org.junit.Test;

import TIG055st2014.mailmaster.AccountSettingsActivity;
import TIG055st2014.mailmaster.AttachmentsActivity;
import TIG055st2014.mailmaster.AppVariablesSingleton;
import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.InstrumentationTestCase;

public class AttachmentsActivityTest extends ActivityInstrumentationTestCase2<AttachmentsActivity> {

	private AttachmentsActivity activity;
	private AppVariablesSingleton d;
	
	public AttachmentsActivityTest() {
		super(AttachmentsActivity.class);
	}
	@Override
	protected void setUp() throws Exception{
		super.setUp();
		d = AppVariablesSingleton.getInstance();
		d.setFolderName("INBOX");
		activity = getActivity();
	}

	public void testNoAttachments() {
		  activity.runOnUiThread(new Runnable() {

		        public void run() {
		        		d.setEmail(null);
		        		getInstrumentation().callActivityOnCreate(activity, null);
		        		assertTrue(activity.fileNames.get(0).equals("This email contains no attachments"));
			       }
			    });
	}
	public void testAttachments() {
		  activity.runOnUiThread(new Runnable() {

		        public void run() {
		        		d.setEmail(null);
		        		d.addAttachment("FilePath");
		        		getInstrumentation().callActivityOnCreate(activity, null);
		        		assertTrue(activity.fileNames.get(0).equals("FilePath"));
			       }
			    });
	}
}
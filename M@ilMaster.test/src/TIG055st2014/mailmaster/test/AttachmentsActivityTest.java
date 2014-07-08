package TIG055st2014.mailmaster.test;

import static org.junit.Assert.*;

import org.junit.Test;

import TIG055st2014.mailmaster.AccountSettingsActivity;
import TIG055st2014.mailmaster.AttachmentsActivity;
import TIG055st2014.mailmaster.DisplayEmail;
import android.content.Context;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.InstrumentationTestCase;

public class AttachmentsActivityTest extends ActivityInstrumentationTestCase2<AttachmentsActivity> {

	private AttachmentsActivity activity;
	private DisplayEmail d;
	
	public AttachmentsActivityTest() {
		super(AttachmentsActivity.class);
	}
	@Override
	protected void setUp() throws Exception{
		super.setUp();
		d = DisplayEmail.getInstance();
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
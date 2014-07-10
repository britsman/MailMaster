package TIG055st2014.mailmaster.test;
import static org.junit.Assert.*;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;

import org.junit.Test;

import TIG055st2014.mailmaster.R;
import TIG055st2014.mailmaster.AccountSettingsActivity;
import TIG055st2014.mailmaster.ComposeActivity;
import TIG055st2014.mailmaster.DisplayEmail;
import TIG055st2014.mailmaster.MailFunctionality;
import android.content.Intent;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ActivityUnitTestCase;
import android.test.InstrumentationTestCase;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;


public class ComposeActivityTest extends ActivityUnitTestCase<ComposeActivity> {

	private ComposeActivity activity;
	private MailFunctionality mf;
	private DisplayEmail d;
	
	public ComposeActivityTest() {
		super(ComposeActivity.class);
		mf = new MailFunctionality("mailmastertesting@gmail.com", "mailmaster123", "gmail.com");
		d = DisplayEmail.getInstance();
		Message m = mf.getInbox().get(0);
		d.setEmail(m);
		d.setReply(mf.getReply(m));
	}
	@Override
	protected void setUp() throws Exception{
		super.setUp();
		Intent intent = new Intent(getInstrumentation().getTargetContext(),
		        ComposeActivity.class);
		startActivity(intent, null, null);
		activity = getActivity();
	}
	public void testReply() {
		  activity.runOnUiThread(new Runnable() {

		        public void run() {
		        	try{
		        		d.setIsReply(true);
		        		getInstrumentation().callActivityOnCreate(activity, null);
		        		getInstrumentation().callActivityOnStart(activity);
		        		TextView subject = (TextView) activity.findViewById(R.id.subjectReply);
		        		Log.d("Testing", subject.getText().toString());
		        		Log.d("Testing", d.getReply().getSubject());
		        		assertTrue(subject.getText().toString().equals(d.getEmail().getSubject()) ||
		        				subject.getText().toString().equalsIgnoreCase("RE: " + d.getEmail().getSubject()));
		        	}
		        	catch(Exception e){
		        		e.printStackTrace();
		        		fail("This should not be reached.");
		        	}
			       }
			    });
	}
	public void testCompose() {
		  activity.runOnUiThread(new Runnable() {

		        public void run() {

	        		try{
		        		d.setIsReply(false);
		        		getInstrumentation().callActivityOnCreate(activity, null);
		        		getInstrumentation().callActivityOnStart(activity);
		        		EditText subject = (EditText) activity.findViewById(R.id.subject);
		        		assertTrue(subject.getText().toString().equals(""));
		        	}
		        	catch(Exception e){
		        		e.printStackTrace();
		        		fail("This should not be reached.");
		        	}
			       }
			    });
	}
}
package TIG055st2014.mailmaster.test;

import static org.junit.Assert.*;

import javax.mail.Message;

import org.junit.Test;

import TIG055st2014.mailmaster.DisplayEmail;
import TIG055st2014.mailmaster.InboxActivity;
import TIG055st2014.mailmaster.MailFunctionality;
import TIG055st2014.mailmaster.R;
import TIG055st2014.mailmaster.ShowEmailActivity;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.InstrumentationTestCase;
import android.util.Log;
import android.view.MenuItem;

public class ShowEmailActivityTest extends  ActivityUnitTestCase<ShowEmailActivity>  {
	private ShowEmailActivity activity;
	private MailFunctionality mf;
	private DisplayEmail d;
	public ShowEmailActivityTest() {
		super(ShowEmailActivity.class);
		mf = new MailFunctionality("mailmastertesting@gmail.com", "mailmaster123", "gmail.com");
		d = DisplayEmail.getInstance();
		d.setFolderName("INBOX");
		Message m = mf.getInbox().get(0);
		d.setEmail(m);
		
		
		
	}

	protected void setUp() throws Exception{
		super.setUp();
	
	}
	public void testReplyPress() {
		  new Thread(new Runnable() {

		        public void run() {
		        	Intent intent = new Intent(getInstrumentation().getTargetContext(),
		    				ShowEmailActivity.class);
		    		startActivity(intent, null, null);
		    		activity = getActivity();
		        	String target = "TIG055st2014.mailmaster.ComposeActivity";
					MenuItem replyIcon = (MenuItem) activity.findViewById(R.id.action_reply);
					Log.d("test", replyIcon.getItemId() + "");
					activity.onOptionsItemSelected(replyIcon);
					assertTrue(getStartedActivityIntent().getAction().equals(target));
					
		       }
		    });
	}
	
	public void testAttachPress() {
		  new Thread(new Runnable() {

		        public void run() {
		        	Intent intent = new Intent(getInstrumentation().getTargetContext(),
		    				ShowEmailActivity.class);
		    		startActivity(intent, null, null);
		    		activity = getActivity();
		        	String target = "TIG055st2014.mailmaster.AttachmentsActivity";
		        	MenuItem attachIcon = (MenuItem) activity.findViewById(R.id.get_attachments);
		        	activity.onOptionsItemSelected(attachIcon);
		        	assertTrue(getStartedActivityIntent().getAction().equals(target));
		       }
		    });
	}
	
}

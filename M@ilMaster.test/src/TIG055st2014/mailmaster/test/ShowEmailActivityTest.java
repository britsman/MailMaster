package TIG055st2014.mailmaster.test;

import static org.junit.Assert.*;

import org.junit.Test;

import TIG055st2014.mailmaster.InboxActivity;
import TIG055st2014.mailmaster.R;
import TIG055st2014.mailmaster.ShowEmailActivity;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.InstrumentationTestCase;
import android.view.MenuItem;

public class ShowEmailActivityTest extends  ActivityUnitTestCase<ShowEmailActivity>  {
	private ShowEmailActivity activity;
	public ShowEmailActivityTest() {
		super(ShowEmailActivity.class);
		
	}

	protected void setUp() throws Exception{
		super.setUp();
		Intent intent = new Intent(getInstrumentation().getTargetContext(),
				ShowEmailActivity.class);
		startActivity(intent, null, null);
		activity = getActivity();
	}


	public void testReplyPress() {
		  activity.runOnUiThread(new Runnable() {

		        public void run() {
		        	
		        	String target = "TIG055st2014.mailmaster.ComposeActivity";
					MenuItem replyIcon = (MenuItem) activity
							.findViewById(R.id.action_reply);
					activity.onOptionsItemSelected(replyIcon);
					assertTrue(getStartedActivityIntent().getAction().equals(target));
					
		       }
		    });
	}
	
	public void testAttachPress() {
		  activity.runOnUiThread(new Runnable() {

		        public void run() {
		        	String target = "TIG055st2014.mailmaster.AttachmentsActivity";
		        	MenuItem attachIcon = (MenuItem) activity.findViewById(R.id.get_attachments);
		        	activity.onOptionsItemSelected(attachIcon);
		        	assertTrue(getStartedActivityIntent().getAction().equals(target));
		       }
		    });
	}
	
}

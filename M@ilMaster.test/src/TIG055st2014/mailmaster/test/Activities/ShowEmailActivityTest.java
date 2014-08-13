package TIG055st2014.mailmaster.test.Activities;

import javax.mail.Message;
import TIG055st2014.mailmaster.R;
import TIG055st2014.mailmaster.Activities.AttachmentsActivity;
import TIG055st2014.mailmaster.Activities.ComposeActivity;
import TIG055st2014.mailmaster.Activities.ShowEmailActivity;
import TIG055st2014.mailmaster.HelpClasses.AppVariablesSingleton;
import TIG055st2014.mailmaster.HelpClasses.MailFunctionality;
import android.app.Instrumentation.ActivityMonitor;
import android.test.ActivityInstrumentationTestCase2;
import android.view.MenuItem;

public class ShowEmailActivityTest extends  ActivityInstrumentationTestCase2<ShowEmailActivity>  {
	private ShowEmailActivity activity;
	private MailFunctionality mf;
	private AppVariablesSingleton apv;
	private String account;
	
	public ShowEmailActivityTest() {
		super(ShowEmailActivity.class);
		account = "mailmastertesting@gmail.com";
		apv = AppVariablesSingleton.getInstance();
		apv.initAccounts();
		apv.resetLists();
		mf = new MailFunctionality(account, "mailmaster123", "gmail.com");
		apv.setFolderName(account, "INBOX");
		Message m = mf.getFolderTest().get(0);
		apv.setEmail(m);		
	}

	protected void setUp() throws Exception{
		super.setUp();
		apv = AppVariablesSingleton.getInstance();
		apv.setAccount(account);
		apv.setTesting(true);
		activity = getActivity();
	}
	public void testReplyPress() {
		ActivityMonitor monitor =
				getInstrumentation().
				addMonitor(ComposeActivity.class.getName(), null, false);
		  activity.runOnUiThread(new Runnable() {

		        public void run() {
					MenuItem replyIcon = activity.testMenu.findItem(R.id.action_reply);
					activity.onOptionsItemSelected(replyIcon);
		       }
		    });
			ComposeActivity startedActivity = (ComposeActivity) monitor
					.waitForActivityWithTimeout(10000); 
			assertNotNull(startedActivity);
	}
	
	public void testAttachPress() {
		ActivityMonitor monitor =
				getInstrumentation().
				addMonitor(AttachmentsActivity.class.getName(), null, false);
		  activity.runOnUiThread(new Runnable() {

		        public void run() {
					MenuItem attachIcon = activity.testMenu.findItem(R.id.get_attachments);
					activity.onOptionsItemSelected(attachIcon);
		       }
		    });
			final AttachmentsActivity startedActivity = (AttachmentsActivity) monitor
					.waitForActivityWithTimeout(10000); 
			assertNotNull(startedActivity);
			  activity.runOnUiThread(new Runnable() {

			        public void run() {
			startedActivity.onBackPressed();
				       }
			    });
	}
	@Override
	public void tearDown() throws Exception{
		super.tearDown();
		apv = AppVariablesSingleton.getInstance();
		apv.resetLists();
		apv.initAccounts();
		apv.setAccount(null);
		apv.setTesting(false);
	}
}

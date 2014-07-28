package TIG055st2014.mailmaster.test;

import static org.junit.Assert.*;

import org.junit.Test;

import TIG055st2014.mailmaster.AddAccountActivity;
import TIG055st2014.mailmaster.R;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

public class AddAccountActivityTest extends ActivityUnitTestCase<AddAccountActivity> {

	private AddAccountActivity activity;
	
	public AddAccountActivityTest() {
		super(AddAccountActivity.class);
	}
	@Override
	protected void setUp() throws Exception{
		super.setUp();
		Intent intent = new Intent(getInstrumentation().getTargetContext(),
				AddAccountActivity.class);
		startActivity(intent, null, null);
		activity = getActivity();
	}

	public void testChangeDefault() {
		  activity.runOnUiThread(new Runnable() {

		        public void run() {
		        	String adress = "mailmastertesting@gmail.com";
		        	Button add = (Button) activity.findViewById(R.id.button);
		            EditText email = ((EditText)activity.findViewById(R.id.email));
		            EditText pw = ((EditText)activity.findViewById(R.id.password));
		            email.setText(adress);
		            pw.setText("mailmaster123");
		            activity.accEdit.remove(adress);
		            activity.accEdit.commit();
		            int prevSize = activity.accounts.getAll().keySet().size();
		        	add.performClick();
		        	assertTrue(activity.accounts.getString("default", "").equals(adress) &&
		        			   activity.accounts.getAll().keySet().size() == prevSize+1);
		       }
		    });
	}
	public void testAccountExists() {
		  activity.runOnUiThread(new Runnable() {

		        public void run() {
		        	String adress = "mailmastertesting@gmail.com";
		        	Button add = (Button) activity.findViewById(R.id.button);
		            EditText email = ((EditText)activity.findViewById(R.id.email));
		            EditText pw = ((EditText)activity.findViewById(R.id.password));
		            email.setText(adress);
		            pw.setText("mailmaster123");
		            activity.accEdit.remove(adress);
		            activity.accEdit.commit();
		            int prevSize = activity.accounts.getAll().keySet().size();
		        	add.performClick();
		        	add.performClick();
		        	assertTrue(activity.accounts.getString("default", "").equals(adress) &&
		        			   activity.accounts.getAll().keySet().size() == prevSize+1);
		       }
		    });
	}
	public void testToSettingsViaAdd() {
		  activity.runOnUiThread(new Runnable() {

		        public void run() {
		        	String target = "TIG055st2014.mailmaster.AccountSettingsActivity";
		        	String adress = "mailmastertesting@gmail.com";
		        	Button add = (Button) activity.findViewById(R.id.button);
		            EditText email = ((EditText)activity.findViewById(R.id.email));
		            EditText pw = ((EditText)activity.findViewById(R.id.password));
		            email.setText(adress);
		            pw.setText("mailmaster123");
		            activity.accEdit.remove(adress);
		            activity.accEdit.commit();
		        	add.performClick();
		        	assertTrue(getStartedActivityIntent().getAction().equals(target));
		       }
		    });
	}
	public void testToSettingsViaIcon() {
		  activity.runOnUiThread(new Runnable() {

		        public void run() {
		        	String target = "TIG055st2014.mailmaster.AccountSettingsActivity";
		        	MenuItem settingsIcon = (MenuItem) activity.findViewById(R.id.action_settings);
		        	activity.toSettings(settingsIcon);
		        	assertTrue(getStartedActivityIntent().getAction().equals(target));
		       }
		    });
	}

}
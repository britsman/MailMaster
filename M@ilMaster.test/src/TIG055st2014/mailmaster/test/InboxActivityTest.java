package TIG055st2014.mailmaster.test;

import TIG055st2014.mailmaster.AddAccountActivity;
import static org.junit.Assert.*;

import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.Test;
import TIG055st2014.mailmaster.R;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import TIG055st2014.mailmaster.InboxActivity;
import TIG055st2014.mailmaster.R;
import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.InstrumentationTestCase;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

public class InboxActivityTest extends ActivityUnitTestCase<InboxActivity> {
	private InboxActivity activity;

	public InboxActivityTest() {
		super(InboxActivity.class);
		// TODO Auto-generated constructor stub
	}

	protected void setUp() throws Exception {
		super.setUp();
		Intent intent = new Intent(getInstrumentation().getTargetContext(),
				InboxActivity.class);
		startActivity(intent, null, null);
		activity = getActivity();
	}

	public void testComposePress() {
		activity.runOnUiThread(new Runnable() {

			public void run() {
				String target = "TIG055st2014.mailmaster.ComposeActivity";
				String adress = "mailmastertesting@gmail.com";
				MenuItem replyIcon = (MenuItem) activity
						.findViewById(R.id.action_toCompose);
				activity.onClickCompose(replyIcon);
				assertTrue(getStartedActivityIntent().getAction()
						.equals(target));
			}
		});
	}

	public void testSettingPress() {
		activity.runOnUiThread(new Runnable() {

			public void run() {
				String target = "TIG055st2014.mailmaster.AccountSettingsActivity";
				String adress = "mailmastertesting@gmail.com";
				MenuItem settingIcon = (MenuItem) activity
						.findViewById(R.id.action_toSettings);
				activity.onClickSettings(settingIcon);
				assertTrue(getStartedActivityIntent().getAction()
						.equals(target));
			}
		});
	}

	public void testEmptyEmailList() {
		activity.runOnUiThread(new Runnable() {
			public void run() {
				String target = "TIG055st2014.mailmaster.AddAccountActivity";
				activity.accounts.getString("defaultAcc", "");
				assertTrue(getStartedActivityIntent().getAction()
						.equals(target));
				// accEdit= activity.accounts.edit();
				// accEdit.remove(activity.defaultAcc);
				// accEdit.commit();
			}
		});
	}
}

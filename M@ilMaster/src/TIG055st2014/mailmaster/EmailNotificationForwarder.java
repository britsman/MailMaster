package TIG055st2014.mailmaster;
import android.app.Activity;
import android.os.Bundle;

/**
 * Dummy activity used to reset unread email counter if
 * the notification is pressed/deleted. the call to finish()
 * stops this activity from staying on the history stack 
 * (so back button won't take you to this empty activity).
 */
public class EmailNotificationForwarder extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmailNotificationVariables.nrUnreadEmail = 0;
        finish();
    }
}
package TIG055st2014.mailmaster;
import android.app.Activity;
import android.os.Bundle;


public class EmailForwarder extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EmailNotificationVariables.nrUnreadEmail = 0;
        finish();
    }
}
package TIG055st2014.mailmaster;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class EmailNotificationService extends Service{

    private Thread thread;
    private boolean running = false;

    private final int emailId = 1;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this,"Service created", Toast.LENGTH_LONG).show();

        thread = new Thread(){
            @Override
            public void run() {
                initVariables();

                NotificationManager NotifyManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                
                //make one for email, remember to update android manifest.
                Intent emailIntent = new Intent(getApplicationContext(), EmailForwarder.class);
                
                while(running){
//                    ArrayList<Event> events = git.getRepoEvents2();
//                    for(Event e : events){
//                            if(e.getType().equals("PushEvent")){
//                               //Check for seen flag
//  
//                    }
                	//check if number of unseen emails are higher than 0
                	if(EmailNotificationVariables.nrUnreadEmail > 0){
                		Notification emailNoti = builder
                				//.setSmallIcon(R.drawable.pushlogo)
                				.setContentTitle("You have " + EmailNotificationVariables.nrUnreadEmail + " unread messages.")
                				.setAutoCancel(true)
                				.setContentIntent(PendingIntent.getActivity(EmailNotificationService.this, 0, emailIntent, 0))
                				.setDeleteIntent(PendingIntent.getActivity(EmailNotificationService.this, 0, emailIntent, 0))
                				.build();

                		NotifyManager.notify(emailId, emailNoti);
                	}

                    try {
                        sleep(60000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.setPriority(Thread.MIN_PRIORITY);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
        Toast.makeText(this,"Service destroyed", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this,"Service started", Toast.LENGTH_LONG).show();
        running = true;
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }
    private void initVariables(){
        EmailNotificationVariables.nrUnreadEmail = 0;
    }
}
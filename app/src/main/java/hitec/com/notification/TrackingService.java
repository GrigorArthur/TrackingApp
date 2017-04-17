package hitec.com.notification;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Timer;
import java.util.TimerTask;

import hitec.com.ui.MainActivity;
import hitec.com.util.MyNotificationManager;

/**
 * Created by Arthur on 4/17/2017.
 */

public class TrackingService extends Service {
    private Context ctx;
    private static Timer timer = new Timer();

    public TrackingService() {
        super();
    }

    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;

    public void onCreate()
    {
        super.onCreate();
        ctx = this;
        startService();
    }

    private void startService()
    {
        timer.scheduleAtFixedRate(new mainTask(), 0, 10000);
    }

    private void showNotification(String message) {
        //optionally we can display the json into log
        MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());

        //creating an intent for the notification
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        mNotificationManager.showSmallNotification("Notification", message, intent);
    }

    private class mainTask extends TimerTask
    {
        public void run()
        {
            showNotification("Timer");
        }
    }
}

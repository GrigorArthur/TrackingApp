package hitec.com.notification;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import hitec.com.ui.HomeActivity;
import hitec.com.ui.MainActivity;
import hitec.com.util.MyNotificationManager;
import hitec.com.util.TrackGPS;

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

    private class mainTask extends TimerTask
    {
        public void run()
        {
            TrackGPS gps = new TrackGPS(getApplicationContext());


            if(gps.canGetLocation()){


                double longitude = gps.getLongitude();
                double latitude = gps .getLatitude();

                Log.v("Location:","Longitude:"+Double.toString(longitude)+"\nLatitude:"+Double.toString(latitude));
                try {
                    Geocoder gCoder = new Geocoder(getApplicationContext());
                    latitude = 41.8057;
                    longitude = 123.4315;
                    List<Address> addresses = gCoder.getFromLocation(latitude, longitude, 1);
                    Address info = addresses.get(0);
                    Log.v("Address", info.getFeatureName());
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (SecurityException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}

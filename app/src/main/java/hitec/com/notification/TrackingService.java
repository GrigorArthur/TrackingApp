package hitec.com.notification;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import hitec.com.task.SendLocationTask;
import hitec.com.ui.HomeActivity;
import hitec.com.ui.MainActivity;
import hitec.com.util.MyNotificationManager;
import hitec.com.util.SharedPrefManager;
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
        try {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 30000, 0, mLocationListener);
            if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 0, mLocationListener);
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
            try {
                Geocoder gCoder = new Geocoder(getApplicationContext());
                List<Address> addresses = gCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                String strAdd = strReturnedAddress.toString();
                String sender = SharedPrefManager.getInstance(getApplicationContext()).getUsername();

                SendLocationTask task = new SendLocationTask();
                task.execute(sender, String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()), strAdd);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (SecurityException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
}

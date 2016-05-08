package team48.coupletones;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MyLocationService extends Service {
    public MyLocationService() {
    }


    private LocationManager mLocationManager = null;

    // Define a listener that responds to location updates
    LocationListener locationListener = new LocationListener() {

        // Location lastKnownLocation;

        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            //  lastKnownLocation.set(location);

            SharedPreferencesEditor shared = new SharedPreferencesEditor(getSharedPreferences("userInfo", MODE_PRIVATE));
            SendMessages sendMessages = new SendMessages(shared.getAPIKey(), GoogleCloudMessaging.getInstance(MyLocationService.this));


            System.out.println("Location updates: " + lastKnownLocation.getLongitude() + " " + lastKnownLocation.getLatitude());


        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    // Or use LocationManager.GPS_PROVIDER
    String locationProvider = LocationManager.GPS_PROVIDER;

    Location lastKnownLocation;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Thread thread = new Thread(new MyThread(startId));
        //thread.start();
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onCreate() {
        InitializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(locationProvider, 0, 0, locationListener);
            lastKnownLocation = mLocationManager.getLastKnownLocation(locationProvider);
        }catch (java.lang.SecurityException ex){
            String s = new String("hello");
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void InitializeLocationManager(){
        if(mLocationManager == null){
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}

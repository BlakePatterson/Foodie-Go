package edu.temple.foodiego;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static android.content.ContentValues.TAG;

public class ForegroundLocationService extends Service {

    private final IBinder binder = new LocalBinder();

    public static int serviceID = 915023345;

    Context boundActivity;

    Boolean isBound = false;

    Notification notification;

    LocationManager locationManager;
    LocationListener locationListener;

    public ForegroundLocationService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //return the local binder
        isBound = true;
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isBound = false;
        return super.onUnbind(intent);
    }

    public class LocalBinder extends Binder {
        //return the service itself
        ForegroundLocationService getService() {
            return ForegroundLocationService.this;
        }
    }

    public void registerActivity(Context activity) {
        boundActivity = activity;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        locationManager = getSystemService(LocationManager.class);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (boundActivity != null) {
                    //send the updated location data to the activity
                    ((LocationServiceInterface) boundActivity).updateLocation(location);
                }
                Log.d(TAG, "onLocationChanged: lat: " + location.getLatitude() + "; long: " + location.getLongitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notification = (new NotificationCompat.Builder(this, getString(R.string.foodie_go_location_updates_notif_channel_id)))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Foodie Go Reading Location")
                .setContentText("The app is currently reading location data")
                .build();

        startForeground(serviceID, notification);

        getLocationUpdates();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
    }

    @SuppressLint("MissingPermission")
    private void getLocationUpdates() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, locationListener);
    }

    interface LocationServiceInterface {
        void updateLocation(Location location);
    }

}
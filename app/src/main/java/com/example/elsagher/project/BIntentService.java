package com.example.elsagher.project;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BIntentService extends Service {
    private final double speed = 40.0;
    public LocationManager locationManager;
    //    public MyLocationListener listener;
    Location myLocation;
    ArrayList<Location> locs = new ArrayList<>();

    public BIntentService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        locs = (ArrayList<Location>) intent.getExtras().getSerializable("arr");
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


        while (mWifi.isConnected()) {

            getLocation();
            Log.e("TAGGGGGGGGGGG", "Service Started! " + myLocation.getLongitude());
            double distance = getNearestAccessPoint();
            double time = distance / (speed * 1000 * 60 * 60);
            Log.e("TAGGGGGGGGGGG", "time " + time);
//            doSomeThing();
            SystemClock.sleep((long) (10 * 1000));

        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    //
//    private class MyLocationListener implements LocationListener {
//        @Override
//        public void onLocationChanged(Location location) {
//            double longitudeGPS = location.getLongitude();
//            double latitudeGPS = location.getLatitude();
//            Log.e("TAGGGGGGGGGGG", "in TrackingService onlocationChanged and about to send lon/lat " + latitudeGPS + " longi: " + longitudeGPS);
//            Toast.makeText(getApplicationContext(), "LocationChangedGPS LAT: " + latitudeGPS + " longi: " + longitudeGPS, Toast.LENGTH_LONG).show();
////            gpsComSinal = true;
//
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//
//        }
//
//    }
    private void getLocation() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        for (String provider : providers) {


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                   return;
            }
            myLocation = locationManager.getLastKnownLocation(provider);
        }
    }

    private double getNearestAccessPoint() {
        Iterator it = locs.iterator();
        double distance = Double.MAX_VALUE;
        Location location;
        while (it.hasNext()) {
            Location another = (Location) it.next();
            if (distance > myLocation.distanceTo(another)) {
                distance = myLocation.distanceTo(another);
            }
            it.remove(); // avoids a ConcurrentModificationException
        }
        return distance;
    }

}

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
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BIntentService extends Service {
    private Integer etaTime;
    public LocationManager locationManager;
    public String stream;
    Thread readthread;
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
        locs = intent.getExtras().getParcelableArrayList("arr");


        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


        while (mWifi.isConnected()) {

            getLocation();
            Log.e("TAGGGGGGGGGGG", "Service Started! " + myLocation.getLongitude());

            Location distination = getNearestAccessPoint();
            Log.e("TAGGGGGGGGGGG", "Service Started! " + distination.getLongitude());

            String urlString = "https://maps.googleapis.com/maps/api/distancematrix/json?"
                    + "origins=" + myLocation.getLatitude() + "," + myLocation.getLongitude()
                    + "&destinations=" + distination.getLatitude() + "," + distination.getLatitude()
                    + "&key=" + "AIzaSyBY1hwrH5SzCYKORQ4sdbQB2vca0POKPUE";

            execute(urlString);
            JsonParse(stream);
            Log.e("TAGGGGGGGGGGG", "time " + etaTime);
//            doSomeThing();
            SystemClock.sleep(1000*5);
            Log.e("TAGGGGGGGGGGG", "time sleep");

        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void execute(final String urlString)  {


        readthread = new Thread(new Runnable() {
            public void run() {


                URL url = null;
                try {
                    url = new URL(urlString);

                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    // Read the BufferedInputStream
                    BufferedReader r = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        sb.append(line);
                    }
                    stream = sb.toString();
                    Log.e("thread", stream);

                    // End reading...............



                    urlConnection.disconnect();






                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        readthread.start();
        Log.e("GGGGGGG", "end of methode");

        try {
            readthread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // if response code = 200 ok


    }

    private void JsonParse(String stream) {
        // Disconnect the HttpURLConnection
        Log.e("GGGGGGG", stream);
        try {
//            // Get the full HTTP Data as JSONObject
            JSONObject reader = new JSONObject(stream);
            JSONArray rows = reader.getJSONArray("rows");
            JSONObject row = rows.getJSONObject(0);
            JSONArray elements = row.getJSONArray("elements");
            JSONObject element = elements.getJSONObject(0);
            JSONObject duration = element.getJSONObject("duration");

            etaTime=Integer.valueOf( duration.getString("value"));

//            JSONObject element = array.getJSONObject(0);
//            JSONObject duration = element.getJSONObject("duration");
//
//            JSONObject time = duration.getJSONObject("value");
//            Log.e("GGGGGGG", "url " + time.toString());
//
//            etaTime = Double.valueOf(time.toString());
//            Log.e("thread", reader.toString());
//            Log.e("GGGGGGG", stream);
//
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


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

    private Location getNearestAccessPoint() {
//        Iterator it = locs.iterator();
        double distance = Double.MAX_VALUE;
        Location location;
        Location nearest = null;
        for (int i = 0; i < locs.size(); i++) {
            Location another = (Location)locs.get(i);
            Log.e("loop", another.getLatitude() + "lat");
            if (distance > myLocation.distanceTo(another)) {
                distance = myLocation.distanceTo(another);
                nearest = myLocation;
            }
//            it.remove(); // avoids a ConcurrentModificationException

        }
//
        return nearest;
    }


}
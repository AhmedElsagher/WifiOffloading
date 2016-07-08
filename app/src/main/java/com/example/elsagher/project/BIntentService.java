package com.example.elsagher.project;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
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
import java.util.List;

public class BIntentService extends Service {
    // while condition
    //eap sim
    //sleep time

    private static final String TAG = "gher.project.Service";
    ArrayList<Location> locationArrayList = new ArrayList<>();

    public BIntentService() {
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        super.onCreate();


    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        locationArrayList = intent.getExtras().getParcelableArrayList("tag");
        Log.e("loop", locationArrayList.size() + "locs");
        new LoopTask().execute(locationArrayList);
        return Service.START_REDELIVER_INTENT;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onStartCommand");

        return null;
    }


    private class LoopTask extends AsyncTask<ArrayList<Location>, Void, String> {
        private Integer etaTime;    //estimated time
        public LocationManager locationManager;
        public String stream;
        private Thread readthread;
        private Location myLocation;
        private ConnectivityManager connManager;
        boolean m3G;
        private Location distination;
        private ArrayList<Location> locs = new ArrayList<>();


        @Override
        protected String doInBackground(ArrayList<Location>... params) {
            locs = params[0];
            connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            m3G = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    .isConnectedOrConnecting();

            Log.e("TAGGGGGGGGGGG", "myLocation Started! " + myLocation.getLatitude());
            while (m3G) {
                getLocation();
                distination = getNearestAccessPoint();
                Log.e("TAGGGGGGGGGGG", "distination Started! " + distination.getLatitude());

                String urlString = "https://maps.googleapis.com/maps/api/distancematrix/json?"
                        + "origins=" + myLocation.getLatitude() + "," + myLocation.getLongitude()
                        + "&destinations=" + distination.getLatitude() + "," + distination.getLongitude()
                        + "&key=" + "AIzaSyBY1hwrH5SzCYKORQ4sdbQB2vca0POKPUE";

                execut(urlString);
                JsonParse(stream);
                Log.e("TAGGGGGGGGGGG", "time " + etaTime);
                SystemClock.sleep(5000);
                Log.e("TAGGGGGGGGGGG", "time sleep");
            }
            return null;

        }

        private void getLocation() {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            List<String> providers = locationManager.getProviders(true);
            for (String provider : providers) {


                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                myLocation = locationManager.getLastKnownLocation(provider);
            }
        }

        private Location getNearestAccessPoint() {
//        Iterator it = locs.iterator();
            double distance = Double.MAX_VALUE;
            Location nearest = null;
            Location another;
            Log.e("loop", locs.size() + "locs");

            for (int i = 0; i < locs.size(); i++) {
                another = (Location) locs.get(i);
                Log.e("loop", another.getLatitude() + "lat");
                if (distance > myLocation.distanceTo(another)) {
                    distance = myLocation.distanceTo(another);
                    nearest = another;
                }
//            it.remove(); // avoids a ConcurrentModificationException

            }
//
            return nearest;
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

                etaTime = Integer.valueOf(duration.getString("value"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void execut(final String urlString) {


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


    }


}
package com.example.elsagher.project;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class IntroActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private Button automatic, manual;
    private ConnectivityManager manager;
    private boolean isWifi;
    private boolean is3g;
    boolean isOnline;
    private ArrayList<Location> AccessPointsList = new ArrayList<>();
    private TelephonyManager mTelephonyManager;
    double downloaded = 0.0;
    private final int timesOfPing = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mTelephonyManager = (TelephonyManager)
                this.getSystemService(Context.TELEPHONY_SERVICE);
        manual = (Button) findViewById(R.id.map_button);
        automatic = (Button) findViewById(R.id.con_button);
        isOnline = checkConnectivity();
        manual.setOnClickListener(this);
        automatic.setOnClickListener(this);
        Log.e("TAGGGGGGGGGGG", "3g "+is3g + " wifi " + isWifi+" online "+isOnline);
        if (isOnline) {
            new RequestLocations().execute("http://wifioffloading.hol.es/sagher.php");
            if (is3g) monitor3GNetwork();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void monitor3GNetwork() {
        try {
            new TestDownloadRate().execute("http://cdn.mos.cms.futurecdn.net/4b9cb8628154cd8853b655e20a71de05-650-80.jpg").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        new SendData().execute();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.intro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.account) {
            Toast.makeText(this, "account", Toast.LENGTH_LONG).show();

        } else if (id == R.id.about) {
            Toast.makeText(this, "about", Toast.LENGTH_LONG).show();

        } else if (id == R.id.contact) {
            Toast.makeText(this, "contact", Toast.LENGTH_LONG).show();

        } else if (id == R.id.setting) {
            Toast.makeText(this, "settings", Toast.LENGTH_LONG).show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onClick(View v) {
        if (isOnline&&v.getId() == manual.getId()) {


            Intent intent = new Intent(this, MapsActivity.class);
            intent.putParcelableArrayListExtra("tag", AccessPointsList);
            startActivity(intent);

        } else if (isOnline&&v.getId() == automatic.getId()) {

            Log.e("TagIntro", "intent service");
            Intent intent = new Intent(this, BIntentService.class);
            intent.putParcelableArrayListExtra("tag", AccessPointsList);
            startService(intent);
        }

    }

    private class RequestLocations extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... strings) {
            String stream = null;
            String urlString = strings[0];
            Log.e("TAGGGGGGGGGGG", "begin thread");
            HTTPDataHandler hh = new HTTPDataHandler();
            stream = hh.GetHTTPData(urlString);
            return stream;
        }

        protected void onPostExecute(String stream) {
            if (stream != null) {
                try {
                    // Get the full HTTP Data as JSONObject
                    JSONArray reader = new JSONArray(stream);
                    String lat, longitude;
                    for (int i = 0; i < reader.length(); i++) {
                        JSONObject aa = reader.getJSONObject(i);
                        lat = aa.getString("latitude");
                        longitude = aa.getString("longitude");
                        Location loc = new Location("");
                        Log.e("long ", longitude);
                        loc.setLatitude(Double.valueOf(lat));
                        loc.setLongitude(Double.valueOf(longitude));
                        AccessPointsList.add(loc);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private class SendData extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... strings) {
            double ping = ping();
            double downloadRate = downloaded;
            String cellID = getCellId();
            String networkType = getNetworkClass();
            String operatorName = getOperatorName();
            String urlString = ("http://wifioffloading.hol.es/monitoring.php?cellid=" +
                    cellID + "&download=" + downloadRate +
                    "&upload=" + (downloadRate / 8) + "&ping=" + ping + "&kind=" + networkType +
                    "&operator=" + operatorName);
            Log.e("TAGGGGGGGGGGG", "begin thread");
            HTTPDataHandler hh = new HTTPDataHandler();
            hh.GetHTTPData(urlString);
            return null;
        }


    }

    public String getCellId() {
        GsmCellLocation cl = (GsmCellLocation) mTelephonyManager.getCellLocation();
        int cid = cl.getCid();
        return cid + "";
    }

    public String getNetworkClass() {
        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "RTT";
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "IDEN";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "EVDO_0";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "EVDO_A";
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "HSUPA";
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "EVDO_B";
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return "EHRPD";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "HSPAP";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            default:
                return "Unknown";
        }
    }

    private class TestDownloadRate extends AsyncTask<String, Void, Double> {
        protected Double doInBackground(String... urls) {
            // String uploaded = "";
            try {

                long BeforeTime = System.currentTimeMillis();
                long TotalTxBeforeTest = TrafficStats.getTotalTxBytes();
                long TotalRxBeforeTest = TrafficStats.getTotalRxBytes();
                URL url = new URL(urls[0]);
                URLConnection connection = new URL(urls[0]).openConnection();
                connection.setUseCaches(false);
                connection.connect();
                InputStream input = connection.getInputStream();

                BufferedInputStream bufferedInputStream = new BufferedInputStream(input);
                byte[] buffer = new byte[1024];
                int n = 0;
                long endLoop = BeforeTime + 30000;
                while (System.currentTimeMillis() < endLoop) {
                    if (bufferedInputStream.read(buffer) == -1) {
                        break;
                    }
                }
                long TotalTxAfterTest = TrafficStats.getTotalTxBytes();
                long TotalRxAfterTest = TrafficStats.getTotalRxBytes();
                long AfterTime = System.currentTimeMillis();

                double TimeDifference = AfterTime - BeforeTime;
                double rxDiff = TotalRxAfterTest - TotalRxBeforeTest;
                double txDiff = TotalTxAfterTest - TotalTxBeforeTest;
                if ((rxDiff != 0) && (txDiff != 0)) {
                    double rxBPS = (rxDiff / (TimeDifference / 1000)); // total rx bytes per second.
                    double txBPS = (txDiff / (TimeDifference / 1000)); // total tx bytes per second.
                    downloaded = (rxBPS / 1024);

//                    uploaded = String.valueOf(txBPS) + "B/s. Total tx = " + txDiff;
                    Log.e("TAGG", "Download speed. " + downloaded);

                } else {
                    downloaded = 0.0;
                }
            } catch (Exception e) {
                Log.e("TAGG", "Error while downloading. " + e.getMessage());
            }
            return downloaded;
        }

        protected void onPostExecute(String stream) {

        } // onPostExecute() end
    } // class end

    /*
    checkConnectivity returns true if user is Online
    and check mobile network and wifi status
    assuming you have credit for 3g
    and assume the wifi connection
     */
    public boolean checkConnectivity() {
        manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting();
        isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();


        return is3g||isWifi;

    }


    public String getOperatorName() {
        String carrierName = mTelephonyManager.getNetworkOperatorName();
        return carrierName;
    }


    public double ping() {
        String str = "";
        double sum = 0;
        String url = "www.google.com";
        for (int j = 0; j < timesOfPing; j++) {
            try {

                Process process = Runtime.getRuntime().exec(
                        "/system/bin/ping -c 1 " + url);
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));
                int i;
                char[] buffer = new char[4096];
                StringBuffer output = new StringBuffer();

                while ((i = reader.read(buffer)) > 0) {
                    output.append(buffer, 0, i);
                }

                reader.close();

                // body.append(output.toString()+"\n");
                str = output.toString();

            } catch (IOException e) {
                // body.append("Error\n");
                e.printStackTrace();
            }
            Log.e("loop times " + j, str);
            String[] splitResult1 = str.split("time=");
            String[] splitResult2 = splitResult1[1].split(" ");
            sum += Double.valueOf(splitResult2[0]);

        }
        return sum / timesOfPing;
    }
}

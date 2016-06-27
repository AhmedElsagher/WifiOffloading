package com.example.elsagher.project;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import java.util.ArrayList;
import java.util.Iterator;

public class IntroActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private Button automatic, manual;
    private ConnectivityManager manager;
    private boolean isWifi;
    private boolean is3g;
    private Location myLocation;
    public LocationManager locationManager;
    private ArrayList<Location> AccessPointsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        manual = (Button) findViewById(R.id.map_button);
        automatic = (Button) findViewById(R.id.con_button);
        manual.setOnClickListener(this);
        automatic.setOnClickListener(this);
        new ProcessJSON().execute("http://wifioffloading.hol.es/sagher.php");
        checkConnectivity();
        Log.e("TAGGGGGGGGGGG", is3g + " wifi " + isWifi);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

//    public void start(View v) {
//        Intent intent = new Intent(this, MapsActivity.class);
//        intent.putExtra("tag", AccessPointsList);
//        startActivity(intent);
//
//    }

//    public void autoConnect(View v) {
//        Log.e("TAGGGGGGGGGGG", " autoConnect ");
//
//        Intent intent = new Intent(this, BIntentService.class);
//        intent.putExtra("arr", AccessPointsList);
//        startService(intent);
//
//    }

    @Override
    public void onClick(View v) {
        if (v.getId() == manual.getId()) {



            Intent intent = new Intent(this, MapsActivity.class);
            intent.putParcelableArrayListExtra("tag", AccessPointsList);
            startActivity(intent);

        } else if (v.getId() == automatic.getId()) {


            Intent intent = new Intent(this, BIntentService.class);
            intent.putParcelableArrayListExtra("arr", AccessPointsList);
            startService(intent);
        }

    }

    private class ProcessJSON extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... strings) {
            String stream = null;
            String urlString = strings[0];
            Log.e("TAGGGGGGGGGGG", "begin thread");

            HTTPDataHandler hh = new HTTPDataHandler();
            stream = hh.GetHTTPData(urlString);

            // Return the data from specified url
            return stream;
        }

        protected void onPostExecute(String stream) {
//            TextView tv = (TextView) findViewById(R.id.text);
//            tv.append("  read  " + stream);
//            //tv.setText(stream);

            /*
                Important in JSON DATA
                -------------------------
                * Square bracket ([) represents a JSON array
                * Curly bracket ({) represents a JSON object
                * JSON object contains key/value pairs
                * Each key is a String and value may be different data types
             */

            //..........Process JSON DATA................
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

                        loc.setLatitude(Double.valueOf(lat));
                        loc.setLongitude(Double.valueOf(longitude));
                        AccessPointsList.add(loc);

                    }

//
//
                    //
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//            } // if statement end
            } // onPostExecute() end

        } // ProcessJSON class end


    }

    public void checkConnectivity() {
        manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

//For 3G check
        is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                .isConnectedOrConnecting();
//For WiFi Check
        isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .isConnectedOrConnecting();


    }

}

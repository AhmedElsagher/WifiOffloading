package com.example.elsagher.project;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import java.security.PrivateKey;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String urlString;
   private ArrayList<LatLng> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locations=new ArrayList<>();
        new ProcessJSON().execute(urlString);


    }
    public void start(View v){
        Intent intent=new Intent(this,MapsActivity.class);
        intent.putExtra("tag",locations);
        startActivity(intent);

    }

    private class ProcessJSON extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... strings) {
            String stream = null;
            String urlString = strings[0];

            HTTPDataHandler hh = new HTTPDataHandler();
            stream = hh.GetHTTPData(urlString);

            // Return the data from specified url
            return stream;
        }

        protected void onPostExecute(String stream) {
//            TextView tv = (TextView) findViewById(R.id.text);
//            tv.append("  read  " + stream);
            //tv.setText(stream);

            /*
                Important in JSON DATA
                -------------------------
                * Square bracket ([) represents a JSON array
                * Curly bracket ({) represents a JSON object
                * JSON object contains key/value pairs
                * Each key is a String and value may be different data types
             */

            //..........Process JSON DATA................
//            if (stream != null) {
//                try {
//                    // Get the full HTTP Data as JSONObject
//                    JSONObject reader = new JSONObject(stream);
//                    JSONObject aa = new JSONObject("content");
//
//
//                    tv.append("\n" + aa + "\n");
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

//            } // if statement end
        } // onPostExecute() end
    } // ProcessJSON class end


}

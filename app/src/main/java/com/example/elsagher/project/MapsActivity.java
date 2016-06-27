package com.example.elsagher.project;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private View mymarkerview;
    private ArrayList<Location> AccessPointsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        AccessPointsList =  getIntent().getExtras().getParcelableArrayList("tag");
        for (int i = 0; i < AccessPointsList.size(); i++) {
            Log.e("tag2",AccessPointsList.get(i).getLatitude()+" lat");
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location myLocation = null;

        for (String provider : providers) {


            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            }
            myLocation = locationManager.getLastKnownLocation(provider);

        }
        for (int i = 0; i < AccessPointsList.size(); i++) {
            mMap.addMarker(new MarkerOptions().
                position(new LatLng(AccessPointsList.get(i).getLatitude(), AccessPointsList.get(i).getLongitude()))
                .title("wifi")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        }

        LatLng sydney = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(sydney).title("ur position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
        mMap.setInfoWindowAdapter(new Yourcustominfowindowadpater());


//        Button connectButton= (Button) mymarkerview.findViewById(R.id.butConncect);


        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(MapsActivity.this, "connect to eap", Toast.LENGTH_SHORT).show();
            }
        });

        Toast.makeText(MapsActivity.this, AccessPointsList.size() + " num", Toast.LENGTH_SHORT).show();// display toast

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                if (arg0.getTitle().equals("wifi"))
                    arg0.showInfoWindow();

                return true;
            }

        });

    }

    private class Yourcustominfowindowadpater implements GoogleMap.InfoWindowAdapter {

        Yourcustominfowindowadpater() {
            mymarkerview = getLayoutInflater()
                    .inflate(R.layout.custom_info_window, null);
        }

        public View getInfoWindow(Marker marker) {
            render(marker, mymarkerview);
            return mymarkerview;
        }

        public View getInfoContents(Marker marker) {
            return null;
        }

        private void render(final Marker marker, View view) {

            Button connectButton = (Button) mymarkerview.findViewById(R.id.butConncect);

            // Add the code to set the required values
            // for each element in your custominfowindow layout file
        }
    }

}
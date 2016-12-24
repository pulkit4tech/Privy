package com.pulkit4tech.privy;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PrivyMapsActivity extends ActionBarActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privy_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.privyMapActivity);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Delhi.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a test marker in Delhi and move the camera
        LatLng delhi = new LatLng(28.633011, 77.219373);
        mMap.addMarker(new MarkerOptions().position(delhi).anchor(.5f,.5f).title("Marker in Home"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(delhi,10.0f));

        LatLng delhi2 = new LatLng(28.633511, 77.219444);
        mMap.addMarker(new MarkerOptions().position(delhi2).anchor(.5f,.5f).title("Test Marker in Home2").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));
    }
}

package com.pulkit4tech.privy;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pulkit4tech.privy.data.LocationData;
import com.pulkit4tech.privy.utilities.LocationServices;

import static com.pulkit4tech.privy.constants.Constants.DEBUG;
import static com.pulkit4tech.privy.constants.Constants.CAMERA_ANIMATION_DURATION;
import static com.pulkit4tech.privy.constants.Constants.MY_PERMISSIONS_REQUEST_FINE_LOCATIONS;

public class PrivyMapsActivity extends ActionBarActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Context mContext;
    private Marker myLocationMarker;
    private CameraPosition MY_LOCATION_CAMERA_POS;

    // My Location
    private LocationData myLocationData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privy_maps);

        mContext = this;

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
        setUpMapInfo();
        addMarkers();
    }


    private void addMarkers() {

        // Add a test marker in Delhi and move the camera
        LatLng delhi = new LatLng(28.633011, 77.219373);
        mMap.addMarker(new MarkerOptions().position(delhi).anchor(.5f, .5f).title("Marker in Home"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(delhi, 15.0f));

        LatLng delhi2 = new LatLng(28.633511, 77.219444);
        mMap.addMarker(new MarkerOptions().position(delhi2).anchor(.5f, .5f).title("Test Marker in Home2").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));

    }

    private void setUpMapInfo() {

        if(!checkLocationEnabledPermission()){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_FINE_LOCATIONS);
        }else {
            setUpMyLocationMarker();
        }

    }

    private void setUpMyLocationMarker() {
        mMap.setMyLocationEnabled(true);
        getMyCurrentLocation();
    }

    private void getMyCurrentLocation(){
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                LocationServices locationService = new LocationServices(mContext);
                myLocationData = locationService.getCurrentLocation();
                if(myLocationData!=null) {

                    // checking for previous marker and if present, replacing it with new marker
                    if(myLocationMarker!=null){
                        myLocationMarker.remove();
                    }

                    MY_LOCATION_CAMERA_POS = new CameraPosition.Builder()
                            .target(myLocationData.getLatLng())
                            .zoom(15.0f)
                            .bearing(0)
                            .tilt(25)
                            .build();

                    myLocationMarker = mMap.addMarker(new MarkerOptions().position(myLocationData.getLatLng()).title("My Location"));

                    //animate camera
                    onGoToMyLocation();
                    Log.d(DEBUG, myLocationData.getLatLng().toString());
                }
                return true;
            }
        });
    }

    private void onGoToMyLocation() {
        changeCamera(CameraUpdateFactory.newCameraPosition(MY_LOCATION_CAMERA_POS), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                Toast.makeText(mContext,"Animation Finished",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(mContext,"Animation Canceled",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeCamera(CameraUpdate update, GoogleMap.CancelableCallback callback){
        mMap.animateCamera(update,CAMERA_ANIMATION_DURATION,callback);
    }

    private boolean checkLocationEnabledPermission() {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_FINE_LOCATIONS:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    setUpMyLocationMarker();
                }
                else {
                    Toast.makeText(mContext,"Please give permission for location",Toast.LENGTH_SHORT).show();
                }
            break;

            default:
                Log.d(DEBUG,"Some other request code: " + requestCode);
        }
    }
}

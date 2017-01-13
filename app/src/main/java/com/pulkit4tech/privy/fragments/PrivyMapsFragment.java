package com.pulkit4tech.privy.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.pulkit4tech.privy.R;
import com.pulkit4tech.privy.data.LocationData;
import com.pulkit4tech.privy.data.json.MarkerData;
import com.pulkit4tech.privy.utilities.LocationServices;
import com.pulkit4tech.privy.utilities.NetworkRequest;

import java.util.HashMap;

import static com.pulkit4tech.privy.constants.Constants.DEBUG;
import static com.pulkit4tech.privy.constants.Constants.CAMERA_ANIMATION_DURATION;
import static com.pulkit4tech.privy.constants.Constants.MY_PERMISSIONS_REQUEST_FINE_LOCATIONS;

public class PrivyMapsFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Context mContext;
    private CameraPosition MY_LOCATION_CAMERA_POS;
    private HashMap<String, MarkerData> universalMarkers;

    // My location
    private LocationData myLocationData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View mView = inflater.inflate(R.layout.activity_privy_maps, container, false);
        mContext = getActivity();
        universalMarkers = new HashMap<>();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.privyMapActivity);
        //if (mapFragment!=null)
        mapFragment.getMapAsync(this);
        return mView;
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
    }


    private void addMarkers() {

        if (myLocationData == null) {
            LocationServices locationService = new LocationServices(mContext);
            myLocationData = locationService.getCurrentLocation();
        }

        if (myLocationData != null)
            markNearbyPrivys(myLocationData.getLatLng());

        // Add a test marker in Delhi and move the camera
//        LatLng delhi = new LatLng(28.633011, 77.219373);
//        mMap.addMarker(new MarkerOptions().position(delhi).anchor(.5f, .5f).title("Marker in Home"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(delhi, 15.0f));
//
//        LatLng delhi2 = new LatLng(28.633511, 77.219444);
//        mMap.addMarker(new MarkerOptions().position(delhi2).anchor(.5f, .5f).title("Test Marker in Home2").icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)));

    }

    private void setUpMapInfo() {

        if (!checkLocationEnabledPermission()) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATIONS);
        } else {
            mMap.setMyLocationEnabled(true);
            setUpMyLocationMarker();
        }
    }

    private void setUpMyLocationMarker() {
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                getMyCurrentLocation();
                return true;
            }
        });
        getMyCurrentLocation();
    }

    private void getMyCurrentLocation() {
        LocationServices locationService = new LocationServices(mContext);
        myLocationData = locationService.getCurrentLocation();
        if (myLocationData != null) {

            MY_LOCATION_CAMERA_POS = new CameraPosition.Builder()
                    .target(myLocationData.getLatLng())
                    .zoom(15.0f)
                    .bearing(0)
                    .tilt(25)
                    .build();

            //animate camera
            moveCameraToMyLocation();
            addMarkers();

            Log.d(DEBUG, myLocationData.getLatLng().toString());
        }
    }

    private void moveCameraToMyLocation() {
        changeCamera(CameraUpdateFactory.newCameraPosition(MY_LOCATION_CAMERA_POS), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                snackMsg("Animation Finished");
            }

            @Override
            public void onCancel() {
                Toast.makeText(mContext, "Animation Canceled", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void changeCamera(CameraUpdate update, GoogleMap.CancelableCallback callback) {
        mMap.animateCamera(update, CAMERA_ANIMATION_DURATION, callback);
    }

    private boolean checkLocationEnabledPermission() {
        return ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void markNearbyPrivys(LatLng myLocation) {
        new NetworkRequest(mContext, mMap, universalMarkers, myLocation).getMarkerData();
    }

    public void snackMsg(String msg) {
        Snackbar.make((CoordinatorLayout) getActivity().findViewById(R.id.coordinator_layout), msg, Snackbar.LENGTH_LONG).show();
    }
}

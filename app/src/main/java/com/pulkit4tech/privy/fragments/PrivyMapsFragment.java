package com.pulkit4tech.privy.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.pulkit4tech.privy.R;
import com.pulkit4tech.privy.data.json.MarkerData;
import com.pulkit4tech.privy.utilities.NetworkRequest;

import java.util.HashMap;

import static android.content.Context.LOCATION_SERVICE;
import static com.pulkit4tech.privy.constants.Constants.DEBUG;
import static com.pulkit4tech.privy.constants.Constants.CAMERA_ANIMATION_DURATION;
import static com.pulkit4tech.privy.constants.Constants.MY_PERMISSIONS_REQUEST_FINE_LOCATIONS;

public class PrivyMapsFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private Context mContext;
    private CameraPosition MY_LOCATION_CAMERA_POS;
    private HashMap<String, MarkerData> universalMarkers;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    // My location
    private Location myLocationData;

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        View mView = inflater.inflate(R.layout.activity_privy_maps, container, false);
        mContext = getActivity();
        universalMarkers = new HashMap<>();
        setUpGoogleApiClient();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.privyMapActivity);
        //if (mapFragment!=null)
        mapFragment.getMapAsync(this);
        return mView;
    }

    private void setUpGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
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
            myLocationData = getCurrentLocation();
        }

        if (myLocationData != null)
            markNearbyPrivys(new LatLng(myLocationData.getLatitude(), myLocationData.getLongitude()));

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
        myLocationData = getCurrentLocation();
        if (myLocationData != null) {

            MY_LOCATION_CAMERA_POS = new CameraPosition.Builder()
                    .target(new LatLng(myLocationData.getLatitude(), myLocationData.getLongitude()))
                    .zoom(15.0f)
                    .bearing(0)
                    .tilt(25)
                    .build();

            //animate camera
            moveCameraToMyLocation();
            addMarkers();

            Log.d(DEBUG, new LatLng(myLocationData.getLatitude(), myLocationData.getLongitude()).toString());
        } else {
            Log.d(DEBUG, "Can't retrieve location at the moment.");
            if (!checkGPSon())
                promptToEnableGPS();
        }
    }

    private void moveCameraToMyLocation() {
        changeCamera(CameraUpdateFactory.newCameraPosition(MY_LOCATION_CAMERA_POS), new GoogleMap.CancelableCallback() {
            @Override
            public void onFinish() {
                Log.d(DEBUG, "moveCameraToMyLocation: Camera Animation finished");
            }

            @Override
            public void onCancel() {
                Log.d(DEBUG, "moveCameraToMyLocation: Camera Animation Cancelled");
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
        new NetworkRequest(getActivity(), mMap, universalMarkers, myLocation).getMarkerData();
    }

    private void snackMsg(String msg) {
        Snackbar.make((CoordinatorLayout) getActivity().findViewById(R.id.coordinator_layout), msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
        myLocationData = com.google.android.gms.location.LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (myLocationData == null) {
            if (!checkGPSon()) {
                promptToEnableGPS();
            }
        } else {
            getMyCurrentLocation();
        }
    }

    private Location getCurrentLocation() {
        if (myLocationData != null)
            return myLocationData;
        Location location = com.google.android.gms.location.LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (location == null && !checkGPSon())
            promptToEnableGPS();

        return location;

    }

    @Override
    public void onConnectionSuspended(int i) {
        stopLocationUpdates();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        snackMsg(getString(R.string.google_api_client_connection_failed));
    }


    private void snackMsgWithAction(String msg) {
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinator_layout), msg, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.turn_on, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start intent to turn on GPS
                Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(onGPS);
            }
        });
        snackbar.show();
    }

    private void promptToEnableGPS() {
        snackMsgWithAction(mContext.getString(R.string.enable_gps_msg));
    }

    private boolean checkGPSon() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); // 10 meters
    }

    private void startLocationUpdates() {
        createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        getMyCurrentLocation();
    }

}

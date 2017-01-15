package com.pulkit4tech.privy.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import com.pulkit4tech.privy.R;
import com.pulkit4tech.privy.data.LocationData;

import static android.content.Context.LOCATION_SERVICE;

public class LocationServices implements LocationListener {

    private Context mContext;
    private LocationData myLocation;

    public LocationServices(Context context) {
        mContext = context;
    }

    public LocationData getCurrentLocation() {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        if(!checkGPSon(locationManager)){
            promptToEnableGPS();
        }
        Criteria criteria = setCriteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return myLocation;
        }
        Location location = locationManager.getLastKnownLocation(bestProvider);
        locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
        if (location != null) {
            onLocationChanged(location);
        }
        return myLocation;
    }

    private void promptToEnableGPS() {
        snackMsgWithAction(mContext.getString(R.string.enable_gps_msg));
    }

    private Criteria setCriteria() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        return criteria;
    }


    @Override
    public void onLocationChanged(Location location) {

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        myLocation = new LocationData();
        myLocation.setLatLng(latLng);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        snackMsg(mContext.getString(R.string.gps_enable_request_success));
    }

    @Override
    public void onProviderDisabled(String s) {
        promptToEnableGPS();
    }

    private boolean checkGPSon(LocationManager manager){
       return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void snackMsg(String msg) {
        Snackbar.make(((Activity) mContext).findViewById(R.id.coordinator_layout), msg, Snackbar.LENGTH_LONG).show();
    }
    
    private void snackMsgWithAction(String msg){
        Snackbar snackbar = Snackbar.make(((Activity) mContext).findViewById(R.id.coordinator_layout),msg,Snackbar.LENGTH_LONG);
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
}

package com.pulkit4tech.privy;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.pulkit4tech.privy.data.json.Location;
import com.pulkit4tech.privy.data.json.PostPrivyRequest;
import com.pulkit4tech.privy.fragments.PrivyMapsFragment;
import com.pulkit4tech.privy.utilities.NetworkRequest;
import com.pulkit4tech.privy.utilities.NoLocationPermission;

import java.util.ArrayList;

import static com.pulkit4tech.privy.constants.Constants.DEBUG;
import static com.pulkit4tech.privy.constants.Constants.MY_PERMISSIONS_REQUEST_FINE_LOCATIONS;
import static com.pulkit4tech.privy.constants.Constants.PLACE_PICKER_REQUEST;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            setUpInfo();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setUpInfo() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//               Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null);
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);
        loadMapFragment();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    snackMsg("Permission granted!!");
                } else {
                    loadFragment(new NoLocationPermission());
                    snackMsg("Please give permission for location");
                }
                break;

            default:
                Log.d(DEBUG, "Some other request code: " + requestCode);
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().add(R.id.frame_container, fragment, fragment.getClass().getName()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
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
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_nearby_privy) {
            loadMapFragment();
        } else if (id == R.id.nav_request_new) {
            loadAddNewPrivyActivity();
        }
        //TODO : Add other conditions

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place placeToAdd = PlacePicker.getPlace(data, this);
                if (placeToAdd.getLatLng() != null) {
                    requestNewPrivy(placeToAdd);
                } else {
                    snackMsg("Some Error!! Was not able to retrieve information properly");
                }
            } else {
                snackMsg("Please Select a Location if you wished to request Privy!");
            }

            navigationView.getMenu().getItem(0).setChecked(true);
            loadMapFragment();
        }
    }

    private void requestNewPrivy(Place placeToAdd) {
        PostPrivyRequest postdata = new PostPrivyRequest();
        Location loc = new Location();
        loc.setLat(placeToAdd.getLatLng().latitude);
        loc.setLng(placeToAdd.getLatLng().longitude);
        postdata.setLocation(loc);
        postdata.setAccuracy(50);
        postdata.setName("Public Toilet");
        postdata.setAddress(placeToAdd.getAddress().toString());
        ArrayList<String> types = new ArrayList<String>();
        types.add("establishment");
        postdata.setTypes(types);
        postdata.setLanguage("en");
        new NetworkRequest(this, postdata).postRequest();
    }

    private void loadAddNewPrivyActivity() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (Exception e) {
            Log.d(DEBUG, e.toString());
        }
    }

    private void loadMapFragment() {
        if (!checkLocationEnabledPermission()) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATIONS);
        } else {
            loadFragment(new PrivyMapsFragment());
        }
    }

    public void snackMsg(String msg) {
        Snackbar.make((CoordinatorLayout) findViewById(R.id.coordinator_layout), msg, Snackbar.LENGTH_LONG).show();
    }

//    private void toastMsg(String msg) {
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
//    }

    private boolean checkLocationEnabledPermission() {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
}

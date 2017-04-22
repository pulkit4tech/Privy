package com.pulkit4tech.privy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.pulkit4tech.privy.data.json.Location;
import com.pulkit4tech.privy.data.json.PostPrivyRequest;
import com.pulkit4tech.privy.fragments.PrivyMapsFragment;
import com.pulkit4tech.privy.utilities.NetworkRequest;
import com.pulkit4tech.privy.fragments.NoLocationPermission;

import java.util.ArrayList;

import static com.pulkit4tech.privy.constants.Constants.DEBUG;
import static com.pulkit4tech.privy.constants.Constants.LOG_PREF;
import static com.pulkit4tech.privy.constants.Constants.MY_PERMISSIONS_REQUEST_FINE_LOCATIONS;
import static com.pulkit4tech.privy.constants.Constants.PLACE_PICKER_REQUEST;
import static com.pulkit4tech.privy.constants.Constants.RC_SIGN_IN;
import static com.pulkit4tech.privy.constants.Constants.RC_SIGN_IN_NEW_PRIVY_REQUEST;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener {

    private NavigationView navigationView;
    private GoogleApiClient mGoogleApiClient;
    private Context mContext;
    private FloatingActionButton fab;
    private SharedPreferences mSharedPreferences;
    private String NAME = "user_name";
    private String EMAIL = "email_id";
    private String PROFILE_PIC_URL = "profile_pic_url";
    private String LOGGED_IN = "logged_in";
    private ImageView profileImg;
    private TextView userName, emailId;
    private AdView bannerAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mSharedPreferences = getSharedPreferences(LOG_PREF, MODE_PRIVATE);

        if (savedInstanceState == null) {
            setUpInfo();
        }

        loadMapFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setUpInfo() {
        setAddmob();
        setGoogleApiClientInfo();
        setUpFab();
        setUpNavigationDrawer();

        if (checkIfLoggedIn()) {
            fab.hide();
        }
    }

    private void setAddmob() {

        bannerAdView = (AdView) findViewById(R.id.bannerAdView);
        bannerAdView.loadAd(new AdRequest.Builder().build());

    }



    private void setUpNavigationDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setUpNavigationHeader();
    }

    private void setUpNavigationHeader() {
        View nav_head = navigationView.getHeaderView(0);
        profileImg = (ImageView) nav_head.findViewById(R.id.profile_pic);
        userName = (TextView) nav_head.findViewById(R.id.user_name);
        emailId = (TextView) nav_head.findViewById(R.id.email_id);

        setUpNavigationHeaderValue();
    }

    private void setUpNavigationHeaderValue() {
        userName.setText(mSharedPreferences.getString(NAME, ""));
        emailId.setText(mSharedPreferences.getString(EMAIL, ""));
        loadProfilePic();

        changeSignInSignOutOption();
    }

    private void loadProfilePic() {
        Glide.with(mContext).load(mSharedPreferences.getString(PROFILE_PIC_URL, ""))
                .asBitmap()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.drawable.default_avatar)
                .into(new BitmapImageViewTarget(profileImg) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        profileImg.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }

    private void changeSignInSignOutOption() {
        Menu menu = navigationView.getMenu();
        MenuItem sign_in_out_item = menu.findItem(R.id.nav_sign_in_out);
        if (checkIfLoggedIn()) {
            sign_in_out_item.setTitle(R.string.sign_out);
            sign_in_out_item.setIcon(this.getResources().getDrawable(R.drawable.sign_out));
        } else {
            sign_in_out_item.setTitle(R.string.sign_in);
            sign_in_out_item.setIcon(this.getResources().getDrawable(R.drawable.sign_in));
        }
    }

    private void setUpFab() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGoogleSignInActivity(RC_SIGN_IN);
            }
        });
    }

    private void startGoogleSignInActivity(int resultcode) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, resultcode);
    }

    private void signOut() {
        clearSharedPreference();
        setUpNavigationHeaderValue();

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    fab.show();
                    snackMsg(getString(R.string.sign_out_msg));
                } else {
                    snackMsg(getString(R.string.sign_out_error_msg));
                    Log.d(DEBUG, "Sign out error : " + status.toString());
                }
            }
        });
    }

    private boolean checkIfLoggedIn() {
        return mSharedPreferences.getBoolean(LOGGED_IN, false);
    }

    private void setGoogleApiClientInfo() {

        // Testing Google Sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
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
                    //loadMapFragment();
                    snackMsg(getString(R.string.location_permission_success));
                } else {
                    loadFragment(new NoLocationPermission());
                    snackMsg(getString(R.string.location_permission_failed));
                }
                break;

            default:
                Log.d(DEBUG, "Some other request code: " + requestCode);
        }
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().add(R.id.frame_container, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_nearby_privy) {
            loadMapFragment();
            closeDrawer();
        } else if (id == R.id.nav_request_new) {
            loadAddNewPrivyActivity();
            closeDrawer();
        } else if (id == R.id.nav_sign_in_out) {
            if (checkIfLoggedIn()) {
                signOut();
            } else {
                startGoogleSignInActivity(RC_SIGN_IN);
            }
        } else if (id == R.id.nav_share) {
            shareApp();
            closeDrawer();
        } else if (id == R.id.nav_feedback) {
            sendFeedBack();
            closeDrawer();
        }
        return true;
    }

    private void sendFeedBack() {
        Intent Email = new Intent(Intent.ACTION_SEND);
        Email.setType("text/email");
        Email.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.app_feedback_mail)});
        Email.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_subject));
        startActivity(Intent.createChooser(Email, getString(R.string.send_feedback_msg)));
    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                getString(R.string.social_share_msg));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void closeDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
//        if(checkLocationEnabledPermission())
//        loadMapFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place placeToAdd = PlacePicker.getPlace(data, this);
                if (placeToAdd.getLatLng() != null) {
                    requestNewPrivy(placeToAdd);
                } else {
                    snackMsg(getString(R.string.error_retrieving_data_msg));
                }
            } else {
                snackMsg(getString(R.string.select_location_request_msg));
            }

            navigationView.getMenu().getItem(0).setChecked(true);
        }


        // Google Sign in Callback
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        if (requestCode == RC_SIGN_IN_NEW_PRIVY_REQUEST) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResultAndLaunchNewRequestPrivy(result);
        }
    }

    private void handleSignInResultAndLaunchNewRequestPrivy(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            addLoginInfo(acct);

            //Launch NewRequestPrivy
            loadAddNewPrivyActivity();

            // Hide Fab button
            fab.hide();
        } else {
            clearSharedPreference();
            snackMsg(getString(R.string.request_location_permission));
        }
        setUpNavigationHeaderValue();
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(DEBUG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            addLoginInfo(acct);
            // Hide Fab button
            fab.hide();
        } else {
            clearSharedPreference();
            snackMsg(getString(R.string.request_location_permission));
        }
        setUpNavigationHeaderValue();
    }

    private void addLoginInfo(GoogleSignInAccount acct) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(LOGGED_IN, true);
        if (acct.getDisplayName() != null)
            editor.putString(NAME, acct.getDisplayName());
        if (acct.getEmail() != null)
            editor.putString(EMAIL, acct.getEmail());
        if (acct.getPhotoUrl() != null)
            editor.putString(PROFILE_PIC_URL, acct.getPhotoUrl().toString());
        editor.commit();

        snackMsg(getString(R.string.sign_in_msg));
    }

    private void clearSharedPreference() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear().commit();
    }

    private void requestNewPrivy(Place placeToAdd) {
        PostPrivyRequest postdata = new PostPrivyRequest();
        Location loc = new Location();
        loc.setLat(placeToAdd.getLatLng().latitude);
        loc.setLng(placeToAdd.getLatLng().longitude);
        postdata.setLocation(loc);
        postdata.setAccuracy(50);
        postdata.setName(getString(R.string.toilet_name_default));
        postdata.setAddress(placeToAdd.getAddress().toString());
        ArrayList<String> types = new ArrayList<String>();
        types.add(getString(R.string.types_default));
        postdata.setTypes(types);
        postdata.setAddress(mSharedPreferences.getString(EMAIL, ""));
        postdata.setLanguage(getString(R.string.language_default));
        new NetworkRequest(this, postdata).postRequest();
    }

    private void loadAddNewPrivyActivity() {
        if (checkIfLoggedIn()) {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            } catch (Exception e) {
                Log.d(DEBUG, e.toString());
            }
        } else {
            startGoogleSignInActivity(RC_SIGN_IN_NEW_PRIVY_REQUEST);
            snackMsg(getString(R.string.request_location_permission));
        }
    }

    private void loadMapFragment() {
        navigationView.getMenu().getItem(0).setChecked(true);
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        snackMsg(getString(R.string.google_api_client_connection_faliure_msg));
        Log.d(DEBUG, "OnConnectionFailed: " + connectionResult.toString());
    }


}

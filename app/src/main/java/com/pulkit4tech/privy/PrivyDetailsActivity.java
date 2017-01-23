package com.pulkit4tech.privy;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.GsonBuilder;
import com.pulkit4tech.privy.data.PostPrivyDeleteRequest;
import com.pulkit4tech.privy.data.json.LocationData;
import com.pulkit4tech.privy.data.json.MarkerData;
import com.pulkit4tech.privy.data.json.PostPrivyDeleteResponse;

import org.json.JSONObject;

import static android.content.Intent.ACTION_VIEW;
import static com.pulkit4tech.privy.constants.Constants.DEBUG;

public class PrivyDetailsActivity extends AppCompatActivity {

    private MarkerData data;
    private String GOOGLE_MAP_API_KEY = "key";
    private String MAPS = "maps";
    private String API = "api";
    private String PLACE = "place";
    private String DELETE = "delete";
    private String JSON = "json";
    private String OK = "OK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privy_details);
        setToolBar();
        setFab();

        data = retrieveData();
        if (data != null) {
            Log.d(DEBUG, data.toString());
            setData();
        }

        setDelete();
    }

    private void setDelete() {
        Button delete = (Button) findViewById(R.id.delete_privy);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePrivy();
            }
        });
    }

    private void deletePrivy() {
        JsonObjectRequest request = new JsonObjectRequest(
                postDeleteUrl(),
                postJsonBody(),
                postDelResListner,
                errorListener
        );

        Volley.newRequestQueue(this).add(request);
    }

    private JSONObject postJsonBody() {
        JSONObject jsonObject = null;
        try {
            PostPrivyDeleteRequest delete_data = new PostPrivyDeleteRequest();
            delete_data.setPlaceid(data.getPlaceid());
            jsonObject = new JSONObject(new GsonBuilder().create().toJson(delete_data));
        } catch (Exception e) {
            Log.d(DEBUG, e.toString());
        }
        return jsonObject;
    }

    private String postDeleteUrl() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(getString(R.string.request_api))
                .appendPath(MAPS)
                .appendPath(API)
                .appendPath(PLACE)
                .appendPath(DELETE)
                .appendPath(JSON)
                .appendQueryParameter(GOOGLE_MAP_API_KEY, getString(R.string.google_maps_key));
        String url = builder.build().toString();
        Log.d(DEBUG, url);
        return url;
    }

    private Response.Listener postDelResListner = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            PostPrivyDeleteResponse res = new GsonBuilder().create().fromJson(response.toString(), PostPrivyDeleteResponse.class);
            if (res.getStatus().equals(OK)) {
                Toast.makeText(getApplicationContext(), getString(R.string.delete_request_success), Toast.LENGTH_LONG).show();
                finish();
            } else {
                Log.d(DEBUG, res.toString());
                snackMsg(getString(R.string.delete_request_failed) + "Reason : " + res.getStatus());
            }
        }
    };

    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            snackMsg(getString(R.string.network_error));
            Log.d(DEBUG, error.toString());
        }
    };

    private void setFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDirection();
            }
        });
    }

    private void getDirection() {
        try {
            Intent intent = new Intent(ACTION_VIEW,
                    Uri.parse(String.format("http://maps.google.com/maps?daddr=%f,%f", data.getGeometry().getLocation().getLat(), data.getGeometry().getLocation().getLng())));
            startActivity(intent);
        } catch (Exception e) {
            Log.d(DEBUG, e.toString());
            snackMsg(getString(R.string.error_processing_request));
        }
    }

    private void setToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions,ConstantConditions
        getSupportActionBar().setTitle(R.string.toolbar_title_privy_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void setData() {
        TextView name = (TextView) findViewById(R.id.privy_details_name);
        TextView geometry = (TextView) findViewById(R.id.privy_details_geo);
        TextView open_now = (TextView) findViewById(R.id.privy_details_opening);
        TextView vicinity = (TextView) findViewById(R.id.privy_details_vicinity);
        TextView ratings = (TextView) findViewById(R.id.privy_details_ratings);

        name.setText(checkNameValue(data.getName()));
        geometry.setText(checkGeoValue(data.getGeometry()));
        open_now.setText(checkOpening(data.getOpeninghours()));
        vicinity.setText(checkVicinityValue(data.getVicinity()));
        ratings.setText(checkRatingValue(data.getRating()));
    }

    private String checkNameValue(String val) {
        if (val == null)
            return getString(R.string.n_a);
        return val;
    }

    private String checkVicinityValue(String vicinity) {
        if (vicinity == null)
            return getString(R.string.n_a);

        return vicinity;
    }

    private String checkRatingValue(float rating) {
        if (rating == 0)
            return getString(R.string.n_a);

        return String.format("%.2f", rating);
    }

    private String checkOpening(MarkerData.OpeningHours openingHours) {
        if (openingHours == null)
            return getString(R.string.n_a);

        return openingHours.isOpennow() ? "YES" : "NO";
    }

    private String checkGeoValue(LocationData geo) {
        if (geo == null)
            return getString(R.string.n_a);
        return "Lat : " + String.format("%.2f", geo.getLocation().getLat()) + " Lng : " + String.format("%.2f", geo.getLocation().getLng());
    }

    private MarkerData retrieveData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            return (MarkerData) getIntent().getSerializableExtra(getString(R.string.marker_data));
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void snackMsg(String msg) {
        Snackbar.make((CardView) findViewById(R.id.cardview), msg, Snackbar.LENGTH_LONG).show();
    }
}

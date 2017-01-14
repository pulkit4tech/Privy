package com.pulkit4tech.privy;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pulkit4tech.privy.data.json.LocationData;
import com.pulkit4tech.privy.data.json.MarkerData;

import static android.content.Intent.ACTION_VIEW;
import static com.pulkit4tech.privy.constants.Constants.DEBUG;

public class PrivyDetailsActivity extends AppCompatActivity {

    private MarkerData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privy_details);
        setToolBar();
        setFab();

        data = retrieveData();
        Log.d(DEBUG, data.toString());
        if (data != null) {
            setData();
        }

    }

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
        getSupportActionBar().setTitle("Privy Details");
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
            return "N/A";
        return val;
    }

    private String checkVicinityValue(String vicinity) {
        if (vicinity == null)
            return "N/A";

        return vicinity;
    }

    private String checkRatingValue(float rating) {
        if (rating == 0)
            return "N/A";

        return String.format("%.2f", rating);
    }

    private String checkOpening(MarkerData.OpeningHours openingHours) {
        if (openingHours == null)
            return "N/A";

        return openingHours.isOpennow() ? "YES" : "NO";
    }

    private String checkGeoValue(LocationData geo) {
        if (geo == null)
            return "N/A";
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
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    private void snackMsg(String msg) {
        Snackbar.make((RelativeLayout) findViewById(R.id.relative_layout_privy_detail), msg, Snackbar.LENGTH_LONG).show();
    }
}

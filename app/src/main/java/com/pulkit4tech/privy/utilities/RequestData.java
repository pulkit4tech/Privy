package com.pulkit4tech.privy.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pulkit4tech.privy.R;
import com.pulkit4tech.privy.data.MarkerData;

import java.util.ArrayList;

import static com.pulkit4tech.privy.constants.Constants.DEBUG;


public class RequestData {

    private LatLng myLocation;
    private ArrayList<MarkerData> markerData;
    private RequestQueue requestQueue;
    private Context mContext;
    private GoogleMap mMap;

    private String LOCATION = "location";
    private String NAME_KEY = "name";
    private String NAME_VALUE = "toilet";
    private String GOOGLE_MAP_API_KEY = "key";
    private String MAPS = "maps";
    private String API = "api";
    private String PLACE = "place";
    private String NEARBY = "nearbysearch";
    private String TYPE = "json";

    public RequestData(Context mContext, GoogleMap mMap, LatLng myLocation){
        this.myLocation = myLocation;
        this.mContext = mContext;
        this.mMap = mMap;
    }

    public void getMarkerData(){
        requestQueue = Volley.newRequestQueue(mContext);
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(mContext.getResources().getString(R.string.request_api))
                .appendPath(MAPS)
                .appendPath(API)
                .appendPath(PLACE)
                .appendPath(NEARBY)
                .appendPath(TYPE)
                .encodedQuery(LOCATION + "=" +String.format("%f,%f",myLocation.latitude,myLocation.longitude))
                .appendQueryParameter(NAME_KEY,NAME_VALUE)
                .appendQueryParameter(GOOGLE_MAP_API_KEY,mContext.getResources().getString(R.string.google_maps_key));

        final String requestUrl = builder.build().toString();
        Log.d(DEBUG,"URL:  " + requestUrl);

        StringRequest request = new StringRequest(Request.Method.GET, requestUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // testing response
                Toast.makeText(mContext,"Got response",Toast.LENGTH_SHORT).show();
                Log.d(DEBUG, "Response:  " + response);

                // dummy data test TODO : Replace with JSON parsing
                markerData = new ArrayList<>();
                markerData.add(new MarkerData());

                for (MarkerData data : markerData){
                    mMap.addMarker(new MarkerOptions().position(data.getTest()).title("Test"));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(mContext,"ERROR!!",Toast.LENGTH_SHORT).show();
                Log.d(DEBUG, error.toString());
            }
        });

        requestQueue.add(request);
    }
}

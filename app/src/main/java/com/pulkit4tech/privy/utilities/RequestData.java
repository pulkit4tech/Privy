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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pulkit4tech.privy.R;
import com.pulkit4tech.privy.data.json.MarkerData;
import com.pulkit4tech.privy.data.json.PrivyPost;

import java.util.HashMap;

import static com.pulkit4tech.privy.constants.Constants.DEBUG;

public class RequestData {

    private LatLng myLocation;
    private RequestQueue requestQueue;
    private Context mContext;
    private GoogleMap mMap;
    private Gson gson;

    private String LOCATION = "location";
    private String NAME_KEY = "name";
    private String NAME_VALUE = "toilet";
    private String RADIUS = "radius";
    private String GOOGLE_MAP_API_KEY = "key";
    private String MAPS = "maps";
    private String API = "api";
    private String PLACE = "place";
    private String NEARBY = "nearbysearch";
    private String TYPE = "json";
    private HashMap<Integer, MarkerData> hm;

    public RequestData(Context mContext, GoogleMap mMap, HashMap<Integer, MarkerData> universalMarkerHashMap, LatLng myLocation) {
        this.myLocation = myLocation;
        this.mContext = mContext;
        this.mMap = mMap;
        hm = universalMarkerHashMap;
        gson = new GsonBuilder().create();
    }

    public void getMarkerData() {
        requestQueue = Volley.newRequestQueue(mContext);
        String requestUrl = getRequestUrl();
        Log.d(DEBUG, "URL:  " + requestUrl);

        StringRequest request = new StringRequest(Request.Method.GET, requestUrl, resListner, errorListner);

        requestQueue.add(request);
    }

    private Response.Listener resListner = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.d(DEBUG, "Response:  " + response);
            PrivyPost post = gson.fromJson(response, PrivyPost.class);
            if (post.getResults().size() == 0) {
                if (post.getStatus().equals("ZERO_RESULTS"))
                    makeToast(mContext, mContext.getResources().getString(R.string.no_result_msg));
                else {
                    makeToast(mContext, mContext.getResources().getString(R.string.error_retrieving_data_msg));
                    Log.e(DEBUG, post.toString());
                }
            } else {
                addMarkers(post);
            }
        }
    };

    private void addMarkers(PrivyPost post) {
        for (MarkerData data : post.getResults()) {
            Log.d(DEBUG, data.toString());
            Marker temp = mMap.addMarker(new MarkerOptions().position(new LatLng(data.getGeometry().getLocation().getLat(), data.getGeometry().getLocation().getLng())).title(data.getName()));
            hm.put(temp.hashCode(), data);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //testing
                if (hm.containsKey(marker.hashCode()))
                    Toast.makeText(mContext, hm.get(marker.hashCode()).getName(), Toast.LENGTH_SHORT).show();
                else
                    marker.remove();
                return false;
            }
        });
    }

    private Response.ErrorListener errorListner = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            makeToast(mContext, mContext.getResources().getString(R.string.network_error));
            Log.d(DEBUG, error.toString());
        }
    };

    private String getRequestUrl() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(mContext.getResources().getString(R.string.request_api))
                .appendPath(MAPS)
                .appendPath(API)
                .appendPath(PLACE)
                .appendPath(NEARBY)
                .appendPath(TYPE)
                .encodedQuery(LOCATION + "=" + String.format("%f,%f", myLocation.latitude, myLocation.longitude))
                .appendQueryParameter(NAME_KEY, NAME_VALUE)
                .appendQueryParameter(RADIUS, mContext.getResources().getString(R.string.radius))
                .appendQueryParameter(GOOGLE_MAP_API_KEY, mContext.getResources().getString(R.string.google_maps_key));

        return builder.build().toString();
    }

    private void makeToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}

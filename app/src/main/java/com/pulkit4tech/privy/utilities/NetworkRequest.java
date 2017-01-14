package com.pulkit4tech.privy.utilities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pulkit4tech.privy.PrivyDetailsActivity;
import com.pulkit4tech.privy.R;
import com.pulkit4tech.privy.data.json.GetPrivyResponse;
import com.pulkit4tech.privy.data.json.MarkerData;
import com.pulkit4tech.privy.data.json.PostPrivyRequest;
import com.pulkit4tech.privy.data.json.PostPrivyResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.pulkit4tech.privy.constants.Constants.DEBUG;

public class NetworkRequest {

    private LatLng myLocation;
    private RequestQueue requestQueue;
    private Activity mContext;
    private PostPrivyRequest data;
    private GoogleMap mMap;
    private Gson gson;

    private String LOCATION = "location";
    private String NAME_KEY = "name";
    private String NAME_VALUE = "toilet";
    private String RADIUS = "radius";
    //    private String RANKBY = "rankby";
//    private String DISTANCE = "distance";
    private String GOOGLE_MAP_API_KEY = "key";
    private String MAPS = "maps";
    private String API = "api";
    private String PLACE = "place";
    private String NEARBY = "nearbysearch";
    private String ADD = "add";
    private String JSON = "json";
    private String OK = "OK";

    private HashMap<String, MarkerData> hm;

    public NetworkRequest(Activity mContext, GoogleMap mMap, HashMap<String, MarkerData> universalMarkerHashMap, LatLng myLocation) {
        this.myLocation = myLocation;
        this.mContext = mContext;
        this.mMap = mMap;
        hm = universalMarkerHashMap;
        gson = new GsonBuilder().create();
        requestQueue = Volley.newRequestQueue(mContext);
    }

    public NetworkRequest(Activity mContext, PostPrivyRequest data) {
        this.mContext = mContext;
        this.data = data;
        gson = new GsonBuilder().create();
        requestQueue = Volley.newRequestQueue(mContext);
    }

    public void getMarkerData() {
        String requestUrl = getRequestUrl();
        Log.d(DEBUG, "URL:  " + requestUrl);

        StringRequest request = new StringRequest(Request.Method.GET, requestUrl, getResListner, getErrorListner);
        requestQueue.add(request);
    }

    public void postRequest() {
        JsonObjectRequest request = new JsonObjectRequest(
                postRequestUrl(),
                postJsonBody(),
                postResListner,
                postErrorListner
        );

        requestQueue.add(request);
    }

    private Response.Listener getResListner = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Log.d(DEBUG, "Response:  " + response);
            GetPrivyResponse post = gson.fromJson(response, GetPrivyResponse.class);
            if (post.getResults().size() == 0) {
                if (post.getStatus().equals("ZERO_RESULTS"))
                    snackMsg(mContext.getString(R.string.no_result_msg));
                else {
                    snackMsg(mContext.getString(R.string.error_retrieving_data_msg));
                    Log.e(DEBUG, post.toString());
                }
            } else {
                addMarkers(post);
            }
        }
    };

    private void addMarkers(GetPrivyResponse post) {
        for (MarkerData data : post.getResults()) {
            Log.d(DEBUG, data.toString());
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
            Marker temp = mMap.addMarker(new MarkerOptions().position(new LatLng(data.getGeometry().getLocation().getLat(), data.getGeometry().getLocation().getLng())).title(data.getName()).icon(bitmapDescriptor));
            hm.put(temp.getId(), data);
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //testing
                if (hm.containsKey(marker.getId()))
                    snackActionMsg(marker);
                else
                    marker.remove();
                return false;
            }
        });
    }

    private Response.ErrorListener getErrorListner = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            snackMsg(mContext.getString(R.string.network_error));
            Log.d(DEBUG, error.toString());
        }
    };

    private String getRequestUrl() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(mContext.getString(R.string.request_api))
                .appendPath(MAPS)
                .appendPath(API)
                .appendPath(PLACE)
                .appendPath(NEARBY)
                .appendPath(JSON)
                .encodedQuery(LOCATION + "=" + String.format("%f,%f", myLocation.latitude, myLocation.longitude))
                .appendQueryParameter(NAME_KEY, NAME_VALUE)
                //   .appendQueryParameter(RANKBY,DISTANCE)
                .appendQueryParameter(RADIUS, mContext.getString(R.string.radius))
                .appendQueryParameter(GOOGLE_MAP_API_KEY, mContext.getString(R.string.google_maps_key));

        return builder.build().toString();
    }

    private Response.Listener postResListner = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            PostPrivyResponse res = gson.fromJson(response.toString(), PostPrivyResponse.class);
            if (res.getStatus().equals(OK)) {
                snackMsg(mContext.getString(R.string.add_privy_success));
            } else {
                Log.d(DEBUG, res.toString());
                snackMsg(mContext.getString(R.string.add_privy_failed));
            }
        }
    };

    private Response.ErrorListener postErrorListner = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            snackMsg(mContext.getString(R.string.network_error));
            Log.d(DEBUG, error.toString());
        }
    };

    private String postRequestUrl() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(mContext.getString(R.string.request_api))
                .appendPath(MAPS)
                .appendPath(API)
                .appendPath(PLACE)
                .appendPath(ADD)
                .appendPath(JSON)
                .appendQueryParameter(GOOGLE_MAP_API_KEY, mContext.getString(R.string.google_maps_key));
        String url = builder.build().toString();
        Log.d(DEBUG, url);
        return url;
    }

    private JSONObject postJsonBody() {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(gson.toJson(data));
        } catch (JSONException e) {
            Log.d(DEBUG, e.toString());
        }
        Log.d(DEBUG, jsonObject.toString());
        return jsonObject;
    }

    private void snackMsg(String msg) {
        Snackbar.make(mContext.findViewById(R.id.coordinator_layout), msg, Snackbar.LENGTH_LONG).show();
    }

    private void snackActionMsg(final Marker marker) {
        Snackbar snackbar = Snackbar.make(mContext.findViewById(R.id.coordinator_layout), hm.get(marker.getId()).getName(), Snackbar.LENGTH_LONG);
        snackbar.setAction("Details", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PrivyDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(mContext.getString(R.string.marker_data), hm.get(marker.getId()));
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        snackbar.show();
    }
}

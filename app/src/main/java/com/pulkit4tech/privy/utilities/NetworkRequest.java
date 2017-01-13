package com.pulkit4tech.privy.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

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
    private Context mContext;
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

    public NetworkRequest(Context mContext, GoogleMap mMap, HashMap<String, MarkerData> universalMarkerHashMap, LatLng myLocation) {
        this.myLocation = myLocation;
        this.mContext = mContext;
        this.mMap = mMap;
        hm = universalMarkerHashMap;
        gson = new GsonBuilder().create();
        requestQueue = Volley.newRequestQueue(mContext);
    }

    public NetworkRequest(Context mContext, PostPrivyRequest data) {
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
                    makeToast(mContext, hm.get(marker.getId()).getName());
                else
                    marker.remove();
                return false;
            }
        });
    }

    private Response.ErrorListener getErrorListner = new Response.ErrorListener() {
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
                .appendPath(JSON)
                .encodedQuery(LOCATION + "=" + String.format("%f,%f", myLocation.latitude, myLocation.longitude))
                .appendQueryParameter(NAME_KEY, NAME_VALUE)
                //   .appendQueryParameter(RANKBY,DISTANCE)
                .appendQueryParameter(RADIUS, mContext.getResources().getString(R.string.radius))
                .appendQueryParameter(GOOGLE_MAP_API_KEY, mContext.getResources().getString(R.string.google_maps_key));

        return builder.build().toString();
    }

    private Response.Listener postResListner = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            PostPrivyResponse res = gson.fromJson(response.toString(), PostPrivyResponse.class);
            if (res.getStatus().equals(OK)) {
                makeToast(mContext, "Successfully added Privy. Thanks contributing to community.");
            } else {
                Log.d(DEBUG, res.toString());
                makeToast(mContext, "Some error while adding Privy!! Please try after sometime or please report.");
            }
        }
    };

    private Response.ErrorListener postErrorListner = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            makeToast(mContext, mContext.getResources().getString(R.string.network_error));
            Log.d(DEBUG, error.toString());
        }
    };

    private String postRequestUrl() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(mContext.getResources().getString(R.string.request_api))
                .appendPath(MAPS)
                .appendPath(API)
                .appendPath(PLACE)
                .appendPath(ADD)
                .appendPath(JSON)
                .appendQueryParameter(GOOGLE_MAP_API_KEY, mContext.getResources().getString(R.string.google_maps_key));
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

    private void makeToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}

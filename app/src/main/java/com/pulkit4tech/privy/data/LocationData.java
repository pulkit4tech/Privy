package com.pulkit4tech.privy.data;

import com.google.android.gms.maps.model.LatLng;

public class LocationData {
    private LatLng latLng;
    private String Description;

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}

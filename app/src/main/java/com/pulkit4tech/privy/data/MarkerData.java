package com.pulkit4tech.privy.data;

import com.google.android.gms.maps.model.LatLng;

public class MarkerData {

    //TODO : This is dummy test => USE GSON to Parse JSON

    private LatLng test;

    public MarkerData(){
        test = new LatLng(28.7037992,77.1006268);
    }

    public LatLng getTest() {
        return test;
    }

    public void setTest(LatLng test) {
        this.test = test;
    }
}

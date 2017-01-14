package com.pulkit4tech.privy.data;

public class PostPrivyDeleteRequest {
    private String place_id;

    public String getPlaceid() {
        return place_id;
    }

    public void setPlaceid(String place_id) {
        this.place_id = place_id;
    }

    @Override
    public String toString() {
        return "PostPrivyDeleteRequest{" +
                "place_id='" + place_id + '\'' +
                '}';
    }
}

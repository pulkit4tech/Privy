package com.pulkit4tech.privy.data.json;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MarkerData implements Serializable {
    private LocationData geometry;

    @SerializedName("icon")
    private String icon_url;
    private String id;
    private String name;
    private String place_id;
    private String scope;
    private List<String> types;
    private String vicinity;
    private OpeningHours opening_hours;
    private float rating;

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public OpeningHours getOpeninghours() {
        return opening_hours;
    }

    public void setOpeninghours(OpeningHours opening_hours) {
        this.opening_hours = opening_hours;
    }

    public LocationData getGeometry() {
        return geometry;
    }

    public void setGeometry(LocationData geometry) {
        this.geometry = geometry;
    }

    public String getIconurl() {
        return icon_url;
    }

    public void setIconurl(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceid() {
        return place_id;
    }

    public void setPlaceid(String place_id) {
        this.place_id = place_id;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    @Override
    public String toString() {
        return "MarkerData{" +
                "geometry=" + geometry +
                ", icon_url='" + icon_url + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", place_id='" + place_id + '\'' +
                ", scope='" + scope + '\'' +
                ", types=" + types +
                ", vicinity='" + vicinity + '\'' +
                ", opening_hours=" + opening_hours +
                ", rating=" + rating +
                '}';
    }

    public class OpeningHours implements Serializable{
        private boolean open_now;
        private ArrayList<String> weekday_text;

        public boolean isOpennow() {
            return open_now;
        }

        public void setOpennow(boolean open_now) {
            this.open_now = open_now;
        }

        public ArrayList<String> getWeekdaytext() {
            return weekday_text;
        }

        public void setWeekdaytext(ArrayList<String> weekday_text) {
            this.weekday_text = weekday_text;
        }

        @Override
        public String toString() {
            return "OpeningHours{" +
                    "open_now=" + open_now +
                    ", weekday_text=" + weekday_text +
                    '}';
        }
    }
}

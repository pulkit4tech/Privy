package com.pulkit4tech.privy.data.json;

import java.io.Serializable;

public class LocationData implements Serializable {
    private Location location;

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "LocationData{" +
                "location=" + location +
                '}';
    }
}

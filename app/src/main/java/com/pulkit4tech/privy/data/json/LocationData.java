package com.pulkit4tech.privy.data.json;

public class LocationData {
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

package com.pulkit4tech.privy.data.json;

import java.util.ArrayList;

public class PostPrivyRequest {
    private Location location;
    private int accuracy;
    private String name;
    private String address;
    private ArrayList<String> types;
    private String language;
    private String website;

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<String> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<String> types) {
        this.types = types;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "PostPrivyRequest{" +
                "location=" + location +
                ", accuracy=" + accuracy +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", types=" + types +
                ", language='" + language + '\'' +
                ", website='" + website + '\'' +
                '}';
    }
}

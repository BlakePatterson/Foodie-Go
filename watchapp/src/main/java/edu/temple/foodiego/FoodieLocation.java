package edu.temple.foodiego;

import android.location.Location;

import com.google.android.gms.maps.model.Marker;

public class FoodieLocation {

    //TODO: update class diagram with proper attributes regarding rating
    private String key;
    private String name;
    private double latitude;
    private double longitude;
    private double rating;

    private Marker marker;

    public FoodieLocation(String name, double latitude, double longitude, double rating) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
    }

    public FoodieLocation(String name, double latitude, double longitude, double rating, String key) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rating = rating;
        this.key = key;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLocation(Location location) {
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    public Location getLocation() {
        Location loc = new Location("dummyprovider");
        loc.setLongitude(this.longitude);
        loc.setLatitude(this.latitude);
        return loc;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public String getKey() {
        return key;
    }
}
package edu.temple.foodiego;

import android.location.Location;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FoodieLocationTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void getName() {
        String name = "Location";
        FoodieLocation location = new FoodieLocation(name, 10.0, 10.0, 5.0);
        assertEquals(location.getName(), name);
    }

    @Test
    public void setName() {
        String name = "Location";
        String notName = "NotLocation";
        FoodieLocation location = new FoodieLocation(notName, 10.0, 10.0, 5.0);
        location.setName(name);
        assertEquals(location.getName(), name);
    }

    @Test
    public void setLatitude() {
        double lat = 0;
        double notLat = 11.0;
        FoodieLocation location = new FoodieLocation("Location", notLat, 10.0, 5.0);
        location.setLatitude(lat);
        assertEquals(String.valueOf(location.getLocation().getLatitude()), String.valueOf(lat));
    }

    @Test
    public void setLongitude() {
        double lon = 0;
        double notLat = 11.0;
        FoodieLocation location = new FoodieLocation("Location", notLat, 10.0, 5.0);
        location.setLongitude(lon);
        assertEquals(String.valueOf(location.getLocation().getLatitude()), String.valueOf(lon));
    }

    @Test
    public void setLocation() {
        Location loc = new Location("dummyprovider");
        loc.setLongitude(0);
        loc.setLatitude(0);
        Location notLoc = new Location("dummyprovider");
        loc.setLongitude(10);
        loc.setLatitude(10);
        FoodieLocation location = new FoodieLocation("Location", notLoc.getLatitude(), notLoc.getLongitude(), 5.0);
        location.setLocation(loc);
        assertEquals(String.valueOf(location.getLocation().getLatitude()), String.valueOf(loc.getLatitude()));
    }

    @Test
    public void getLocation() {
        Location loc = new Location("dummyprovider");
        loc.setLongitude(0);
        loc.setLatitude(0);
        FoodieLocation location = new FoodieLocation("Location", loc.getLatitude(), loc.getLongitude(), 5.0);
        assertEquals(String.valueOf(location.getLocation().getLatitude()), String.valueOf(loc.getLatitude()));
    }

    @Test
    public void getRating() {
        double rating = 5.0;
        FoodieLocation location = new FoodieLocation("name", 10.0, 10.0, rating);
        assertEquals(String.valueOf(location.getRating()), String.valueOf(rating));
    }

    @Test
    public void setRating() {
        double rating = 5.0;
        double notRating = 2.5;
        FoodieLocation location = new FoodieLocation("name", 10.0, 10.0, notRating);
        location.setRating(rating);
        assertEquals(String.valueOf(location.getRating()), String.valueOf(rating));
    }

    @Test
    public void getKey() {
        String key = "12345";
        FoodieLocation location = new FoodieLocation("name", 10.0, 10.0, 5.0, key);
        assertEquals(location.getKey(), key);
    }
}
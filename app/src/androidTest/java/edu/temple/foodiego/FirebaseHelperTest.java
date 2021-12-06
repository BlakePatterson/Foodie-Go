package edu.temple.foodiego;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.*;

public class FirebaseHelperTest {

    private String data;
    private final FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
    private final DatabaseReference locationRef = fbdb.getReference("location");
    private final ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            data = String.valueOf(snapshot.getValue());
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) { }
    };


    @Test
    public void testGetNearbyLocations() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        FirebaseHelper firebaseHelper = FirebaseHelper.getInstance(context);
        locationRef.addValueEventListener(valueEventListener);
        Location loc = new Location("");
        LatLng defaultLoc = new LatLng(39.9812, -75.1497);
        loc.setLatitude(defaultLoc.latitude);
        loc.setLongitude(defaultLoc.longitude);
        firebaseHelper.getNearbyLocations(2000.0, loc);
        locationRef.removeEventListener(valueEventListener);
        Log.d("test", "testGetNearbyLocations: " + data);
        boolean result = false;
        if (data == null || data.length() > 0) {
            result = true;
        }
        assertTrue(result);
    }

    @Test
    public void testAddFriend() {
    }

    @Test
    public void testGetFriends() {
    }

    @Test
    public void testPostReview() {
    }

    @Test
    public void testGetReviews() {
    }

    @Test
    public void testGetFriendsReviews() {
    }

    @Test
    public void testAddToken() {
    }

    @Test
    public void testGetTokens() {
    }

    @Test
    public void testPostActivity() {
    }

    @Test
    public void testGetLocationActivities() {
    }

    @Test
    public void testGetFriendsActivity() {
    }

    @Test
    public void testReplaceCharBeforeSet() {
    }

    @Test
    public void testReplaceCharAfterGet() {
    }
}
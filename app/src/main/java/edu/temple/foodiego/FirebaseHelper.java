package edu.temple.foodiego;
import com.android.volley.*;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseHelper {

    private static FirebaseHelper instance;
    private static Context ctxt;

    private FirebaseDatabase database;

    private FirebaseHelper(Context context) {
        ctxt = context;
        this.database = FirebaseDatabase.getInstance();
    }

    public static synchronized FirebaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new FirebaseHelper(context);
        } else {
            ctxt = context;
        }
        return instance;
    }

    public static void getNearByLocations(Context context, Double radius, LatLng latLng)
    {
        StringRequest sq = new StringRequest(
                Request.Method.GET,
                //"https://maps.googleapis.com/maps/api/place/nearbysearch/json",
                "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                        +latLng.latitude+"%2C"+latLng.longitude+"&radius=1500&type=restaurant&keyword=cruise&key=AIzaSyC8JH8DtkIKCiZFw_kf2xKTR9qtlpym-CE",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response",response);
/*                        FoodieLocation foodieLocation = new FoodieLocation("restaurant",39.9545,-75.2026, 9);
//                        //Get a reference to the user field of the database
//                        FirebaseDatabase database = FirebaseDatabase.getInstance();
//                        DatabaseReference locationRef = database.getReference("location");
//
//                        //Add a new entry to the location list
//                        DatabaseReference newUserRef = locationRef.push();
//                        HashMap<String, String> locationDataMap = new HashMap<>();
//                        locationDataMap.put("name", foodieLocation.getName());
//                        locationDataMap.put("latitude", String.valueOf(foodieLocation.getLocation().getLatitude()));
//                        locationDataMap.put("longitude", String.valueOf(foodieLocation.getLocation().getLongitude()));
//                        locationDataMap.put("rating", String.valueOf(foodieLocation.getRating()));
//                        //Save the user data on the database
                        newUserRef.setValue(locationDataMap);
*/                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       error.printStackTrace();
                    }
                }
        ){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return super.getParams();
            }

            @Override
            public String getBodyContentType() {
                return super.getBodyContentType();
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(sq);
    }
}

package edu.temple.foodiego;
import com.android.volley.*;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
                        +latLng.latitude+"%2C"+latLng.longitude+"&radius="+radius+"&type=restaurant&key=AIzaSyC8JH8DtkIKCiZFw_kf2xKTR9qtlpym-CE",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference locationRef = database.getReference("location");

                        locationRef.get().addOnCompleteListener(task -> {
                            try {
                                //Convert response into json array
                                //Log.e("response",response);
                                JSONArray responseJsonArray = new JSONArray(((new JSONObject(response)).getString("results")));
                                //Convert database into json obj
                                JSONObject existJsonObjects = new JSONObject(String.valueOf(task.getResult().getValue()));
                                Log.e("Length",responseJsonArray.length()+"");


                                Boolean dataExistInDB = false;
                                for (int i=0; i< responseJsonArray.length(); i++) {
                                    //get the object's name at i in response,then replace special character.
                                    String responobjName=((JSONObject) responseJsonArray.get(i)).getString("name");
                                    responobjName= responobjName.replace(" ", "_")
                                               .replace('\'' ,'^')
                                               .replace(",","*");

                                    //Check if the obj is in database
                                    dataExistInDB = false;

                                    Iterator<String> keys = existJsonObjects.keys();
                                    //search database if there is name already exist
                                    while (keys.hasNext())
                                    {
                                        String existobjName =((JSONObject)existJsonObjects.get(keys.next())).getString("name");
                                        if(existobjName.trim().equals(responobjName.trim()))
                                        {
                                            dataExistInDB = true;
                                            Log.e("exist data",existobjName+" not added");break;
                                        }
                                    }
                                   //if not add it

                                    if (!dataExistInDB) {
                                        Log.e("adding data",responobjName);
                                        JSONObject location_obj = new JSONObject(new JSONObject(((JSONObject) responseJsonArray.get(i)).getString("geometry")).getString("location"));
                                        //Add a new entry to the location list
                                        DatabaseReference newRef = locationRef.push();
                                        HashMap<String, String> locationDataMap = new HashMap<>();
                                        locationDataMap.put("name", responobjName);
                                        locationDataMap.put("latitude", location_obj.getString("lat"));
                                        locationDataMap.put("longitude", location_obj.getString("lng"));
                                        locationDataMap.put("rating", ((JSONObject) responseJsonArray.get(i)).getString("rating"));
                                        //Save the user data on the database
                                        newRef.setValue(locationDataMap);
                                    }
                                }
                                } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                },
                error -> error.printStackTrace()
        ){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError { return super.getParams();}

            @Override
            public String getBodyContentType() { return super.getBodyContentType(); }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(sq);
    }
}

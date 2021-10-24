package edu.temple.foodiego;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class LocationDetailActivity extends AppCompatActivity {
    final String location_database = "location";
    TextView foodieName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);


        Intent receivedIntent= getIntent();
        String s = receivedIntent.getStringExtra("data");

        foodieName = findViewById(R.id.TVLocationName);
        foodieName.setText(s);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference locationRef = database.getReference(location_database);

        locationRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Log.e("location data", String.valueOf(task.getResult().getValue()));
                //foodieName.append(String.valueOf(task.getResult().getValue()));

                JSONObject jo = null;
                try {
                    jo = new JSONObject(String.valueOf(task.getResult().getValue()));
                    Iterator<String> keys = jo.keys();
                    while (keys.hasNext())
                    {
                        String key = keys.next();
                        if(((JSONObject)jo.get(key)).getString("Name").equals(s))
                        {
                            String content = "Name"+ ((JSONObject)jo.get(key)).getString("Name");
                            foodieName.setText(content);
                            return;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


//                if (!task.isSuccessful()) {
//                    Log.d(TAG, "login: Error getting data", task.getException());
//                }
//                else {
//                    try {
//                        JSONObject Data = new JSONObject(String.valueOf(task.getResult().getValue()));
//                        String s = Data.toString();
//                        Log.e("data",s);
////
//
//                    } catch (JSONException e) {
//                        //Close the loading dialog
//                        //loadingDialog.dismiss();
//                        e.printStackTrace();
//                        //Toast.makeText(LoginActivity.this, "Error Finding User Data, Please Try Again", Toast.LENGTH_LONG).show();
//                    }
//                }
            }
        });
        //addFoodieLication();
    }

    //a temporary method for creating a foodie location in database

    public void addFoodieLication()
    {
        FoodieLocation foodieLocation = new FoodieLocation("restaurant",39.9545,-75.2026, 9);
        //Get a reference to the user field of the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference locationRef = database.getReference("location");

        //Add a new entry to the location list
        DatabaseReference newUserRef = locationRef.push();
        HashMap<String, String> locationDataMap = new HashMap<>();
        locationDataMap.put("name", foodieLocation.getName());
        locationDataMap.put("latitude", String.valueOf(foodieLocation.getLocation().getLatitude()));
        locationDataMap.put("longitude", String.valueOf(foodieLocation.getLocation().getLongitude()));
        locationDataMap.put("rating", String.valueOf(foodieLocation.getRating()));
        //Save the user data on the database
        newUserRef.setValue(locationDataMap);
    }


}
package edu.temple.foodiego;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class LocationDetailActivity extends AppCompatActivity {

    private FoodieLocation location;

    private FoodieUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);

        Bundle startIntent = getIntent().getExtras();
        if(startIntent != null) {
            String name = startIntent.getString(getString(R.string.locationDetailNameKey));
            double lat = startIntent.getDouble(getString(R.string.locationDetailLatKey));
            double lon = startIntent.getDouble(getString(R.string.locationDetailLongKey));
            double rating = startIntent.getDouble(getString(R.string.locationDetailRatingKey));
            location = new FoodieLocation(name, lat, lon, rating);

            String username = startIntent.getString(getString(R.string.username_bundle_key));
            String firstname = startIntent.getString(getString(R.string.firstname_bundle_key));
            String lastname = startIntent.getString(getString(R.string.lastname_bundle_key));
            String key = startIntent.getString(getString(R.string.key_bundle_key));
            user = new FoodieUser(username, firstname, lastname, key);

            Log.d(TAG, "onCreate: location detail activity started with: location: " + location.getName() + "; user: " + user.getUsername());
        }


    }
}
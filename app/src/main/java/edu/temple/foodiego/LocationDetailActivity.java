package edu.temple.foodiego;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
        } else {
            location = new FoodieLocation("Error", 0, 0, 0);
            user = new FoodieUser("", "", "", "");
        }

        TextView locationNameTextView = findViewById(R.id.locationNameTextView);
        TextView ratingTextView = findViewById(R.id.ratingTextView);
        FloatingActionButton beginRouteButton = findViewById(R.id.beginRouteFloatingActionButton);
        FloatingActionButton leaveReviewButton = findViewById(R.id.leaveReviewFloatingActionButton);
        FloatingActionButton claimTokenButton = findViewById(R.id.claimTokenFloatingActionButton);

        locationNameTextView.setText(location.getName());
        ratingTextView.setText(String.valueOf(location.getRating()));

        beginRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //begin route button has been clicked
                Log.d(TAG, "onClick: start route to location has been clicked");
                startRouteToLocation();
            }
        });

        leaveReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //leave review button has been clicked
                Log.d(TAG, "onClick: leave review button has been clicked");
                openPublishReviewDialog();
            }
        });

        claimTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Claim token button has been clicked
                Log.d(TAG, "onClick: claim token button has been clicked");
                //TODO: perform a check to see if user is within range,
                // if they are perform logic to give them token
                // otherwise display a toast saying they cannot redeem token
            }
        });

    }

    public void openPublishReviewDialog() {

        //TODO: display a dialog to leave a review

    }

    public void postReviewToFirebase() {

    }

    public FoodieLocation startRouteToLocation() {
        //TODO: write functionality to send FoodieLocation to the MapFragment, initiating a route

        return null;
    }
}
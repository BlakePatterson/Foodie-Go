package edu.temple.foodiego;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class LocationDetailActivity extends AppCompatActivity {
    final String location_database = "location";
    TextView foodieName;
    Button reviewButton;
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
                openLeaveReviewDialog();
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
                if(user != null)
                {
                    FirebaseHelper.addToken(user, location);
                }
                else
                {
                    Log.e("claim fail","user not exist");
                }
            }
        });

    }

    public void openLeaveReviewDialog(){
        new AlertDialog.Builder(this).setView(R.layout.dialog_leave_review)
            .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Dialog d = (Dialog) dialog;
                        RatingBar bar = d.findViewById(R.id.ratingBar);
                        EditText reviewBox = d.findViewById(R.id.reviewBox);
                        double rating = bar.getRating();
                        String review = reviewBox.getText().toString();
                        if(review.equals("") || rating == 0){
                            Toast.makeText(LocationDetailActivity.this, "Please fill in both the rating and the review.", Toast.LENGTH_LONG).show();
                        }else{
                            //call postReview()
                            d.dismiss();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void postReviewToFirebase() {

    }

    public FoodieLocation startRouteToLocation() {
        //TODO: write functionality to send FoodieLocation to the MapFragment, initiating a route
        return null;
    }
}
package edu.temple.foodiego;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.ArrayList;

public class LocationDetailActivity extends AppCompatActivity {
    private FoodieLocation location;

    private FoodieUser user;

    private ArrayList<FoodieReview> reviews;

    private ArrayList<FoodieActivityLog> activity;

    private Location userLocation;
    BroadcastReceiver userLocationReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_detail);

        Bundle startIntent = getIntent().getExtras();
        Context context = this;
        if(startIntent != null) {
            String name = startIntent.getString(getString(R.string.locationDetailNameKey));
            double lat = startIntent.getDouble(getString(R.string.locationDetailLatKey));
            double lon = startIntent.getDouble(getString(R.string.locationDetailLongKey));
            double rating = startIntent.getDouble(getString(R.string.locationDetailRatingKey));
            String location_key = startIntent.getString("location_key");
            location = new FoodieLocation(name, lat, lon, rating,location_key);

            String username = startIntent.getString(getString(R.string.username_bundle_key));
            String firstname = startIntent.getString(getString(R.string.firstname_bundle_key));
            String lastname = startIntent.getString(getString(R.string.lastname_bundle_key));
            String key = startIntent.getString(getString(R.string.key_bundle_key));
            user = new FoodieUser(username, firstname, lastname, key);

            userLocation = (Location) startIntent.getParcelable("userLocation");

            Log.d(TAG, "onCreate: location detail activity started with: location: " + location.getName() + "; user: " + user.getUsername());
        } else {
            location = new FoodieLocation("Error", 0, 0, 0);
            user = new FoodieUser("", "", "", "");
        }

        TextView locationNameTextView = findViewById(R.id.locationNameTextView);
        TextView ratingTextView = findViewById(R.id.ratingTextView);
        FloatingActionButton leaveReviewButton = findViewById(R.id.leaveReviewFloatingActionButton);
        FloatingActionButton claimTokenButton = findViewById(R.id.claimTokenFloatingActionButton);

        locationNameTextView.setText(location.getName());
        ratingTextView.setText(String.valueOf(location.getRating()));

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
                if(user != null)
                {
                    if(userLocation.distanceTo(location.getLocation())<50){
                        FirebaseHelper.addToken(user, location, "arrival", 1, b -> {
                            if(b) {
                                Toast.makeText(context, "New token Granted.", Toast.LENGTH_LONG).show();
                                Log.d("Token","New token Granted.");

                                FoodieActivityLog foodieActivityLog = new FoodieActivityLog(user, location, "claimed a token", LocalDateTime.now());
                                FirebaseHelper.postActivity(foodieActivityLog);
                            }
                            else {
                                Toast.makeText(context, "Token is already granted.", Toast.LENGTH_LONG).show();
                                Log.d("Token","Token is already granted.");
                            }
                        });
                    } else
                    {
                        Toast.makeText(context, "Please get closer to this location.", Toast.LENGTH_LONG).show();
                        Log.d("Token","user is too far away to the location.");
                    }
                }
                else
                {
                    Log.d("claim fail","User not exist.");
                }
            }
        });

        RecyclerView reviewsRecyclerView = findViewById(R.id.reviewsRecyclerView);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseHelper.getInstance(this).getReviews(location, result -> {
            reviews = result;
            ReviewAdapter reviewAdapter = new ReviewAdapter(reviews);
            reviewsRecyclerView.setAdapter(reviewAdapter);
        });


        RecyclerView activityRecyclerView = findViewById(R.id.activityRecyclerView);
        activityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        FirebaseHelper.getInstance(this).getLocationActivities(location, result -> {
            activity = result;
            LocationActivityAdapter locationActivityAdapter = new LocationActivityAdapter(activity);
            activityRecyclerView.setAdapter(locationActivityAdapter);
        });


        userLocationReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                userLocation = (Location) intent.getParcelableExtra("userLocation");
            }
        };
        registerReceiver(userLocationReceiver,new IntentFilter("edu.temple.foodiego.userlocation"));
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(userLocationReceiver);
        super.onDestroy();
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

                            postReviewToFirebase(review, rating);

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

    public void postReviewToFirebase(String review, double rating) {
        Log.d(TAG, "postReviewToFirebase: posting review to firebase with rating: " + String.valueOf(rating) + "; review message: " + review);

        FirebaseHelper.getInstance(this).postReview(user, location, rating, review);
    }

}
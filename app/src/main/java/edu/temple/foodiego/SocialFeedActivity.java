package edu.temple.foodiego;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDate;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class SocialFeedActivity extends AppCompatActivity implements FirebaseHelper.GetFriendsActivityResponse, FirebaseHelper.GetFriendsResponse {
    private FoodieUser user;
    private RecyclerView list;
    private ArrayList<FoodieUser> friends;
    private ArrayList<FoodieActivityLog> feedData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_feed);

        Bundle startIntent = getIntent().getExtras();
        if(startIntent != null) {
            String username = startIntent.getString(getString(R.string.username_bundle_key));
            String firstname = startIntent.getString(getString(R.string.firstname_bundle_key));
            String lastname = startIntent.getString(getString(R.string.lastname_bundle_key));
            String key = startIntent.getString(getString(R.string.key_bundle_key));
            user = new FoodieUser(username, firstname, lastname, key);

            Log.d(TAG, "onCreate: social feed activity started with: user: " + user.getUsername());
        } else {
            user = new FoodieUser("", "", "", "");
        }
        if(savedInstanceState != null){
            feedData = (ArrayList<FoodieActivityLog>) savedInstanceState.getSerializable("feed");
        }
        if(feedData != null){
            Log.d("SocialFeedActivity", "onCreate: found feed data");
        }
        FirebaseHelper helper = FirebaseHelper.getInstance(SocialFeedActivity.this);
        helper.getFriends(user, SocialFeedActivity.this);
        list = findViewById(R.id.socialFeed);

        FloatingActionButton postActivityButton = findViewById(R.id.postActivityFloatingActionButton);

        postActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPostActivityDialog();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new SocialFeedAdapter(getFriendsActivity()));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable("feed", feedData);
        super.onSaveInstanceState(outState);
    }

    public ArrayList<FoodieActivityLog> getFriendsActivity(){
        if(feedData == null){
            Toast.makeText(this, "Still fetching data from database, please try again", Toast.LENGTH_LONG).show();
            ArrayList<FoodieActivityLog> result = new ArrayList<>();
            //result.add(new FoodieActivityLog(user, new FoodieLocation("Nowhere", 0, 0, 0), "did nothing", LocalDate.now()));
            return result;
        }else{
            return feedData;
        }
    }

    public void openPostActivityDialog(){
        new AlertDialog.Builder(this)
                .setView(R.layout.dialog_post_activity)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.postButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Dialog d = (Dialog) dialog;
                        EditText locationNameField = d.findViewById(R.id.locationNameInput);
                        EditText socialActivityField = d.findViewById(R.id.socialActivityInput);
                        String location = locationNameField.getText().toString();
                        String message = socialActivityField.getText().toString();
                        if(location.equals("") || message.equals("")){
                            Toast.makeText(SocialFeedActivity.this, "Please fill out both fields", Toast.LENGTH_LONG).show();
                            Log.d(TAG, "onClick: one or more fields were blank");
                        }else{
                            postActivityToFirebase(new FoodieActivityLog(user, new FoodieLocation(location, 0, 0, 0), message, LocalDate.now()));
                        }
                    }
                })
                .show();

    }

    public void postActivityToFirebase(FoodieActivityLog activity){
        FirebaseHelper.postActivity(activity);
    }

    @Override
    public void getFriendsActivityResult(ArrayList<FoodieActivityLog> logs) {
        feedData = logs;
        Log.d(TAG, "getFriendsActivityResult: data retrieved");
        list.setAdapter(new SocialFeedAdapter(getFriendsActivity()));
        list.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void getFriendsResult(ArrayList<FoodieUser> friends) {
        this.friends = friends;
        FirebaseHelper helper = FirebaseHelper.getInstance(SocialFeedActivity.this);
        helper.getFriendsActivity(friends, SocialFeedActivity.this);
    }
}
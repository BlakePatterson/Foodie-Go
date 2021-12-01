package edu.temple.foodiego;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class SocialFeedActivity extends AppCompatActivity {
    private FoodieUser user;
    private RecyclerView list;

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
        list.setAdapter(new SocialFeedAdapter(getFriendsActivity()));
    }

    public ArrayList<FoodieActivityLog> getFriendsActivity(){
        //TODO: get data from FirebaseHelper
        return new ArrayList<>();
    }

    public void openPostActivityDialog(){

    }

    public void postActivityToFirebase(FoodieActivityLog activity){

    }
}
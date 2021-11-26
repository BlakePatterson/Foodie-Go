package edu.temple.foodiego;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class SocialFeedActivity extends AppCompatActivity {
    private FoodieUser user;
    private RecyclerView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_feed);
        list = findViewById(R.id.socialFeed);
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